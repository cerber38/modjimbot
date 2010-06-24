/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot;

/**
 * @author fraer72
 * Хранение данных http пользователей
 */

public class httpUsers {
    public String ip = "";
    public String name = "";
    public String pass = "";
    public String services = "";
    public long beginning = 0;
    public String time = "";
    public String uid = "";

    httpUsers(String ip, String name, String pass, String services, String time)
    {
    this.ip = ip;
    this.name = name;
    this.pass = pass;
    this.services = services;
    this.time = time;
    }

    public void setUid(String uid){
    this.uid = uid;
    }

    public String getUid(){
    return uid;
    }

    public void setBeginning(long beginning){
    this.beginning = beginning;
    }

    public long getBeginning(){
    return beginning;
    }

}
