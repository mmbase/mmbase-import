<%@ include file="inc/top.jsp" %>
<mm:cloud>
<html>
<head>
	<title>edittag - example 1</title>
<%@ include file="inc/head.jsp" %>
</head>
<body>
<%@ include file="inc/nav.jsp" %>
<h4>eerste voorbeeld met edittag</h4>
<p>Een artikel met gerelateerde plaatjes en links.</p>

<mm:edit editor="yammeditor.jsp" icon="/mmbase/edit/my_editors/img/mmbase-edit.gif">
	<mm:node number="artikel">
	  <h2>[<em>number:</em> <mm:field name="number" id="nr" />] <em>title:</em> <mm:field name="title" /></h2>
	  <%-- <mm:field name="subtitle" write="false" /> --%><%-- subtitle is not vissible but we want it in the editor --%>
	  <div class="intro"><em>intro:</em> <mm:field name="intro" /></div>
	  <mm:list nodes="$nr" 
	    path="news,posrel,images"
	    fields="posrel.pos"
	    orderby="posrel.pos">
		<mm:node element="images">
		  <em><mm:field name="gui()" /><br />
		  [<mm:field name="number" />] <mm:field name="title" /></em><br />
		  <mm:maywrite>Je bent ingelogd en mag editen!</mm:maywrite>
		</mm:node>
	  </mm:list>
	  <div><em>body:</em> <mm:field name="body" />
	  <mm:related path="posrel,urls"
	  	  fields="urls.url,posrel.pos"
	      orderby="posrel.pos">
	  	  <mm:first><br /><strong>Links:</strong></mm:first>
	  	  <em>name:</em> <a href="<mm:field name="urls.url" />"><mm:field name="urls.name" /></a> [<mm:field name="urls.number" />]
	  	  <mm:last inverse="true">,</mm:last>
	  </mm:related>
	  </div>
	</mm:node>
</mm:edit>

</body>
</html>
</mm:cloud>
