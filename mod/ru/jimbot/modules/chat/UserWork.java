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

package ru.jimbot.modules.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;
import ru.jimbot.Messages;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class UserWork {
    public static final int STATE_NO_REG = 0;
    public static final int STATE_NO_CHAT = 1;
    public static final int STATE_CHAT = 2;
    public static final int STATE_OFFLINE = 3;
    public static final int STATE_BANNED = -1;
    public static final int STATE_CAPTCHA = -2;
    
    private int currCountUser=0;
    public DBChat db;
    //Кэш для базы юзеров
    private ConcurrentHashMap <String,Users> uc;
    // Индекс ид-уин
    private ConcurrentHashMap <Integer,String> uu;
    //Кэш для объектов авторизации
    private ConcurrentHashMap <Integer,HashSet<String>> auth;
    private String serviceName = "";
    private String host, name, user, pass;
    private RoomWork rw;
    private ConcurrentHashMap <Integer,String> ww;
    //Кэш кланов
    private ClanWork rc;

    //public ChatServer srv;
    
    /** Creates a new instance of UserWork */
    public UserWork(String name) {
        try{
        	serviceName = name;
        	host = ChatProps.getInstance(name).getStringProperty("db.host");
        	this.name = ChatProps.getInstance(name).getStringProperty("db.dbname");
        	user = ChatProps.getInstance(name).getStringProperty("db.user");
        	pass = ChatProps.getInstance(name).getStringProperty("db.pass");
            db = new DBChat(name);
            db.openConnection(host, this.name, user, pass);
            uc = new ConcurrentHashMap<String,Users>();
            uu = new ConcurrentHashMap<Integer,String>();
            auth = new ConcurrentHashMap<Integer,HashSet<String>>();
            currCountUser=0;
            clearStatusUsers();
            rw = new RoomWork(db);
            rw.fillCash();
            rc = new ClanWork( db );
            rc.StartClanCash();
            ww = new ConcurrentHashMap<Integer,String>();
        } catch (Exception ex){
            ex.printStackTrace();
        }
    }
    

    
    public void closeDB(){
        db.shutdown();
    }

    public int count() {
        if (currCountUser==0) 
            currCountUser = (int)db.getLastIndex("users");
        return currCountUser;
    }
    





    
    /**
     * Возвращает данные о пользователе из БД
     * @param id - ИД пользователя
     * @return
     */
    private Users getUserFromDB(int id){
    	Vector v = db.getObjectVector("select * from users where id="+id);
    	Users u = new Users();
    	if(v.size()>0) u = (Users)v.get(0);
    	if(u.id!=0) u.group = getUserGroup(u.id);
    	return u;
    }
    
    /**
     * Возвращает данные о пользователе из БД
     * @param sn - УИН пользователя
     * @return
     */
    private Users getUserFromDB(String sn){
    	Vector v = db.getObjectVector("select * from users where sn='"+sn+"'");
    	Users u = new Users();
    	if(v.size()>0) u = (Users)v.get(0);
    	if(u.id!=0) u.group = getUserGroup(u.id);
    	return u;
    }



    /**
     * Очистка статуса юзеров после перезапуска бота
     */
    public void clearStatusUsers(){
        db.executeQuery("update users set state=1 where state=3");
        db.executeQuery("update users set state=1 where state=2");
//        db.commit();
    }
    
    /**
     * Определение зареган юзер или нет
     * @param uin
     * @return
     */
    public boolean testUser(String uin){
        if(uc.containsKey(uin)){
        	return uc.get(uin).state!=0;
        }
        Users u = getUserFromDB(uin);
        if(u.id==0){
        	u.sn = uin;
        	uc.put(u.sn, u);
        	return false;
        } else {
        	uu.put(u.id, u.sn);
        	uc.put(u.sn, u);
        	return true;
        }
    }
    
    /**
     * Выполнение статистических запросов
     */
    
    /**
     * Вывод последних ников пользователя
     */
    public String getUserNicks(int user_id){
    	String s = Messages.getInstance(serviceName).getString("UserWork.getUserNicks.0");
        try{
            PreparedStatement pst = db.getDb().prepareStatement("select t.msg from events t where t.type='REG' and t.user_id=" + 
                    user_id + " order by t.time desc");
            ResultSet rs = pst.executeQuery();
            for(int i=1;i<11;i++){
                if(rs.next()){
                    s += i + ") " + rs.getString(1) + "; ";
                } else {
                    break;
                }
            }
            rs.close();
            pst.close();            
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return s;
    }
    

    
    /**
     * Вывод истории киков
     */
    public String getKickHist() {
   	String s = Messages.getInstance(serviceName).getString("UserWork.getKickHist.0")+"\n";
        try {
            PreparedStatement pst = db.getDb().prepareStatement("select time, user_id, user_id2, msg from events where type='KICK' order by time desc");
            ResultSet rs = pst.executeQuery();
            for(int i=1;i<21;i=i+1){
                if(rs.next()){
                    s += i + ". - |"+rs.getTimestamp(1)+"| "+
                            rs.getInt(2)+"("+(rs.getInt(2)==0 ? "Admin" : getUser(rs.getInt(2)).localnick)+"), " + 
                            rs.getInt(3)+"("+(rs.getInt(3)==0 ? "Admin" : getUser(rs.getInt(3)).localnick)+"), " +
                            rs.getString(4)+'\n';
                }
            }
            rs.close();
            pst.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }
    
    /**
     * Вывод истории банов
     */
    public String getBanHist() {
    	String s = Messages.getInstance(serviceName).getString("UserWork.getBanHist.0")+"\n";
        try {
            PreparedStatement pst = db.getDb().prepareStatement("select time, user_id, user_id2, msg from events where type='BAN' order by time desc");
            ResultSet rs = pst.executeQuery();
            for(int i=1;i<21;i=i+1){
                if(rs.next()){
                    s += i + ". - |"+rs.getTimestamp(1)+"| "+
                            rs.getInt(2)+"("+(rs.getInt(2)==0 ? "Admin" : getUser(rs.getInt(2)).localnick)+"), " + 
                            rs.getInt(3)+"("+(rs.getInt(3)==0 ? "Admin" : getUser(rs.getInt(3)).localnick)+"), " +
                            rs.getString(4)+'\n';
                }
            }
            rs.close();
            pst.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return s;
    }
    
    /**
     * Количество посещений чата за последние сутки
     */
    public int statUsersCount(){
        long last = System.currentTimeMillis() - 1000*3600*24;
        int r=0;
        try{
            PreparedStatement pst = db.getDb().prepareStatement("SELECT count( DISTINCT t.user_id ) " +
                    "FROM log t WHERE t.user_id <>0 AND t.time >= ?");
            pst.setTimestamp(1,new Timestamp(last));
            ResultSet rs = pst.executeQuery();
            if(!rs.next()) 
                r = 0;
            else
                r = rs.getInt(1);
            rs.close();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return r;
    }
    
    /**
     * Количество кикнутых юзеров
     */
    public int statKickUsersCount(){
        long last = System.currentTimeMillis() - 1000*3600*24;
        int r=0;
        try{
            PreparedStatement pst = db.getDb().prepareStatement("select count( DISTINCT t.user_id) from log t where user_id<>0 " +
                    "and type='KICK' and time>=?");
            pst.setTimestamp(1,new Timestamp(last));
            ResultSet rs = pst.executeQuery();
            if(!rs.next()) 
                r = 0;
            else
                r = rs.getInt(1);
            rs.close();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return r;
    }
    
    /**
     * Количество забаненых юзеров
     */
    public int statBanUsersCount(){
        long last = System.currentTimeMillis() - 1000*3600*24;
        int r=0;
        try{
            PreparedStatement pst = db.getDb().prepareStatement("select count( DISTINCT t.user_id) from log t where user_id<>0 " +
                    "and type='BAN' and time>=?");
            pst.setTimestamp(1,new Timestamp(last));
            ResultSet rs = pst.executeQuery();
            if(!rs.next()) 
                r = 0;
            else
                r = rs.getInt(1);
            rs.close();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return r;
    }


    /**
     * Количество киков
     */
    public int statKickCount(){
        long last = System.currentTimeMillis() - 1000*3600*24;
        int r=0;
        try{
            PreparedStatement pst = db.getDb().prepareStatement("select count(*) from log t where type = 'KICK' " +
                    "and time>=?");
            pst.setTimestamp(1,new Timestamp(last));
            ResultSet rs = pst.executeQuery();
            if(!rs.next()) 
                r = 0;
            else
                r = rs.getInt(1);
            rs.close();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return r;
    }
    
    /**
     * Количество сообщений
     */
    public int statMsgCount(){
        long last = System.currentTimeMillis() - 1000*3600*24;
        int r=0;
        try{
            PreparedStatement pst = db.getDb().prepareStatement("select count(*) from log t where type = 'OUT' and user_id<>0 " +
                    "and time>=?");
            pst.setTimestamp(1,new Timestamp(last));
            ResultSet rs = pst.executeQuery();
            if(!rs.next()) 
                r = 0;
            else
                r = rs.getInt(1);
            rs.close();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return r;
    }
    
    /**
     * Пятерка лидеров
     */
    public String statUsersTop(){
        long last = System.currentTimeMillis() - 1000*3600*24;
        String s = "";
        try{
            PreparedStatement pst;
                pst = db.getDb().prepareStatement("select t.user_id, sum(length(t.msg)) cnt " +
                        "from log t where t.type = 'OUT' and t.user_id<>0 and time>=? " +
                        "group by t.user_id order by cnt desc limit 0,5");
            pst.setTimestamp(1,new Timestamp(last));
            ResultSet rs = pst.executeQuery();
            for(int i=1;i<6;i=i+1){
                if(rs.next()){
                    Users us = getUser(rs.getInt(1));
                    s += i + ". - |" + us.id + "|" + us.localnick + " - " + rs.getInt(2) + ";\n";
                } else {
                    break;
                }
            }
            rs.close();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return s;
    }
    
    
    /**
     * Статистика по уинам
     */
    public String getUinStat(){
    	String s = Messages.getInstance(serviceName).getString("UserWork.getUinStat.0") + "\n";
        String[] uins = new String[ChatProps.getInstance(serviceName).uinCount()];
        int[] cnt = new int[ChatProps.getInstance(serviceName).uinCount()];
        for(int i=0;i<ChatProps.getInstance(serviceName).uinCount();i++){
            uins[i] = ChatProps.getInstance(serviceName).getUin(i);
            cnt[i] = 0;
        }
        int a=0;
        Iterator <Users> it = uc.values().iterator();
        while(it.hasNext()){
            Users u = it.next();
            if(u.state == UserWork.STATE_CHAT){
                a++;
                for(int i=0;i<uins.length;i++){
                    if(uins[i].equals(u.basesn)) cnt[i]++;
                }
            }
        }
        for(int i=0;i<uins.length;i++){
            s += i + " - " + uins[i] + " = " + cnt[i] + "\n";
        }
        return s+"Всего в чате: "+a;
    }
    
    public String getFreeUin(){
        String[] uins = new String[ChatProps.getInstance(serviceName).uinCount()];
        int[] cnt = new int[ChatProps.getInstance(serviceName).uinCount()];
        for(int i=0;i<ChatProps.getInstance(serviceName).uinCount();i++){
            uins[i] = ChatProps.getInstance(serviceName).getUin(i);
            cnt[i] = 0;
        }
        Iterator <Users> it = uc.values().iterator();
        while(it.hasNext()){
            Users u = it.next();
            if(u.state == UserWork.STATE_CHAT){
                for(int i=0;i<uins.length;i++){
                    if(uins[i].equals(u.basesn)) cnt[i]++;
                }
            }
        }
        int k=0;
        for(int i=1;i<cnt.length;i++){
             if(cnt[i]<cnt[k]) k = i;
        }
        return uins[k];
    }
    
    public int getCurrUinUsers(String uin){
        Iterator <Users> it = uc.values().iterator();
        int i=0;
        while(it.hasNext()){
            Users u = it.next();
            if(u.state == UserWork.STATE_CHAT && u.basesn.equals(uin)) i++;
        }
        return i;
    }
    
    public Users getUser(int id) {
    	if(uu.containsKey(id)){
    		return uc.get(uu.get(id));
    	}
    	Users u = getUserFromDB(id);
    	if(u.id==0) return u; // Нет юзера в БД, или глюк :)
    	uu.put(id, u.sn);
    	uc.put(u.sn, u);
    	return u;
    }
    
    public Users getUser(String uin) {
    	if(uc.containsKey(uin)) return uc.get(uin);
    	Users u = getUserFromDB(uin);
    	if(u.id==0) return u; // Нет юзера в БД, или глюк :)
    	uu.put(u.id, u.sn);
    	uc.put(u.sn, u);
    	return u;
    }
    
    public Vector getUsers(int state) {
        return db.getObjectVector("select * from users where state = " + state);
    }
    
    public String getUserInfo(int id) {
        Users u = getUser(id);
        if(u.state==-1) 
            return u.getInfo()+"\n"+getBanDesc(u.sn);
        else
            return u.getInfo();
    }
    
    public String getUserInfo(String uin) {
        Users u = getUser(uin);
        if(u.state==-1) 
            return u.getInfo()+"\n"+getBanDesc(uin);
        else
            return u.getInfo();
    }
    
    /**
     * Данный ник уже используется?
     * @param n
     * @return
     */
    public boolean isUsedNick(String n){
    	String q = "select count(*) from users where localnick like '"+n+"'";
    	Vector<String[]> v = db.getValues(q);
//    	System.out.println(v.get(0)[0]);
    	return !v.get(0)[0].equals("0");
    }
    
    /**
     * Число смен ника за последние 24 часа
     * @param id
     * @return
     */
    public int getCountNickChange(int id) {
    	String q = "SELECT count(*) FROM `events` WHERE user_id="+id+" and type='REG' and (to_days( now( ) ) - to_days( time )) <1";
    	Vector<String[]> v = db.getValues(q);
//    	System.out.println(Integer.parseInt(v.get(0)[0]));
    	return Integer.parseInt(v.get(0)[0]);
    }
    
    /**
     * Причина бана
     * @param uin
     * @return
     */
    public String getBanDesc(String uin) {
        Vector<String[]> v = db.getValues("SELECT user_id2,time, msg FROM `events` " +
                "where type='BAN' and user_sn='"+uin+"'order by time desc");
        if(v.size()==0) return "";
        return getUser(Integer.parseInt(v.get(0)[0])).localnick+", "+
                v.get(0)[1]+", "+v.get(0)[2];
    }
    
    /**
     * Перечень активных киков из базы
     * @return
     */
    public Vector<Users> getKickList() {
    	return db.getObjectVector("select * from users where lastkick>now()");
    }
    
    public String listUsers(){
        String s = "Users list\n";
        Iterator <Users> it = uc.values().iterator();
        while(it.hasNext()){
            Users u = it.next();
            if(u.state==STATE_BANNED) {
                s += u.id + ", " + u.localnick + ", " +
                    u.sn + ", " + u.nick + ", " +
                    u.basesn + ", " + getStateText(u.state) + '\n';
            }             
        }
        return s;
    }
    
    private String getStateText(int state) {
        switch (state){
            case 1: return "Вне чата";
            case 2: return "В чате";
            case 3: return "Вне сети";
            case -1: return "Забанен";
        }
        return "";
    }
    
    public String listUsers_a(){
        String s = "Пользователи в чате:\n";
       for(int i=1;i<count();i++){
            try{
                Users u = new Users();
                u=getUser(i);
                if(u.state==2){
                    s += i + ", " + u.localnick + '\n';
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return s;        
    }
    
    public synchronized int addUser(Users u) {
        int id = count();
        
        u.id = id;
        uc.put(u.sn,u); //Кэшируем юзера
        db.insertObject(u);
//        db.commit();
        currCountUser++;
        return id;
    }
    
    public void updateUser(Users u) {
        uc.put(u.sn,u); //кэшируем юзера
        db.updateObject(u);
//        db.commit();
    }
    
    public void reqUserInfo(String uin, IcqProtocol proc) {
        Users u = new Users();
        u.sn = uin;
        addUser(u);
        Log.info("Add user " + u.sn + ", " + u.nick + ", id=" + u.id);
    }
    

    

    
    /**
     * Работа с объектами полномочий
     */
    
    /**
     * Поиск значения нужного параметра
     */
    public String getUserPropsValue(int user_id, String name){
        try{
            Vector v=db.getUserProps(user_id);
            for(int i=0;i<v.size();i++){
                String[] ss = (String[])v.get(i);
                if(ss[0].equals(name)) return ss[1];
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }
    
    public boolean setUserPropsValue(int user_id,String name, String value){
        return db.setUserProps(user_id, name, value);
    }
    
    /**
     * Определение группы по умолчанию
     */
    public String getUserGroupDefault(){
        String s = ChatProps.getInstance(serviceName).getStringProperty("auth.groups");
        if(s.equals("")) return "user"; // Если по ошибке группы не определены
        return s.split(";")[0];
    }
    
    /**
     * Определение группы пользователя
     */
    public String getUserGroup(int user_id){
        if(db.existUserProps(user_id)){
            String s = getUserPropsValue(user_id,"group");
            if(s.equals(""))
                return getUserGroupDefault();
            else
                return s;
        } else
            return getUserGroupDefault();
    }
    
    /**
     * Определение доступных пользователю полномочий
     */
    public HashSet<String> getUserAuthObjects(int user_id){
        if(auth.containsKey(user_id)){
            return auth.get(user_id);
        }
        String group = ChatProps.getInstance(serviceName).getStringProperty("auth.group_"+getUserGroup(user_id));
        String grant = getUserPropsValue(user_id,"grant");
        String revoke = getUserPropsValue(user_id,"revoke");
        HashSet<String> r = new HashSet<String>();
        if(group==null) return r;
        for(String s:group.split(";")){
        	r.add(s);
        }
        for(String s:grant.split(";")){
        	r.add(s);
        }
        for(String s:revoke.split(";")){
        	if(r.contains(s)) r.remove(s);
        }
        auth.put(user_id, r);
        return r;
    }

  


    /**
     * Вывод отчета о пользователе
     */
    public String getUserAuthInfo(int user_id){
        String s = Messages.getInstance(serviceName).getString("UserWork.getUserAuthInfo.0", new Object[] {user_id, getUserGroup(user_id)});
        for(String sc:getUserAuthObjects(user_id)){
        	s += sc + ";";
        }
        return s;
    }
    
    public String getUserAuthInfo(String sn){
        if(ChatProps.getInstance(serviceName).testAdmin(sn))
            return "Полномочия пользователя не ограничены";
        return getUserAuthInfo(getUser(sn).id);
    }




    /**
     * Проверка полномочий пользователя
     */
    public boolean authorityCheck(int user_id, String obj){
        return getUserAuthObjects(user_id).contains(obj);
    }

    public boolean authorityCheck(String sn, String obj){
        if(ChatProps.getInstance(serviceName).testAdmin(sn)) return true;
        return authorityCheck(getUser(sn).id,obj);
    }
    
    /**
     * Добавление прав пользователю
     */
    public boolean grantUser(int user_id, String authObj){
    	HashSet<String> r = getUserAuthObjects(user_id);
    	if(r.contains(authObj)) return false;
    	r.add(authObj);
    	auth.put(user_id, r);
    	saveUserAuth(user_id);
    	return true;
    }
    
    /**
     * Сохраняем права пользователя в БД
     * @param user_id
     */
    private void saveUserAuth(int user_id) {
    	HashSet<String> u = getUserAuthObjects(user_id);
    	HashSet<String> g = new HashSet<String>();
    	for(String s:ChatProps.getInstance(serviceName).getStringProperty("auth.group_"+getUserGroup(user_id)).split(";")){
    		g.add(s);
    	}
    	String grant = "";
    	String revoke = "";
    	for(String s:u)
    		if(!g.contains(s))
    			grant += (grant.length()==0 ? "" : ";") + s;
    	for(String s:g)
    		if(!u.contains(s))
    			revoke += (revoke.length()==0 ? "" : ";") + s;
    	db.setUserProps(user_id, "grant", grant);
    	db.setUserProps(user_id, "revoke", revoke);
    }
    
    /**
     * Удаление прав пользователя
     */
    public boolean revokeUser(int user_id, String authObj){
    	HashSet<String> r = getUserAuthObjects(user_id);
    	if(!r.contains(authObj)) return false;
    	r.remove(authObj);
    	auth.put(user_id, r);
    	saveUserAuth(user_id);
    	return true;
    }
    
    /**
     * Очистка кеша всех дополнительных полномочий при установке новой группы
     */
    public void clearCashAuth(int user_id){
        if(auth.containsKey(user_id)) auth.remove(user_id);
        if(uu.containsKey(user_id)){
            Users u = uc.get(uu.get(user_id));
            u.group = this.getUserGroup(user_id);
            uc.put(u.sn, u);
        }
    }
    
    /**
     * Возвращает комнату
     * @param id
     * @return
     */
    public Rooms getRoom(int id){
        if(rw.checkRoom(id))
            return rw.getRoom(id);
        Rooms r = new Rooms();
        r.setId(id);
        return r;
    }
    
    /**
     * Существует ли такая комната?
     * @param id
     * @return
     */
    public boolean checkRoom(int id){
    	return rw.checkRoom(id);
    }
    
    /**
     * Создание новой комнаты
     * @param r
     * @return
     */
    public boolean createRoom(Rooms r){
    	return rw.createRoom(r);
    }
    
    /**
     * Обновление уже существующей комнаты
     * @param r
     * @param pass
     * @return
     */
    public boolean saveRoom(Rooms r, String pass){
    	return rw.updateRoom(r, pass);
    }
    
    /**
     * Набор ИД существующих комнат
     * @return
     */
    public Set<Integer> getRooms() {
    	return rw.getRooms();
    }
   ///////////////////////////////////////////
   //ДОПОЛНИТЕЛЬНЫЕ КОМАНДЫ
   ///////////////////////////////////////////


     /**
     * Число запусков голосования пользователем за последние 24 часа
     * @param id
     * @return
     */
    public int getCountgolosovanChange(int id) {
        String q = "SELECT count(*) FROM `events` WHERE user_id="+id+" and type='GOLOSOVAN' and (to_days( now( ) ) - to_days( time )) <1";
        Vector <String[]> v = db.getValues(q);
        return Integer.parseInt(v.get(0)[0]);
    }    

     /**
     * Перечень активных времяных модераторов
     * @return
     */
    public Vector<Users> getModList()
    {
    	return db.getObjectVector("select * from users where lastMod>now()");
    }

     /**
     * Количество закрываний за сутки
     */
    public int statBanroomCount()
        {
        long last = System.currentTimeMillis() - 1000*3600*24;
        int r=0;
        try
        {
        PreparedStatement pst = db.getDb().prepareStatement("select count(*) from log t where type = 'Banroom' " +
                    "and time>=?");
        pst.setTimestamp(1,new Timestamp(last));
        ResultSet rs = pst.executeQuery();
        if(!rs.next())
        r = 0;
        else
        r = rs.getInt(1);
        rs.close();
        pst.close();
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        return r;
        }

     /**
     * Количество юзеров в тюрьме всего
     */
    public int statBanroomColCount(){
        int s = ChatProps.getInstance(serviceName).getIntProperty("room.tyrma");
        int r=0;
        try
        {
        PreparedStatement pst = db.getDb().prepareStatement("SELECT count(*) FROM users WHERE room="+s);
        ResultSet rs = pst.executeQuery();
        if(!rs.next())
        r = 0;
        else
        r = rs.getInt(1);
        rs.close();
        pst.close();
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        return r;
        }

     /**
     * Вывод истории закрытий
     */
    public String getZakHist()
        {
        String s = "\nПоследняя 5 закрытых:\n";
        try
        {
        PreparedStatement pst = db.getDb().prepareStatement("select time, user_id, user_id2, msg from events where type='Banroom' order by time desc");
        ResultSet rs = pst.executeQuery();
        for(int i=0;i<5;i=i+1)
        {
        if(rs.next())
        {
        s += i + ". - |"+rs.getTimestamp(1)+"| "+
        rs.getInt(2)+"("+(rs.getInt(2)==0 ? "Admin" : getUser(rs.getInt(2)).localnick)+") " +
        rs.getString(4)+'\n';
        }
        }
        rs.close();
        pst.close();
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        return s;
    }

         /*
         * Бутылочка
         */
        public String GetButilochka(int id)
        {
        String s = "";
        try {
        PreparedStatement pst =  db.getDb().prepareStatement("select * from butilochka where id=" + id);
        ResultSet rs = pst.executeQuery();
        if(rs.next())
        {
        s = rs.getString(2);
        }
        rs.close();
        pst.close();
        } catch (Exception ex) {}
        return s;
        }



        public int getCountpassChange(int id, int id2)
        {
    	String q = "SELECT count(*) FROM `events` WHERE user_id="+id+" and user_id2="+id2+" and type='PASS' and (to_days( now( ) ) - to_days( time )) <1";
    	Vector<String[]> v = db.getValues(q);
    	return Integer.parseInt(v.get(0)[0]);
        }

        public int getCountgolosChange(int id, int id2)
        {
        String q = "SELECT count(*) FROM `events` WHERE user_id="+id+" and user_id2="+id2+" and type='GOLOS' and (to_days( now( ) ) - to_days( time )) <1";
        Vector<String[]> v = db.getValues(q);
        return Integer.parseInt(v.get(0)[0]);
        }

        public String getVseAdmMsg()
        {
        String s="20 последних адм сообщений:\n№|Пользователь|Сообщение|Время\n";
        try{
        PreparedStatement pst = db.getDb().prepareStatement("select id, id_2, msg, time from admmsg");
        ResultSet rs = pst.executeQuery();
        for(int i=1;i<21;i=i+1)
        {
        if(rs.next())
        {
        Users us = getUser(rs.getInt(2));
        s += i + ". - " + (us.localnick.equals("") ? "Еще не зареган" : ("|" + us.id + "|" + us.localnick)) +
        " - " + rs.getString(3) + " |" + rs.getTimestamp(4) + "|\n";
        }
        }
        rs.close();
        pst.close();
        } 
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        return s;
        }



        /*
         * Вывод 5-ки самых рейтинговых
         */

        public String BogachiUsersTop(){
        String s = "";
        try{
        PreparedStatement pst = db.getDb().prepareStatement("SELECT id, localnick, ball FROM users WHERE ball > 0 ORDER BY ball DESC LIMIT 0,6");
        ResultSet rs = pst.executeQuery();
        for(int i=1;i<6;i++){
        if(rs.next()){
        s += i+". - |" + rs.getInt(1) + "|" + rs.getString(2) + ", рейтинг - " + rs.getInt(3) + "; \n";
        }
        else
        {
        break;
        }
        }
        rs.close();
        pst.close();
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        return s;
        }

        /*
         * Вывод 5-ки самых умных
         */

        public String AnswerUsersTop(){
        String s = "\n-------\n5-ка самых умных:\n";
        try{
        PreparedStatement pst = db.getDb().prepareStatement("SELECT id, localnick, answer FROM users WHERE answer > 0 ORDER BY answer DESC LIMIT 0,21");
        ResultSet rs = pst.executeQuery();
        for(int i=1;i<6;i++){
        if(rs.next()){
        s += i+". - |" + rs.getInt(1) + "|" + rs.getString(2) + ", правельных ответов - " + rs.getInt(3) + "; \n";
        } 
        else 
        {
        break;
        }
        }
        rs.close();
        pst.close();
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        s += "-------\n";
        return s;
        }

        /**
        * Вывод истории разбанов
        */
        public String getUbanHist() {
        String s = "20 последних разбанов:\n";
        s+="Время|Пользователь|Кто разбанил\n";
        try {
        PreparedStatement pst = db.getDb().prepareStatement("select time, user_id, user_id2 from events where type='UBAN' order by time desc");
        ResultSet rs = pst.executeQuery();
        for(int i=1;i<21;i++)
        {
        if(rs.next())
        {
        s += i+". - | ["+rs.getTimestamp(1)+"] "+
        (rs.getInt(2)==0 ? "Admin" : getUser(rs.getInt(2)).localnick)+"|" +rs.getInt(2)+"|,"+
        (rs.getInt(3)==0 ? "Admin" : getUser(rs.getInt(3)).localnick)+"|" +rs.getInt(3)+"|\n";
        }
        }
        rs.close();
        pst.close();
        }
        catch (Exception ex)
        {
        ex.printStackTrace();
        }
        return s;
        }

        public boolean deleteRoom(Rooms r)
        {
    	return rw.deleteRoom(r);
        }

// КЛАНЫ //

        /**
          * Возвращает клан
          * @param id
          * @return
          */
       public Clan getClan( int id )
       {
       if( rc.CheckClan( id ) )
       return rc.getClan( id );
       Clan c = new Clan();
       c.setId( id );
       return c;
       }

       /**
         * Существует ли такой клан или неь?
         * @param id
         * @return
         */
       public boolean CheckClan( int id )
       {
       return rc.CheckClan( id );
       }

       /**
         * Создание нового клана
         * @param с
         * @return
         */
       public boolean CreateClan( Clan c )
       {
       return rc.CreateClan( c );
       }

       /**
         * Обновление уже существующего клана
         * @param с
         * @return
         */
       public boolean saveClan( Clan c )
       {
       return rc.UpdateClan( c );
       }

       /**
         * Набор ИД существующих кланов
         * @return
         */
       public Set<Integer> getClans()
       {
       return rc.getClans();
       }

       /**
         * Удаление клана
         * @param с
         * @return
         */
       public boolean DeleteClan( Clan c )
       {
       return rc.DeleteClan( c );
       }

       /**
         * Получаем общее число кланов
         * @return
         */
       public int getCountClan()
       {
       return rc.getCountClan();
       }

       /**
         * Проверяем название клана на уникальность
         * Вернет true если такое название клана существует
         * @return
         */
       public boolean testClanName( String ClanName )
       {
       return rc.TestClanName( ClanName );
       }

       /**
         * Получаем максимальный ид
         * @return
         */
       public int getMaxId()
       {
       return rc.getMaxId();
       }

       /**
        * Получаем список всех кланов
        * @return
        */
       public String ListClan()
       {
       String list = "Список кланов:\nid|Название|Лидер|Комната|Рейтинг|\n";
       try{
       PreparedStatement pst = db.getDb().prepareStatement( "select id, name_clan,  leader_clan, room_clan, ball_clan, info_clan from clans" );
       ResultSet rs = pst.executeQuery();
       while( rs.next() )
       {
       list += "------\n";
       list += "|" + rs.getInt( 1 ) + "|" + rs.getString( 2 )  + "|" + getUser( rs.getInt( 3 ) ).localnick + "|" + rs.getInt( 4 ) + "|" + rs.getInt( 5 ) + "|" + "\n";
       list += "О клане: " + rs.getString( 6 ) + "\n";
       list += "------\n";
       }
       rs.close();
       pst.close();
       }
       catch ( Exception ex )
       {
       ex.printStackTrace();
       }
       return list;
       }

       /**
        * Получаем список кланов по рейтингу клана
        * @return
        */
       public String ClanTop(){
       String s = "Вывод кланов по рейтингу:\n|id|Название|Рейтинг|\n";
       try{
       PreparedStatement pst = db.getDb().prepareStatement( "SELECT id, name_clan, ball_clan FROM clans WHERE ball_clan > 0 ORDER BY ball_clan DESC" );
       ResultSet rs = pst.executeQuery();
       while(rs.next())
       {
       s += "|" + rs.getInt(1) + "|" + rs.getString(2) + "|" + rs.getInt(3) + "| \n";
       }
       rs.close();
       pst.close();
       }
       catch (Exception ex)
       {
       ex.printStackTrace();
       }
       return s;
       }

       /**
        * Список членов клана
        * @param id
        * @return
        */
       
       public String ClanMemberList( int id ){
       String s = "Список членов клана ''" + getClan( id ).getName() + "'' :\n";
       Users uss = getUser( getClan( id ).getLeader() );
       s += "Лидер клана: " + uss.localnick + "\n";
       s += "|id|Nick|ClanGroup|\n";
       try{
       PreparedStatement pst = db.getDb().prepareStatement( "SELECT id FROM users WHERE clansman =" + id + " order by clansman desc" );
       ResultSet rs = pst.executeQuery();
       while( rs.next() )
       {
       Users u = getUser( rs.getInt( 1 ) );
       if ( u.id != getClan( id ).getLeader() )
       {
       s += "|" + u.id + "|" + u.localnick + "|" + u.clangroup + "|" + '\n';
       }
       }
       rs.close();
       pst.close();
       }
       catch (Exception ex)
       {
       ex.printStackTrace();
       }
       return s;
       }


       /**
        * Удаление всех членов клана
        * @param id
        * @return
        */

       public void ClanDelAllMember( int id ){
       try{
       PreparedStatement pst = db.getDb().prepareStatement( "SELECT id FROM users WHERE clansman =" + id );
       ResultSet rs = pst.executeQuery();
       while( rs.next() )
       {
       Users u = getUser( rs.getInt( 1 ) );
       u.clansman = 0;
       u.clangroup = "";
       updateUser( u );
       }
       rs.close();
       pst.close();
       }
       catch (Exception ex)
       {
       ex.printStackTrace();
       }
       }



  ////////
}
