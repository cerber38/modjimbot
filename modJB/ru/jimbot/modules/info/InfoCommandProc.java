/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.info;

import com.google.api.translate.Language;
import com.google.api.translate.Translate;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.modules.AbstractCommandProcessor;
import ru.jimbot.modules.AbstractServer;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.Log;
import ru.jimbot.util.MainProps;
import org.htmlparser.Parser;
import org.htmlparser.filters.CssSelectorNodeFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.*;

/**
 * @author fraer72
 */

public class InfoCommandProc extends AbstractCommandProcessor {
    private InfoServer srv;
    private CommandParser parser = null;
    private HashMap<String,Cmd> commands = new HashMap<String,Cmd>();
    private ConcurrentHashMap <String, String> town;
    private boolean firstStartMsg = false;

    public InfoCommandProc(InfoServer srv) {
    this.srv = srv;
    parser = new CommandParser(commands);
    town = new ConcurrentHashMap<String,String>();
    init();
    }

    private void init(){
    commands.put("!about", new Cmd("!about","",1));
    commands.put("!help", new Cmd("!help","",2));
    commands.put("!google", new Cmd("!google","$s",3));
    commands.put("!weather", new Cmd("!weather","$c",4));
    commands.put("!translate_en", new Cmd("!translate_en","$s",5));
    commands.put("!translate_ru", new Cmd("!translate_ru","$s",6));
    }

     public AbstractServer getServer(){
    	return srv;
    }


    private void firstMsg(){
    if(!firstStartMsg)
    new WeatherReader(srv).loadTown(srv.getName());
    firstStartMsg=true;
    }

     public void addTown(String Key, String id){
     if(!town.containsKey(Key))
         town.put(Key, id);     
     }

     private boolean isTown(String Key){
     return town.containsKey(Key);
     }

     private String getTown(String Key){
     return town.get(Key);
     }

     /**
      * Парсер инфо бота
      * @param proc
      * @param uin
      * @param msg
      */

    public void parse(IcqProtocol proc, String uin, String msg) {
    firstMsg();
    try {
     msg = msg.trim();
     if(msg.length()==0){
      Log.getLogger(srv.getName()).error("Пустое сообщение в парсере команд: " + uin + ">" + msg);
     return;
     }
     if(msg.charAt(0)=='!'){
     Log.getLogger(srv.getName()).info("INFO-BOT COM_LOG: " + uin + ">>" + msg);
     }

    //Обработка команд
    int tp = parser.parseCommand(msg);
    int tst=0;
    if(tp<0)tst=0;
    else
    tst = tp;
    switch (tst){
    case 1:
    proc.mq.add(uin,MainProps.getAbout());
    break;
    case 2:
    proc.mq.add(uin,"Вас приветствует jImBot - info!" +
                    "\n!google <запрос> - выполняет поисковые запросы в Google, " +
                    "выдает самый первый найденный результат." +
                    "\n!weather <город> - запрос погоды с gismeteo.ru" +
                    "\n!translate_en <text> - перевод русского текста на англиский" +
                    "\n!translate_ru <text> - перевод английского текста на русский" +
                    "\n!about - информация о программе" +
                    "\nДля получения помоши пошлите \"!help\"\n" +
                    "Не посылайте сообщения чаще,  чем раз в 3с.");
    break;
    case 3:
    commandGoogle(proc, uin, parser.parseArgs(msg));
    break;
    case 4:
    commandWeather(proc, uin, parser.parseArgs(msg));
    break;
    case 5:
    commandTranslate(proc, uin, parser.parseArgs(msg), Language.RUSSIAN, Language.ENGLISH);
    break;
    case 6:
    commandTranslate(proc, uin, parser.parseArgs(msg), Language.ENGLISH, Language.RUSSIAN);
    break;
    default:
    Log.getLogger(srv.getName()).info("INFO-BOT: " + uin + ">>" + msg);
    proc.mq.add(uin,"Неизвестная команда, для помощи используйте !help");
    }
    } catch (Exception ex) {
    ex.printStackTrace();
    }
    }
    
    /**
     * Выполняет поисковые запросы в Google.
     * Выдает самый первый найденный результат, если таковой имеется. 
     * @param proc
     * @param uin
     * @param v
     */


    private void commandGoogle(IcqProtocol proc, String uin, Vector v){
    String msg = (String)v.get(0);
    msg = msg.toLowerCase();
    if (msg.length() <= 0) {
    proc.mq.add(uin, "Пустой запрос");
    return;
    }
    try {
    Parser parse_html = new Parser("http://www.google.ru/search?hl=ru&num=1&q=" + msg);
    // вытаскиваем из html кода первую найденную ссылку, если таковая имеется
    NodeList nodeList = parse_html.extractAllNodesThatMatch(new CssSelectorNodeFilter("div#res * li > h3 > a"));
    if (nodeList.size() > 0) {
    LinkTag a = (LinkTag) nodeList.elementAt(0);
    proc.mq.add(uin, a.getLinkText() + " | " + a.getLink());
    } else {
    proc.mq.add(uin, "Ничего не найдено");
    }
    } catch (ParserException ex) {
    ex.printStackTrace();
    }
    }

    /**
     * Выполняет запрос погоды с gismeteo.ru
     * @param proc
     * @param uin
     * @param v
     */
    
    private void commandWeather(IcqProtocol proc, String uin, Vector v){
    String msg = (String)v.get(0);
    msg = msg.toLowerCase();
    if (msg.length() <= 0) {
    proc.mq.add(uin, "Пустой запрос");
    return;
    }
    if (town.isEmpty()) {
    proc.mq.add(uin, "Запрос в данное время не может быть выполнен!");
    return;
    }
    if (!isTown(msg)) {
    proc.mq.add(uin, "Извините, вашего города нет в нашей базе!");
    return;
    }
    new WeatherReader(srv).startParseXml(uin, "http://informer.gismeteo.ru/xml/" + getTown(msg) + "_1.xml");
    }

    /**
     * Перевод текста с англиского на русский и наоборот
     * @param proc
     * @param uin
     * @param v
     * @param language0
     * @param language1
     */


    private void commandTranslate(IcqProtocol proc, String uin, Vector v,  Language language0, Language language1){
    /*Existing users please note - we've enforced setting the HTTP referrer as of version 0.7,
     which was previously optional. I believe this value should be set to your website address ideally,
     to help Google monitor who's using the APIs and allow them to contact you if necessary. */
    Translate.setHttpReferrer("http://code.google.com/p/modjimbot/");

    String msg = (String)v.get(0);
    if (msg.length() <= 0) {
    proc.mq.add(uin, "Пустой запрос");
    return;
    }
    try {

    String translatedText = Translate.execute(msg, language0, language1);

    proc.mq.add(uin, "Перевод: " + translatedText);
    } catch (Exception ex) {
    ex.printStackTrace();
    }
    }


}
