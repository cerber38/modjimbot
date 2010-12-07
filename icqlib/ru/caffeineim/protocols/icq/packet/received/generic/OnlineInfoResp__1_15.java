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
package ru.caffeineim.protocols.icq.packet.received.generic;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.integration.events.StatusEvent;
import ru.caffeineim.protocols.icq.integration.listeners.OurStatusListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class OnlineInfoResp__1_15 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(OnlineInfoResp__1_15.class);

	private int status = 0;

	/**
	 * Creates a new instance of OnlineInfoResp__1_15
	 */
	public OnlineInfoResp__1_15(byte[] array) {
		super(array, true);
		int index = 0;
		Snac snac = getSnac();
		byte data[] = snac.getDataFieldByteArray();
		if((snac.getFlag0() & 0x80) != 0) {
			int unknownDataLen = new RawData(data, index, RawData.WORD_LENGHT).getValue();
			index += unknownDataLen + 2;
		}
		int uinLg = new RawData(data, index, RawData.BYTE_LENGHT).getValue();
		++index;
		String uin = new RawData(data, index, uinLg).getStringValue();
		index += uin.length();
		int warnLvl = new RawData(data, index, RawData.WORD_LENGHT).getValue();
		index += 2;
		int tlvNb = new RawData(data, index, RawData.WORD_LENGHT).getValue();
		index += 2;
		for(int i = 0; i < tlvNb; ++i) {
			Tlv tlvi = new Tlv(data, index);
			if(0x06 == tlvi.getType()) {
				status = tlvi.getValue();
			}
			index += (tlvi.getLength() + 4);
		}
	}

	public int getStatusFlag() {
		return status & 0xFFFF0000;
	}

	public int getStatusMode() {
		return status & 0x0000FFFF;
	}

	public void execute(OscarConnection connection) throws Exception {
	}

	public void notifyEvent(OscarConnection connection) {
		StatusEvent e = new StatusEvent(this);
		for (int i = 0; i < connection.getOurStatusListeners().size(); i++) {
			OurStatusListener l = (OurStatusListener) connection.getOurStatusListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onStatusResponse()");
			l.onStatusResponse(e);
		}
	}
}
