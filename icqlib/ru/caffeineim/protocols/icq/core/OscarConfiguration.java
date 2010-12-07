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
package ru.caffeineim.protocols.icq.core;

//import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>Created by 16.01.2010
 *   @author Samolisov Pavel
 */
public class OscarConfiguration {

        private static Log log = LogFactory.getLog(OscarConfiguration.class);

        private static final String CONFIG_FILE = "/icqlib.properties";

        public static final String VERSION = "VERSION";
        public static final String PING_TIMEOUT = "PING_TIMEOUT";

        private static Properties properties = new Properties();

        private static boolean loaded = false;

        public static void load() {
                try {
                        properties.load(OscarConfiguration.class.getResourceAsStream(CONFIG_FILE));
                        loaded = true;
                }catch (IOException e) {
                        log.error("Could not load properties from file " + CONFIG_FILE, e);
                        System.exit(1);
                }
        }

        public static String get(String property) {
                if (!loaded)
                        load();

                if (!properties.containsKey(property))
                        throw new IllegalStateException("Could not find property " + property);

                return properties.getProperty(property);
        }
}


