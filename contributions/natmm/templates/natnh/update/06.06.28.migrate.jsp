<%@include file="/taglibs.jsp" %>
<%@page import="nl.leocms.evenementen.EventNotifier" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
	<mm:list path="rubriek,posrel,images">
		<mm:node element="rubriek" id="r" />
		<mm:node element="images" id="i" />
		Creating relation from <mm:field name="rubriek.naam" /> to <mm:field name="images.title" /><br/>
		<mm:createrelation source="r" destination="i" role="contentrel" />
		<mm:deletenode element="posrel" />
	</mm:list>
</mm:log>
</mm:cloud>

