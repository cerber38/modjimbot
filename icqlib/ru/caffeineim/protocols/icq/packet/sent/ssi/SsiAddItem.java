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
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.contacts.Contact;
import ru.caffeineim.protocols.icq.contacts.Group;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class SsiAddItem extends Flap {
	
	/** 
	 * Creates a new instance of SsiAddNewItem - add User 
	 * @throws ConvertStringException 
	 */
	public SsiAddItem(Contact cnt) throws ConvertStringException {
		super(2);
		Snac snac = new Snac(0x13, 0x08, 0x00, 0x00, 0x00000008);	
		
		// uin lenght
		snac.addRawDataToSnac(new RawData(cnt.getId().length(), RawData.WORD_LENGHT));
		
		// uin
		snac.addRawDataToSnac(new RawData(cnt.getId()));
		
		// group id
		snac.addRawDataToSnac(new RawData(cnt.getGroupId(), RawData.WORD_LENGHT));
			
		// item id
		snac.addRawDataToSnac(new RawData(cnt.getItemId(), RawData.WORD_LENGHT));
		
		// type of item
		snac.addRawDataToSnac(new RawData(0x0000, RawData.WORD_LENGHT));

		byte[] nick = StringTools.stringToByteArray(cnt.getNickName());
		
		// addition data
		snac.addRawDataToSnac(new RawData(20 + nick.length, RawData.WORD_LENGHT));

		// nick name
		Tlv tlv1 = new Tlv(0x0131);		
		tlv1.appendRawDataToTlv(new RawData(nick));
		snac.addTlvToSnac(tlv1);
			
		// email		
		snac.addTlvToSnac(new Tlv(0x013A));
			
		// comment
		snac.addTlvToSnac(new Tlv(0x013C));		
			
		// unknown
		snac.addTlvToSnac(new Tlv(0x0137));		

		// unknown		
		snac.addTlvToSnac(new Tlv(0x0066));		

		addSnac(snac);
	}
	
	/** 
	 * Creates a new instance of SsiAddNewItem - add Group 
	 * @throws ConvertStringException 
	 */
	public SsiAddItem(Group grp) throws ConvertStringException {
		super(2);
		Snac snac = new Snac(0x13, 0x08, 0x00, 0x00, 0x00000008);	
		
		byte[] groupId = StringTools.stringToByteArray(grp.getId());
		
		// group name lenght
		snac.addRawDataToSnac(new RawData(groupId.length, RawData.WORD_LENGHT));
		
		// group name
		snac.addRawDataToSnac(new RawData(groupId));
		
		// group id
		snac.addRawDataToSnac(new RawData(grp.getGroupId(), RawData.WORD_LENGHT));
			
		// item id
		snac.addRawDataToSnac(new RawData(0x0000, RawData.WORD_LENGHT));
		
		// type of item
		snac.addRawDataToSnac(new RawData(0x0001, RawData.WORD_LENGHT));

		// lenght of addition data
		snac.addRawDataToSnac(new RawData(0x0000, RawData.WORD_LENGHT));

		addSnac(snac);
	}
}