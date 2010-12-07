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

import java.util.Date;
import java.util.EventObject;
import java.util.List;

import ru.caffeineim.protocols.icq.metainfo.MoreUserInfoParser;
import ru.caffeineim.protocols.icq.setting.enumerations.CountryEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.GenderEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MaritalStatusEnum;

/**
 * <p>Created by 30.03.2008
 *   @author Samolisov Pavel
 */
public class MetaMoreUserInfoEvent extends EventObject {

    private static final long serialVersionUID = -7346651515115520506L;

    public MetaMoreUserInfoEvent(MoreUserInfoParser source) {
        super(source);
    }

    public int getAge() {
        return ((MoreUserInfoParser) getSource()).getAge();
    }

    public GenderEnum getGender() {
        return ((MoreUserInfoParser) getSource()).getGender();
    }

    public String getHomePage() {
        return ((MoreUserInfoParser) getSource()).getHomePage();
    }

    public Date getBirth() {
        return ((MoreUserInfoParser) getSource()).getBirth();
    }

    public List getLanguages() {
        return ((MoreUserInfoParser) getSource()).getLanguages();
    }

    public String getOriginalCity() {
        return ((MoreUserInfoParser) getSource()).getOriginalCity();
    }

    public String getOriginalState() {
        return ((MoreUserInfoParser) getSource()).getOriginalState();
    }

    public CountryEnum getOriginalCountry() {
        return ((MoreUserInfoParser) getSource()).getOriginalCountry();
    }

    public MaritalStatusEnum getMaritalStatus() {
        return ((MoreUserInfoParser) getSource()).getMaritalStatus();
    }
}
