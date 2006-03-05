<%
Date now = new Date();	               // time in milliseconds
long nowSec = (now.getTime() / 1000);  // time in MMBase time
nowSec = (nowSec/(60*15))*(60*15);     // help the query cache by rounding to quarter of an hour
now = new Date(nowSec * 1000);
%>