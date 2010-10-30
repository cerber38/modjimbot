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

package ru.jimbot.modules;


import java.io.File;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.anek.AnekCommandProc;
import ru.jimbot.modules.chat.ChatCommandProc;
import ru.jimbot.modules.chat.RobAdmin;
import ru.jimbot.modules.http.HttpConnection;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;
import bsh.Interpreter;


/**
 * Класс для работы с макросами. Запуск, получение списка, обработка ошибок,
 * передача параметров и данных
 * @author Prolubnikov Dmitry
 */
public class WorkScript {
private static String SCRIPT_FOLDER = "/scripts/"; // Папка с макросами
private ScriptCash scripts = new ScriptCash();
private static ConcurrentHashMap<String,WorkScript> instances = new ConcurrentHashMap<String,WorkScript>();
private static WorkScript mainInst = new WorkScript("");
private String sn=""; // Имя сервиса
long StartScriptTime = System.currentTimeMillis();



    public WorkScript(String s) {
        sn = s;
    }

    /**
     * Возвращает экземпляр WorkScript для нужного сервиса
     * @param name
     * @return
     */
    public static WorkScript getInstance(String name){
        if(name.equals("")) return mainInst;
        if(!instances.containsKey(name)) instances.put(name, new WorkScript(name));
        return instances.get(name);
    }

    /**
     * Возвращает кеш со скриптами
     * @return
     */
    public ScriptCash getScripts(){
    	return scripts;
    }





    /**
     * Запуск скрипта.
     * @param name
     * @param in
     * @param srv
     * @return
     */
    public String startScript(String name, String in, AbstractServer srv){
    String s = "";
    try{
    Interpreter bsh = new Interpreter();
    bsh.set("inText", in);
    bsh.set("proc", null);
    bsh.set("uin", "");
    bsh.set("srv", srv);
    bsh.eval(scripts.getScript("./services/"+sn+SCRIPT_FOLDER + name + ".bsh"));
    s = bsh.get("out").toString();
    } 
    catch (Exception ex)
    {
    Log.getLogger(sn).error("Ошибка скрипта: \"" + name + "\" - " + ex.getMessage());
    }
    return s;
    }

    /**
     * Запуск скрипта обработки сообщения до парсера команд.
     * @param msg - Текст сообщения
     * @return - Обработанное сообщение
     */
    public  String startMessagesScript(String msg, AbstractServer srv, String uin){
    String s = msg;
    if(!new File("./services/"+sn+"/scripts/messages.bsh").exists()) return s;
    try {
    Interpreter bsh = new Interpreter();
    bsh.set("msg", msg);
    bsh.set("srv", srv);
    bsh.set("uin", uin);
    bsh.eval(scripts.getScript("./services/"+sn+"/scripts/messages.bsh"));
    s = bsh.get("msg").toString();
    }
    catch (Exception ex)
    {
    Log.getLogger(sn).error("Ошибка скрипта обработки сообщений: " + ex.getMessage());
    }
    return s;
    }

    /**
     * Запуск скрипта админа
     * @param name
     * @param srv
     * @param adm
     * @return
     */
    public String startAdminScript(RobAdmin adm) {
    String s = "";
    try{
    Interpreter bsh = new Interpreter();
    bsh.set("adm", adm);
    bsh.eval(scripts.getScript("./services/" + sn + "/scripts/admin.bsh"));
    } 
    catch (Exception ex)
    {
    ex.printStackTrace();
    Log.getLogger(sn).error("Ошибка скрипта админа: " + ex.getMessage());
    }
    return s;
    }

    /**
     * Вызов скрипта для обработки  расширений команд чата
     * @param scriptName - имя файла скрипта
     * @param msg - сообщение, содержащее команду
     * @param uin - уин, отправившего команду
     * @param proc - ссылка на процесс обработки icq
     * @return - текстовый результат работы скрипта
     */
    public String startChatCommandScript(String scriptName, String msg,
    String uin, IcqProtocol prot, ChatCommandProc proc){
    try{
    Interpreter bsh = new Interpreter();
    bsh.set("in", "run");
    bsh.set("cmd", proc);
    bsh.set("uin", uin);
    bsh.set("msg", msg);
    bsh.set("proc", prot);
    bsh.eval(scripts.getScript("./services/"+sn+SCRIPT_FOLDER + "command/" + scriptName + ".bsh"));
    }
    catch (Exception ex)
    {
    Log.getLogger(sn).error("Ошибка скрипта: \"" + scriptName + "\" - " + ex.getMessage());
    return ex.getMessage();
    }
    return "";
    }

