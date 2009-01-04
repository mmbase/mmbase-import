<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
#set( $dollar = "$" )
<mm:cloud>
  <mm:import externid="elementId" required="true" />
  <mm:node number="${elementId}" notfound="skip">

    <mm:countrelations role="posrel" searchdir="destination" id="secondaryContentCount" write="false" />
    
    <c:if test="${dollar}{secondaryContentCount gt 0}">

      <div class="content">
        <div class="linklist">
          
          <mm:field name="title"><mm:isnotempty><h2><mm:write /></h2></mm:isnotempty></mm:field>
       
          <ul>

            <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos" searchdir="destination">
              <li>
                <a href="<mm:attachment/>" title="<mm:field name='description'/>" target="_blank"><mm:field name="title" /></a>
              </li>
            </mm:relatednodes>
  
   			    <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos" searchdir="destination">
              <mm:field name="url" id="url" write="false" />
              <li>
             	  <a href="<mm:url referid='url'/>" title="<mm:field name='name'/>" target="_blank"><mm:field name="name" /></a>
              </li>
            </mm:relatednodes>
            
            <mm:relatednodes type="contentelement" role="posrel" orderby="posrel.pos" searchdir="destination">
              <mm:field name="number" id="elementNumber" write="false" />
              <li>
                <a href="<cmsc:contenturl number="${elementNumber}"/>" title="<mm:field name='title'/>"><mm:field name="title" /></a>
              </li>
            </mm:relatednodes>
            
            <mm:relatednodes type="page" searchdir="destination">
              <li>
                <a href="<cmsc:contenturl />" title="<mm:field name='title'/>"><mm:field name="title" /></a>
              </li>
            </mm:relatednodes>
          </ul>
        
        </div>
      </div>

    </c:if>
  </mm:node>
</mm:cloud>