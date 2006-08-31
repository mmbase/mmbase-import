<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN"
        "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl">
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8" />
    <title>Packages</title>
</head>
<body>

<mm:import externid="pack" /> 

<mm:notpresent referid="pack">
  <mm:node number="MMBasePackageCollection">
  <table cellpadding="0" cellspacing="0" class="list" style="margin-top: 2px; font-size: 1.2em;" width="95%">
  <tr>
	  <th>Name</th>
	  <th>Maintainer</th>
	  <th>Type</th>
	  <th>Version</th>
	  <th>&nbsp;</th>
  </tr>
  <mm:relatednodes type="packages" role="posrel" 
	orderby="posrel.pos">
	<mm:import id="pack" reset="true"><mm:field name="number" /></mm:import>
	<tr>
	<td>
	  <a href="<mm:url page="index.jsp" referids="pack,page?,portal?" />"><mm:field name="name" /></a>
	</td>
  
	<td>    
	  <mm:related path="rolerel,organisation" 
	    fields="rolerel.role,organisation.name"
	  	constraints="rolerel.role='maintainer'">
		<mm:first inverse="true">, </mm:first>
		<mm:field name="organisation.name" />
		<mm:import id="is_shown" reset="true" />  
	  </mm:related> 
	  <mm:related path="rolerel,persons"
    	fields="rolerel.role,persons.firstname,persons.suffix,persons.lastname,persons.email"
	    constraints="rolerel.role='maintainer'">
		<mm:present referid="is_shown">, </mm:present>
		<mm:remove referid="is_shown" />
		<mm:first inverse="true">, </mm:first> 
		<mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" />
	  </mm:related>       
	  <mm:remove referid="is_shown" />
	</td>
	
	<td>    
	  <mm:related path="related,packagetypes">
		<mm:field name="packagetypes.name" />
	  </mm:related>
	</td>
  
	<td>
	  <mm:field name="versionnumber"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
	  <mm:field name="versionnumber"><mm:isempty><mm:field name="creationdate"><mm:time format="dd-MM-yyyy" /></mm:field></mm:isempty></mm:field>
	</td>

	<td>
	  <a href="<mm:url page="index.jsp" referids="pack,page?,portal?" />"><img src="/media/arrow-right.gif" alt="-&gt;" width="15" height="20" style="border: 0;" /></a>
	</td>
  
	</tr>
  </mm:relatednodes>
  </table>
  </mm:node>
<mm:remove referid="pack" />
</mm:notpresent>

<mm:present referid="pack">
  <mm:include page="package.jsp" referids="pack" /> 
</mm:present>

</body>
</html>
</mm:cloud>
