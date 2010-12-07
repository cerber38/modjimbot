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

import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.integration.events.MessageAckEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class MessageAck__4_12 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(MessageAck__4_12.class);

	private Iterator iter;
	private RawData time;
	private RawData id;
	private RawData type;
	private RawData rcptUin;

	public MessageAck__4_12(byte array[]) {
		super(array, true);
		byte data[] = getSnac().getDataFieldByteArray();
		time = new RawData(data, 0, 4);
		id = new RawData(data, 4, 4);
		type = new RawData(data, 8, 2);
		rcptUin = new RawData(data, 11, data[10]);
	}

	public void execute(OscarConnection oscarconnection) {
	}

	public void notifyEvent(OscarConnection connection) {
		MessageAckEvent e = new MessageAckEvent(this);
		for (int i = 0; i < connection.getMessagingListeners().size(); i++) {
			MessagingListener l = (MessagingListener) connection.getMessagingListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onMessageAck()");
			l.onMessageAck(e);
		}
	}

	public int getMessageTime() {
		return time.getValue();
	}

	public int getMessageId() {
		return id.getValue();
	}

	public short getMessageType() {
		return (short)type.getValue();
	}

	public String getRcptUin() {
		return rcptUin.getStringValue();
	}
}
