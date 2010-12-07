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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>Created by 28.03.2008
 *   @author Samolisov Pavel
 */
public class LanguagesEnum {

    public static final int NONE = 0;
    public static final int AFRIKAANS = 55;
    public static final int ALBANIAN = 58;
    public static final int ARABIC = 1;
    public static final int ARMENIAN = 59;
    public static final int AZERBAIJANI = 68;
    public static final int BELORUSSIAN = 72;
    public static final int BHOJPURI = 2;
    public static final int BOSNIAN = 56;
    public static final int BULGARIAN = 3;
    public static final int BURMESE = 4;
    public static final int CANTONESE = 5;
    public static final int CATALAN = 6;
    public static final int CHAMORRO = 61;
    public static final int CHIMESE = 7;
    public static final int CROATIAN = 8;
    public static final int CZECH = 9;
    public static final int DANISH = 10;
    public static final int DUTH = 11;
    public static final int ENGLISH = 12;
    public static final int ESPERANTO = 13;
    public static final int ESTONIAN = 14;
    public static final int FARCI = 15;
    public static final int FINNISH = 16;
    public static final int FRENCH = 17;
    public static final int GAELIC = 18;
    public static final int GERMAN = 19;
    public static final int GREEK = 20;
    public static final int GUJARATI = 70;
    public static final int HEBREW = 21;
    public static final int HINDI = 22;
    public static final int HUNGARIAN = 23;
    public static final int ICELANDIC = 24;
    public static final int INDONESIAN = 25;
    public static final int ITALIAN = 26;
    public static final int JAPANESE = 27;
    public static final int KHMER = 28;
    public static final int KOREAN = 29;
    public static final int KURDISH = 69;
    public static final int LAO = 30;
    public static final int LATVIAN = 31;
    public static final int LITHUANIAN = 32;
    public static final int MACEDONIAN = 65;
    public static final int MALAY = 33;
    public static final int MANDARIN = 63;
    public static final int MONGOLIAN = 62;
    public static final int NORWEGIAN = 34;
    public static final int PERSIAN = 57;
    public static final int POLISH = 35;
    public static final int PORTUGUESE = 36;
    public static final int PUNJABI = 60;
    public static final int ROMANIAN = 37;
    public static final int RUSSIAN = 38;
    public static final int SERBO_CROATIAN = 39;
    public static final int SINDHI = 66;
    public static final int SLOVAK = 40;
    public static final int SLOVENIAN = 41;
    public static final int SOMALI = 42;
    public static final int SPANISH = 43;
    public static final int SWAHILI = 44;
    public static final int SWEDISH = 45;
    public static final int TAGALOG = 46;
    public static final int TAIWANESS = 64;
    public static final int TAMIL = 71;
    public static final int TATAR = 47;
    public static final int THAI = 48;
    public static final int TURKISH = 49;
    public static final int UKRAINIAN = 50;
    public static final int URDU = 51;
    public static final int VIETNAMESE = 52;
    public static final int WELSH = 67;
    public static final int YIDDISH = 53;
    public static final int YORUBA = 45;

