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

import java.util.ArrayList;
import java.util.List;

import ru.caffeineim.protocols.icq.Item;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class Group extends ContactListItem {

    private List contacts = new ArrayList();

    /**
     * Creates a new instance of Group
     */
    public Group(Item item) {
        super(item);
    }

    /**
     * Creates a new instance of Group
     */
    public Group(short groupId, String name) {
        super(new Item((short)0x00, groupId, name));
    }

    public void addItem(ContactListItem item) {
        if(item == null)
            throw new NullPointerException();
        contacts.add(item);
    }

    public void removeItem(ContactListItem item) {
        if(item == null)
            throw new NullPointerException();
        contacts.remove(item);
    }

    public List getContainedItems() {
        return contacts;
    }
}
