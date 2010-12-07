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
package ru.caffeineim.protocols.icq.setting.enumerations;

/**
 * <p>Created by 23.03.2008
 *   @author Samolisov Pavel
 */
public class MessageMissedTypeEnum {
	
	public static final int MESSAGE_INVALID       = 0;
	public static final int MESSAGE_TOO_LARGE     = 1;
	public static final int MESSAGE_RATE_EXCEEDED = 2;
	public static final int SENDER_TOO_EVIL       = 3;
	public static final int USER_TOO_EVIL         = 4;
	public static final int UNKNOWN               = 5;
	
	private int error;

	public MessageMissedTypeEnum(int error) {
		this.error = error;
	}
	
	public int getError() {
		return error;
	}
	
	public String toString() {
		switch(error) {
			case MESSAGE_INVALID:
				return "message invalid";				
			case MESSAGE_TOO_LARGE:
				return "message too large";			
			case MESSAGE_RATE_EXCEEDED:
				return "message rate exceeded";
			case SENDER_TOO_EVIL:
				return "sender too evil";
			case USER_TOO_EVIL:
				return "you are too evil";
			default:
				return "unknown error: " + error;
		}	
	}
}