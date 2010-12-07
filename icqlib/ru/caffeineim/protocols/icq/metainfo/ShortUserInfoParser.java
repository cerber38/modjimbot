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
import ru.caffeineim.protocols.icq.integration.events.MetaShortUserInfoEvent;
import ru.caffeineim.protocols.icq.integration.listeners.MetaInfoListener;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 24.03.2008
 *   @author Samolisov Pavel
 */
public class ShortUserInfoParser extends BaseMetaInfoParser {

	private static Log log = LogFactory.getLog(ShortUserInfoParser.class);

	private String nickName;
	private String firstName;
	private String lastName;
	private String email;
	private boolean authFlag;

	protected EventObject getNewEvent() {
		return new MetaShortUserInfoEvent(this);
	}

	protected void sendMessage(EventListener listener, EventObject e) {
		log.debug("notify listener " + listener.getClass().getName() + " onShortUserInfo()");
		((MetaInfoListener) listener).onShortUserInfo((MetaShortUserInfoEvent) e);
	}

	public void parse(byte[] data, int position) throws ConvertStringException {
		position += 3; // skip subtype and success byte (always 0x0A) and data size.

		// Nickname lenght
		RawData rStrLen = new RawData(data, position, RawData.WORD_LENGHT);
		rStrLen.invertIndianness();
		position += RawData.WORD_LENGHT;

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

		// Auth Flag
		RawData authdata = new RawData(data, position, RawData.BYTE_LENGHT);
		authFlag = (authdata.getValue() == 0);
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

	public boolean isAuthFlag() {
		return authFlag;
	}
}
