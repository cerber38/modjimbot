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
public class PostBackgroundEnum {

    public static final int UNSPECIFIED = 0;
    public static final int ELEMENTARY_SCHOOL = 300;
    public static final int HIGH_SCHOOL = 301;
    public static final int COLLEGE = 302;
    public static final int UNIVERSITY = 303;
    public static final int MILITARY = 304;
    public static final int PAST_WORK_PLACE = 305;
    public static final int PAST_ORGANIZATION = 306;
    public static final int OTHER = 399;

    private static EnumerationsMap allPostbackground = new EnumerationsMap();
    static {
        allPostbackground.put(UNSPECIFIED, "Unspecified");
        allPostbackground.put(ELEMENTARY_SCHOOL, "Elementary school");
        allPostbackground.put(HIGH_SCHOOL, "High school");
        allPostbackground.put(COLLEGE, "College");
        allPostbackground.put(UNIVERSITY, "University");
        allPostbackground.put(MILITARY, "Military");
        allPostbackground.put(PAST_WORK_PLACE, "Past work place");
        allPostbackground.put(PAST_ORGANIZATION, "Past organization");
        allPostbackground.put(OTHER, "Other");
    }

    private int postbackground;

    public PostBackgroundEnum(int postbackground) {
        this.postbackground = postbackground;
    }

    public int getPostBackground() {
        return postbackground;
    }

    public String toString() {
        if (allPostbackground.containsKey(getPostBackground())) {
            return (String) allPostbackground.get(getPostBackground());
        }
        else {
            return "";
        }
    }

    /**
     *
     * @return all postbackgrounds as map
     */
    public static Map getAllPostBackgroundMap() {
        return allPostbackground;
    }

    /**
     *
     * @return all postbackgrounds as String array
     */
    public static String[] getAllPostBackgrounds() {
        return (String[]) allPostbackground.values().toArray(new String[allPostbackground.size()]);
    }
}
