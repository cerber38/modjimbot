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
public class AffilationEnum {

    public static final int UNSPECIFIED = 0;
    public static final int ALUMNI = 200;
    public static final int CHARITY = 201;
    public static final int CLUB_SOCIAL = 202;
    public static final int COMMUNITY = 203;
    public static final int CULTURAL = 204;
    public static final int FAN_CLUBS = 205;
    public static final int FRATERNITY = 206;
    public static final int HOBBYISTS = 207;
    public static final int INTERNATIONAL = 208;
    public static final int NATURE_AND_ENVIRONMENT = 209;
    public static final int PROFESSIONAL = 210;
    public static final int SCIENTIFIC = 211;
    public static final int SELF_IMPROVEMENT = 212;
    public static final int SPIRITUAL = 213;
    public static final int SPORTS = 214;
    public static final int SUPPORT = 215;
    public static final int TRADE_AND_BUSINESS = 216;
    public static final int UNION = 217;
    public static final int VOLUNTEER = 218;
    public static final int OTHER = 299;

    private static EnumerationsMap allAffiliations = new EnumerationsMap();
    static {
          allAffiliations.put(UNSPECIFIED, "Unspecified");
          allAffiliations.put(ALUMNI, "Alumni Org.");
          allAffiliations.put(CHARITY, "Charity Org.");
          allAffiliations.put(CLUB_SOCIAL, "Club/Social Org.");
          allAffiliations.put(COMMUNITY, "Community Org.");
          allAffiliations.put(CULTURAL, "Cultural Org.");
          allAffiliations.put(FAN_CLUBS, "Fan Clubs");
          allAffiliations.put(FRATERNITY, "Fraternity/Sorority");
          allAffiliations.put(HOBBYISTS, "Hobbyists Org.");
          allAffiliations.put(INTERNATIONAL, "International Org.");
          allAffiliations.put(NATURE_AND_ENVIRONMENT, "Nature and Environment Org.");
          allAffiliations.put(PROFESSIONAL, "Professional Org.");
          allAffiliations.put(SCIENTIFIC, "Scientific/Technical Org.");
          allAffiliations.put(SELF_IMPROVEMENT, "Self Improvement Group");
          allAffiliations.put(SPIRITUAL, "Spiritual/Religious Org.");
          allAffiliations.put(SPORTS, "Sports Org.");
          allAffiliations.put(SUPPORT, "Support Org.");
          allAffiliations.put(TRADE_AND_BUSINESS, "Trade and Business Org.");
          allAffiliations.put(UNION, "Union");
          allAffiliations.put(VOLUNTEER, "Volunteer Org.");
          allAffiliations.put(OTHER, "Other");
    }

    private int affiliation;

    public AffilationEnum(int affiliation) {
        this.affiliation = affiliation;
    }

    public int getAffiliation() {
        return affiliation;
    }

    public String toString() {
        if (allAffiliations.containsKey(getAffiliation())) {
            return (String) allAffiliations.get(getAffiliation());
        }
        else {
            return "";
        }
    }

    /**
     *
     * @return all affiliations as map
     */
    public static Map getAllAffiliationsMap() {
        return allAffiliations;
    }

    /**
     *
     * @return all as String array
     */
    public static String[] getAllAffiliations() {
        return (String[]) allAffiliations.values().toArray(new String[allAffiliations.size()]);
    }
}
