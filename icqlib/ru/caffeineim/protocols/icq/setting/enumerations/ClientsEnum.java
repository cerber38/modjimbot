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
package ru.caffeineim.protocols.icq.setting.enumerations;

import ru.caffeineim.protocols.icq.setting.Capabilities;


/**
 * Detected clients enum
 *
 * @author Samolisov Pavel
 * @since  07.02.2009
 */
public class ClientsEnum {

    public static final byte CLI_NONE = 0;

    public static final byte CLI_QIP = 1;

    public static final byte CLI_MIRANDA = 2;

    public static final byte CLI_LICQ = 3;

    public static final byte CLI_TRILLIAN = 4;

    public static final byte CLI_SIM = 5;

    public static final byte CLI_KOPETE = 6;

    public static final byte CLI_MICQ = 7;

    public static final byte CLI_ANDRQ = 8;

    public static final byte CLI_IM2 = 9;

    public static final byte CLI_MACICQ = 10;

    public static final byte CLI_AIM = 11;

    public static final byte CLI_UIM = 12;

    public static final byte CLI_WEBICQ = 13;

    public static final byte CLI_GAIM = 14;

    public static final byte CLI_ALICQ = 15;

    public static final byte CLI_STRICQ = 16;

    public static final byte CLI_YSM = 17;

    public static final byte CLI_VICQ = 18;

    public static final byte CLI_LIBICQ2000 = 19;

    public static final byte CLI_JIMM = 20;

    public static final byte CLI_SMARTICQ = 21;

    public static final byte CLI_ICQLITE4 = 22;

    public static final byte CLI_ICQLITE5 = 23;

    public static final byte CLI_ICQ98 = 24;

    public static final byte CLI_ICQ99 = 25;

    public static final byte CLI_ICQ2001B = 26;

    public static final byte CLI_ICQ2002A2003A = 27;

    public static final byte CLI_ICQ2000 = 28;

    public static final byte CLI_ICQ2003B = 29;

    public static final byte CLI_ICQLITE = 30;

    public static final byte CLI_GNOMEICQ = 31;

    public static final byte CLI_AGILE = 32;

    public static final byte CLI_SPAM = 33;

    public static final byte CLI_CENTERICQ = 34;

    public static final byte CLI_LIBICQJABBER = 35;

    public static final byte CLI_ICQ2GO = 36;

    public static final byte CLI_ICQPPC = 37;

    public static final byte CLI_STICQ = 38;

    public static final byte CLI_MCHAT = 39;

    private static final String[] clientNames = { "Not detected", "QIP",
            "Miranda", "LIcq", "Trillian", "SIM", "Kopete", "MICQ", "&RQ",
            "IM2", "ICQ for MAC", "AIM", "UIM", "WebICQ", "Gaim", "Alicq",
            "StrICQ", "YSM", "vICQ", "Libicq2000", "Jimm", "SmartICQ",
            "ICQ Lite v4", "ICQ Lite v5", "ICQ 98", "ICQ 99", "ICQ 2001b",
            "ICQ 2002a/2003a", "ICQ 2000", "ICQ 2003b", "ICQ Lite",
            "Gnome ICQ", "Agile Messenger", "SPAM:)", "CenterICQ",
            "Libicq2000 from Jabber", "ICQ2GO!", "ICQ for Pocket PC", "StIcq", ""
    };

    private int type = 0;

    private int caps = Capabilities.CAPF_NO_INTERNAL;

    public ClientsEnum(int type, int caps) {
        this.type = type;
        this.caps = caps;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCaps() {
        return caps;
    }

    public void setCaps(int caps) {
        this.caps = caps;
    }

    public String toString() {
        return clientNames[type];
    }
}
