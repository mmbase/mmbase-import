<% 
if(depth==0) { 
 %><td style="padding-left:5px;color:white;height:18px;"></td><%
} else if(depth==1) { 
 %><td style="padding-left:5px;color:white;"><li /></td><%
} else if(depth==2) {
 %><td style="padding-left:14px;padding-right:2px;color:white;">-&nbsp;&nbsp;</td><%
 } else if(depth==3) {
 %><td style="padding-left:18px;padding-right:2px;color:white;">*&nbsp;&nbsp;</td><%
} else {
 %><td style="padding-left:22px;padding-right:2px;color:white;">.&nbsp;&nbsp;</td><%
} 
%>