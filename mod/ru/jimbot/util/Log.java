/**
 * JimBot - Java IM Bot
 * Copyright (C) 2006-2009 JimBot project
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.jimbot.util;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *  <p>Logger wrapper class.<br>
 *  Provides static methods to the Log4J logging methods.
 *  This is a convenience class only and can not be instantiated.
 *
 *  @author   Takis Diakoumis, Prolubnikov Dmitry
 * @version  $Revision: 1.1 $
 * @date     $Date: 2006/12/15 17:34:39 $
 */
public class Log implements Serializable {
    /** The Log4J Logger object */
	private static Logger system, con, err, http, talk, flood;
    
    /** the log pattern string */
    public static final String PATTERN = "[%d{dd.MM.yy HH:mm:ss}] %m%n";
    
    /** the max number of log files rolled over */
    public static final int MAX_BACKUP_INDEX = 5;
    
    /** <p><code>private<code> constructor to prevent instantiation. */
    private Log() {}

    /**
     * Initialises the logger instance with the specified level.
     *
     * @param level - the log level
     */
    public static void init(String level) {
    	PropertyConfigurator.configure("lib/log4j.properties");
    	system = Logger.getRootLogger();
//    	con = Logger.getLogger("con");
    	err = Logger.getLogger("error");
    	http = Logger.getLogger("http");
    	talk = Logger.getLogger("talk");
    	flood = Logger.getLogger("flood");
//    	((RollingFileAppender)talk.getAppender("talk")).setFile("");
    }

    /**
     * Adds the specified appender to the logger.
     *
     * @param appender - the appender to be added
     */
//    public static void addAppender(Appender appender) {
//        if (logger == null) {
//            throw new RuntimeException("Logger not initialised.");
//        }
//        logger.addAppender(appender);
//    }
    
    /**
     * Logs a message at log level INFO.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public static synchronized void info(Object message, Throwable throwable) {
        system.info(message, throwable);
//        con.info(message, throwable);
    }

    /**
     * Logs a message at log level DEBUG.
     *
     * @param message  the log message.
     */
    public static synchronized void debug(Object message) {
    	system.debug(message);
//    	con.debug(message);
    }

    /**
     * Logs a message at log level DEBUG.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public static synchronized void debug(Object message, Throwable throwable) {
    	system.debug(message, throwable);
//        con.debug(message, throwable);
    }

    /**
     * Logs a message at log level ERROR.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public static synchronized void error(Object message, Throwable throwable) {
        err.error(message, throwable);
    }

    /**
     * Logs a message at log level INFO.
     *
     * @param message  the log message.
     */
    public static synchronized void info(Object message) {
    	system.info(message);
    }
    
    public static synchronized void http(Object message) {
    	http.debug(message);
    }
    
    public static synchronized void talk(Object message) {
    	talk.info(message);
    }
    
    public static synchronized void flood(Object message) {
    	flood.info(message);
    }
    
    public static synchronized void flood2(Object message) {
    	flood.debug(message);
    }

    /**
     * Logs a message at log level ERROR.
     *
     * @param message  the log message.
     */
    public static synchronized void error(Object message) {
    	err.error(message);
    }

    /**
     * Returns whether a logger exists and
     * has been initialised.
     *
     * @return <code>true</code> if initialised |
     *         <code>false</code> otherwise
     */
    public static boolean isLogEnabled() {
        return system != null;
    }

}



