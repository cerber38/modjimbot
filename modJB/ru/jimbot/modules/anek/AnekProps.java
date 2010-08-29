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

package ru.jimbot.modules.anek;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;
import ru.jimbot.Manager;
import ru.jimbot.modules.AbstractProps;
import ru.jimbot.table.UserPreference;
import ru.jimbot.util.Log;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class AnekProps implements AbstractProps {
	public static HashMap<String,AnekProps> props = new HashMap<String,AnekProps>();
    public String PROPS_FILE = "";
    private String PROPS_FOLDER = "";
    public String ENCODING = "windows-1251";
    public Properties appProps;
    public Properties langProps;
    public boolean isLoaded = false;
  
    /** Creates a new instance of AnekProps */
    public AnekProps() {
    }
    
    public static AnekProps getInstance(String name){
    	if(props.containsKey(name))
    		return props.get(name);
    	else {
    		AnekProps p = new AnekProps();
    		p.PROPS_FILE = "./services/"+name+"/"+name+".xml";
    		p.PROPS_FOLDER = "./services/"+name;
    		p.setDefault();
    		      /*p.load();*/
    		props.put(name, p);
    		return p;
    	}
    }
    
    public void setDefault() {
        appProps = new Properties();
        setIntProperty("conn.uinCount",1);
        setStringProperty("conn.uin0","111");
        setStringProperty("conn.pass0","Password");
        setIntProperty("icq.status",0);
        setIntProperty("icq.xstatus",0);
        setIntProperty("bot.pauseIn",3000); //Пауза входящих сообщений
        setIntProperty("bot.pauseOut",500); //Пауза исходящих сообщений
        setIntProperty("bot.msgOutLimit",20); //Ограничение очереди исходящих сообщений
        setIntProperty("bot.pauseRestart",11*60*1000); //Пауза перед запуском упавшего коннекта
        setStringProperty("bot.adminUIN","111111;222222");
        setIntProperty("icq.AUTORETRY_COUNT",5);
        setStringProperty("icq.STATUS_MESSAGE1","");
        setStringProperty("icq.STATUS_MESSAGE2","");
        setBooleanProperty("bot.useAds",false);
        setIntProperty("bot.adsRate",3);
        setBooleanProperty("main.StartBot",false);
        setIntProperty("chat.MaxOutMsgSize",500);
        setIntProperty("chat.MaxOutMsgCount",5);   
        setStringProperty("db.host","localhost:3306");
        setStringProperty("db.user","root");
        setStringProperty("db.pass","");
        setStringProperty("db.dbname","botdb");
    }

    public UserPreference[] getUserPreference(){
        UserPreference[] p = {
            new UserPreference(UserPreference.CATEGORY_TYPE,"main", "Основные настройки"),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"main.StartBot","Запускать анекдотный бот",getBooleanProperty("main.StartBot"),true),
            new UserPreference(UserPreference.CATEGORY_TYPE,"anek", "Настройки анекдотного бота"),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.status","ICQ статус",getIntProperty("icq.status"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.xstatus","x-статус (0-37)",getIntProperty("icq.xstatus"),true),
            new UserPreference(UserPreference.STRING_TYPE,"icq.STATUS_MESSAGE1","Сообщение x-статуса 1",getStringProperty("icq.STATUS_MESSAGE1"),true),
            new UserPreference(UserPreference.STRING_TYPE,"icq.STATUS_MESSAGE2","Сообщение x-статуса 2",getStringProperty("icq.STATUS_MESSAGE2"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.AUTORETRY_COUNT","Число переподключений движка при обрыве",getIntProperty("icq.AUTORETRY_COUNT"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseIn","Пауза для входящих сообщений",getIntProperty("bot.pauseIn"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseOut","Пауза для исходящих сообщений",getIntProperty("bot.pauseOut"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.msgOutLimit","Ограничение очереди исходящих",getIntProperty("bot.msgOutLimit"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxOutMsgSize","Максимальный размер одного исходящего сообщения",getIntProperty("chat.MaxOutMsgSize"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"chat.MaxOutMsgCount","Максимальное число частей исходящего сообщения",getIntProperty("chat.MaxOutMsgCount"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.pauseRestart","Пауза перед перезапуском коннекта",getIntProperty("bot.pauseRestart"),true),
            new UserPreference(UserPreference.STRING_TYPE,"bot.adminUIN","Админские UIN",getStringProperty("bot.adminUIN"),true),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"bot.useAds","Использовать рекламу в боте",getBooleanProperty("bot.useAds"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"bot.adsRate","Частота рекламы",getIntProperty("bot.adsRate"),true),
            new UserPreference(UserPreference.CATEGORY_TYPE,"db", "Настройки mySQL"),
            new UserPreference(UserPreference.STRING_TYPE,"db.host","Хост БД",getStringProperty("db.host"),false),
            new UserPreference(UserPreference.STRING_TYPE,"db.user","Пользователь",getStringProperty("db.user"),false),
            new UserPreference(UserPreference.PASS_TYPE,"db.pass","Пароль",getStringProperty("db.pass"),false),
            new UserPreference(UserPreference.STRING_TYPE,"db.dbname","Название базы данных",getStringProperty("db.dbname"),false)
        };
        return p;
    }

    public UserPreference[] OtherUserPreference(){
        UserPreference[] p = {

        };
        return p;
    }


    public UserPreference[] getUINPreference(){
        UserPreference[] p = new UserPreference[uinCount()*2+1];
        p[0] = new UserPreference(UserPreference.CATEGORY_TYPE,"conn", "Настройки UINов для подключения");
        for(int i=0;i<uinCount();i++){
            p[i*2+1] = new UserPreference(UserPreference.STRING_TYPE,"conn.uin" + i,"UIN" + i,getProperty("conn.uin" + i,""),true);
            p[i*2+2] = new UserPreference(UserPreference.PASS_TYPE,"conn.pass" + i,"Password" + i,getProperty("conn.pass" + i, ""),true);
        }
        return p;
    }

    public boolean isAutoStart(){
    	return getBooleanProperty("main.StartBot");
    }
    
    public int uinCount(){
        return getIntProperty("conn.uinCount");
    }
    
    public String getUin(int i){
        return getStringProperty("conn.uin"+i);
    }
    
    public String getPass(int i){
        return getStringProperty("conn.pass"+i);
    }    
    
    /**
     * Изменение уина
     * @param i
     * @param uin
     * @param pass
     */
    public void setUin(int i, String uin, String pass){
    	setStringProperty("conn.uin"+i, uin);
    	setStringProperty("conn.pass"+i, pass);
    }
    
    /**
     * Добавление нового уина в настройки
     * @param uin - уин
     * @param pass - пароль
     * @return - порядковый номер нового уина
     */
    public int addUin(String uin, String pass){
    	int c = uinCount();
    	setIntProperty("conn.uinCount", c+1);
    	setStringProperty("conn.uin"+c, uin);
    	setStringProperty("conn.pass"+c, pass);
    	return c;
    }
    
    /**
     * Удаление уина из настроек
     * @param c
     */
    public void delUin(int c) {
    	// Сдвигаем элементы после удаленного
    	for(int i=0; i<(uinCount()-1); i++){
    		if(i>=c){
    			setStringProperty("conn.uin"+i, getUin(i+1));
    			setStringProperty("conn.pass"+i, getPass(i+1));
    		}
    	}
    	//Удаляем самый последний элемент
    	appProps.remove("conn.uin"+(uinCount()-1));
    	appProps.remove("conn.pass"+(uinCount()-1));
    	setIntProperty("conn.uinCount", uinCount()-1);
    }
    
    public boolean testAdmin(String uin) {
        if(uin.equals("0")) return true; //Выртуальный админ
        String s = getStringProperty("bot.adminUIN");
        if(s.equals("")) return true;
        String[] ss = s.split(";");
        try{
            for(int i=0;i<ss.length;i++){
                if(ss[i].equalsIgnoreCase(uin)) return true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

    public String[] getAdmins(){
    	return getStringProperty("bot.adminUIN").split(";");
    }

    public final void load() {
       File file = new File(PROPS_FILE);
        if(!file.exists()){
        String[] xml = PROPS_FILE.split("/");
        Log.getDefault().error(xml[2] + " не создан!");
        return;
        }
        setDefault();
        try {
            FileInputStream fi = new FileInputStream(file);
            appProps.loadFromXML(fi);
            fi.close();
            Log.getDefault().info("Load preferences ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error opening preferences: ");
        }
    }
    
    public final void save() {
        File file = new File(PROPS_FILE);
        File dir = new File(this.PROPS_FOLDER);
        try {
        	if(!dir.exists())
        		dir.mkdirs();
            FileOutputStream fo = new FileOutputStream(file);
            appProps.storeToXML(fo, "jImBot properties");
            fo.close();
            Log.getDefault().info("Save preferences ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error saving preferences: ");
        }
    }

    public void registerProperties(Properties _appProps) {
        appProps = _appProps;
    }
    
    public String getProperty(String key) {
        return appProps.getProperty(key);
    }
    
    public String getStringProperty(String key) {
        return appProps.getProperty(key);
    }
    
    public String getProperty(String key, String def) {
        return appProps.getProperty(key,def);
    }
    
    public void setProperty(String key, String val) {
        appProps.setProperty(key,val);
    }
    
    public void setStringProperty(String key, String val) {
        appProps.setProperty(key,val);
    }
    
    public void setIntProperty(String key, int val) {
        appProps.setProperty(key,Integer.toString(val));
    }
    
    public void setBooleanProperty(String key, boolean val) {
        appProps.setProperty(key, val ? "true":"false");
    }
    
    public int getIntProperty(String key) {
        return Integer.parseInt(appProps.getProperty(key));
    }
    
    public boolean getBooleanProperty(String key) {
        return Boolean.valueOf(appProps.getProperty(key)).booleanValue();
    }

	public Properties getProps() {
		return appProps;
	}


   /**
     * Авто создание конфига
     * @param name
     */

        public void AddXmlConfig (String name){
    String Xml = "./services/" + name + "/" + name + ".xml";
    File NEW = new File(Xml);
        try {
            FileOutputStream fo = new FileOutputStream(NEW);
            appProps.storeToXML(fo, "jImBot properties");
            fo.close();
            Log.getLogger(name).info(name + ".xml был создан автоматически!");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error saving preferences: ");
        }
    }

}
