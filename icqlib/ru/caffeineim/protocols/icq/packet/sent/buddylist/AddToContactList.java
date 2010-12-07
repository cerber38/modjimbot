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
package ru.caffeineim.protocols.icq.packet.sent.buddylist;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class AddToContactList extends Flap {

	/**
	 * Add user to contact list
	 * 
	 * @param uin For AIM use Buddy name. For icq use UIN.
	 */
	public AddToContactList(String uin) {
		super(2);
		Snac snac = new Snac(0x03, 0x04, 0x00, 0x00, 0x00);
		
		// uin lenght
		snac.addRawDataToSnac(new RawData(uin.length(), RawData.BYTE_LENGHT));
		
		// uin
		snac.addRawDataToSnac(new RawData(uin));
		
		this.addSnac(snac);
	}
}