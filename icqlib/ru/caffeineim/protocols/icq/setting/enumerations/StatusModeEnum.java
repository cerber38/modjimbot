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
public class StatusModeEnum {

	public static final int ONLINE = 0x00;
	public static final int AWAY = 0x01;  
	public static final int DND = 0x02;
	public static final int NA = 0x04;
	public static final int OCCUPIED = 0x10;
	public static final int FREE_FOR_CHAT = 0x20;  
	public static final int OFFLINE = 0xFF;
	public static final int INVISIBLE = 0x100;
	// Custom user-statuses
	public static final int LUNCH = 0x2001;
	public static final int EVIL = 0x3000;
	public static final int DEPRESSION = 0x4000;
	public static final int HOME = 0x5000;
	public static final int WORK = 0x6000;

        private boolean isWebAware;

	/** This array of available statuses may be useful for GUI components like JComboBox. */
	/* -- Thanks to Felix Berger of Xnap project -- */
	public static final StatusModeEnum[] AVAILABLE_STATUSES = new StatusModeEnum[] {
		new StatusModeEnum(StatusModeEnum.ONLINE),
		new StatusModeEnum(StatusModeEnum.LUNCH),
		new StatusModeEnum(StatusModeEnum.EVIL),
		new StatusModeEnum(StatusModeEnum.DEPRESSION),
		new StatusModeEnum(StatusModeEnum.HOME),
		new StatusModeEnum(StatusModeEnum.WORK),
		new StatusModeEnum(StatusModeEnum.AWAY),
		new StatusModeEnum(StatusModeEnum.NA),
		new StatusModeEnum(StatusModeEnum.OCCUPIED),
		new StatusModeEnum(StatusModeEnum.DND),
		new StatusModeEnum(StatusModeEnum.FREE_FOR_CHAT),
		new StatusModeEnum(StatusModeEnum.INVISIBLE),
		new StatusModeEnum(StatusModeEnum.OFFLINE)
	};

	private int status;

	public StatusModeEnum(int status, boolean isWebAware) {
		this.status = status;
                this.isWebAware = isWebAware;
	}

	public StatusModeEnum(int status) {
		this.status = status;
	}

	public int getMode() {
		return this.status;
	}

        public boolean getWebAware(){
               return isWebAware;
        }

	public String toString() {
		String ret = "";
		if ( (status & ONLINE) == ONLINE) {
			ret = "Online";
		}
		if ( (status & AWAY) == AWAY) {
			ret = "Away";
		}
		if ( (status & NA) == NA) {
			ret = "NA";
		}
		if ( (status & OCCUPIED) == OCCUPIED) {
			ret = "Occupied";
		}
		if ( (status & DND) == DND) {
			ret = "DND";
		}
		if ( (status & FREE_FOR_CHAT) == FREE_FOR_CHAT) {
			ret = "Free for chat";
		}
		if ( (status & OFFLINE) == OFFLINE) {
			ret = "Offline";
		}
		if ( (status & INVISIBLE) == INVISIBLE) {
			ret = "Invisible";
		}
		if ( (status & LUNCH) == LUNCH) {
			ret = "Lunch";
		}
		if ( (status & EVIL) == EVIL) {
			ret = "Evil";
		}
		if ( (status & DEPRESSION) == DEPRESSION) {
			ret = "Depression";
		}
		if ( (status & HOME) == HOME) {
			ret = "@Home";
		}
		if ( (status & WORK) == WORK) {
			ret = "@Work";
		}
		
		return ret;
	}
}
