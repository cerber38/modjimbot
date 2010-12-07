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

import ru.caffeineim.protocols.icq.metainfo.WorkUserInfoParser;
import ru.caffeineim.protocols.icq.setting.enumerations.CountryEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.OccupationEnum;

/**
 * <p>Created by 26.03.2008
 *   @author Samolisov Pavel
 */
public class MetaWorkUserInfoEvent extends EventObject {
	
	private static final long serialVersionUID = -8999509597744117843L;

	public MetaWorkUserInfoEvent(WorkUserInfoParser source) {
		super(source);
	}
	
	public String getWorkCity() {
		return ((WorkUserInfoParser) getSource()).getWorkCity();
	}

	public String getWorkState() {
		return ((WorkUserInfoParser) getSource()).getWorkState();
	}
	
	public String getWorkPhone() {
		return ((WorkUserInfoParser) getSource()).getWorkPhone();
	}
	
	public String getWorkFax() {
		return ((WorkUserInfoParser) getSource()).getWorkFax();
	}
	
	public String getWorkAddress() {
		return ((WorkUserInfoParser) getSource()).getWorkAddress();
	}
	
	public String getWorkZip() {
		return ((WorkUserInfoParser) getSource()).getWorkZip();
	}
	
	public String getWorkCompany() {
		return ((WorkUserInfoParser) getSource()).getWorkCompany();
	}
	
	public String getWorkDepartment() {
		return ((WorkUserInfoParser) getSource()).getWorkDepartment();
	}
	
	public String getWorkPosition() {
		return ((WorkUserInfoParser) getSource()).getWorkPosition();
	}
	
	public String getWorkWebPage() {
		return ((WorkUserInfoParser) getSource()).getWorkWebPage();
	}
	
	public OccupationEnum getWorkOccupation() {
		return ((WorkUserInfoParser) getSource()).getWorkOccupation();
	}
	
	public CountryEnum getWorkCountry() {
		return ((WorkUserInfoParser) getSource()).getWorkCountry();
	}
}