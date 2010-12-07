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

import java.text.MessageFormat;
import java.util.Iterator;

import ru.caffeineim.protocols.icq.core.OscarConnection;
import ru.caffeineim.protocols.icq.exceptions.ContactListOperationException;
import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.packet.sent.buddylist.AddToContactList;
import ru.caffeineim.protocols.icq.packet.sent.buddylist.RemoveFromContactList;
import ru.caffeineim.protocols.icq.packet.sent.meta.RequestShortUserInfo;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiAddItem;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiBeginEdit;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiEndEdit;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiRemoveItem;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiRemoveYourself;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiSendAuthReplyMessage;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiSendAuthRequestMessage;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiSendYouWereAdded;
import ru.caffeineim.protocols.icq.packet.sent.ssi.SsiUpdateGroupHeader;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by 15.08.2007
 *   @author Samolisov Pavel
 */
public class ContactList implements IContactList {

	private static final String GROUP_TITLE_FMT = "{0}:\n";

	private static final String CONTACT_TITLE_FMT = "   {0} ({1})\n";

    private short maxGroupId = 0;

    private short maxContactId = 0;

    private OscarConnection connection;

    private Group rootGroup;

    private int count;

    public ContactList(OscarConnection connection, Group rootGroup, int count) {
    	this.connection = connection;
        this.rootGroup = rootGroup;
        this.count = count;

        for (Iterator iter = rootGroup.getContainedItems().iterator(); iter.hasNext();) {
            ContactListItem item = (ContactListItem) iter.next();
            if (item instanceof Group) {
                Group grp = (Group) item;

                if (grp.getGroupId() > maxGroupId)
                    maxGroupId = grp.getGroupId();

                for (Iterator grpiter = rootGroup.getContainedItems().iterator(); grpiter.hasNext();) {
                    ContactListItem cntct = (ContactListItem) grpiter.next();
                    if (cntct instanceof Contact) {
                        Contact cnt = (Contact) cntct;
                        if (cnt.getItemId() > maxContactId)
                            maxContactId = cnt.getItemId();
                    }
                }
            }
        }
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#addContact(java.lang.String, java.lang.String)
	 */
    public void addContact(String userId, String groupName) throws ContactListOperationException {
    	addContact(userId, getGroupByName(groupName));
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#addContact(java.lang.String, ru.caffeineim.protocols.icq.contacts.Group)
	 */
    public void addContact(String userId, Group group) throws ContactListOperationException {
        Contact contact = new Contact(++ maxContactId, group.getGroupId(), userId);
        addContact(contact, group);
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#addContact(ru.caffeineim.protocols.icq.contacts.Contact, ru.caffeineim.protocols.icq.contacts.Group)
	 */
    public void addContact(Contact contact, Group group) throws ContactListOperationException {
    	if (group == null)
    		throw new ContactListOperationException("Group could not be null");

    	group.addItem(contact);
        count++;

        try {
        	connection.sendFlap(new SsiBeginEdit());
        	connection.sendFlap(new SsiAddItem(contact));
        	connection.sendFlap(new SsiUpdateGroupHeader(group));
        	connection.sendFlap(new SsiEndEdit());

        	connection.sendFlap(new AddToContactList(contact.getId()));

        	if (StringTools.isEmpty(contact.getNickName())) {
        		connection.sendFlap(new RequestShortUserInfo(contact.getId(), connection.getUserId()));
        	}
        }
        catch (ConvertStringException e) {
        	throw new ContactListOperationException(e);
		}
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#removeContact(java.lang.String)
	 */
    public void removeContact(String userId) throws ContactListOperationException {
        Contact contact = getContactById(userId);
        if (contact != null) {
            removeContact(contact);
        }
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#removeContact(ru.caffeineim.protocols.icq.contacts.Contact)
	 */
    public void removeContact(Contact contact) throws ContactListOperationException {
        Group group = getGroupById(contact.getGroupId());
        if (group == null)
        	throw new ContactListOperationException("Group could not be null");

        group.removeItem(contact);

        try {
        	connection.sendFlap(new SsiBeginEdit());
        	connection.sendFlap(new SsiRemoveItem(contact));
        	connection.sendFlap(new SsiUpdateGroupHeader(group));
        	connection.sendFlap(new SsiEndEdit());

        	connection.sendFlap(new RemoveFromContactList(contact.getId()));
        }
        catch (ConvertStringException e) {
        	throw new ContactListOperationException(e);
		}
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#addGroup(java.lang.String)
	 */
    public void addGroup(String group) throws ContactListOperationException {
        addGroup(new Group(++ maxGroupId, group));
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#addGroup(ru.caffeineim.protocols.icq.contacts.Group)
	 */
    public void addGroup(Group group) throws ContactListOperationException {
    	if (group == null)
        	throw new ContactListOperationException("Group could not be null");

    	rootGroup.addItem(group);

        try {
        	connection.sendFlap(new SsiBeginEdit());
        	connection.sendFlap(new SsiAddItem(group));
        	connection.sendFlap(new SsiEndEdit());
        }
        catch (ConvertStringException e) {
			throw new ContactListOperationException(e);
		}
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#removeGroup(ru.caffeineim.protocols.icq.contacts.Group)
	 */
    public void removeGroup(Group group) throws ContactListOperationException {
    	if (group == null)
        	throw new ContactListOperationException("Group could not be null");

    	rootGroup.removeItem(group);

    	try {
    		connection.sendFlap(new SsiBeginEdit());
    		connection.sendFlap(new SsiRemoveItem(group));
    		connection.sendFlap(new SsiEndEdit());
    	}
    	catch (ConvertStringException e) {
    		throw new ContactListOperationException(e);
		}
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#removeYourself(java.lang.String)
	 */
    public void removeYourself(String userId) {
        connection.sendFlap(new SsiRemoveYourself(userId));
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#sendAuthRequestMessage(java.lang.String, java.lang.String)
	 */
    public void sendAuthRequestMessage(String userId, String request) throws ContactListOperationException {
    	try {
    		connection.sendFlap(new SsiSendAuthRequestMessage(userId, request));
    	}
    	catch (ConvertStringException e) {
    		throw new ContactListOperationException(e);
		}
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#sendAuthReplyMessage(java.lang.String, java.lang.String, boolean)
	 */
    public void sendAuthReplyMessage(String userId, String reply, boolean auth) throws ContactListOperationException {
    	try {
    		connection.sendFlap(new SsiSendAuthReplyMessage(userId, reply, auth));
    	}
    	catch (ConvertStringException e) {
    		throw new ContactListOperationException(e);
		}
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#sendYouWereAdded(java.lang.String)
	 */
    public void sendYouWereAdded(String userId) {
        connection.sendFlap(new SsiSendYouWereAdded(userId));
    }

    /* (non-Javadoc)
	 * @see ru.caffeineim.protocols.icq.contacts.IContactList#getRootGroup()
	 */
    public Group getRootGroup() {
        return rootGroup;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = rootGroup.getContainedItems().iterator(); iter.hasNext();) {
            ContactListItem item = (ContactListItem) iter.next();
            sb.append(MessageFormat.format(GROUP_TITLE_FMT, new Object[]{item.getId()}));
            if (item instanceof Group) {
                Group grp = (Group) item;
                for (Iterator grpiter = grp.getContainedItems().iterator(); grpiter.hasNext();) {
                    ContactListItem grpitem = (ContactListItem) grpiter.next();
                    if (grpitem instanceof Contact) {
                        Contact cnt = (Contact) grpitem;
                        sb.append(MessageFormat.format(CONTACT_TITLE_FMT, new Object[]{cnt.getNickName(), cnt.getId()}));
                    }
                }
            }
        }

        return sb.toString();
    }

    /**
     * @param id идентификатор группы
     * @return группу по ее идентификатору
     */
    private Group getGroupById(short id) {
        for (Iterator iter = rootGroup.getContainedItems().iterator(); iter.hasNext();) {
            ContactListItem item = (ContactListItem) iter.next();
            if (item.getGroupId() == id) {
                return (Group) item;
            }
        }

        return null;
    }

    /**
     * @param name название группы
     * @return группу по ее названию
     */
    private Group getGroupByName(String name) {
    	if (name == null)
    		return null;

        for (Iterator iter = rootGroup.getContainedItems().iterator(); iter.hasNext();) {
            ContactListItem item = (ContactListItem) iter.next();
            if (name.equals(item.getId())) {
                return (Group) item;
            }
        }

        return null;
    }

    /**
     * @param userId
     * @return контакт по его идентификатору
     */
    private Contact getContactById(String userId) {
        for (Iterator iter = rootGroup.getContainedItems().iterator(); iter.hasNext();) {
            ContactListItem item = (ContactListItem) iter.next();
            if (item instanceof Group) {
                Group grp = (Group) item;
                for (Iterator grpiter = grp.getContainedItems().iterator(); grpiter.hasNext();) {
                    ContactListItem cntct = (ContactListItem) grpiter.next();
                    if (cntct.getId().equals(userId))
                        return (Contact) cntct;
                }
            }
        }

        return null;
    }
}
