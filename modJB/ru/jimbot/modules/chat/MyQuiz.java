/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.util.Log;

/**
 * Викторина :) и все что с ней связанно!
 * @author Fraer72
 */
public class MyQuiz implements Runnable {
private ChatServer srv;
private ChatProps psp;
private int sleepAmount = 1000;
public Thread th;
private int count = 0;// Количество викторин :D т.е. максимальное количество комнат где задействована викторина
private static Random R;
private ConcurrentHashMap <Integer, QuizInfo> QuizInfo;// для хранения данных по комнатам
static {
R = new Random(System.nanoTime());
}
private boolean QuizStart = true;
private boolean autofilling = true;
public boolean isStart = false;

private long throwOut = 5000;

public MyQuiz(ChatServer s)
{
srv = s;
psp = ChatProps.getInstance(srv.getName());
QuizInfo = new ConcurrentHashMap<Integer, QuizInfo>();
}

    private class QuizInfo {
        public int id = 0;/*Ид викторины*/
        public int room = 0;/*Комната*/
        public int AG = 0;/*Ид для получения вопроса/ответа*/
        public long time = 0;/*Хранит время викторины в конкретной комнате*/
        public String word;/*Ответ*/
        public char[] mix;/*Масив звездочек и букв*/
        public int starsCount;/*Количество оставшихся звездочек*/
        public boolean start = true;/*Новый вопрос?*/

        QuizInfo(int id, int room, int AG, long time) {
            this.id = id;
            this.room = room;
            this.AG = AG;
            this.time = time;
        }
    }

/**
 * Закрываем звездачками
 * @param word
 * @param id - id викторины
 */

public void Hint(String word, int id)
{
QuizInfo quiz = QuizInfo.get(id);
quiz.word = word;
quiz.starsCount = word.length();
quiz.mix = new char[quiz.starsCount];
Arrays.fill(quiz.mix, '*');
}

/**
 * Задаем вопрос, в отдельном случаи генерируем новый
 * @param id - id викторины
 * @param room - комната
 */

public void Vopros( int id, int room )
{
QuizInfo quiz = QuizInfo.get(id);
if(quiz.start)Hint(GetAnswer(quiz.AG), id);
String s = GetQuestion(quiz.AG);
// Проверяем сколько звезд осталось
if(hasStars(id))
{
replaceOne(id);// Откроем еще одну
String help = String.valueOf(getMix(id));
String g_1 = "Вопрос № - (" + quiz.AG + "): " + s + "\n" +
"Подсказка: ''" + help + "''";
srv.cq.addMsg(g_1, "", quiz.room);
quiz.time = System.currentTimeMillis();
quiz.start = false;
QuizInfo.put(id, quiz);
}else{
    // если ответ не дан
String g_2 = "На вопрос № - (" + quiz.AG + "): " + s + "\n" +
        "Ответ не дан.";
srv.cq.addMsg(g_2, "", quiz.room);
quiz.AG = Random_ID();
quiz.time = System.currentTimeMillis();
quiz.start = true;
QuizInfo.put(id, quiz);
}
}

/**
 * Проверка ответа
 * @param msg - ответ пользователя
 * @param room - комната
 * @return
 */

public boolean TestOtvet(int room, String msg)
{
int id = GetQuizId(room);// id викторины
QuizInfo quiz = QuizInfo.get(id);
String[] mmsg = msg.split(" ");
String answer = mmsg[0].toLowerCase();
return answer.trim().equals(GetAnswer(quiz.AG).toLowerCase());
}

/**
 * Ответ дан, генерируем следующий вопрос
 * @param uin - уин пользователя
 * @param room - комната
 */

public void Otvet(String uin, int room)
{
int id = GetQuizId(room);// id викторины
QuizInfo quiz = QuizInfo.get(id);
srv.us.getUser(uin).ball += psp.getIntProperty("vic.ball");//дадим бал к репе :)
srv.us.getUser(uin).answer += 1;//дадим 1 правельный ответ :)
srv.us.updateUser(srv.us.getUser(uin));
srv.cq.addMsg("Пользователь |" + srv.us.getUser(uin).id  +
"|" + srv.us.getUser(uin).localnick +
" ответил верно ''" + GetAnswer(quiz.AG) + "''" +
"\nОтветил верно на "+ srv.us.getUser(uin).answer + " вопрос(ов)" +
"", "", room);
quiz.AG = Random_ID();
quiz.time = System.currentTimeMillis();
quiz.start = true;
QuizInfo.put(id, quiz);
Vopros(id, room);
}

/**
 * Сохраняет комнаты
 */
private void QuizStart(){
String[] rooms = psp.getStringProperty("vic.room").split(";");
for(int i = 0;i < rooms.length; i++){
QuizInfo quiz = new QuizInfo(i, Integer.parseInt(rooms[i]), Random_ID(), System.currentTimeMillis());
count++;
QuizInfo.put(i, quiz);
}
}

/**
 * Главный метод, парсит время и все остальное!
 */
private void Quiz(){
    if(!isStart) setStart(true);
    // Тест времени
   if(psp.getBooleanProperty("vic.on.off") & psp.getBooleanProperty("vic.time_game.on.off") & !testHours()){
   if(psp.getBooleanProperty("vic.throwout.on.off")){
   ThrowOut();
   }
   return;
   }
   if(autofilling){
    if(getCountQuestion() == 0){
    Log.getLogger(srv.getName()).talk("Нет вопросов в БД для викторины! Викторина будет полностью остановлена!");
    autofilling = false;
    stop();
    return;// если нет вопросов в БД
    }
    }
    if(!TestCountChat()){
    return;// если в чате не кого нету
    }
if(QuizStart){
    String s = psp.getStringProperty("vic.room");
        if(s.equals("")){
           Log.getLogger(srv.getName()).error("Не указанно не одной комнаты для викторины!");
            return ;// если не указанно не одной комнаты
        } else {
        // если все нормально
    QuizStart = false;
    autofilling = false;
    QuizStart();
    }
}
// тест времени 
for(int id = 0;id < count; id++){
    QuizInfo quiz = QuizInfo.get(id);
    if((System.currentTimeMillis() - quiz.time)>psp.getIntProperty("vic.time")){
        quiz.time = System.currentTimeMillis();// пишем новое время
     Vopros(quiz.id, quiz.room); // вопрос
    }
}

}

/**
 * Получаем по комнате ид викторины
 * @param room - комната
 * @return
 */

private int GetQuizId( int room ){
    int id_quyz = 0;
for(int id = 0;id < count; id++){
    QuizInfo quiz = QuizInfo.get(id);
    if(quiz.room == room){
        id_quyz = quiz.id;
    }
}
  return id_quyz;
}


/**
 * Проверяет сколько чел в чате, что бы не забивать очередь
 * @return false - если в чате не кого нет
 */

private boolean TestCountChat(){
    int cnt=0;
    Enumeration<String> e = srv.cq.uq.keys();
    while(e.hasMoreElements())
    {
    String i = e.nextElement();
    Users us = srv.us.getUser(i);
    if(us.state==UserWork.STATE_CHAT)
    {
    cnt++;
    }
    }
   if((cnt) == 0){
       return false;
   }
    return true;
}
    /**
     * Проверяет сколько человек в определенной комнате
     * @param room
     * @return
     */


