<%
// *** java variables needed for image handling 
boolean validLink = true;
String linkTXT = "";
String altTXT = "";
String imgFormat = "";
String readmoreURL ="";
String readmoreTarget ="";
// *** list the subdirectories with additional templates
boolean isSubDir = (request.getRequestURI()).indexOf("/actie/")>-1;
%>