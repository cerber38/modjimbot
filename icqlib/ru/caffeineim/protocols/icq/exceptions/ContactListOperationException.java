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
package ru.caffeineim.protocols.icq.exceptions;

/**
 * <p>Created by 06.02.2010
 *   @author Samolisov Pavel
 */
public class ContactListOperationException extends Exception {

	private static final long serialVersionUID = -2693101766944086413L;

	public ContactListOperationException(String message) {
		super(message);
	}

	public ContactListOperationException(String message, Throwable t) {
		super(message, t);
	}

	public ContactListOperationException(Throwable t) {
		super(t);
	}
}
