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



package ru.jimbot.util;

import java.io.BufferedReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import ru.jimbot.Manager;



/**
 * Класс для создания сервисов. на основе шаблонов.
 * @author nek
 */

public class CreateService {
        private static final String DRIVER = "com.mysql.jdbc.Driver";
        private static final String DATABASE_URL = "jdbc:mysql://localhost:3306/";
        private static Connection connection = null;
        private static Statement statement = null;
        private static Connection con = null;
        private static Statement st = null;

public static void initMySQLService(String name) {
                Log.getLogger(name).info("Create database for service \"" + name + "\" init ...");
                try {
                    Class.forName(DRIVER).newInstance();
                    try {
                        String usr = generate();
                        connection = DriverManager.getConnection(DATABASE_URL, "root", MainProps.getStringProperty("db.pass"));
                        statement = connection.createStatement();
                        // Создаем пользователя
                        statement.executeUpdate("CREATE USER '"+usr+"'@'localhost' IDENTIFIED BY '"+usr+"';");
                        // Даем базовые привелегии и ограничения
                        statement.executeUpdate("GRANT USAGE ON * . * TO '"+usr+"'@'localhost' IDENTIFIED BY '"+usr+"' WITH MAX_QUERIES_PER_HOUR 0 MAX_CONNECTIONS_PER_HOUR 0 MAX_UPDATES_PER_HOUR 0 MAX_USER_CONNECTIONS 0;");
                        // Создаем базу данных для пользователя
                        statement.executeUpdate("CREATE DATABASE IF NOT EXISTS `"+usr+"`;");
                        // Даем на созданную базу данных все привелегии
                        statement.executeUpdate("GRANT ALL PRIVILEGES ON `"+usr+"` . * TO '"+usr+"'@'localhost';");
                        // Перегружаем привелегии
                        statement.executeUpdate("FLUSH PRIVILEGES;");
                        // Закрываем соединение
                        statement.close();
                        connection.close();
                        // Создаем таблицы
                        createTebles(st, con, "ru/jimbot/sql/db.sql", usr, name);
                        // Настраиваем сервис
                        setProps(usr, name);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        statement.close();
                        connection.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
        }
/**

         * Установка настроек базы данных сервиса
         * @param usr Данные автоизации MySQL
         * @param name Имя сервиса
         */

        private static void setProps(String usr, String name) {
                try {

                        Manager.getInstance().getService(name).getProps().setStringProperty("db.user", usr);
                Manager.getInstance().getService(name).getProps().setStringProperty("db.pass", usr);
                Manager.getInstance().getService(name).getProps().setStringProperty("db.dbname", usr);
                Manager.getInstance().getService(name).getProps().save();
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }



        /**
         * Создает таблицы базы данных
         * @param st
         * @param con
         * @param file
         * @param usr
         * @throws IOException
         * @throws SQLException
         */

        private static void createTebles(Statement st, Connection con, String file, String usr, String Service) throws
                IOException, SQLException {
                con = DriverManager.getConnection(DATABASE_URL+usr, usr, usr);
        st = con.createStatement();
                InputStream stream = MainProps.class.getClassLoader().getResourceAsStream(file);
                BufferedReader buffer = new BufferedReader(new InputStreamReader(stream));
        while (buffer.ready()) {
                // Делаем запрос
                st.executeUpdate(buffer.readLine());
        }
        Log.getLogger(Service).info("Creating a database for the service \"" + Service + "\" completed!");
        st.close();
        con.close();
        return;
        }

        /**
         * Генерирует случайную последовательность букв
         * @return
         */
        private static String generate(){
        String s = "1234567890AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
        Random r = new Random();
        String v="";
        for(int i=0;i<10;i++){
            v += s.charAt(r.nextInt(s.length()));
        }
        return v;
    }

}