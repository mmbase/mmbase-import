/*
$Id: INFO.java,v 1.4 2000-02-24 12:41:23 wwwtech Exp $

VPRO (C)

This source file is part of mmbase and is (c) by VPRO until it is being
placed under opensource. This is a private copy ONLY to be used by the
MMBase partners.

$Log: not supported by cvs2svn $
*/
package org.mmbase.module;

import java.lang.*;
import java.net.*;
import java.util.*;
import java.io.*;
import javax.servlet.http.*;

import org.mmbase.util.*;


/**
 * The module which provides access to a filesystem residing in
 * a database
 *
 * @author Daniel Ockeloen
 *
 * @$Revision: 1.4 $ $Date: 2000-02-24 12:41:23 $
 */
public class INFO extends ProcessorModule {

	private String classname = getClass().getName();

	Random rnd;
	String documentroot;
	Hashtable DirCache=new Hashtable();

	public void init() {
		documentroot=System.getProperty("mmbase.htmlroot");
		// org.mmbase super.init();
		rnd=new RandomPlus();
	}

	public void reload() {
	}

	public void onload() {
	}

	public void unload() {
	}

	public void shutdown() {
	}


	/**
	 * INFO, a support module for servscan. provides const and
	 * general info like dates, times, browser info etc etc
	 */
	public INFO() {
	}

