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
public class StatusFlagEnum {

	public static final int DIRECT_CONNECTION_ALLOWED = 0x00;
	public static final int DIRECT_CONNECTION_AUTHORIZATION = 0x10000000;
	public static final int DIRECT_CONNECTION_CONTACT = 0x20000010;
	public static final int WEBAWARE = 0x00010000;
	public static final int SHOW_IP = 0x00020000;
	public static final int BIRTHDAY = 0x00080000;

	private int flag;

	public StatusFlagEnum(int flag, boolean showIp, boolean webAware, boolean birthday) {
		this.flag = flag;
		if (showIp) {
			this.flag |= SHOW_IP;
		}
		if (webAware) {
			this.flag |= WEBAWARE;
		}
		if (birthday) {
			this.flag |= BIRTHDAY;
		}
	}

	public StatusFlagEnum(int flag) {
		this.flag = flag;
	}

	public int getFlag() {
		return flag;
	}

	public String toString() {
		String ret = "";
		
		if (flag < 0x130000) {
			ret = "Direct connection allowed";
		}
		else if ( (flag & DIRECT_CONNECTION_AUTHORIZATION) ==
			DIRECT_CONNECTION_AUTHORIZATION) {
			ret = "Direct connection authorization";
		}
		else if ( (flag & DIRECT_CONNECTION_CONTACT) == DIRECT_CONNECTION_CONTACT) {
			ret = "Direct connection contact";
		}
		if ( (flag & SHOW_IP) == SHOW_IP) {
			ret += " show ip";
		}
		if ( (flag & WEBAWARE) == WEBAWARE) {
			ret += " webaware";
		}
		if ( (flag & BIRTHDAY) == BIRTHDAY) {
			ret += " birthday";
		}
		
		return ret;
	}
}