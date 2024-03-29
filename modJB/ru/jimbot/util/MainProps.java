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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Vector;
import ru.caffeineim.protocols.icq.integration.OscarInterface;
import ru.jimbot.modules.chat.ChatProps;
import ru.jimbot.table.UserPreference;

/**
 * Основные настройки бота
 * 
 * @author Prolubnikov Dmitry
 */
public class MainProps {
    /*Оставьте хоть какую нибудь благодарность авторам, не изменяя эту строчку внизу.*/
    public static final String VERSION = "jImBot v.0.4.0 (c)Spec (06/07/2009)\nBy modifying - fraer72\nVersion modJB v0.1(29/08/2010)";

    public static final String PROG_TITLE = "jImBot";
    public static final String PROPS_FILE = "./jimbot.xml";
    public static final String ENCODING = "windows-1251";
    private static Properties appProps;
    private static Vector servers = new Vector();
    private static String currentServer = "";
    private static int currentPort = 0;
    private static int countServer = 0;
    private static HashSet<String> ignor;
    private static HashSet<String> ignorIP;
    
    /** Creates a new instance of MainProps */
    public MainProps() {
    }
    
    public static final void setDefault() {
        appProps = new Properties();
        setStringProperty("icq.serverDefault","login.icq.com");
        setIntProperty("icq.portDefault",5190);
        setStringProperty("main.Socks5ProxyHost","");
        setStringProperty("main.Socks5ProxyPort","");
        setStringProperty("main.Socks5ProxyUser","");
        setStringProperty("main.Socks5ProxyPass","");
        setBooleanProperty("main.autoStart",true);
        setIntProperty("icq.AUTORETRY_COUNT",5);
        setBooleanProperty("icq.md5login",false);
        setBooleanProperty("main.StartHTTP",true);
        setStringProperty("http.user","admin");
        setStringProperty("http.pass","admin"); 
        setIntProperty("http.delay",10);
        setIntProperty("http.maxErrLogin",3);
        setIntProperty("http.timeErrLogin",10);
        setIntProperty("http.timeBlockLogin",20);
        setIntProperty("srv.servicesCount",1);
        setStringProperty("srv.serviceName0","AnekBot");
        setStringProperty("srv.serviceType0","anek");
        setBooleanProperty("main.checkNewVer", true);
        setBooleanProperty("log.service", true);
        setBooleanProperty("dellog.on.off", false);
        setIntProperty("dellog.time", 5 );
        setBooleanProperty("avto.db.on.off", true);
        setStringProperty("db.pass","");
    }
    
