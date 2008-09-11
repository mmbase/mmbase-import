<%@page language="java" contentType="text/html;charset=utf-8"%>
<% response.setContentType("text/html; charset=UTF-8"); %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
</head>
<body style="background-color:#FFFFFF;">
<%= new Date() %>
<table>
<% for(int i=0; i< NMIntraConfig.style1.length; i++ ) { %>
      <tr>
         <td><%= NMIntraConfig.style1[i] %></td>
         <td style="background-color:<%= NMIntraConfig.color1[i] %>">&nbsp;&nbsp;&nbsp;</td><td><%= NMIntraConfig.color1[i] %></td>
         <td style="background-color:<%= NMIntraConfig.color2[i] %>">&nbsp;&nbsp;&nbsp;</td><td><%= NMIntraConfig.color2[i] %></td>
         <td style="background-color:<%= NMIntraConfig.color3[i] %>">&nbsp;&nbsp;&nbsp;</td><td><%= NMIntraConfig.color3[i] %></td>
         <td style="background-color:<%= NMIntraConfig.color4[i] %>">&nbsp;&nbsp;&nbsp;</td><td><%= NMIntraConfig.color4[i] %></td>
      </tr>
<% } %>
</table>
<%@page import="java.util.*,java.io.*,java.text.*"%>
<%
String root =  application.getRealPath("nmintra/css/");
if(root==null) {
  application.getRealPath("css/");
}
if(root!=null) {
   root += "/";
   String sourceFile = root + "source.css";
   BufferedReader srcFileReader = null;
   BufferedWriter destFileWriter = null;
   
   for(int i= 0; i< NMIntraConfig.style1.length;i++) {
      srcFileReader = new BufferedReader(new FileReader(sourceFile));
      destFileWriter  = new BufferedWriter(new FileWriter(root + NMIntraConfig.style1[i] + ".css"));
   
      String nextLine = srcFileReader.readLine();
      while(nextLine!=null) {
         nextLine = nextLine.replaceAll("<style1>",NMIntraConfig.style1[i]);
         nextLine = nextLine.replaceAll("<color1>",NMIntraConfig.color1[i]);
         nextLine = nextLine.replaceAll("<color2>",NMIntraConfig.color2[i]);   
         nextLine = nextLine.replaceAll("<color3>",NMIntraConfig.color3[i]);   
         nextLine = nextLine.replaceAll("<color4>",NMIntraConfig.color4[i]);   
         destFileWriter.write(nextLine + "\n");
         nextLine = srcFileReader.readLine();
      }
      destFileWriter.close();
      srcFileReader.close();
   }
   %>CSS files have been created.<%
} else {
   %>The directory hoofdsite themas could not be found.<%
}
%>
</body>
</html>
