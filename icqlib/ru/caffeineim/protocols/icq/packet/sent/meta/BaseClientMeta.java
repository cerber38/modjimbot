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
package ru.caffeineim.protocols.icq.packet.sent.meta;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;
import ru.caffeineim.protocols.icq.Tlv;

/**
 * <p>Created by 30.03.2008
 *   @author Samolisov Pavel
 */
public abstract class BaseClientMeta extends Flap {
	
	protected Snac snac;
	protected Tlv tlv;
	
	/**
	 * Creates a new instance of BaseClientMeta
	 *  
	 * @param lenght data lenght
	 * @param uinForRequest for request ICQ UIN
	 * @param type type of request
	 */
	public BaseClientMeta(int lenght, String uinForRequest, int type) {
		super(2);
		snac = new Snac(0x15, 0x02, 0x00, 0x00, 0x00);	
				
		// Creating TLV 
		tlv = new Tlv(0x01);
		
		// Chunk size
		tlv.appendRawDataToTlv(new RawData(lenght, RawData.WORD_LENGHT));
				 
		// request owner UIN
		RawData rUin = new RawData(Integer.parseInt(uinForRequest), RawData.DWORD_LENGHT);
		rUin.invertIndianness();				
		tlv.appendRawDataToTlv(rUin);
		
		// Request Type
		RawData rdType = new RawData(type, RawData.WORD_LENGHT);
		rdType.invertIndianness();
		tlv.appendRawDataToTlv(rdType);
		
		// Adding sequence-id 
		tlv.appendRawDataToTlv(new RawData(0x0200, RawData.WORD_LENGHT));		
	}

	/**
	 * Creates a new instance of BaseClientMeta
	 *  
	 * @param lenght data lenght
	 * @param uinForRequest for request ICQ UIN
	 * @param type of request
	 * @param subType subtype of meta request
	 */
	public BaseClientMeta(int lenght, String uinForRequest, int type, int subType) {
		this(lenght, uinForRequest, type);
		
		// Request SubType
		RawData rdSubType = new RawData(subType, RawData.WORD_LENGHT);
		rdSubType.invertIndianness();
		tlv.appendRawDataToTlv(rdSubType);		
	}
	
	public void finalizePacket() {
		snac.addTlvToSnac(tlv);
		addSnac(snac);
	}
}