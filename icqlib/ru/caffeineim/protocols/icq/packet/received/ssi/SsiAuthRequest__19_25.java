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
package ru.caffeineim.protocols.icq.packet.received.ssi;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.SsiAuthRequestEvent;
import ru.caffeineim.protocols.icq.integration.listeners.ContactListListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class SsiAuthRequest__19_25 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(SsiAuthRequest__19_25.class);

	private String senderUin;

	private String message;

	public SsiAuthRequest__19_25(byte[] array) throws ConvertStringException {
		super(array, true);
		int position = 0;

		byte[] data = getSnac().getDataFieldByteArray();

		// unknown 8 bytes
		position += 8;

		// uin
		RawData len = new RawData(data[position++]);
		senderUin = new String(data, position, len.getValue());
		position += len.getValue();

		// message
		len = new RawData(data, position, RawData.WORD_LENGHT);
		position += 2;
		message = StringTools.utf8ByteArrayToString(data, position, len.getValue());
	}

	public void notifyEvent(OscarConnection connection) {
		SsiAuthRequestEvent e = new SsiAuthRequestEvent(this);
		for (int i = 0; i < connection.getContactListListeners().size(); i++) {
			ContactListListener l = (ContactListListener) connection.getContactListListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onSsiAuthRequest()");
			l.onSsiAuthRequest(e);
		}
	}

	public String getSenderUin() {
		return senderUin;
	}

	public String getMessage() {
		return message;
	}
}
