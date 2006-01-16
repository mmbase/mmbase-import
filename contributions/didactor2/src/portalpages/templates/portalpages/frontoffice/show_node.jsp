<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<mm:content postprocessor="reducespace">
<mm:cloud jspvar="cloud">
<mm:import externid="node" required="true"/>
<%@include file="/shared/setImports.jsp" %>
<link rel="stylesheet" type="text/css" href="<mm:treefile page="/portalpages/css/base.css" objectlist="$includePath" referids="$referids" />" />
  
  <mm:node number="$node" notfound="skipbody">
    <mm:treeinclude page="/education/paragraph/paragraph_anonymous.jsp" objectlist="$includePath" referids="$referids">
      <mm:param name="node_id"><mm:write referid="node"/></mm:param>
      <mm:param name="path_segment">../</mm:param>
    </mm:treeinclude>
  </mm:node>   
  
  <mm:node number="$node">
	<table>
	  <tr>
	    <td align="left" style="padding-left: 5px;">
	      <h1 style="color:#B85602;"><mm:field name="name"/></h1>
	    </td>
	  </tr>
	  <tr>
	    <td align="left" style="padding-top: 20px;padding-left: -10px;">
	      <table border="0">
	        <tr>
		      <td width="660" valign="top" align="left">
                <mm:field name="body" escape="none"/> 
			  </td>
		    </tr>
	      </table>
	    </td>
	  </tr>
    </table>   
  </mm:node>
</mm:cloud>
</mm:content>
