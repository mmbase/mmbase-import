<?xml version="1.0" encoding="UTF-8"?>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" escaper="none">
<mm:cloud jspvar="cloud">
<mm:import externid="node" required="true"/>
<mm:import externid="parentnode"/>
<%@include file="/shared/setImports.jsp" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<title>News content</title>
	<link rel="stylesheet" type="text/css" href="<mm:treefile page="/portalpages/css/base.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>
  
  <mm:node number="$node">
  <table>
	<tr>
	  <td>
	    <h1 style="color:#B85602;"><mm:field name="title" /></h1>
	  </td>
	</tr>
	<tr>
	  <td>
	    <mm:field name="abstract" />
	  </td>
	</tr>
  </table>
  <mm:field name="impos">
    <mm:compare value="1">
      <mm:field name="body" />	
      <table>
        <tr>
          <td>
            <mm:relatednodes type="images">
              <h3><mm:field name="title" /></h3>
              <img src="<mm:image />" width="200" border="0" /><br/>
              <mm:field name="description" /> 
            </mm:relatednodes>
          </td>
        </tr>
      </table>
    </mm:compare>
  </mm:field>
  <mm:field name="impos">
    <mm:compare value="0">
      <table>
        <tr>
         <mm:relatednodes type="images">
           <h3><mm:field name="title" /></h3>
           <img src="<mm:image />" width="200" border="0" />
           <mm:field name="description" /> 
         </mm:relatednodes>
        </tr>
        <tr>
 	      <td>
 	        <mm:field name="body" />
 	      </td>   
 	    </tr>
      </table>	  
    </mm:compare>
  </mm:field>
  <mm:field name="impos">
    <mm:compare value="2">
      <table>
        <tr>	
  	      <td>   
            <mm:relatednodes type="images">
              <table>
                <tr> <h3><mm:field name="title"/></h3></tr>
                <tr align="right"> <mm:field name="description" />  </tr>
                <tr> <td><img src="<mm:image />" width="200" border="0"/><br/></td></tr>
              </table>
            </mm:relatednodes>
          </td>      
          <td>
            <mm:field name="body" />
          </td>
        </tr>
      </table>
    </mm:compare>
  </mm:field>
  </mm:node>
  
	<mm:node number="$parentnode" notfound="skipbody">
		<mm:treeinclude page="/education/paragraph/paragraph_anonymous.jsp" objectlist="$includePath" referids="$referids">
			<mm:param name="node_id"><mm:write referid="parentnode"/></mm:param>
			<mm:param name="path_segment">../</mm:param>
		</mm:treeinclude>
	</mm:node>   
  
</body>
</html>

</mm:cloud>
</mm:content>

