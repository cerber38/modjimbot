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
public class IdleTimeEnum {

	public static final int START_IDLE_TIME = 0x3C;
	public static final int STOP_IDLE_TIME = 0x00;

	private int idleTimeMode;

	public IdleTimeEnum(int idleTimeMode) {
		this.idleTimeMode = idleTimeMode;
	}

	public int getIdleTimeMode() {
		return idleTimeMode;
	}

	public String toString() {
		String ret = "";
		switch (idleTimeMode) {
			case START_IDLE_TIME:
				ret = "Idle time started";
				break;
			case STOP_IDLE_TIME:
				ret = "Idle time stopped";
				break;
		}
		
		return ret;
	}
}