    public int AllUsersRoom(int room)
    {
    int c = 0;
    Enumeration<String> e2 = srv.cq.uq.keys();
    while(e2.hasMoreElements())
    {
    String i2 = e2.nextElement();
    Users us = srv.us.getUser(i2);
    if(us.state==UserWork.STATE_CHAT)
    {
    if(us.room == room)
    {
    c++;
    }
    }
    }
    return (c);
    }

/**
 * Парсер викторины
 * @param uin - уин пользователя
 * @param mmsg - сообщение
 * @param room - комната
 */
public void parse(String uin, String mmsg, int room) {
    if(getCountQuestion() == 0){
    return;// если нет вопросов
    }
   // Тест времени
   if(psp.getBooleanProperty("vic.on.off") & psp.getBooleanProperty("vic.time_game.on.off") & !testHours()){
   return;// если игра не идет
   }
if(TestOtvet(room, mmsg) && TestRoom(room)) Otvet(uin,room);
}

/**
 * Метод переведет пользователь в другие комнаты
 */

public void ThrowOut(){
Set<Integer> users = new HashSet();
String[] rooms = psp.getStringProperty("vic.room").split(";");
for(int i = 0;i < rooms.length; i++){
    if(AllUsersRoom(Integer.parseInt(rooms[i])) != 0){
    Enumeration<String> e = srv.cq.uq.keys();
    while(e.hasMoreElements())
    {
    String a = e.nextElement();
    Users us = srv.us.getUser(a);
    if(us.state==UserWork.STATE_CHAT & us.room == Integer.parseInt(rooms[i]) & !psp.testAdmin(us.sn))
    {
    users.add(us.id);
    }
    }
    }
}
    // Переводим
    for (int id : users)
    {
    Users u = srv.us.getUser(id);
    u.room = psp.getIntProperty("vic.throwout.room");
    srv.us.updateUser(u);
    srv.cq.changeUserRoom(u.sn,psp.getIntProperty("vic.throwout.room"));
    }
}
    /**
     * Проверка времени игры
     * @return
     */

