
package ru.jimbot.modules;


import ru.jimbot.protocol.IcqProtocol;
import bsh.Interpreter;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.util.Log;
//import sun.org.mozilla.javascript.internal.Interpreter;



public class TimeS�ript
    {

    private ScriptCash scripts = new ScriptCash();
    private static ConcurrentHashMap<String,TimeS�ript> instances = new ConcurrentHashMap<String,TimeS�ript>();
    private static TimeS�ript mainInst = new TimeS�ript("");
    private String sn = ""; // ��� �������

    
    public TimeS�ript(String s)
    {
    sn = s;
    }


     /**
     * ���������� ��������� TimeSkript ��� ������� �������
     * @param name
     * @return
     */
    public static TimeS�ript getInstance(String name)
    {
    if(name.equals("")) return mainInst;
    if(!instances.containsKey(name)) instances.put(name, new TimeS�ript(name));
    return instances.get(name);
    }



    /**
     * ���������� ��� �� ��������
     * @return
     */
    public ScriptCash getScripts(){
    	return scripts;
    }




    public  String startTimeScript(IcqProtocol proc, String uin, AbstractServer srv)
    {
    String s = "";
    if(!new File("./services/" + sn + "/scripts/TimeSkript.bsh").exists()) return s;
    try
    {
    Interpreter bsh = new Interpreter();
    bsh.set("proc", proc);
    bsh.set("uin", uin);
    bsh.set("srv", srv);
    bsh.eval(scripts.getScript("./services/" + sn + "/scripts/TimeSkript.bsh"));
    }
    catch (Exception ex)
    {
    ex.printStackTrace();
    Log.info("������ �������: " + ex.getMessage());
    }
    return s;
    }




}
