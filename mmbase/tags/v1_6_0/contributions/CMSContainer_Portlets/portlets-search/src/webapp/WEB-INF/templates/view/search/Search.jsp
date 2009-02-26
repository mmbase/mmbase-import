<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ taglib uri="http://www.luceus.com/taglib" prefix="lm"%>
<%@ taglib uri="http://www.luceus.com/highlighter/taglib" prefix="lmh"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<cmsc:location var="cur" sitevar="site" />
<cmsc:list-pages var="pages" origin="${site}" />

<c:set var="category"/>
<c:set var="optionPages"/>
<c:set var="hltag">span class="searchphrase"</c:set>

<c:forEach var="page" items="${pages}" varStatus="status">
   <c:if test="${searchCategory eq page.id}">
      <c:set var="category" value="${searchCategory}"/>
   </c:if>
   <c:if test="${fn:length(page.externalurl) == 0}">
      <c:set var="optionPages">${optionPages}<c:if test="${not empty optionPages}">,</c:if>${page.id}</c:set>
   </c:if>
</c:forEach>
<mm:cloud method="asis">
   <mm:listnodescontainer type="properties">
      <mm:constraint field="key" operator="EQUAL" value="search.add.pages" />
      <mm:listnodes max="1">
         <mm:node element="number" jspvar="n" notfound="skip">
            <mm:field name="value" id="searchAddPages" jspvar="pageList" write="false" />
            <c:set var="pages" value="${fn:split(pageList,',')}"/>
            <c:forEach var="page" items="${pages}">
               <mm:node number="${page}" notfound="skip">
                  <c:set var="optionPages">${optionPages},<mm:field name="number"/></c:set>
               </mm:node>
            </c:forEach>
         </mm:node>
      </mm:listnodes>
   </mm:listnodescontainer>
</mm:cloud>

<c:set var="pagerIndex" value="/WEB-INF/templates/view/pager.jsp" />

<c:set var="listHeader" value="/WEB-INF/templates/view/list_header.jsp" />
<c:set var="listFooter" value="/WEB-INF/templates/view/list_footer.jsp" />

<%--

SEARCH PERFORMED HERE

--%>
<c:if test="${not empty searchText}">
   <lm:index name="${indexName}">
      <lm:search var="results" start="${param['pager.offset']}" max="${elementsPerPage}" fields="title,displaytext,contentelement,pageintro,page,pagetitle">
         <lm:match field="fulltext" value="${searchText}" />
         <c:if test="${not empty category}">
            <lm:match field="categories" value="${category}" />
         </c:if>
      </lm:search>
   </lm:index>
</c:if>
<c:if test="${not empty results}">
   <c:set var="totalElements" scope="request" value="${results[0].hits}" />
</c:if>
<c:if test="${empty results}">
   <c:set var="totalElements" scope="request" value="0"/>
</c:if>

<div class="pageheader">
   <h1>
      <fmt:message key="search.results"/>
      <span class="printButton">
         <a href="javascript:print();" title="<fmt:message key="view.print"/>"><fmt:message key="view.print"/></a>
      </span>
   </h1>

   <c:if test="${not empty searchText}">
      <h2>
      	<fmt:message key="search.entry">
      		<fmt:param>${searchText}</fmt:param>
      	</fmt:message>
      	
      </h2>
      <p><fmt:message key="search.count">
      		<fmt:param>${totalElements}</fmt:param>
      	 </fmt:message>
      <c:if test="${totalElements > elementsPerPage}">
      	<fmt:message key="search.showing">
      		<fmt:param>${offset + 1}</fmt:param>
      		<fmt:param>${offset + elementsPerPage}</fmt:param>
      	</fmt:message>
      </c:if></p>
   </c:if>
   <div class="extra">
      <div class="tools">
         <form action="<cmsc:renderURL/>" id="extendedSearch" name="extendedSearch" method="post">
            <fieldset>
               <p>
                  <strong><fmt:message key="search.criteria"/></strong>
               </p>
               <input type="text" id="searchText" name="searchText" class="searchText" value="${searchText}" />
               <input type="submit" value="<fmt:message key="search.submit"/>" class="buttonextra" />
            </fieldset>
   
            <fieldset>
               <p>
                  <strong><fmt:message key="search.specify"/></strong>
               </p>
               <select name="searchCategory" id="searchCategory">
                  <option value=""><fmt:message key="search.allchannels"/></option>
                  <mm:cloud method="asis">
                     <c:forTokens var="pageid" items="${optionPages}" delims=",">
                        <mm:node number="${pageid}">
                           <option value="${pageid}" <c:if test="${searchCategory eq pageid}">selected='selected'</c:if>><mm:field name="title"/></option>                 
                        </mm:node>                  
                     </c:forTokens>
                  </mm:cloud>
               </select>
               <input type="submit" value="<fmt:message key="search.submit"/>" class="buttonextra" />               

            </fieldset>
        </form>

         <div class="clr">&nbsp;</div>
      </div>
   </div>
   <div class="clr">&nbsp;</div>
