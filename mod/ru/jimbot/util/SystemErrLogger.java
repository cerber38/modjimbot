/*
 * SystemErrLogger.java
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
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

/* ----------------------------------------------------------
 * CVS NOTE: Changes to the CVS repository prior to the 
 *           release of version 3.0.0beta1 has meant a 
 *           resetting of CVS revision numbers.
 * ----------------------------------------------------------
 */

/**
 * Output stream for System.err that logs to registered Log4J
 * logger instance as well as standard System.err output.
 *
 * @author   Takis Diakoumis, Prolubnikov Dmitry
 * @version  $Revision: 1.1 $
 * @date     $Date: 2006/12/15 17:34:39 $
 */
public class SystemErrLogger extends OutputStream {
  
    /** string buffer for text concat */
    private StringBuffer buf;

    /** matcher to remove new lines from log messages */
    private Matcher newLineMatcher;

    /** the System.err print stream */
    private PrintStream systemErr;

    /** Creates a new instance of SystemErrLogger */
    public SystemErrLogger() {
        systemErr = System.err;
        buf = new StringBuffer();
        newLineMatcher = Pattern.compile("[\n\r]+").matcher("");
    }
    
    private void log(final String s) {
        Runnable update = new Runnable() {
            public void run() {
                newLineMatcher.reset(s);
                if (s.length() == 1 && newLineMatcher.find()) {
                    return;
                }
                String text = newLineMatcher.replaceAll(" ");
                Log.error(text);
            }
        };
        SwingUtilities.invokeLater(update);
    }

    public synchronized void write(int b) {
        systemErr.write(b);
        b &= 0x000000FF;
        char c = (char)b;
        buf.append(String.valueOf(c));
    }

    public synchronized void write(byte[] b, int offset, int length) {
        systemErr.write(b, offset, length);
        buf.append(new String(b, offset, length));
    }

    public synchronized void write(byte[] b) {
        try {
            systemErr.write(b);
        } catch (IOException e) {}
        buf.append(new String(b));
    }

    public synchronized void flush() {
        systemErr.flush();
        synchronized (buf) {
            if (buf.length() > 0) {
                log(buf.toString());
                buf.setLength(0);
            }                
        }

    } 

}



