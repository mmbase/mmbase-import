package org.mmbase.module;
 
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

public interface scancacheInterface {
	public void init();
	public String get(String pool,String key);
	public String get(String pool,String key,String line);
	public String getNew(String pool,String key,String line);
	public String put(String pool,String key,String value);
	public String newput(String pool,HttpServletResponse res,String key,String value);
	public String newput2(String pool,String key,String value, int cachetype);
}
