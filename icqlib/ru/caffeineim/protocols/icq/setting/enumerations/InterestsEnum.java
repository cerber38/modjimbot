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
public class InterestsEnum {

    public static final int UNSPECIFIED = 0;
    public static final int ART = 100;
    public static final int CARS = 101;
    public static final int CELEBRITY_FANS = 102;
    public static final int COLLECTIONS = 103;
    public static final int COMPUTERS = 104;
    public static final int CULTURE_LITERATURE = 105;
    public static final int FITNES = 106;
    public static final int GAMES = 107;
    public static final int HOBBIES = 108;
    public static final int ICQ_PROVIDING_HELP = 109;
    public static final int INTERNET = 110;
    public static final int LIFESTYLE = 111;
    public static final int MOVIES_TV = 112;
    public static final int MUSIC = 113;
    public static final int OUTDOOR_ACTIVITIES = 114;
    public static final int PARENTING = 115;
    public static final int PETS = 116;
    public static final int RELIGION = 117;
    public static final int SCIENCE = 118;
    public static final int SKILLS = 119;
    public static final int SPORTS = 120;
    public static final int WEB_DESIGN = 121;
    public static final int NATURE_AND_ENVIRONMENT = 122;
    public static final int NEWS_AND_MEDIA = 123;
    public static final int GOVERNMENT = 124;
    public static final int BUSINESS_ECONOMY = 125;
    public static final int MYSTICS = 126;
    public static final int TRAVEL = 127;
    public static final int ASTRONOMY = 128;
    public static final int SPACE = 129;
    public static final int CLOTHING = 130;
    public static final int PARTIES = 131;
    public static final int WOMEN = 132;
    public static final int SOCIAL_SCIENCE = 133;
    public static final int L60S = 134;
    public static final int L70S = 135;
    public static final int L80S = 136;
    public static final int L50S = 137;
    public static final int FINANCE_AND_CORPORATE = 138;
    public static final int ENTERTAINMENT = 139;
    public static final int CONSUMER_ELECTRONICS = 140;
    public static final int RETAIL_STORES = 141;
    public static final int HEALS_AND_BEAUTY = 142;
    public static final int MEDIA = 143;
    public static final int HOUSEHOLD_PRODUCTS = 144;
    public static final int MAIL_ORDER_CATALOG = 145;
    public static final int BUSINESS_SERVICES = 146;
    public static final int AUDIO_AND_VIRTUAL = 147;
    public static final int SPORTING_AND_ATHLETIC = 148;
    public static final int PUBLISHING = 149;
    public static final int HOME_AUTOMATION = 150;

    private static EnumerationsMap allInterests = new EnumerationsMap();
    static {
          allInterests.put(UNSPECIFIED, "Unspecified");
          allInterests.put(ART, "Art");
          allInterests.put(CARS, "Cars");
          allInterests.put(CELEBRITY_FANS, "Celebrity Fans");
          allInterests.put(COLLECTIONS, "Collections");
          allInterests.put(COMPUTERS, "Computers");
          allInterests.put(CULTURE_LITERATURE, "Culture & Literature");
          allInterests.put(FITNES, "Fitness");
          allInterests.put(GAMES, "Games");
          allInterests.put(HOBBIES, "Hobbies");
          allInterests.put(ICQ_PROVIDING_HELP, "ICQ - Providing Help");
          allInterests.put(INTERNET, "Internet");
          allInterests.put(LIFESTYLE, "Lifestyle");
          allInterests.put(MOVIES_TV, "Movies/TV");
          allInterests.put(MUSIC, "Music");
          allInterests.put(OUTDOOR_ACTIVITIES, "Outdoor Activities");
          allInterests.put(PARENTING, "Parenting");
          allInterests.put(PETS, "Pets/Animals");
          allInterests.put(RELIGION, "Religion");
          allInterests.put(SCIENCE, "Science/Technology");
          allInterests.put(SKILLS, "Skills");
          allInterests.put(SPORTS, "Sports");
          allInterests.put(WEB_DESIGN, "Web Design");
          allInterests.put(NATURE_AND_ENVIRONMENT, "Nature and Environment");
          allInterests.put(NEWS_AND_MEDIA, "News & Media");
          allInterests.put(GOVERNMENT, "Government");
          allInterests.put(BUSINESS_ECONOMY, "Business & Economy");
          allInterests.put(MYSTICS, "Mystics");
          allInterests.put(TRAVEL, "Travel");
          allInterests.put(ASTRONOMY, "Astronomy");
          allInterests.put(SPACE, "Space");
          allInterests.put(CLOTHING, "Clothing");
          allInterests.put(PARTIES, "Parties");
          allInterests.put(WOMEN, "Women");
          allInterests.put(SOCIAL_SCIENCE, "Social science");
          allInterests.put(L60S, "60's");
          allInterests.put(L70S, "70's");
          allInterests.put(L80S, "80's");
          allInterests.put(L50S, "50's");
          allInterests.put(FINANCE_AND_CORPORATE, "Finance and corporate");
          allInterests.put(ENTERTAINMENT, "Entertainment");
          allInterests.put(CONSUMER_ELECTRONICS, "Consumer electronics");
          allInterests.put(RETAIL_STORES, "Retail stores");
          allInterests.put(HEALS_AND_BEAUTY, "Health and beauty");
          allInterests.put(MEDIA, "Media");
          allInterests.put(HOUSEHOLD_PRODUCTS, "Household products");
          allInterests.put(MAIL_ORDER_CATALOG, "Mail order catalog");
          allInterests.put(BUSINESS_SERVICES, "Business services");
          allInterests.put(AUDIO_AND_VIRTUAL, "Audio and visual");
          allInterests.put(SPORTING_AND_ATHLETIC, "Sporting and athletic");
          allInterests.put(PUBLISHING, "Publishing");
          allInterests.put(HOME_AUTOMATION, "Home automation");
    }

    private int interest;

    public InterestsEnum(int interest) {
        this.interest = interest;
    }

    public int getInterest() {
        return interest;
    }

    public String toString() {
        if (allInterests.containsKey(getInterest())) {
            return (String) allInterests.get(getInterest());
        }
        else {
            return "";
        }
    }

    /**
     *
     * @return all occupations as map
     */
    public static Map getAllInterestsMap() {
        return allInterests;
    }

    /**
     *
     * @return all interests as String array
     */
    public static String[] getAllInterests() {
        return (String[]) allInterests.values().toArray(new String[allInterests.size()]);
    }
}
