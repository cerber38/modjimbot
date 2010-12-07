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
package ru.caffeineim.protocols.icq.packet.sent.generic;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class ClientFamilies extends Flap {

	public ClientFamilies() {
		super(2);
		Snac snac = new Snac(0x01, 0x17, 0x0, 0x0, 0x00);
		/* add required families */
		// FAMILY Family 1 at version 3.
		snac.addRawDataToSnac(new RawData(0x00010003, RawData.DWORD_LENGHT));
		// FAMILY Family 19 at version 4 for ICQ2002.
		snac.addRawDataToSnac(new RawData(0x00130004, RawData.DWORD_LENGHT));
		// FAMILY Family 2 at version 1.
		snac.addRawDataToSnac(new RawData(0x00020001, RawData.DWORD_LENGHT));
		// FAMILY Family 3 at version 1.
		snac.addRawDataToSnac(new RawData(0x00030001, RawData.DWORD_LENGHT));
		// FAMILY Family 21 at version 1.
		snac.addRawDataToSnac(new RawData(0x00150001, RawData.DWORD_LENGHT));
		// FAMILY Family 4 at version 1.
		snac.addRawDataToSnac(new RawData(0x00040001, RawData.DWORD_LENGHT));
		// FAMILY Family 6 at version 1.
		snac.addRawDataToSnac(new RawData(0x00060001, RawData.DWORD_LENGHT));
		// FAMILY Family 9 at version 1.
		snac.addRawDataToSnac(new RawData(0x00090001, RawData.DWORD_LENGHT));
		// FAMILY Family 10 at version 1.
		snac.addRawDataToSnac(new RawData(0x000A0001, RawData.DWORD_LENGHT));
		// Family 11 at version 1.
		snac.addRawDataToSnac(new RawData(0x000B0001, RawData.DWORD_LENGHT));
		/* adding snac to the flap */
		this.addSnac(snac);
	}
}