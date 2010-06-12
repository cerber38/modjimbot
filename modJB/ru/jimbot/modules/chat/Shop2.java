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

    /**
     * @author Юрий
     */

    public class Shop2 {
    private HashMap<String, Cmd> commands = new HashMap<String, Cmd>();
    private HashMap<String, CommandExtend> BredMap;
    private CommandParser parser;
    private ChatCommandProc cmd;

    public Shop2(ChatCommandProc c){
    parser = new CommandParser(commands);
    cmd = c;
    BredMap = new HashMap<String, CommandExtend>();
    init();
    }

    private void init(){
    commands.put("!автосалон", new Cmd("!автосалон","",1));
    commands.put("!недвижимость", new Cmd("!недвижимость","",2));
    commands.put("!бутик", new Cmd("!бутик","",3));
    commands.put("!зоо", new Cmd("!зоо","",4));
    commands.put("!делтовар", new Cmd("!делтовар","$c $n",5));
    commands.put("!аддтовар", new Cmd("!аддтовар","$c $n $s",6));
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
    if(BredMap.containsKey(uin))
    if(!BredMap.get(uin).isExpire())
    tp = parser.parseCommand(BredMap.get(uin).getCmd());
    else {
    tp = parser.parseCommand(tmsg);
    BredMap.remove(uin);
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

    default:
    f = false;
    }
    return f;
    }

private void MagAvto(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
    Users uss = cmd.srv.us.getUser(uin);
    int i = 0;
    boolean AVTO = false;
    if(BredMap.containsKey(uin)){
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
        if(uss.ball < (Price("avto",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("avto",i))-uss.ball) + " баллов для покупки");
            return;
        }
        AVTO = true;
        BredMap.remove(uin);
    }
    if(!AVTO){
        proc.mq.add(uin,ListTovar("avto"));
        BredMap.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("avto",i);
    uss.car = Tovar("avto",i);
    cmd.srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("avto",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

private void MagDom(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
    Users uss = cmd.srv.us.getUser(uin);
    int i = 0;
    boolean DOM = false;
    if(BredMap.containsKey(uin)){
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
        if(uss.ball < (Price("dom",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("dom",i))-uss.ball) + " баллов для покупки");
            return;
        }
        DOM = true;
        BredMap.remove(uin);
    }
    if(!DOM){
        proc.mq.add(uin,ListTovar("dom"));
        BredMap.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("dom",i);
    uss.home = Tovar("dom",i);
    cmd.srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("dom",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

private void MagOdej(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
    Users uss = cmd.srv.us.getUser(uin);
    int i = 0;
    boolean ODEJ = false;
    if(BredMap.containsKey(uin)){
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
        if(uss.ball < (Price("odejda",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("odejda",i))-uss.ball) + " баллов для покупки");
            return;
        }
        ODEJ = true;
        BredMap.remove(uin);
    }
    if(!ODEJ){
        proc.mq.add(uin,ListTovar("odejda"));
        BredMap.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("odejda",i);
    uss.clothing = Tovar("odejda",i);
    cmd.srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("odejda",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

private void MagJivot(IcqProtocol proc, String uin, Vector v, String mmsg) {
    if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
    Users uss = cmd.srv.us.getUser(uin);
    int i = 0;
    boolean JIV = false;
    if(BredMap.containsKey(uin)){
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
        if(uss.ball < (Price("jivotnoe",i))){
            proc.mq.add(uin,uss.localnick + " у вас не хватает " + ((Price("jivotnoe",i))-uss.ball) + " баллов для покупки");
            return;
        }
        JIV = true;
        BredMap.remove(uin);
    }
    if(!JIV){
        proc.mq.add(uin,ListTovar("jivotnoe"));
        BredMap.put(uin, new CommandExtend(uin, mmsg, mmsg,v, 2*60000));
        return;
    }
    if(i == 0){proc.mq.add(uin,uss.localnick + " вы вышли из магазина"); return;}
    uss.ball -= Price("jivotnoe",i);
    uss.animal = Tovar("jivotnoe",i);
    cmd.srv.us.updateUser(uss);
    proc.mq.add(uin,uss.localnick + " вы купили  - '" + Tovar("jivotnoe",i) + "'\nУ Вас осталось " + uss.ball + " баллов.");
}

    /*
     * Список товаров
     */
    public String ListTovar(String table) {
    String list = "Здравствуйте я могу Вам предложить:\n" +
    "Номер ~ Название » Цена(баллов)\n";
    try{
    PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select id, tovar, price from " + table);
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
    if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
    if(!cmd.auth(proc,uin, "gift")) return;
    try{
        Users uss = cmd.srv.us.getUser(uin);
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
        if (cmd.radm.testMat1(cmd.radm.changeChar(tovar))){
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
    if(!cmd.isChat(proc,uin) && !cmd.psp.testAdmin(uin)) return;
    if(!cmd.auth(proc,uin, "gift")) return;
    try{
        Users uss = cmd.srv.us.getUser(uin);
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
        cmd.srv.us.db.executeQuery("DELETE FROM " + table2 + " WHERE id=" + id);
        cmd.srv.us.db.executeQuery("REPAIR TABLE " + table2);
        }catch (Exception ex){
        ex.printStackTrace();
        proc.mq.add(uin,"При удалении товара возникла ошибка - "+ex.getMessage());
        }
        }

   public boolean TestTable(String table) {
    try{
    PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("SELECT * FROM " + table + " WHERE 0");
    ResultSet rs = pst.executeQuery();
    rs.close();
    pst.close();
    }catch (Exception e){
    return false;
    }
    return true;
    }

    public int getCount(String table, int id){
    String q = "SELECT count(*) FROM " + table + " WHERE id="+id;
    Vector<String[]> v = cmd.srv.us.db.getValues(q);
    return Integer.parseInt(v.get(0)[0]);
    }

    public int Price(String table, int id) {
    int price = 0;
    try{
    PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select price from " + table + " where id=" + id);
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

    public String Tovar(String table, int id) {
    String tovar = "";
    try{
    PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("select tovar from " + table + " where id=" + id);
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

    public void AddBD(String table, String tovar, int price) {
    try {
    PreparedStatement pst = (PreparedStatement) cmd.srv.us.db.getDb().prepareStatement("insert into " + table + " values(null, ?, ?)");
    pst.setString(1,tovar);
    pst.setInt(2,price);
    pst.execute();
    pst.close();
    }catch (Exception ex){
    ex.printStackTrace();
    }
    }

}