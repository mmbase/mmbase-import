<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@page import="java.util.Iterator,com.finalist.cmsc.mmbase.PropertiesUtil"
%><%@page import="com.finalist.cmsc.repository.RepositoryUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="images.title">
   <script src="../repository/search.js" type="text/javascript"></script>
   <script type="text/javascript">
   function setShowMode() {
	   var showMode = document.getElementsByTagName("option");
	   var assetShow;
       for(i = 0; i < showMode.length; i++){
          if(showMode[i].selected & showMode[i].id=="a_list"){
              assetShow="list";
          }else if(showMode[i].selected & showMode[i].id=="a_thumbnail"){
        	  assetShow="thumbnail";
          }
       }
      document.forms[0].assetShow.value = assetShow;
      document.forms[0].submit();
	}
	function showInfo(objectnumber) {
		openPopupWindow('imageinfo', '900', '500',
				'../resources/imageinfo.jsp?objectnumber=' + objectnumber);
	}
	
	function initParentHref(elem) {
		if(elem.id=='selected'){
			elem.parentNode.setAttribute('href', '');
			elem.id ='';
			return;
		}
		elem.parentNode.setAttribute('href', elem.getAttribute('href'));
		var oldSelected = document.getElementById('selected');
		if(oldSelected){
			oldSelected.id="";
		}
		elem.id ='selected';
	}

   function doSelectIt() {
      var href = document.getElementById('assetList').getAttribute('href')+"";
      if (href.length<10) {
          alert("You must select one image");
          return;
      }
      if (href.indexOf('javascript:') == 0) {
       eval(href.substring('javascript:'.length, href.length));
       return false;
      }
      document.location=href;
   }
   
   function doCancleIt(){
      window.top.close();
   }
   
	function selectElement(element, title, src, width, height, description) {
		if (window.top.opener != undefined) {
			window.top.opener.selectElement(element, title, src, width, height,
					description);
			window.top.close();
		}
	}

	function selectChannel(channelid, path) {
		var imageMode = document.getElementsByTagName("option");
	       for(i = 0; i < imageMode.length; i++){
	          if(imageMode[i].selected & imageMode[i].id=="a_list"){
	              document.location.href = '../../repository/HighFrequencyAsset.do?action=often&offset=0&channelid='+channelid+'&assetShow=list&assettypes=images&strict=${strict}';
	          }else if(imageMode[i].selected & imageMode[i].id=="a_thumbnail"){
	              document.location.href = '../../repository/HighFrequencyAsset.do?action=often&offset=0&channelid='+channelid+'&assetShow=thumbnail&assettypes=images&strict=${strict}';
	          }
	       }
	}
</script>
   <link rel="stylesheet" type="text/css" href="../css/assetsearch.css" />
	</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">search</mm:import><%-- either often or search --%>