    /**
     * Установка выбранного скрипта (дополнение списка команд и полномочий чата)
     * @param name
     * @param proc
     * @return
     */
    public String installChatCommandScript(String name, ChatCommandProc proc){
    String t = "";
    try{
    Interpreter bsh = new Interpreter();
    bsh.set("in", "install");
    bsh.set("name", name);
    bsh.set("cmd", proc);
    bsh.eval(scripts.getScript("./services/" + sn + SCRIPT_FOLDER + "command/" + name + ".bsh"));
    t = bsh.get("out").toString();
    } 
    catch (Exception ex)
    {
    Log.getLogger(sn).error("Ошибка при установке скрипта: \"" + name + "\" - " + ex.getMessage());
    return ex.getMessage();
    }
    return t;
    }

    /**
     * Установка всех скриптов
     * @param proc
     */
    public void installAllChatCommandScripts(ChatCommandProc proc){
    Log.getLogger(sn).info("Начинаю установку скриптов для " + sn);
    // Формируем список файлов
    Vector<String> v = new Vector<String>();
    File f = new File("./services/" + sn + SCRIPT_FOLDER + "command/");
    if(!f.exists()) return;
    if(!f.isDirectory()) return;
    File[] fs = f.listFiles();
    if(fs.length>0)
    for(int i=0;i<fs.length;i++){
    if(fs[i].isFile())
    if(getExt(fs[i].getName()).equals("bsh"))
    v.add(getName(fs[i].getName()));
    }
    try {
    for(int i=0; i<v.size(); i++){
    Log.getLogger(sn).info("Устанавливаю скрипт: " + v.get(i));
    Log.getLogger(sn).info("Завершено: " + installChatCommandScript(v.get(i),proc));
    }
    } catch (Exception ex){
    ex.printStackTrace();
    }
    }

    public String startAnekScript(String name, IcqProtocol proc, AnekCommandProc cproc, String uin, String msg){
    try {
    Interpreter bsh = new Interpreter();
    bsh.set("proc", proc);
    bsh.set("uin", uin);
    bsh.set("msg", msg);
    bsh.set("cp", cproc);
    bsh.eval(scripts.getScript("./services/"+sn+SCRIPT_FOLDER + name + ".bsh"));
    } 
    catch (Exception ex)
    {
    ex.printStackTrace();
    return ex.getMessage();
    }
    return "";
    }

    public String startCommandScript(String name, IcqProtocol proc, AbstractServer srv, String uin, String arg){
    try{
    Interpreter bsh = new Interpreter();
    bsh.set("inText", arg);
    bsh.set("proc", proc);
    bsh.set("uin", uin);
    bsh.set("srv", srv);
    bsh.eval(scripts.getScript("./services/"+sn+SCRIPT_FOLDER + name + ".bsh"));
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    return ex.getMessage();
    }
    return "";
    }

    /**
     * Возвращает список HTTP скриптов
     */
    public Vector<String> listHTTPScripts(){
    Vector<String> v = new Vector<String>();
    try{
    File f = new File("./scripts/http/");
    File[] fs = f.listFiles();
    if(fs.length<0) return v;
    for(int i=0;i<fs.length;i++){
    if(fs[i].isFile())
    if(getExt(fs[i].getName()).equals("bsh"))
    v.add(getName(fs[i].getName()));
    }   
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    }
    return v;
    }

    /**
     * Запуск HTTP-скрипта
     * @param name
     * @param con
     */
    public HttpConnection startHTTPScript(String name, HttpConnection con){
    HttpConnection c = con;
    try{
    Interpreter bsh = new Interpreter();
    bsh.set("con", con);
    bsh.eval(scripts.getScript("./scripts/http/" + name + ".bsh"));
    c = (HttpConnection)bsh.get("con");
    } 
    catch (Exception ex)
    {
    ex.printStackTrace();
    Log.getLogger(sn).http("Ошибка запуска HTTP-скрипта " + name);
    }
    return c;
    }

    private String getName(String s){
        if(s.indexOf(".")<0)
            return s;
        else
            return s.replace('.', ':').split(":")[0];
    }

    private String getExt(String s){
        if(s.indexOf(".")<0)
            return "";
        else
            return s.replace('.', ':').split(":")[1];
    }
}
