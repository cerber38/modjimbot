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


import java.io.PrintStream;
import java.util.Vector;
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.modules.WorkScript;
import ru.jimbot.modules.http.Server;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;
import ru.jimbot.util.SystemErrLogger;

/**
 * Запуск бота
 * @author Prolubnikov Dmitriy
 *
 */
public class StartBot3 implements Runnable {
private Thread th;
int sleepAmount = 1000;
long Time = System.currentTimeMillis();
long Time2 = MainProps.testtime;
private String Fucking = "You Fucking!!! Put copyrights of authors into place! Immediate!";
public static String name = System.getProperty("os.name"); //Название операционной системы
public static String version = System.getProperty("os.version"); //Версия операционной системы
public static String java = System.getProperty("java.version"); //Версия JRE
private AbstractProps props = null;

public StartBot3(){}

	public void start(){
		th = new Thread(this);
		th.setPriority(Thread.NORM_PRIORITY);
		th.start();
	}

private void CheckOfCopyrights(){
try{
boolean f = false;
String ver = MainProps.VERSION;
String ver2 = Monitor2.copirite;
ver = ver.replaceAll( "\n" , " " );
ver2 = ver2.replaceAll( "\n" , " " );
if( ver2.trim().equalsIgnoreCase( ver.trim() ) )
{
f = true;
}
if( f )
{
stop();
}
else
{
stop();
Log.getDefault().info( Fucking );
Manager.getInstance().exit();
}
}
catch ( Exception ex )
{
ex.printStackTrace();
Log.getDefault().info( "Errore: " + ex );
stop();
}
}


	public synchronized void stop() {
		th = null;
		notify();
	}

    public AbstractProps getProps() {
    	return props;
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
            	System.setErr(new PrintStream(new SystemErrLogger(), true));
                MainProps.load();
                Manager.getInstance();
                if(MainProps.getBooleanProperty("main.StartHTTP"))
                    try {
                        Vector<String> v = WorkScript.getInstance("").listHTTPScripts();
                        String[] s = new String[/*4*/2+v.size()*2];
                        s[0] = "/";
                        s[1] = "ru.jimbot.modules.http.MainPage";
                        //s[2] = "sms";
                        //s[3] = "ru.jimbot.modules.http.SmsWork";
                        for(int i=0;i<v.size();i++){
                            s[i*2+2] = v.get(i);
                            s[i*2+3] = "ru.jimbot.modules.http.HTTPScriptRequest";
                        }
                        Server.startServer(s);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                 try {
                	 Manager.getInstance().startAll();
                 } catch (Exception ex) {
                	 ex.printStackTrace();
                 }
    }


    	public void run() {
		Thread me = Thread.currentThread();
		while (th == me) {
			CheckOfCopyrights();
			try {
				th.sleep(sleepAmount);
			} catch (InterruptedException e) { break; }
		}
		th=null;
	}

}
