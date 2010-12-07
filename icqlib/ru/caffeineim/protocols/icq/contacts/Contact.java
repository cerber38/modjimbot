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

import java.io.Serializable;
import java.util.Iterator;

import ru.caffeineim.protocols.icq.Item;
import ru.caffeineim.protocols.icq.Tlv;
import ru.caffeineim.protocols.icq.setting.enumerations.StatusModeEnum;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 *   @author Samolisov Pavel
 */
public class Contact extends ContactListItem implements Serializable {

    private static final long serialVersionUID = -4126754352326743822L;

    private boolean isInVisibleList;

    private boolean isInInvisibleList;

    private String firstName = "";

    private String lastName = "";

    private String nickName = "";

    private String email = "";

    private String SMSNumber = "";

    private String comment = "";

    private transient StatusModeEnum currentStatus;


    /**
     * Create a new minimalistic Contact. By default, the contact isn't in the
     * invisible list nor in the visible one.
     */
    public Contact(Item item) {
        super(item);
        this.isInVisibleList = false;
        this.isInInvisibleList = (item.getType() == Item.TYPE_IGNORE_LIST);
        this.currentStatus = new StatusModeEnum(StatusModeEnum.OFFLINE);

        Iterator tlvIter = item.getTlvsIterator();
        while (tlvIter.hasNext()) {
            Tlv tlv = (Tlv) tlvIter.next();
            switch (tlv.getType()) {
            case 0x006d:
                break;
            case 0x0131:
                setNickName(tlv.getStringValue());
                break;
            case 0x0137:
                setEmail(tlv.getStringValue());
                break;
            case 0x013a:
                setSMS(tlv.getStringValue());
                break;
            case 0x013c:
                setComment(tlv.getStringValue());
            case 0x013d:
            case 0x013e:
            case 0x0145:
            default:
                break;
            }
        }
    }

    public Contact(short itemid, short groupid, String uin) {
        super(new Item(itemid, groupid, uin));
        setNickName(uin);
    }

    public boolean getIsInVisibleList() {
        return isInVisibleList;
    }

    public void setIsInVisibleList(boolean isIn) {
        this.isInVisibleList = isIn;
    }

    public boolean getIsInInvisibleList() {
        return isInInvisibleList;
    }

    public void setIsInInvisibleList(boolean isIn) {
        this.isInInvisibleList = isIn;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSMS() {
        return SMSNumber;
    }

    public void setSMS(String sms) {
        this.SMSNumber = sms;
    }

    public StatusModeEnum getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(StatusModeEnum status) {
        this.currentStatus = status;
    }

    public boolean equals(Object o) {
        return getId().equals(((Contact) o).getId());
    }

    public String toString() {
        return getNickName() + "(" + getId() + ")";
    }
}
