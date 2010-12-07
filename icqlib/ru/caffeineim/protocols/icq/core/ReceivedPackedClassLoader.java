/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package ru.caffeineim.protocols.icq.core;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class ReceivedPackedClassLoader {

	private static Log log = LogFactory.getLog(ReceivedPackedClassLoader.class);

	private Vector classNamesVector;

    public ReceivedPackedClassLoader(IReceivedPacketRegistry registry) {
        this.classNamesVector = registry.getClassNameVector();
    }

    public Class loadClass(int commandId, int subCommmandId) {
        Class ret = null;
        String endString = "__" + commandId + "_" + subCommmandId;
        for (int i = 0; i < classNamesVector.size(); i++) {
            if (((String) classNamesVector.get(i)).endsWith(endString)) {
                try {
                    ret = Class.forName((String) classNamesVector.get(i));
                }
                catch (ClassNotFoundException CNFE) {
                    log.error("Class not found " + CNFE.getMessage());
                }
            }
        }

        return ret;
    }
}
