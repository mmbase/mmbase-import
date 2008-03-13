<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<mm:cloud>
  <mm:import externid="elementId" required="true" />
  <mm:node number="${elementId}" notfound="skip">
    <div class="heading">
      <mm:field name="title">
        <mm:isnotempty>
          <h2>
            <mm:write />
          </h2>
        </mm:isnotempty>
      </mm:field>
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
          <p class="intro"><mm:write /></p>
        </mm:isnotempty>
      </mm:field>

      <mm:field name="body" escape="none">
        <mm:isnotempty>
          <p class="body"><mm:write /></p>
        </mm:isnotempty>
      </mm:field>
       
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
              <fmt:formatNumber value="${size div 1048576}" minFractionDigits="1" maxFractionDigits="1" /> MB          
            </c:set>
          </c:when>
          <c:when test="${( size div 1024 ) gt 1.0 }">
            <c:set var="sizeString">
              <fmt:formatNumber value="${size div 1024}" minFractionDigits="1" maxFractionDigits="1" /> KB          
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
      
      <!-- linkbalk -->
      <div class="article_block">
        <div>
          <div class="left">
            <a href="#" class="back" onclick="history.back( ); return false;">
              <fmt:message key="view.back" />&nbsp;<img alt="" src="<cmsc:staticurl page='/gfx/arrow_link_${imagePostfix}.gif' />" />
            </a>
          </div>
          <div class="right">
<%--
            <a href="#">Artikel afdrukken</a>
            <span class="space">|</span>
            <a href="#">Download als pdf</a>
--%>
          </div>
        </div>
      </div>
      <div class="clear"></div>
      <div class="divider"></div>
    </div>
  </mm:node>
</mm:cloud>