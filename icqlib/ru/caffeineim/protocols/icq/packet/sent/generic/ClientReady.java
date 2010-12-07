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
public class ClientReady extends Flap {

	public ClientReady() {
		super(2);
		Snac snac = new Snac(0x01, 0x02, 0, 0, 0);

		// 00 01 00 03 01 10 04 7B FAMILY Family 1 at version 3.
		snac.addRawDataToSnac(new RawData(0x00010003, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 13 00 02 01 10 04 7B FAMILY Family 19 at version 2.
		snac.addRawDataToSnac(new RawData(0x00130002, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 02 00 01 01 01 04 7B FAMILY Family 2 at version 1.
		snac.addRawDataToSnac(new RawData(0x00020001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0101047B, RawData.DWORD_LENGHT));

		// 00 03 00 01 01 10 04 7B FAMILY Family 3 at version 1.
		snac.addRawDataToSnac(new RawData(0x00030001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 15 00 01  01 10 04 7B FAMILY Family 21 at version 1.
		snac.addRawDataToSnac(new RawData(0x00150001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 04 00 01 01 10 04 7B FAMILY Family 4 at version 1.
		snac.addRawDataToSnac(new RawData(0x00040001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 06 00 01 01 10 04 7B FAMILY Family 6 at version 1.
		snac.addRawDataToSnac(new RawData(0x00060001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 09 00 01 01 10 04 7B FAMILY Family 9 at version 1.
		snac.addRawDataToSnac(new RawData(0x00090001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 0A 00 01 01 10 04 7B FAMILY Family 10 at version 1.
		snac.addRawDataToSnac(new RawData(0x000A0001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		// 00 0B 00 01 01 10 04 7B FAMILY Family 11 at version 1.
		snac.addRawDataToSnac(new RawData(0x000B0001, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(0x0110047B, RawData.DWORD_LENGHT));

		this.addSnac(snac);
	}
}