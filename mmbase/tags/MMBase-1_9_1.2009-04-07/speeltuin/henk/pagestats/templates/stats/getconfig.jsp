<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="java.io.*" %>
<%
String configFile = "C:/data/competentie/webapps/competentie/WEB-INF/config/modules/pagestats.xml";
String logdirPath = "";
Config conf = null;
boolean configIsOk = false;
if(!(new File(configFile)).exists()) {
   %>The config file <%= configFile %> does not exist.<br/><%;
} else {

   ConfigBuilder cbuilder = new ConfigBuilder(configFile);
   conf = cbuilder.getConfig();
   logdirPath = conf.getLogDir();
   if(!(new File(logdirPath)).exists()) {
      %>The log directory <%= logdirPath %> does not exist.<br/><%
   } else {
      configIsOk = true;
   }
}
%>