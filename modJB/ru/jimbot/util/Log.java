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
import java.util.HashMap;

import org.apache.log4j.*;

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
	protected Logger system, con, err, http, talk, flood;
    private static Log defaultLogger;
    private static HashMap<String, Log> loggers = new HashMap<String, Log>();
    /** the log pattern string */
    public static final String PATTERN = "[%d{dd.MM.yy HH:mm:ss}] %m%n";

    /** the max number of log files rolled over */
    public static final int MAX_BACKUP_INDEX = 5;

    public static final String Log4jProperties = "" +
    "log4j.category.$SERVICE.system=info, $SERVICE.system\n" +
    "log4j.category.$SERVICE.error=error, $SERVICE.error\n" +
    "log4j.category.$SERVICE.talk=info, $SERVICE.talk\n" +
    "log4j.category.$SERVICE.http=debug, $SERVICE.http\n" +
    "log4j.category.$SERVICE.flood=info, $SERVICE.flood\n" +
    "\n" +
    "log4j.appender.$SERVICE.system=org.apache.log4j.RollingFileAppender\n" +
    "log4j.appender.$SERVICE.system.File=log/$SERVICE/system.log\n" +
    "log4j.appender.$SERVICE.system.encoding=UTF-8\n" +
    "log4j.appender.$SERVICE.system.MaxFileSize=500KB\n" +
    "log4j.appender.$SERVICE.system.MaxBackupIndex=5\n" +
    "log4j.appender.$SERVICE.system.layout=org.apache.log4j.PatternLayout\n" +
    "log4j.appender.$SERVICE.system.layout.ConversionPattern=%d{dd.MM.yyyy HH:mm:ss} [%5p] - %m%n\n" +
    "\n" +
    "log4j.appender.$SERVICE.error=org.apache.log4j.RollingFileAppender\n" +
    "log4j.appender.$SERVICE.error.File=log/$SERVICE/error.log\n" +
    "log4j.appender.$SERVICE.error.encoding=UTF-8\n" +
    "log4j.appender.$SERVICE.error.MaxFileSize=500KB\n" +
    "log4j.appender.$SERVICE.error.MaxBackupIndex=5\n" +
    "log4j.appender.$SERVICE.error.layout=org.apache.log4j.PatternLayout\n" +
    "log4j.appender.$SERVICE.error.layout.ConversionPattern=%d{dd.MM.yyyy HH:mm:ss} [%5p] - %m%n\n" +
    "\n" +
    "log4j.appender.$SERVICE.http=org.apache.log4j.RollingFileAppender\n" +
    "log4j.appender.$SERVICE.http.File=log/$SERVICE/http.log\n" +
    "log4j.appender.$SERVICE.http.encoding=UTF-8\n" +
    "log4j.appender.$SERVICE.http.MaxFileSize=500KB\n" +
    "log4j.appender.$SERVICE.http.MaxBackupIndex=5\n" +
    "log4j.appender.$SERVICE.http.layout=org.apache.log4j.PatternLayout\n" +
    "log4j.appender.$SERVICE.http.layout.ConversionPattern=%d{dd.MM.yyyy HH:mm:ss} [%5p] - %m%n\n" +
    "\n" +
    "log4j.appender.$SERVICE.talk=org.apache.log4j.DailyRollingFileAppender\n" +
    "log4j.appender.$SERVICE.talk.File=log/$SERVICE/talk.log\n" +
    "log4j.appender.$SERVICE.talk.encoding=UTF-8\n" +
    "log4j.appender.$SERVICE.talk.DatePattern=\'.\'yyyy-MM-dd\n" +
    "log4j.appender.$SERVICE.talk.layout=org.apache.log4j.PatternLayout\n" +
    "log4j.appender.$SERVICE.talk.layout.ConversionPattern=%d{dd.MM.yyyy HH:mm:ss} - %m%n\n" +
    "\n" +
    "log4j.appender.$SERVICE.flood=org.apache.log4j.RollingFileAppender\n" +
    "log4j.appender.$SERVICE.flood.File=log/$SERVICE/flood.log\n" +
    "log4j.appender.$SERVICE.flood.encoding=UTF-8\n" +
    "log4j.appender.$SERVICE.flood.MaxFileSize=500KB\n" +
    "log4j.appender.$SERVICE.flood.MaxBackupIndex=5\n" +
    "log4j.appender.$SERVICE.flood.layout=org.apache.log4j.PatternLayout\n" +
    "log4j.appender.$SERVICE.flood.layout.ConversionPattern=%d{dd.MM.yyyy HH:mm:ss} [%5p] - %m%n\n" +
    "\n" +
    "log4j.appender.telnet=org.apache.log4j.net.TelnetAppender\n" +
    "log4j.appender.telnet.layout=org.apache.log4j.SimpleLayout\n" +
    "log4j.appender.telnet.Port=5050";

    /** <p><code>private<code> constructor to prevent instantiation. */
    private Log() {}

    public static Log getLogger(String serviceName) {
        if(loggers.containsKey(serviceName)) return loggers.get(serviceName);
        Log l = new Log();
//        l.init("services/"+serviceName + "/");
        l.init(serviceName);
        loggers.put(serviceName, l);
        return l;
    }

    public static Log getDefault() {
        if(defaultLogger==null) {
            defaultLogger = new Log();
            defaultLogger.init("");
        }
        return defaultLogger;
    }

    /**
     * Initialises the logger instance with the specified level.
     *
     */
    public void init(String folder) {
        if(folder.equals("")){
            PropertyConfigurator.configure("lib/log4j.properties");
            system = Logger.getRootLogger();
            err = Logger.getLogger("error");
            http = Logger.getLogger("http");
            talk = Logger.getLogger("talk");
            flood = Logger.getLogger("flood");
            http.setAdditivity(false);
        } else {
            PropertyConfigurator.configure("services/" + folder + "/log4j.properties");
            system = Logger.getLogger(folder + ".system");
            err = Logger.getLogger(folder + ".error");
            http = Logger.getLogger(folder + ".http");
            talk = Logger.getLogger(folder + ".talk");
            flood = Logger.getLogger(folder + ".flood");
            http.setAdditivity(false);
        }

//    	system = folder.equals("") ? Logger.getRootLogger() : Logger.getLogger("system"+folder);
////    	con = Logger.getLogger("con");
//    	err = Logger.getLogger("error"+folder);
//    	http = Logger.getLogger("http"+folder);
//    	talk = Logger.getLogger("talk"+folder);
//    	flood = Logger.getLogger("flood"+folder);
//        http.setAdditivity(false);
//        if(!folder.equals("")){
//            FileAppender t = (FileAppender)Logger.getRootLogger().getAppender("system");
//            t.setName("system" + folder);
//            t.setFile(folder + "log/system.log");
//            system.removeAllAppenders();
//            system.addAppender(t);
//            DailyRollingFileAppender t1 = (DailyRollingFileAppender)Logger.getLogger("talk").getAppender("talk");
//            t1.setName("talk" + folder);
//            t1.setFile(folder + "log/talk.log");
//            talk.removeAllAppenders();
//            talk.addAppender(t1);
//        }
//        ((FileAppender)system.getAppender("system")).setFile(folder + "log/system.log");
//        ((DailyRollingFileAppender)talk.getAppender("talk")).setFile(folder + "log/talk.log");
//        ((FileAppender)err.getAppender("error")).setFile(folder + "log/error.log");
//        ((FileAppender)http.getAppender("http")).setFile(folder + "log/http.log");
//        ((FileAppender)flood.getAppender("flood")).setFile(folder + "log/flood.log");

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
    public synchronized void info(Object message, Throwable throwable) {
        system.info(message, throwable);
//        con.info(message, throwable);
    }

    /**
     * Logs a message at log level DEBUG.
     *
     * @param message  the log message.
     */
    public synchronized void debug(Object message) {
    	system.debug(message);
//    	con.debug(message);
    }

    /**
     * Logs a message at log level DEBUG.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public synchronized void debug(Object message, Throwable throwable) {
    	system.debug(message, throwable);
//        con.debug(message, throwable);
    }

    /**
     * Logs a message at log level ERROR.
     *
     * @param message  the log message.
     * @param throwable the throwable.
     */
    public synchronized void error(Object message, Throwable throwable) {
        err.error(message, throwable);
    }

    /**
     * Logs a message at log level INFO.
     *
     * @param message  the log message.
     */
    public synchronized void info(Object message) {
    	system.info(message);
    }

    public synchronized void http(Object message) {
    	http.debug(message);
    }

    public synchronized void talk(Object message) {
    	talk.info(message);
    }

    public synchronized void flood(Object message) {
    	flood.info(message);
    }

    public synchronized void flood2(Object message) {
    	flood.debug(message);
    }

    /**
     * Logs a message at log level ERROR.
     *
     * @param message  the log message.
     */
    public synchronized void error(Object message) {
    	err.error(message);
    }

    /**
     * Returns whether a logger exists and
     * has been initialised.
     *
     * @return <code>true</code> if initialised |
     *         <code>false</code> otherwise
     */
    public boolean isLogEnabled() {
        return system != null;
    }

}





