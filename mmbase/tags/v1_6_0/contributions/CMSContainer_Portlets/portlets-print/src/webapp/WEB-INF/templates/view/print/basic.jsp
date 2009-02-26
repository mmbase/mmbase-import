<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<h1><img src="<cmsc:staticurl page='/gfx/logo.gif'/>" alt="Logo" /></h1>
	<mm:cloud>		
		<mm:node number="${param.articleId}" notfound="skip"> 
	       <p class="inleiding"><mm:field name="intro"><mm:isnotempty><mm:write escape="none" /></mm:isnotempty></mm:field></p>      
	       <p>
	       <mm:relatednodes type="images"  role="imagerel" orderby="imagerel.order" searchdir="destination">
	           <mm:first>
	              	<img src="<mm:image/>" alt="<mm:field name="title" />" title="<mm:field name="title" />"/>
	           	</mm:first>
	       	</mm:relatednodes> 
	       	<mm:field name="body"><mm:isnotempty><mm:write escape="none" /></mm:isnotempty></mm:field>
	       	</p>	       	      
		</mm:node>
	</mm:cloud>
<p class="sub"><fmt:message key="view.copyright"/> <a href="javascript: window.print();"><fmt:message key="view.site"/></a></p>  
<p id="printtekst"><fmt:message key="view.printmessage"/></p>
<script type="text/javascript">
	document.getElementById('printtekst').style.display = 'none';
</script>