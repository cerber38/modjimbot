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
package ru.caffeineim.protocols.icq.integration.events;

import java.util.EventObject;

import ru.caffeineim.protocols.icq.packet.received.byddylist.IncomingUser__3_11;
import ru.caffeineim.protocols.icq.setting.enumerations.ClientsEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusFlagEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class IncomingUserEvent extends EventObject {

    private static final long serialVersionUID = -5665513460661361463L;

    public IncomingUserEvent(IncomingUser__3_11 source) {
		super(source);
	}

	public String getIncomingUserId() {
		return ( (IncomingUser__3_11) getSource()).getUserId();
	}

	public String getInterlnalIp() {
		return ( (IncomingUser__3_11) getSource()).getInterlnalIp();
	}

	public int getPort() {
		return ( (IncomingUser__3_11) getSource()).getPort();
	}

	public String getExternalIp() {
		return ( (IncomingUser__3_11) getSource()).getExternalIp();
	}

	public StatusModeEnum getStatusMode() {
		return ( (IncomingUser__3_11) getSource()).getStatusMode();
	}


    public ClientsEnum getClientData() {
        return ( (IncomingUser__3_11) getSource()).getClient();
    }
}