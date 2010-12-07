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
public class MessageFlagsEnum {

	public static final int NORMAL_MESSAGE = 0;
	public static final int MULTIPLE_RECIPIENT = 80;
	public static final int AUTO_REPLY_MESSAGE_REQUEST = 3;

	private int flag;

	public MessageFlagsEnum(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public String toString() {
		String ret = "";
		switch (flag) {
			case NORMAL_MESSAGE:
				ret = "Normal message";
				break;
			case MULTIPLE_RECIPIENT:
				ret = "Multiple recipient";
				break;
			case AUTO_REPLY_MESSAGE_REQUEST:
				ret = "Auto reply message request";
				break;
		}
		
		return ret;
	}
}