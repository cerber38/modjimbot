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
package ru.caffeineim.protocols.icq.setting;

import java.util.TreeMap;

import ru.caffeineim.protocols.icq.setting.enumerations.MessageTypeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusFlagEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.TcpConnectionFlagEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public class Tweaker {

	private static final Integer INITIAL_STATUS_MODE = new Integer(0x01FF);
	private static final Integer INITIAL_STATUS_FLAGS = new Integer(0x02FF);
	private static final Integer TCP_CONNECTION_FLAG = new Integer(0x03FF);
	private static final Integer P2P_PORT_LISTENING = new Integer(0x04FF);
	private static final Integer REQUEST_FREE_FOR_CHAT_MESSAGE = new Integer(
		  MessageTypeEnum.REQUEST_FREE_FOR_CHAT_MESSAGE);
	private static final Integer REQUEST_DND_MESSAGE = new Integer(
		  MessageTypeEnum.REQUEST_DND_MESSAGE);
	private static final Integer REQUEST_OCCUPIED_MESSAGE = new Integer(
		  MessageTypeEnum.REQUEST_OCCUPIED_MESSAGE);
	private static final Integer REQUEST_NA_MESSAGE = new Integer(
		  MessageTypeEnum.REQUEST_NA_MESSAGE);
	private static final Integer REQUEST_AWAY_MASSEGE = new Integer(
		  MessageTypeEnum.REQUEST_AWAY_MESSAGE);

	private TreeMap map;

	public Tweaker(boolean webAware, boolean birthday) {
		map = new TreeMap();
		map.put(INITIAL_STATUS_MODE, new StatusModeEnum(StatusModeEnum.ONLINE));
		map.put(INITIAL_STATUS_FLAGS,
				new StatusFlagEnum(StatusFlagEnum.DIRECT_CONNECTION_AUTHORIZATION, false, webAware, birthday));
		map.put(TCP_CONNECTION_FLAG,
				new TcpConnectionFlagEnum(TcpConnectionFlagEnum.NORMAL));
		map.put(P2P_PORT_LISTENING, new Integer(5000));
	}

	public StatusFlagEnum getInitialStatusFlags() {
		return (StatusFlagEnum) map.get(INITIAL_STATUS_FLAGS);
	}

	public void setInitialStatusFlags(StatusFlagEnum flags) {
		map.put(INITIAL_STATUS_FLAGS, flags);
	}

	public StatusModeEnum getInitialStatusMode() {
		return (StatusModeEnum) map.get(INITIAL_STATUS_MODE);
	}

	public void setInitialStatusMode(StatusModeEnum mode) {
		map.put(INITIAL_STATUS_MODE, mode);
	}

	public TcpConnectionFlagEnum getTcpConnectionFlag() {
		return (TcpConnectionFlagEnum) map.get(TCP_CONNECTION_FLAG);
	}

	public void setTcpConnectionFlag(TcpConnectionFlagEnum flag) {
		map.put(TCP_CONNECTION_FLAG, flag);
	}

	public int getP2PPortListening() {
		return ( (Integer) map.get(P2P_PORT_LISTENING)).intValue();
	}

	public void setP2PPortListening(int port) {
		map.put(P2P_PORT_LISTENING, new Integer(port));
	}

	public String getRequestMessage(int modeRequest) {
		String stored = (String) map.get(new Integer(modeRequest));
		if (stored != null) {
			return "" + stored;
		}
		else {
			return "";
		}
	}

	public void setFreeForChatRequestMessage(String message) {
		map.put(REQUEST_FREE_FOR_CHAT_MESSAGE, new Integer(message));
	}

	public void setDNDRequestMessage(String message) {
		map.put(REQUEST_DND_MESSAGE, new Integer(message));
	}

	public void setOccupiedRequestMessage(String message) {
		map.put(REQUEST_OCCUPIED_MESSAGE, new Integer(message));
	}

	public void setNARequestMessage(String message) {
		map.put(REQUEST_NA_MESSAGE, new Integer(message));
	}

	public void setAwayRequestMessage(String message) {
		map.put(REQUEST_AWAY_MASSEGE, new Integer(message));
	}
}