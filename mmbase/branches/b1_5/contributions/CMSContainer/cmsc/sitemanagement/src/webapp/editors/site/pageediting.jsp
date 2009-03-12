<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ include file="globals.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">

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

            function switchChannelPanel(element) {
               if (window.top.bottompane.oldChannelsCols) {
                  var oldChannelsCols = window.top.bottompane.oldChannelsCols;
                  var channelsCols = window.top.bottompane.document.body.cols;
                  element.value = "<fmt:message key="toolbar.showtree" />";
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
			right:18px;
			top:36px;
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
			text-align:left;
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
      <table style="width:100%; height:100%;" cellpadding="0" cellspacing="0" border="0">
         <tr style="height:37px;">
            <td style="width:60%;">
               <div class="tabs" style="width:100%;">
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
                              <fmt:message key="toolbar.preview"/>
                           </a>
                        </div>
                     </div>
                  </div>
               </div>

            </td>
               <td style="width:40%; height:36px;vertical-align:middle; text-align:right; background:url(<cmsc:staticurl page='/editors/gfx/edit_shadow.gif'/>) repeat-x center;">
                  <input type="checkbox" name="treevisable" id="treevisable" value="<fmt:message key='toolbar.showtree'/>" checked onClick="return switchChannelPanel(this);">
                     <fmt:message key="toolbar.showtree" />
                  </input>
                  <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
                     <mm:hasrank minvalue="administrator">
                        <mm:haspage page="/editors/workflow/">
                           <fmt:bundle basename="cmsc-workflow">
                              <input name="finish" type="button" value="<fmt:message key='workflow.action.finish'/>" onClick="setPublishAction('finish')" style="margin-top:5px; display:inline; "/>
                              <input name="accept" type="button" value="<fmt:message key='workflow.action.accept'/>" onClick="setPublishAction('accept');" style="margin-top:5px; display:inline; "/>
                              <input name="publish" type="button" value="<fmt:message key='workflow.action.publish'/>" onClick="setPublishAction('publish');" style="margin-top:5px; display:inline; "/>
                           </fmt:bundle>
                        </mm:haspage>
                     </mm:hasrank>
                  </mm:cloud>

                  <div id="alertMain" style="visibility:hidden;">
                     <div id="alertLf" onClick="closeAlert();">&nbsp;</div>
                     <div id="divResult" onClick="closeAlert();">&nbsp;</div>
                     <div id="alertRt" onClick="closeAlert();">&nbsp;</div>
                  </div>
               </td>
            </tr>

            <tr>
               <td  style="width:100%;" colspan="2">
                  <iframe src="${requestScope.pathofpage}" name="pcontent" id="pcontent" frameborder="0" style="width:100%;height:100%;"/>
               </td>
            </tr>
         </table>
      </body>
   </html:html>
</mm:content>