</div>

<mm:content type="text/html" encoding="UTF-8">
<mm:cloud method="asis">
   <cmsc:renderURL var="tmpRenderUrl"><cmsc:param name="searchText" value="${searchText}" /><cmsc:param name="searchCategory" value="${searchCategory}" /></cmsc:renderURL>
   <c:set var="renderUrl" scope="request" value="${tmpRenderUrl}"/>

   <pg:pager url="${renderUrl}" maxPageItems="${elementsPerPage}" items="${totalElements}" 
         index="${pagesIndex}" maxIndexPages="${showPages}" export="offset,currentPage=pageNumber">

      <c:set var="offset" value="${offset}" scope="request"/>
      <c:set var="startPage" value="${offset + 1}" scope="request"/>  
      <c:set var="endPage" value="${startPage + (elementsPerPage - 1)}" scope="request"/>
      <c:if test="${endPage > totalElements}">
         <c:set var="endPage" value="${totalElements}" scope="request"/>
      </c:if>
      
      <c:if test="${totalElements > elementsPerPage}">
         <c:import url="${pagerIndex}"/>
      </c:if>

   <c:if test="${not empty listHeader}">
       <c:import url="${listHeader}"/>
   </c:if>
   
<%--

SEARCH RESULTS HERE

--%>


   <div class="results search">

      <ol start="${startPage}">

         <c:forEach var="n" items="${results}" varStatus="stat">
            <lmh:bestof input="${n}" field="displaytext" query="${searchText}" size="100" fragments="3" var="txt" tag="${hltag}"/>
               <c:choose>
                  <c:when test="${n.repository eq 'gns'}">
                  <li>
                  	<c:set var="pagetitle">${n.fields['pagetitle'][0]}</c:set>
                     <c:if test="${empty n.fields['pagetitle'][0]}">
                        <c:set var="pagetitle">${n.key}</c:set>
                     </c:if>
                     
                     <c:if test="${fn:length(pagetitle) > 50 && fn:indexOf(pagetitle, ' ') == -1}">
   	               	<c:set var="pagetitle">${fn:substring(pagetitle,0,20)}...${fn:substring(pagetitle,fn:length(pagetitle)-25,fn:length(pagetitle))}</c:set>
                     </c:if>

                     <a href="${n.key}" title="${n.fields['pagetitle'][0]}" target="_blank"><span><strong>${pagetitle}</strong> </span><br />${txt}</a>
                  </li>
                  </c:when>
                  <c:otherwise>
                     <mm:node number="${n.fields['contentelement'][0]}"><span>
                     <mm:field name="title" jspvar="title" write="false"/>
                     <c:if test="${fn:length(title) > 50 && fn:indexOf(title, ' ') == -1}">
   	               	<c:set var="title">${fn:substring(title,0,20)}...${fn:substring(title,fn:length(title)-25,fn:length(title))}</c:set>
                     </c:if>
                     <li>
                       <c:choose>
                           <c:when test="${n.type eq 'attachments'}">
                              <a href="<mm:attachment/>" title="<mm:field name='title'/>" target="_blank">
                                 <span><strong>${title}</strong> </span><br />${txt}</a>
                           </c:when>
                           <c:otherwise>
                              <a href="<cmsc:link dest="${n.fields['page'][0]}" element="${n.fields['contentelement'][0]}" />" title="${n.fields['pagetitle'][0]}">
                                 <span><strong>${title}</strong> </span><br />${txt}</a>
                           </c:otherwise>
                        </c:choose>
                     </li></span></mm:node>
                  </c:otherwise>
               </c:choose>
         </c:forEach>
      </ol>
   </div>

   <c:if test="${not empty listFooter}">
       <c:import url="${listFooter}"/>
   </c:if>
   <c:if test="${totalElements > elementsPerPage}">
      <c:import url="${pagerIndex}"/>
   </c:if>
   </pg:pager>

</mm:cloud>
</mm:content>