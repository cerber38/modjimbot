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
package ru.caffeineim.protocols.icq.core.exceptions;

import ru.caffeineim.protocols.icq.setting.enumerations.LoginErrorTypeEnum;

/**
 *	@author Fabrice Michellonet
 */
public class LoginException extends Exception {

    private static final long serialVersionUID = 5005784558498444971L;

    private LoginErrorTypeEnum errorType;

	public LoginException(LoginErrorTypeEnum type) {
		super(type.toString());
		errorType = type;
	}

	public LoginException(LoginErrorTypeEnum type, Throwable t) {
		super(type.toString(),t);
		errorType = type;
	}

	public LoginException(String s, Throwable t) {
		super(s,t);
		errorType = new LoginErrorTypeEnum(LoginErrorTypeEnum.UNKNOWN_ERROR);
	}

	public LoginErrorTypeEnum getErrorType() {
		return errorType;
	}
}