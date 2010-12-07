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
public class TcpConnectionFlagEnum {

	public static final int FIREWALL = 0x01;
	public static final int SOCKS = 0x02;
	public static final int NORMAL = 0x04;
	public static final int ICQ2GO = 0x06;

	private int tcpConnectionFlag;

	public TcpConnectionFlagEnum(int tcpConnectionFlag) {
		this.tcpConnectionFlag = tcpConnectionFlag;
	}

	public int getTcpConnectionFlag() {
		return tcpConnectionFlag;
	}

	public String toString() {
		String ret = "";
		switch (tcpConnectionFlag) {
			case FIREWALL:
				ret = "Firewall";
				break;
			case SOCKS:
				ret = "Socks";
				break;
			case NORMAL:
				ret = "Normal";
				break;
			case ICQ2GO:
				ret = "ICQ2Go";
				break;
		}
		
		return ret;
	}
}
