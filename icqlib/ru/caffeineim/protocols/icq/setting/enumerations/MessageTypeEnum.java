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
 *   @author Samolisov Pavel
 */
public class MessageTypeEnum {

	public static final int PLAIN_MESSAGE = 0x01;
	public static final int CHAT_REQUEST = 0x02;
	public static final int FILE_REQUEST = 0x03;
	public static final int URL = 0x04;
	public static final int UNKNOWN = 0x05;
	public static final int AUTHORIZATION_REQUEST = 0x06;
	public static final int AUTHORIZATION_REJECTED = 0x07;
	public static final int AUTHORIZATION_ACCEPTED = 0x08;
	public static final int XSTATUS_MESSAGE = 0x1A;
	public static final int USER_ADDED_YOU = 0x0C;
	public static final int WEB_PAGER_MESSAGE = 0x0D;
	public static final int EMAIL_EXPRESS_MESSAGE = 0x0E;
	public static final int CONTACT_LIST = 0x13;
	public static final int REQUEST_AWAY_MESSAGE = 0xE8;
	public static final int REQUEST_OCCUPIED_MESSAGE = 0xE9;
	public static final int REQUEST_NA_MESSAGE = 0xEA;
	public static final int REQUEST_DND_MESSAGE = 0xEB;
	public static final int REQUEST_FREE_FOR_CHAT_MESSAGE = 0xEC;

	private int type;

	public MessageTypeEnum(int type) {
		this.type = type;
	}

	public int getType() {
		return type;
	}

	public String toString() {
		String ret = "";
		switch (type) {
			case PLAIN_MESSAGE:
				ret = "Plain message";
				break;
			case CHAT_REQUEST:
				ret = "Chat request";
				break;
			case FILE_REQUEST:
				ret = "File request";
				break;
			case URL:
				ret = "Url";
				break;
			case UNKNOWN:
				ret = "Unknown";
				break;
			case AUTHORIZATION_REQUEST:
				ret = "Authorization request";
				break;
			case XSTATUS_MESSAGE:
				ret = "XStatus message";
				break;
			case AUTHORIZATION_REJECTED:
				ret = "Authorization rejected";
				break;
			case AUTHORIZATION_ACCEPTED:
				ret = "Authorization accepted";
				break;
			case USER_ADDED_YOU:
				ret = "User Added you";
				break;
			case WEB_PAGER_MESSAGE:
				ret = "Web pager message";
				break;
			case EMAIL_EXPRESS_MESSAGE:
				ret = "Email express message";
				break;
			case CONTACT_LIST:
				ret = "Contact list";
				break;
			case REQUEST_AWAY_MESSAGE:
				ret = "Request away message";
				break;
			case REQUEST_OCCUPIED_MESSAGE:
				ret = "Request occupied message";
				break;
			case REQUEST_NA_MESSAGE:
				ret = "Request NA message";
				break;
			case REQUEST_DND_MESSAGE:
				ret = "Request DND message";
				break;
			case REQUEST_FREE_FOR_CHAT_MESSAGE:
				ret = "Request free for chat message";
				break;
		}
		
		return ret;
	}
}