/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.util;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author fraer72
 */
public class Time {
       public int state=0;
       private long time=0;
       long startTime = System.currentTimeMillis();

       public static long getTimeStart()
       {
       long t = 0;
       try
       {
       File f = new File("./state");
       t = f.lastModified();
       }
       catch (Exception ex)
       {
       ex.printStackTrace();
       }
       return t;
       }

       public static long getUpTime()
       {
       return System.currentTimeMillis() - getTimeStart();
       }

       private  long getHourStat()
       {
       if (getUpTime() > 1000 * 60 * 60) {
       return state / (getUpTime() / 3600000);
       }
       return 0;
       }

       private long getDayStat()
       {
       if (getUpTime() > 1000 * 60 * 60 * 24) {
       return state / (getUpTime() / 86400000);
       }
       return 0;
       }

       public static String getTime(long t) {
       Date dt = new Date(t);
       DateFormat df = DateFormat.getTimeInstance(DateFormat.MEDIUM);
       df.setTimeZone(TimeZone.getTimeZone("GMT"));
       return (t / 86400000) + " дней " + df.format(dt);
       }


	   public  long getDeltaTime()
       {
	   return System.currentTimeMillis()-time;
	   }


	   public long getLastTime()
       {
	   return time;
	   }

       }
