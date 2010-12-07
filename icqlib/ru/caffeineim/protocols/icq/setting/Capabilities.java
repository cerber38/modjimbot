/**
 * Copyright 2008 Caffeine-Soft Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.caffeineim.protocols.icq.setting;

import ru.caffeineim.protocols.icq.setting.enumerations.ClientsEnum;
import ru.caffeineim.protocols.icq.tool.BytesTools;


/**
 * Detecting and parsing client's capabilities
 * @author Pavel Samolisov
 * @author Egor Baranov
 * @author Manuel Linsmayer
 * @author Andreas Rossbacher
 * @author Sergey Chernov
 * @author Andrey B. Ivlev
 * @since  07.02.2009
 */
public class Capabilities {

    // No capability
    public static final int CAPF_NO_INTERNAL = 0x00000000;

    // Client unterstands type-2 messages
    public static final int CAPF_AIM_SERVERRELAY_INTERNAL = 0x00000001;

    // Client unterstands UTF-8 messages
    public static final int CAPF_UTF8_INTERNAL = 0x00000002;

    // Client capabilities for detection
    public static final int CAPF_MIRANDAIM = 0x00000004;

    public static final int CAPF_TRILLIAN = 0x00000008;

    public static final int CAPF_TRILCRYPT = 0x00000010;

    public static final int CAPF_SIM = 0x00000020;

    public static final int CAPF_SIMOLD = 0x00000040;

    public static final int CAPF_LICQ = 0x00000080;

    public static final int CAPF_KOPETE = 0x00000100;

    public static final int CAPF_MICQ = 0x00000200;

    public static final int CAPF_ANDRQ = 0x00000400;

    public static final int CAPF_QIP = 0x000000800;

    public static final int CAPF_IM2 = 0x00001000;

    public static final int CAPF_MACICQ = 0x00002000;

    public static final int CAPF_RICHTEXT = 0x00004000;

    public static final int CAPF_IS2001 = 0x00008000;

    public static final int CAPF_IS2002 = 0x00010000;

    public static final int CAPF_STR20012 = 0x00020000;

    public static final int CAPF_AIMICON = 0x00040000;

    public static final int CAPF_AIMCHAT = 0x00080000;

    public static final int CAPF_UIM = 0x00100000;

    public static final int CAPF_RAMBLER = 0x00200000;

    public static final int CAPF_ABV = 0x00400000;

    public static final int CAPF_NETVIGATOR = 0x00800000;

    public static final int CAPF_XTRAZ = 0x01000000;

    public static final int CAPF_AIMFILE = 0x02000000;

    public static final int CAPF_JIMM = 0x04000000;


    public static final int CAPF_AIMIMIMAGE = 0x08000000;

    public static final int CAPF_AVATAR = 0x10000000;

    public static final int CAPF_DIRECT = 0x20000000;

    public static final int CAPF_TYPING = 0x40000000;

    public static final int CAPF_MCHAT = 0x80000000;

    private static final byte[] CAP_OLD_HEAD = { (byte) 0x09, (byte) 0x46 };

