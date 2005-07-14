<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:import externid="extraheader" />
<mm:import externid="extrabody" />
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud" method="asis">
<%@include file="/shared/setImports.jsp" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <mm:write referid="extraheader" escape="none" />
<%--    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/menubars.css" objectlist="$includePath" referids="$referids" />" />--%>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
<%--    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/calendar.css" objectlist="$includePath" referids="$referids" />" />--%>
  </head>
  <body <mm:write referid="extrabody" escape="none" />>
  	<div>
      <mm:treeinclude page="/cockpit/applicationbarcockpit.jsp" objectlist="$includePath" referids="$referids"/>
      <mm:treeinclude page="/cockpit/providerbar.jsp" objectlist="$includePath" referids="$referids" />
      <mm:treeinclude page="/cockpit/educationbarcockpit.jsp" objectlist="$includePath" referids="$referids" />
	</div>
</mm:cloud>
</mm:content>
