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

import ru.caffeineim.protocols.icq.setting.enumerations.LoginErrorTypeEnum;

/**
 * <p>Created by
 *   @author Lo�c Broquet 
 */
public class LoginErrorEvent extends EventObject {	
	
    private static final long serialVersionUID = 7662695167749107169L;

    /** 
	 * Creates a new instance of LoginErrorEvent 
	 */
	public LoginErrorEvent(LoginErrorTypeEnum type) {
		super(type);
	}
	
	public String getErrorMessage() {
		return source.toString(); 
	}
	
	public LoginErrorTypeEnum getErrorType() {
		return (LoginErrorTypeEnum) source;
	}
}
