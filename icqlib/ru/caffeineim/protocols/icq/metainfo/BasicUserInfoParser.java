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

import java.util.EventListener;
import java.util.EventObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.MetaBasicUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MetaInfoListener;
import ru.caffeineim.protocols.icq.setting.enumerations.CountryEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.TimeZoneEnum;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 25.03.2008
 *   @author Samolisov Pavel
 */
public class BasicUserInfoParser extends BaseMetaInfoParser {

	private static Log log = LogFactory.getLog(BasicUserInfoParser.class);

	private String nickName;
	private String firstName;
	private String lastName;
	private String email;
	private String homeCity;
	private String homeState;
	private String homePhone;
	private String homeFax;
	private String homeAddress;
	private String cellPhone;
	private String zipCode;
	private int    homeCountry;
	private byte   timezone;
	private boolean authFlag;
	private boolean webawareFlag;
	private boolean directConnection;
	private boolean publishPrimaryEmail;


	protected EventObject getNewEvent() {
		return new MetaBasicUserInfoEvent(this);
	}


	protected void sendMessage(EventListener listener, EventObject e) {
		log.debug("notify listener " + listener.getClass().getName() + " onBasicUserInfo()");
		((MetaInfoListener) listener).onBasicUserInfo((MetaBasicUserInfoEvent) e);
	}


	public void parse(byte[] data, int position) throws ConvertStringException {
		position += 3; // skip subtype and success byte (always 0x0A) and data size.

		// Nickname lenght
		RawData rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;;

		// Nickname
		nickName = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// First Name lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// First Name
		firstName = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// Last Name lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Last Name
		lastName = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// Email lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Email
		email = (new RawData(data, position, rStrLen.getValue() - 1)).getStringValue();
		position += rStrLen.getValue();

		// Home city lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Home city
		homeCity = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// Home state lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Home state
		homeState = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// Home phone lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Home phone
		homePhone = (new RawData(data, position, rStrLen.getValue() - 1)).getStringValue();
		position += rStrLen.getValue();

		// Home fax lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Home fax
		homeFax = (new RawData(data, position, rStrLen.getValue() - 1)).getStringValue();
		position += rStrLen.getValue();

		// Home address lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Home address
		homeAddress = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// Cell phone lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Cell phone
		cellPhone = (new RawData(data, position, rStrLen.getValue() - 1)).getStringValue();
		position += rStrLen.getValue();

		// Home zip lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Home zip
		zipCode = (new RawData(data, position, rStrLen.getValue()-1)).getStringValue();
		position += rStrLen.getValue();

		// Country code
		RawData homeCountryRD = (new RawData(data, position, RawData.WORD_LENGHT));
		homeCountryRD.invertIndianness();
		homeCountry = homeCountryRD.getValue();
		position += RawData.WORD_LENGHT;

		// GMT offset
		timezone = (byte) (new RawData(data, position, RawData.BYTE_LENGHT)).getValue();
		position += RawData.BYTE_LENGHT;

		// Auth Flag
		RawData authdata = new RawData(data, position, RawData.BYTE_LENGHT);
		authFlag = (authdata.getValue() == 0);
		position += RawData.BYTE_LENGHT;

		// WebAware Flag
		RawData webawaredata = new RawData(data, position, RawData.BYTE_LENGHT);
		webawareFlag = (webawaredata.getValue() == 0);
		position += RawData.BYTE_LENGHT;

		// Direct connection permissions
		RawData directdata = new RawData(data, position, RawData.BYTE_LENGHT);
		directConnection = (directdata.getValue() == 0);
		position += RawData.BYTE_LENGHT;

		// Publish primary email flag
		RawData publishemaildata = new RawData(data, position, RawData.BYTE_LENGHT);
		publishPrimaryEmail = (publishemaildata.getValue() == 0);
		position += RawData.BYTE_LENGHT;

		// New Zip (russian zips is 6 simbols)
		if (position + 6 < data.length) {
			position += RawData.BYTE_LENGHT;
			zipCode = (new RawData(data, position, 6)).getStringValue();
		}
	}

	public String getNickName() {
		return nickName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getEmail() {
		return email;
	}

	public String getHomeCity() {
		return homeCity;
	}

	public String getHomeState() {
		return homeState;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public String getHomeFax() {
		return homeFax;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public String getZipCode() {
		return zipCode;
	}

	public CountryEnum getHomeCountry() {
		return new CountryEnum(homeCountry);
	}

	public TimeZoneEnum getTimeZone() {
		return new TimeZoneEnum(timezone);
	}

	public boolean isAuthFlag() {
		return authFlag;
	}

	public boolean isWebawareFlag() {
		return webawareFlag;
	}

	public boolean isDirectConnection() {
		return directConnection;
	}

	public boolean isPublishPrimaryEmail() {
		return publishPrimaryEmail;
	}
}
