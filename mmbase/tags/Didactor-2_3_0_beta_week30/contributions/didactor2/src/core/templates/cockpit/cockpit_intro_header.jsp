<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:import externid="extraheader" />
<mm:import externid="extrabody" />
<mm:cloud method="asis">	
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head><%-- UGLY: head is unbalanced in this file. --%>
      <link rel="stylesheet" type="text/css" href="${mm:treefile('/css/base.css', pageContext, includePath)}" />
      <mm:write referid="extraheader" escape="none" />
    </head>
    <body class="componentbody" <mm:write referid="extrabody" escape="none" />> <%-- UGLY: body is unbalanced in this file. --%>
    <div>
      <mm:treeinclude page="/cockpit/applicationbarcockpit.jsp" objectlist="$includePath" referids="$referids"/>
      <mm:treeinclude page="/cockpit/providerbar.jsp"          objectlist="$includePath"  referids="$referids" />
      <mm:treeinclude page="/cockpit/educationbarcockpit.jsp"  objectlist="$includePath" referids="$referids" /> 	
      
    </div>
  </mm:cloud>
