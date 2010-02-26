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

package ru.jimbot.modules.anek;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import ru.jimbot.util.Log;
/**
 * Выполнение внешней программы
 * @author Prolubnikov Dmitry
 */
public class StartDos {
    private Process process;
    private String processName;
    
    /** Creates a new instance of StartDos */
    public StartDos() {
    }
    
    public String rebootBot(){
        String run = ".\\reboot.bat";
        String s="";
        try {
            process = Runtime.getRuntime().exec(run);
            process.getOutputStream().close();
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(),"windows-1251"));
            String line;
            while((line = in.readLine()) != null) {
                if (!line.equals("")) s += line + '\n';
            }
            in.close();
            process.destroy();            
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.debug(s);
        return s;        
    }
}
