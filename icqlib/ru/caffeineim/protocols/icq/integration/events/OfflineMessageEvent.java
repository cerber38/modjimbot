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

import ru.caffeineim.protocols.icq.metainfo.OfflineMessageParser;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageFlagsEnum;
import ru.caffeineim.protocols.icq.setting.enumerations.MessageTypeEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel 
 */
public class OfflineMessageEvent extends EventObject {
	
    private static final long serialVersionUID = -1783900296942284591L;

    public OfflineMessageEvent(OfflineMessageParser parser) {
		super(parser);
	}

	public String getSenderUin() {
		return ((OfflineMessageParser) getSource()).getSenderUin();
	}
	
	public Date getSendDate() {
		return ((OfflineMessageParser) getSource()).getSendDate();
	}

	public String getMessage() {
		return ((OfflineMessageParser) getSource()).getMessage();
	}
	
	public MessageTypeEnum getMessageType() {
		return ((OfflineMessageParser) getSource()).getMessageType();
	}
	
	public MessageFlagsEnum getMessageFlag() {
		return ((OfflineMessageParser) getSource()).getMessageFlag();
	}
}