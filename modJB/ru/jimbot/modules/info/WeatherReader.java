/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;
import org.xml.sax.Attributes; 
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Реализация парсера xml
 * @author fraer72
 */

public class WeatherReader extends DefaultHandler{
    private InfoServer srv;
    private Vector data;
    static String uin = "";
    /*Время суток*/
    private String[] getTod = {"Ночь", "Утро", "День", "Вечер"};
    /*Месяц*/
    private String[] getMount = {"", "янв.", "фев.", "мар", "апр.", "май.", "июн.", "июл.", "авг.", "сен.", "окт.", "ноя.", "дек."};
    /*День*/
    private String[] getDay = {"", "Вс.", "Пн.", "Вт.", "Ср.", "Чт.", "Пт.", "Сб."};
    /*Облачность по градациям*/
    private String[] getCloudiness = {"ясно", "малооблачно", "облачно", "пасмурно"};
    /*Тип осадков*/
    private String[] getPrecipitation = {"", "", "", "", "дождь", "ливень", "снег", "снег", "гроза", "нет данных", "без осадков"};

  public WeatherReader(InfoServer srv) {
  this.srv = srv;
  }

  public WeatherReader() {
  }

  /**
   * Начало документа
   * @throws SAXException
   */

  public void startDocument() throws SAXException {
  data = new Vector();
  if(!data.contains(uin)) data.add(uin);
  }

  /**
   * Начало элемента. Функции передается название элемента(открывающий тэг) и список его атрибутов.
   * @param namespaceURI
   * @param localName
   * @param qName
   * @param atts
   * @throws SAXException
   */

  public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
    try{
    if(qName.equals("TOWN"))
    data.add(java.net.URLDecoder.decode(atts.getValue("sname"), "Cp1251"));
    else if(qName.equals("FORECAST")) {
    data.add(atts.getValue("day"));
    data.add(Integer.parseInt(atts.getValue("month")));
    data.add(atts.getValue("year"));
    data.add(Integer.parseInt(atts.getValue("tod")));
    data.add(Integer.parseInt(atts.getValue("weekday")));
    } else if (qName.equals("PHENOMENA")) {
    data.add(Integer.parseInt(atts.getValue("cloudiness")));
    data.add(Integer.parseInt(atts.getValue("precipitation")));
    data.add(atts.getValue("rpower"));
    data.add(atts.getValue("spower"));
    } else if (qName.equals("PRESSURE")) {
    data.add(atts.getValue("max"));
    data.add(atts.getValue("min"));
    } else if (qName.equals("TEMPERATURE")) {
    data.add(atts.getValue("max"));
    data.add(atts.getValue("min"));
    } else if (qName.equals("WIND")) {
    data.add(atts.getValue("max"));
    data.add(atts.getValue("min"));
    data.add(atts.getValue("direction"));
    } else if (qName.equals("RELWET")) {
    data.add(atts.getValue("max"));
    data.add(atts.getValue("min"));
    }
    } catch (Exception ex) {
    ex.printStackTrace();
    }
  }

   /**
    * Конец элемента
    * @param namespaceURI
    * @param localName
    * @param qName
    * @throws SAXException
    */


    public void endElement(String namespaceURI, String localName, String qName)
        throws SAXException {
    }


   /**
    * Конец документа
    */

   public void endDocument() {
   try{
   String weather = "Погода в г. " + data.get(1) + ":\n\n"
               + getTod[(Integer) data.get(5)] + " " + data.get(2) + " " +
               getMount[(Integer) data.get(3)] + ", " + getDay[(Integer) data.get(6)] +
               ", " + getCloudiness[(Integer )data.get(7)] + ", " + getPrecipitation[(Integer) data.get(8)] + ", тем. " +
               data.get(13) + ", " + data.get(14) + ",\n"
               + "давление " + data.get(11) + " мм.рт.ст., ветер - " + data.get(15) + "м/с.\n\n"

               + getTod[(Integer) data.get(23)] + " " + data.get(20) + " " +
               getMount[(Integer) data.get(21)] + ", " + getDay[(Integer) data.get(24)] +
               ", " + getCloudiness[(Integer )data.get(25)] + ", " + getPrecipitation[(Integer) data.get(26)] + ", тем. " +
               data.get(31) + ", " + data.get(32) + ",\n"
               + "давление " + data.get(29) + " мм.рт.ст., ветер - " + data.get(33) + "м/с.\n\n"

               + getTod[(Integer) data.get(41)] + " " + data.get(38) + " " +
               getMount[(Integer) data.get(39)] + ", " + getDay[(Integer) data.get(42)] +
               ", " + getCloudiness[(Integer )data.get(43)] + ", " + getPrecipitation[(Integer) data.get(44)] + ", тем. " +
               data.get(49) + ", " + data.get(50) + ",\n"
               + "давление " + data.get(47) + " мм.рт.ст., ветер - " + data.get(51) + "м/с.\n\n"

               + getTod[(Integer) data.get(59)] + " " + data.get(56) + " " +
               getMount[(Integer) data.get(57)] + ", " + getDay[(Integer) data.get(60)] +
               ", " + getCloudiness[(Integer )data.get(61)] + ", " + getPrecipitation[(Integer) data.get(62)] + ", тем. " +
               data.get(67) + ", " + data.get(68) + ",\n"
               + "давление " + data.get(65) + " мм.рт.ст., ветер - " + data.get(69) + "м/с.";
   send(weather,(String) data.get(0));
   data.removeAllElements();
   } catch (Exception ex) {
   ex.printStackTrace();
   }
   }
   
   /**
    * Отправка сообщения всeм активным юзерам
    * @param messages
    */

   private void send(String messages, String uin){
   srv.getIcqProcess().mq.add(uin, messages);
   }


      public void startParseXml(String uin, String link){
      this.uin = uin;
      XMLReader xmlReader = null;
      HttpURLConnection conn = null;
      InputSource source = null;
      try {
      SAXParserFactory spfactory = SAXParserFactory.newInstance();
      /*Установка опций проверки правильности*/
      spfactory.setValidating(false);
      SAXParser saxParser = spfactory.newSAXParser();
      xmlReader = saxParser.getXMLReader();
      /*Установка обработчика содержимого*/
      xmlReader.setContentHandler(new WeatherReader(srv));
      /*Установка ErrorHandler*/
      xmlReader.setErrorHandler(new WeatherReader(srv));
      URL yahoo = new URL(link);
      conn = (HttpURLConnection) yahoo.openConnection();
      source = new InputSource(conn.getInputStream());
      xmlReader.parse(source);
      } catch (Exception ex) {
      ex.printStackTrace();
      send("Запрос не можт быть выполнен в данное время!", uin);
      }
    }


        /**
         * Загрузка списка городов с id
         */


    public void loadTown(String ns){
    if(!new File("./services/" + ns + "/id_town").exists()) return;
    String s = "";
    try {
    BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("./services/" + ns + "/id_town"),"windows-1251"));
    while (r.ready()) {
    s += r.readLine() + "\n";
    }
    r.close();
    } catch (Exception ex) {
    ex.printStackTrace();
    }
    String[] town = s.split("\n");
    for(int i = 0; i < town.length; i++){
    ((InfoCommandProc)srv.cmd).addTown(town[i].split("-")[0].trim(), town[i].split("-")[1].trim());
    }
    }
}
