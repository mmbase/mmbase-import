<%
Date now = new Date();	                              // time in milliseconds
long nowSec = (now.getTime() / 1000);                 // time in MMBase time
int quarterOfAnHour = 60*15;
nowSec = (nowSec/quarterOfAnHour)*quarterOfAnHour;    // help the query cache by rounding to quarter of an hour
now = new Date(nowSec * 1000);
%>