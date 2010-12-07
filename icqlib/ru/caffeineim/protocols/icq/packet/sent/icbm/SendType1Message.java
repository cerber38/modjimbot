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
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageChannelEnum;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel
 */
public class SendType1Message extends SendMessage {

	public SendType1Message(String uin, String message) throws ConvertStringException {
		super(uin, new MessageChannelEnum(MessageChannelEnum.MESSAGE_CHANNEL_1));

		// TLV(2) containing [ TLV(1281):caps | TLV(257):msg ] 
		Tlv tlv2 = new Tlv(0x02);

		// ------ TLV(0501) contains capabilities
		Tlv tlv0501 = new Tlv(0x0106, RawData.WORD_LENGHT, 0x0501);

		// ------ TLV(0101) contains the message
		// Message Charset : UTF8
		Tlv tlv0101 = new Tlv(0x00020000, RawData.DWORD_LENGHT, 0x0101);		
		
		// The message    
		tlv0101.appendRawDataToTlv(new RawData(StringTools.stringToUcs2beByteArray(message)));

		// Building Tlv(2)
		tlv2.appendTlvToTlv(tlv0501);
		tlv2.appendTlvToTlv(tlv0101);		
		
		// put TLV(2) in SNAC
		snac.addTlvToSnac(tlv2);

		// ------  empty TLV(6) store message if recipient offline.
		snac.addRawDataToSnac(new RawData(0x0006, RawData.WORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0000, RawData.WORD_LENGHT));

		this.addSnac(snac);
	}
}
