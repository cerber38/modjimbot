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
package ru.caffeineim.protocols.icq.packet.received.ssi;

import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ru.caffeineim.protocols.icq.Item;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.integration.events.ContactListEvent;
import ru.caffeineim.protocols.icq.integration.listeners.ContactListListener;
import ru.caffeineim.protocols.icq.packet.received.ReceivedPacket;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public final class SsiContactListReply__19_6 extends ReceivedPacket {

	private static Log log = LogFactory.getLog(SsiContactListReply__19_6.class);

	private int version;

	private int count;

    private int lastChangeTime;

    private SortedMap items = new TreeMap();

    public SsiContactListReply__19_6(byte array[]) throws ConvertStringException {
        super(array, true);
        int position = 8;
        byte data[] = getSnac().getDataFieldByteArray();
        version = (new RawData(data, position, RawData.BYTE_LENGHT)).getValue();
        position++;
        count = (new RawData(data, position, RawData.WORD_LENGHT)).getValue();
        position += RawData.WORD_LENGHT;

        for (int i = 0; i < count; i++) {
            Item item = new Item(data, position);
            int id = (item.getGroup() << 16) + item.getId();
            items.put(new Integer(id), item);
            position += item.getLength();
        }

        lastChangeTime = (new RawData(data, position, RawData.DWORD_LENGHT)).getValue();
        position += RawData.DWORD_LENGHT;
    }

    public void execute(OscarConnection connection) {
    }

    public void notifyEvent(OscarConnection connection) {
        ContactListEvent e = new ContactListEvent(this);

        log.debug("fetched contact-list");
        connection.buildContactList(e.getRoot(), getCount());

        for (int i = 0; i < connection.getContactListListeners().size(); i++) {
            ContactListListener l = (ContactListListener) connection.getContactListListeners().get(i);
			log.debug("notify listener " + l.getClass().getName() + " onUpdateContactList()");
            l.onUpdateContactList(e);
        }
    }

    public int getCount() {
        return count;
    }

    public int getLastChangeTime() {
        return lastChangeTime;
    }

    public Iterator getItemsIterator() {
        return items.values().iterator();
    }

    public Item get(int id) {
        return (Item) items.get(new Integer(id));
    }
}
