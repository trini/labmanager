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

import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;

/**
 * @author Tom Rini <tom_rini@mentor.com>
 */
public class LabManagerVirtualMachine implements Serializable, Comparable<LabManagerVirtualMachine> {
    private final LabManager labmanager;
    private final String name;

    @DataBoundConstructor
    public LabManagerVirtualMachine(LabManager labmanager, String name) {
        this.labmanager = labmanager;
        this.name = name;
    }

    public LabManager getLabmanager() {
        return labmanager;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof LabManagerVirtualMachine)) {
            return false;
        }

        LabManagerVirtualMachine that = (LabManagerVirtualMachine) o;

        if (labmanager != null ? !labmanager.equals(that.labmanager) : that.labmanager != null) {
            return false;
        }
        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        return 31 * result + (labmanager != null ? labmanager.hashCode() : 0);
    }

    public String getDisplayName() {
        return this.toString();
    }

    public int compareTo(LabManagerVirtualMachine o) {
        return name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return labmanager.toString() + ":" + name;
    }
}