    public static UserPreference[] getUserPreference(){
        UserPreference[] p = {
            new UserPreference(UserPreference.CATEGORY_TYPE,"main", "Основные настройки"),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"main.checkNewVer","Уведомлять о новых версиях",getBooleanProperty("main.checkNewVer"),true),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"main.autoStart","Автозапуск при загрузке",getBooleanProperty("main.autoStart"),true),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"main.StartHTTP","Запускать HTTP сервер",getBooleanProperty("main.StartHTTP"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"http.delay","Время жизни HTTP сессии",getIntProperty("http.delay"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"http.maxErrLogin","Число ошибочных входов для блокировки",getIntProperty("http.maxErrLogin"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"http.timeErrLogin","Допустимый период между ошибками",getIntProperty("http.timeErrLogin"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"http.timeBlockLogin","Время блокировки входа",getIntProperty("http.timeBlockLogin"),true),
            new UserPreference(UserPreference.CATEGORY_TYPE,"main", "Настройки прокси"),
            new UserPreference(UserPreference.STRING_TYPE,"main.Socks5ProxyHost","Прокси хост",getStringProperty("main.Socks5ProxyHost"),true),
            new UserPreference(UserPreference.STRING_TYPE,"main.Socks5ProxyPort","Прокси порт",getStringProperty("main.Socks5ProxyPort"),true),
            new UserPreference(UserPreference.STRING_TYPE,"main.Socks5ProxyUser","Прокси пользователь",getStringProperty("main.Socks5ProxyUser"),true),
            new UserPreference(UserPreference.STRING_TYPE,"main.Socks5ProxyPass","Прокси пароль",getStringProperty("main.Socks5ProxyPass"),true),
            new UserPreference(UserPreference.CATEGORY_TYPE,"bot", "Настройки бота"),
            new UserPreference(UserPreference.STRING_TYPE,"icq.serverDefault","ICQ Сервер 1",getStringProperty("icq.serverDefault"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"icq.portDefault","ICQ Порт 1",getIntProperty("icq.portDefault"),true),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"icq.md5login","Безопасный логин",getBooleanProperty("icq.md5login"),true),
            new UserPreference(UserPreference.CATEGORY_TYPE,"log", "Настройки логов"),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"log.service","Включить/Выключить посервисное логирирование",getBooleanProperty("log.service"),true),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"dellog.on.off","Включить/Выключить авто-очистку логов",getBooleanProperty("dellog.on.off"),true),
            new UserPreference(UserPreference.INTEGER_TYPE,"dellog.time","Интервал чистки логов(в часах)",getIntProperty("dellog.time"),true),
            new UserPreference(UserPreference.CATEGORY_TYPE,"db_create", "Настройки авто создании базы"),
            new UserPreference(UserPreference.BOOLEAN_TYPE,"avto.db.on.off","Включить/Выключить авто создании базы данных",getBooleanProperty("avto.db.on.off"),true),
            new UserPreference(UserPreference.PASS_TYPE,"db.pass","Пароль root`a",getStringProperty("db.pass"),false)
        };
        return p;
    }


    /**
     * Вернет текущюю дату
     * @return
     */

    public static String currentData(){
        Date date = new Date(System.currentTimeMillis());
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTime(date);
        return currentCalendar.get(Calendar.DAY_OF_MONTH) + "/" + (currentCalendar.get(Calendar.MONTH) + 1)  + "/" + currentCalendar.get(Calendar.YEAR);
    }
    
    /**
     * Загружает игнор-лист из файла
     */
    public static void loadIgnorList(){
    	String s;
    	ignor = new HashSet<String>();
        try{
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("ignore.txt"),"windows-1251")); 
            while (r.ready()){
                s = r.readLine();
                if(!s.equals("")){
                    ignor.add(s);
                }
            }
            r.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Загружает заигноренные ip адреса из файла
     */
    public static void loadIgnorIPList(){
    	String s;
    	ignorIP = new HashSet<String>();
        try{
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("ignoreIP.txt"),"windows-1251"));
            while (r.ready()){
                s = r.readLine();
                if(!s.equals("")){
                    ignorIP.add(s);
                }
            }
            r.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }

    /**
     * Загрузка уинов с паролями
     * @param services
     * @return
     */

    public static String[] loadUinList(String services){
    String s = "";
    try{
    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("./services/" + services + "/uins.txt"), ENCODING));
    while (r.ready()){
    s += r.readLine() + '\n';
    }
    r.close();
    } catch (Exception ex){
    ex.printStackTrace();
    }
    return s.trim().split("\n");
    }

    /**
     * Проверка есть ли уины с паролями в файле
     * @param services
     * @return
     */

    public static boolean isUins(String services){
    return ChatProps.getInstance(services).loadText("./services/" + services + "/uins.txt").equals("");
    }

    /**
     * Проверка на существование файла с паролями
     * @param services
     * @return
     */

    public static boolean isUins2(String services){
    return new File("./services/" + services + "/uins.txt").exists();
    }

    /**
     * Создание файла с паролями
     * @param services
     */

    public static void uinsAddFile(String services){
    try{
    new File("./services/" + services + "/uins.txt").createNewFile();
    } catch (Exception ex){
    ex.printStackTrace();
    }
    }
    
    /**
     * Очистим файл
     * @param services
     */

    public static void uinsRecreate(String services){
    deleteFile("./services/" + services + "/uins.txt");
    uinsAddFile(services);
    }

    /**
     * Удаление файла
     * @param name
     */

    public static void deleteFile(String name) {
    File i = new File (name);
      if (i.exists()) i.delete();
    }
    
    /**
     * Уин в игноре?
     * @param uin
     * @return
     */
    public static boolean isIgnor(String uin){
    	if(ignor==null) return false;
    	return ignor.contains(uin);
    }

    /**
     * IP в игноре?
     * @param uin
     * @return
     */
    public static boolean isIpIgnor(String ip){
    	if(ignorIP==null) return false;
    	return ignorIP.contains(ip);
    }
    
    public static Properties getProps(){
        return appProps;
    }
    
    public static int getUserCount(){
    	return getIntProperty("users.Count");
    }

    public static String getUserName(int i){
    	return getStringProperty("users.Name"+i);
    }

    public static String getUserPass(int i) {
    	return getStringProperty("users.Pass"+i);
    }

    public static String getUserService(int i) {
    	return getStringProperty("users.Service"+i);
    }

    public static String getUserIp(int i) {
    	return getStringProperty("users.ip"+i);
    }

    public static String getUserInTime(int i) {
    	return getStringProperty("users.inTume"+i);
    }

 /**
  * Добавление пользователя
  * @param name
  * @param pass
  * @param service
  * @return
  */

    public static int addUser(String name, String pass, String service){
    	int c = getUserCount();
    	setIntProperty("users.Count", c+1);
    	setStringProperty("users.Name"+c, name);
    	setStringProperty("users.Pass"+c, pass);
        setStringProperty("users.Service"+c, service);
    	setStringProperty("users.ip"+c, "");
        setStringProperty("users.inTume"+c, "");
        return c;
    }

 /**
  * Изменение данных пользователя
  * @param name
  * @param pass
  * @param services
  */

    public static void changeUser(String name, String pass, String services){
        Integer a = 0;
        for(int i=0; i<(getUserCount()-1); i++){
        if(getUserName(i).equals(name)) a = i;
        }
        setStringProperty("users.Pass"+a, pass);
        setStringProperty("users.Service"+a, services);
    }

