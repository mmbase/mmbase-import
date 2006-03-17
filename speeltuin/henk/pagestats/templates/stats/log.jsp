<%@ page import="java.io.*,java.util.*" %>
<%
// The file that uses this include to write to the logfile should contain two java variables "username" and "sLog"
// String logDir = "D:/vwl_dev/data/vwl_dev/webapps/formas/logs/";
String logDir = application.getRealPath("logs/");

Calendar cal = Calendar.getInstance();
cal.setTime(new Date());

String date = 
   cal.get(Calendar.YEAR) 
   + "." + (cal.get(Calendar.MONTH)<10 ? "0" : "" ) +  cal.get(Calendar.MONTH) 
   + "." + (cal.get(Calendar.DAY_OF_MONTH)<10 ? "0" : "" ) + cal.get(Calendar.DAY_OF_MONTH)
   + "-" + (cal.get(Calendar.HOUR_OF_DAY)<10 ? "0" : "" ) + cal.get(Calendar.HOUR_OF_DAY) 
   + "." + (cal.get(Calendar.MINUTE)<10 ? "0" : "" ) + cal.get(Calendar.MINUTE) 
   + "." + (cal.get(Calendar.SECOND)<10 ? "0" : "" ) + cal.get(Calendar.SECOND);
BufferedWriter logFile  = (BufferedWriter) application.getAttribute("logFile"); 
if(logFile==null) {
   logFile = new BufferedWriter(new FileWriter(logDir + "formas" + date + ".txt"));
   application.setAttribute("logFile",logFile);
}

logFile.write(	  "date=" + date + ";"  
				   + "user=" + username + ";"
				   + "ip=" + request.getRemoteHost() + ";"
				   + "sessionid=" + session.getId() + ";"
				   + sLog
				 );
logFile.newLine();
logFile.flush();
%>
