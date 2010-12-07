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
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class MetaTypeEnum {

	public static final int CLIENT_REQUEST_OFFLINE_MESSAGES = 0x003C;
	public static final int CLIENT_ACK_OFFLINE_MESSAGES = 0x003E;
	public static final int CLIENT_ADVANCED_META = 0x07D0;
	public static final int SERVER_OFFLINE_MESSAGE = 0x0041;
	public static final int SERVER_END_OF_OFFLINE_MESSAGES = 0x0042;
	public static final int SERVER_ADVANCED_META = 0x07DA;

	private int type;

	public MetaTypeEnum(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public String toString() {
		String ret = "";
		switch (type) {
			case CLIENT_REQUEST_OFFLINE_MESSAGES:
				ret = "Client request offline messages";
				break;
			case CLIENT_ACK_OFFLINE_MESSAGES:
				ret = "Client ack offline messages";
				break;
			case CLIENT_ADVANCED_META:
				ret = "Client advanced meta";
				break;
			case SERVER_OFFLINE_MESSAGE:
				ret = "Server offline message";
				break;
			case SERVER_END_OF_OFFLINE_MESSAGES:
				ret = "Server end of offline messages";
				break;
			case SERVER_ADVANCED_META:
				ret = "Server advanced meta";
				break;
		}
		
		return ret;
	}
}