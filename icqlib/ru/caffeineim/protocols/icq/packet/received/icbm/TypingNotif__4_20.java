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
package ru.caffeineim.protocols.icq.packet.received.icbm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class TypingNotif__4_20 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(TypingNotif__4_20.class);

	public static final short TYPING_FINISHED = 0;
	public static final short TYPING_CURRENT  = 1;
	public static final short TYPING_BEGIN    = 2;

	private RawData uin;
	private RawData type;

	/**
	 * Creates a new instance of TypingNotif__4_20
	 */
	public TypingNotif__4_20(byte[] array) {
		super(array, true);
		byte[] data = getSnac().getByteArray();

		int index = 0;
		RawData id = new RawData(data, index, 8);
		index += 8;
		RawData channel = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;
		RawData uinLg = new RawData(data, index++, RawData.BYTE_LENGHT);
		uin = new RawData(data, index, uinLg.getValue());
		index += uinLg.getValue();
		type = new RawData(data, index, RawData.WORD_LENGHT);
	}

	public void notifyEvent(OscarConnection connection) {
		// TODO handle this event
		log.info(uin.getStringValue() + " is typing...");
	}
}
