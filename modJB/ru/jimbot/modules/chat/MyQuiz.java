/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import com.mysql.jdbc.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Random;

/**
 * @author Fraer72
 */
public class MyQuiz implements Runnable {
private ChatServer srv;
public ChatProps psp;
private int sleepAmount = 1000;
private Random r = new Random();
private long cTime = System.currentTimeMillis();
private Thread th;
long VicTime = 0;
int n = 0;
private  String word;
private  char[] mix;
private int starsCount;
private static Random R;
static {R = new Random(System.nanoTime());}


public MyQuiz(ChatServer s)
{
srv = s;
psp = ChatProps.getInstance(srv.getName());
}

public void Hint(String word)
{
this.word = word;
starsCount = word.length();
mix = new char[starsCount];
Arrays.fill(mix, '*');
}

private boolean testTime()
{
return (System.currentTimeMillis()-cTime)>90000;
}

private int getRND(int i)
{
return r.nextInt(i);
}

public String HintVic()
{
String s = "";
if(hasStars())
{
replaceOne();
s = String.valueOf(getMix());
}
else
{
s = "The answer is not given";
n = Num();
VicTime = 0;
}
return s;
}

public int Num()
{
long i = srv.us.db.getLastIndex("victorina");
String k = ""+i;
int L = Integer.valueOf(k);
int G =  getRND(L);
return G;
}

public void Vopros()
{
if(GetAnswer(n).length()>8)
{
while(GetAnswer(n).length()>8)
{
n = Num();
}
}
if(VicTime==0)Hint(GetAnswer(n));
VicTime = System.currentTimeMillis();
String s = GetQuestion(n);
String g = "Вопрос № - (" + n + "): " + s + "\n" +
"Подсказка: ''" + HintVic() + "''";
srv.cq.addMsg(g, "", psp.getIntProperty("vic.room"));
}

public boolean TestOtvet(String s)
{
String[] i = s.split(" ");//разделитель
String aa = i[0];//до разделителя
String Otvet = aa.toLowerCase();//опустим регистр
if(Otvet.equals(GetAnswer(n)))//проверим
{
return true;//если ответ пользователя верный
}
else
{
return false;//если ответ пользователя неверный
}
}

public void Otvet(String uin)
{
srv.us.getUser(uin).ball += 1;//дадим бал к репе :)
srv.us.getUser(uin).answer += 1;//дадим 1 правельный ответ :)
srv.us.updateUser(srv.us.getUser(uin));
srv.cq.addMsg("Пользователь |" + srv.us.getUser(uin).id  +
"|" + srv.us.getUser(uin).localnick +
" ответил верно ''" + GetAnswer(n) + "''" +
"\nОтветил верно на "+ srv.us.getUser(uin).answer + " вопрос(ов)" +
"", "", psp.getIntProperty("vic.room"));
VicTime = 0;
n = Num();
Vopros();
}

private void timeEvent()
{
if(testTime()) {
cTime = System.currentTimeMillis();
Vopros();
}
}

public void parse(String uin, String mmsg, int room) {
if(TestOtvet(mmsg) && srv.us.getUser(uin).room==psp.getIntProperty("vic.room")){Otvet(uin);}
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
timeEvent();
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

/*Ответ*/
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
s = s.replace(" ","");//если в БД перед ответом пробел
} catch (Exception ex) {}
return s;
}

/*Вопроc*/
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
        
public void replaceOne() 
{
if (starsCount == 0) 
{
return;
}
int nextStarIndex = R.nextInt(starsCount);
for (int i = 0; i < mix.length; i++) 
{
if (mix[i] == '*') 
{
if (nextStarIndex-- == 0)
{
mix[i] = word.charAt(i);
break;
}
}
}
starsCount--;
}

public char[] getMix()
{
return mix;
}

public boolean hasStars()
{
return starsCount != 1 && starsCount != 0;
}

@Override
public String toString()
{
return word;
}

public int Star()
{
return starsCount;
}

}