<mm:import externid="assetShow">list</mm:import><%-- either list or thumbnail --%>

   <c:if test="${action eq 'search'}">
      <div class="tabs"><!-- actieve TAB -->
      <div class="tab_active">
      <div class="body">
      <div><a><fmt:message key="images.title" /></a></div>
      </div>
      </div>
      </div>
   </c:if>

   <div class="editor" style="height:555px">
      <c:choose>
         <c:when test="${action eq 'search'}">
            <mm:import id="formAction">/editors/resources/ImageAction</mm:import>
            <mm:import id="channelMsg"><fmt:message key="images.results" /></mm:import>
         </c:when>
         <c:otherwise>
            <mm:import id="formAction">/editors/repository/HighFrequencyAsset</mm:import>
            <c:if test="${param.channelid eq 'all'}">
                <mm:import id="channelMsg"><fmt:message key="images.channel.title"><fmt:param>ALL CHANNELS</fmt:param></fmt:message></mm:import>
            </c:if>
            <c:if test="${param.channelid ne 'all'}">
               <mm:node number="${channelid}">
                  <mm:field name="path" id="path" write="false" />
                  <mm:import id="channelMsg">
                     <fmt:message key="images.channel.title">
                        <fmt:param value="${path}" />
                     </fmt:message>
                  </mm:import>
               </mm:node>
            </c:if>
         </c:otherwise>
      </c:choose>
      <div class="body" <c:if test="${action == 'often'}">style="display:none"</c:if> >
         <html:form action="${formAction}" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="assetShow" value="${assetShow}"/>
            <html:hidden property="strict" value="${strict}"/>
            <html:hidden property="offset"/>
            <c:if test="${action eq 'often'}">
            <html:hidden property="assettypes" value="images"/>
            <html:hidden property="channelid" value="${channelid}"/>
            </c:if>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
            <c:if test="${action eq 'search'}">
            <mm:import id="contenttypes" jspvar="contenttypes">images</mm:import>
               <%@include file="imageform.jsp" %>
            </c:if>
         </html:form>
      </div>
      <div class="ruler_green">
         <div><c:out value="${channelMsg}" /></div>
   `  </div>
		<select name="imageMode" id="imageMode" onchange="javascript:setShowMode()">
			<c:if test="${assetShow eq 'list'}">
				<option id="a_list" selected="selected"><fmt:message key="asset.image.list"/></option>
				<option id="a_thumbnail"><fmt:message key="asset.image.thumbnail"/></option>
			</c:if>
			<c:if test="${assetShow eq 'thumbnail'}">
				<option id="a_list"><fmt:message key="asset.image.list"/></option>
				<option id="a_thumbnail" selected="selected"><fmt:message key="asset.image.thumbnail"/></option>
			</c:if>
		</select>
      <div class="body" style="max-height:400px;overflow-y:auto; overflow-x:hidden"> 
         <mm:import externid="results" jspvar="nodeList" vartype="List"/>
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
         <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
         <c:if test="${resultCount > 0}">
            <%@include file="../repository/searchpages.jsp" %>

            <c:if test="${assetShow eq 'thumbnail'}">
            <div id="assetList" class="hover" style="width:100%" href="">
            <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
               <mm:field id="trashnumber" name="number" write="false"/>
            </mm:node>
                  <mm:listnodes referid="results">
                     <mm:field name="description" escape="js-single-quotes" jspvar="description">
                        <%
                           description = ((String) description).replaceAll("[\\n\\r\\t]+", " ");
                        %>
                        <c:if test="${strict == 'images'}">
                          <mm:import id="url">javascript:top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();</mm:import>
                        </c:if>
                        <c:if test="${ empty strict}">
                        	<mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:image />','<mm:field name="width"/>','<mm:field name="height"/>', '<%=description%>');</mm:import>
                        </c:if>
                        <mm:relatednodes role="creationrel" type="contentchannel">
                           <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
                        </mm:relatednodes>
                     </mm:field>
                     <c:if test="${creationRelNumber ne trashnumber}">
                     <div class="grid" href="<mm:write referid="url"/>" onclick="initParentHref(this)" title="double click to show the info">
                        <div class="thumbnail" ondblclick="showInfo('<mm:field name="number"/>')"><mm:image mode="img" template="s(120x100)"/></div>
                        <div class="assetInfo">
                              <mm:field id="title" write="false" name="title"/>
                              <c:if test="${fn:length(title) > 15}">
                                 <c:set var="title">${fn:substring(title,0,14)}...</c:set>
                              </c:if>${title}
                              <br/><mm:field name="itype" />
                        </div>
                     </div>
                     </c:if>
                  </mm:listnodes>
            </div>
            </c:if>

			<c:if test="${assetShow eq 'list'}">
				<table>
            <c:if test="${action == 'search'}">
					<tr class="listheader">
						<th width="55"></th>
						<th nowrap="true"><a href="javascript:orderBy('title')"
							class="headerlink"><fmt:message key="imagesearch.titlecolumn" /></a></th>
						<th nowrap="true"><a href="javascript:orderBy('filename')"
							class="headerlink"><fmt:message
							key="imagesearch.filenamecolumn" /></a></th>
						<th nowrap="true"><a href="javascript:orderBy('itype')"
							class="headerlink"><fmt:message
							key="imagesearch.mimetypecolumn" /></a></th>
						<th></th>
					</tr>
            </c:if>
					<tbody id="assetList" class="hover"  href="">
					   <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
					      <mm:field id="trashnumber" name="number" write="false"/>
					   </mm:node>
						<c:set var="useSwapStyle">true</c:set>
						<mm:listnodes referid="results">
                     <mm:field name="description" escape="js-single-quotes" jspvar="description">
                        <%
                           description = ((String) description).replaceAll("[\\n\\r\\t]+", " ");
                        %>
                        <c:if test="${strict == 'images'}">
                           <mm:import id="url">javascript:top.opener.selectContent('<mm:field name="number" />', '', ''); top.close();</mm:import>
                        </c:if>
                        <c:if test="${ empty strict}">
                           <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:image />','<mm:field name="width"/>','<mm:field name="height"/>', '<%=description%>');</mm:import>
                        </c:if>
                        <mm:relatednodes role="creationrel" type="contentchannel">
			                  <c:set var="creationRelNumber"><mm:field name="number" id="creationnumber"/></c:set>
			               </mm:relatednodes>
                     </mm:field>
                     <c:if test="${creationRelNumber ne trashnumber}">
							<tr <c:if test="${useSwapStyle}">class="swap"</c:if>
								href="<mm:write referid="url"/>">
								<td style="white-space: nowrap;">
                        <a href="javascript:showInfo(<mm:field name="number" />)">
                              <img src="../gfx/icons/info.png" alt="<fmt:message key="imagesearch.icon.info" />" title="<fmt:message key="imagesearch.icon.info" />" /></a>
								</td>
                        <td onMouseDown="initParentHref(this.parentNode)">
                           <mm:field id="title" write="false" name="title"/>
                           <c:if test="${fn:length(title) > 50}">
                              <c:set var="title">${fn:substring(title,0,49)}...</c:set>
                           </c:if>
                           ${title}
                        </td>
                        <td onMouseDown="initParentHref(this.parentNode)">
                           <mm:field name="filename"/>
                        </td>
								<td onMouseDown="initParentHref(this.parentNode)"><mm:field name="itype" /></td>
								<td  onMouseDown="initParentHref(this.parentNode)"><img
									src="<mm:image template="s(120x100)"/>" alt="" /></td>
							</tr>
							</c:if>
							<c:set var="useSwapStyle">${!useSwapStyle}</c:set>
						</mm:listnodes>
					</tbody>
				</table>
			</c:if>

		</c:if>
	      <c:if test="${resultCount == 0 && (param.action == 'often' || param.title != null)}">
	         <fmt:message key="imagesearch.noresult" />
	      </c:if>
         <div style="clear:both" ></div>
	      <c:if test="${resultCount > 0}">
	         <%@include file="../repository/searchpages.jsp" %>
	      </c:if>
      </div>
      <c:if test="${action == 'often'}">
      <div class="body">
      <mm:url page="/editors/repository/select/SelectorChannel.do" id="select_channel_url" write="false" />
      <mm:url page="/editors/resources/ImageInitAction.do?action=search&strict=${strict}" id="search_image_url" write="false" />
      <mm:url page="/editors/resources/imageupload.jsp?uploadedNodes=0&channelid=${channelid}&strict=${strict}" id="new_image_url" write="false" />
      <mm:url page="/editors/repository/HighFrequencyAsset.do?action=often&assetShow=${assetShow}&offset=0&channelid=all&assettypes=images&strict=${strict}" id="often_show_images" write="false"/>
		<ul class="shortcuts">
			<li><a href="${often_show_images}"><fmt:message key="imageselect.link.allchannel" /></a></li>
			<li><a onclick="openPopupWindow('selectchannel', 340, 400);" target="selectchannel" href="${select_channel_url}"><fmt:message key="imageselect.link.channel" /></a></li>
			<li><a href="${search_image_url}"><fmt:message key="imageselect.link.search" /></a></li>
			<li><a href="${new_image_url}"><fmt:message key="imageselect.link.new" /></a></li>
		</ul>
		</div>
      </c:if>
</div>
       <div id="commandbuttonbar" class="buttonscontent" style="clear:both">
            <div class="page_buttons_seperator">
               <div></div>
            </div>
            <div class="page_buttons">
                <div class="button">
                    <div class="button_body">
                        <a class="bottombutton" title="Select the image." href="javascript:doSelectIt();"><fmt:message key="imageselect.ok" /></a>
                    </div>
                </div>
               
                <div class="button">
                    <div class="button_body">
                        <a class="bottombutton" href="javascript:doCancleIt();" title="Cancel this task, image will NOT be selected."><fmt:message key="imageselect.cancel" /></a>
                    </div>
                </div>
                <div class="begin">
                </div>
            </div>
        </div>
</mm:cloud>
</body>
</html:html>
</mm:content>