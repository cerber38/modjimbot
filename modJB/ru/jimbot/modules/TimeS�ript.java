
package ru.jimbot.modules;


import ru.jimbot.protocol.IcqProtocol;
import bsh.Interpreter;
import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import ru.jimbot.util.Log;
//import sun.org.mozilla.javascript.internal.Interpreter;



public class TimeSСЃript
    {

    private ScriptCash scripts = new ScriptCash();
    private static ConcurrentHashMap<String,TimeSСЃript> instances = new ConcurrentHashMap<String,TimeSСЃript>();
    private static TimeSСЃript mainInst = new TimeSСЃript("");
    private String sn = ""; // пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ

    
    public TimeSСЃript(String s)
    {
    sn = s;
    }


     /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ TimeSkript пїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ
     * @param name
     * @return
     */
    public static TimeSСЃript getInstance(String name)
    {
    if(name.equals("")) return mainInst;
    if(!instances.containsKey(name)) instances.put(name, new TimeSСЃript(name));
    return instances.get(name);
    }



    /**
     * пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅ пїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅпїЅ
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
    Log.info("пїЅпїЅпїЅпїЅпїЅпїЅ пїЅпїЅпїЅпїЅпїЅпїЅпїЅ: " + ex.getMessage());
    }
    return s;
    }




}
