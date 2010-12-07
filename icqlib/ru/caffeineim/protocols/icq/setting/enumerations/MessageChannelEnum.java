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
public class MessageChannelEnum {

	public static final int MESSAGE_CHANNEL_1 = 1;
	public static final int MESSAGE_CHANNEL_2 = 2;
	public static final int MESSAGE_CHANNEL_4 = 4;

	private int channelNumber;

	public MessageChannelEnum(int channelNumber) {
		this.channelNumber = channelNumber;
	}

	public int getChannelNumber() {
		return channelNumber;
	}

	public String toString() {
		String ret = "";
		switch (channelNumber) {
			case MESSAGE_CHANNEL_1:
				ret = "Channel 1";
				break;
			case MESSAGE_CHANNEL_2:
				ret = "Channel 2";
				break;
			case MESSAGE_CHANNEL_4:
				ret = "Channel 4";
				break;
		}
		
		return ret;
	}
}