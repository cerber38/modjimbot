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
package ru.caffeineim.protocols.icq;

import java.util.Iterator;

import ru.caffeineim.protocols.icq.exceptions.ConvertStringException;
import ru.caffeineim.protocols.icq.tool.StringTools;

/**
 * <p>Created by
 *   @author Lo�c Broquet
 */
public class Item extends DataField {

    public static final short TYPE_CONTACT = 0x0000;
    public static final short TYPE_GROUP = 0x0001;
    public static final short TYPE_VISIBLE = 0x0002;
    public static final short TYPE_INVISIBLE = 0x0003;
    public static final short TYPE_VISIBILITY = 0x0004;
    public static final short TYPE_PRESENCE_INFO = 0x0005;
    public static final short TYPE_ICQTIC = 0x0009;
    public static final short TYPE_IGNORE_LIST = 0x000e;
    public static final short TYPE_LAST_UPDATE_DATE = 0x000f;
    public static final short TYPE_SMS_CONTACT = 0x0010;
    public static final short TYPE_IMPORT_TIME = 0x0013;
    public static final short TYPE_CONTACT_ICON = 0x0014;

    // Signification du Tlv de type "visibility (0x0004)"
    public static final byte VISIBILITY_TLV_ALLOW_ALL = 0x01;
    public static final byte VISIBILITY_TLV_BLOCK_ALL = 0x02;
    public static final byte VISIBILITY_TLV_ALLOW_LIST = 0x03;
    public static final byte VISIBILITY_TLV_BLOCK_LIST = 0x04;
    public static final byte VISIBILITY_TLV_ALLOW_CONTACT = 0x05;

    private short nameLen;
    private String name;
    private short group;
    private short id;
    private Tlv itemTlv;

    public Item (short id, short groupid, String name) {
        this.id = id;
        this.group = groupid;
        this.name = name;
        this.nameLen = (short) name.length();
    }

    public Item(byte data[], int offset) throws ConvertStringException {
        int index = offset;
        nameLen = (short) new RawData(data, index, RawData.WORD_LENGHT).getValue();
        index += RawData.WORD_LENGHT;
        name = StringTools.utf8ByteArrayToString(data, index, nameLen);
        index += nameLen;
        group = (short) new RawData(data, index, RawData.WORD_LENGHT).getValue();
        index += RawData.WORD_LENGHT;
        id = (short) new RawData(data, index, RawData.WORD_LENGHT).getValue();
        index += RawData.WORD_LENGHT;
        itemTlv = new Tlv(data, index);
        byteArray = new byte[getLength()];
        System.arraycopy(data, offset, byteArray, 0, byteArray.length);
    }

    public int getLength() {
        return itemTlv.getByteArray().length + nameLen + 6;
    }

    public String getName() {
        return name;
    }

    public short getGroup() {
        return group;
    }

    public short getId() {
        return id;
    }

    public short getType() {
        return (short)itemTlv.getType();
    }

    public Iterator getTlvsIterator() {
        return new Iterator() {
            private int offset = 0;

            private final int offsetMax = itemTlv.getDataFieldByteArray().length;

            public boolean hasNext() {
                return offset < offsetMax;
            }

            public Object next() {
                Tlv retTlv = new Tlv(itemTlv.getDataFieldByteArray(), offset);
                offset += retTlv.getByteArray().length;
                return retTlv;
            }

            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    protected byte[] getByteArray() {
        byte[] retArray = new byte[byteArray.length];
        System.arraycopy(byteArray, 0, retArray, 0, retArray.length);
        return retArray;
    }
}
