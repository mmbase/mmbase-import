<% // *** java variables needed for image handling 
boolean validLink = true;
String linkTXT = "";
String altTXT = "";
String imgFormat = "";
String readmoreURL ="";
String readmoreTarget ="";

String subDir = request.getRequestURI();
boolean isSubDir = (subDir.indexOf("/actie/")>-1);
int slashPos = subDir.indexOf("/",1);
if(slashPos>-1) {
   subDir = subDir.substring(0,slashPos+1);
} else {
   subDir = "";
}
%>