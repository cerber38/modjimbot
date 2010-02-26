package ru.jimbot.modules.chat;

import java.util.concurrent.ConcurrentHashMap;
import bsh.Interpreter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import ru.jimbot.modules.Cmd;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.Messages;
import ru.jimbot.db.DBObject;

public class ScriptWork {
	/**
	 * SQL - запрос:
	 * CREATE TABLE `scripts` (
	 *   `id` int(11) NOT NULL,
	 *   `script` text NOT NULL,
	 *   `describe` varchar(256) NOT NULL,
	 *   `enable` int(11) NOT NULL,
	 *   UNIQUE KEY `id` (`id`)
	 * ) ENGINE=MyISAM DEFAULT CHARSET=utf8;
	 */
	private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
	private CommandParser parser;
	private ChatCommandProc cmd;
	private String error = "";
	public String ename = "";
	public Integer last = 0;
	private ConcurrentHashMap <Integer,Scripts> scr = new ConcurrentHashMap <Integer,Scripts>();
	public ScriptWork(ChatCommandProc c) {
		parser = new CommandParser(commands);
		cmd = c;
		//installAll(c);
		init();
	}
	private void init(){
		commands.put("!screate", new Cmd("!screate","$s",1));
		commands.put("!senable", new Cmd("!senable","$n",2));
		commands.put("!sedit", new Cmd("!sedit","$n $s",3));
		commands.put("!sdelete", new Cmd("!sdelete","$n",4));
		commands.put("!slist", new Cmd("!slist","",5));
		commands.put("!sdescribe", new Cmd("!sdescribe","$n $s",6));
		commands.put("!seadd", new Cmd("!seadd","$n $s",7));
		commands.put("!slisting", new Cmd("!slisting","$n",8));
		commands.put("!shelp", new Cmd("!shelp","$n",9));
		commands.put("!simport", new Cmd("!simport","$s $s",10));
		commands.put("!sexport", new Cmd("!sexport","$n $s",11));
	}
	public boolean commandExec(IcqProtocol proc, String uin, String mmsg) {
		String msg = mmsg.trim();
		int tp = parser.parseCommand(msg);
		int tst=0;
		if(tp<0)
			tst=0;
		else
			tst = tp;
		boolean f = true;
		switch (tst){
			case 1:
				commandScriptCreate(proc, uin, parser.parseArgs(msg));
				break;
			case 2:
				commandScriptEnable(proc, uin, parser.parseArgs(msg));
				break;
			case 3:
				commandScriptEdit(proc, uin, parser.parseArgs(msg));
				break;
			case 4:
				commandScriptDelete(proc, uin, parser.parseArgs(msg));
				break;
			case 5:
				commandScriptList(proc, uin);
				break;
			case 6:
				commandScriptDescribe(proc, uin, parser.parseArgs(msg));
				break;
			case 7:
				commandScriptEditAdd(proc, uin, parser.parseArgs(msg));
				break;
			case 8:
				commandScriptListing(proc, uin, parser.parseArgs(msg));
				break;
			case 9:
				commandScriptHelp(proc, uin);
				break;
			case 10:
				commandScriptImport(proc, uin, parser.parseArgs(msg));
				break;
			case 11:
				commandScriptExport(proc, uin, parser.parseArgs(msg));
				break;
			default:
				f = false;
		}
		return f;
	}
	public void commandScriptCreate(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			String s = (String)v.get(0);
			Scripts script = new Scripts();
			script.setEnable(1);
			script.setScript(s);
			if (create(script)) proc.mq.add(uin, "Скрипт успешно создан.");
			else {
				proc.mq.add(uin, "При создании скрипта возникла ошибка: " + getError());
				return;
			}
			proc.mq.add(uin, "Устанавливаю скрипт: " + last + "\nЗавершено: " +  installScript(last, cmd));
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptEnable(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			Integer i = (Integer)v.get(0);
			if (!check(i)) {
				proc.mq.add(uin, "Скрипт не найден.");
				return;
			}
			if (getScript(i).getEnable() == 0) {
				getScript(i).setEnable(1);
				if (update(getScript(i))) proc.mq.add(uin, "Скрипт успешно активирован.");
				else {
					proc.mq.add(uin, "При активации скрипта возникла ошибка: " + getError());
					return;
				}
				proc.mq.add(uin, "Устанавливаю скрипт: " + getScript(i).ScriptID() + "\nЗавершено: " +  installScript(getScript(i).ScriptID(), cmd));
			} else {
				getScript(i).setEnable(0);
				if (update(getScript(i))) proc.mq.add(uin, "Скрипт успешно деактивирован.");
				else {
					proc.mq.add(uin, "При деактивации скрипта возникла ошибка: " + getError());
					return;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptEdit(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			Integer i = (Integer)v.get(0);
			String s = (String)v.get(1);
			if (!check(i)) {
				proc.mq.add(uin, "Скрипт не найден.");
				return;
			}
			getScript(i).setScript(s);
			if (update(getScript(i))) proc.mq.add(uin, "Скрипт успешно обновлен.");
			else {
				proc.mq.add(uin, "При обновлении скрипта возникла ошибка: " + getError());
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptListing(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			Integer i = (Integer)v.get(0);
			if (!check(i)) {
				proc.mq.add(uin, "Скрипт не найден.");
				return;
			}
			proc.mq.add(uin, getScript(i).getScript());
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptEditAdd(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			Integer i = (Integer)v.get(0);
			String s = (String)v.get(1);
			if (!check(i)) {
				proc.mq.add(uin, "Скрипт не найден.");
				return;
			}
			getScript(i).setScript(getScript(i).getScript() + s);
			if (update(getScript(i))) proc.mq.add(uin, "Скрипт успешно обновлен.");
			else {
				proc.mq.add(uin, "При обновлении скрипта возникла ошибка: " + getError());
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptDescribe(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			Integer i = (Integer)v.get(0);
			String s = (String)v.get(1);
			if (!check(i)) {
				proc.mq.add(uin, "Скрипт не найден.");
				return;
			}
			getScript(i).setDescribe(s);
			if (update(getScript(i))) proc.mq.add(uin, "Описание скрипта успешно обновлено.");
			else {
				proc.mq.add(uin, "При обновлении описания скрипта возникла ошибка: " + getError());
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptHelp(IcqProtocol proc, String uin) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			String s = "ScriptWork Engine 3 (DataBase Script Work Plugin)"
					+ "!screate <text> - создание скриптов.\n"
					+ "!senable <id> - включение/выключение скриптов.\n"
					+ "!sedit <id> <text> - редактирование скриптов.\n"
					+ "!sdelete <id> - удаление скриптов.\n"
					+ "!slist - вывод списка скриптов.\n"
					+ "!sdescribe <id> <text> - изменение описания скрипта\n"
					+ "!seadd <id> <text> - добавления текста к скрипту.\n"
					+ "!slisting <id> - вывод листинга скрипта.\n"
					+ "!shelp - справка по командам управления скриптами в базе данных";
			proc.mq.add(uin, s);
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptList(IcqProtocol proc, String uin) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			String s = "";
			int c = 0;
			for(Integer i:getScripts()) {
				s += " » " + i + " " + getScript(i).getDescribe();
				if (getScript(i).getEnable() == 0) s += " (Не активен)\n";
				else s += " (Активен)\n";
				c ++;
			}
			s += "Всего скриптов: " + c;
			proc.mq.add(uin, s);
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptDelete(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			Integer i = (Integer)v.get(0);
			if (!check(i)) {
				proc.mq.add(uin, "Скрипт не найден.");
				return;
			}
			if (delete(i)) proc.mq.add(uin, "Скрипт успешно удален.");
			else {
				proc.mq.add(uin, "При удалении скрипта возникла ошибка: " + getError());
				return;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptImport(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			String name = (String)v.get(0);
			String path = (String)v.get(1);
			if (path.equals("")) path = "__SCRPATH__";
			if (ScriptImport(path, name)) proc.mq.add(uin, "Скрипт успешно импортирован. ID = " + last);
			else  proc.mq.add(uin, "При импортировании скрипта вожникла ошибка: " + getError());
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public void commandScriptExport(IcqProtocol proc, String uin, Vector v) {
		if(!cmd.auth(proc,uin, "scripts")) return;
		try {
			Integer i = (Integer)v.get(0);
			String path = (String)v.get(1);
			if (path.equals("")) path = "__SCRPATH__";
			if (ScriptExport(path, i)) proc.mq.add(uin, "Скрипт успешно экспортирован. Файл: " + ename);
			else  proc.mq.add(uin, "При экспортировании скрипта вожникла ошибка: " + getError());
		} catch (Exception ex) {
			ex.printStackTrace();
			//proc.mq.add(uin, Messages.getString("ChatCommandProc.err", new Object[] {ex.getMessage()}));
		}
	}
	public String getError() {
		String err = error;
		error = "";
		return err;
	}
	public void installAll(IcqProtocol prot) {
		installAll(cmd, prot);
	}
	public void installAll(ChatCommandProc proc) {
		installAll(proc, null);
	}
	public void installAll(ChatCommandProc proc, IcqProtocol prot) {
		Log.info(" > ScriptWork(): Install DataBase Scripts.");
		String sss = "";
		String inst = "";
		try {
			Vector<String[]> var = cmd.srv.us.db.getValues("SELECT `id`, `script`, `describe`, `enable` FROM scripts");
			for(int i=0;i<var.size();i++) {
				String[] s = var.get(i);
				Scripts script = new Scripts();
				script.setID(Integer.parseInt(s[0]));
				script.setScript(s[1]);
				script.setDescribe(s[2]);
				script.setEnable(Integer.parseInt(s[3]));
				scr.put(script.ScriptID(), script);
				inst = installScript(script.ScriptID(),proc);
				if (prot != null) sss += "Устанавливаю скрипт: " + script.ScriptID() + "\nЗавершено: " + inst + "\n";
				Log.info("   Install script: " + script.ScriptID());
				Log.info("     Completed: " + inst);
			}
			if (prot != null) {
				String[] ss = cmd.srv.getProps().getAdmins();
				for(int i=0;i<ss.length;i++){
					prot.mq.add(ss[i], sss);
				}
			}
		} catch (Exception ex){
			error = ex.getMessage();
			ex.printStackTrace();
		}
	}
	public String installScript(Integer name, ChatCommandProc proc){
		String t = "";
		try{
			if (scr.get(name).getEnable() == 0) return "Disable";
			Interpreter bsh = new Interpreter();
			bsh.set("in", "install");
			bsh.set("name", Integer.toString(name));
			bsh.set("cmd", proc);
			bsh.eval(scr.get(name).getScript());
			t = bsh.get("out").toString();
		} catch (Exception ex) {
			error = ex.getMessage();
			return ex.getMessage();
		}
		return t;
	}
	public String startScript(String scriptName, String msg, String uin, IcqProtocol prot, ChatCommandProc proc){
		try{
			if (scr.get(Integer.parseInt(scriptName)).getEnable() == 0) return "Disable";
			Interpreter bsh = new Interpreter();
			bsh.set("in", "run");
			bsh.set("cmd", proc);
			bsh.set("uin", uin);
			bsh.set("msg", msg);
			bsh.set("proc", prot);
			bsh.eval(scr.get(Integer.parseInt(scriptName)).getScript());
		} catch (Exception ex) {
			error = ex.getMessage();
			return ex.getMessage();
		}
		return "";
	}
	public boolean check(int id) {
		return scr.containsKey(id);
	}
	public Set<Integer> getScripts() {
		return scr.keySet();
	}
	public boolean create(Scripts s){
		last = (int) cmd.srv.us.db.getLastIndex("scripts");
		Log.info(" > ScriptWork(): Create Script ID = " + last);
		boolean f = false;
		try {
			PreparedStatement pst = cmd.srv.us.db.getDb().prepareStatement("insert into scripts values(?,?,?,?)");
			pst.setInt(1,last);
			s.setID(last);
			pst.setString(2, s.getScript());
			pst.setString(3, s.getDescribe());
			pst.setInt(4, s.getEnable());
			pst.execute();
			pst.close();
			scr.put(last, s);
			f=true;
		} catch (Exception ex){
			error = ex.getMessage();
		}
		return f;
	}
	public boolean update(Scripts s) {
		Log.info(" > ScriptWork(): Update Script ID = " + s.ScriptID());
		boolean f = false;
		try {
			PreparedStatement pst = cmd.srv.us.db.getDb().prepareStatement("update scripts set `script`=?, `describe`=?, `enable`=? where `id`=?");
			pst.setInt(4, s.ScriptID());
			pst.setString(1, s.getScript());
			pst.setString(2, s.getDescribe());
			pst.setInt(3, s.getEnable());
			pst.execute();
			pst.close();
			scr.put(s.ScriptID(), s);
			f=true;
		} catch (Exception ex){
			error = ex.getMessage();
		}
		return f;
	}
	public boolean delete(Integer id) {
		Log.info(" > ScriptWork(): Delete Script ID = " + id);
		boolean f = false;
		try {
			PreparedStatement pst = cmd.srv.us.db.getDb().prepareStatement("delete from `scripts` where `id` = ? limit 1");
			pst.setInt(1, id);
			pst.execute();
			pst.close();
			scr.remove(id);
			f=true;
		} catch (Exception ex){
			error = ex.getMessage();
		}
		return f;
	}
	public Scripts getScript(int id){
		if(!scr.containsKey(id)) return null;
		return scr.get(id);
	}
	public boolean ScriptImport(String fileName){
		return ScriptImport("__SCRPATH__", fileName);
	}
	public boolean ScriptImport(String path, String fileName){
		Log.info(" > ScriptWork(): Import Script Name: " + fileName);
		boolean f = false;
		String s="";
		try {
			path = path.replace("__SCRPATH__", "services/" + cmd.srv.getName() + "/scripts/command/");
			path = path.replace("__TXT__", "text/");
			path = path.replace("__SRVNAME__", cmd.srv.getName());
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("./" + path + fileName + ".bsh"),"windows-1251"));
			while(in.ready()) s += in.readLine() + '\n';
			in.close();
			Scripts script = new Scripts();
			script.setScript(s);
			if (create(script)) f=true;
		} catch (Exception ex) {
			error += ex.getMessage();
		}
		return f;
	}
	public boolean ScriptExport(Integer id){
		return ScriptExport("__SCRPATH__", id);
	}
	public boolean ScriptExport(String path, Integer id){
		Log.info(" > ScriptWork(): Export Script ID = " + id);
		boolean f = false;
		try {
			ename = nameGenerate();
			path = path.replace("__SCRPATH__", "services/" + cmd.srv.getName() + "/scripts/command/");
			path = path.replace("__TXT__", "text/");
			path = path.replace("__SRVNAME__", cmd.srv.getName());
			File file = new File("./" + path + ename + ".bsh");
			while(file.exists()) {
				ename = nameGenerate();
				file = new File("./" + path + ename + ".bsh");
			}
			OutputStreamWriter fil = new OutputStreamWriter(new FileOutputStream(file.getPath(), true),"windows-1251");
			fil.write(getScript(id).getScript());
			fil.close();
		} catch (Exception ex) {
			error += ex.getMessage();
		}
		return f;
	}
	private static String nameGenerate(){
		String s = "123456789ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijklmnprstuvwxyz";
		Random r = new Random();
		String v="";
		for(int i=0;i<10;i++){
			v += s.charAt(r.nextInt(s.length()));
		}
		return v;
	}
	class Scripts extends DBObject {
		private int id = 0;
		private String script = "";
		private String describe = "";
		private int enable = 0;
		public Scripts(){

		}
		public Scripts(int _id, String _script, String _describe, int _enable){
			id = _id;
			script = _script;
			describe = _describe;
			enable = _enable;
		}
		public int ScriptID() {
			return id;
		}
		public void setID(int id) {
			this.id = id;
		}
		public String getScript() {
			return script;
		}
		public void setScript(String script) {
			this.script = script;
		}
		public String getDescribe() {
			return describe;
		}
		public void setDescribe(String describe) {
			this.describe = describe;
		}
		public int getEnable() {
			return enable;
		}
		public void setEnable(int enable) {
			this.enable = enable;
		}
		@Override
		public String[] getFields() {
			return null;
		}
		@Override
		public String getTableName() {
			return null;
		}
		@Override
		public int[] getTypes() {
			return null;
		}

	}
}
