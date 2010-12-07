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

import ru.caffeineim.protocols.icq.packet.received.generic.OnlineInfoResp__1_15;

/**
 * <p>Created by
 *   @author Lo�c Broquet 
 */
public class StatusEvent extends EventObject {
    
    private static final long serialVersionUID = -459517084246159557L;

    /** 
	 * Creates a new instance of StatusEvent 
	 */
	public StatusEvent(OnlineInfoResp__1_15 source) {
		super(source);		
	}
	
	public int getStatusFlag() {
		return ((OnlineInfoResp__1_15)source).getStatusFlag();
	}
	
	public int getStatusMode() {
		return ((OnlineInfoResp__1_15)source).getStatusMode();
	}	
}