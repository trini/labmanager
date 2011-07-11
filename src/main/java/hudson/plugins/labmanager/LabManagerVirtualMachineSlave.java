/**
 *  Copyright (C) 2010-2011 Mentor Graphics Corporation
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

import hudson.model.Computer;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.model.Slave;
import hudson.model.TaskListener;
import hudson.slaves.ComputerLauncher;
import hudson.slaves.ComputerListener;
import hudson.slaves.Cloud;
import hudson.slaves.RetentionStrategy;
import hudson.slaves.NodeProperty;
import hudson.slaves.SlaveComputer;
import hudson.Util;
import hudson.Extension;
import hudson.Functions;
import hudson.AbortException;
import hudson.util.FormValidation;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

/**
 * This describes a Virtual Machine that is being used as a slave that
 * resides within Lab Manager.
 * @author Tom Rini <tom_rini@mentor.com>
 */
public class LabManagerVirtualMachineSlave extends Slave {
    private final String lmDescription;
    private final String vmName;
    private final String idleOption;
    private final String launchDelay;

    @DataBoundConstructor
    public LabManagerVirtualMachineSlave(String name, String nodeDescription,
            String remoteFS, String numExecutors, Mode mode,
            String labelString, ComputerLauncher delegateLauncher,
            RetentionStrategy retentionStrategy,
            List<?extends NodeProperty<?>> nodeProperties,
            String lmDescription, String vmName, String idleOption,
            boolean launchSupportForced, String launchDelay)
            throws Descriptor.FormException, IOException {
        super(name, nodeDescription, remoteFS, numExecutors, mode, labelString,
                new LabManagerVirtualMachineLauncher(delegateLauncher, lmDescription,
                        vmName, idleOption,
            launchSupportForced ? Boolean.TRUE : null, launchDelay),
                retentionStrategy, nodeProperties);
        this.lmDescription = lmDescription;
        this.vmName = vmName;
        this.idleOption = idleOption;
        this.launchDelay = launchDelay;
    }

    public String getLmDescription() {
        return lmDescription;
    }

    public String getVmName() {
        return vmName;
    }

    public String getIdleOption() {
        return idleOption;
    }

    public boolean isLaunchSupportForced() {
        return ((LabManagerVirtualMachineLauncher) getLauncher()).getOverrideLaunchSupported() == Boolean.TRUE;
    }

    public void setLaunchSupportForced(boolean slaveLaunchesOnBootup) {
        ((LabManagerVirtualMachineLauncher) getLauncher()).setOverrideLaunchSupported(slaveLaunchesOnBootup ? Boolean.TRUE : null);
    }

    public String getLaunchDelay() {
        return launchDelay;
    }

    /**
     * For UI.
     *
     * @return original launcher
     */
    public ComputerLauncher getDelegateLauncher() {
        return ((LabManagerVirtualMachineLauncher) getLauncher()).getDelegate();
    }

    /**
     * Allow for a configurable maximum of VMs to be on at a given time
     */
    @Extension
    public static class LabManagerVirtualMComputerListener extends ComputerListener {
        private String lmDescription;

        @Override
        public void preLaunch(Computer c, TaskListener taskListener) throws IOException, InterruptedException {
            /* We may be called on any slave type so check that we should
             * be in here. */
            if (!(c.getNode() instanceof LabManagerVirtualMachineSlave))
                return;

            LabManagerVirtualMachineLauncher LMVML = (LabManagerVirtualMachineLauncher)((SlaveComputer) c).getLauncher();
            LabManager hypervisor = LMVML.findOurLmInstance();
            int maxOnlineSlaves = hypervisor.getMaxOnlineSlaves();

            /* A maximum of 0 means no limit, allow. */
            if (maxOnlineSlaves == 0)
                return;

            if (hypervisor.markOneSlaveOnline(c.getDisplayName()) > maxOnlineSlaves) {
                hypervisor.markOneSlaveOffline(c.getDisplayName());
                throw new AbortException("Maximum allowed VM count reached for this cloud.");
            }
        }
    }

    @Extension
    public static final class DescriptorImpl extends SlaveDescriptor {
        public DescriptorImpl() {
            load();
        }

        public String getDisplayName() {
            return "Slave virtual computer running under Lab Manager";
        }

        @Override
        public boolean isInstantiable() {
            return true;
        }

        public List<LabManagerVirtualMachine> getDefinedLabManagerVirtualMachines(String lmDescription) {
            List<LabManagerVirtualMachine> virtualMachinesList = new ArrayList<LabManagerVirtualMachine>();
            if (lmDescription != null && !lmDescription.equals("")) {
                LabManager hypervisor = null;
                for (Cloud cloud : Hudson.getInstance().clouds) {
                    if (cloud instanceof LabManager && ((LabManager) cloud).getLmDescription().equals(lmDescription)) {
                        hypervisor = (LabManager) cloud;
                        break;
                    }
                }
                virtualMachinesList.addAll(hypervisor.getLabManagerVirtualMachines());
            }
            return virtualMachinesList;
        }

        public List<LabManager> getLabmanagers() {
            List<LabManager> result = new ArrayList<LabManager>();
            for (Cloud cloud : Hudson.getInstance().clouds) {
                if (cloud instanceof LabManager) {
                    result.add((LabManager) cloud);
                }
            }
            return result;
        }

        public List<Descriptor<ComputerLauncher>> getComputerLauncherDescriptors() {
            List<Descriptor<ComputerLauncher>> result = new ArrayList<Descriptor<ComputerLauncher>>();
            for (Descriptor<ComputerLauncher> launcher : Functions.getComputerLauncherDescriptors()) {
                if (!LabManagerVirtualMachineLauncher.class.isAssignableFrom(launcher.clazz)) {
                    result.add(launcher);
                }
            }
            return result;
        }

        public List<String> getIdleOptions() {
            List<String> options = new ArrayList<String>();
            options.add("Shutdown");
            options.add("Shutdown and Revert");
            options.add("Suspend");
            return options;
        }

        public FormValidation doCheckLaunchDelay(@QueryParameter String value) {
            return FormValidation.validatePositiveInteger(value);
        }
    }
}
