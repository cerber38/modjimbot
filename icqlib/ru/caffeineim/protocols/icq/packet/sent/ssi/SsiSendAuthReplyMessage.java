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
package ru.caffeineim.protocols.icq.packet.sent.ssi;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class SsiSendAuthReplyMessage extends Flap {
	
	/** 
	 * Creates a new instance of SsiSendAuthReplyMessage 
	 * @throws ConvertStringException 
	 */
	public SsiSendAuthReplyMessage(String uin, String message, boolean auth) 
			throws ConvertStringException {
		super(2);
		Snac snac = new Snac(0x13, 0x1A, 0x00, 0x00, 0x0000001A);	
		
		// uin lenght
		snac.addRawDataToSnac(new RawData(uin.length(), RawData.BYTE_LENGHT));
		
		// uin
		snac.addRawDataToSnac(new RawData(uin));
		
		// auth flag		
		snac.addRawDataToSnac(new RawData(auth ? 0x01 : 0x00, RawData.BYTE_LENGHT));
		
		byte[] msg = StringTools.stringToByteArray(message);
		
		// reason message len
		snac.addRawDataToSnac(new RawData(msg.length, RawData.WORD_LENGHT));
		
		// reason message
		snac.addRawDataToSnac(new RawData(msg));
		
		// unknown
		snac.addRawDataToSnac(new RawData(0x00, RawData.WORD_LENGHT));
		
		addSnac(snac);
	}	
}