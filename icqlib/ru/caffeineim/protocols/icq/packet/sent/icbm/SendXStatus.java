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

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.setting.enumerations.XStatusModeEnum;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class SendXStatus extends Flap {

	public Snac snac;

	private static final byte[] CAPABILITY = {
        			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
        			0x00, 0x00};
	
	private static final byte[] CAPABILITY2 = {
					0x1A, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 
					0x00, 0x00, 0x4F, 0x00, 0x3B, 0x60, (byte)0xB3, 
					(byte)0xEF, (byte)0xD8};

	private static final byte[] CAPABILITY3 = {
					0x2A, 0x6C, 0x45, (byte)0xA4, (byte)0xE0, (byte)0x9C,
					0x5A, 0x5E, 0x67, (byte)0xE8, 0x65, 0x08,
					0x00, 0x2A, 0x00, 0x00};
		

	private static final byte[] UNKNOWN = {
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00, 0x00, 0x00};
	
	public SendXStatus(int Time, int msgID, String userId, String myId, int senderTcpVersion, 
				XStatusModeEnum xstatus, String msg, String extmsg) throws ConvertStringException {
		super(2);
		snac = new Snac(0x04, 0x0B, 0x00, 0x00, 0x00);
	    
		// Time
		snac.addRawDataToSnac(new RawData(Time, RawData.DWORD_LENGHT));
	   
		// Message id     
		snac.addRawDataToSnac(new RawData(msgID, RawData.DWORD_LENGHT));
	    
		// channel of the message, 2 by default
		snac.addRawDataToSnac(new RawData(0x02, RawData.WORD_LENGHT));
	    
		// UIN
		snac.addRawDataToSnac(new RawData(userId.length(), RawData.BYTE_LENGHT));
		snac.addRawDataToSnac(new RawData(userId));
			
		// Reason code
		snac.addRawDataToSnac(new RawData(0x03, RawData.WORD_LENGHT));
		
		// Length of following data 
		snac.addRawDataToSnac(new RawData(0x1B00, RawData.WORD_LENGHT));

		// TCP Ver
		snac.addRawDataToSnac(new RawData(senderTcpVersion, RawData.WORD_LENGHT));						

		// Unknown
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));
		
		// Capability
		snac.addRawDataToSnac(new RawData(CAPABILITY));
	    
		// Unknown 
		snac.addRawDataToSnac(new RawData(0x01, RawData.BYTE_LENGHT));
			
		// Client capabilites flag (normal message)
		snac.addRawDataToSnac(new RawData(0x00000000, RawData.DWORD_LENGHT));

		// Unknown
		snac.addRawDataToSnac(new RawData(0x72, RawData.BYTE_LENGHT));
			
		// seems to be a downcounter
		snac.addRawDataToSnac(new RawData(0x190E, RawData.WORD_LENGHT));

		// ** Plugin info **		
		// length of Following data
		snac.addRawDataToSnac(new RawData(0x0072, RawData.WORD_LENGHT));
	
		// Message type (unknown), message flag(unknown)
		snac.addRawDataToSnac(new RawData(0x1900, RawData.WORD_LENGHT));
			
		// unknown
		snac.addRawDataToSnac(new RawData(0x00000000, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x00000000, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0000, RawData.WORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x00, RawData.BYTE_LENGHT));
		
		// response capability
		snac.addRawDataToSnac(new RawData(CAPABILITY2));
		snac.addRawDataToSnac(new RawData(CAPABILITY3));
		
		// unknown
		snac.addRawDataToSnac(new RawData(0x00, RawData.BYTE_LENGHT));
			
		// Plugin information
		snac.addRawDataToSnac(new RawData("Script Plug-in: Remote Notification Arrive"));
		snac.addRawDataToSnac(new RawData(0x00, RawData.BYTE_LENGHT));
			
		// ** XStatus message **
		// Message type (unknown), message flag(normal message)
		snac.addRawDataToSnac(new RawData(0x0001, RawData.WORD_LENGHT));
		// status, priority, message string length
		snac.addRawDataToSnac(new RawData(UNKNOWN));
		// unknown
		snac.addRawDataToSnac(new RawData(0xF5010000, RawData.DWORD_LENGHT));
		// unknown
		snac.addRawDataToSnac(new RawData(0xF1010000, RawData.DWORD_LENGHT));
			
		// XStatus msg body												   
		snac.addRawDataToSnac(new RawData("<NR><RES>&lt;ret event='OnRemoteNotification'&gt;&lt;srv&gt;&lt;id&gt;cAwaySrv&lt;/id&gt;&lt;val srv_id='cAwaySrv'&gt;&lt;Root&gt;&lt;CASXtraSetAwayMessage&gt;&lt;/CASXtraSetAwayMessage&gt;&lt;uin&gt;" + myId + "&lt;/uin&gt;&lt;index&gt;" + xstatus.getXStatus() + "&lt;/index&gt;&lt;title&gt;"));
		// manual convert to byte array		
		snac.addRawDataToSnac(new RawData(StringTools.stringToByteArray(msg)));	
		snac.addRawDataToSnac(new RawData("&lt;/title&gt;&lt;desc&gt;"));
		// manual convert to byte array
		snac.addRawDataToSnac(new RawData(StringTools.stringToByteArray(extmsg)));
		snac.addRawDataToSnac(new RawData("&lt;/desc&gt;&lt;/Root&gt;\r\n"));							
		snac.addRawDataToSnac(new RawData("&lt;/val&gt;&lt;/srv&gt;&lt;srv&gt;&lt;id&gt;cRandomizerSrv&lt;/id&gt;&lt;val srv_id='cRandomizerSrv'&gt;undefined&lt;/val&gt;&lt;/srv&gt;&lt;/ret&gt;</RES></NR>\r\n"));		
		
		this.addSnac(snac);
	}
}