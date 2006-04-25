<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:log jspvar="log">
<%@include file="getconfig.jsp" %>
<%
if(configIsOk){
   
   ModelBuilder modelBuilder = (ModelBuilder)session.getValue("MODEL");
   boolean isSomeFiles = false;
   if (modelBuilder==null) {         
      conf.logConfig();
      modelBuilder = new ModelBuilder(conf);
      FileParser fileParser = new FileParser(modelBuilder, conf);

      String filenamePrefix = conf.getFileNamePrefix();
      String fileExtension = conf.getFileExt();
   
      File logdir = new File( logdirPath );
      String [] logfiles = logdir.list();
      for(int i=0; i<logfiles.length;i++) {
         String fileName = logfiles[i];
         if( ( (filenamePrefix==null) || (fileName.indexOf(filenamePrefix)==0) ) &&
             ( (fileExtension==null) || (fileName.lastIndexOf(fileExtension)==(fileName.length()-fileExtension.length()) ) )) {
            File file = new File(logdirPath, fileName);
            if(!file.exists()) {
               %>The file <%= logdirPath + fileName %> does not exist.<br/><%;   
            } else {
               fileParser.parse(file);
               if (!isSomeFiles) isSomeFiles = true;
            }
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
         + modelBuilder.getConfig().getLogDir()
         + " or the files found could not be parsed. Check the mmbase log for misconfiguration.");
   }
}
%>
</mm:log>