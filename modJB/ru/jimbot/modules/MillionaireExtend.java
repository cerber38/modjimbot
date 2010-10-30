/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules;

/**
 * Для храненния данных игры
 * @author fraer72
 */
public class MillionaireExtend {
private String uin; // uin пользователя
private Integer level; // уровень игры
private long time;// время
private Integer question;// id вопроса
private Integer answer;// номер ответа
private Integer ball = 0;// Выигрыш

    public MillionaireExtend(String uin,
                             Integer level,
                             Integer question,
                             Integer answer,
                             long time) {
    this.uin = uin;
    this.level = level;
    this.question = question;
    this.answer = answer;
    this.time = System.currentTimeMillis() + time;
    }

	public String getUin() {
		return uin;
	}

	public Integer getLevel() {
		return level;
	}

	public Integer setLevel() {
		return level++;
	}

	public Integer getQuestion() {
		return question;
	}

	public Integer getAnswer() {
		return answer;
	}

	public void setQuestion(Integer question) {
		this.question = question;
	}

	public void setAnswer(Integer answer) {
	        this.answer = answer;
	}

	public Integer getBall() {
		return ball;
	}

	public void setBall(Integer ball) {
	        this.ball = ball;
	}

	public void updateTime(long time) {
	       this.time = System.currentTimeMillis() + time;
	}

	public boolean isTime() {
		return System.currentTimeMillis()>time;
	}

}