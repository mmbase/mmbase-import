<%@page language="java" contentType="text/html;charset=utf-8"%>
<% response.setContentType("text/html; charset=UTF-8"); %>
<%@include file="/taglibs.jsp" %>
<%@include file="../../includes/request_parameters.jsp" %>
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
   
   for(int i= 0; i< style1.length;i++) {
      srcFileReader = new BufferedReader(new FileReader(sourceFile));
      destFileWriter  = new BufferedWriter(new FileWriter(root + style1[i] + ".css"));
   
      String nextLine = srcFileReader.readLine();
      while(nextLine!=null) {
         nextLine = nextLine.replaceAll("<style1>",style1[i]);
         nextLine = nextLine.replaceAll("<color1>",color1[i]);
         nextLine = nextLine.replaceAll("<color2>",color2[i]);   
         nextLine = nextLine.replaceAll("<color3>",color3[i]);   
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
