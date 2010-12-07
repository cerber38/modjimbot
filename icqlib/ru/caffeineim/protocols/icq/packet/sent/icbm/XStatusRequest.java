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

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class XStatusRequest extends SendMessage {
	
	private static final byte[] CAPABILITY1 = {
        			0x09, 0x46, 0x13, 0x49, 0x4C, 0x7F, 0x11,
        			(byte) 0xD1, (byte) 0x82, 0x22, 0x44, 0x45,
        			0x53, 0x54, 0x00, 0x00};

	private static final byte[] CAPABILITY2 = {
        			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        			0x00, 0x00};

	private static final byte[] CAPABILITY3 = {
					0x1A, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 
					0x00, 0x00, 0x4F, 0x00, 0x3B, 0x60, (byte)0xB3, 
					(byte)0xEF, (byte)0xD8};

	private static final byte[] CAPABILITY4 = {
					0x2A, 0x6C, 0x45, (byte)0xA4, (byte)0xE0, (byte)0x9C,
					0x5A, 0x5E, 0x67, (byte)0xE8, 0x65, 0x08,
					0x00, 0x2A, 0x00, 0x00};

	private static final byte[] UNKNOWN = {
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00, 0x00, 0x00};

	public XStatusRequest(String userId, String myId) {
		
		super(userId, new MessageChannelEnum(MessageChannelEnum.MESSAGE_CHANNEL_2));
		
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
		tlv2711.appendRawDataToTlv(new RawData(0x0A00, RawData.WORD_LENGHT));
    
		// Capability 2
		tlv2711.appendRawDataToTlv(new RawData(CAPABILITY2));
		tlv2711.appendRawDataToTlv(new RawData(0x0000, RawData.WORD_LENGHT));
    
		// Unknown
		//tlv2711.appendRawDataToTlv(new RawData(0x03, 0x03));		
		// Normal Message
		tlv2711.appendRawDataToTlv(new RawData(0x00000000, RawData.DWORD_LENGHT));

		// Unknown
		tlv2711.appendRawDataToTlv(new RawData(0x00, RawData.BYTE_LENGHT));		
		
		// Seems to be a downcounter
		tlv2711.appendRawDataToTlv(new RawData(0x95D2, RawData.WORD_LENGHT));
		
		// Length of following data
		tlv2711.appendRawDataToTlv(new RawData(0x0E00, RawData.WORD_LENGHT));
		
		// Seems to be a downcounter
		tlv2711.appendRawDataToTlv(new RawData(0x95D2, RawData.WORD_LENGHT));
		
		// Unknown
		tlv2711.appendRawDataToTlv(new RawData(UNKNOWN));

		// Plugin capabilites
		tlv2711.appendRawDataToTlv(new RawData(CAPABILITY3));
		tlv2711.appendRawDataToTlv(new RawData(CAPABILITY4));
		
		// des
		tlv2711.appendRawDataToTlv(new RawData(0x00, RawData.BYTE_LENGHT));
		
		//	Plugin information
		tlv2711.appendRawDataToTlv(new RawData("Script Plug-in: Remote Notification Arrive"));
		tlv2711.appendRawDataToTlv(new RawData(0x00, RawData.BYTE_LENGHT));
		
		// ** Request message **
		// Message type (unknown), message flag(normal message)
		tlv2711.appendRawDataToTlv(new RawData(0x0001, RawData.WORD_LENGHT));
		// status, priority, message string length
		tlv2711.appendRawDataToTlv(new RawData(UNKNOWN));
		// unknown F5
		tlv2711.appendRawDataToTlv(new RawData(0x13010000, RawData.DWORD_LENGHT));
		// unknown
		tlv2711.appendRawDataToTlv(new RawData(0xF1010000, RawData.DWORD_LENGHT));

		// Request 
		int trans = (int)Math.random()*18;
		tlv2711.appendRawDataToTlv(new RawData("<N><QUERY>&lt;Q&gt;&lt;PluginID&gt;srvMng&lt;/PluginID&gt;&lt;/Q&gt;</QUERY><NOTIFY>&lt;srv&gt;&lt;id&gt;cAwaySrv&lt;/id&gt;&lt;req&gt;&lt;id&gt;AwayStat&lt;/id&gt;&lt;trans&gt;" + trans + "&lt;/trans&gt;&lt;senderId&gt;" + myId + "&lt;/senderId&gt;&lt;/req&gt;&lt;/srv&gt;</NOTIFY></N>"));		
		
		tlv5.appendTlvToTlv(tlv2711);
		snac.addTlvToSnac(tlv5);
		
		// GUIDLEN
		snac.addRawDataToSnac(new RawData(0x03, RawData.WORD_LENGHT));
    
		// GUID
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));
		
		this.addSnac(snac);
	}
}