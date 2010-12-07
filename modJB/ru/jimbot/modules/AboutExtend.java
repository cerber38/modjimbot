/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules;

/**
 * Храним данны при заполнении интерактивной анкете
 * @author fraer72
 */

public class AboutExtend {
private String uin;// uin пользователя
private long vremia;// время на заполнение
private int order = 0;// номер вопроса
private boolean answer = false;// вопрос задан? ждем ответа?
private boolean commandReg = false;// после регистрации?

	public AboutExtend(String uin, long expire, boolean commandReg) {
	vremia = System.currentTimeMillis() + expire;
	this.uin = uin;
        this.commandReg = commandReg;
	}

        public boolean isExpire() {
        return System.currentTimeMillis()>vremia;
        }

        public String getUin(){
        return uin;
        }

        public int getOrder(){
        return order;
        }

        public void setOrder(){
        this.order++;
        }

        public void setOrder(int order){
        if(order < 0)
            order--;
                    else
                        order++;
        }
        public boolean getAnswer(){
        return answer;
        }

        public void setAnswer(boolean answer){
        this.answer = answer;
        }

        public boolean getCommand(){
        return commandReg;
        }

}
