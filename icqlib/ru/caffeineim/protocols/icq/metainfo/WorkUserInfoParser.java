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
import ru.caffeineim.protocols.icq.integration.events.MetaWorkUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MetaInfoListener;
import ru.caffeineim.protocols.icq.setting.enumerations.CountryEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.OccupationEnum;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 26.03.2008
 *   @author Samolisov Pavel
 */
public class WorkUserInfoParser extends BaseMetaInfoParser {

	private static Log log = LogFactory.getLog(WorkUserInfoParser.class);

	private String workCity;
	private String workState;
	private String workPhone;
	private String workFax;
	private String workAddress;
	private String workZip;
	private int    workCountry;
	private String workCompany;
	private String workDepartment;
	private String workPosition;
	private String workWebPage;
	private int workOccupationCode;


	protected EventObject getNewEvent() {
		return new MetaWorkUserInfoEvent(this);
	}

	protected void sendMessage(EventListener listener, EventObject e) {
		log.debug("notify listener " + listener.getClass().getName() + " onWorkUserInfo()");
		((MetaInfoListener) listener).onWorkUserInfo((MetaWorkUserInfoEvent) e);
	}

	public void parse(byte[] data, int position) throws ConvertStringException {
		position += 3; // skip subtype and success byte (always 0x0A) and data size.

		// lenght
		RawData rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Work City
		workCity = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Work State
		workState = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Work Phone
		workPhone = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Work Fax
		workFax = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Work Address
		workAddress = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

		// Work Zip code
		workZip = (new RawData(data, position, rStrLen.getValue() - 1)).getStringValue();
		position += rStrLen.getValue();

		// Work Country
		RawData country = new RawData(data, position, RawData.WORD_LENGHT);
		country.invertIndianness();
		position += RawData.WORD_LENGHT;
		workCountry = country.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;;

		// Work Company
		workCompany = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;;

		// Work Department
		workDepartment = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;;

		// Work Position
		workPosition = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// Work Occupation Code
		RawData occup = new RawData(data, position, RawData.WORD_LENGHT);
		occup.invertIndianness();
		position += RawData.WORD_LENGHT;
		workOccupationCode = occup.getValue();

		// lenght
		rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;;

		// Work WebPage
		workWebPage = StringTools.byteArrayToString(data, position, rStrLen.getValue() - 1);
		position += rStrLen.getValue();

		// Unknown
		if (position + 1 <= data.length) {
			position += RawData.BYTE_LENGHT;
		}

		// New Zip (russian zips is 6 simbols)
		if (position + 6 < data.length) {
			position += RawData.BYTE_LENGHT;
			workZip = (new RawData(data, position, 6)).getStringValue();
		}
	}

	public String getWorkCity() {
		return workCity;
	}

	public String getWorkState() {
		return workState;
	}

	public String getWorkPhone() {
		return workPhone;
	}

	public String getWorkFax() {
		return workFax;
	}

	public String getWorkAddress() {
		return workAddress;
	}

	public String getWorkZip() {
		return workZip;
	}

	public String getWorkCompany() {
		return workCompany;
	}

	public String getWorkDepartment() {
		return workDepartment;
	}

	public String getWorkPosition() {
		return workPosition;
	}

	public String getWorkWebPage() {
		return workWebPage;
	}

	public OccupationEnum getWorkOccupation() {
		return new OccupationEnum(workOccupationCode);
	}

	public CountryEnum getWorkCountry() {
		return new CountryEnum(workCountry);
	}
}
