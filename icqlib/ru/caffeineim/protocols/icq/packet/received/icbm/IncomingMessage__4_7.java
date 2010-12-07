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
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.IncomingMessageEvent;
import ru.caffeineim.protocols.icq.integration.events.IncomingUrlEvent;
import ru.caffeineim.protocols.icq.integration.events.XStatusRequestEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MessagingListener;
import ru.caffeineim.protocols.icq.integration.listeners.XStatusListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.packet.sent.icbm.MessageType2Ack;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageFlagsEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageTypeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class IncomingMessage__4_7 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(IncomingMessage__4_7.class);

	public static final int UCS2BE_ENCODING_MASK = 0x00020000;

	private RawData time;
	private RawData msgId;
	private RawData messageChannel;
	private Tlv userIdleTime;
	private RawData ackType;
	private RawData senderTcpVer;
	private RawData msgSeqNum;
	private RawData msgType;
	private RawData msgFlags;
	private RawData foreground;
	private RawData background;
	private String senderID;
	private String message;
	private String url;
	private Tlv senderStatus;
	private Tlv onlineSinceTime;
	private Tlv ackType2;
	private Tlv port;
	private Tlv ip;
	private RawData encoding;

	private boolean isRequestAwayMessage = false;
	private boolean isRequestXStatus = false;
	private boolean isFileAckOrAbortRequest = false;
	private boolean isFileAckOrFileOk = false;

	// TODO must normal recive RTF messages!
	public IncomingMessage__4_7(byte[] array) throws ConvertStringException {
		super(array, true);

		int position = 0;
		byte[] data = getSnac().getDataFieldByteArray();

		/* retreiving TIME */
		time = new RawData(data, position, 4);
		position += 4;

		/* retreiving ID */
		msgId = new RawData(data, position, 4);
		position += 4;

		/* retreiving message channel */
		messageChannel = new RawData(data, position, 2);
		position += 2;

		/* retreiving sender's ID */
		RawData len = new RawData(data[position++]);
		senderID = new String(data, position, len.getValue());
		position += len.getValue();

		/* skipping sender warning level */
		position += 2;

		/* Retreive the number of TLV */
		RawData nbTlv = new RawData(data, position, 2);
		position += 2;

		for (int i = 0; i < nbTlv.getValue(); i++) {
			Tlv tmpTlv = new Tlv(data, position);

			switch (tmpTlv.getType()) {
				case 0x06:
					senderStatus = tmpTlv;
					break;
				case 0x0F:
					userIdleTime = tmpTlv;
					break;
				case 0x03:
					onlineSinceTime = tmpTlv;
					break;
			}
			position += tmpTlv.getByteArray().length;
		}

		if (messageChannel.getValue() == 1) {
			parseType1(position, data);
		}
		else if (messageChannel.getValue() == 2) {
			parseType2(position, data);
		}
		else {
			parseType4(position, data);
		}

		if (getMessageType().getType() == MessageTypeEnum.XSTATUS_MESSAGE) {
			isRequestXStatus = true;
		}
	}

	public void execute(OscarConnection connection) throws Exception {
		/* send the acknowledge if not type 4 and MessageType not type XSTATUS_REQUEST*/
		if (messageChannel.getValue() == 2 && !isRequestXStatus) {
			connection.sendFlap(new MessageType2Ack(time,
					msgId, senderID, senderTcpVer,
					msgSeqNum, msgType, msgFlags,
					connection.getTweaker().getRequestMessage(msgType.getValue())));
		}
	}

	public void notifyEvent(OscarConnection connection) {
		if (getMessageType().getType() == MessageTypeEnum.PLAIN_MESSAGE)
			notifyIncomingMessage(connection);
		else if (getMessageType().getType() == MessageTypeEnum.URL)
			notifyIncomingUrl(connection);
		else if (getMessageType().getType() == MessageTypeEnum.XSTATUS_MESSAGE /*|| getMessageType().getType() == 232*/)
			notifyXStatusRequest(connection);
		else {
			log.error("UNRECOGNIZED IncomingMessage__4_7 TYPE: " + getMessageType().getType() +
					" (norm " + MessageTypeEnum.PLAIN_MESSAGE + ", url " + MessageTypeEnum.URL + "," +
					" xstatusreq " + MessageTypeEnum.XSTATUS_MESSAGE + ")");
		}
	}

	private void notifyIncomingMessage(OscarConnection connection) {
		IncomingMessageEvent e = new IncomingMessageEvent(this);
		for (int i = 0; i < connection.getMessagingListeners().size(); i++) {
			MessagingListener l = (MessagingListener) connection.getMessagingListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onIncomingMessage()");
			l.onIncomingMessage(e);
		}
	}

	private void notifyXStatusRequest(OscarConnection connection) {
		XStatusRequestEvent e = new XStatusRequestEvent(this);
		for (int i = 0; i < connection.getXStatusListeners().size(); i++) {
			XStatusListener l = (XStatusListener) connection.getXStatusListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onXStatusRequest()");
			l.onXStatusRequest(e);
		}
	}

	private void notifyIncomingUrl(OscarConnection connection) {
		IncomingUrlEvent e = new IncomingUrlEvent(this);
		for (int i = 0; i < connection.getMessagingListeners().size(); i++) {
			MessagingListener l = (MessagingListener) connection.getMessagingListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onIncomingUrl()");
			l.onIncomingUrl(e);
		}
	}

	/**
	 * This method parse Type 1 message, which is the oldest way to
	 * send a message.
	 *
	 * @param position The position where we must start to parse.
	 * @param data The packet we must parse.
	 * @throws ConvertStringException
	 */
	private void parseType1(int position, byte[] data) throws ConvertStringException {
		/* Setting up the msgType property to NORMAL MESSAGE */
		msgType = new RawData(0x01);

		/* skippping TYPE1 TLV(1) header */
		position += 4;

		/* capacity TLV*/
		Tlv tlvCapa = new Tlv(data, position);
		position += tlvCapa.getLength() + 4;

		/* retreiving the message's length */
		Tlv tlvMsg = new Tlv(data, position);
		int msgLen = tlvMsg.getLength() - 4;

		/* skipping TLV(0x0101) header + tlv length */
		position += 4;

		/* encoding fields */
		encoding = new RawData(data, position, RawData.DWORD_LENGHT);
		position += 4;

		/* retreiving the message itself */
		if ((getEncoding() & UCS2BE_ENCODING_MASK) > 0)
			message = StringTools.ucs2beByteArrayToString(data, position, msgLen);
		else
			message = StringTools.byteArrayToString(data, position, msgLen);
	}

	private void parseType2(int position, byte[] data) throws ConvertStringException {

		/* skipping the first 4 bytes of the TLV(5) */
		position += 4;

		/* Retreiving the ack type */
		ackType = new RawData(data, position, 2);
		position += 2;

		/* File Ack and Abort Request */
		if (ackType.getValue() != 0) {
			/**@todo implements file ack and abort request parsing */
			isFileAckOrAbortRequest = true;
		}
		else {
			/* skipping the next 2 DWORD */
			position += 8;

			/* skipping 16 bytes of capability */
			position += 16;

			/* retreiving acktype2 */
			ackType2 = new Tlv(data, position);
			position += (4 + ackType2.getLength());

			/* File Ack or File OK. */
			if (ackType2.getValue() == 2) {
				/* retreiving port */
				port = new Tlv(data, position);
				position += (4 + port.getValue());

				/* retreiving IP */
				ip = new Tlv(data, position);
				position += (4 + ip.getValue());
				isFileAckOrFileOk = true;
			}
			else {
				/* skipping 10 bytes till sender's TCP version field */
				position += 10;

				/* retreiving TCP version */
				senderTcpVer = new RawData(data, position, 2);
				position += 2;

				/* skipping 23 bytes till seq number message */
				position += 23;

				/* retreiving message seq number */
				msgSeqNum = new RawData(data, position, 2);
				position += 2;

				/* skipping 16 bytes */
				position += 16;

				/* retreiving message type field */
				msgType = new RawData(data, position, 1);
				position++;

				/* retreiving message flags field */
				msgFlags = new RawData(data, position, 1);
				position++;

				if (msgType.getValue() == 0x01)
					parseType2Message(position, data);

				else if ( (msgType.getValue() >= 0xe8) && (msgType.getValue() <= 0xec))
					isRequestAwayMessage = true;
			}
		}
	}

	private void parseType2Message(int position, byte[] data) throws ConvertStringException {
		/* Skipping 4 bytes till message (finally !)[i guess you could make it a lil bit more complicated mr AOL !] */
		position += 4;

		/* Retreiving message length */
		RawData msgLen = new RawData(data, position, 2);
		msgLen.invertIndianness();
		position += 2;

		/* Retreiving message */
		message = StringTools.utf8ByteArrayToString(data, position, msgLen.getValue() - 1);
		position += message.length();

		/* Retreiving foreground info */
		foreground = new RawData(data, position, 4);
		position += 4;

		/* Retreiving background info */
		background = new RawData(data, position, 4);
		position += 4;
	}

	private void parseType4(int position, byte[] data) {

		/* skipping the first 4 bytes of the TLV(5) */
		position += 4;

		/* skipping sender's id field */
		position += 4;

		/* retreiving message type field */
		msgType = new RawData(data, position, 2);
		msgType.invertIndianness();
		position += 2;

		/* Retreiving message length */
		RawData msgLen = new RawData(data, position, 2);
		msgLen.invertIndianness();
		position += 2;

		if (msgType.getValue() == 0x01)
			parseType4Message(position, data, msgLen.getValue());
		else if (msgType.getValue() == 0x04)
			parseType4LinkMessage(position, data, msgLen.getValue());
	}

	private void parseType4Message(int position, byte[] data, int msgLen) {
		message = new String(data, position, msgLen - 1);
	}

	private void parseType4LinkMessage(int position, byte[] data, int msgLen) {
		/* Retreiving message + link */
		String tmp = new String(data, position, msgLen - 1);
		int divPosition = tmp.indexOf( (char) 0xFE);
		message = new String(tmp.substring(0, divPosition));
		url = new String(tmp.substring(divPosition + 1, tmp.length()));
	}

	public int getMessageId() {
		return msgId.getValue();
	}

	public int getMessageChannel() {
		return messageChannel.getValue();
	}

	public String getSenderID() {
		return senderID;
	}

	public int getSenderTcpVersion() {
		return senderTcpVer.getValue();
	}

	public int getMessageSeqNum() {
		return msgSeqNum.getValue();
	}

	public String getMessage() {
		return message;
	}

	public String getUrl() {
		return url;
	}

	public StatusModeEnum getSenderStatus() {
		/* AIM do not provide status */
		if (senderStatus == null)
			return new StatusModeEnum(StatusModeEnum.ONLINE);
		return new StatusModeEnum(senderStatus.getValue());
	}

	public MessageFlagsEnum getMessageFlag() {
		return new MessageFlagsEnum(msgFlags.getValue());
	}

	public MessageTypeEnum getMessageType() {
		return new MessageTypeEnum(msgType.getValue());
	}

	public int getTime() {
		return time.getValue();
	}

	public int getEncoding() {
		return encoding.getValue();
	}

	private boolean isRequestAwayMessage() {
		return isRequestAwayMessage;
	}

	private boolean isFileAckOrAbortRequest() {
		return isFileAckOrAbortRequest;
	}

	private boolean isRequestXStatus() {
		return isRequestXStatus;
	}

	private boolean isFileAckOrFileOk() {
		return isFileAckOrFileOk;
	}
}
