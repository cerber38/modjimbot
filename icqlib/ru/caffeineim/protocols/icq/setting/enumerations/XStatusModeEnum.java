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
public class XStatusModeEnum {
	
	public static final int NONE = 0;
	public static final int ANGRY = 1;
	public static final int TAKING_A_BATH = 2;
	public static final int TIRED = 3;
	public static final int PARTY = 4;
	public static final int DRINKING_BEER = 5;
	public static final int THINKING = 6;
	public static final int EATING = 7;
	public static final int WATCHING_TV = 8;
	public static final int MEETING = 9;
	public static final int COFFEE = 10;
	public static final int LISTENING_TO_MUSIC = 11;
	public static final int BUSINESS = 12;
	public static final int SHOOTING = 13;
	public static final int HAVING_FUN = 14;
	public static final int ON_THE_PHONE = 15;
	public static final int GAMING = 16;
	public static final int STUDYING = 17;
	public static final int SHOPPING = 18;
	public static final int FEELING_SICK = 19;
	public static final int SLEEPING = 20;
	public static final int SURFING = 21;
	public static final int BROWSING = 22;
	public static final int WORKING = 23;
	public static final int TYPING = 24;
	public static final int PICNIC = 25;
	public static final int COOKING = 26;
	public static final int SMOKING = 27;
	public static final int I_HIGH = 28;
	public static final int ON_WC = 29;
	public static final int QUESTION = 30;
	public static final int WATCHING_PRO7 = 31;
	public static final int LOVE = 32;
	public static final int GOOGLE = 33;
	public static final int NOTEPAD = 34;
        /**
           35 - Секс;
           36 - RuLove;
           37 - Перекур;
         */
        public static final int SEX = 35;
        public static final int RULOVE = 36;
        public static final int SMOUKE = 37;

	/**
	 * All XStatuses array
	 */
	public static final XStatusModeEnum[] AVAILABLE_X_STATUSES = new XStatusModeEnum[] {
			new XStatusModeEnum(XStatusModeEnum.NONE),
			new XStatusModeEnum(XStatusModeEnum.ANGRY),
			new XStatusModeEnum(XStatusModeEnum.TAKING_A_BATH),
			new XStatusModeEnum(XStatusModeEnum.TIRED),
			new XStatusModeEnum(XStatusModeEnum.PARTY),
			new XStatusModeEnum(XStatusModeEnum.DRINKING_BEER),
			new XStatusModeEnum(XStatusModeEnum.THINKING),
			new XStatusModeEnum(XStatusModeEnum.EATING),
			new XStatusModeEnum(XStatusModeEnum.WATCHING_TV),
			new XStatusModeEnum(XStatusModeEnum.MEETING),
			new XStatusModeEnum(XStatusModeEnum.COFFEE),
			new XStatusModeEnum(XStatusModeEnum.LISTENING_TO_MUSIC),
			new XStatusModeEnum(XStatusModeEnum.BUSINESS),
			new XStatusModeEnum(XStatusModeEnum.SHOOTING),
			new XStatusModeEnum(XStatusModeEnum.HAVING_FUN),
			new XStatusModeEnum(XStatusModeEnum.ON_THE_PHONE),
			new XStatusModeEnum(XStatusModeEnum.GAMING),
			new XStatusModeEnum(XStatusModeEnum.STUDYING),
			new XStatusModeEnum(XStatusModeEnum.SHOPPING),
			new XStatusModeEnum(XStatusModeEnum.FEELING_SICK),
			new XStatusModeEnum(XStatusModeEnum.SLEEPING),
			new XStatusModeEnum(XStatusModeEnum.SURFING),
			new XStatusModeEnum(XStatusModeEnum.BROWSING),
			new XStatusModeEnum(XStatusModeEnum.WORKING),
			new XStatusModeEnum(XStatusModeEnum.TYPING),
			new XStatusModeEnum(XStatusModeEnum.PICNIC),
			new XStatusModeEnum(XStatusModeEnum.COOKING),
			new XStatusModeEnum(XStatusModeEnum.SMOKING),
			new XStatusModeEnum(XStatusModeEnum.I_HIGH),
			new XStatusModeEnum(XStatusModeEnum.ON_WC),
			new XStatusModeEnum(XStatusModeEnum.QUESTION),
			new XStatusModeEnum(XStatusModeEnum.WATCHING_PRO7),
			new XStatusModeEnum(XStatusModeEnum.LOVE),
			new XStatusModeEnum(XStatusModeEnum.GOOGLE),
			new XStatusModeEnum(XStatusModeEnum.NOTEPAD),
                        new XStatusModeEnum(XStatusModeEnum.SEX),
                        new XStatusModeEnum(XStatusModeEnum.RULOVE),
                        new XStatusModeEnum(XStatusModeEnum.SMOUKE)};

