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

package ru.jimbot;


/**
 *
 * @author Prolubnikov Dmitry
 */
public class Monitor2 implements Runnable {
    boolean isPause=false;
    private Thread th;
    int sleepAmount = 1000;
    private static final int testMaxCount = 30; // Период проверки файла - 15с
    private int count =0;
    long TimesDelLog = System.currentTimeMillis();

    /** Creates a new instance of Monitor */
    public Monitor2() {
    }
    
    public void start(){
        th = new Thread(this,"monitor");
        th.setPriority(Thread.MIN_PRIORITY);
        th.start();
    }
    
    public synchronized void stop() {
        th = null;
        notify();
    }
       
    private void testState(){
        count++;
        if(count>testMaxCount){
            count=0;
            Manager.getInstance().testState();
            Manager.getInstance().testDB();
        }
    }
      
    public void run() {
        Thread me = Thread.currentThread(); 
        while (th == me) {
            testState();
            if(Manager.isDeleteLog()) deleteLogs();
            try {
                Thread.sleep(sleepAmount);
            } catch (InterruptedException e) { break; }             
        }
        th=null;
    }

    private void deleteLogs(){
    if((System.currentTimeMillis()-TimesDelLog)> Manager.timeDeleteLog()*60*60000){
    Manager.getInstance().deleteLogs();
    TimesDelLog = System.currentTimeMillis();
    }
    }

}
