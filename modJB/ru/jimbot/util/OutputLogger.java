/*
 * OutputLogger.java
 *
 * Copyright (C) 2002, 2003, 2004, 2005, 2006 Takis Diakoumis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 */


package ru.jimbot.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 *
 * @author   Takis Diakoumis, Prolubnikov Dmitry
 * @version  $Revision: 1.1 $
 * @date     $Date: 2006/12/15 17:34:39 $
 */
public class OutputLogger {

    private OutputLogger() {}

    public static void initialiseLogger(String name, 
                                        String layout, 
                                        String path, 
                                        int maxBackups) throws IOException {

        if (LogManager.exists(name) != null) {
            return;
        }

        Logger logger = Logger.getLogger(name);
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setConversionPattern(layout);

        RollingFileAppender appender = new RollingFileAppender(
                                                        patternLayout, 
                                                        path,
                                                        true);
        appender.setMaxBackupIndex(maxBackups);
        appender.setMaxFileSize("1MB");
        logger.addAppender(appender);
    }

    public static synchronized void append(String name, String text) {
        Logger logger = Logger.getLogger(name);
        if (logger != null) {
            logger.info(text);
        }
    }
    
    public static void info(String name, String message, Throwable throwable) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
            logger.info(message, throwable);
        }
    }
  
    public static void warning(String name, String message, Throwable throwable) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
            logger.warn(message, throwable);
        }
    }  
  
    public static void debug(String name, String message) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
            logger.debug(message);
        }
    }
    
    public static void debug(String name, String message, Throwable throwable) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
           logger.debug(message, throwable);
        }
    }

    public static void error(String name, String message, Throwable throwable) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
            logger.error(message, throwable);
        }
    }
  
    public static void info(String name, String message) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
            logger.info(message);
        }
    }
  
    public static void warning(String name, String message) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
            logger.warn(message);
        }  
    }  
  
    public static void error(String name, String message) {
        Logger logger = Logger.getLogger(name);        
        if (logger != null) {
            logger.error(message);
        }  
    }
    
}



