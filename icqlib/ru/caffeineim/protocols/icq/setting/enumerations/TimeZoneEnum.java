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
package ru.caffeineim.protocols.icq.setting.enumerations;

import java.util.Map;

/**
 * <p>Created by 26.03.2008
 *   @author Samolisov Pavel
 */
public class TimeZoneEnum {

    private static EnumerationsMap timezones = new EnumerationsMap();

    static {
        timezones.put(-100,"Unspecified");
        timezones.put(24  ,"GMT-12:00 Eniwetok; Kwajalein");
        timezones.put(23  ,"GMT-11:30");
        timezones.put(22  ,"GMT-11:00 Midway Island; Samoa");
        timezones.put(21  ,"GMT-10:30");
        timezones.put(20  ,"GMT-10:00 Hawaii");
        timezones.put(19  ,"GMT-9:30");
        timezones.put(18  ,"GMT-9:00 Alaska");
        timezones.put(17  ,"GMT-8:30");
        timezones.put(16  ,"GMT-8:00 Pacific Time; Tijuana");
        timezones.put(15  ,"GMT-7:30");
        timezones.put(14  ,"GMT-7:00 Arizona; Mountain Time");
        timezones.put(13  ,"GMT-6:30");
        timezones.put(12  ,"GMT-6:00 Central Time; Central America; Saskatchewan");
        timezones.put(11  ,"GMT-5:30");
        timezones.put(10  ,"GMT-5:00 Eastern Time; Bogota; Lima; Quito");
        timezones.put(9   ,"GMT-4:30");
        timezones.put(8   ,"GMT-4:00 Atlantic Time; Santiago; Caracas; La Paz");
        timezones.put(7   ,"GMT-3:30 Newfoundland");
        timezones.put(6   ,"GMT-3:00 Greenland; Buenos Aires; Georgetown");
        timezones.put(5   ,"GMT-2:30");
        timezones.put(4   ,"GMT-2:00 Mid-Atlantic");
        timezones.put(3   ,"GMT-1:30");
        timezones.put(2   ,"GMT-1:00 Cape Verde Islands; Azores");
        timezones.put(1   ,"GMT-0:30");
        timezones.put(0   ,"GMT+0:00 London; Dublin; Edinburgh; Lisbon; Casablanca");
        timezones.put(-1  ,"GMT+0:30");
        timezones.put(-2  ,"GMT+1:00 Central European Time; West Central Africa; Warsaw");
        timezones.put(-3  ,"GMT+1:30");
        timezones.put(-4  ,"GMT+2:00 Jerusalem; Helsinki; Harare; Cairo; Bucharest; Athens");
        timezones.put(-5  ,"GMT+2:30");
        timezones.put(-6  ,"GMT+3:00 Moscow; St. Petersburg; Nairobi; Kuwait; Baghdad");
        timezones.put(-7  ,"GMT+3:30 Tehran");
        timezones.put(-8  ,"GMT+4:00 Baku; Tbilisi; Yerevan; Abu Dhabi; Muscat");
        timezones.put(-9  ,"GMT+4:30 Kabul");
        timezones.put(-10 ,"GMT+5:00 Calcutta; Chennai; Mumbai; New Delhi; Ekaterinburg");
        timezones.put(-11 ,"GMT+5:30");
        timezones.put(-12 ,"GMT+6:00 Astana; Dhaka; Almaty; Novosibirsk; Sri Jayawardenepura");
        timezones.put(-13 ,"GMT+6:30 Rangoon");
        timezones.put(-14 ,"GMT+7:00 Bankok; Hanoi; Jakarta; Krasnoyarsk");
        timezones.put(-15 ,"GMT+7:30");
        timezones.put(-16 ,"GMT+8:00 Perth; Taipei; Singapore; Hong Kong; Beijing");
        timezones.put(-17 ,"GMT+8:30");
        timezones.put(-18 ,"GMT+9:00 Tokyo; Osaka; Seoul; Sapporo; Yakutsk");
        timezones.put(-19 ,"GMT+9:30 Darwin; Adelaide");
        timezones.put(-20 ,"GMT+10:00 East Australia; Guam; Vladivostok");
        timezones.put(-21 ,"GMT+10:30");
        timezones.put(-22 ,"GMT+11:00 Magadan; Solomon Is.; New Caledonia");
        timezones.put(-23 ,"GMT+11:30");
        timezones.put(-24 ,"GMT+12:00 Auckland; Wellington; Fiji; Kamchatka; Marshall Is.");
    }

    private int timezone;

    public TimeZoneEnum(int timezone) {
        this.timezone = timezone;
    }

    public int getTimeZone() {
        return timezone;
    }

    public String toString() {
        if (timezones.containsKey(getTimeZone())) {
            return (String) timezones.get(getTimeZone());
        }
        else {
            return "";
        }
    }

    /**
     *
     * @return all timezones as map
     */
    public static Map getAllTimeZonesMap() {
        return timezones;
    }

    /**
     *
     * @return all timezones as String array (sort by Alphabetical)
     */
    public static String[] getAllTimeZones() {
        return (String[]) timezones.values().toArray(new String[timezones.size()]);
    }
}
