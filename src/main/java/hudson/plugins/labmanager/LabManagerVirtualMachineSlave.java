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

import hudson.model.Slave;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.slaves.ComputerLauncher;
import hudson.slaves.RetentionStrategy;
import hudson.slaves.NodeProperty;
import hudson.slaves.Cloud;
import hudson.Util;
import hudson.Extension;
import hudson.Functions;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;

import org.kohsuke.stapler.DataBoundConstructor;

/**
 * This describes a Virtual Machine that is being used as a slave that
 * resides within Lab Manager.
 * @author Tom Rini <tom_rini@mentor.com>
 */
public class LabManagerVirtualMachineSlave extends Slave {
    private final String lmDescription;
    private final String vmName;
    private final String idleOption;

    @DataBoundConstructor
    public LabManagerVirtualMachineSlave(String name, String nodeDescription,
            String remoteFS, String numExecutors, Mode mode,
            String labelString, LabManagerVirtualMachineLauncher launcher,
            ComputerLauncher delegateLauncher,
            RetentionStrategy retentionStrategy,
            List<?extends NodeProperty<?>> nodeProperties,
            String lmDescription, String vmName, String idleOption,
            boolean launchSupportForced)
            throws Descriptor.FormException, IOException {
        super(name, nodeDescription, remoteFS, numExecutors, mode, labelString,
                new LabManagerVirtualMachineLauncher(delegateLauncher, lmDescription,
                        vmName, idleOption,
			launchSupportForced ? Boolean.TRUE : null),
                retentionStrategy, nodeProperties);
        this.lmDescription = lmDescription;
        this.vmName = vmName;
        this.idleOption = idleOption;
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

    /**
     * For UI.
     *
     * @return original launcher
     */
    public ComputerLauncher getDelegateLauncher() {
        return ((LabManagerVirtualMachineLauncher) getLauncher()).getDelegate();
    }

    @Extension
    public static final class DescriptorImpl extends SlaveDescriptor {
        private String lmDescription;
        private String vmName;
        private String idleOption;
        private boolean launchSupportForced = true;

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
            return launchSupportForced;
        }
    }
}
