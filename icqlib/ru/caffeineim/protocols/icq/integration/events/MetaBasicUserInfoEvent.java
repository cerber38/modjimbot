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
package ru.caffeineim.protocols.icq.integration.events;

import java.util.EventObject;

import ru.caffeineim.protocols.icq.metainfo.BasicUserInfoParser;
import ru.caffeineim.protocols.icq.setting.enumerations.CountryEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.TimeZoneEnum;

/**
 * <p>Created by 25.03.2008
 *   @author Samolisov Pavel
 */
public class MetaBasicUserInfoEvent extends EventObject {
	
	private static final long serialVersionUID = -6831886773708776171L;

	public MetaBasicUserInfoEvent(BasicUserInfoParser source) {
		super(source);
	}	

	public String getNickName() {
		return ((BasicUserInfoParser) getSource()).getNickName();
	}
	
	public String getFirstName() {
		return ((BasicUserInfoParser) getSource()).getFirstName();
	}
	
	public String getLastName() {
		return ((BasicUserInfoParser) getSource()).getLastName();
	}
	
	public String getEmail() {
		return ((BasicUserInfoParser) getSource()).getEmail();
	}
	
	public String getHomeCity() {
		return ((BasicUserInfoParser) getSource()).getHomeCity();
	}
	
	public String getHomeState() {
		return ((BasicUserInfoParser) getSource()).getHomeState();
	}
	
	public String getHomePhone() {
		return ((BasicUserInfoParser) getSource()).getHomePhone();
	}
	
	public String getHomeFax() {
		return ((BasicUserInfoParser) getSource()).getHomeFax();
	}
	
	public String getHomeAddress() {
		return ((BasicUserInfoParser) getSource()).getHomeAddress();
	}
	
	public String getCellPhone() {
		return ((BasicUserInfoParser) getSource()).getCellPhone();
	}
	
	public String getZipCode() {
		return ((BasicUserInfoParser) getSource()).getZipCode();
	}
	
	public CountryEnum getHomeCountry() {
		return ((BasicUserInfoParser) getSource()).getHomeCountry();
	}
	
	public TimeZoneEnum getTimeZone() {
		return ((BasicUserInfoParser) getSource()).getTimeZone();
	}
	
	public boolean isAuth() {
		return ((BasicUserInfoParser) getSource()).isAuthFlag();
	}
	
	public boolean isWebaware() {
		return ((BasicUserInfoParser) getSource()).isWebawareFlag();
	}
	
	public boolean isDirectConnection() {
		return ((BasicUserInfoParser) getSource()).isDirectConnection();
	}
	
	public boolean isPublishPrimaryEmail() {
		return ((BasicUserInfoParser) getSource()).isPublishPrimaryEmail();
	}
}
