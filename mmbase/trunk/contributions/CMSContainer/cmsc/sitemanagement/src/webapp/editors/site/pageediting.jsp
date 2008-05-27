<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ include file="globals.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="net.sf.mmapps.modules.cloudprovider.*,org.mmbase.bridge.Cloud,org.mmbase.bridge.Node,java.util.*,net.sf.mmapps.commons.util.StringUtil;" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
   <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
   <html:html xhtml="true">
      <cmscedit:head title="toolbar.title">
         <link href="../css/toolbar.css" rel="stylesheet" type="text/css"/>
         <script language="JavaScript" src="../../js/prototype.js" type="text/javascript"></script>
         <c:url value="/editors/workflow/PageWorkflowPublishAction.do" var="pagePublishAction"/>
         <c:url value="/editors/site/NavigatorPanel.do" var="pageUrl"/>
         <script type="text/javascript">
            
            function refreshTreeTab(){
               var fresh="${requestScope.fresh}";
               if(fresh != null && fresh !=""){
                  parent.frames['pages'].location.reload();
               }            
            }
            
            function loadPreview(){
               var curMode="${sessionScope.pageMode}";
               if(curMode =='preview'){
                  setPagePreview();
               }
            }

            function setPagePreview(){
               setElementStyleByClassName('portlet-mode-type-admin', 'display', false ? '' : 'none');
               setElementStyleByClassName('portlet-mode-type-edit', 'display', false ? '' : 'none');
               setElementStyleByClassName('portlet-mode-type-view', 'display', false ? '' : 'none');
               setElementStyleByClassName('portlet-mode-spacer', 'display', 'none');
               setElementStyleByClassName('portlet-header-canvas', 'display', 'none');
               setElementStyleByClassName('portlet-canvas', 'borderWidth', '0px');
               setElementStyleByClassName('portlet-mode-canvas portlet-mode-type-view', 'display', 'none');
            }

            function setElementStyleByClassName(cl, propertyName, propertyValue) {
               if (!pcontent.document.getElementsByTagName) return;
               var re = new RegExp("(^| )" + cl + "( |$)");
               var el = pcontent.document.all ? pcontent.document.all : pcontent.document.getElementsByTagName("body")[0].getElementsByTagName("*"); // fix for IE5.x
               for (var i = 0; i < el.length; i++) {
                  if (el[i].className && el[i].className.match(re)) {
                     el[i].style[propertyName] = propertyValue;
                  }
               }
            }

            function switchChannelPanel(element) {
               if (window.top.bottompane.oldChannelsCols) {
                  var oldChannelsCols = window.top.bottompane.oldChannelsCols;
                  var channelsCols = window.top.bottompane.document.body.cols;
                  element.value = "<fmt:message key="toolbar.hidetree" />";
                  window.top.bottompane.oldChannelsCols = channelsCols;
                  window.top.bottompane.document.body.cols = oldChannelsCols;
               }
            }

            function setPublishAction(action){
               var tmpUrl="${pagePublishAction}";
               var nodeId="${requestScope.nodeId}";
               new Ajax.Request(
                  tmpUrl,
                  {
                     method:'post',
                     parameters: {actionvalue: action, number: nodeId},
                     onComplete: showResponse
                  }
               );
            }
               
            function showResponse(response) {
               $('divResult').innerHTML = response.responseText;
               var alertDiv = $('alertMain');
               var left = alertDiv.style;
               left.cssText = "visibility:visable;";
            }

            function closeAlert(){
               var alertDiv = $('alertMain');
               var alertFlag = alertDiv.style;      
               alertFlag.cssText = "visibility:hidden;";
            }
         </script>
      </cmscedit:head>

      <style type="text/css">
		<!--
		#alertMain {
			width:384px;
			height:71px;
			position:absolute;
			left:780px;
			top:35px;
			z-index:1000;
		}
		
		#alertLf{
			width:58px; 
			height:80px; 
			background:url(../gfx/alert_green_left.gif) no-repeat; 
			float:left;
		}
		#divResult{
			width:314px; 
			height:80px;
			background:url(../gfx/alert_middle.gif) repeat-x;
			vertical-align:middle;
			font:14px bold;
			float:left;
		}
		#alertRt{
			width:12px;
			height:80px;
			background:url(../gfx/alert_right.gif) no-repeat;
			float:left;
		}
		-->
		</style>
      <body onLoad="refreshTreeTab();">
         <div class="tabs" style="width:70%">
            <div ${'edit' eq sessionScope.pageMode ? 'class="tab_active"' : 'class="tab"'} >
               <div class="body">
                  <div>
                     <a href="${pageUrl}?nodeId=${requestScope.nodeId}&pageMode=edit" id="edit">
                        <fmt:message key="toolbar.editpage"/>
                     </a>
                  </div>
               </div>
            </div>
            <div ${'preview' eq sessionScope.pageMode ? 'class="tab_active"' : 'class="tab"'} >
               <div class="body">
                  <div>
                     <a href="${pageUrl}?nodeId=${requestScope.nodeId}&pageMode=preview" id="preview">
                        <fmt:message key="toolbar.preview" />
                     </a>
                  </div>
               </div>
            </div>
         </div>

         <div style="width:30%;float:right;height:35px; background: url(<cmsc:staticurl page='/editors/gfx/edit_shadow.gif'/>) repeat-x;">
            <input type="checkbox" name="treevisable" id="treevisable" value="<fmt:message key='toolbar.showtree'/>" checked onChange="return switchChannelPanel(this);">
               <fmt:message key="toolbar.showtree" />
            </input>
            <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
               <mm:hasrank minvalue="administrator">
                  <mm:haspage page="/editors/workflow/">
                  <fmt:bundle basename="cmsc-workflow">
                     <input name="finish" type="button" value="<fmt:message key='workflow.action.finish'/>" onClick="setPublishAction('finish')" />
                     <input name="accept" type="button" value="<fmt:message key='workflow.action.accept'/>" onClick="setPublishAction('accept');" />
                     <input name="publish" type="button" value="<fmt:message key='workflow.action.publish'/>" onClick="setPublishAction('publish');" />
                  </fmt:bundle>
                  </mm:haspage>
               </mm:hasrank>
            </mm:cloud>
         </div>
         <div id="alertMain" style="visibility:hidden;">
            <div id="alertLf" onClick="closeAlert();">&nbsp;</div>
            <div id="divResult" onClick="closeAlert();">&nbsp;</div>
            <div id="alertRt" onClick="closeAlert();">&nbsp;</div>
         </div>
         <iframe src="${requestScope.pathofpage}" onload="loadPreview()" name="pcontent" frameborder="0" width="100%" height="445px"></iframe>
      </body>      
   </html:html>
</mm:content>
