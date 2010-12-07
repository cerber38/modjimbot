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
package ru.caffeineim.protocols.icq.packet.received.byddylist;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class RefusedContact__3_10 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(RefusedContact__3_10.class);

	private String ref;

	public RefusedContact__3_10(byte array[]) {
		super(array, true);
		ref = null;
		byte data[] = getSnac().getDataFieldByteArray();
		ref = new String(data, 1, data.length - 1);
	}

	public void execute(OscarConnection oscarconnection) {
	}

	public void notifyEvent(OscarConnection connection) {
		log.warn(ref);
	}
}
