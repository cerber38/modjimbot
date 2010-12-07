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
package ru.caffeineim.protocols.icq.packet.sent.meta;

import ru.caffeineim.protocols.icq.setting.enumerations.MetaSubTypeEnum;

/**
 * <p>Created by 15.08.07
 *   @author Samolisov Pavel
 */
public class RequestShortUserInfo extends RequestUserInfo {		
	/** 
	 * Creates a new instance of RequestShortUserInfo 
	 * 
	 * @param uinSearch ICQ UIN to search
	 * @param uinForRequest for request ICQ UIN
	 */
	public RequestShortUserInfo(String uinSearch, String uinForRequest) {
		super(uinSearch, uinForRequest, MetaSubTypeEnum.REQUEST_SHORT_USER_INFO);
	}	
}
