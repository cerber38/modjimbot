/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;
import ru.jimbot.modules.Cmd;
import ru.jimbot.modules.CommandExtend;
import ru.jimbot.modules.CommandParser;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.MainProps;

    /**
     * Магазин раличных товаров
     * @author Юрий, fraer72
     */

    public class Shop2 {
    private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
    private HashMap<String, CommandExtend> ComShop;
    private CommandParser parser;
    private ChatServer srv;
    private ChatProps psp;

    public Shop2(ChatServer srv, ChatProps psp){
    parser = new CommandParser(commands);
    this.srv = srv;
    this.psp = psp;
    ComShop = new HashMap<String, CommandExtend>();
    init();
    }

    private void init(){
    commands.put("!автосалон", new Cmd("!автосалон","",1));
    commands.put("!недвижимость", new Cmd("!недвижимость","",2));
    commands.put("!бутик", new Cmd("!бутик","",3));
    commands.put("!зоо", new Cmd("!зоо","",4));
    commands.put("!делтовар", new Cmd("!делтовар","$c $n",5));
    commands.put("!аддтовар", new Cmd("!аддтовар","$c $n $s",6));
    commands.put("!reset", new Cmd("!reset","",7));
    }

   /**
    * Добавление новой команды
    * @param name
    * @param c
    * @return - истина, если команда уже существует
    */
    public boolean addCommand(String name, Cmd c)
    {
    boolean f = commands.containsKey(name);
    commands.put(name, c);
    return f;
    }

    public boolean commandShop2(IcqProtocol proc, String uin, String mmsg) {
    String tmsg = mmsg.trim();
    int tp = 0;
    if(ComShop.containsKey(uin + "_reset"))
    if(!ComShop.get(uin + "_reset").isExpire())
    commandResetPurchases(proc, uin, tmsg);
    else {
    tp = parser.parseCommand(tmsg);
    ComShop.remove(uin + "_reset");
    }else
    tp = parser.parseCommand(tmsg);
    if(ComShop.containsKey(uin))
    if(!ComShop.get(uin).isExpire())
    tp = parser.parseCommand(ComShop.get(uin).getCmd());
    else {
    tp = parser.parseCommand(tmsg);
    ComShop.remove(uin);
    }else
    tp = parser.parseCommand(tmsg);
    int tst=0;
    if(tp<0)
    tst=0;
    else
    tst = tp;
    boolean f = true;
    switch (tst){
    case 1:
    MagAvto(proc, uin, parser.parseArgs(tmsg), mmsg);
    break;
    case 2:
    MagDom(proc, uin, parser.parseArgs(tmsg), mmsg);
    break;
    case 3:
    MagOdej(proc, uin, parser.parseArgs(tmsg), mmsg);
    break;
    case 4:
    MagJivot(proc, uin, parser.parseArgs(tmsg), mmsg);
    break;
    case 5:
    Del(proc, uin, parser.parseArgs(tmsg));
    break;
    case 6:
    Add(proc, uin, parser.parseArgs(tmsg));
    break;
    case 7:
    commandResetPurchases(proc, uin, mmsg);
    break;

    default:
    f = false;
    }
    return f;
    }

private void MagAvto(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    Users uss = srv.us.getUser(uin);
    int i = 0;
    boolean AVTO = false;
    if(ComShop.containsKey(uin)){
        try{
            i = Integer.parseInt(mmsg);
        } catch(NumberFormatException e){
            proc.mq.add(uin,uss.localnick + " укажите номер покупки.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && (getCount("avto",i)) == 0){
            proc.mq.add(uin,uss.localnick + " нет такого товара в продаже, выберите правильно.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && uss.ball < (Price("avto",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("avto",i))-uss.ball) + " баллов для покупки");
            return;
        }
        AVTO = true;
        ComShop.remove(uin);
    }
    if(!AVTO){
        proc.mq.add(uin,ListTovar("avto"));
        ComShop.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("avto",i);
    uss.car = (!uss.car.equals("") & !psp.getBooleanProperty("some.on.off") ? (Tovar("avto",i)) : uss.car + ", " + Tovar("avto",i));
    srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("avto",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

private void MagDom(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    Users uss = srv.us.getUser(uin);
    int i = 0;
    boolean DOM = false;
    if(ComShop.containsKey(uin)){
        try{
            i = Integer.parseInt(mmsg);
        } catch(NumberFormatException e){
            proc.mq.add(uin,uss.localnick + " укажите номер покупки.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && (getCount("dom",i)) == 0){
            proc.mq.add(uin,uss.localnick + " нет такого товара в продаже, выберите правильно.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && uss.ball < (Price("dom",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("dom",i))-uss.ball) + " баллов для покупки");
            return;
        }
        DOM = true;
        ComShop.remove(uin);
    }
    if(!DOM){
        proc.mq.add(uin,ListTovar("dom"));
        ComShop.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("dom",i);
    uss.home = (!uss.home.equals("") & !psp.getBooleanProperty("some.on.off") ? (Tovar("dom",i)) : uss.home + ", " + Tovar("dom",i));
    srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("dom",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

private void MagOdej(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    Users uss = srv.us.getUser(uin);
    int i = 0;
    boolean ODEJ = false;
    if(ComShop.containsKey(uin)){
        try{
            i = Integer.parseInt(mmsg);
        } catch(NumberFormatException e){
            proc.mq.add(uin,uss.localnick + " укажите номер покупки.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && (getCount("odejda",i)) == 0){
            proc.mq.add(uin,uss.localnick + " нет такого товара в продаже, выберите правильно.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && uss.ball < (Price("odejda",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("odejda",i))-uss.ball) + " баллов для покупки");
            return;
        }
        ODEJ = true;
        ComShop.remove(uin);
    }
    if(!ODEJ){
        proc.mq.add(uin,ListTovar("odejda"));
        ComShop.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("odejda",i);
    uss.clothing = (!uss.clothing.equals("") & !psp.getBooleanProperty("some.on.off") ? (Tovar("odejda",i)) : uss.clothing + ", " + Tovar("odejda",i));
    srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("odejda",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

private void MagJivot(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    Users uss = srv.us.getUser(uin);
    int i = 0;
    boolean JIV = false;
    if(ComShop.containsKey(uin)){
        try{
            i = Integer.parseInt(mmsg);
        } catch(NumberFormatException e){
            proc.mq.add(uin,uss.localnick + " укажите номер покупки.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && (getCount("jivotnoe",i)) == 0){
            proc.mq.add(uin,uss.localnick + " нет такого товара в продаже, выберите правильно.\nДля выхода выберите '0'");
            return;
        }
        if(i != 0 && uss.ball < (Price("jivotnoe",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("jivotnoe",i))-uss.ball) + " баллов для покупки");
            return;
        }
        JIV = true;
        ComShop.remove(uin);
    }
    if(!JIV){
        proc.mq.add(uin,ListTovar("jivotnoe"));
        ComShop.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("jivotnoe",i);
    uss.animal = (!uss.animal.equals("") & !psp.getBooleanProperty("some.on.off") ? (Tovar("jivotnoe",i)) : uss.animal + ", " + Tovar("jivotnoe",i));
    srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("jivotnoe",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

    /*
     * Список товаров
     */
    private String ListTovar(String table) {
    String list = "Здравствуйте я могу Вам предложить:\n" +
    "Номер ~ Название » Цена(баллов)\n";
    try{
    PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select id, tovar, price from " + table);
    ResultSet rs = pst.executeQuery();
    while(rs.next()){
    list += rs.getInt(1) + " ~ " + rs.getString(2) +  " » " + rs.getInt(3) + '\n';
    }
    rs.close();
    pst.close();
    }catch (Exception ex){
    ex.printStackTrace();
    }
    list += "Для покупки выберите цифру\nДля выхода выберите '0'";
    return list;
    }

    /*
     * Добавить товар
     */
    private void Add(IcqProtocol proc, String uin, Vector v) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!((ChatCommandProc)srv.cmd).auth(proc,uin, "gift")) return;
    try{
        Users uss = srv.us.getUser(uin);
        String table = (String)v.get(0);
        int price = (Integer)v.get(1);
        String tovar = (String)v.get(2);
        if(table.equals("") || table.equals(" ")){
        proc.mq.add(uin,uss.localnick + " вы не указали категорию куда добавить товар(авто, дом, одежда, животное)");
        return;
        }
        String table2 = table.toLowerCase();//опустим регистр
        table2 = table2.replace("авто", "avto");
        table2 = table2.replace("дом", "dom");
        table2 = table2.replace("одежда", "odejda");
        table2 = table2.replace("животное", "jivotnoe");
        if(!TestTable(table2)){
        proc.mq.add(uin,uss.localnick + " такой категории не существует(авто, дом, одежда, животное)");
        return;
        }
        if(price <= 0){
        proc.mq.add(uin,uss.localnick + " цена не может быть '0' или меньше '0'");
        return;
        }
        if(tovar.equals("") || tovar.equals(" ")){
        proc.mq.add(uin,uss.localnick + " вы не указали название товара для продажи");
        return;
        }
        if (((ChatCommandProc)srv.cmd).radm.testMat1(((ChatCommandProc)srv.cmd).radm.changeChar(tovar))){
        proc.mq.add(uin,uss.localnick + " в названии товара мат 'МАТ'");
        return;
        }
        if(tovar.length() > 50){
        proc.mq.add(uin,uss.localnick + " название товара слишком длинное");
        return;
        }
        AddBD(table2, tovar, price);
        proc.mq.add(uin,uss.localnick + " вы успешно добавили новый товар");
        }catch (Exception ex){
        ex.printStackTrace();
        proc.mq.add(uin,"При создании товара возникла ошибка - "+ex.getMessage());
        }
        }

    /*
     * Удалить товар
     */
    private void Del(IcqProtocol proc, String uin, Vector v) {
    if(!((ChatCommandProc)srv.cmd).isChat(proc,uin) && !psp.testAdmin(uin)) return;
    if(!((ChatCommandProc)srv.cmd).auth(proc,uin, "gift")) return;
    try{
        Users uss = srv.us.getUser(uin);
        String table = (String)v.get(0);
        int id = (Integer)v.get(1);
        String table2 = table.toLowerCase();//опустим регистр
        table2 = table2.replace("авто", "avto");
        table2 = table2.replace("дом", "dom");
        table2 = table2.replace("одежда", "odejda");
        table2 = table2.replace("животное", "jivotnoe");
        if(!TestTable(table2)){
        proc.mq.add(uin,uss.localnick + " такой категории не существует(авто, дом, одежда, животное)");
        return;
        }
        if(getCount(table2,id) == 0){
        proc.mq.add(uin,uss.localnick + " нет такого товара");
        return;
        }
        proc.mq.add(uin,uss.localnick + " товар '" + Tovar(table2,id) + "' успешно удален");
        srv.us.db.executeQuery("DELETE FROM " + table2 + " WHERE id=" + id);
        srv.us.db.executeQuery("REPAIR TABLE " + table2);
        }catch (Exception ex){
        ex.printStackTrace();
        proc.mq.add(uin,"При удалении товара возникла ошибка - "+ex.getMessage());
        }
        }

   private boolean TestTable(String table) {
    try{
    PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("SELECT * FROM " + table + " WHERE 0");
    ResultSet rs = pst.executeQuery();
    rs.close();
    pst.close();
    }catch (Exception e){
    return false;
    }
    return true;
    }

    private int getCount(String table, int id){
    String q = "SELECT count(*) FROM " + table + " WHERE id="+id;
    Vector<String[]> v = srv.us.db.getValues(q);
    return Integer.parseInt(v.get(0)[0]);
    }

    public int Price(String table, int id) {
    int price = 0;
    try{
    PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select price from " + table + " where id=" + id);
    ResultSet rs = pst.executeQuery();
    if(rs.next()){
        price += rs.getInt(1);
    }
    rs.close();
    pst.close();
    }catch (Exception ex){
    ex.printStackTrace();
    }
    return price;
    }

    private String Tovar(String table, int id) {
    String tovar = "";
    try{
    PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("select tovar from " + table + " where id=" + id);
    ResultSet rs = pst.executeQuery();
    if(rs.next()){
        tovar += rs.getString(1);
    }
    rs.close();
    pst.close();
    }catch (Exception ex){
    ex.printStackTrace();
    }
    return tovar;
    }

    private void AddBD(String table, String tovar, int price) {
    try {
    PreparedStatement pst = (PreparedStatement) srv.us.db.getDb().prepareStatement("insert into " + table + " values(null, ?, ?)");
    pst.setString(1,tovar);
    pst.setInt(2,price);
    pst.execute();
    pst.close();
    }catch (Exception ex){
    ex.printStackTrace();
    }
    }

    /**
     * Удалени не нужных покупок
     * @param proc
     * @param uin
     * @param mmsg
     */


    private void commandResetPurchases (IcqProtocol proc, String uin, String mmsg){
    Users uss = srv.us.getUser(uin);
    Vector v = new Vector();
    String s = "";
    if(!ComShop.containsKey(uin + "_reset")){
        ComShop.put(uin + "_reset", new CommandExtend(uin, "0", "0", v, 2*60000));
        proc.mq.add(uin,uss.localnick + " укажите категорию товара: \n 1) Автосалон \n 2) Недвижимость \n 3) Бутик \n 4) Зоо \n\n " +
                "Для выхода пошлите 0");
        return;
    }
    /*1. Шаг*/
    if(Integer.parseInt(ComShop.get(uin + "_reset").getMsg()) == 0){
        /*Проверим ответ*/
        if(!MainProps.testInteger(mmsg)){
        proc.mq.add(uin,uss.localnick + " категория товара указан не верно!");
        return;
        }
        if(Integer.parseInt(mmsg) < 0 || Integer.parseInt(mmsg) > 4){
        proc.mq.add(uin,uss.localnick + " категория товара указан не верно!");
        return;
        }
        switch(Integer.parseInt(mmsg)){
            case 0:
            proc.mq.add(uin,uss.localnick + " команда завершена!");
            ComShop.remove(uin + "_reset");
            break;
            case 1:
            if(uss.car.equals("")){
            proc.mq.add(uin,uss.localnick + " у вас нет покупок в автосалоне!");
            ComShop.remove(uin + "_reset");
            return;
            }
            String[] some = uss.car.split(",");
            s += "Список ваших покупок в автосалоне:\n";
            for(int i=0; i<some.length; i++)
            s += i+1 + ") " + some[i].trim() + "\n";
            s += "Укажите номер для удаленя\nДля выхода пошлите 0";
            proc.mq.add(uin,s);
            v.add(1);
            ComShop.put(uin + "_reset", new CommandExtend(uin, "", "1", v, 2*60000));
            break;
            case 2:
            if(uss.home.equals("")){
            proc.mq.add(uin,uss.localnick + " у вас нет покупок в магазине недвижимости!");
            ComShop.remove(uin);
            return;
            }
            String[] some2 = uss.home.split(",");
            s += "Список ваших покупок в магазине недвижимости:\n";
            for(int i=0; i<some2.length; i++)
            s += i+1 + ") " + some2[i].trim() + "\n";
            s += "Укажите номер для удаленя\nДля выхода пошлите 0";
            proc.mq.add(uin,s);
            v.add(2);
            ComShop.put(uin + "_reset", new CommandExtend(uin, "", "1", v, 2*60000));
            break;
            case 3:
            if(uss.clothing.equals("")){
            proc.mq.add(uin,uss.localnick + " у вас нет покупок в магазине одежды!");
            ComShop.remove(uin);
            return;
            }
            String[] some3 = uss.clothing.split(",");
            s += "Список ваших покупок в магазине одежды:\n";
            for(int i=0; i<some3.length; i++)
            s += i+1 + ") " + some3[i].trim() + "\n";
            s += "Укажите номер для удаленя\nДля выхода пошлите 0";
            proc.mq.add(uin,s);
            v.add(3);
            ComShop.put(uin + "_reset", new CommandExtend(uin, "", "1", v, 2*60000));
            break;
            case 4:
            if(uss.animal.equals("")){
            proc.mq.add(uin,uss.localnick + " у вас нет покупок в зоо магазине!");
            ComShop.remove(uin);
            return;
            }
            String[] some4 = uss.animal.split(",");
            s += "Список ваших покупок в магазине одежды:\n";
            for(int i=0; i<some4.length; i++)
            s += i+1 + ")" + some4[i] + "\n";
            s += "Укажите номер для удаленя\nДля выхода пошлите 0";
            proc.mq.add(uin,s);
            v.add(4);
            ComShop.put(uin + "_reset", new CommandExtend(uin, "", "1", v, 2*60000));
            break;
        }
            return;
    }
    /*2. Шаг*/
    if(Integer.parseInt(ComShop.get(uin + "_reset").getMsg()) == 1){
        /*Проверим ответ*/
        if(!MainProps.testInteger(mmsg)){
        proc.mq.add(uin,uss.localnick + " номер покупки указан не верно!");
        return;
        }
        if(Integer.parseInt(mmsg) == 0){
        proc.mq.add(uin,uss.localnick + " команда завершена!");
        ComShop.remove(uin + "_reset");
        return;
        }
    v = ComShop.get(uin + "_reset").getData();
        switch((Integer)v.get(0)){
            case 1:
            if((Integer.parseInt(mmsg)-1) < 0 || (Integer.parseInt(mmsg)-1) > uss.car.split(",").length){
            proc.mq.add(uin,uss.localnick + " номер покупки указан не верно!");
            return;
            }
            if(uss.car.split(",").length == 1)
                uss.car = "";
            else
                uss.car = uss.car.replace("," + uss.car.split(",")[(Integer.parseInt(mmsg)-1)], "");                      
            proc.mq.add(uin,uss.localnick + " покупка успешно удалена из списка!");
            break;
            case 2:
            if((Integer.parseInt(mmsg)-1) < 0 || (Integer.parseInt(mmsg)-1) > uss.home.split(",").length){
            proc.mq.add(uin,uss.localnick + " номер покупки указан не верно!");
            return;
            }
            if(uss.home.split(",").length == 1)
                uss.home = "";
            else
                uss.home = uss.home.replace("," + uss.home.split(",")[(Integer.parseInt(mmsg)-1)], "");
            proc.mq.add(uin,uss.localnick + " покупка успешно удалена из списка!");
            break;
            case 3:
            if((Integer.parseInt(mmsg)-1) < 0 || (Integer.parseInt(mmsg)-1) > uss.clothing.split(",").length){
            proc.mq.add(uin,uss.localnick + " номер покупки указан не верно!");
            return;
            }
            if(uss.clothing.split(",").length == 1)
                uss.clothing = "";
            else
                uss.clothing = uss.clothing.replace("," + uss.clothing.split(",")[(Integer.parseInt(mmsg)-1)], "");
            proc.mq.add(uin,uss.localnick + " покупка успешно удалена из списка!");
            break;
            case 4:
            if((Integer.parseInt(mmsg)-1) < 0 || (Integer.parseInt(mmsg)-1) > uss.animal.split(",").length){
            proc.mq.add(uin,uss.localnick + " номер покупки указан не верно!");
            return;
            }
            if(uss.animal.split(",").length == 1)
                uss.animal = "";
            else
                uss.animal = uss.animal.replace("," + uss.animal.split(",")[(Integer.parseInt(mmsg)-1)], "");
            break;
        }
        ComShop.remove(uin + "_reset");
        srv.us.updateUser(uss);
    }


    }

}