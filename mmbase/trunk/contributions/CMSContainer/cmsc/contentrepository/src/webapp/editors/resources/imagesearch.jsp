<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp" 
%><%@page import="java.util.Iterator,
                 com.finalist.cmsc.mmbase.PropertiesUtil"
%><mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="images.title">
   <script src="../repository/search.js" type="text/javascript"></script>
   <script type="text/javascript">
	function showInfo(objectnumber) {
		openPopupWindow('imageinfo', '900', '500',
				'imageinfo.jsp?objectnumber=' + objectnumber);
	}
	
	function initParentHref(elem) {
		elem.parentNode.setAttribute('href', elem.getAttribute('href'));
		var oldSelected = document.getElementById('selected');
		if(oldSelected){
			oldSelected.id="";
		}
		elem.id ='selected';
	}

   function doSelectIt() {
      var href = document.getElementById('imgList').getAttribute('href')+"";
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
	    document.location = "../../repository/HighFrequencyImg.do?action=often&channelid="+channelid;
	}
</script>
   <link rel="stylesheet" type="text/css" href="../css/imagesearch.css" />
	</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">search</mm:import><%-- either often or search --%>
   <div class="editor" style="height:555px">
      <c:choose>
         <c:when test="${action eq 'search'}">
            <mm:import id="formAction">/editors/resources/ImageAction</mm:import>
            <mm:import id="channelMsg"><fmt:message key="images.results" /></mm:import>
         </c:when>
         <c:otherwise>
            <mm:import id="formAction">/editors/repository/HighFrequencyImag</mm:import>
            <c:if test="${param.channelid eq 'all'}">
                <mm:import id="channelMsg"><fmt:message key="images.channel.title"><fmt:param>ALL CHANNELS</fmt:param></fmt:message></mm:import>
            </c:if>
            <c:if test="${param.channelid ne 'all'}">
               <mm:node number="${channelid}">
                  <mm:field name="pathfragment" id="pathfragment" write="false" />
                  <mm:import id="channelMsg">
                     <fmt:message key="images.channel.title">
                        <fmt:param value="${pathfragment}" />
                     </fmt:message>
                  </mm:import>
               </mm:node>
            </c:if>
         </c:otherwise>
      </c:choose>
      <div class="body" <c:if test="${action == 'often'}">style="display:none"</c:if> >
         <html:form action="${formAction}" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="offset"/>
            <c:if test="${action eq 'often'}">
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
      <div class="body" style="max-height:400px;overflow-y:auto; overflow-x:hidden"> 
         <mm:import externid="results" jspvar="nodeList" vartype="List"/>
         <mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>
         <mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
         <c:if test="${resultCount > 0}">
            <%@include file="../repository/searchpages.jsp" %>
            <div id="imgList" class="hover" style="width:100%" href="">
                  <mm:listnodes referid="results">
                     <mm:field name="description" escape="js-single-quotes" jspvar="description">
                        <%description = ((String)description).replaceAll("[\\n\\r\\t]+"," "); %>
                        <mm:import id="url">javascript:selectElement('<mm:field name="number"/>', '<mm:field name="title" escape="js-single-quotes"/>','<mm:image />','<mm:field name="width"/>','<mm:field name="height"/>', '<%=description%>');</mm:import>
                     </mm:field>
                     <div class="grid" href="<mm:write referid="url"/>" onclick="initParentHref(this)">
                        <div class="thumbnail" ondblclick="showInfo('<mm:field name="number"/>')"><mm:image  mode="img" template="s(100x100)" alt="double click to show the info"/></div>
                        <div class="imgInfo"><mm:field name="title"/><br/><mm:field name="itype"/></div>
                     </div>
                  </mm:listnodes>
            </div>
         </c:if>
	      <c:if test="${resultCount == 0 && (param.action == 'often' || param.title != null)}">
	         <fmt:message key="imagesearch.noresult" />
	      </c:if>
	      <c:if test="${resultCount > 0}">
	         <%@include file="../repository/searchpages.jsp" %>
	      </c:if>
      </div>
      <c:if test="${action == 'often'}">
      <div class="body">
      <mm:url page="/editors/repository/select/SelectorChannel.do" id="select_channel_url" write="false" />
      <mm:url page="/editors/resources/ImageInitAction.do?action=search" id="search_image_url" write="false" />
      <mm:url page="/editors/resources/imageupload.jsp?channelid=${channelid}" id="new_image_url" write="false" />
      <mm:url page="/editors/repository/HighFrequencyImg.do?action=often&channelid=all" id="often_show_images" write="false"/>
		<ul class="shortcuts">
			<li><a href="${often_show_images}"><fmt:message key="imageselect.link.allchannel" /></a></li>
			<li><a onclick="openPopupWindow('selectchannel', 340, 400);" target="selectchannel" href="${select_channel_url}"><fmt:message key="imageselect.link.channel" /></a></li>
			<li><a href="${search_image_url}"><fmt:message key="imageselect.link.search" /></a></li>
			<li><a href="${new_image_url}"><fmt:message key="imageselect.link.new" /></a></li>
		</ul>
		</div>
      </c:if>
</div>
       <div id="commandbuttonbar" class="buttonscontent">
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