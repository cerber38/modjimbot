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

package ru.jimbot.modules;

import java.util.HashMap;
import java.util.Vector;

/**
 *
 * @author Prolubnikov Dmitry
 */
public class CommandParser {
    private HashMap<String, Cmd> cmd;
    
    public CommandParser(HashMap<String, Cmd> cmd) {
        this.cmd = cmd;
    }

    /**
     * Возвращает код команды, или -1 если не найдена
     */
    public int parseCommand(String s){
        String s1 = "";
        try{
            s1 = s.trim().split(" ")[0];
            s1 = s1.toLowerCase();
        } catch (Exception e) {}
        if(cmd.containsKey(s1))
        	return cmd.get(s1).cmd;
        else
        	return -1;
//        for(int i=0;i<cmd.length;i++){
//            if(s1.equalsIgnoreCase(cmd[i][0])) return i;
//        }
//        return -1;
    }
    
    /**
     * Возвращает описание команды
     * @param s
     * @return
     */
    public Cmd parseCommand2(String s){
        String s1 = "";
        try{
            s1 = s.trim().split(" ")[0];
            s1 = s1.toLowerCase();
        } catch (Exception e) {}
        if(cmd.containsKey(s1))
        	return cmd.get(s1);
        else
        	return new Cmd();
    }
    
    /**
     * Возвращает вектор аргументов команды
     * $s - произвольная строка символов до конца
     * $c - слово, без пробелов
     * $n - число
     */
    public Vector parseArgs(String s){
        Vector v = new Vector();
        Cmd c = parseCommand2(s);
//        int k = parseCommand(s);
        if(c.cmd==-1) return v;
        String arg = c.param;
        if(arg.equals("")) return v;
        for(int i=0;i<arg.split(" ").length;i++){
            if(i>=(s.split(" ").length-1)){
                if(arg.split(" ")[i].equals("$s") ||
                        arg.split(" ")[i].equals("$c")) 
                    v.add("");
                else
                    v.add(0);
                    
            } else {
                if(arg.split(" ")[i].equals("$c"))
                    v.add(s.split(" ")[i+1]);
                else if(arg.split(" ")[i].equals("$n"))
                    v.add(parseN(s.split(" ")[i+1]));
                else
                    v.add(parseS(i+1,s));
            }
        }
        return v;
    }
    
    private String parseS(int c, String s){
        int k=0,i=0;
        for(i=0;i<s.length();i++){
            if(s.charAt(i)==' ') k++;
            if(k>=c) break;
        }
        return s.substring(i+1);
    }
    
    private int parseN(String s){
        try{
            return Integer.parseInt(s);
        } catch (Exception ex) {}
        return 0;
    }
}
