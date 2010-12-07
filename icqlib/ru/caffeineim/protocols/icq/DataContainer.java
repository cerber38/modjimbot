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

import java.util.Vector;

/**
 * <p>Created by
 *   @author Fabrice Michellonet
 */
public abstract class DataContainer extends DataField {

    private Vector dataFieldVector;
    private byte[] dataFieldByteAray;
    private int dataFieldLen;
    private boolean dataFieldModified;
    protected boolean headerModified;

    public DataContainer() {
        dataFieldVector = new Vector(0);
        dataFieldLen = 0;
        dataFieldModified = true;
        headerModified = true;
    }

    public abstract byte[] getHeaderByteArray();

    public void addDataField(DataField data) {
        dataFieldVector.addElement(data);
        dataFieldLen += data.getByteArray().length;
        dataFieldModified = true;
    }

    protected Object elementAt(int i) {
        return dataFieldVector.elementAt(i);
    }

    public byte[] getDataFieldByteArray() {
        if (dataFieldModified) {
            int position = 0;
            dataFieldByteAray = new byte[dataFieldLen];
            /* Adding data fields */
            for (int i = 0; i < dataFieldVector.size(); i++) {
                DataField obj = (DataField) dataFieldVector.elementAt(i);
                System.arraycopy(obj.getByteArray(), 0, dataFieldByteAray, position,
                         obj.getByteArray().length);
                position += obj.getByteArray().length;
            }
            dataFieldModified = false;
        }

        return dataFieldByteAray;
    }

    public byte[] getByteArray() {
        if (headerModified || dataFieldModified) {
            byteArray = new byte[getHeaderByteArray().length + dataFieldLen];
            /* Adding Header fields */
            System.arraycopy(getHeaderByteArray(), 0, byteArray, 0,
                       getHeaderByteArray().length);
            /* Adding data fields */
            System.arraycopy(getDataFieldByteArray(), 0, byteArray,
                       getHeaderByteArray().length,
                       getDataFieldByteArray().length);
            headerModified = false;
        }

        return byteArray;
    }
}
