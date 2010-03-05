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

//import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.modules.AbstractServer;
import ru.jimbot.modules.WorkScript;
//import ru.jimbot.modules.http.HttpConnection;
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
private static boolean start = true;
private Thread th;
int sleepAmount = 1000;
private static StartBot3 botstart = null;
long Time = System.currentTimeMillis();
long Time2 = MainProps.testtime;
private String Fucking = "You Fucking!!! Put copyrights of authors into place! Immediate!";
public static String name = System.getProperty("os.name"); //Название операционной системы
public static String version = System.getProperty("os.version"); //Версия операционной системы
public static String java = System.getProperty("java.version"); //Версия JRE
//String newString = new String( advertising.getBytes( "ISO-8859-1" ), "windows-1251"  );
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

        // Самосоздание батника для запуска бота :)
/*File bat = new File("JimBot_Start.bat");
            if( !bat.exists() ){
            try{
            MainProps.AddFail( "bat", "./JimBot_Start", "mode con cp select=1251\njava -jar JimBot.jar" );
            } catch ( Exception ex ) {
            ex.printStackTrace();
            Log.info( "При создании bat. файла возникла ошибка: " + ex );
            }
            }*/
            	//Log.init("");
                //Log.info( "Операционная система: " + name );
                //Log.info( "Версия операционной системы: " + version );

            	System.setErr(new PrintStream(new SystemErrLogger(), true));
                MainProps.load();
                Manager.getInstance();
                if(MainProps.getBooleanProperty("main.StartHTTP"))
                    try {
                        Vector<String> v = WorkScript.getInstance("").listHTTPScripts();
                        String[] s = new String[2+v.size()*2];
                        s[0] = "/";
                        s[1] = "ru.jimbot.modules.http.MainPage";
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
//            }
//        });
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