	private byte[][] statusMatrix = {
			{ 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
					0x00, 0x00, 0x00, 0x00, 0x00, 0x00 },
			{ 0x01, (byte) 0xD8, (byte) 0xD7, (byte) 0xEE, (byte) 0xAC, 0x3B,
					0x49, 0x2A, (byte) 0xA5, (byte) 0x8D, (byte) 0xD3,
					(byte) 0xD8, 0x77, (byte) 0xE6, 0x6B, (byte) 0x92 },
			{ 0x5A, 0x58, 0x1E, (byte) 0xA1, (byte) 0xE5, (byte) 0x80, 0x43,
					0x0C, (byte) 0xA0, 0x6F, 0x61, 0x22, (byte) 0x98,
					(byte) 0xB7, (byte) 0xE4, (byte) 0xC7 },
			{ (byte) 0x83, (byte) 0xC9, (byte) 0xB7, (byte) 0x8E, 0x77,
					(byte) 0xE7, 0x43, 0x78, (byte) 0xB2, (byte) 0xC5,
					(byte) 0xFB, 0x6C, (byte) 0xFC, (byte) 0xC3, 0x5B,
					(byte) 0xEC },
			{ (byte) 0xE6, 0x01, (byte) 0xE4, 0x1C, 0x33, 0x73, 0x4B,
					(byte) 0xD1, (byte) 0xBC, 0x06, (byte) 0x81, 0x1D, 0x6C,
					0x32, 0x3D, (byte) 0x81 },
			{ (byte) 0x8C, 0x50, (byte) 0xDB, (byte) 0xAE, (byte) 0x81,
					(byte) 0xED, 0x47, (byte) 0x86, (byte) 0xAC, (byte) 0xCA,
					0x16, (byte) 0xCC, 0x32, 0x13, (byte) 0xC7, (byte) 0xB7 },
			{ 0x3F, (byte) 0xB0, (byte) 0xBD, 0x36, (byte) 0xAF, 0x3B, 0x4A,
					0x60, (byte) 0x9E, (byte) 0xEF, (byte) 0xCF, 0x19, 0x0F,
					0x6A, 0x5A, 0x7F },
			{ (byte) 0xF8, (byte) 0xE8, (byte) 0xD7, (byte) 0xB2, (byte) 0x82,
					(byte) 0xC4, 0x41, 0x42, (byte) 0x90, (byte) 0xF8, 0x10,
					(byte) 0xC6, (byte) 0xCE, 0x0A, (byte) 0x89, (byte) 0xA6 },
			{ (byte) 0x80, 0x53, 0x7D, (byte) 0xE2, (byte) 0xA4, 0x67, 0x4A,
					0x76, (byte) 0xB3, 0x54, 0x6D, (byte) 0xFD, 0x07, 0x5F,
					0x5E, (byte) 0xC6 },
			{ (byte) 0xF1, (byte) 0x8A, (byte) 0xB5, 0x2E, (byte) 0xDC, 0x57,
					0x49, 0x1D, (byte) 0x99, (byte) 0xDC, 0x64, 0x44, 0x50,
					0x24, 0x57, (byte) 0xAF },
			{ 0x1B, 0x78, (byte) 0xAE, 0x31, (byte) 0xFA, 0x0B, 0x4D, 0x38,
					(byte) 0x93, (byte) 0xD1, (byte) 0x99, 0x7E, (byte) 0xEE,
					(byte) 0xAF, (byte) 0xB2, 0x18 },
			{ 0x61, (byte) 0xBE, (byte) 0xE0, (byte) 0xDD, (byte) 0x8B,
					(byte) 0xDD, 0x47, 0x5D, (byte) 0x8D, (byte) 0xEE, 0x5F,
					0x4B, (byte) 0xAA, (byte) 0xCF, 0x19, (byte) 0xA7 },
			{ 0x48, (byte) 0x8E, 0x14, (byte) 0x89, (byte) 0x8A, (byte) 0xCA,
					0x4A, 0x08, (byte) 0x82, (byte) 0xAA, 0x77, (byte) 0xCE,
					0x7A, 0x16, 0x52, 0x08 },
			{ 0x10, 0x7A, (byte) 0x9A, 0x18, 0x12, 0x32, 0x4D, (byte) 0xA4,
					(byte) 0xB6, (byte) 0xCD, 0x08, 0x79, (byte) 0xDB, 0x78,
					0x0F, 0x09 },
			{ 0x6F, 0x49, 0x30, (byte) 0x98, 0x4F, 0x7C, 0x4A, (byte) 0xFF,
					(byte) 0xA2, 0x76, 0x34, (byte) 0xA0, 0x3B, (byte) 0xCE,
					(byte) 0xAE, (byte) 0xA7 },
			{ 0x12, (byte) 0x92, (byte) 0xE5, 0x50, 0x1B, 0x64, 0x4F, 0x66,
					(byte) 0xB2, 0x06, (byte) 0xB2, (byte) 0x9A, (byte) 0xF3,
					0x78, (byte) 0xE4, (byte) 0x8D },
			{ (byte) 0xD4, (byte) 0xA6, 0x11, (byte) 0xD0, (byte) 0x8F, 0x01,
					0x4E, (byte) 0xC0, (byte) 0x92, 0x23, (byte) 0xC5,
					(byte) 0xB6, (byte) 0xBE, (byte) 0xC6, (byte) 0xCC,
					(byte) 0xF0 },
			{ 0x60, (byte) 0x9D, 0x52, (byte) 0xF8, (byte) 0xA2, (byte) 0x9A,
					0x49, (byte) 0xA6, (byte) 0xB2, (byte) 0xA0, 0x25, 0x24,
					(byte) 0xC5, (byte) 0xE9, (byte) 0xD2, 0x60 },
			{ 0x63, 0x62, 0x73, 0x37, (byte) 0xA0, 0x3F, 0x49, (byte) 0xFF,
					(byte) 0x80, (byte) 0xE5, (byte) 0xF7, 0x09, (byte) 0xCD,
					(byte) 0xE0, (byte) 0xA4, (byte) 0xEE },
			{ 0x1F, 0x7A, 0x40, 0x71, (byte) 0xBF, 0x3B, 0x4E, 0x60,
					(byte) 0xBC, 0x32, 0x4C, 0x57, (byte) 0x87, (byte) 0xB0,
					0x4C, (byte) 0xF1 },
			{ 0x78, 0x5E, (byte) 0x8C, 0x48, 0x40, (byte) 0xD3, 0x4C, 0x65,
					(byte) 0x88, 0x6F, 0x04, (byte) 0xCF, 0x3F, 0x3F, 0x43,
					(byte) 0xDF },
			{ (byte) 0xA6, (byte) 0xED, 0x55, 0x7E, 0x6B, (byte) 0xF7, 0x44,
					(byte) 0xD4, (byte) 0xA5, (byte) 0xD4, (byte) 0xD2,
					(byte) 0xE7, (byte) 0xD9, 0x5C, (byte) 0xE8, 0x1F },
			{ 0x12, (byte) 0xD0, 0x7E, 0x3E, (byte) 0xF8, (byte) 0x85, 0x48,
					(byte) 0x9E, (byte) 0x8E, (byte) 0x97, (byte) 0xA7, 0x2A,
					0x65, 0x51, (byte) 0xE5, (byte) 0x8D },
			{ (byte) 0xBA, 0x74, (byte) 0xDB, 0x3E, (byte) 0x9E, 0x24, 0x43,
					0x4B, (byte) 0x87, (byte) 0xB6, 0x2F, 0x6B, (byte) 0x8D,
					(byte) 0xFE, (byte) 0xE5, 0x0F },
			{ 0x63, 0x4F, 0x6B, (byte) 0xD8, (byte) 0xAD, (byte) 0xD2, 0x4A,
					(byte) 0xA1, (byte) 0xAA, (byte) 0xB9, 0x11, 0x5B,
					(byte) 0xC2, 0x6D, 0x05, (byte) 0xA1 },
			{ 0x2C, (byte) 0xE0, (byte) 0xE4, (byte) 0xE5, 0x7C, 0x64, 0x43,
					0x70, (byte) 0x9C, 0x3A, 0x7A, 0x1C, (byte) 0xE8, 0x78,
					(byte) 0xA7, (byte) 0xDC },
			{ 0x10, 0x11, 0x17, (byte) 0xC9, (byte) 0xA3, (byte) 0xB0, 0x40,
					(byte) 0xF9, (byte) 0x81, (byte) 0xAC, 0x49, (byte) 0xE1,
					0x59, (byte) 0xFB, (byte) 0xD5, (byte) 0xD4 },
			{ 0x16, 0x0C, 0x60, (byte) 0xBB, (byte) 0xDD, 0x44, 0x43,
					(byte) 0xF3, (byte) 0x91, 0x40, 0x05, 0x0F, 0x00,
					(byte) 0xE6, (byte) 0xC0, 0x09 },
			{ 0x64, 0x43, (byte) 0xC6, (byte) 0xAF, 0x22, 0x60, 0x45, 0x17,
					(byte) 0xB5, (byte) 0x8C, (byte) 0xD7, (byte) 0xDF,
					(byte) 0x8E, 0x29, 0x03, 0x52 },
			{ 0x16, (byte) 0xF5, (byte) 0xB7, 0x6F, (byte) 0xA9, (byte) 0xD2,
					0x40, 0x35, (byte) 0x8C, (byte) 0xC5, (byte) 0xC0,
					(byte) 0x84, 0x70, 0x3C, (byte) 0x98, (byte) 0xFA },
			{ 0x63, 0x14, 0x36, (byte) 0xff, 0x3f, (byte) 0x8a, 0x40,
					(byte) 0xd0, (byte) 0xa5, (byte) 0xcb, 0x7b, 0x66,
					(byte) 0xe0, 0x51, (byte) 0xb3, 0x64 },
			{ (byte) 0xb7, 0x08, 0x67, (byte) 0xf5, 0x38, 0x25, 0x43, 0x27,
					(byte) 0xa1, (byte) 0xff, (byte) 0xcf, 0x4c, (byte) 0xc1,
					(byte) 0x93, (byte) 0x97, (byte) 0x97 },
			{ (byte) 0xdd, (byte) 0xcf, 0x0e, (byte) 0xa9, 0x71, (byte) 0x95,
					0x40, 0x48, (byte) 0xa9, (byte) 0xc6, 0x41, 0x32, 0x06,
					(byte) 0xd6, (byte) 0xf2, (byte) 0x80 },
			{ (byte) 0xD4, (byte) 0xE2, (byte) 0xB0, (byte) 0xBA, 0x33, 0x4E,
					0x4F, (byte) 0xA5, (byte) 0x98, (byte) 0xD0, 0x11, 0x7D,
					(byte) 0xBF, 0x4D, 0x3C, (byte) 0xC8 },
			{ 0x00, 0x72, (byte) 0xD9, 0x08, 0x4A, (byte) 0xD1, 0x43,
					(byte) 0xDD, (byte) 0x91, (byte) 0x99, 0x6F, 0x02, 0x69,
					0x66, 0x02, 0x6F },
                        { (byte) 0xE6, 0x01, (byte) 0xE4, 0x1C, 0x33, 0x73, 0x4B,
                                  (byte) 0xD1, (byte) 0xBC, 0x06, (byte) 0x81, 0x1D,
                                  0x6C, 0x32, 0x3D, (byte) 0x82 },
                        { (byte) 0xCD, 0x56, 0x43, (byte) 0xA2, (byte) 0xC9, 0x4C,
                                 0x47, 0x24, (byte) 0xB5, 0x2C, (byte) 0xDC,
                                 0x01, 0x24, (byte) 0xA1, (byte) 0xD0, (byte) 0xCD },
                        {(byte)0x3F, (byte)0xB0, (byte)0xBD, (byte)0x36, (byte)0xAF, (byte)0x3B,
                                 (byte)0x4A, (byte)0x60, (byte)0x9E, (byte)0xEF, (byte)0xCF,
                                 (byte)0x19, (byte)0x0F, (byte)0x6A, (byte)0x5A, (byte)0x7E}};


