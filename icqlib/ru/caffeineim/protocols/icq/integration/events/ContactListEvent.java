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
import java.util.Iterator;
import java.util.SortedMap;

import ru.caffeineim.protocols.icq.Item;
import ru.caffeineim.protocols.icq.contacts.Contact;
import ru.caffeineim.protocols.icq.contacts.Group;
import ru.caffeineim.protocols.icq.packet.received.ssi.SsiContactListReply__19_6;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class ContactListEvent extends EventObject {

	private static final long serialVersionUID = -3740165191665792396L;

	private static final String EMPTY = "";

    private Group root;

    /**
     * Creates a new instance of ContactListEvent
     */
    public ContactListEvent(SsiContactListReply__19_6 source) {
        super(source);

        SortedMap grpMap = new java.util.TreeMap();
        SortedMap contMap = new java.util.TreeMap();

        for (Iterator iter = source.getItemsIterator(); iter.hasNext();) {
            Item item = (Item) iter.next();
            switch(item.getType()) {
                case Item.TYPE_IGNORE_LIST:
                case Item.TYPE_CONTACT:
                    contMap.put(new Short(item.getId()), item);
                    break;
                case Item.TYPE_GROUP:
                    grpMap.put(new Short(item.getGroup()), new Group(item));
                    break;
                case Item.TYPE_CONTACT_ICON:
                    break;
            }
        }

        for (Iterator iter = contMap.values().iterator(); iter.hasNext();) {
            Item item = (Item) iter.next();
            Group grp = (Group) grpMap.get(new Short(item.getGroup()));
            grp.addItem(new Contact(item));
        }

        root = (Group) grpMap.remove(new Short((short) 0x0000));
        if (root == null)
        	root = new Group((short) 0x0000, EMPTY);

        for (Iterator iter = grpMap.values().iterator(); iter.hasNext();) {
        	Group grp = (Group) iter.next();
        	root.addItem(grp);
        }
    }

    public Group getRoot() {
        return root;
    }

    public int getCount() {
    	return ((SsiContactListReply__19_6) getSource()).getCount();
    }
}
