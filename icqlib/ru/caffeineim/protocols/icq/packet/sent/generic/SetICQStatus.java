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
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusFlagEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.TcpConnectionFlagEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class SetICQStatus extends Flap {

	private static final byte[] unknownFields = new byte[] {
                                              0x00, 0x0B, 0x00, 0x00, 0x00, 0x00, 0x00,
                                              0x00,
                                              0x00, 0x50, 0x00, 0x00, 0x00, 0x03, 0x00,
                                              0x00,
                                              0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                                              0x00,
                                              0x00, 0x00, 0x00, 0x00};



	public SetICQStatus(StatusModeEnum statusMode,
			StatusFlagEnum statusFlag,
			TcpConnectionFlagEnum tcpConnectionFlag, byte[] localIP,
			int p2pPort) {

		super(2);
                int webAware = 0x00000000;
                if (statusMode.getWebAware()) webAware |= 0x10080000;
		Snac snac = new Snac(0x01, 0x1E, 0, 0, 0);
		/* adding TLVs */
		snac.addTlvToSnac(new Tlv(webAware|statusMode.getMode() | statusFlag.getFlag(),
				RawData.DWORD_LENGHT, 0x06));
		snac.addTlvToSnac(new Tlv(0x00, RawData.WORD_LENGHT, 0x08));
		/* Split TLV 0x0C into RawDatas */
		snac.addRawDataToSnac(new RawData(0x000C0025, RawData.DWORD_LENGHT));

		/* ADD Internal IP */
		snac.addRawDataToSnac(new RawData(localIP));

		snac.addRawDataToSnac(new RawData(p2pPort, RawData.DWORD_LENGHT));
		snac.addRawDataToSnac(new RawData(tcpConnectionFlag.getTcpConnectionFlag(),
				RawData.BYTE_LENGHT));

		snac.addRawDataToSnac(new RawData(unknownFields));
		
		this.addSnac(snac);
	}
}