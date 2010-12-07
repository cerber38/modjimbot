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
package ru.caffeineim.protocols.icq.packet.received.usagestats;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class MinReportInterval__11_2 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(MinReportInterval__11_2.class);

	private short interval;

	public MinReportInterval__11_2(byte array[]) {
		super(array, true);
		byte data[] = getSnac().getDataFieldByteArray();
		interval = (short)((data[0] << 8) + (data[1] & 0xff));
	}

	public void execute(OscarConnection oscarconnection) {
	}

	public void notifyEvent(OscarConnection connection) {
		//log.info("Report interval: " + interval + "h");
	}
}
