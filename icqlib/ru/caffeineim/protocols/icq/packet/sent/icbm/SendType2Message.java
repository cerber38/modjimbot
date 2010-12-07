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
public class SendType2Message extends SendMessage {

	private static final byte[] CAPABILITY1 = {
                                            0x09, 0x46, 0x13, 0x49, 0x4C, 0x7F, 0x11,
                                            (byte) 0xD1, (byte) 0x82, 0x22, 0x44, 0x45,
                                            0x53, 0x54, 0x00, 0x00};

	private static final byte[] CAPABILITY2 = {
                                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                            0x00, 0x00};

	private static final byte[] UNKNOWN = {
                                        0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                        0x00, 0x00, 0x00, 0x00, 0x00};
    
	private static final byte[] CAP_UTF8_GUID = { 
										(byte) 0x7b, (byte) 0x30, (byte) 0x39, (byte) 0x34, (byte) 0x36,
										(byte) 0x31, (byte) 0x33, (byte) 0x34, (byte) 0x45, (byte) 0x2D,
										(byte) 0x34, (byte) 0x43, (byte) 0x37, (byte) 0x46, (byte) 0x2D, 
										(byte) 0x31, (byte) 0x31, (byte) 0x44, (byte) 0x31, (byte) 0x2D, 
										(byte) 0x38, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x2D, 
										(byte) 0x34, (byte) 0x34, (byte) 0x34, (byte) 0x35, (byte) 0x35, 
										(byte) 0x33, (byte) 0x35, (byte) 0x34, (byte) 0x30, (byte) 0x30, 
										(byte) 0x30, (byte) 0x30, (byte) 0x7D};

	public SendType2Message(String uin, String message) throws ConvertStringException {
		super(uin, new MessageChannelEnum(MessageChannelEnum.MESSAGE_CHANNEL_2));

		// Acktype 
		Tlv tlv5 = new Tlv(0x00, RawData.WORD_LENGHT, 0x05);
    
		// Time
		tlv5.appendRawDataToTlv(new RawData(0x00000000, RawData.DWORD_LENGHT));

		// Message id    
		tlv5.appendRawDataToTlv(new RawData(0x00000000, RawData.DWORD_LENGHT));
    
		// Capability
		tlv5.appendRawDataToTlv(new RawData(CAPABILITY1));
    
		// Normal message 
		tlv5.appendTlvToTlv(new Tlv(0x01, RawData.WORD_LENGHT, 0x0A));
    
		// Unknown
		tlv5.appendRawDataToTlv(new RawData(0x000F0000, RawData.DWORD_LENGHT));

		// Len 
		Tlv tlv2711 = new Tlv(0x1B00, RawData.WORD_LENGHT, 0x2711);
    
		// TCP Ver
		tlv2711.appendRawDataToTlv(new RawData(0x0B00, RawData.WORD_LENGHT));
    
		// Capability 2
		tlv2711.appendRawDataToTlv(new RawData(CAPABILITY2));
    
		// Unknown
		tlv2711.appendRawDataToTlv(new RawData(0x03, 0x03));
		
		// Normal Message
		tlv2711.appendRawDataToTlv(new RawData(0x00000000, RawData.DWORD_LENGHT));

		// SEQ1    
		tlv2711.appendRawDataToTlv(new RawData(0xffff, RawData.WORD_LENGHT));
    
		// ???
		tlv2711.appendRawDataToTlv(new RawData(0x0e00, RawData.WORD_LENGHT));
		
		// SEQ1 copy
		tlv2711.appendRawDataToTlv(new RawData(0xffff, RawData.WORD_LENGHT));
    
		// Unknown
		tlv2711.appendRawDataToTlv(new RawData(UNKNOWN));
    
		// 01 00 msg type?
		tlv2711.appendRawDataToTlv(new RawData(0x0100, RawData.WORD_LENGHT));
    
		// ??
		tlv2711.appendRawDataToTlv(new RawData(0x0000, RawData.WORD_LENGHT));
    
		// ?? prio
		tlv2711.appendRawDataToTlv(new RawData(0x2100, RawData.WORD_LENGHT));
    
		
		// message length
		byte[] msg = StringTools.stringToByteArray(message);
		RawData rLen = new RawData(msg.length + 1, RawData.WORD_LENGHT);
		rLen.invertIndianness();
		tlv2711.appendRawDataToTlv(rLen);
    
		// message UTF8
		tlv2711.appendRawDataToTlv(new RawData(msg));
		tlv2711.appendRawDataToTlv(new RawData(0x00, RawData.BYTE_LENGHT));
    
		// Foreground color
		tlv2711.appendRawDataToTlv(new RawData(0x00, RawData.DWORD_LENGHT));
    
		// Background color
		tlv2711.appendRawDataToTlv(new RawData(0xFFFFFF00, RawData.DWORD_LENGHT));
		
		// Unknown
		tlv2711.appendRawDataToTlv(new RawData(0x26000000, RawData.DWORD_LENGHT));
    
		// UTF8 GUID
		tlv2711.appendRawDataToTlv(new RawData(CAP_UTF8_GUID));
		
		tlv5.appendTlvToTlv(tlv2711);
		snac.addTlvToSnac(tlv5);

		// GUIDLEN
		snac.addRawDataToSnac(new RawData(0x03, RawData.WORD_LENGHT));
    
		// GUID
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));
		this.addSnac(snac);
	}
}
