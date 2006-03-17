<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@ page import="nl.ou.rdmc.stats.process.*" %>
<%@ page import="java.io.*" %>
<mm:log jspvar="log">
<%
try {
    ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
   boolean isSomeFiles = false;
   if (modelBuilder==null) {
      
      ConfigBuilder cbuilder = new ConfigBuilder(application.getRealPath("WEB-INF/config/modules/pagestats.xml"));
      Config conf = cbuilder.getConfig();
      conf.logConfig();
        modelBuilder = new ModelBuilder(conf);
        FileParser fileParser = new FileParser(modelBuilder, conf);

       String logdirPath = conf.getLogDir();
      String filenamePrefix = conf.getFileNamePrefix();
      String fileExtension = conf.getFileExt();
        File logdir = new File( logdirPath );
        String [] logfiles = logdir.list();
      for(int i=0; i<logfiles.length;i++) {
         String fileName = logfiles[i];
       if ( ( (filenamePrefix==null) || (fileName.indexOf(filenamePrefix)==0) ) &&
            ( (fileExtension==null) || (fileName.lastIndexOf(fileExtension)==(fileName.length()-fileExtension.length()) ) )) {            
              File file = new File(logdirPath, fileName);
              fileParser.parse(file);
              if (!isSomeFiles) isSomeFiles = true;
         }
        }
      modelBuilder.isSomeFilesParsed(isSomeFiles);
        session.putValue("MODEL", modelBuilder);
   } else {
      %>
      Using model from session.<br/>
      <%
   }
   if (modelBuilder.isSomeFilesParsed()) {
      %>
      <a href="users.jsp">View User Sessions</a><br>
      <a href="subtracetypes.jsp">View Subtrace Types</a><br>
      <a href="pageviews.jsp">View Pages</a><br>
      <%
   } else {
      out.println("No log file of the form '"
         + modelBuilder.getConfig().getFileNamePrefix() 
         + "*" + (modelBuilder.getConfig().getFileExt().equals("") ?  "" : "." + modelBuilder.getConfig().getFileExt())
         + "' could be found in " 
         + modelBuilder.getConfig().getLogDir());
   }
} catch (Exception e) {
   log.error(e);
   out.println("Sorry something went wrong, please contact the system administrator to fix the problem.");
}
%>
</mm:log>