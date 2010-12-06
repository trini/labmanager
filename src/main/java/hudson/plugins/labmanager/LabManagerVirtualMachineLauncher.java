/**
 *  Copyright (C) 2010 Mentor Graphics Corporation
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Based on the libvirt-plugin which is:
 *  Copyright (C) 2010, Byte-Code srl <http://www.byte-code.com>
 *
 * Date: Mar 04, 2010
 * Author: Marco Mornati<mmornati@byte-code.com>
 */
package hudson.plugins.labmanager;

import hudson.slaves.ComputerLauncher;
import hudson.slaves.SlaveComputer;
import hudson.model.TaskListener;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.Extension;
import hudson.slaves.Cloud;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kohsuke.stapler.DataBoundConstructor;

import com.vmware.labmanager.*;
import com.vmware.labmanager.LabManager_x0020_SOAP_x0020_interfaceStub.*;

/**
 * {@link ComputerLauncher} for Lab Manager that waits for the Virtual Machine
 * to really come up before proceeding to the real user-specified
 * {@link ComputerLauncher.
 *
 * @author Tom Rini <tom_rini@mentor.com>
 */
public class LabManagerVirtualMachineLauncher extends ComputerLauncher {

    private static final Logger LOGGER = Logger.getLogger(LabManagerVirtualMachineLauncher.class.getName());
    private ComputerLauncher delegate;
    private String lmDescription;
    private String vmName;
    private int idleAction;
    private Boolean overrideLaunchSupported;

    /**
     * Constants.
     */
    /* Machine status codes. */
    private static final int MACHINE_STATUS_OFF = 1;
    private static final int MACHINE_STATUS_ON = 2;
    private static final int MACHINE_STATUS_SUSPENDED = 3;
    private static final int MACHINE_STATUS_STUCK = 4;
    private static final int MACHINE_STATUS_INVALID = 128;

    /* Machine action codes. */
    private static final int MACHINE_ACTION_ON = 1;
    private static final int MACHINE_ACTION_OFF = 2;
    private static final int MACHINE_ACTION_SUSPEND = 3;
    private static final int MACHINE_ACTION_RESUME = 4;
    private static final int MACHINE_ACTION_RESET = 5;
    private static final int MACHINE_ACTION_SNAPSHOT = 6;
    private static final int MACHINE_ACTION_REVERT = 7;
    private static final int MACHINE_ACTION_SHUTDOWN = 8;

    /**
     * @param delegate real user-specified {@link ComputerLauncher}.
     * @param lmDescription Human reable description of the Lab Manager
     * instance.
     * @param vMName The 'VM Name' field in the configuration in Lab Manager.
     */
    @DataBoundConstructor
    public LabManagerVirtualMachineLauncher(ComputerLauncher delegate,
                    String lmDescription, String vmName, String idleOption,
                    Boolean overrideLaunchSupported) {
        super();
        this.delegate = delegate;
        this.lmDescription = lmDescription;
        this.vmName = vmName;
        if ("Shutdown".equals(idleOption))
            idleAction = MACHINE_ACTION_SHUTDOWN;
        else
            idleAction = MACHINE_ACTION_SUSPEND;
        this.overrideLaunchSupported = overrideLaunchSupported;
    }

    /**
     * Determine what LabManager object controls this slave.  Once we have
     * that we can call and get the information out that we need to perform
     * SOAP calls.
     */
    private LabManager findOurLmInstance() throws RuntimeException {
        if (lmDescription != null && vmName != null) {
            LabManager labmanager = null;
            for (Cloud cloud : Hudson.getInstance().clouds) {
                if (cloud instanceof LabManager && ((LabManager) cloud).getLmDescription().equals(lmDescription)) {
                    labmanager = (LabManager) cloud;
                    return labmanager;
                }
            }
        }
        LOGGER.log(Level.SEVERE, "Could not find our Lab Manager instance!");
        throw new RuntimeException("Could not find our Lab Manager instance!");
    }

    /**
     * We have stored inside of the LabManager object all of the information
     * needed to get the Machine object back out.  We cannot store the
     * machineId value itself so we work our way towards it by getting the
     * Configuration we know the machine lives in and then returning the
     * Machine object.  We know that the machine name is unique to the
     * configuration.
     */
    private Machine getMachine(LabManager labmanager,
                    LabManager_x0020_SOAP_x0020_interfaceStub lmStub,
                    AuthenticationHeaderE lmAuth)
            throws java.rmi.RemoteException {
        Machine vm = null;
        GetSingleConfigurationByName gscbnReq = new GetSingleConfigurationByName();
        gscbnReq.setName(labmanager.getLmConfiguration());
        GetSingleConfigurationByNameResponse gscbnResp = lmStub.getSingleConfigurationByName(gscbnReq, lmAuth);
        ListMachines lmReq = new ListMachines();
        lmReq.setConfigurationId(gscbnResp.getGetSingleConfigurationByNameResult().getId());
        ListMachinesResponse lmResp = lmStub.listMachines(lmReq, lmAuth);

        ArrayOfMachine aom = lmResp.getListMachinesResult();
        for (Machine mach : aom.getMachine()) {
            if (mach.getName().equals(this.vmName))
                vm = mach;
        }

        return vm;
    }

