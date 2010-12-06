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

import hudson.slaves.SlaveComputer;
import hudson.model.Slave;

/**
 * @author Tom Rini <tom_rini@mentor.com>
 */
public class LabManagerVirtualMachineSlaveComputer extends SlaveComputer {
    public LabManagerVirtualMachineSlaveComputer(Slave slave) {
        super(slave);
    }
}
