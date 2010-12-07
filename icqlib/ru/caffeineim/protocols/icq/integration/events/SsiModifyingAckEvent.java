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

import ru.caffeineim.protocols.icq.packet.received.ssi.SsiModifyingAck__19_14;
import ru.caffeineim.protocols.icq.setting.enumerations.SsiResultModeEnum;

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel 
 */
public class SsiModifyingAckEvent extends EventObject {

    private static final long serialVersionUID = -2803906198129443216L;

    /** 
	 * Creates a new instance of SsiModifyingAckEvent 
	 */
	public SsiModifyingAckEvent(SsiModifyingAck__19_14 source) {
		super(source);		
	}
	
	public SsiResultModeEnum[] getResults() {
		return ((SsiModifyingAck__19_14)getSource()).getResuls();
	}	
}