    private static EnumerationsMap allLanguages = new EnumerationsMap();
    static {
          allLanguages.put(NONE, "None");
          allLanguages.put(AFRIKAANS,"Afrikaans");
          allLanguages.put(ALBANIAN,"Albanian");
          allLanguages.put(ARABIC, "Arabic");
          allLanguages.put(ARMENIAN,"Armenian");
          allLanguages.put(AZERBAIJANI,"Azerbaijani");
          allLanguages.put(BELORUSSIAN,"Belorussian");
          allLanguages.put(BHOJPURI, "Bhojpuri");
          allLanguages.put(BOSNIAN,"Bosnian");
          allLanguages.put(BULGARIAN, "Bulgarian");
          allLanguages.put(BURMESE, "Burmese");
          allLanguages.put(CANTONESE, "Cantonese");
          allLanguages.put(CATALAN, "Catalan");
          allLanguages.put(CHAMORRO, "Chamorro");
          allLanguages.put(CHIMESE, "Chinese");
          allLanguages.put(CROATIAN, "Croatian");
          allLanguages.put(CZECH, "Czech");
          allLanguages.put(DANISH, "Danish");
          allLanguages.put(DUTH, "Dutch");
          allLanguages.put(ENGLISH, "English");
          allLanguages.put(ESPERANTO, "Esperanto");
          allLanguages.put(ESTONIAN, "Estonian");
          allLanguages.put(FARCI, "Farci");
          allLanguages.put(FINNISH, "Finnish");
          allLanguages.put(FRENCH, "French");
          allLanguages.put(GAELIC, "Gaelic");
          allLanguages.put(GERMAN, "German");
          allLanguages.put(GREEK, "Greek");
          allLanguages.put(GUJARATI, "Gujarati");
          allLanguages.put(HEBREW, "Hebrew");
          allLanguages.put(HINDI, "Hindi");
          allLanguages.put(HUNGARIAN, "Hungarian");
          allLanguages.put(ICELANDIC, "Icelandic");
          allLanguages.put(INDONESIAN, "Indonesian");
          allLanguages.put(ITALIAN, "Italian");
          allLanguages.put(JAPANESE, "Japanese");
          allLanguages.put(KHMER, "Khmer");
          allLanguages.put(KOREAN, "Korean");
          allLanguages.put(KURDISH, "Kurdish");
          allLanguages.put(LAO, "Lao");
          allLanguages.put(LATVIAN, "Latvian");
          allLanguages.put(LITHUANIAN, "Lithuanian");
          allLanguages.put(MACEDONIAN, "Macedonian");
          allLanguages.put(MALAY, "Malay");
          allLanguages.put(MANDARIN, "Mandarin");
          allLanguages.put(MONGOLIAN, "Mongolian");
          allLanguages.put(NORWEGIAN, "Norwegian");
          allLanguages.put(PERSIAN, "Persian");
          allLanguages.put(POLISH, "Polish");
          allLanguages.put(PORTUGUESE, "Portuguese");
          allLanguages.put(PUNJABI, "Punjabi");
          allLanguages.put(ROMANIAN, "Romanian");
          allLanguages.put(RUSSIAN, "Russian");
          allLanguages.put(SERBO_CROATIAN, "Serbo-Croatian");
          allLanguages.put(SINDHI, "Sindhi");
          allLanguages.put(SLOVAK, "Slovak");
          allLanguages.put(SLOVENIAN, "Slovenian");
          allLanguages.put(SOMALI, "Somali");
          allLanguages.put(SPANISH, "Spanish");
          allLanguages.put(SWAHILI, "Swahili");
          allLanguages.put(SWEDISH, "Swedish");
          allLanguages.put(TAGALOG, "Tagalog");
          allLanguages.put(TAIWANESS, "Taiwaness");
          allLanguages.put(TAMIL, "Tamil");
          allLanguages.put(TATAR, "Tatar");
          allLanguages.put(THAI, "Thai");
          allLanguages.put(TURKISH, "Turkish");
          allLanguages.put(UKRAINIAN, "Ukrainian");
          allLanguages.put(URDU, "Urdu");
          allLanguages.put(VIETNAMESE, "Vietnamese");
          allLanguages.put(WELSH, "Welsh");
          allLanguages.put(YIDDISH, "Yiddish");
          allLanguages.put(YORUBA, "Yoruba");
    }

    private int lang;

    public LanguagesEnum(int lang) {
        this.lang = lang;
    }

    public int getLanguage() {
        return lang;
    }

    public String toString() {
        if (allLanguages.containsKey(getLanguage()))
            return (String) allLanguages.get(getLanguage());
        else
            return "";
    }

    /**
     *
     * @return all languages as map
     */
    public static Map getAllLanguagesMap() {
        return allLanguages;
    }

    /**
     *
     * @return all languages as String array (sort by Alphabetical)
     */
    public static String[] getAllLanguages() {
        List languages = new ArrayList();
        languages.addAll(allLanguages.values());
        Collections.sort(languages);

        return (String[]) languages.toArray(new String[languages.size()]);
    }
}
