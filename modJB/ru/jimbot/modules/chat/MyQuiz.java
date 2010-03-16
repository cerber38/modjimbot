/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Random;
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
private Thread th;
private int count = 0;// Количество викторин :D т.е. максимальное количество комнат где задействована викторина
private static Random R;
private ConcurrentHashMap <Integer, QuizInfo> QuizInfo;// для хранения данных по комнатам
static {
R = new Random(System.nanoTime());
}
private boolean QuizStart = true;


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
Hint(GetAnswer(quiz.AG), id);
String s = GetQuestion(quiz.AG);
// Проверяем сколько звезд осталось
if(hasStars(id))
{
replaceOne(id);// Откроем еще одну
String help = String.valueOf(getMix(id));
String g_1 = "Вопрос № - (" + id + "): " + s + "\n" +
"Подсказка: ''" + help + "''";
srv.cq.addMsg(g_1, "", quiz.room);
quiz.time = System.currentTimeMillis();
QuizInfo.put(id, quiz);
}else{
    // если ответ не дан
String g_2 = "На вопрос № - (" + id + "): " + s + "\n" +
        "Ответ не дан.";
srv.cq.addMsg(g_2, "", quiz.room);
quiz.AG = Random_ID();
quiz.time = System.currentTimeMillis();
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
srv.us.getUser(uin).ball += 1;//дадим бал к репе :)
srv.us.getUser(uin).answer += 1;//дадим 1 правельный ответ :)
srv.us.updateUser(srv.us.getUser(uin));
srv.cq.addMsg("Пользователь |" + srv.us.getUser(uin).id  +
"|" + srv.us.getUser(uin).localnick +
" ответил верно ''" + GetAnswer(id) + "''" +
"\nОтветил верно на "+ srv.us.getUser(uin).answer + " вопрос(ов)" +
"", "", room);
quiz.AG = Random_ID();
quiz.time = System.currentTimeMillis();
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
private void Quiz()
{
    if(TestCountChat()){         // TODO: будет ли грузить данная проверка в потоке? и
                                 // понять, будет ли работать викторина после нее? т.е. если return, а
                                 // текущее время не сохраненно
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
    QuizStart();
    }
}
// тест времени 
for(int id = 0;id < count; id++){
    QuizInfo quiz = QuizInfo.get(id);
    if((System.currentTimeMillis() - quiz.time)>90000){
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
   if((cnt-1) == 0){
       return false;
   }
    return true;
}

/**
 * Парсер викторины
 * @param uin - уин пользователя
 * @param mmsg - сообщение
 * @param room - комната
 */
public void parse(String uin, String mmsg, int room) {
if(TestOtvet(room, mmsg) && TestRoom(room)) Otvet(uin,room);
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
    String s = psp.getStringProperty("vic.room");
        if(s.equals("")) return false;
        String[] rooms = s.split(";");
        try{
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

}
