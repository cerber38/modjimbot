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

import ru.caffeineim.protocols.icq.packet.received.icbm.MessageAutoReply__4_11;
import ru.caffeineim.protocols.icq.setting.enumerations.XStatusModeEnum;

/**
 * <p>Created by 17.07.07
 *   @author Samolisov Pavel 
 */
public class XStatusResponseEvent extends EventObject {    
    
    private static final long serialVersionUID = 3401985213523814147L;
    
    private XStatusModeEnum xstatus = new XStatusModeEnum(XStatusModeEnum.NONE);
	private String title = "";
	private String description = "";
	
	/** 
	 * Creates a new instance of XStatusResponseEvent 
	 */
	public XStatusResponseEvent(MessageAutoReply__4_11 source) {
		super(source);
		parseXStatusMessage((String)( (MessageAutoReply__4_11)getSource()).getMessage());
	}
	
	private void parseXStatusMessage(String message) {
		String[] strs = message.split("[&;]");
		try {
			xstatus = new XStatusModeEnum(Integer.parseInt(strs[44]));
			title = strs[52];
			description = strs[60];
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getSenderID() {
		return ( (MessageAutoReply__4_11) getSource()).getSenderID();	
	}
	
	public XStatusModeEnum getXStatus() {
		return xstatus;
    }
	
	public String getTitle(){
		return title;
	}
	
	public String getDescription() {
		return description;
	}
}