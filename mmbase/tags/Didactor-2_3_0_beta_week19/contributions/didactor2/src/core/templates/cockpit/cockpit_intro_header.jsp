<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="extraheader" />
<mm:import externid="extrabody" />
<mm:content postprocessor="reducespace">
  <mm:cloud method="delegate" authenticate="asis">	
    <jsp:directive.include file="/shared/setImports.jsp" />
    <html xmlns="http://www.w3.org/1999/xhtml">
      <head>
        <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
        <mm:write referid="extraheader" escape="none" />
      </head>
      <body class="componentbody" <mm:write referid="extrabody" escape="none" />> <%-- body is unbalanced in this file. --%>
      <div>
        <mm:treeinclude page="/cockpit/applicationbarcockpit.jsp" objectlist="$includePath" referids="$referids"/>
        <mm:treeinclude page="/cockpit/providerbar.jsp"          objectlist="$includePath"  referids="$referids" />
        <mm:treeinclude page="/cockpit/educationbarcockpit.jsp"  objectlist="$includePath" referids="$referids" /> 	

      </div>
    </mm:cloud>
  </mm:content>
