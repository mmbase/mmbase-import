<%@ page import="java.io.*,java.util.*" %>
<%@include file="getconfig.jsp" %>
<%
// The file that uses this include to write to the logfile should contain two java variables "username" and "sLog"

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
   logFile = new BufferedWriter(new FileWriter(logdirPath + conf.getFileNamePrefix() + date + ".txt"));
   application.setAttribute("logFile",logFile);
}

logFile.write(	  "date=" + date + ";"  
				   + "user=" + username + ";"
				   + "ip=" + request.getRemoteHost() + ";"
				   + "sessionid=" + request.getSession().getId() + ";"
				   + sLog
				 );
logFile.newLine();
logFile.flush();
%>