/**
 * Проверка логина и пароля
 * @param name
 * @param pass
 * @return
 */

    public static boolean testAuth(String name, String pass){
        Integer a = 0;
        boolean f = false;
        for(int i=0; i<getUserCount(); i++){
        if(getUserName(i).equals(name)){
        a = i;
        f = true;
        }
        }
        if(!f) return false;
        if(getUserPass(a).trim().equals(pass.trim())) return true;
        return false;
    }

 /**
  * Изменение не постоянных данных пользователя
  * @param name
  * @param ip
  * @param time
  */

   public static void changeUser_ipAndTime(String name, String ip, String time){
        Integer a = 0;
        for(int i=0; i<getUserCount(); i++){
        if(getUserName(i).equals(name)) a = i;
        }
        setStringProperty("users.ip"+a, ip);
        setStringProperty("users.inTume"+a, time);
        save();
   }

   /**
    * Изменение пароля пользователя
    * @param name
    * @param pass
    */
   
   public static void changeUserPass(String name, String pass){
        Integer a = 0;
        for(int i=0; i<getUserCount(); i++){
        if(getUserName(i).equals(name)) a = i;
        }    
        setStringProperty("users.Pass"+a, pass);
   }

   /**
    * Изменение полномочий пользователя
    * @param name
    * @param pass
    */

   public static void changeUserService(String name, String service){
        Integer a = 0;
        for(int i=0; i<getUserCount(); i++){
        if(getUserName(i).equals(name)) a = i;
        }
        setStringProperty("users.Service"+a, service);
   }

