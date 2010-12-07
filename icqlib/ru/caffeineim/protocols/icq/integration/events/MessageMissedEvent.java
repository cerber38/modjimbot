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

import ru.caffeineim.protocols.icq.packet.received.icbm.MissedMessage__4_10;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageMissedTypeEnum;

/**
 * <p>Created by 23.03.2008
 *   @author Samolisov Pavel
 */
public class MessageMissedEvent extends EventObject {
	
	private static final long serialVersionUID = -1249038091908724243L;

	public MessageMissedEvent(MissedMessage__4_10 source) {
		super(source);
	}
	
	public int getMissedMsgCount() {
		return ((MissedMessage__4_10) source).getMissedMsgCount();
	}
	
	public String getUin() {
		return ((MissedMessage__4_10) source).getUin();
	}
	
	public MessageMissedTypeEnum getReason() {
		return ((MissedMessage__4_10) source).getReason();
	}
}
