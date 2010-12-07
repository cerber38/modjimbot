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
package ru.caffeineim.protocols.icq.packet.sent.authorization;

import ru.caffeineim.protocols.icq.Flap;
import ru.caffeineim.protocols.icq.RawData;
import ru.caffeineim.protocols.icq.Snac;
import ru.caffeineim.protocols.icq.Tlv;

/**
 * <p> Created by
 * @author Egor Baranov
 */
public class UINRegistration extends Flap {
    private final static byte[] REGISTRATION_REQUEST_BEGIN = { 0x00, 0x00,
            0x00, 0x00, 0x28, 0x00, 0x03, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00,

            0x03, 0x46, 0x00, 0x00, 0x03, 0x46, 0x00, 0x00,

            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, };

    private final static byte[] REGISTRATION_REQUEST_END = { 0x00, 0x03, 0x46,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x11, 0x01 };

    public UINRegistration(String password) {
        super(2);

        byte[] passwd_lng = getPasswordLng(password);

        byte[] regData = new byte[REGISTRATION_REQUEST_BEGIN.length
                + password.toCharArray().length
                + REGISTRATION_REQUEST_END.length + 2];

        for (int i = 0; i < REGISTRATION_REQUEST_BEGIN.length; i++) {
            regData[i] = REGISTRATION_REQUEST_BEGIN[i];
        }

        regData[REGISTRATION_REQUEST_BEGIN.length] = passwd_lng[0];
        regData[REGISTRATION_REQUEST_BEGIN.length + 1] = passwd_lng[1];

        for (int i = 0; i < password.toCharArray().length; i++) {
            regData[i + REGISTRATION_REQUEST_BEGIN.length + 2] = (byte) password
                    .toCharArray()[i];
        }

        for (int i = 0; i < REGISTRATION_REQUEST_END.length; i++) {
            regData[i + REGISTRATION_REQUEST_BEGIN.length
                    + password.toCharArray().length + 2] = REGISTRATION_REQUEST_END[i];
        }

        Snac regSnac = new Snac(0x17, 0x04, 0, 0, 0);
        Tlv regTlv = new Tlv(new RawData(regData), 0x01);

        regSnac.addTlvToSnac(regTlv);
    }

    protected byte[] getPasswordLng(String password) {
        short lng = (short) password.length();
        RawData lngData = new RawData(lng, RawData.DWORD_LENGHT);
        lngData.invertIndianness();
        return lngData.getByteArray();
    }

    protected byte[] getPasswordArray(String password) {
        byte[] lng = getPasswordLng(password);

        byte[] result = new byte[lng.length + password.length()];

        for (int i = 0; i < lng.length; i++) {
            result[i] = lng[i];
        }

        for (int i = 0; i < password.length(); i++) {
            result[lng.length + i] = (byte) password.charAt(i);
        }

        return result;
    }
}
