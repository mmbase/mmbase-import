package org.mmbase.util;

import java.io.*;

public class Execute
{
	private String className = getClass().getName();

	public String execute (String command[])  {
		Process p=null;
		String s="",tmp="";

		BufferedReader	dip= null;
		BufferedReader	dep= null;
 
		try 
		{
			p = (Runtime.getRuntime()).exec(command,null);
			p.waitFor();
		} 
		catch (Exception e) 
		{
			s+=e.toString();
			return s;
		}

		dip = new BufferedReader( new InputStreamReader(p.getInputStream()));
		dep = new BufferedReader( new InputStreamReader(p.getErrorStream()));

		try 
		{
			while ((tmp = dip.readLine()) != null) 
			{
           		s+=tmp+"\n"; 
			}
			while ((tmp = dep.readLine()) != null) 
			{
				s+=tmp+"\n";
			}
		} 
		catch (Exception e) 
		{
			return s;
		}
		return s;
	}

	public String execute (String command) 
	{
		Process p=null;
		String s="",tmp="";

		BufferedReader	dip= null;
		BufferedReader	dep= null;
 
		try 
		{
			p = (Runtime.getRuntime()).exec(command,null);
			p.waitFor();
		} 
		catch (Exception e) 
		{
			s+=e.toString();
			return s;
		}

		dip = new BufferedReader( new InputStreamReader(p.getInputStream()));
		dep = new BufferedReader( new InputStreamReader(p.getErrorStream()));

		try 
		{
			while ((tmp = dip.readLine()) != null) 
			{
           		s+=tmp+"\n"; 
			}
			while ((tmp = dep.readLine()) != null) 
			{
				s+=tmp+"\n";
			}
		} 
		catch (Exception e) 
		{
			return s;
		}
		return s;
	}

	private void writeLog( String msg )
	{
		System.out.println(className + " : " + msg );
	}

	public static void main(String args[])
	{
		Execute execute = new Execute();
		System.out.println(execute.execute(args[0]));
	}
}
