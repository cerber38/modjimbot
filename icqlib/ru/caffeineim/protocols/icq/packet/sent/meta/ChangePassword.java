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

import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.setting.enumerations.MetaSubTypeEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MetaTypeEnum;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 30.03.2008
 *   @author Samolisov Pavel
 */
public class ChangePassword extends BaseClientMeta {
	
	protected static final int REQUEST_LENGHT = 0x0D00;
	
	public ChangePassword(String uinForRequest, String password) throws ConvertStringException {		
		super(REQUEST_LENGHT + (StringTools.stringToByteArray(password).length << 8), 
				uinForRequest, MetaTypeEnum.CLIENT_ADVANCED_META, MetaSubTypeEnum.CHANGE_USER_PASSWORD);
		
		// Password lenght
		byte[] passwd = StringTools.stringToByteArray(password);
		RawData rdPasswd = new RawData(passwd.length, RawData.WORD_LENGHT);
		rdPasswd.invertIndianness();
		tlv.appendRawDataToTlv(rdPasswd);
		
		// Pasword
		tlv.appendRawDataToTlv(new RawData(passwd));
		tlv.appendRawDataToTlv(new RawData(0x00, RawData.BYTE_LENGHT));
		
		finalizePacket();
	}
}