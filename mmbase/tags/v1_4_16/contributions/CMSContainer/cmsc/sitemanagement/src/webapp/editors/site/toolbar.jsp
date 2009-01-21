<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="UTF-8" %>
<%@ include file="globals.jsp" %>

<mm:content type="text/html" encoding="UTF-8" expires="0">
   <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
   <html:html xhtml="true">
      <cmscedit:head title="toolbar.title">
         <link href="../css/toolbar.css" rel="stylesheet" type="text/css"/>
         <script type="text/javascript">
            function switchChannelPanel(element) {
               if (window.top.bottompane.oldChannelsCols) {
                  var oldChannelsCols = window.top.bottompane.oldChannelsCols;
                  window.top.bottompane.sitemanagement

                  var channelsCols = window.top.bottompane.document.body.cols;
                  if (channelsCols == '0,*') {
                     element.value = "<fmt:message key="toolbar.hidetree" />";
                  }
                  else {
                     element.value = "<fmt:message key="toolbar.showtree" />";
                  }
                  window.top.bottompane.oldChannelsCols = channelsCols;
                  window.top.bottompane.document.body.cols = oldChannelsCols;
               }
            }

            var selected;
            function selectMenu(item) {
               if (selected != undefined && selected != item) {
                  selected.className = '';
               }
               item.className = 'active';
               selected = item;
            }
         </script>
      </cmscedit:head>
      <body>
      <div id="menu" width="100%">
         <ul>
            <li>
               <a href="#" class="tlink4" onclick="return switchChannelPanel(this);">
                  <fmt:message key="toolbar.hidetree"/>
               </a>
            </li>
            <li>
               <a href="${param.pagepath}" class="tlink1" target="pcontent" onclick="selectMenu(this.parentNode)">
                  <fmt:message key="toolbar.editpage"/>
               </a>
            </li>
            <li>
               <a href="PageEdit.do?number=${param.number}" class="tlink2" target="pcontent"
                  onclick="selectMenu(this.parentNode)">
                  <fmt:message key="toolbar.properties"/>
               </a>
            </li>
            <li>
               <a href="${param.pagepath}?mode=preview" class="tlink3" target="pcontent"
                  onclick="selectMenu(this.parentNode)">
                  <fmt:message key="toolbar.preview"/>
               </a>
            </li>
            <mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">
               <mm:hasrank minvalue="administrator">
                  <mm:haspage page="/editors/workflow/">
                     <li>
                        <a href="../workflow/publish.jsp?number=${param.number}" class="tlink5" target="pcontent"
                           onclick="selectMenu(this.parentNode)">
                           <fmt:message key="toolbar.publish"/>
                        </a>
                     </li>
                  </mm:haspage>
               </mm:hasrank>
            </mm:cloud>
         </ul>
      </div>
      </body>
   </html:html>
</mm:content>