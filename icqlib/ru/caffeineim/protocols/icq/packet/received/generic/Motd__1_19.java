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
package ru.caffeineim.protocols.icq.packet.received.generic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class Motd__1_19 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(Motd__1_19.class);

	public static final short MTD_MDT_UPGRAGE = 1;
	public static final short MTD_ADV_UPGRAGE = 2;
	public static final short MTD_SYS_BULLETIN = 3;
	public static final short MTD_NORMAL = 4;
	public static final short MTD_NONE = 5;
	public static final short MTD_NEWS = 6;
	private short type;
	private String msg;

	public Motd__1_19(byte array[]) {
		super(array, true);
		msg = null;
		byte data[] = getSnac().getDataFieldByteArray();
		type = (short)(new RawData(data, 0, 2)).getValue();
		Tlv tlv1 = new Tlv(data, 2);
		Tlv tlv2 = new Tlv(data, 6 + tlv1.getLength());

		if (type != 5)
			msg = new Tlv(data, 10 + tlv1.getLength() + tlv2.getLength()).getStringValue();
	}

	public short getType() {
		return type;
	}

	public String getMessage() {
		return msg;
	}

	public void notifyEvent(OscarConnection connection) {
		if (getMessage() != null)
			log.info("Message of the day : " + getMessage());
	}
}
