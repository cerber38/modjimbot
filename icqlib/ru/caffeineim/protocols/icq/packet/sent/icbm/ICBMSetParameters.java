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

/**
 * Setting ICBM Server parameters
 *
 * <p>Created by 22.06.2008
 *   @author Prolubnikov Dmitry
 */
public class ICBMSetParameters extends Flap {

	public ICBMSetParameters() {
        super(2);
        Snac snac = new Snac(0x04, 0x02, 0x0, 0x0, 0x00);
        snac.addRawDataToSnac(new RawData(0, RawData.WORD_LENGHT));				// channel to setup
        snac.addRawDataToSnac(new RawData(0x00000003, RawData.DWORD_LENGHT));	// message flags
        snac.addRawDataToSnac(new RawData(8000, RawData.WORD_LENGHT));			// max message snac size
        snac.addRawDataToSnac(new RawData(999, RawData.WORD_LENGHT));			// max sender warning level
        snac.addRawDataToSnac(new RawData(999, RawData.WORD_LENGHT));			// max receiver warning level
        snac.addRawDataToSnac(new RawData(0x0000, RawData.WORD_LENGHT));		// minimum message interval (sec)
        snac.addRawDataToSnac(new RawData(0x0000, RawData.WORD_LENGHT));		// unknown
        this.addSnac(snac);
	}
}
