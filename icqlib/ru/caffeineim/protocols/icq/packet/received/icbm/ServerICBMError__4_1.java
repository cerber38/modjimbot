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
import ru.caffeineim.protocols.icq.integration.events.MessageErrorEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.setting.enumerations.GlobalErrorsEnum;

/**
 * <p>Created by
 *   @author Onix
 *   @author Samolisov Pavel
 */
public class ServerICBMError__4_1 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(ServerICBMError__4_1.class);

	private GlobalErrorsEnum error;

	/**
	 * @param array
	 */
	public ServerICBMError__4_1(byte[] array) {
		super(array, true);

		byte[] data = getSnac().getDataFieldByteArray();

		// retrieve error code
		RawData code = new RawData(data, 0, 2);

		error = new GlobalErrorsEnum(code.getValue());
	}

	public GlobalErrorsEnum getError() {
		return error;
	}

	public void notifyEvent(OscarConnection connection) {
		MessageErrorEvent e = new MessageErrorEvent(this);
		for (int i = 0; i < connection.getMessagingListeners().size(); i++) {
			MessagingListener l = (MessagingListener) connection.getMessagingListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onMessageError()");
			l.onMessageError(e);
		}
	}
}
