<%@page language="java" contentType="text/html;charset=utf-8"%>
<% response.setContentType("text/html; charset=UTF-8"); %>
<%@include file="/taglibs.jsp" %>
<html>
<head>
</head>
<body style="background-color:#FFFFFF;">
<%= new Date() %>
<table>
<% for(int i=0; i< NatMMConfig.style1.length; i++ ) { %>
      <tr>
         <td><%= NatMMConfig.style1[i] %></td>
         <td style="background-color:<%= NatMMConfig.color1[i] %>">&nbsp;&nbsp;&nbsp;</td><td><%= NatMMConfig.color1[i] %></td>
         <td style="background-color:<%= NatMMConfig.color2[i] %>">&nbsp;&nbsp;&nbsp;</td><td><%= NatMMConfig.color2[i] %></td>
         <td style="background-color:<%= NatMMConfig.color3[i] %>">&nbsp;&nbsp;&nbsp;</td><td><%= NatMMConfig.color3[i] %></td>
      </tr>
<% } %>
</table>
<%@page import="java.util.*,java.io.*,java.text.*"%>
<%
String root =  application.getRealPath("natmm/hoofdsite/themas/");
if(root==null) {
  application.getRealPath("hoofdsite/themas/");
}
if(root!=null) {
   root += "/";
   String sourceFile = root + "source.css";
   BufferedReader srcFileReader = null;
   BufferedWriter destFileWriter = null;
   
   for(int i= 0; i< NatMMConfig.style1.length;i++) {
      srcFileReader = new BufferedReader(new FileReader(sourceFile));
      destFileWriter  = new BufferedWriter(new FileWriter(root + NatMMConfig.style1[i] + ".css"));
   
      String nextLine = srcFileReader.readLine();
      while(nextLine!=null) {
         nextLine = nextLine.replaceAll("<style1>",NatMMConfig.style1[i]);
         nextLine = nextLine.replaceAll("<color1>",NatMMConfig.color1[i]);
         nextLine = nextLine.replaceAll("<color2>",NatMMConfig.color2[i]);   
         nextLine = nextLine.replaceAll("<color3>",NatMMConfig.color3[i]);   
         destFileWriter.write(nextLine + "\n");
         nextLine = srcFileReader.readLine();
      }
      destFileWriter.close();
      srcFileReader.close();
   }
   boolean createFixedFontCSS = false;
   if(createFixedFontCSS) {
   
      sourceFile = root + "main.css";
      srcFileReader = new BufferedReader(new FileReader(sourceFile));
      destFileWriter  = new BufferedWriter(new FileWriter(root + "ie3_main.css"));
   
      String nextLine = srcFileReader.readLine();
      while(nextLine!=null) {
         nextLine = nextLine.replaceAll("1.0em","12px");
         nextLine = nextLine.replaceAll("0.9em","12px");
         nextLine = nextLine.replaceAll("0.75em","12px");
         nextLine = nextLine.replaceAll("0.7em","11px");
         nextLine = nextLine.replaceAll("font-size: 100%;","font-size: 12px;");   
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
