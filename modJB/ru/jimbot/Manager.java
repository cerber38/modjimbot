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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.AbstractServer;
import ru.jimbot.modules.anek.AnekServer;
import ru.jimbot.modules.chat.ChatServer;
import ru.jimbot.modules.http.Server;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;

/**
 * Управление сервисами бота
 *
 * @author Prolubnikov Dmitriy
 *
 */
public class Manager {
	private HashMap<String,AbstractServer> services = new HashMap<String,AbstractServer>();
	private Monitor2 mon = new Monitor2();
	private static Manager mn = null;
	private ConcurrentHashMap<String, Object> data = null;

	/**
	 * Инициализация. Создание сервисов, определенных в файле конфигурации.
	 */
	public Manager() {
		createState();
		mon.start();
		for(int i=0;i<MainProps.getServicesCount();i++){
			addService(MainProps.getServiceName(i),MainProps.getServiceType(i));
		}
	}

	/**
	 * Возвращает нужный объект по ключу
	 * @param key
	 * @return
	 */
	public Object getData(String key){
	    if(!data.containsKey(key)) return null;
	    return data.get(key);
	}

	/**
	 * Запоминает новое значение объекта
	 * @param key
	 * @param o
	 */
	public void setData(String key, Object o){
	    data.put(key, o);
	}

	/**
	 * Перезапускает все действующие процессы
	 */
	public  static void restart() {
	    mn.stopAll();
	    mn.mon.stop();
	    mn = null;
	    System.gc();
	    getInstance().startAll();
	}

	/**
	 * Возвращает экземпляр класса. При необходимости производит его создание и инициализацию.
	 * @return
	 */
	public static Manager getInstance() {
		if(mn==null){
			mn = new Manager();
			mn.data = new ConcurrentHashMap<String, Object>();
		}
		return mn;
	}

	/**
	 * Общее число зареганых сервисов
	 * @return
	 */
	public int getServiceCount() {
		return services.keySet().size();
	}

	/**
	 * Возвращает набор имен сервисов (для последующего перебора)
	 * @return
	 */
	public Set<String> getServiceNames() {
		return services.keySet();
	}

	/**
     * Проверка состояния файла. При необходимости - выключение
     */
    public void testState(){
    	BufferedReader r = null;
        try{
            File f = new File("./state");
            if(!f.exists()) {
                createState();
                return;
            }
            r = new BufferedReader(new InputStreamReader(new FileInputStream("state")));
            String s = r.readLine();
            if(s.equals("Stop")) {
                exit();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
        	if(r!=null){
        		try{r.close();}catch(Exception e) {}
        	}
        }
    }

    /**
     * Проверка подключения к БД. Повоторят попытку подключиться если база недоступна.
     */
    public void testDB(){
    	for(String s : services.keySet()){
    		if(services.get(s).isRun){
    			try {
    				if(services.get(s).getDB().isClosed()){
    					services.get(s).getDB().getDb();
    				}
    			} catch (SQLException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }

    /**
     * Создает файл статуса программы
     */
    private void createState(){
        try {
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("state")));
            w.write("start");
            w.newLine();
            w.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     */
    public void exit() {
    	stopAll();
    	if(MainProps.getBooleanProperty("main.StartHTTP"))
        Server.stopServer();
    	mon.stop();
    	Log.getDefault().info("Exit bot " + new Date(System.currentTimeMillis()).toString());
    	System.exit(0);
    }

	/**
	 * Добавление нового сервиса
	 * @param name
	 * @param type = "anek", "chat"
	 */
	public void addService(String name, String type) {
		if(type.equals("chat")){
			services.put(name, new ChatServer(name));
		} else if (type.equals("anek")){
			services.put(name, new AnekServer(name));
		} else {
			Log.getLogger(name).error("Неизвестный тип сервиса: "+type);
		}
	}

	/**
	 * Удаление сервиса
	 * @param name
	 */
	public void delService(String name){
		if(services.containsKey(name)){
			try{
			    services.get(name).stop();
			} catch (Exception e) {}
			services.remove(name);
		} else {
			Log.getLogger(name).error("Отсутствет сервис с именем "+name);
		}
	}

	/**
	 * Запуск сервиса
	 * @param name
	 */
	public void start(String name){
		if(services.containsKey(name)){
			Log.getLogger(name).info("Запускаю сервис: " + name);
			services.get(name).start();
		} else {
			Log.getLogger(name).error("Отсутствет сервис с именем "+name);
		}
	}

	/**
	 * Остановка сервиса
	 * @param name
	 */
	public void stop(String name){
		if(services.containsKey(name)){
			Log.getLogger(name).info("Останавливаю сервис: " + name);
			services.get(name).stop();
		}else{
			Log.getLogger(name).error("Отсутствет сервис с именем "+name);
		}
	}

	/**
	 * Запуск всех сервисов
	 */
	public void startAll() {
		for(AbstractServer s : services.values()){
			if(s.getProps().isAutoStart()) s.start();
		}
	}

	/**
	 * Остановка всех сервисов
	 */
	public void stopAll() {
		for(AbstractServer s : services.values()){
			if(s.isRun)s.stop();
		}
	}

	/**
	 * Возвращает ссылку на конкретный сервис
	 * @param name
	 * @return
	 */
	public AbstractServer getService(String name) {
		return services.get(name);
	}

	/**
	 * Статус выполнения данного сервиса
	 * @param name
	 * @return
	 */
	public boolean isRun(String name) {
		if(services.containsKey(name))
			return services.get(name).isRun;
		else
			return false;
	}
}
