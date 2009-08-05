<%@page import="net.sf.mmapps.modules.config.*,net.sf.mmapps.modules.config.dumper.*"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud jspvar="cloud">
<% Configuration appConfig = Dumper.getConfiguration(cloud); %>
</mm:cloud>
