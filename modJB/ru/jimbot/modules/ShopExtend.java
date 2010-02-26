package ru.jimbot.modules;

import java.util.Vector;

public class ShopExtend
{
	private String uin;
	private long vremia;
	private String msg;
	private String cmd;
	private Vector data;


public ShopExtend(String _uin, String _cmd, String _msg, Vector _data, long expire)
{
		vremia = System.currentTimeMillis() + expire;
		uin = _uin;
		cmd = _cmd;
		msg = _msg;
		data = _data;
}

	public String getMsg()
    {
	return msg;
	}

	public String getUin()
    {
	return uin;
	}

	public String getCmd()
    {
	return cmd;
	}

	public Vector getData()
    {
	return data;
	}

	public boolean isExpire()
    {
	return System.currentTimeMillis()>vremia;
	}
}