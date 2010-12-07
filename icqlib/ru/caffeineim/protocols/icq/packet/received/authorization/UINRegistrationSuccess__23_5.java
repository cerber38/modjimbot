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
package ru.caffeineim.protocols.icq.packet.received.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.integration.events.UINRegistrationSuccessEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MetaInfoListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Egor Baranov
 */
public class UINRegistrationSuccess__23_5 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(UINRegistrationSuccess__23_5.class);

	protected String uin;

    public UINRegistrationSuccess__23_5(byte array[]) {
        super(array, true);
        RawData uinData = new RawData(array, 50, RawData.DWORD_LENGHT);
        uinData.invertIndianness();
        uin = uinData.toStringValue();
    }

    public String getNewUIN() {
        return uin;
    }

    public void notifyEvent(OscarConnection connection) {
        UINRegistrationSuccessEvent e = new UINRegistrationSuccessEvent(this);
        for (int i = 0; i < connection.getMetaInfoListeners().size(); i++) {
            MetaInfoListener l = (MetaInfoListener) connection.getMessagingListeners().get(i);
            log.debug("notify listener " + l.getClass().getName() + " onRegisterNewUINSucces()");
            l.onRegisterNewUINSuccess(e);
        }
    }

}
