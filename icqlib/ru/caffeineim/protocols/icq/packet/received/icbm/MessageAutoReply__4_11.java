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

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.XStatusResponseEvent;
import ru.caffeineim.protocols.icq.integration.listeners.XStatusListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageFlagsEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageTypeEnum;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 *   @author Samolisov Pavel
 */
public class MessageAutoReply__4_11 extends ReceivedPacket {

	public static final short UNSUPPORTED_CHANNEL = 1;
	public static final short BUSTED_PAYLOAD      = 2;
	public static final short CHANNEL_SPECIFIC    = 3;

	private String message;
	private MessageTypeEnum messageType;
	private RawData messageChannel;
	private String senderID;

	private boolean isResponseXStatus = false;

	/**
	 * Creates a new instance of MessageAutoReply__4_11
	 */
	public MessageAutoReply__4_11(byte[] array) throws Exception {
		super(array, true);

		byte data[] = getSnac().getDataFieldByteArray();
		int index = 0;
		RawData id = new RawData(data, index, 2*RawData.DWORD_LENGHT);
		index += 2*RawData.DWORD_LENGHT;
		messageChannel = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;
		RawData uinLg = new RawData(data, index, RawData.BYTE_LENGHT);
		index += RawData.BYTE_LENGHT;
		senderID = new String(data, index, uinLg.getValue());
		index += uinLg.getValue();
		RawData reasonCode = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;

		if (reasonCode.getValue() == CHANNEL_SPECIFIC) {
            if (index >= data.length) {    //by Пушкожук - MDC присылает странный пакет
                messageType = new MessageTypeEnum(MessageTypeEnum.UNKNOWN); //иначе будет ошибка в другом месте
                return;
            } 
			switch (messageChannel.getValue()) {
				case 1:
					getChannel1Msg(data, index);
					break;
				case 2:
					getChannel2Msg(data, index);
					break;
				default:
					throw new Exception("Unknown channel");
			}
		}

		if (messageChannel.getValue() == 2 && messageType.getType() == MessageTypeEnum.XSTATUS_MESSAGE) {
			isResponseXStatus = true;
		}
	}

	private void getChannel1Msg(byte[] data, int index) throws ConvertStringException {
		// 1st fragment
		RawData id1 = new RawData(data, index++, RawData.BYTE_LENGHT);
		RawData ver1 = new RawData(data, index++, RawData.BYTE_LENGHT);
		RawData frag1Lg = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;
		RawData capabilities = new RawData(data, index, frag1Lg.getValue());

		// 1st fragment
		RawData id2 = new RawData(data, index++, RawData.BYTE_LENGHT);
		RawData ver2 = new RawData(data, index++, RawData.BYTE_LENGHT);
		RawData frag2Lg = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;

		// text parsing
		message = parseMessageType1(data, index, frag2Lg.getValue());
	}

	private void getChannel2Msg(byte[] data, int index) throws ConvertStringException {
		// 1st part
		RawData part1Lg = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		index += RawData.WORD_LENGHT;
		RawData protocolVersion = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		index += RawData.WORD_LENGHT;
		RawData guid = new RawData(data, index, 16); // Little endian !
		index += 16;
		RawData unknownWord = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;
		RawData clientCapabilities = new RawData(data, index, RawData.DWORD_LENGHT); // Little endian !
		index += RawData.DWORD_LENGHT;
		RawData unknownByte = new RawData(data, index, RawData.BYTE_LENGHT);
		index += RawData.BYTE_LENGHT;
		RawData cntr1 = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		index += RawData.WORD_LENGHT;

		// 2nd part
		RawData part2Lg = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		index += RawData.WORD_LENGHT;
		RawData cntr2 = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		part2Lg.invertIndianness();
		index += part2Lg.getValue();

		message = parseMessageType2(data, index);
	}

	private String parseMessageType1(byte[] data, int index, int length) throws ConvertStringException {
		RawData charset = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;
    	RawData language = new RawData(data, index, RawData.WORD_LENGHT);
		index += RawData.WORD_LENGHT;
		length -= 2*RawData.WORD_LENGHT;

		return StringTools.utf8ByteArrayToString(data, index, length);
	}

	private String parseMessageType2(byte[] data, int index) throws ConvertStringException {
		messageType = new MessageTypeEnum(data[index++]);
		MessageFlagsEnum msgFlag = new MessageFlagsEnum(data[index++]);
		RawData status = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		index += RawData.WORD_LENGHT;
		RawData priority = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		index += RawData.WORD_LENGHT;
		RawData msgLg = new RawData(data, index, RawData.WORD_LENGHT); // Little endian !
		index += RawData.WORD_LENGHT;

		// Message string ASCIIZ
		msgLg.invertIndianness();
		message = StringTools.utf8ByteArrayToString(data, index, msgLg.getValue());

		if (messageType.getType() == MessageTypeEnum.XSTATUS_MESSAGE) {
			// Пропускаем 66 байт - информация о плагине
			index += 66;

			// Пропускаем 24 байта - заголовок вложенного Type 1 сообщения
			index += 24;

			message = StringTools.utf8ByteArrayToString(data, index, data.length - index);
		}

		if (messageType.getType() == MessageTypeEnum.PLAIN_MESSAGE) {
			RawData textColor = new RawData(data, index, RawData.DWORD_LENGHT);
			index += RawData.DWORD_LENGHT;
			RawData backgroundColor = new RawData(data, index, RawData.DWORD_LENGHT);
			index += RawData.DWORD_LENGHT;
			// TODO fix this invocation exception
			/*RawData guidLg = new RawData(data, index, RawData.DWORD_LENGHT);
			index += RawData.DWORD_LENGHT;
			RawData guid = new RawData(data, index, guidLg.getValue());*/
		}

		return message;
	}

	public void notifyEvent(OscarConnection connection) {
		if (getMessageType().getType() == MessageTypeEnum.XSTATUS_MESSAGE) {
			notifyXStatusRequest(connection);
		}
	}

	private void notifyXStatusRequest(OscarConnection connection) {
		XStatusResponseEvent e = new XStatusResponseEvent(this);
		for (int i = 0; i < connection.getXStatusListeners().size(); i++) {
			XStatusListener l = (XStatusListener) connection.getXStatusListeners().get(i);
			l.onXStatusResponse(e);
		}
	}

	public String getMessage() {
		return message;
	}

	public String getSenderID() {
		return senderID;
	}

	public int getChannel() {
		return messageChannel.getValue();
	}

	public MessageTypeEnum getMessageType() {
		return messageType;
	}

	public boolean isResponseXStatus() {
		return isResponseXStatus;
	}
}
