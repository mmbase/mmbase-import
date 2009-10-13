<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@page session="true" language="java" contentType="text/html; charset=utf-8"  import="java.util.Stack,org.mmbase.bridge.*,org.mmbase.util.xml.UtilReader"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-2.0"  prefix="mm"
%><mm:content expires="0"><mm:cloud method="asis">
<html>
  <head>
    <title>Preview</title>
  </head>
  <body>
    <mm:import externid="node_number" required="true" />
    <mm:formatter format="xhtml">
      <mm:node referid="node_number">
        <mm:listrelations>
          <mm:relatednode />
        </mm:listrelations>
      </mm:node>
    </mm:formatter>
  </body>
</html>
</mm:cloud>
</mm:content>
