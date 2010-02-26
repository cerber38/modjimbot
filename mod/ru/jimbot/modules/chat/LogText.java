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

package ru.jimbot.modules.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import ru.jimbot.db.DBAdaptor;


/**
 * Формирование текстового файла из базы логов
 * 
 * @author Prolubnikov Dmitry
 */
public class LogText {
private DBAdaptor db;

    public LogText(DBAdaptor _db) {
        db = _db;
    }

    public StringBuffer getLogTxt(long t, boolean in){
        long t1 = t - 1000*3600*24;
        StringBuffer sb = new StringBuffer(10240);
        sb.append("<html><body><font color=\"#080000\" face=\"Courier New\" size=\"3\">");
        try{
            PreparedStatement pst;
            if(in) {
                pst = db.getDb().prepareStatement("select time, user_id, user_sn, type, msg, room from LOG t where time between ? and ?");
            }
            else {
                pst = db.getDb().prepareStatement("select time, user_id, user_sn, type, msg, room from LOG t where time between ? and ? and type not in ('IN')");
            }
            pst.setTimestamp(1,new Timestamp(t1));
            pst.setTimestamp(2,new Timestamp(t));
            ResultSet rs = pst.executeQuery();
            while (rs.next()){
                sb.append("[" + rs.getTimestamp(1).toString().substring(0, rs.getTimestamp(1).toString().lastIndexOf(".")) + "] " +
                        "[" + rs.getString(6) + "] ");
                if(rs.getString(4).equals("IN")){
                    sb.append("<i>[" + getStr(rs,3) + "] ");
                    sb.append(getStr(rs,5) + "</i>");
                    sb.append("<br>\n");                    
                } else {
                    sb.append("<b>[" + rs.getInt(2) + "]</b> ");
                    if(getStr(rs,4).equals("KICK")) sb.append(" <b><font color=\"#FF0000\">***KICK USER***</font></b> ");
                    if(getStr(rs,4).equals("REG")) sb.append(" <b><font color=\"#0000FF\">REG -></font></b> ");
                    if(getStr(rs,4).equals("BAN")) sb.append(" <b><font color=\"#FF0000\">***BAN USER***</font></b> ");
                    if(getStr(rs,4).equals("UBAN")) sb.append(" <b><font color=\"#FF0000\">***UBAN USER***</font></b> ");
                    if(getStr(rs,4).equals("STATE")) sb.append(" <b><font color=\"#00FF00\">Статус:</font></b> ");
                    if(getStr(rs,5).indexOf(":")>0) 
                        sb.append("<b>" + getStr(rs,5).substring(0, getStr(rs,5).indexOf(":")) + "</b>" +
                                getStr(rs,5).substring(getStr(rs,5).indexOf(":")));
                    else 
                        sb.append(getStr(rs,5));
                    sb.append("<br>\n");
                    
                }
            }
            rs.close();
            pst.close();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        sb.append("</font></body></html>");
        return sb;
    }
    
    private String getStr(ResultSet rs, int col) throws SQLException{
        String s = rs.getString(col);
        s = s.replaceAll("<", "&lt");
        s = s.replaceAll(">", "&gt");
        return s;
    }
}
