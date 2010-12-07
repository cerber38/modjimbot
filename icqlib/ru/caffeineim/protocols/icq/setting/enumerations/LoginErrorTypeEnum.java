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
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class LoginErrorTypeEnum {

	public static final int UNKNOWN_ERROR           = 0;
	public static final int BAD_UIN_ERROR           = 1;
	public static final int PASSWORD_ERROR          = 2;
	public static final int NOT_EXISTS_ERROR        = 3;
	public static final int LIMIT_EXCEEDED_ERROR    = 4;
	public static final int MAXIMUM_USERS_IP_ERROR  = 5;
	public static final int OLDER_ICQ_VERSION_ERROR = 6;
	public static final int CANT_REGISTER_ERROR     = 7;
	 
	private int error;

	public LoginErrorTypeEnum(int error) {
		this.error = error;
	}

	public int getError() {
		return this.error;
	}

	public String toString() {
		String ret = "";
		switch (error) {
			case UNKNOWN_ERROR:
				ret = "Unknown Error";
				break;
			case BAD_UIN_ERROR:
				ret = "Bad UIN.";
				break;
			case PASSWORD_ERROR:
				ret = "Password incorrect.";
				break;
			case NOT_EXISTS_ERROR:
				ret = "This ICQ number does not exist.";
				break;
			case MAXIMUM_USERS_IP_ERROR:
				ret = "The amount of users connected from this IP has reached the maximum.";
				break;
			case LIMIT_EXCEEDED_ERROR:
				ret = "Rate limit exceeded. Please try to reconnect in a few minutes.";
				break;
			case OLDER_ICQ_VERSION_ERROR:
				ret = "You are using an older version of ICQ. Please upgrade.";
				break;
			case CANT_REGISTER_ERROR:
				ret = "Can't register on the ICQ network. Reconnect in a few minutes.";
				break;
		}
		
		return ret;
	}
}
