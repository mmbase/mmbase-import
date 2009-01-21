<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<cmsc:portletmode name="edit">
 	<%@include file="/WEB-INF/templates/edit/cmsc.org/fragments/itemheader.jsp" %>
</cmsc:portletmode> 
<mm:cloud>
	<mm:import externid="elementId" required="true" from="request" />
	<mm:node number="${elementId}" notfound="skip">
	   <c:set var="title">
            <mm:field name="title"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
        </c:set>       
        <c:set var="intro">        					
            <mm:field name="intro"><mm:isnotempty><mm:write escape="none" /></mm:isnotempty></mm:field>
        </c:set>
        <c:set var="body">
            <mm:field name="body"><mm:isnotempty><mm:write escape="none" /></mm:isnotempty></mm:field>
        </c:set>          	 
        <h3>${title}</h3> 
    	<mm:relatednodescontainer type="images" role="imagerel" searchdirs="destination">							
			<mm:sortorder field="imagerel.pos"/>
	        <mm:relatednodes>
	            <mm:first>
	                <img src="<mm:image template="s(200)" />" class="foto" alt="<mm:field name="title" />" title="<mm:field name="title"/>"/>
	            </mm:first>
	        </mm:relatednodes>	
        </mm:relatednodescontainer>
        <p class="text">${intro}</p>
        <p class="text">${body}</p>
        <div class="sub">         	
        	<span class="date">
                <mm:field name="publishdate" id="articleDate">
                    <mm:time time="$articleDate" format="yyyy/MM/dd HH:mm" />
                </mm:field>
            </span>        	
        </div>
        <div class="clear"></div>
	</mm:node>
</mm:cloud>
<cmsc:portletmode name="edit">
	<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>										
</cmsc:portletmode>