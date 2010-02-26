/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules;

import java.util.Vector;

/**
 * @author fraer72
 */
public class NewExtend {
	private String uin;
	private long vremia;
	private String msg;
	private String cmd;
	private Vector data;

	/**
	 * Создание нового элемента
	 * @param _uin - Уин пользователя
	 * @param _cmd - команда
	 * @param _msg - сообщение
	 * @param _data - промежуточные данные для работы команды
	 * @param expire - длительность ожидания ответа (мс)
	 */
	public NewExtend(String _uin, String _cmd, String _msg, Vector _data, long expire) {
		vremia = System.currentTimeMillis() + expire;
		uin = _uin;
		cmd = _cmd;
		msg = _msg;
		data = _data;
	}

	public String getMsg(){
		return msg;
	}

	public String getUin() {
		return uin;
	}

	public String getCmd() {
		return cmd;
	}

	public Vector getData() {
		return data;
	}

	public boolean isExpire() {
		return System.currentTimeMillis()>vremia;
	}
}