	/**
	 * Generate a list of values from a command to the processor
	 */
	 public Vector getList(scanpage sp,StringTagger tagger, String value) {
    	String line = Strip.DoubleQuote(value,Strip.BOTH);
		StringTokenizer tok = new StringTokenizer(line,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("COLOR")) return(doColor(tok));
			if (cmd.equals("RANGE")) return(doRange(tok));
			if (cmd.equals("SCANDATE")) return(doScanDate(sp,tagger));
		}
		return(null);
	}

	/**
	 * Execute the commands provided in the form values
	 */
	public boolean process(scanpage sp, Hashtable cmds,Hashtable vars) {
		debug("CMDS="+cmds);
		debug("VARS="+vars);
		return(false);
	}

	/**
	*	Handle a $MOD command
	*/
	public String replace(scanpage sp, String cmds) {
		StringTokenizer tok = new StringTokenizer(cmds,"-\n\r");
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();	
			if (cmd.equals("TIME")) return(doTime(tok));
			if (cmd.equals("USER")) return(doUser(sp,tok));
			if (cmd.equals("BROWSER")) return(doBrowser(sp,tok));
			if (cmd.equals("OS")) return(doOs(sp,tok));
			if (cmd.equals("RANDOM")) return(doRandom(sp,tok));
			if (cmd.equals("ENCODE")) return(doParamEncode(sp,tok));
			if (cmd.equals("DECODE")) return(doParamDecode(sp,tok));
			if (cmd.equals("ESCAPE")) return(doEscape(sp,tok));
			if (cmd.equals("EXISTS")) return(doExists(sp,tok));
			if (cmd.equals("RELTIME")) return(doRelTime(tok));
			if (cmd.equals("MEMORY")) return(doMemory(tok));
		}
		return("No command defined");
	}
	

	String doEscape(scanpage sp, StringTokenizer tok) {
		String result=null;
		while (tok.hasMoreTokens()) {
			String tmp=tok.nextToken();
			if (result==null) {
				result=tmp;
			} else {
				result+="-"+tmp;
			}
		}
		return(Escape.singlequote(result));
	}


	String doParamEncode(scanpage sp, StringTokenizer tok) {
		String result=null;
		while (tok.hasMoreTokens()) {
			String tmp=tok.nextToken();
			if (result==null) {
				result=tmp;
			} else {
				result+="-"+tmp;
			}
		}
		return(URLParamEscape.escapeurl(result));
	}


	String doParamDecode(scanpage sp, StringTokenizer tok) {
		String result=null;
		while (tok.hasMoreTokens()) {
			String tmp=tok.nextToken();
			if (result==null) {
				result=tmp;
			} else {
				result+="-"+tmp;
			}
		}
		return(URLParamEscape.unescapeurl(result));
	}

	String doOs(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			return("Illegal browser command");	
		} else {
			String tmp=sp.req.getHeader("User-Agent");
			if (tmp.indexOf("Windows 95")!=-1 || tmp.indexOf("Win95")!=-1) {
				return("WIN95");
			} else if (tmp.indexOf("Mac")!=-1) {
				return("MAC");
			}
			return("UNIX");
		}
	}


	String doRandom(scanpage sp, StringTokenizer tok) {
	int j=0;
	int s=0;
	int e=0;
	if (tok.hasMoreTokens()) {
		String start=tok.nextToken();
		if (tok.hasMoreTokens()) {
			String end=tok.nextToken();
			try {
				s=Integer.parseInt(start);
				e=Integer.parseInt(end);
				j=Math.abs(rnd.nextInt()%(e-s));
			} catch (Exception f) {}
		}
	}	
	return(""+(s+j));
	}



	String doBrowser(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			if (cmd.equals("WANTEDHOST")) {
				return(sp.req.getHeader("Host"));
			} else 

			if (cmd.equals("NAME")) return(sp.req.getHeader("User-Agent"));
			String br=sp.req.getHeader("User-Agent");
			if (cmd.equals("NETSCAPE")) {
				if (br.indexOf("Mozilla")==0 && br.indexOf("MSIE")==-1) { return("YES"); } else { return("NO"); }
			} 
			if (cmd.equals("MSIE")) {
				if (br.indexOf("MSIE")!=-1) { return("YES"); } else { return("NO"); }
			}
			return("Illegal browser command");	
		} else {
			return(sp.req.getHeader("User-Agent"));
		}
	}


	String doUser(scanpage sp, StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			//if (cmd.equals("NAME")) return(sp.req.getRemoteUser());
			if (cmd.equals("NAME")) return(HttpAuth.getRemoteUser(sp.req));
			// org.mmbase if (cmd.equals("SESSIONNAME")) return(sp.req.getSessionName());
			if (cmd.equals("HOSTNAME")) return(sp.req.getRemoteHost());
			if (cmd.equals("SECLEVEL")) return(sp.req.getAuthType());
			if (cmd.equals("IPNUMBER")) return(sp.req.getRemoteAddr());
			if (cmd.equals("BACK")) {
				String tmp=sp.req.getHeader("Referer");
				if (tmp!=null) {
					return(tmp);
				} else {
					return("");
				}
			}
			if (cmd.equals("COUNTRY")) {
				String tmp=sp.req.getRemoteHost();
				if (tmp!=null) {
					String domain = tmp.substring(tmp.lastIndexOf('.')+1);
					if (domain!=null) {
						return(domain);
					} else {
						return("");
					}
				}
			}
			if (cmd.equals("DOMAIN")) {
				String tmp=sp.req.getRemoteHost();
				if (tmp!=null && tmp.indexOf('.')!=-1) {
					String domain = tmp.substring(tmp.lastIndexOf('.'));
					tmp = tmp.substring(0,tmp.lastIndexOf('.'));
					tmp = tmp.substring(tmp.lastIndexOf('.')+1);
					domain = tmp+domain;
					if (domain!=null) {
						return(domain);
					} else {
						return("");
					}
				} else {
					return("");
				}
			}
			if (cmd.equals("INDOMAIN")) {
				String tmp=sp.req.getRemoteHost();
				if (tmp!=null && tmp.indexOf('.')!=-1) {
					String domain = tmp.substring(tmp.lastIndexOf('.'));
					tmp = tmp.substring(0,tmp.lastIndexOf('.'));
					tmp = tmp.substring(tmp.lastIndexOf('.')+1);
					domain = tmp+domain;
					if (domain!=null) {
						String serverdomain=getProperty("server","Domain");
						if (serverdomain.equals(domain)) {
							return("YES");
						} 
						return("NO");
					} else {
						return("NO");
					}
				} else {
					String servername=getProperty("server","MachineName");
					if (servername.equals(tmp)) {
						return("YES");
					}
					return("NO");
				}
			}
			return("Illegal user command");	
		} else {
			return(HttpAuth.getRemoteUser(sp.req));
		}
	}


	Vector doRange(StringTokenizer tok) {
		Vector results = new Vector();
		String firstToken="";
		int start=1;
		int end=10;
		int step=1;
		try {
			if (tok.hasMoreTokens()) {
				firstToken=tok.nextToken();
				start=Integer.parseInt(firstToken);
				if (tok.hasMoreTokens()) {
					end=Integer.parseInt(tok.nextToken());
					if (tok.hasMoreTokens()) {
						step=Integer.parseInt(tok.nextToken());
					}
					for (int i=start;i<=end;i+=step) {
						results.addElement(""+i);
					}
				}
			}
		} catch (Exception e) {
			if (firstToken.equals("ALPHA")) {
				for (int i='A';i<='Z';i++) {
					results.addElement(""+(char)i);
				}		
			}
		}
		return(results);
	}

	Vector doColor(StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			Vector results = new Vector();
			if (cmd.equals("BASIC")) { 
				results.addElement("black");results.addElement("000000");
				results.addElement("white");results.addElement("FFFFFF");
				results.addElement("red");results.addElement("FF0000");
				results.addElement("green");results.addElement("00FF00");
				results.addElement("blue");results.addElement("0000FF");
				results.addElement("mint-blue");results.addElement("31FFCE");
				results.addElement("mint-green");results.addElement("20FFFFF");
			}
			return(results);
		}
		return(null);
	}

	public final static int Not=0;
	public final static int Dutch=1;
	public final static int English=2;

	String doTime(StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken(),rtn="";
			
			Date d=new Date(System.currentTimeMillis());
			// extra hack to check if the first param is a number
			// so we can have given times instead of epoch
			try {
				int i=Integer.parseInt(cmd);
				d=new Date(((long)i)*1000);
				cmd=tok.nextToken();
			} catch(Exception e) {
				// no problem it was probably not a number
			}

			int whichname=INFO.Not;

			if (cmd.equals("NAME")) {
				whichname=INFO.English;
				if (tok.hasMoreTokens()) cmd=tok.nextToken();
			} else if (cmd.equals("DUTCHNAME")) {
				whichname=INFO.Dutch;
				if (tok.hasMoreTokens()) cmd=tok.nextToken();
			} else {
				whichname=INFO.Not;
			}
			if (cmd.equals("TIME")) {
				if (d.getMinutes()<10) {
					return(""+d.getHours()+":0"+d.getMinutes());
				} else {
					return(""+d.getHours()+":"+d.getMinutes());
				}
			}
			if (cmd.equals("CURTIME")) return(""+System.currentTimeMillis()/1000);
			if (cmd.equals("DCURTIME")) return(""+DateSupport.currentTimeMillis()/1000);
			if (cmd.equals("CURTIME10")) return(""+System.currentTimeMillis()/(10*1000));
			if (cmd.equals("CURTIME20")) return(""+System.currentTimeMillis()/(20*1000));
			if (cmd.equals("HOUR")) return(""+d.getHours());
			if (cmd.equals("MIN")) return(""+d.getMinutes());
			if (cmd.equals("SEC")) return(""+d.getSeconds());
			if (cmd.equals("YEAR")) return(""+(1900+d.getYear())); // year 2000 bug! 
			if (cmd.equals("SHORTYEAR")) return(""+d.getYear());
			if (cmd.equals("MONTH")) {
				switch(whichname) {
					case INFO.Not:
						rtn=""+(d.getMonth()+1);
						break;
					case INFO.English:
						rtn=DateStrings.longmonths[d.getMonth()];
						break;
					case INFO.Dutch:
						rtn=DateStrings.Dutch_longmonths[d.getMonth()];
						break;
					default:
						rtn="Plokta";
						break;
				}
				return(rtn);
			}
			if (cmd.equals("DAY")) return(""+d.getDate());
			if (cmd.equals("WEEKDAY")) {
				switch(whichname) {
					case INFO.Not:
						rtn=""+(d.getDay()+1);
						break;
					case INFO.English:
						rtn=DateStrings.longdays[d.getDay()];
						break;
					case INFO.Dutch:
						rtn=DateStrings.Dutch_longdays[d.getDay()];
						break;
					default:
						rtn="Plokta";
						break;
				}
				return(rtn);
			}
			long secInYear=calcsec(d);
			int days=(int)(secInYear/(3600*24));
			if (cmd.equals("YDAY")) return(""+(days+1));
			if (cmd.equals("WEEK")) return(""+((days/7)+1)); 
			if (cmd.equals("GWEEK")) return(""+(((days+3)/7))); // +3 days
			if (cmd.equals("PREVWEEK")) {
				long tmp=days/7;
				if (tmp<1) tmp=52;
				if (tmp>52) tmp=1;
				return(""+tmp);
			}
			if (cmd.equals("PREVGWEEK")) {
				long tmp=((days+3)/7)-1; // +3 days
				if (tmp<1) tmp=52;
				if (tmp>52) tmp=1;
				return(""+tmp);
			}
			if (cmd.equals("NEXTWEEK")) {
				long tmp=(days/7)+2;
				if (tmp==53) tmp=1;
				if (tmp==54) tmp=2;
				return(""+tmp);
			}
			if (cmd.equals("NEXTGWEEK")) {
				long tmp=((days+3)/7)+1; // +3days
				if (tmp==53) tmp=1;
				if (tmp==54) tmp=2;
				return(""+tmp);
			}
			if (cmd.equals("WEEKDATE")) {
				String sday;
				int iday,iweek;
				Date ad;

				iweek=(days/7)+1;
				if (tok.hasMoreTokens()) {
					sday=tok.nextToken();
				} else {
					sday="1";
				}
				try {
					iday=Integer.parseInt(sday);
				} catch (NumberFormatException e) {
					iday=1;
				}
				iday-=2;
				if (iday<0) {
					iweek--;
					iday=6;
				}
				d=DateSupport.Date(d.getYear(),iweek,iday);
				rtn=d.getDate()+" "+DateStrings.Dutch_longmonths[d.getMonth()];
				return(rtn);
			}
			if (cmd.equals("NEXTWEEKDATE")) {
				String sday;
				int iday,iweek;
				Date ad;

				iweek=(days/7)+2;
				if (tok.hasMoreTokens()) {
					sday=tok.nextToken();
				} else {
					sday="1";
				}
				try {
					iday=Integer.parseInt(sday);
				} catch (NumberFormatException e) {
					iday=1;
				}
				iday-=2;
				if (iday<0) {
					iweek--;
					iday=6;
				}
				d=DateSupport.Date(d.getYear(),iweek,iday);
				rtn=d.getDate()+" "+DateStrings.Dutch_longmonths[d.getMonth()];
				return(rtn);
			}
			if (cmd.equals("GWEEKDATE")) {
				String sday;
				int iday,iweek;
				Date ad;

				iweek=((days+3)/7)+1;
				if (tok.hasMoreTokens()) {
					sday=tok.nextToken();
				} else {
					sday="1";
				}
				try {
					iday=Integer.parseInt(sday);
				} catch (NumberFormatException e) {
					iday=1;
				}
				iday-=2;
				if (iday<0) {
					iweek--;
					iday=6;
				}
				d=DateSupport.Date(d.getYear(),iweek,iday);
				rtn=d.getDate()+" "+DateStrings.Dutch_longmonths[d.getMonth()];
				return(rtn);
			}
			if (cmd.equals("NEXTGWEEKDATE")) {
				String sday;
				int iday,iweek;
				Date ad;

				iweek=((days+3)/7)+2;
				if (tok.hasMoreTokens()) {
					sday=tok.nextToken();
				} else {
					sday="1";
				}
				try {
					iday=Integer.parseInt(sday);
				} catch (NumberFormatException e) {
					iday=1;
				}
				iday-=2;
				if (iday<0) {
					iweek--;
					iday=6;
				}
				d=DateSupport.Date(d.getYear(),iweek,iday);
				rtn=d.getDate()+" "+DateStrings.Dutch_longmonths[d.getMonth()];
				return(rtn);
			} else if (cmd.equals("WEEKCURTIME")) {
				int ctime=(int)(DateSupport.currentTimeMillis()/1000);
				//debug(ctime);
				Date d2=new Date((long)ctime*1000);
				//debug(d2.toString());
				//debug(t2d(ctime));
				int day=d2.getDay();
				int hours=d2.getHours();
				int min=d2.getMinutes();
				int sec=d2.getSeconds();
				ctime-=((day+1)*86400);
				ctime-=(hours*3600);
				ctime-=(min*60);
				ctime-=(sec);
				return(""+ctime);
				//return(t2d(ctime));
			}
			return("Illegal date command");	
		} else {
			return(new Date(DateSupport.currentTimeMillis()).toString());
		}
	}

	long calcsec(Date d) {
		Date b = new Date ((d.getYear()),0,0);
		return((d.getTime()-b.getTime())/1000);
	}

	public String getModuleInfo() {
		return("Support routines for servscan, Daniel Ockeloen");
	}

	/**	
	 * This method is used to retrieve time related info from a relative time value.
	 * 3 types of MOD commands exist:
	 * 	$MOD-INFO-RELTIME-GET???-timeValueInMillis (where ??? is HOURS,MINUTES,SECONDS or MILLIS).
	 * 	$MOD-INFO-RELTIME-COUNTMILLIS-hourValue-minuteValue-secondValue-milliValue
	 * 	$MOD-INFO-RELTIME-GETTIME-timeValueInMillis
	 * @param tok The StringTokenizer containing the subsequent cmd argument tokens.
	 * @return A String containing cmd result.
	 */
	String doRelTime(StringTokenizer tok) {
		int timeValue = 0;
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();

			// Check commandname.
			if (cmd.equals("GETHOURS")) {
				if (tok.hasMoreTokens()) {
					try {
						timeValue = Integer.parseInt(tok.nextToken());
					} catch (NumberFormatException nfe) {
						debug("doRelTime: Invalid timeValue specified. "+nfe);
						return("INFO::doRelTime: Invalid timeValue specified timeValue="+timeValue);
					} 
				} else {
					return("INFO::doRelTime: No timeValue specified");
				}
				debug("doRelTime: GETHOURS-"+timeValue+" result= "+RelativeTime.getHours(timeValue));
				return (""+RelativeTime.getHours(timeValue));	

			} else if (cmd.equals("GETMINUTES")) {
				if (tok.hasMoreTokens()) {
					try {
						timeValue = Integer.parseInt(tok.nextToken());
					} catch (NumberFormatException nfe) {
						debug("doRelTime: Invalid timeValue specified. "+nfe);
						return("INFO::doRelTime: Invalid timeValue specified timeValue="+timeValue);
					} 
				} else {
					return("INFO::doRelTime: No timeValue specified");
				}
				debug("doRelTime: GETMINUTES-"+timeValue+" result= "+RelativeTime.getMinutes(timeValue));
				return (""+RelativeTime.getMinutes(timeValue));	

			} else if (cmd.equals("GETSECONDS")) {
				if (tok.hasMoreTokens()) {
					try {
						timeValue = Integer.parseInt(tok.nextToken());
					} catch (NumberFormatException nfe) {
						debug("doRelTime: Invalid timeValue specified. "+nfe);
						return("INFO::doRelTime: Invalid timeValue specified timeValue="+timeValue);
					} 
				} else {
					return("INFO::doRelTime: No timeValue specified");
				}
				debug("doRelTime: GETSECONDS-"+timeValue+" result= "+RelativeTime.getSeconds(timeValue));
				return (""+RelativeTime.getSeconds(timeValue));	

			} else if (cmd.equals("GETMILLIS")) {
				if (tok.hasMoreTokens()) {
					try {
						timeValue = Integer.parseInt(tok.nextToken());
					} catch (NumberFormatException nfe) {
						debug("doRelTime: Invalid timeValue specified. "+nfe);
						return("INFO::doRelTime: Invalid timeValue specified timeValue="+timeValue);
					} 
				} else {
					return("INFO::doRelTime: No timeValue specified");
				}
				debug("doRelTime: GETMILLIS-"+timeValue+" result= "+RelativeTime.getMillis(timeValue));
				return (""+RelativeTime.getMillis(timeValue));	

			} else if (cmd.equals("COUNTMILLIS")) {
				if (tok.hasMoreTokens() && (tok.countTokens()==4)) {
					String value = tok.nextToken()+":"+tok.nextToken()+":"+tok.nextToken()+"."+tok.nextToken(); 
					timeValue = RelativeTime.convertTimeToInt(value);
					debug("doRelTime -> COUNTMILLIS result= " +timeValue);
		            return (""+timeValue);
				} else {
			       	String error = "doRelTime: Error, Amount of timeValues is != 4 (h,m,s,ms)";
            		debug(error);
		            return error;
        		} 
			} else if (cmd.equals("GETTIME")) {

				if (tok.hasMoreTokens()) {
					try {
						timeValue = Integer.parseInt(tok.nextToken());
					} catch (NumberFormatException nfe) {
						debug("doRelTime: Invalid timeValue specified. "+nfe);
						return("INFO::doRelTime: Invalid timeValue specified timeValue="+timeValue);
					} 
				} else {
					return("INFO::doRelTime: No timeValue specified");
				}
				debug("doRelTime: GETTIME-"+timeValue+" result= "+RelativeTime.convertIntToTime(timeValue));
				return (""+RelativeTime.convertIntToTime(timeValue));	

			} else {
				return("INFO::doRelTime: Undefined command specified, command= "+cmd);
			}
		} else {
			return("INFO::doRelTime: No command specified");
		}
		// return("INFO::doRelTime: Not implemented yet.");
	}

	/**
	 * This method is used to retrieve the amount of FREE MEMORY in either the JVM or the SYSTEM.
	 * @param tok The StringTokenizer containing the subsequent cmd argument tokens.
	 * @return A String containing the available memory.
	 */
	String doMemory(StringTokenizer tok) {
		if (tok.hasMoreTokens()) {
			String cmd=tok.nextToken();
			Runtime rt = Runtime.getRuntime();
			int whichMem = 0;
			float memDiv = 0.0f;

			// Check commandname.
			if (cmd.equals("GETJVM")) {
				whichMem = 0;
			} else if (cmd.equals("GETSYS")) {
				whichMem = 1;
			} else {
				debug("doMemory: Undefined command requested -> "+cmd);
				return("INFO::doMemory: Undefined command requested -> "+cmd);
			}

			if (tok.hasMoreTokens()) {
				cmd = tok.nextToken();
				if (cmd.equals("MB"))      memDiv = 1048576.0f;
				else if (cmd.equals("KB")) memDiv = 1024.0f;
			}
			if (memDiv < 1.0f) {
				if (whichMem == 0)
					return (""+rt.totalMemory());
				else
					return (""+rt.freeMemory());
			} else {
				if (whichMem == 0)
					return (""+(rt.totalMemory()/memDiv));
				else
					return (""+(rt.freeMemory()/memDiv));
			}
		} else {
			debug("doMemory: No command specified");
			return("INFO::doMemory: No command specified");
		}
	}


	Vector doScanDate(scanpage sp,StringTagger tagger) {
		String temp = sp.req.getHeader("Pragma");
		if (temp!=null && temp.indexOf("no-cache")!=-1) {
			DirCache=new Hashtable();
		}

		Vector results=new Vector();

		// get base
		String base=tagger.Value("BASE");
		if (base==null) return(results);
   		base = Strip.DoubleQuote(base,Strip.BOTH);
	
		// find the start
		Date start=null;
		String val=tagger.Value("START");
		if (val!=null) {
    		val = Strip.DoubleQuote(val,Strip.BOTH);
			start=DateSupport.parsedbmdate(val);
		}

		// find the end
		Date end=null;
		val=tagger.Value("END");
		if (val!=null) {
    		val = Strip.DoubleQuote(val,Strip.BOTH);
			end=DateSupport.parsedbmdate(val);
		}
		

		// start included
		boolean startincluded=true;
		val=tagger.Value("STARTINCLUDED");
		if (val!=null) {
    		val = Strip.DoubleQuote(val,Strip.BOTH);
			if (val.equals("FALSE")) {
				startincluded=false;
			}
		}


		// end included
		boolean endincluded=true;
		val=tagger.Value("ENDINCLUDED");
		if (val!=null) {
    		val = Strip.DoubleQuote(val,Strip.BOTH);
			if (val.equals("FALSE")) {
				endincluded=false;
			}
		}

		// revert list
		boolean revert=false;
		val=tagger.Value("REVERS");
		if (val!=null) {
    		val = Strip.DoubleQuote(val,Strip.BOTH);
			if (val.equals("TRUE")) {
				revert=true;
			}
		}

		// scan the disk
		File scanfile = new File(documentroot+base);
		//debug(documentroot+base);
		SortedVector fullres=(SortedVector)DirCache.get(documentroot+base);
		if (fullres==null) {
			fullres=getDirTimes(scanfile);
			DirCache.put(documentroot+base,fullres);
			//debug(DirCache.toString());
		}

		if (start!=null) {
			long val2=start.getTime();
			if (!startincluded) val2++; // shift start to exclude start
			long val3;
			if (revert) fullres=revertVector(fullres);
			if (end!=null) {
				long val4=end.getTime();
				if (!endincluded) val4--; // shift end to exclude start
				Date rd;
				for (Enumeration e=fullres.elements();e.hasMoreElements();) {
					val3=Long.parseLong((String)e.nextElement());	
					if (val3>=val2 && val3<=val4) {
						rd=new Date(val3);
						results.addElement(DateSupport.makedbmdate(rd));
						results.addElement(DateStrings.Dutch_longmonths[rd.getMonth()]);
						results.addElement(""+rd.getDate());
						results.addElement(DateStrings.Dutch_longdays[rd.getDay()]);
					}
				}
			} else {
				Date rd;
				for (Enumeration e=fullres.elements();e.hasMoreElements();) {
					val3=Long.parseLong((String)e.nextElement());	
					if (val3>=val2) {
						rd=new Date(val3);
						results.addElement(DateSupport.makedbmdate(rd));
						results.addElement(DateStrings.Dutch_longmonths[rd.getMonth()]);
						results.addElement(""+rd.getDate());
						results.addElement(DateStrings.Dutch_longdays[rd.getDay()]);
					}
				}
			}
		}
		tagger.setValue("ITEMS","4");
		return(results);
	}

	SortedVector revertVector(SortedVector src) {
		SortedVector dst=new SortedVector();
		for (int i=0;i<src.size();i++) {
			dst.insertElementAt(src.elementAt(i),0);
		}
		return(dst);
	}

	SortedVector getDirTimes(File scanfile) {
		SortedVector results;
		// scan the disk
		results=new SortedVector();
		File theFile;
		Date d;
		String theFileName;
		String files[] = scanfile.list();		
		if (files==null) return(new SortedVector());
		for (int i=0;i<files.length;i++) {
			theFileName=files[i];
			theFile = new File(scanfile,theFileName);
			if (theFile.isDirectory() && theFileName.length()==10) {
				d=DateSupport.parsedbmdate(theFileName);
				//debug(theFileName+" "+d.getTime()+" "+d.toString()+" "+d.toGMTString()+" "+DateSupport.makedbmdate(d));
				results.addSorted(""+d.getTime());
			}
		}
		return(results);
	}

	private String t2d(int time) {
		return(DateSupport.getTimeSec(time)+" "+DateSupport.getMonthDay(time)+" "+DateStrings.Dutch_months[DateSupport.getMonthInt(time)]+" "+DateSupport.getYear(time));
	}

	public String doExists(scanpage sp,StringTokenizer tok) {
		String type=tok.nextToken();
		String path=tok.nextToken();
		boolean rtn=false;

		/* fully quallify path */
		if (path.charAt(0)=='/') {
			String droot = documentroot;

			if(documentroot.endsWith("/"))
			{
				droot = documentroot.substring(0, documentroot.length()-1);
			}
			path=droot+path;
			// debug("INFO -> doExists full path : "+path);
		} else {
			String r=sp.req_line;
			int i=r.lastIndexOf('/');
			path=documentroot+r.substring(0,i+1)+path;
			// debug("INFO -> doExists req path : "+path);
		}
		/* check */
		if (type.equals("PATH")) {
			rtn=pathExists(path);
		} else if (type.equals("FILE")) {
			rtn=fileExists(path);
		} else if (type.equals("DIR")) {
			rtn=dirExists(path);
		}
		if (rtn) return("YES");
		else return("NO");
	}

	private boolean pathExists(String path) {
		File f=new File(path);
		if (f.exists()) {
			return(true);
		} else {
			return(false);
		}
	}

	private boolean dirExists(String path) {
		File f=new File(path);
		if (f.exists() && f.isDirectory()) {
			return(true);
		} else {
			return(false);
		}
	}

	private boolean fileExists(String path) {
		File f=new File(path);
		if (f.exists() && f.isFile()) {
			return(true);
		} else {
			return(false);
		}
	}

    private void debug( String msg )
    {
        System.out.println( classname +":"+msg );
    }
}
