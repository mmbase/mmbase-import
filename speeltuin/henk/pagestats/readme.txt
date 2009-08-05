PageStats

Author:  Henk Hangyi (http://www.mmatch.nl - MMBase consultancy and implementation)
Date:    25-04-2006
License: MPL 1.1/GPL 2.0/LGPL 2.1
This software was originally developed for the Ruud de Moor Centrum of the Open University.

This tool generates detailed logs for a jsp-application. It does not depend
on MMBase, except for the logging used. The ideas is that the jsp-pages write logfiles
of the following format:

date=2006.03.24-18.29.48;user=anonymous;ip=127.0.0.1;sessionid=692795FAE9351E8D95B2EF914E81DDCB;page=Portal;schooltype=HAVO

The tags date and user are mandatory. The tags ip and sessionid are optional.
Other tags can be defined in pagestats.xml

To install pagestats carry out the following steps:

1. copy config/modules/pagestats.xml to your webapp/WEB-INF/config/modules.
   Don't forget to set the:
   - logdir
   - tags you use
   - sessionstart
2. copy the directory templates/stats to your webapp
   Set the variable configFile in getconfig.jsp to the location of the pagestats.xml file
3. compile the sources and add them to your webapp/WEB-INF/classes
4. add the include stats/log.jsp and the variables username and sLog to the pages
   you want to incorporate in your log analyses. E.g.
   <%
   String username = "anonymous";
   String sLog = "page=Portal";
   sLog += ";schooltype=" + educationTypeId;
   %>
   <%@include file="/stats/log.jsp" %>
5. Browse your application
6. View the stats on http://localhost/your_webapp/stats
      
