<%-- uses the global variable timeStamp --%>

<%
if (!timeStamp.equals("now")){
		long td = Integer.parseInt(timeStamp);
		td = 1000 * td;
		dd = new Date(td); 
}
cal.setTime(dd);
thisYear = cal.get(Calendar.YEAR);
thisMonth = cal.get(Calendar.MONTH);
thisDay = cal.get(Calendar.DAY_OF_MONTH);
%>
