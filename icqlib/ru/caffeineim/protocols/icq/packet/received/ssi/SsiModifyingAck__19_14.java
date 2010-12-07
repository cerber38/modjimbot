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
import ru.caffeineim.protocols.icq.integration.events.SsiModifyingAckEvent;
import ru.caffeineim.protocols.icq.integration.listeners.ContactListListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.setting.enumerations.SsiResultModeEnum;

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class SsiModifyingAck__19_14 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(SsiModifyingAck__19_14.class);

	private SsiResultModeEnum[] results;

	public SsiModifyingAck__19_14(byte[] array) {
		super(array, true);
		int position = 0;

		byte[] data = getSnac().getDataFieldByteArray();

		// Retreiving result's array
		int count = data.length/RawData.WORD_LENGHT;
		results = new SsiResultModeEnum[count];
		for (int i = 0; i<count; i++) {
			results[i] = new SsiResultModeEnum(new RawData(data, position, RawData.WORD_LENGHT).getValue());
			position += RawData.WORD_LENGHT;
		}
	}

	public void notifyEvent(OscarConnection connection) {
		SsiModifyingAckEvent e = new SsiModifyingAckEvent(this);
		for (int i = 0; i < connection.getContactListListeners().size(); i++) {
			ContactListListener l = (ContactListListener) connection.getContactListListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onSsiModifyingAck()");
			l.onSsiModifyingAck(e);
		}
	}

	public SsiResultModeEnum[] getResuls() {
		return results;
	}
}