/**
 * Есть такой пользователь?
 * @param name
 * @return
 */

   public static boolean testUser(String name) {
        for(int i=0; i<getUserCount(); i++){
    	if(getUserName(i).equals(name)) return true;
        }
        return false;
   }

 /**
  * Удаление пользователя
  * @param name
  */

   public static void delUser(String name) {
    	// Сдвигаем элементы после удаленного на его место
    	boolean f = false;
    	for(int i=0; i<(getUserCount()-1); i++){
    	if(getUserName(i).equals(name))
    	f = true;
    	if(f){
    	setStringProperty("users.Name"+i, getUserName(i+1));
    	setStringProperty("users.Pass"+i, getUserPass(i+1));
        setStringProperty("users.Service"+i, getUserService(i+1));
    	setStringProperty("users.ip"+i, getUserIp(i+1));
        setStringProperty("users.inTume"+i, getUserInTime(i+1));
    	}
    	}
    	//Удаляем самый последний элемент
    	appProps.remove("users.Name"+(getUserCount()-1));
    	appProps.remove("users.Pass"+(getUserCount()-1));
        appProps.remove("users.Service"+(getUserCount()-1));
        appProps.remove("users.ip"+(getUserCount()-1));
        appProps.remove("users.inTume"+(getUserCount()-1));
    	setIntProperty("users.Count", getUserCount()-1);
    }

    public static int getServicesCount(){
    	return getIntProperty("srv.servicesCount");
    }
    
    public static String getServiceName(int i){
    	return getStringProperty("srv.serviceName"+i);
    }
    
    public static String getServiceType(int i) {
    	return getStringProperty("srv.serviceType"+i);
    }

    public static int addService(String name, String type){
    	int c = getServicesCount();
    	setIntProperty("srv.servicesCount", c+1);
    	setStringProperty("srv.serviceName"+c, name);
    	setStringProperty("srv.serviceType"+c, type);
    	return c;
    }

   public static String getType(String name){

        for(int i=0; i<getServicesCount(); i++){
        if(getServiceName(i).equals(name)) return getServiceType(i);
        }
        return null;
   }

    /**
     * Авто создание log4j.PROPERTIES для заданного сервиса
     * @param name
     */

    public static void AddLogProperties (String name){
    try {
    String config = Log.Log4jProperties;
    config = config.replace("$SERVICE", name);
    OutputStreamWriter NewFile = new OutputStreamWriter( new FileOutputStream( "./services/" + name + "/log4j.properties", true ), "windows-1251" );
    NewFile.write( config );
    NewFile.close();
    }
    catch ( Exception ex )
    {
    Log.getLogger(name).error( "Ошибка создания файла \"log4j.properties\" " , ex );
    }
    }

    /**
     * Авто создание директорий
     * @param name
     */

    public static void AddDirectory (String name, String type){
    if(type.equals("info")){
    String Directory_i = "./services/" + name + "/";
    File NEW = new File(Directory_i);
    if(!NEW.exists())
    NEW.mkdirs();
    return;
    }
    String Directory = "./services/" + name + "/scripts/command/";
    File NEW = new File(Directory);
    if(!NEW.exists())
    NEW.mkdirs();
    String Directory_m = "./services/" + name + "/msg/";
    File NEW2 = new File(Directory_m);
    if(!NEW2.exists())
    NEW2.mkdirs();
    }

 
    /**
     * Копирование скриптов
     * @param name - имя сервиса
     */

  public static synchronized void CopyingScript(String name, String type){
  String[] files = null;
  if(type.equals("chat")){
  files = new String[4];
  files[0] = "admin.bsh";
  files[1] = "messages.bsh";
  files[2] = "start.bsh";
  files[3] = "stop.bsh";
  }else{
  files = new String[3];
  files[0] = "main.bsh";
  files[1] = "start.bsh";
  files[2] = "stop.bsh";
  }
  try{
     for(int i=0; i < files.length; i++){
     InputStream stream = MainProps.class.getClassLoader().getResourceAsStream("ru/jimbot/scripts/" + type + "/" + files[i]);
     BufferedInputStream in = new BufferedInputStream(stream);
     BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("./services/" + name+  "/scripts/" + files[i]));
          int len = 0, b = 0;
     while ((b = in.read()) != -1) {
        out.write(b);
        len += b;
     }
     in.close();
     out.flush();
     out.close();
     }
    } catch (Exception ex){
    Log.getDefault().error("Errore copyring script - " + ex.getMessage());
    }
  }

  public static synchronized void CopyingMessagesXml(String name){
  try{
     InputStream stream = MainProps.class.getClassLoader().getResourceAsStream("ru/jimbot/msg/messages.xml");
     BufferedInputStream in = new BufferedInputStream(stream);
     BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream("./services/" + name+  "/msg/messages.xml"));
     int len = 0, b = 0;
     while ((b = in.read()) != -1) {
     out.write(b);
     len += b;
     }
     in.close();
     out.flush();
     out.close();
      } catch (Exception ex){
    Log.getDefault().error("Errore copyring messages - " + ex.getMessage());
    }
  }


    public static void delService(String name) {
    	// Сдвигаем элементы после удаленного на его место
    	boolean f = false;
    	for(int i=0; i<(getServicesCount()-1); i++){
    		if(getServiceName(i).equals(name))
    			f = true;
    		if(f){
    			setStringProperty("srv.serviceName"+i, getServiceName(i+1));
    			setStringProperty("srv.serviceType"+i, getServiceType(i+1));
    		}
    	}
    	//Удаляем самый последний элемент
    	appProps.remove("srv.serviceName"+(getServicesCount()-1));
    	appProps.remove("srv.serviceType"+(getServicesCount()-1));
    	setIntProperty("srv.servicesCount", getServicesCount()-1);
    }
    
    
    public static String getServer() {
        if(currentServer.equals("")) 
            currentServer = getStringProperty("icq.serverDefault");
        return currentServer;
    }
    
    public static int getPort(){
        if(currentPort==0) 
            currentPort = getIntProperty("icq.portDefault");
        return currentPort;
    }
    
    public static void nextServer(){
        if(servers.size()==0) return;
        countServer++;
        if(countServer>=servers.size()) countServer=0;
        String s = servers.get(countServer).toString();
        if(s.indexOf(":")<0){ 
            currentPort = getIntProperty("icq.portDefault");
            currentServer = s;
        } else{
            currentPort = Integer.parseInt(s.split(":")[1]);
            currentServer = s.split(":")[0];
        }
    }
    
    /**
     * Загрузка списка ICQ серверов из файла
     */
    public static void loadServerList(){
        String s;
        try{
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("servers.txt"),"windows-1251")); 
            while (r.ready()){
                s = r.readLine();
                if(!s.equals("")){
                    servers.add(s);
                }
            }
            r.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }        
    }
    
    public static boolean isProxy(){
        return !getStringProperty("main.Socks5ProxyHost").equals("");
    }
        
    public static String[] getProxy(){
        String[] s = new String[4];
        s[0] = getStringProperty("main.Socks5ProxyHost");
        s[1] = getStringProperty("main.Socks5ProxyPort");
        if(s[1].equals("")){
            s[1] = "0";
        }
        s[2] = getStringProperty("main.Socks5ProxyUser");
        s[3] = getStringProperty("main.Socks5ProxyPass");
        return s;
    }
    

  
    public static boolean isHide(){
        return Boolean.valueOf(getProperty("isHide","true")).booleanValue();
    }


    public static final void load() {
        String fileName = PROPS_FILE;
        File file = new File(fileName);
        setDefault();
        loadIgnorList();
        loadIgnorIPList();
        try {
            FileInputStream fi = new FileInputStream(file);
            appProps.loadFromXML(fi);
            fi.close();
            Log.getDefault().info("Load preferences ok");
            loadServerList();
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error opening preferences: ");
        }
    }
    
    public static final void save() {
        String fileName = PROPS_FILE;
        File file = new File(fileName);
        try {
            FileOutputStream fo = new FileOutputStream(file);
            appProps.storeToXML(fo, "jImBot properties");
            fo.close();
            Log.getDefault().info("Save preferences ok");
        } catch (Exception ex) {
            ex.printStackTrace();
            Log.getDefault().error("Error saving preferences: ");
        }
    }
    
    /**
     * Читает текстовый файл по URL
     * @param file - фаил
     * @param encoding - кодировка
     * @return
     */
    public static String getStringFromHTTP(String file, String encoding){
        String s = "";
        try {
            URL url = new URL(file);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty ( "User-agent", "JimBot/0.4 (Java" + 
                    "; U;" + System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version") +
                    "; ru; " + System.getProperty("java.vendor") + " " + System.getProperty("java.version") +
                    ")");
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
            byte[] b = new byte[1024];
            int count = 0;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            while ((count=bis.read(b)) != -1)
                bout.write(b, 0, count);
            bout.close();
            bis.close();
            conn.disconnect();
            s = bout.toString(encoding);
        } catch (Exception ex) {
            Log.getDefault().error("Ошибка HTTP при чтении новой версии", ex);
        }
        return s;
    }

     /**
      * Создание файла
      * @param Type
      * @param Way
      * @param text
      * @param encoding
      * @throws UnsupportedEncodingException
      */
    

     public static void AddFail(String Type, String Way , String text, String encoding ) throws UnsupportedEncodingException {
     try {
     OutputStreamWriter NewFile = new OutputStreamWriter(new FileOutputStream(Way + "." + Type, true), encoding);
     NewFile.write(text);
     NewFile.close();
     }catch (Exception ex){
     Log.getDefault().error("Ошибка создания файла: ", ex);
     }
     }


    /**
     * Число?
     * @param msg
     * @return
     */

    public static boolean testInteger(String msg){
    Integer answer;
    try{
    answer = Integer.parseInt(msg);
    }catch(NumberFormatException e){
    return false;
    }
    return true;
    }

    public static String getAbout(){
    return VERSION +
        "\nВерсия icq библиотеки: " + OscarInterface.getVersion() +
        "\nПоддержка проекта: http://jimbot.ru" +
        "\nProject - http://code.google.com/p/modjimbot/";
    }

    public static void registerProperties(Properties _appProps) {
        appProps = _appProps;
    }
    
    public static String getProperty(String key) {
        return appProps.getProperty(key);
    }
    
    public static String getStringProperty(String key) {
        return appProps.getProperty(key);
    }
    
    public static String getProperty(String key, String def) {
        return appProps.getProperty(key,def);
    }
    
    public static void setProperty(String key, String val) {
        appProps.setProperty(key,val);
    }
    
    public static void setStringProperty(String key, String val) {
        appProps.setProperty(key,val);
    }
    
    public static void setIntProperty(String key, int val) {
        appProps.setProperty(key,Integer.toString(val));
    }
    
    public static void setBooleanProperty(String key, boolean val) {
        appProps.setProperty(key, val ? "true":"false");
    }
    
    public static int getIntProperty(String key) {
        return Integer.parseInt(appProps.getProperty(key));
    }
    
    public static boolean getBooleanProperty(String key) {
        return Boolean.valueOf(appProps.getProperty(key)).booleanValue();
    }
}
