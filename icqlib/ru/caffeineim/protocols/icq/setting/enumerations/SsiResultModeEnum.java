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
public class SsiResultModeEnum {

	public static final int NO_ERRORS = 0x00;
	public static final int NOT_FOUND_IN_LIST = 0x02;  
	public static final int ALLREADY_EXISTS = 0x03;
	public static final int ERROR_ADDING_ITEM = 0x0A;
	public static final int LIMIT_OF_ITEMS_EXCEEDED = 0x0C;
	public static final int TRYING_ADD_ICQ_TO_AIM = 0x0D;
	public static final int CANT_ADD_BECAUSE_REQUEST_AUTH = 0x0E;
		
	private int result;

	public SsiResultModeEnum(int result) {
		this.result = result;
	}

	public int getResult() {
		return this.result;
	}

	public String toString() {
		String ret = "";
		if (result == NO_ERRORS) {
			ret = "No errors (success)";
		}
		if (result == NOT_FOUND_IN_LIST) {
			ret = "Item you want to modify not found in list";
		}
		if (result == ALLREADY_EXISTS) {
			ret = "Item you want to add allready exists";
		}
		if (result == ERROR_ADDING_ITEM) {
			ret = "Error adding item (invalid id, allready in list, invalid data)";
		}
		if (result  == LIMIT_OF_ITEMS_EXCEEDED) {
			ret = "Can't add item. Limit for this type of items exceeded";
		}
		if (result  == TRYING_ADD_ICQ_TO_AIM) {
			ret = "Trying to add ICQ contact to an AIM list";
		}
		if (result == CANT_ADD_BECAUSE_REQUEST_AUTH) {
			ret = "Can't add this contact because it requires authorization";
		}
		return ret;
	}
}