    /**
     * Perform the specified action on the specified machine via SOAP.
     */
    private static void performAction(LabManager labmanager,
                    LabManager_x0020_SOAP_x0020_interfaceStub lmStub,
                    AuthenticationHeaderE lmAuth, Machine vm, int action) 
            throws java.rmi.RemoteException {
        MachinePerformAction mpaReq = new MachinePerformAction();
        mpaReq.setAction(action);
        mpaReq.setMachineId(vm.getId());
        /* We can't actually do anything here, problems come
         * as an exception I believe. */
        lmStub.machinePerformAction(mpaReq, lmAuth);
    }

    /**
     * Do the real work of launching the machine via SOAP.
     */
    @Override
    public void launch(SlaveComputer slaveComputer, TaskListener taskListener)
            throws IOException, InterruptedException {
        /**
         * What we know is that at least at one point this particular
         * machine existed.  But we want to be sure it still exists.
         * If it exists we can check the status.  If we are off,
         * power on.  If we are suspended, resume.  If we are on,
         * do nothing.  The problem is that we don't have the machineId
         * right now so we need to call our getMachine.
         */
        LabManager labmanager = findOurLmInstance();
        LabManager_x0020_SOAP_x0020_interfaceStub lmStub = labmanager.getLmStub();
        AuthenticationHeaderE lmAuth = labmanager.getLmAuth();
        int machineAction = 0;
        Machine vm = getMachine(labmanager, lmStub, lmAuth);

        /* Determine the current state of the VM. */
        switch (vm.getStatus()) {
            case MACHINE_STATUS_OFF:
                    machineAction = MACHINE_ACTION_ON;
                    break;
            case MACHINE_STATUS_SUSPENDED:
                    machineAction = MACHINE_ACTION_RESUME;
                    break;
            case MACHINE_STATUS_ON:
                    /* Nothing to do */
                    break;
            case MACHINE_STATUS_STUCK:
            case MACHINE_STATUS_INVALID:
                    LOGGER.log(Level.SEVERE, "Problem with the machine status!");
                    throw new IOException("Problem with the machine status");
        }

        /* Perform the action, if needed.  This will be sleeping until
        * it returns from the server. */
        if (machineAction != 0)
            performAction(labmanager, lmStub, lmAuth, vm, machineAction);

        /* At this point the VM is ready to go. */
        delegate.launch(slaveComputer, taskListener);
    }

    /**
     * Handle bringing down the Virtual Machine.
     */
    @Override
    public void afterDisconnect(SlaveComputer slaveComputer,
                    TaskListener taskListener) {
        taskListener.getLogger().println("Running disconnect procedure...");
        delegate.afterDisconnect(slaveComputer, taskListener);
        taskListener.getLogger().println("Shutting down Virtual Machine...");

        LabManager labmanager = findOurLmInstance();
        LabManager_x0020_SOAP_x0020_interfaceStub lmStub = labmanager.getLmStub();
        AuthenticationHeaderE lmAuth = labmanager.getLmAuth();

        try {
            int machineAction = 0;
            Machine vm = getMachine(labmanager, lmStub, lmAuth);

            /* Determine the current state of the VM. */
            switch (vm.getStatus()) {
                    case MACHINE_STATUS_OFF:
                    case MACHINE_STATUS_SUSPENDED:
                            break;
                    case MACHINE_STATUS_ON:
                            machineAction = idleAction;
                            break;
                    case MACHINE_STATUS_STUCK:
                    case MACHINE_STATUS_INVALID:
                            LOGGER.log(Level.SEVERE, "Problem with the machine status!");
            }

            /* Perform the action, if needed.  This will be sleeping until
             * it returns from the server. */
            if (machineAction != 0)
                performAction(labmanager, lmStub, lmAuth, vm, machineAction);
        } catch (Throwable t) {
            taskListener.fatalError(t.getMessage(), t);
        }
    }

    public ComputerLauncher getDelegate() {
        return delegate;
    }

    public Boolean getOverrideLaunchSupported() {
        return overrideLaunchSupported;
    }

    public void setOverrideLaunchSupported(Boolean overrideLaunchSupported) {
        this.overrideLaunchSupported = overrideLaunchSupported;
    }

    @Override
    public boolean isLaunchSupported() {
        if (this.overrideLaunchSupported == null)
            return delegate.isLaunchSupported();
        else {
                LOGGER.log(Level.FINE, "Launch support is overridden to always return: " + overrideLaunchSupported);
                return overrideLaunchSupported;
        }
    }

    @Override
    public void beforeDisconnect(SlaveComputer slaveComputer, TaskListener taskListener) {
        delegate.beforeDisconnect(slaveComputer, taskListener);
    }

    @Override
    public Descriptor<ComputerLauncher> getDescriptor() {
        return Hudson.getInstance().getDescriptor(getClass());
    }

    @Extension
    public static final Descriptor<ComputerLauncher> DESCRIPTOR = new Descriptor<ComputerLauncher>() {

        private String lmDescription;
        private String vmName;
        private Boolean overrideLaunchSupported;
        private ComputerLauncher delegate;

        public String getDisplayName() {
            return "Virtual Machine Launcher";
        }

        public String getLmDescription() {
            return lmDescription;
        }

        public String getVmName() {
            return vmName;
        }

        public Boolean getOverrideLaunchSupported() {
            return overrideLaunchSupported;
        }

        public ComputerLauncher getDelegate() {
            return delegate;
        }
    };
}
