/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import ru.jimbot.db.DBObject;

/**
 * Клан в чате
 *
 * @author fraer72
 */

public class Clan extends DBObject {
private int id = 0; // Ид клана
private int leader_clan = 0; // Ид лидера клана
private int room_clan = 0; // Ид комнаты клана
private String name_clan = ""; // Название клана
private int ball_clan = 0;// Рейтинг клана
private String info_clan = "";// Инфо о клане
private String symbol_clan = "";//Символ клана

public Clan(){}

public Clan(int _id, int _leader_clan, int _room_clan, String _name_clan, int _ball_clan, String _info_clan, String symbol_clan){
id = _id;
leader_clan = _leader_clan;
room_clan = _room_clan;
name_clan = _name_clan;
ball_clan = _ball_clan;
info_clan = _info_clan;
}

public int getId(){
return id;
}

public void setId(int id){
this.id = id;
}

public int getLeader(){
return leader_clan;
}

public void setLeader(int id){
this.leader_clan = id;
}

public int getRoom(){
return room_clan;
}

public void setRoom(int id){
this.room_clan = id;
}

public String getName(){
return name_clan;
}

public void setName(String name){
this.name_clan = name;
}

public int getBall(){
return ball_clan;
}

public void setBall(int id){
this.ball_clan = id;
}

public String getInfo(){
return info_clan;
}

public void setInfo(String name){
this.info_clan = name;
}

public String getSymbol(){
return symbol_clan;
}

public void setSymbol(String symbol_clan){
this.symbol_clan = symbol_clan;
}

@Override
public String[] getFields(){
// TODO Auto-generated method stub
return null;
}

@Override
public String getTableName(){
// TODO Auto-generated method stub
return null;
}

@Override
public int[] getTypes(){
// TODO Auto-generated method stub
return null;
}

}
