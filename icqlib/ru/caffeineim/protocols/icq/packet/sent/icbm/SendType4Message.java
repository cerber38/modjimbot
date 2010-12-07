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
package ru.caffeineim.protocols.icq.packet.sent.icbm;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageChannelEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageFlagsEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageTypeEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class SendType4Message extends SendMessage {

	public SendType4Message(String senderUin, String receiverUin, String message) {
		super(receiverUin, new MessageChannelEnum(MessageChannelEnum.MESSAGE_CHANNEL_4));

		Tlv tlv5 = new Tlv(0x05);

		// Sender ID
		RawData senderId = new RawData(Integer.parseInt(senderUin), RawData.DWORD_LENGHT);
		senderId.invertIndianness();

		tlv5.appendRawDataToTlv(senderId);

		// Message type
		tlv5.appendRawDataToTlv(new RawData(MessageTypeEnum.PLAIN_MESSAGE, RawData.BYTE_LENGHT));

		// Message Flag
		tlv5.appendRawDataToTlv(new RawData(MessageFlagsEnum.NORMAL_MESSAGE, RawData.BYTE_LENGHT));

		// Message length
		RawData messageLength = new RawData(message.length(), RawData.WORD_LENGHT);
		messageLength.invertIndianness();
		tlv5.appendRawDataToTlv(messageLength);

		// The message itself
		tlv5.appendRawDataToTlv(new RawData(message));

		// put TLV(5) in SNAC
		snac.addTlvToSnac(tlv5);

		// ------  empty TLV(3) request an ack from server.
		snac.addRawDataToSnac(new RawData(0x03, RawData.WORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));

		// ------  empty TLV(6) store message if recipient offline.
		snac.addRawDataToSnac(new RawData(0x06, RawData.WORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));

		this.addSnac(snac);
	}
}
