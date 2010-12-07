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
package ru.caffeineim.protocols.icq.contacts;

import ru.caffeineim.protocols.icq.Item;

/**
 * <p>Created by
 *   @author Lo�c Broquet 
 */
public class ContactListItem {
	
	private String id; // Item name string
	
	private short itemid; // Item id
	
	private short groupid; // Group id
	
	/** 
	 * Creates a new instance of ContactListItem 
	 */
	public ContactListItem(Item item) {
		this.id = item.getName();
		this.itemid = item.getId();
		this.groupid = item.getGroup();	
	}
	
	public String getId() {
		return id;
	}	
	
	public short getItemId() {
		return itemid;
	}
	
	public short getGroupId() {
		return groupid;
	}
}