    private static final byte[] CAP_OLD_TAIL = { (byte) 0x4C, (byte) 0x7F,
            (byte) 0x11, (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44,
            (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    public static final byte[] CAP_AIM_SERVERRELAY = { (byte) 0x09,
            (byte) 0x46, (byte) 0x13, (byte) 0x49, (byte) 0x4C, (byte) 0x7F,
            (byte) 0x11, (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44,
            (byte) 0x45, (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    public static final byte[] CAP_UTF8 = { (byte) 0x09, (byte) 0x46,
            (byte) 0x13, (byte) 0x4E, (byte) 0x4C, (byte) 0x7F, (byte) 0x11,
            (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    public static final byte[] CAP_UTF8_GUID = { (byte) 0x7b, (byte) 0x30,
            (byte) 0x39, (byte) 0x34, (byte) 0x36, (byte) 0x31, (byte) 0x33,
            (byte) 0x34, (byte) 0x45, (byte) 0x2D, (byte) 0x34, (byte) 0x43,
            (byte) 0x37, (byte) 0x46, (byte) 0x2D, (byte) 0x31, (byte) 0x31,
            (byte) 0x44, (byte) 0x31, (byte) 0x2D, (byte) 0x38, (byte) 0x32,
            (byte) 0x32, (byte) 0x32, (byte) 0x2D, (byte) 0x34, (byte) 0x34,
            (byte) 0x34, (byte) 0x35, (byte) 0x35, (byte) 0x33, (byte) 0x35,
            (byte) 0x34, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,
            (byte) 0x7D };

    private static final byte[] CAP_MIRANDAIM = { (byte) 0x4D, (byte) 0x69,
            (byte) 0x72, (byte) 0x61, (byte) 0x6E, (byte) 0x64, (byte) 0x61,
            (byte) 0x4D, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    private static final byte[] CAP_TRILLIAN = { (byte) 0x97, (byte) 0xb1,
            (byte) 0x27, (byte) 0x51, (byte) 0x24, (byte) 0x3c, (byte) 0x43,
            (byte) 0x34, (byte) 0xad, (byte) 0x22, (byte) 0xd6, (byte) 0xab,
            (byte) 0xf7, (byte) 0x3f, (byte) 0x14, (byte) 0x09 };

    private static final byte[] CAP_TRILCRYPT = { (byte) 0xf2, (byte) 0xe7,
            (byte) 0xc7, (byte) 0xf4, (byte) 0xfe, (byte) 0xad, (byte) 0x4d,
            (byte) 0xfb, (byte) 0xb2, (byte) 0x35, (byte) 0x36, (byte) 0x79,
            (byte) 0x8b, (byte) 0xdf, (byte) 0x00, (byte) 0x00 };

    private static final byte[] CAP_SIM = { 'S', 'I', 'M', ' ', 'c', 'l', 'i',
            'e', 'n', 't', ' ', ' ', (byte) 0, (byte) 0, (byte) 0, (byte) 0 };

    private static final byte[] CAP_SIMOLD = { (byte) 0x97, (byte) 0xb1,
            (byte) 0x27, (byte) 0x51, (byte) 0x24, (byte) 0x3c, (byte) 0x43,
            (byte) 0x34, (byte) 0xad, (byte) 0x22, (byte) 0xd6, (byte) 0xab,
            (byte) 0xf7, (byte) 0x3f, (byte) 0x14, (byte) 0x00 };

    private static final byte[] CAP_LICQ = { 'L', 'i', 'c', 'q', ' ', 'c', 'l',
            'i', 'e', 'n', 't', ' ', (byte) 0, (byte) 0, (byte) 0, (byte) 0 };

    private static final byte[] CAP_KOPETE = { 'K', 'o', 'p', 'e', 't', 'e',
            ' ', 'I', 'C', 'Q', ' ', ' ', (byte) 0, (byte) 0, (byte) 0,
            (byte) 0 };

    private static final byte[] CAP_MICQ = { 'm', 'I', 'C', 'Q', ' ',
            (byte) 0xA9, ' ', 'R', '.', 'K', '.', ' ', (byte) 0, (byte) 0,
            (byte) 0, (byte) 0 };

    private static final byte[] CAP_ANDRQ = { '&', 'R', 'Q', 'i', 'n', 's',
            'i', 'd', 'e', (byte) 0, (byte) 0, (byte) 0, (byte) 0, (byte) 0,
            (byte) 0, (byte) 0 };

    private static final byte[] CAP_QIP = { (byte) 0x56, (byte) 0x3F,
            (byte) 0xC8, (byte) 0x09, (byte) 0x0B, (byte) 0x6F, (byte) 0x41,
            'Q', 'I', 'P', ' ', '2', '0', '0', '5', 'a' };

    private static final byte[] CAP_IM2 = { (byte) 0x74, (byte) 0xED,
            (byte) 0xC3, (byte) 0x36, (byte) 0x44, (byte) 0xDF, (byte) 0x48,
            (byte) 0x5B, (byte) 0x8B, (byte) 0x1C, (byte) 0x67, (byte) 0x1A,
            (byte) 0x1F, (byte) 0x86, (byte) 0x09, (byte) 0x9F }; // IM2 Ext

    private static final byte[] CAP_MACICQ = { (byte) 0xdd, (byte) 0x16,
            (byte) 0xf2, (byte) 0x02, (byte) 0x84, (byte) 0xe6, (byte) 0x11,
            (byte) 0xd4, (byte) 0x90, (byte) 0xdb, (byte) 0x00, (byte) 0x10,
            (byte) 0x4b, (byte) 0x9b, (byte) 0x4b, (byte) 0x7d };

    private static final byte[] CAP_RICHTEXT = { (byte) 0x97, (byte) 0xb1,
            (byte) 0x27, (byte) 0x51, (byte) 0x24, (byte) 0x3c, (byte) 0x43,
            (byte) 0x34, (byte) 0xad, (byte) 0x22, (byte) 0xd6, (byte) 0xab,
            (byte) 0xf7, (byte) 0x3f, (byte) 0x14, (byte) 0x92 };

    private static final byte[] CAP_IS2001 = { (byte) 0x2e, (byte) 0x7a,
            (byte) 0x64, (byte) 0x75, (byte) 0xfa, (byte) 0xdf, (byte) 0x4d,
            (byte) 0xc8, (byte) 0x88, (byte) 0x6f, (byte) 0xea, (byte) 0x35,
            (byte) 0x95, (byte) 0xfd, (byte) 0xb6, (byte) 0xdf };

    private static final byte[] CAP_IS2002 = { (byte) 0x10, (byte) 0xcf,
            (byte) 0x40, (byte) 0xd1, (byte) 0x4c, (byte) 0x7f, (byte) 0x11,
            (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    private static final byte[] CAP_STR20012 = { (byte) 0xa0, (byte) 0xe9,
            (byte) 0x3f, (byte) 0x37, (byte) 0x4f, (byte) 0xe9, (byte) 0xd3,
            (byte) 0x11, (byte) 0xbc, (byte) 0xd2, (byte) 0x00, (byte) 0x04,
            (byte) 0xac, (byte) 0x96, (byte) 0xdd, (byte) 0x96 };

    private static final byte[] CAP_AIMICON = { (byte) 0x09, (byte) 0x46,
            (byte) 0x13, (byte) 0x46, (byte) 0x4c, (byte) 0x7f, (byte) 0x11,
            (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 }; // CAP_AIM_BUDDYICON

    private static final byte[] CAP_AIMIMIMAGE = { (byte) 0x09, (byte) 0x46,
            (byte) 0x13, (byte) 0x45, (byte) 0x4c, (byte) 0x7f, (byte) 0x11,
            (byte) 0xd1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 }; // CAP_AIM_BUDDYICON

    private static final byte[] CAP_AIMCHAT = { (byte) 0x74, (byte) 0x8F,
            (byte) 0x24, (byte) 0x20, (byte) 0x62, (byte) 0x87, (byte) 0x11,
            (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    private static final byte[] CAP_UIM = { (byte) 0xA7, (byte) 0xE4,
            (byte) 0x0A, (byte) 0x96, (byte) 0xB3, (byte) 0xA0, (byte) 0x47,
            (byte) 0x9A, (byte) 0xB8, (byte) 0x45, (byte) 0xC9, (byte) 0xE4,
            (byte) 0x67, (byte) 0xC5, (byte) 0x6B, (byte) 0x1F };

    private static final byte[] CAP_RAMBLER = { (byte) 0x7E, (byte) 0x11,
            (byte) 0xB7, (byte) 0x78, (byte) 0xA3, (byte) 0x53, (byte) 0x49,
            (byte) 0x26, (byte) 0xA8, (byte) 0x02, (byte) 0x44, (byte) 0x73,
            (byte) 0x52, (byte) 0x08, (byte) 0xC4, (byte) 0x2A };

    private static final byte[] CAP_ABV = { (byte) 0x00, (byte) 0xE7,
            (byte) 0xE0, (byte) 0xDF, (byte) 0xA9, (byte) 0xD0, (byte) 0x4F,
            (byte) 0xe1, (byte) 0x91, (byte) 0x62, (byte) 0xC8, (byte) 0x90,
            (byte) 0x9A, (byte) 0x13, (byte) 0x2A, (byte) 0x1B };

    private static final byte[] CAP_NETVIGATOR = { (byte) 0x4C, (byte) 0x6B,
            (byte) 0x90, (byte) 0xA3, (byte) 0x3D, (byte) 0x2D, (byte) 0x48,
            (byte) 0x0E, (byte) 0x89, (byte) 0xD6, (byte) 0x2E, (byte) 0x4B,
            (byte) 0x2C, (byte) 0x10, (byte) 0xD9, (byte) 0x9F };

    private static final byte[] CAP_XTRAZ = { (byte) 0x1A, (byte) 0x09,
            (byte) 0x3C, (byte) 0x6C, (byte) 0xD7, (byte) 0xFD, (byte) 0x4E,
            (byte) 0xC5, (byte) 0x9D, (byte) 0x51, (byte) 0xA6, (byte) 0x47,
            (byte) 0x4E, (byte) 0x34, (byte) 0xF5, (byte) 0xA0 };

    private static final byte[] CAP_AIMFILE = { (byte) 0x09, (byte) 0x46,
            (byte) 0x13, (byte) 0x43, (byte) 0x4C, (byte) 0x7F, (byte) 0x11,
            (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    private static final byte[] CAP_DIRECT = { (byte) 0x09, (byte) 0x46,
            (byte) 0x13, (byte) 0x44, (byte) 0x4C, (byte) 0x7F, (byte) 0x11,
            (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    private static final byte[] CAP_JIMM = { 'J', 'i', 'm', 'm', ' ' };

    private static final byte[] CAP_AVATAR = { (byte) 0x09, (byte) 0x46,
            (byte) 0x13, (byte) 0x4C, (byte) 0x4C, (byte) 0x7F, (byte) 0x11,
            (byte) 0xD1, (byte) 0x82, (byte) 0x22, (byte) 0x44, (byte) 0x45,
            (byte) 0x53, (byte) 0x54, (byte) 0x00, (byte) 0x00 };

    private static final byte[] CAP_TYPING = { (byte) 0x56, (byte) 0x3f,
            (byte) 0xc8, (byte) 0x09, (byte) 0x0b, (byte) 0x6f, (byte) 0x41,
            (byte) 0xbd, (byte) 0x9f, (byte) 0x79, (byte) 0x42, (byte) 0x26,
            (byte) 0x09, (byte) 0xdf, (byte) 0xa2, (byte) 0xf3 };

    private static final byte[] CAP_MCHAT = { 'm', 'C', 'h', 'a', 't', ' ',
            'i', 'c', 'q' };

    /**
     * Merge two received capabilities into one byte array
     * @param capabilities_old
     * @param capabilities_new
     * @return
     */
    public static byte[] mergeCapabilities(byte[] capabilities_old,
        byte[] capabilities_new) {

        if (capabilities_new == null)
            return capabilities_old;

        if (capabilities_old == null)
            return capabilities_new;

        // Extend new capabilities to match with old ones
        byte[] extended_new = new byte[capabilities_new.length * 8];
        for (int i = 0; i < capabilities_new.length; i += 2) {
            System.arraycopy(CAP_OLD_HEAD, 0, extended_new, (i * 8),
                    CAP_OLD_HEAD.length);
            System.arraycopy(capabilities_new, i, extended_new,
                    ((i * 8) + CAP_OLD_HEAD.length), 2);
            System.arraycopy(CAP_OLD_TAIL, 0, extended_new, ((i * 8)
                    + CAP_OLD_HEAD.length + 2), CAP_OLD_TAIL.length);
        }

        // Check for coexisting capabilities and merge
        boolean found = false;
        for (int i = 4; i < capabilities_old.length; i += 16) {
            byte[] tmp_old = new byte[16];
            System.arraycopy(capabilities_old, i, tmp_old, 0, 16);
            for (int j = 0; j < extended_new.length; j += 16) {
                byte[] tmp_new = new byte[16];
                System.arraycopy(extended_new, j, tmp_new, 0, 16);
                if (tmp_old == tmp_new) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                byte[] merged = new byte[extended_new.length + 16];
                System.arraycopy(extended_new, 0, merged, 0,
                        extended_new.length);
                System.arraycopy(tmp_old, 0, merged, extended_new.length,
                        tmp_old.length);
                extended_new = merged;
                found = false;
            }
        }

        return extended_new;
    }

    public static ClientsEnum detectUserClient(int dwFP1, int dwFP2, int dwFP3,
        final byte[] capabilities, int wVersion) {

        int client = ClientsEnum.CLI_NONE;
        String szVersion = "";
        int caps = CAPF_NO_INTERNAL;

        if (capabilities != null) {
            // Caps parsing
            for (int j = 0; j < capabilities.length / 16; j++) {
                if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_AIM_SERVERRELAY, 0, 16)) {
                    caps |= CAPF_AIM_SERVERRELAY_INTERNAL;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_UTF8, 0, 16)) {
                    caps |= CAPF_UTF8_INTERNAL;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_MIRANDAIM, 0, 8)) {
                    caps |= CAPF_MIRANDAIM;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_TRILLIAN, 0, 16)) {
                    caps |= CAPF_TRILLIAN;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_TRILCRYPT, 0, 16)) {
                    caps |= CAPF_TRILCRYPT;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_SIM, 0, 0xC)) {
                    caps |= CAPF_SIM;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_SIMOLD, 0, 16)) {
                    caps |= CAPF_SIMOLD;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_LICQ, 0, 0xC)) {
                    caps |= CAPF_LICQ;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_KOPETE, 0, 0xC)) {
                    caps |= CAPF_KOPETE;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_MICQ, 0, 16)) {
                    caps |= CAPF_MICQ;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_ANDRQ, 0, 9)) {
                    caps |= CAPF_ANDRQ;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_QIP, 0, 11)) {
                    caps |= CAPF_QIP;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_IM2, 0, 16)) {
                    caps |= CAPF_IM2;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_MACICQ, 0, 16)) {
                    caps |= CAPF_MACICQ;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_RICHTEXT, 0, 16)) {
                    caps |= CAPF_RICHTEXT;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_IS2001, 0, 16)) {
                    caps |= CAPF_IS2001;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_IS2002, 0, 16)) {
                    caps |= CAPF_IS2002;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_STR20012, 0, 16)) {
                    caps |= CAPF_STR20012;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_AIMICON, 0, 16)) {
                    caps |= CAPF_AIMICON;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_AIMCHAT, 0, 16)) {
                    caps |= CAPF_AIMCHAT;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_UIM, 0, 16)) {
                    caps |= CAPF_UIM;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_RAMBLER, 0, 16)) {
                    caps |= CAPF_RAMBLER;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_ABV, 0, 16)) {
                    caps |= CAPF_ABV;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_NETVIGATOR, 0, 16)) {
                    caps |= CAPF_NETVIGATOR;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_XTRAZ, 0, 16)) {
                    caps |= CAPF_XTRAZ;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_AIMFILE, 0, 16)) {
                    caps |= CAPF_AIMFILE;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_JIMM, 0, 5)) {
                    caps |= CAPF_JIMM;
                } else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_AIMIMIMAGE, 0, 16))
                    caps |= CAPF_AIMIMIMAGE;
                else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_AVATAR, 0, 16))
                    caps |= CAPF_AVATAR;
                else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_DIRECT, 0, 16))
                    caps |= CAPF_DIRECT;
                else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_TYPING, 0, 16))
                    caps |= CAPF_TYPING;
                else if (BytesTools.byteArrayEquals(capabilities, j * 16,
                    CAP_MCHAT, 0, 9)) {
                    caps |= CAPF_MCHAT;
                }
            }

            if ((caps & CAPF_MCHAT) != 0) {
                client = ClientsEnum.CLI_MCHAT;
            }

            if ((caps & CAPF_JIMM) != 0) {
                client = ClientsEnum.CLI_JIMM;
            }

            if ((caps & CAPF_QIP) != 0) {
                client = ClientsEnum.CLI_QIP;
                if (((dwFP1 >> 24) & 0xFF) != 0)
                    szVersion += " (" + ((dwFP1 >> 24) & 0xFF)
                        + ((dwFP1 >> 16) & 0xFF) + ((dwFP1 >> 8) & 0xFF)
                        + (dwFP1 & 0xFF) + ")";
            }

            if (((caps & (CAPF_TRILLIAN + CAPF_TRILCRYPT)) != 0)
                    && (dwFP1 == 0x3b75ac09)) {
                client = ClientsEnum.CLI_TRILLIAN;
            }

            if (((caps & CAPF_IM2) != 0) && (dwFP1 == 0x3FF19BEB)) {
                client = ClientsEnum.CLI_IM2;
            }

            if ((caps & (CAPF_SIM + CAPF_SIMOLD)) != 0) {
                client = ClientsEnum.CLI_SIM;
            }

            if ((caps & CAPF_KOPETE) != 0) {
                client = ClientsEnum.CLI_KOPETE;
            }

            if ((caps & CAPF_LICQ) != 0) {
                client = ClientsEnum.CLI_LICQ;
            }

            if (((caps & CAPF_AIMICON) != 0) && ((caps & CAPF_AIMFILE) != 0)
                    && ((caps & CAPF_AIMIMIMAGE) != 0)) {
                client = ClientsEnum.CLI_GAIM;
            }

            if ((caps & CAPF_UTF8_INTERNAL) != 0) {
                switch (wVersion) {
                    case 10:
                        if (((caps & CAPF_TYPING) != 0)
                                && ((caps & CAPF_RICHTEXT) != 0)) {
                            client = ClientsEnum.CLI_ICQ2003B;
                        }
                    case 7:
                        if (((caps & CAPF_AIM_SERVERRELAY_INTERNAL) == 0)
                                && ((caps & CAPF_DIRECT) == 0) && (dwFP1 == 0)
                                && (dwFP2 == 0) && (dwFP3 == 0)) {
                            client = ClientsEnum.CLI_ICQ2GO;
                        }
                        break;
                    default:
                        if ((dwFP1 == 0) && (dwFP2 == 0) && (dwFP3 == 0)) {
                            if ((caps & CAPF_RICHTEXT) != 0) {
                                client = ClientsEnum.CLI_ICQLITE;
                                if (((caps & CAPF_AVATAR) != 0)
                                        && ((caps & CAPF_XTRAZ) != 0)) {
                                    if ((caps & CAPF_AIMFILE) != 0)
                                        client = ClientsEnum.CLI_ICQLITE5;
                                    else
                                        client = ClientsEnum.CLI_ICQLITE4;
                                }
                            } else if ((caps & CAPF_UIM) != 0)
                                client = ClientsEnum.CLI_UIM;
                            else
                                client = ClientsEnum.CLI_AGILE;
                        }
                    break;
                }
            }

            if ((caps & CAPF_MACICQ) != 0) {
                client = ClientsEnum.CLI_MACICQ;
            }

            if ((caps & CAPF_AIMCHAT) != 0) {
                client = ClientsEnum.CLI_AIM;
            }

            if ((dwFP1 & 0xFF7F0000) == 0x7D000000) {
                client = ClientsEnum.CLI_LICQ;
                int ver = dwFP1 & 0xFFFF;
                if (ver % 10 != 0) {
                    szVersion = ver / 1000 + "." + (ver / 10) % 100 + "." + ver
                        % 10;
                } else {
                    szVersion = ver / 1000 + "." + (ver / 10) % 100;
                }
            }

            switch (dwFP1) {
                case 0xFFFFFFFF:
                    if ((dwFP3 == 0xFFFFFFFF) && (dwFP2 == 0xFFFFFFFF)) {
                        client = ClientsEnum.CLI_GAIM;
                        break;
                    }
                    if ((dwFP2 == 0) && (dwFP3 != 0xFFFFFFFF)) {
                        if (wVersion == 7) {
                            client = ClientsEnum.CLI_WEBICQ;
                            break;
                        }
                        if ((dwFP3 == 0x3B7248ED)
                                && ((caps & CAPF_UTF8_INTERNAL) == 0)
                                && ((caps & CAPF_RICHTEXT) == 0)) {
                            client = ClientsEnum.CLI_SPAM;
                            break;
                        }
                    }

                    client = ClientsEnum.CLI_MIRANDA;
                    szVersion = ((dwFP2 >> 24) & 0x7F) + "."
                        + ((dwFP2 >> 16) & 0xFF) + "." + ((dwFP2 >> 8) & 0xFF)
                        + "." + (dwFP2 & 0xFF);
                    break;

                case 0xFFFFFFFE:
                    if (dwFP3 == dwFP1) {
                        client = ClientsEnum.CLI_JIMM;
                    }
                    break;
                case 0xFFFFFF8F:
                    client = ClientsEnum.CLI_STRICQ;
                    break;
                case 0xFFFFFF42:
                    client = ClientsEnum.CLI_MICQ;
                    break;
                case 0xFFFFFFBE:
                    client = ClientsEnum.CLI_ALICQ;
                    break;
                case 0xFFFFFF7F:
                    client = ClientsEnum.CLI_ANDRQ;
                    szVersion = ((dwFP2 >> 24) & 0xFF) + "."
                        + ((dwFP2 >> 16) & 0xFF) + "." + ((dwFP2 >> 8) & 0xFF)
                        + "." + (dwFP2 & 0xFF);
                    break;
                case 0xFFFFFFAB:
                    client = ClientsEnum.CLI_YSM;
                    break;
                case 0x04031980:
                    client = ClientsEnum.CLI_VICQ;
                    break;
                case 0x3AA773EE:
                    if ((dwFP2 == 0x3AA66380) && (dwFP3 == 0x3A877A42)) {
                        if (wVersion == 7) {
                            if (((caps & CAPF_AIM_SERVERRELAY_INTERNAL) != 0)
                                    && ((caps & CAPF_DIRECT) != 0)) {
                                if ((caps & CAPF_RICHTEXT) != 0) {
                                    client = ClientsEnum.CLI_CENTERICQ;
                                    break;
                                }
                                client = ClientsEnum.CLI_LIBICQJABBER;
                            }
                        }
                        client = ClientsEnum.CLI_LIBICQ2000;
                    }
                    break;
                case 0x3b75ac09:
                    client = ClientsEnum.CLI_TRILLIAN;
                    break;
                case 0x3BA8DBAF: // FP2: 0x3BEB5373; FP3: 0x3BEB5262;
                    if (wVersion == 2)
                        client = ClientsEnum.CLI_STICQ;
                    break;
                case 0x3FF19BEB:
                    if ((wVersion == 8) && (dwFP1 == dwFP3)) // FP2:
                        // 0x3FEC05EB;
                        // FP3:
                        // 0x3FF19BEB;
                        client = ClientsEnum.CLI_IM2;
                    break;
                case 0x4201F414:
                    if (((dwFP2 & dwFP3) == dwFP1) && (wVersion == 8))
                        client = ClientsEnum.CLI_SPAM;
                    break;
            }

            if (client != ClientsEnum.CLI_NONE)
                if ((dwFP1 != 0) && (dwFP1 == dwFP3) && (dwFP3 == dwFP2)
                        && (caps == 0)) {
                    client = ClientsEnum.CLI_VICQ;
                }

            if (((caps & CAPF_AIM_SERVERRELAY_INTERNAL) != 0)
                && ((caps & CAPF_DIRECT) != 0)
                && ((caps & CAPF_UTF8_INTERNAL) != 0)
                && ((caps & CAPF_RICHTEXT) != 0)) {
                if ((dwFP1 != 0) && (dwFP2 != 0) && (dwFP3 != 0))
                    client = ClientsEnum.CLI_ICQ2002A2003A;
                }
            if (((caps & (CAPF_STR20012 + CAPF_AIM_SERVERRELAY_INTERNAL)) != 0)
                    && ((caps & CAPF_IS2001) != 0)) {
                if ((dwFP1 == 0) && (dwFP2 == 0) && (dwFP3 == 0)
                        && (wVersion == 0))
                    client = ClientsEnum.CLI_ICQPPC;
                else
                    client = ClientsEnum.CLI_ICQ2001B; // FP1: 1068985885; FP2:0;
                    // FP3:1068986138
            }

            if (wVersion == 7) {
                if (((caps & CAPF_AIM_SERVERRELAY_INTERNAL) != 0)
                        && ((caps & CAPF_DIRECT) != 0)) {
                    if ((dwFP1 == 0) && (dwFP2 == 0) && (dwFP3 == 0))
                        client = ClientsEnum.CLI_ANDRQ;
                    else
                        client = ClientsEnum.CLI_ICQ2000;

                } else if ((caps & CAPF_RICHTEXT) != 0) {
                    client = ClientsEnum.CLI_GNOMEICQ;
                }
            }

            if (dwFP1 > 0x35000000 && dwFP1 < 0x40000000) {
                switch (wVersion) {
                    case 6:
                        client = ClientsEnum.CLI_ICQ99;
                        break;
                    case 7:
                        client = ClientsEnum.CLI_ICQ2000;
                        break;
                    case 8:
                        client = ClientsEnum.CLI_ICQ2001B;
                        break;
                    case 9:
                        client = ClientsEnum.CLI_ICQLITE;
                        break;
                    case 10:
                        client = ClientsEnum.CLI_ICQ2003B;
                        break;
                }
            }
        }

        return new ClientsEnum(client, caps);
    }
}
