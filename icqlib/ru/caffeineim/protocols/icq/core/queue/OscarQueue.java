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
package ru.caffeineim.protocols.icq.core.queue;

import java.util.LinkedList;

/**
 * <p>Created by 11.01.2010
 *   @author Samolisov Pavel
 */
public class OscarQueue extends LinkedList implements Queue {

	private static final long serialVersionUID = 138769934354273633L;

	public synchronized void push(Object obj) {
		super.addLast(obj);
	}

	public synchronized Object poll() {
		Object element = super.getFirst();
		super.remove(element);
		return element;
	}

	public synchronized boolean isEmpty() {
		return super.size() < 1;
	}
}