    public boolean testHours(){
    Date date = new Date(System.currentTimeMillis());
    String [] test = psp.getStringProperty("vic.game.time").split(";");
    boolean a = true;
    int current_hour = date.getHours();// Текущий час
    int hour_beginning = Integer.parseInt(test[0]);// Час начала игры
    int hour_final = Integer.parseInt(test[1]);// Час конца игры

    if(hour_beginning > hour_final){

    if(current_hour > hour_beginning){
    a = false;
    }

    if(current_hour < hour_beginning & current_hour >= hour_final ){
    a = false;
    }

    if(current_hour > hour_final & current_hour < hour_beginning){
    a = false;
    }

    }else{

    if(current_hour < hour_beginning){
    a = false;
    }

    if(current_hour >= hour_final){
    a = false;
    }

    }

    if(a)
        return true;
    else
        return false;
    }

public void start() 
{
th = new Thread(this);
th.setPriority(Thread.NORM_PRIORITY);
th.start();
}

public synchronized void stop()
{
th = null;
notify();
}

public void run()
{
Thread me = Thread.currentThread();
while (th == me)
{
Quiz();
try {
Thread.sleep(sleepAmount);
}
catch (InterruptedException e)
{
break;
}
}
th=null;
}

/**
 * Получаем ответ
 * @param id
 * @return
 */
public String GetAnswer(int id)
{
String s = "";
try {
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from victorina where id=" + id);
ResultSet rs = pst.executeQuery();
if(rs.next())
{
s = rs.getString(3);
}
rs.close();
pst.close();
} catch (Exception ex) {}
return s.trim();
}

/**
 * Получаем вопрос
 * @param id
 * @return
 */
public String GetQuestion(int id)
{
String s = "";
try {
PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select * from victorina where id=" + id);
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

/**
 * Получаем рандомный ид
 * @return
 */

private int Random_ID()
{
    int ids = 0;
    try {
    PreparedStatement pst =  (PreparedStatement) srv.us.db.getDb().prepareStatement("SELECT id FROM victorina ORDER BY RAND( ) LIMIT 0 , 1");
    ResultSet rs = pst.executeQuery();
    if(rs.next())
    {
    ids = rs.getInt(1);
    }
    rs.close();
    pst.close();
    } catch (Exception ex) 
    {
        //errore
    }
    return ids;
}

/**
 * В комнате проходит викторина?
 * @param room - комната
 * @return
 */

    public boolean TestRoom(int room) {
        try{
    String s = psp.getStringProperty("vic.room");
        if(s.equals("")) return false;
        String[] rooms = s.split(";");
            for(int i=0;i<rooms.length;i++){
                if(Integer.parseInt(rooms[i]) == room) return true;
            }
        } catch(Exception ex){
            ex.printStackTrace();
        }
        return false;
    }

/**
 * Открываем одну звездочку
 * @param id - id викторины
 */
public void replaceOne(int id)
{
QuizInfo quiz = QuizInfo.get(id);
if (quiz.starsCount == 0)
{
return;
}
int nextStarIndex = R.nextInt(quiz.starsCount);
for (int i = 0; i < quiz.mix.length; i++)
{
if (quiz.mix[i] == '*')
{
if (nextStarIndex-- == 0)
{
quiz.mix[i] = quiz.word.charAt(i);
break;
}
}
}
quiz.starsCount--;
}

/**
 * Вернет смесь звездочек и букв
 * @param id - id викторины
 * @return
 */

public char[] getMix(int id)
{
QuizInfo quiz = QuizInfo.get(id);
return quiz.mix;
}

/**
 * Проверяем сколько звездочек осталось
 * @param id - id викторины
 * @return
 */

public boolean hasStars(int id)
{
QuizInfo quiz = QuizInfo.get(id);
return quiz.starsCount != 1 && quiz.starsCount != 0;
}

/**
 * Получаем максимальный ид
 * @return
 */

public int getCountQuestion()
{
String q = "SELECT count(*) FROM `victorina` WHERE id";
Vector<String[]> v = srv.us.db.getValues(q);
return Integer.parseInt(v.get(0)[0]);
}

/**
 * Метод нужен что бы узнать ответ в определенной викторине из других классов или скриптов
 * @param room
 * @return
 */

public String Answer(int room){
int id = GetQuizId(room);// id викторины
QuizInfo quiz = QuizInfo.get(id);
return GetAnswer(quiz.AG);
}

/**
 * Добавить викторину на ходу
 * @param room
 */

public void AddQuiz(int room){
String rooms = psp.getStringProperty("vic.room");
if(rooms.equals("")){
    psp.setStringProperty("vic.room", Integer.toString(room));
    psp.save();
}else{
    psp.setStringProperty("vic.room", rooms +";" + Integer.toString(room));
    psp.save();
}
QuizInfo quiz = new QuizInfo(count, room, Random_ID(), System.currentTimeMillis());
QuizInfo.put(count, quiz);
count++;
}

   public void setStart(boolean start){
   isStart = start;
   }

   public boolean isStart(){
   return isStart;
   }


}

