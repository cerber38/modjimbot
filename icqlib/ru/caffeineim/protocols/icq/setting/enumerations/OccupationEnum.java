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
public class OccupationEnum {

    public static final int UNSPECIFIED = 0;
    public static final int ACADEMIC = 1;
    public static final int ADMINISTRATIVE = 2;
    public static final int ART_ENTERTAIMENT = 3;
    public static final int COLLEGE_STUDENT = 4;
    public static final int COMPUTERS = 5;
    public static final int COMMUNITY = 6;
    public static final int EDUCATION = 7;
    public static final int ENGINEERING = 8;
    public static final int FINANCIAL_SERVICES = 9;
    public static final int GOVERNMENT = 10;
    public static final int HIGH_SCHOOL_STUDENT = 11;
    public static final int HOME = 12;
    public static final int ICQ_PROVIDING_HELP = 13;
    public static final int LAW = 14;
    public static final int MANAGERIAL = 15;
    public static final int MANUFACTURING = 16;
    public static final int MEDICAL = 17;
    public static final int MILITARY = 18;
    public static final int NON_GOVERNMENT = 19;
    public static final int PROFESSIONAL = 20;
    public static final int RETAIL = 21;
    public static final int RETIRED = 22;
    public static final int SCIENCE = 23;
    public static final int SPORTS = 24;
    public static final int TECHNICAL = 25;
    public static final int UNIVERSITY_STUDENT = 26;
    public static final int WEB_BUILDING = 27;
    public static final int OTHER = 99;

    private static EnumerationsMap allOccupations = new EnumerationsMap();
    static {
        allOccupations.put(0, "Unspecified");
        allOccupations.put(1, "Academic");
        allOccupations.put(2, "Administrative");
        allOccupations.put(3, "Art/Entertainment");
        allOccupations.put(4, "College Student");
        allOccupations.put(5, "Computers");
        allOccupations.put(6, "Community & Social");
        allOccupations.put(7, "Education");
        allOccupations.put(8, "Engineering");
        allOccupations.put(9, "Financial Services");
        allOccupations.put(10, "Government");
        allOccupations.put(11, "High School Student");
        allOccupations.put(12, "Home");
        allOccupations.put(13, "ICQ - Providing Help");
        allOccupations.put(14, "Law");
        allOccupations.put(15, "Managerial");
        allOccupations.put(16, "Manufacturing");
        allOccupations.put(17, "Medical/Health");
        allOccupations.put(18, "Military");
        allOccupations.put(19, "Non-Government Organization");
        allOccupations.put(20, "Professional");
        allOccupations.put(21, "Retail");
        allOccupations.put(22, "Retired");
        allOccupations.put(23, "Science & Research");
        allOccupations.put(24, "Sports");
        allOccupations.put(25, "Technical");
        allOccupations.put(26, "University Student");
        allOccupations.put(27, "Web Building");
        allOccupations.put(99, "Other Services");
    }

    private int occupation;

    public OccupationEnum(int occupation) {
        this.occupation = occupation;
    }

    public int getOccupation() {
        return occupation;
    }

    public String toString() {
        if (allOccupations.containsKey(getOccupation())) {
            return (String) allOccupations.get(getOccupation());
        }
        else {
            return "";
        }
    }

    /**
     *
     * @return all occupations as map
     */
    public static Map getAllOccupationsMap() {
        return allOccupations;
    }

    /**
     *
     * @return all occupations as String array (sort by Alphabetical)
     */
    public static String[] getAllOccupations() {
        return (String[]) allOccupations.values().toArray(new String[allOccupations.size()]);
    }
}
