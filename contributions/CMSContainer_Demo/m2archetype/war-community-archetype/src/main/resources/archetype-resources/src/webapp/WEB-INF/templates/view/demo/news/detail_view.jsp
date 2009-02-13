<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
#set( $dollar = "$" )
<mm:cloud>
  <mm:import externid="elementId" required="true" />
  <mm:node number="${elementId}" notfound="skip">
	<cmsc:portletmode name="edit">
		<mm:relatednodes type="contentchannel" role="creationrel">
			<mm:field name="number" write="false" jspvar="channelnumber"/>
			<cmsc:isallowededit channelNumber="${channelnumber}">
				<c:set var="edit" value="true"/>
			</cmsc:isallowededit>
		</mm:relatednodes>
	</cmsc:portletmode>
		  
	<c:if test="${edit}">
		<form name="contentportlet" method="post" 
	  		  action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<%@include file="/WEB-INF/templates/edit/itemheader.jsp" %>
	</c:if>


    <div class="heading">
      <h2 id="content_${elementId}_title"><mm:field name="title"/></h2>
	  <c:if test="${edit}">
		  <script type="text/javascript">
			new InPlaceEditor.Local('content_${elementId}_title');
		  </script>
	  </c:if>
    </div>
    <div class="content">
      <div class="eventinfo">
        <mm:field name="publishdate"><mm:time format="dd-MM-yyyy" /></mm:field>
      </div>
      <div class="divider3"></div>
      <!-- top images -->      
      <cmsc-bm:linkedimages width="525" position="top" style="display: block; clear: both; padding-bottom: 20px;" />
      <cmsc-bm:linkedimages width="220" position="top-left" style="float: left; padding: 0px 20px 20px 0px;" />
      <cmsc-bm:linkedimages width="220" position="top-right" style="float: right; padding: 0px 0px 20px 20px;" />

      <mm:field name="intro" escape="none">
        <mm:isnotempty>
          <p class="intro" id="content_${elementId}_intro"><mm:write /></p>
          <c:if test="${edit}">
          
  		  	<script type="text/javascript">
				new InPlaceEditor.Local('content_${elementId}_intro', {minHeight:300, htmlarea:true, formId:'contentportlet'});
		  	</script>
		  </c:if>
        </mm:isnotempty>
      </mm:field>

	<c:if test="${edit}">
		<div id="content_${elementId}_body">
	</c:if>
      <mm:field name="body" escape="none">
        <mm:isnotempty>
          <p class="body"><mm:write /></p>
        </mm:isnotempty>
      </mm:field>
	<c:if test="${edit}">
		</div>
	  	<script type="text/javascript">
			new InPlaceEditor.Local('content_${elementId}_body', {minHeight:300, htmlarea:true, formId:'contentportlet'});
	  	</script>
	</c:if>
       
      <div class="divider3"></div>
       
      <%-- related articles --%>
      <mm:relatednodes type="contentelement" role="posrel" orderby="posrel.pos" searchdir="destination">
        <mm:field name="number" id="elementNumber" write="false" />
        
        <mm:first>
          <h3 style="font-size: 1em;"><fmt:message key="view.article.related.articles" /></h3>
          <ul>
        </mm:first>
        
        <li>
          <a 
            href="<cmsc:contenturl number="${elementNumber}"/>" 
            title="<mm:field name="title" escape="text/html/attribute" />"
          ><mm:field name="title" escape="text/xml" /></a>
        </li>
          
        <mm:last></ul></mm:last>          
      </mm:relatednodes>

      <%-- related urls --%>
      <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos" searchdirs="destination">
        <mm:first>
          <h3 style="font-size: 1em;"><fmt:message key="view.article.related.urls" /></h3>
          <ul>
        </mm:first>
          
          <li>
            <a 
              href="<mm:field name="url" escape="text/html/attribute" />" 
              title="<mm:field name="description" escape="text/html/attribute" />"
              target="_blank"
            ><mm:field name="name" escape="text/xml" /></a>
          </li>
          
        <mm:last></ul></mm:last>          
      </mm:relatednodes>

      <%-- related attachments --%>
      <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos" searchdirs="destination">
        <%-- pretty print the file sizes --%>
        <mm:field name="size" jspvar="size" write="false" />
        <c:choose>
          <c:when test="${( size div 1048576 ) gt 1.0 }">
            <c:set var="sizeString">
              <fmt:formatNumber value="${dollar}{size div 1048576}" minFractionDigits="1" maxFractionDigits="1" /> MB          
            </c:set>
          </c:when>
          <c:when test="${dollar}{( size div 1024 ) gt 1.0 }">
            <c:set var="sizeString">
              <fmt:formatNumber value="${dollar}{size div 1024}" minFractionDigits="1" maxFractionDigits="1" /> KB          
            </c:set>
          </c:when>
          <c:otherwise>
            <c:set var="sizeString">
              <fmt:formatNumber value="${size}" minFractionDigits="1" maxFractionDigits="1" /> B          
            </c:set>
          </c:otherwise>
        </c:choose>

        <mm:first>
          <h3 style="font-size: 1em;"><fmt:message key="view.article.related.attachments" /></h3>
          <ul>
        </mm:first>
        
        <li>
          <a href="<mm:attachment />" title="<mm:field name="description" escape="text/html/attribute" />">
            <mm:field name="title" escape="text/xml" />
          </a>
          (${sizeString})
        </li>
        
        <mm:last></ul></mm:last>
      </mm:relatednodes>

      <!-- bottom images -->
      <cmsc-bm:linkedimages width="220" position="bottom-left" style="float: left; padding: 20px 20px 0px 0px;" />
      <cmsc-bm:linkedimages width="220" position="bottom-right" style="float: right; padding: 20px 0px 0px 20px;" />
      <cmsc-bm:linkedimages width="525" position="bottom" style="display: block; clear: both; padding-top: 20px;" />
      <div class="clear"></div>
      <div class="divider"></div>
    </div>
    
	<c:if test="${edit}">
		<%@include file="/WEB-INF/templates/edit/itemfooter.jsp" %>
		</form>
	</c:if>
    
  </mm:node>
</mm:cloud>