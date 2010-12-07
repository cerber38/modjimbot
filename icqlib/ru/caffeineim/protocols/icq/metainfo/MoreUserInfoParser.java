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
package ru.caffeineim.protocols.icq.metainfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.MetaMoreUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MetaInfoListener;
import ru.caffeineim.protocols.icq.setting.enumerations.CountryEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.GenderEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.LanguagesEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MaritalStatusEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.TimeZoneEnum;
import ru.caffeineim.protocols.icq.tool.DateTools;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 30.03.2008
 *   @author Samolisov Pavel
 */
public class MoreUserInfoParser extends BaseMetaInfoParser {

	private static Log log = LogFactory.getLog(MoreUserInfoParser.class);

    private int age;
    private GenderEnum gender;
    private String homePage;
    private Date birth;
    private List languages;
    private String originalCity;
    private String originalState;
    private CountryEnum originalCountry;
    private TimeZoneEnum userTimeZone;
    private MaritalStatusEnum maritalStatus;

    protected EventObject getNewEvent() {
        return new MetaMoreUserInfoEvent(this);
    }

    protected void sendMessage(EventListener listener, EventObject e) {
    	log.debug("notify listener " + listener.getClass().getName() + " onMoreUserInfo()");
        ((MetaInfoListener) listener).onMoreUserInfo((MetaMoreUserInfoEvent) e);
    }

    public void parse(byte[] data, int position) throws ConvertStringException {
        position += 3; // skip subtype and success byte (always 0x0A) and data size.

        // Age
        RawData ageRD = new RawData(data, position, RawData.WORD_LENGHT);
        ageRD.invertIndianness();
        age = ageRD.getValue();
        position += RawData.WORD_LENGHT;

        // Gender
        gender = new GenderEnum(new RawData(data, position, RawData.BYTE_LENGHT).getValue());
        position += RawData.BYTE_LENGHT;

        // Home Page Lenght
        RawData rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
        rStrLen.invertIndianness();
        position += RawData.WORD_LENGHT;

        // Home page
        homePage = (new RawData(data, position, rStrLen.getValue() - 1)).getStringValue();
        position += rStrLen.getValue();

        // Birth year
        RawData year = new RawData(data, position, RawData.WORD_LENGHT);
        year.invertIndianness();
        position += RawData.WORD_LENGHT;

        // Birth month
        RawData month = new RawData(data, position, RawData.BYTE_LENGHT);
        position += RawData.BYTE_LENGHT;

        // Birth day
        RawData day = new RawData(data, position, RawData.BYTE_LENGHT);
        position += RawData.BYTE_LENGHT;

        birth = DateTools.makeDate(year.getValue(), month.getValue(), day.getValue(), 0, 0);

        // Languages
        languages = new ArrayList();
        int lang = new RawData(data, position, RawData.BYTE_LENGHT).getValue();
        position += RawData.BYTE_LENGHT;
        languages.add(new LanguagesEnum(lang));

        lang = new RawData(data, position, RawData.BYTE_LENGHT).getValue();
        position += RawData.BYTE_LENGHT;
        languages.add(new LanguagesEnum(lang));

        lang = new RawData(data, position, RawData.BYTE_LENGHT).getValue();
        position += RawData.BYTE_LENGHT;
        languages.add(new LanguagesEnum(lang));

        // UNKNOWN
        position += RawData.WORD_LENGHT;

        // Original From City lenght
        rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
        rStrLen.invertIndianness();
        position += RawData.WORD_LENGHT;

        // Original From City
        originalCity = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
        position += rStrLen.getValue();

        // Original From State lenght
        rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
        rStrLen.invertIndianness();
        position += RawData.WORD_LENGHT;

        // Original From State
        originalState = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
        position += rStrLen.getValue();

        // Original From country
        RawData originalCountryRD = (new RawData(data, position, RawData.WORD_LENGHT));
        originalCountryRD.invertIndianness();
        originalCountry = new CountryEnum(originalCountryRD.getValue());
        position += RawData.WORD_LENGHT;

        // Marital Status
        RawData maritalStatusRD = (new RawData(data, position, RawData.WORD_LENGHT));
        maritalStatusRD.invertIndianness();
        maritalStatus = new MaritalStatusEnum(maritalStatusRD.getValue());
        position += RawData.WORD_LENGHT;

        // User time zone
        userTimeZone = new TimeZoneEnum((new RawData(data, position, RawData.BYTE_LENGHT)).getValue());
    }

    public int getAge() {
        return age;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public String getHomePage() {
        return homePage;
    }

    public Date getBirth() {
        return birth;
    }

    public List getLanguages() {
        return languages;
    }

    public String getOriginalCity() {
        return originalCity;
    }

    public String getOriginalState() {
        return originalState;
    }

    public CountryEnum getOriginalCountry() {
        return originalCountry;
    }

    public TimeZoneEnum getUserTimeZone() {
        return userTimeZone;
    }

    public MaritalStatusEnum getMaritalStatus() {
        return maritalStatus;
    }
}
