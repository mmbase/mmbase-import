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
      if (href.length<10) return;
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
	    document.location = "../../resources/ImageAction.do?action=often&contenttypes=images&offset=0&order=title&direction=1&channelid="+channelid;
	}
</script>
   <link rel="stylesheet" type="text/css" href="../../editors/editwizards_new/style/extra/wizard.css">
		<style type="text/css">
div.editor div.body ul.shortcuts li {
	padding-left: 5px;
	font-size: 12px;
	font-weight: normal;
}

div.editor div.body ul.shortcuts li a {
	background-image: url(../gfx/button_side_block.gif);
	background-position: right center;
	background-repeat: no-repeat;
	padding-right: 15px;
}

div.editor div.body #imgList div.grid {
	width: 200px;
	height: 200px;
	float: left;
	text-align: center
}

div.editor div.body #imgList div.grid:hover, div.editor div.body #imgList #selected{
	background-color: #f1f400;
}

div.editor div.body #imgList div.grid div.thumbnail {
	width: 100%;
	height: 80%;
	text-align: center;
	vertical-align:middle;
	padding: 0;
}

div.editor div.body #imgList div.grid div.imgInfo {
	width: 100%;
	height: 20%;
	text-align: center;
	padding: 0px;
}
</style>
	</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
<mm:import externid="action">often</mm:import><%-- either often or search --%>
   <div class="editor" style="height:555px">
      <div class="body">
         <html:form action="/editors/resources/ImageAction" method="post">
            <html:hidden property="action" value="${action}"/>
            <html:hidden property="offset"/>
            <html:hidden property="order"/>
            <html:hidden property="direction"/>
            <mm:import id="contenttypes" jspvar="contenttypes">images</mm:import>
            <c:if test="${action == 'search'}">
               <%@include file="imageform.jsp" %>
            </c:if>
         </html:form>
      </div>
		<div class="ruler_green">
		<c:choose>
			<c:when test="${empty param.channelid}">
				<div><fmt:message key="images.channel.title"><fmt:param>ALL CHANNELS</fmt:param></fmt:message></div>
			</c:when>
			<c:otherwise>
				<c:if test="${param.channelid eq 'current'}">
					<mm:import id="channelid" externid="creation" from="session" />
				</c:if>
				<mm:node number="${channelid}">
					<mm:field name="pathfragment" id="pathfragment" write="false" />
					<div><fmt:message key="images.channel.title">
						<fmt:param value="${pathfragment}" />
					</fmt:message></div>
				</mm:node>
			</c:otherwise>
		</c:choose>
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
	      <c:if test="${resultCount == 0 && param.title != null}">
	         <fmt:message key="imagesearch.noresult" />
	      </c:if>
	      <c:if test="${resultCount > 0}">
	         <%@include file="../repository/searchpages.jsp" %>
	      </c:if>
      </div>
      <c:if test="${action != 'search'}">
      <div class="body">
      <mm:url page="/editors/repository/select/SelectorChannel.do" id="select_channel_url" write="false" />
      <mm:url page="/editors/resources/ImageAction.do?action=search&channelid=${channelid}" id="search_image_url" write="false" />
      <mm:url page="/editors/resources/imageupload.jsp?channelid=${channelid}" id="new_image_url" write="false" />
		<ul class="shortcuts">
			<li><a href="#"><fmt:message key="imageselect.link.allchannel" /></a></li>
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
                        <a id="bottombutton-ok" class="bottombutton" title="Select the image." href="javascript:doSelectIt();" unselectable="on" titlesave="Select the image." titlenosave="Cannot be saved, since no image is selected." inactive="false"><fmt:message key="imageselect.ok" /></a>
                    </div>
                </div>
               
                <div class="button">
                    <div class="button_body">
                        <a id="bottombutton-cancel" class="bottombutton" href="javascript:doCancleIt();" title="Cancel this task, image will NOT be selected."><fmt:message key="imageselect.cancel" /></a>
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