	private int status;

	public static Object[] AVAILABLE_STATUSES;

	/**
	 * Create new XStatusModeEnum Instance
	 * 
	 * @param status
	 */
	public XStatusModeEnum(int status) {
		this.status = status;
	}


	/**
	 * 
	 * @return current XStatus
	 */
	public int getXStatus() {
		return status;
	}

	/**	 
	 * 
	 * @return current XStatus bytes
	 */
	public byte[] getXStatusData() {
		return statusMatrix[status];
	}

	public String toString() {
		String ret = "";
		if ((status & NONE) == NONE) {
			ret = "NONE";
		}
		if ((status & ANGRY) == ANGRY) {
			ret = "ANGRY";
		}
		if ((status & TAKING_A_BATH) == TAKING_A_BATH) {
			ret = "TAKING_A_BATH";
		}
		if ((status & TIRED) == TIRED) {
			ret = "TIRED";
		}
		if ((status & PARTY) == PARTY) {
			ret = "PARTY";
		}
		if ((status & DRINKING_BEER) == DRINKING_BEER) {
			ret = "DRINKING_BEER";
		}
		if ((status & THINKING) == THINKING) {
			ret = "THINKING";
		}
		if ((status & EATING) == EATING) {
			ret = "EATING";
		}
		if ((status & WATCHING_TV) == WATCHING_TV) {
			ret = "WATCHING_TV";
		}
		if ((status & MEETING) == MEETING) {
			ret = "MEETING";
		}
		if ((status & COFFEE) == COFFEE) {
			ret = "COFFEE";
		}
		if ((status & LISTENING_TO_MUSIC) == LISTENING_TO_MUSIC) {
			ret = "LISTENING_TO_MUSIC";
		}
		if ((status & BUSINESS) == BUSINESS) {
			ret = "BUSINESS";
		}
		if ((status & SHOOTING) == SHOOTING) {
			ret = "SHOOTING";
		}
		if ((status & HAVING_FUN) == HAVING_FUN) {
			ret = "HAVING_FUN";
		}
		if ((status & ON_THE_PHONE) == ON_THE_PHONE) {
			ret = "ON_THE_PHONE";
		}
		if ((status & GAMING) == GAMING) {
			ret = "GAMING";
		}
		if ((status & STUDYING) == STUDYING) {
			ret = "STUDYING";
		}
		if ((status & FEELING_SICK) == FEELING_SICK) {
			ret = "FEELING_SICK";
		}
		if ((status & SLEEPING) == SLEEPING) {
			ret = "SLEEPING";
		}
		if ((status & SURFING) == SURFING) {
			ret = "SURFING";
		}
		if ((status & BROWSING) == BROWSING) {
			ret = "BROWSING";
		}
		if ((status & WORKING) == WORKING) {
			ret = "WORKING";
		}
		if ((status & TYPING) == TYPING) {
			ret = "TYPING";
		}
		if ((status & PICNIC) == PICNIC) {
			ret = "PICNIC";
		}
		if ((status & COOKING) == COOKING) {
			ret = "COOKING";
		}
		if ((status & SMOKING) == SMOKING) {
			ret = "SMOKING";
		}
		if ((status & I_HIGH) == I_HIGH) {
			ret = "I_HIGH";
		}
		if ((status & ON_WC) == ON_WC) {
			ret = "ON_WC";
		}
		if ((status & QUESTION) == QUESTION) {
			ret = "QUESTION";
		}
		if ((status & WATCHING_PRO7) == WATCHING_PRO7) {
			ret = "WATCHING_PRO7";
		}
		if ((status & LOVE) == LOVE) {
			ret = "LOVE";
		}
		if ((status & NOTEPAD) == NOTEPAD) {
			ret = "NOTEPAD";
		}
		if ((status & GOOGLE) == GOOGLE) {
			ret = "GOOGLE";
		}
		
		if ((status & SEX) == SEX) {
			ret = "SEX";
		}

		if ((status & RULOVE) == RULOVE) {
			ret = "RULOVE";
		}
                
		if ((status & SMOUKE) == SMOUKE) {
			ret = "SMOUKE";
		}
                return ret;
	}
}
