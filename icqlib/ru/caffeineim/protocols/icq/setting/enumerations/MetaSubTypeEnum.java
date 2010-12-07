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
public class MetaSubTypeEnum {

	public static final int SERVER_META_PROCESSING_ERROR     = 0x0001;
	public static final int SERVER_SET_USER_HOME_INFO        = 0x0064;
	public static final int SERVER_SET_USER_WORK_INFO        = 0x006E;
	public static final int SERVER_SET_USER_MORE_INFO        = 0x0078;
	public static final int SERVER_SET_USER_NOTES_INFO       = 0x0082;
	public static final int SERVER_SET_USER_EMAIL_INFO       = 0x0087;
	public static final int SERVER_SET_USER_INTERESTS_INFO   = 0x008C;
	public static final int SERVER_SET_USER_AFFILATIONS_INFO = 0x0096;
	public static final int SERVER_SET_USER_PERMISSIONS_INFO = 0x00A0;
	public static final int SERVER_SET_USER_PASSWORD         = 0x00AA;
	public static final int SERVER_UNREGISTER_ACCOUNT        = 0x00B4;
	public static final int SERVER_SET_USER_HOMEPAGE         = 0x00BE;
	public static final int SERVER_BASIC_USER_INFO_REPLY     = 0x00C8;
	public static final int SERVER_WORK_USER_INFO_REPLY      = 0x00D2;
	public static final int SERVER_MORE_USER_INFO_REPLY      = 0x00DC;
	public static final int SERVER_ABOUT_USER_INFO_REPLY     = 0x00E6;
	public static final int SERVER_EXTENDED_EMAIL_USER_INFO_REPLY = 0x00EB;
	public static final int SERVER_INTERESTS_USER_INFO_REPLY = 0x00F0;
	public static final int SERVER_AFFILATIONS_USER_INFO_REPLY = 0x00FA;
	public static final int SERVER_SHORT_USER_INFO_REPLY     = 0x0104;
	public static final int SERVER_HOMEPAGE_USER_INFO_REPLY  = 0x010E;
	public static final int SERVER_USER_FOUND                = 0x01A4;
	public static final int SERVER_LAST_USER_FOUND           = 0x01AE;	
	public static final int SERVER_REGISTATION_STATUS        = 0x0302;
	public static final int SERVER_RANDOM_SEARCH_REPLY       = 0x0366;
	public static final int SERVER_VARIABLE_REQUESTED_VIA_XML= 0x08A2;
	public static final int SERVER_ACK_FOR_FULLINFO          = 0x0C3F;
	public static final int SERVER_ACK_FOR_USER_SPAM_REPORT  = 0x2012;

	public static final int SET_USER_BASIC_INFO              = 0x03EA;
	public static final int SET_USER_WORK_INFO               = 0x03F3;
	public static final int SET_USER_MORE_INFO               = 0x03FD;
	public static final int SET_USER_NOTES_INFO              = 0x0406;
	public static final int SET_USER_EXTENDED_EMAIL_INFO     = 0x040B;
	public static final int SET_USER_INTERESTS_INFO          = 0x0410;
	public static final int SET_USER_AFFILIATIONS_INFO       = 0x041A;
	public static final int SET_USER_PERMISSIONS_INFO        = 0x0424;
	public static final int CHANGE_USER_PASSWORD             = 0x042E;
	public static final int SET_USER_HOMEPAGE_CATEGORY_INFO  = 0x0442;
	public static final int REQUEST_FULL_USER_INFO           = 0x04B2;
	public static final int REQUEST_SHORT_USER_INFO          = 0x04BA;
	public static final int UNREGISTER_USER                  = 0x04C4;
	public static final int REQUEST_FULL_USER_INFO_2         = 0x04DC;
	public static final int SEARCH_BY_DETAILS_PLAIN          = 0x0515;
	public static final int SEARCH_BY_UIN_PLAIN              = 0x051F;
	public static final int SEARCH_BY_EMAIL_PLAIN            = 0x0529;
	public static final int WHITEPAGES_SEARCH_PLAIN_SIMPLE   = 0x0533;
	public static final int SEARCH_BY_DETAILS_PLAIN_WILDCARD = 0x053D;
	public static final int SEARCH_BY_EMAIL_PLAIN_WILDCARD   = 0x0547;
	public static final int WHITEPAGES_SEARCH_PLAIN_WILDCARD = 0x0551;
	public static final int SEARCH_BY_UIN_TLV                = 0x0569;
	public static final int WHITEPAGES_SEARCH_TLV            = 0x055F;
	public static final int SEARCH_BY_EMAIL_TLV              = 0x0573;
	public static final int RANDOM_CHAT_USER_SEARCH          = 0x074E;
	public static final int REQUEST_SERVER_VARIABLE_VIA_XML  = 0x0898;
	public static final int SEND_REGISTRATION_STATS_REPORT   = 0x0AA5;
	public static final int SEND_SHORTCUT_BAR_STATS_REPORT   = 0x0AAF;
	public static final int SAVE_INFO_TLV_BASED              = 0x0C3A;
	public static final int CLIENT_SEND_SMS                  = 0x1482;
	public static final int CLIENT_SPAM_REPORT               = 0x2008;

	private int subType;

	public MetaSubTypeEnum(int subType) {
		this.subType = subType;
	}

	public int getSubType() {
		return subType;
	}
}