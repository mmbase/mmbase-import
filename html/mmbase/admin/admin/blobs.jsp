<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@page import="org.mmbase.storage.implementation.database.*" %>
<%@include file="../settings.jsp" %>
<mm:cloud method="$method" authenticate="$authenticate" rank="administrator" jspvar="cloud">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Converting blobs</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link rel="stylesheet" type="text/css" href="../css/mmbase.css" />
</head>
<body class="basic" >
<!-- <%= cloud.getUser().getIdentifier()%>/<%=  cloud.getUser().getRank()%> -->

<mm:import externid="convertnow" />

<mm:notpresent referid="convertnow">
  <form>
    <input type="submit" name="convertnow" value="Convert"/> 
    <p>
      Convert all the blobs (fields with type 'BYTE' of MMBase) to the right storage location. See release-notes of 1.7.0.
      Before doing this, make sure that the 'old' blobdata dir is now WEB-INF/data. If you are
      converting from blobs-in-database, then only make sure this directory exists and is writeable.
    </p>      
    <p>
      This might take a while, inspect the mmbase log while doing it please.
    </p>
  </form>
</mm:notpresent>

<mm:present referid="convertnow">
  <% 
    try {
    DatabaseStorageManager storage = (DatabaseStorageManager) org.mmbase.module.core.MMBase.getMMBase().getStorageManager();
    int result = storage.convertLegacyBinaryFiles();
    if (result < 0) {
      out.print("Your database is not configured for blobs on disk");
    } else {
      out.print("Converted " + result + " fields");
    } 
    } catch (Throwable e) {
      out.println("<pre>" + e.getClass().getName() + e.getMessage() + org.mmbase.util.logging.Logging.stackTrace(e) + "</pre>");
    }
  %>
</mm:present>

</body>
</html>
</mm:cloud>