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
 * <p>Created by 30.03.2008
 *   @author Samolisov Pavel
 */
public class MaritalStatusEnum {

    public static final int UNSPECIFIED = 0;
    public static final int SINGLE = 10;
    public static final int CLOSE_RELATIONSHIPS = 11;
    public static final int ENGAGED = 12;
    public static final int MARRIED = 20;
    public static final int DIVORCED = 30;
    public static final int SEPARATED = 31;
    public static final int WIDOWED = 40;

    private static EnumerationsMap allMaritalStatuses = new EnumerationsMap();
    static {
        allMaritalStatuses.put(UNSPECIFIED, "Unspecified");
        allMaritalStatuses.put(SINGLE, "Single");
        allMaritalStatuses.put(CLOSE_RELATIONSHIPS, "Close Relationships");
        allMaritalStatuses.put(ENGAGED, "Engaged");
        allMaritalStatuses.put(MARRIED, "Married");
        allMaritalStatuses.put(DIVORCED, "Divorced");
        allMaritalStatuses.put(SEPARATED, "Separated");
        allMaritalStatuses.put(WIDOWED, "Widowed");
    }

    private int maritalstatus;

    public MaritalStatusEnum(int maritalstatus) {
        this.maritalstatus = maritalstatus;
    }

    public int getMaritalStatus() {
        return maritalstatus;
    }

    public String toString() {
        if (allMaritalStatuses.containsKey(getMaritalStatus())) {
            return (String) allMaritalStatuses.get(getMaritalStatus());
        }
        else {
            return "";
        }
    }

    /**
     *
     * @return all maritalstatuses as map
     */
    public static Map getAllMaritalStatusMap() {
        return allMaritalStatuses;
    }

    /**
     *
     * @return all maritalstatuses as String array
     */
    public static String[] getAllMaritalStatuses() {
        return (String[]) allMaritalStatuses.values().toArray(new String[allMaritalStatuses.size()]);
    }
}
