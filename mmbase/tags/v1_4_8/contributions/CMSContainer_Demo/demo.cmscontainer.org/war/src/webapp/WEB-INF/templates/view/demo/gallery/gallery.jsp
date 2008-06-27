<%@include file="/WEB-INF/templates/portletglobals.jsp"%>

<%-- LOGO --%>
<div id="logo"><a href="<cmsc:property key="view.logo.url" />" target="_blank"></a></div>

<mm:content type="text/html" encoding="UTF-8">
  <mm:cloud method="asis">
    <mm:import externid="elementId" required="true" />
    <mm:node number="${elementId}" notfound="skip">

      <c:set var="thumbnailWidth" value="146" />
      <c:set var="thumbnailHeight" value="106" />
      <c:set var="imageWidth" value="449" />
      <c:set var="imageHeight" value="${imageWidth * 3 / 4 }" />

      <div id="contentdiv">
        <div class="gallery">
          <h2><mm:field name="title" /></h2>
          
          <div class="intro">
            <c:if test="${empty param.mode or param.mode eq 'list'}">
              <p><mm:field name="intro" /></p>
            </c:if>
            &nbsp;
          </div>
    
          <mm:relatednodescontainer type="images" role="imagerel">
            <mm:sortorder field="imagerel.number" direction="up" />
            <%-- 
              url to detail page used by pageindex of detail mode 
              AND link to detail image in list mode
            --%>
            <cmsc:renderURL page="${page}" window="${window}" var="detailUrl">
              <cmsc:param name="elementId" value="${elementId}" />
              <cmsc:param name="mode" value="detail" />
            </cmsc:renderURL>
  
            <cmsc:renderURL page="${page}" window="${window}" var="listUrl">
              <cmsc:param name="elementId" value="${elementId}" />
              <cmsc:param name="mode" value="list" />
            </cmsc:renderURL>
            
            <%-- settings for list mode --%>
            <c:if test="${empty param.mode or param.mode eq 'list'}">
              <%-- 3000, they don't want it to show different pages AFAIK  --%>
              <c:set var="elementsPerPage" value="3000" />
              <c:set var="pageName" value="pagina" />
              <cmsc:renderURL var="renderUrl" />
            </c:if>
    
            <%-- settings for detail mode --%>
            <c:if test="${not empty param.mode and param.mode eq 'detail'}">
              <c:set var="elementsPerPage" value="1" />
              <c:set var="pageName" value="foto" />
              <c:set var="renderUrl" value="${detailUrl}" />
              <c:set var="mode" value="detail" />
            </c:if>
    
            <%-- settings for both modes --%>
            <c:set var="totalElements">
              <mm:size id="totalitems" />
            </c:set>
            <c:set var="pagesIndex" value="center" />
            <c:set var="showPages" value="10" />
    
            <pg:pager
              url="${renderUrl}"
              maxPageItems="${elementsPerPage}"
              items="${totalElements}"
              index="${pagesIndex}"
              maxIndexPages="${showPages}"
              isOffset="true"
              export="offset,currentPage=pageNumber"
            >
    
              <c:if test="${not empty param.mode and param.mode eq 'detail'}">
                <mm:relatednodes offset="${offset}" max="${elementsPerPage}" orderby="imagerel.order">
                  <mm:field name="title" jspvar="imageTitle" write="false" />
                </mm:relatednodes>
                
                <div class="gallery-detail">
                  <mm:relatednodes offset="${offset}" max="${elementsPerPage}" orderby="imagerel.order">
                    <img src="<mm:image width="${imageWidth}" height="${imageHeight}" />" />
                    
                    <ul class="gallery-detail-navigation">
                      <li>
                        <c:set var="maxPopupWidth" value="800" />
                        <c:set var="maxPopupHeight" value="600" />
                        <mm:image>
                          <c:set var="imageUrl" value="${_}" />
                          <c:set var="popupWidth" value="${dimension.width > maxPopupWidth ? maxPopupWidth  : dimension.width + 20}" />
                          <c:set var="popupHeight" value="${dimension.height > maxPopupHeight ? maxPopupHeight : dimension.height}" />
                        </mm:image>
                        <a 
                          href="${imageUrl}" 
                          onclick="return openWallpaperImage( ${popupWidth}, ${popupHeight}, '<cmsc:staticurl page="/popups/wallPaperPopup.jsp" />?nodenumber=${_node.number}', this);"
                        ><fmt:message key="view.gallery.downloadtodesktop" /></a>
                      </li>
                      <li>&nbsp;|&nbsp;</li>
                      <li>
                        <a href="${listUrl}"><fmt:message key="view.gallery.backtolist" /></a>
                      </li>
                    </ul>
                  </mm:relatednodes> 
                </div>
              </c:if>
    
              <c:if test="${empty param.mode or param.mode eq 'list'}">
                <c:set var="count" value="0" />
    
                <div class="gallery-list">
                  <mm:relatednodes offset="${offset}" max="${elementsPerPage}" orderby="imagerel.order">
                    <mm:image template="resize(x${thumbnailHeight * 2})+resize(${thumbnailWidth * 2}x<)+resize(50%)+gravity(center)+crop(${thumbnailWidth}x${thumbnailHeight}+0+0)+(+repage)" jspvar="imageUrl" write="false" />
                    <div class="gallery-list-item ${count mod 3 eq 2 ? 'row-end' : ''}">
                      <a href="${detailUrl}?pager.offset=${offset + count}">
                        <img src="${imageUrl}" alt="${mm:escape('text/xml', _node.description)}" />                
                      </a>
                      <p class="gallery-list-caption"><mm:field name="title" escape="text/xml" /></p>
                    </div>
                    
                    <mm:last><div class="clear"></div></mm:last>
                    
                    <c:set var="count" value="${count + 1}" />
                  </mm:relatednodes>
                </div>
    
                <c:if test="${totalElements+1 > elementsPerPage+1}">
                  <%@include file="pagerindex.jsp"%>
                </c:if>
              </c:if>
            </pg:pager>
          </mm:relatednodescontainer>
        </div>
      </div>
    </mm:node>
  </mm:cloud>
</mm:content>