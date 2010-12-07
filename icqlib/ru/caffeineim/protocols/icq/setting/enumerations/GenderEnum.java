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
public class GenderEnum {

	public static final int UNKNOWN = 0x00;
	public static final int MALE = 0x02;
	public static final int FEMALE = 0x01;

	private int gender;

	public GenderEnum(int gender) {
		this.gender = gender;
	}

	public int getGender() {
		return gender;
	}

	public String toString() {
		String ret = "";
		switch (gender) {
			case UNKNOWN:
				ret = "UNKNOWN";
				break;
			case MALE:
				ret = "Male";
				break;
			case FEMALE:
				ret = "Female";
				break;
		}
		
		return ret;
	}
}