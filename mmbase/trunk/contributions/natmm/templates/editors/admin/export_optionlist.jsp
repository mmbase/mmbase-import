<%@include file="/taglibs.jsp" %>
<%@page import="java.io.*" %>
<%@page import="java.util.*" %>
<mm:cloud rank="basic user">
<%
String dataFile =  editwizard_location + "/data/config/option_lists/images_metatags.xml";

String showText = "<table>";
try {
   BufferedWriter dataFileWriter = new BufferedWriter(new FileWriter(application.getRealPath(dataFile)));
   
   dataFileWriter.write("<?xml version=\"1.0\"?>\n");
   dataFileWriter.write("<!DOCTYPE optionlist PUBLIC \"-//MMBase/DTD editwizard 1.0//EN\" \"http://www.mmbase.org/dtd/wizard-schema_1_0.dtd\">\n");
   dataFileWriter.write("<optionlist name=\"images_metatags\">\n");
   
   %><mm:listnodes type="thema" orderby="omschrijving"
   	><mm:field name="naam" jspvar="thema_name" vartype="String" write="false"><%
         dataFileWriter.write("<option id=\"" + thema_name + "\">" + thema_name + "</option>\n");
   	   showText += "<tr><td>" + thema_name + "</td></tr>";
   	%></mm:field
   ></mm:listnodes><%
   
   dataFileWriter.write("</optionlist>\n");
   dataFileWriter.close();
} catch (Exception e) {
   showText += e; 
}
showText += "</table>";

%><html>
<head>
<title>Update categorie&euml;n</title>
<link rel="stylesheet" type="text/css" href="../css/editorstyle.css">
</head>
<body>
<div align="center">
<p><div class="subHeader">De volgende categorie&euml;n zijn beschikbaar:</div></p>
<%= showText %>
</div>
</body>
</html>
</mm:cloud>
