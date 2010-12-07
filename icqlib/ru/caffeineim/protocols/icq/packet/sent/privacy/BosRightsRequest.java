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
package ru.caffeineim.protocols.icq.packet.sent.privacy;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.Snac;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class BosRightsRequest extends Flap {

	public BosRightsRequest() {
		super(2);
		/* add the empty snac */
		Snac snac = new Snac(0x09, 0x02, 0x0, 0x0, 0x00);
		this.addSnac(snac);
	}
}