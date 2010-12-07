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

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class SsiRemoveYourself extends Flap {
	
	/** 
	 * Creates a new instance of SsiRemoveYourself 
	 */
	public SsiRemoveYourself(String uin) {
		super(2);
		Snac snac = new Snac(0x13, 0x16, 0x00, 0x00, 0x00000016);	
		
		// uin lenght
		snac.addRawDataToSnac(new RawData(uin.length(), RawData.BYTE_LENGHT));
		
		// uin
		snac.addRawDataToSnac(new RawData(uin));
				
		addSnac(snac);
	}	
}
