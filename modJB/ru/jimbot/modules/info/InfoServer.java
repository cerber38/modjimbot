/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.jimbot.modules.info;

import ru.jimbot.modules.AbstractProps;
import ru.jimbot.modules.AbstractServer;
import ru.jimbot.modules.MsgInQueue;
import ru.jimbot.modules.UINmanager;
import ru.jimbot.protocol.IcqProtocol;
import ru.jimbot.util.MainProps;

/**
 * @author fraer72
 */

public class InfoServer extends AbstractServer {
    public InfoConnection con;
    public MsgInQueue inq;
    private InfoProps props = null;

        public InfoServer(String name) {
    	this.setName(name);
        this.setType("info");
    	InfoProps.getInstance(name).load();
        cmd = new InfoCommandProc(this);
        con = new InfoConnection(this);
        con.server = MainProps.getServer();
        con.port = MainProps.getPort();
        con.proxy = MainProps.getProxy();
        inq = new MsgInQueue(cmd);
     }

     public void start(){
     String[] icq = new String[InfoProps.getInstance(this.getName()).uinCount()];
     String[] pass = new String[InfoProps.getInstance(this.getName()).uinCount()];
     for(int i=0;i<InfoProps.getInstance(this.getName()).uinCount();i++){
     icq[i] = InfoProps.getInstance(this.getName()).getUin(i);
     pass[i] = InfoProps.getInstance(this.getName()).getPass(i);
     }
     con.uins = new UINmanager(icq, pass, con, true,
     InfoProps.getInstance(this.getName()), this.getName());
     con.uins.start();
     for(int i=0;i<con.uins.count();i++){
     inq.addReceiver((IcqProtocol)con.uins.proc.get(i));
     }
     inq.start();
     isRun=true;
     }

     public AbstractProps getProps() {
    	 if(props==null)
    		 props = InfoProps.getInstance(this.getName());
     	return props;
     }

     public int getIneqSize(){
    	 return inq.size();
     }

    public IcqProtocol getIcqProcess(int baseUin) {
        if(con.uins == null) return null;
               if(con.uins.proc.get(baseUin) == null) return null;
   	 return con.uins.proc.get(baseUin);
    }

    public IcqProtocol getIcqProcess(String baseUin) {
        if(!con.server.equals("")) {
            for(int i=0; i<con.uins.count();i++){
                if(con.uins.getUin(i).equalsIgnoreCase(baseUin))
                    return (IcqProtocol)con.uins.proc.get(i);
            }
        }
        return null;
    }


    public IcqProtocol getIcqProcess() {
    return con.uins.proc.get(0);
    }

}
