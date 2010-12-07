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
package ru.caffeineim.protocols.icq.request.event;

import java.util.EventObject;

import ru.caffeineim.protocols.icq.Flap;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class RequestAnswerEvent extends EventObject {
	
    private static final long serialVersionUID = 1877859126220384940L;
    
    private Flap request;

	/**
	 * Create a RequestAnswerEvent by defining the requesting packet and
	 * the reply packet that has been received.
	 *
	 * @param requestPacket The requesting packet.
	 * @param requestAnswerPacket The reply packet.
	 */
	public RequestAnswerEvent(Flap requestPacket, Flap requestAnswerPacket) {
		super(requestAnswerPacket);
		this.request = requestPacket;
	}

	/**
	 * Returns the packet reply to the request.
	 *
	 * @return The reply packet.
	 */
	public Flap getRequestAnswerPacket() {
		return ( (Flap) getSource());
	}

	/**
	 * Returns the requesting packet.
	 *
	 * @return The requesting packet
	 */
	public Flap getRequestPacket() {
		return request;
	}
}