<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
   <cmscedit:head title="newspubinfo.title">
      <link href="<cmsc:staticurl page='/editors/css/main.css'/>" rel="stylesheet" type="text/css" />
   </cmscedit:head>
   <body>
   <div class="tabs">
      <div class="tab_active">
         <div class="body">
            <div>
               <a href="#"><fmt:message key="newspubinfo.title" /></a>
            </div>
         </div>
      </div>
   </div>
    
   <div class="editor">
      <div class="body">
         <mm:cloud jspvar="cloud" rank="basic user" loginpage="../../login.jsp">
            <mm:node number="${param.objectnumber}">
               <div style="float:left; padding:5px;">
                  <fmt:message key="newspubform.title" />: <b><mm:field name="title"/></b><br/>
                  <fmt:message key="newspubform.description" />: <mm:field name="description"/><br/>
                  <fmt:message key="newspubform.subject" />: <mm:field name="subject"/><br/>
                  <fmt:message key="newspubform.intro" />: <mm:field name="intro"/><br/>
                  <fmt:message key="newspubinfo.status" />
                  <mm:field name="status" write="false" id="status"/>
                  <c:if test="${status eq 'DELIVERED'}">
                     <fmt:message key="newspubinfo.hassend" />
                  </c:if>
                  <c:if test="${status eq 'READY'}">
                     <fmt:message key="newspubinfo.notsend" />
                  </c:if>
                  <br/>
                  <br/>
                  <fmt:bundle basename="cmsc-repository">
                  <mm:field name="creationdate" id="creationdate" write="false"/>
                  <mm:present referid="creationdate">
                     <fmt:message key="secondaryinfo.creationdate"/>: 
                        <mm:write referid="creationdate">
                           <mm:time format="dd-MM-yyyy hh:mm"/>
                        </mm:write><br/>
                  </mm:present>

                  <mm:field name="lastmodifieddate" id="lastmodifieddate" write="false"/>
                  <mm:present referid="lastmodifieddate">
                     <fmt:message key="secondaryinfo.lastmodifier" />: <mm:field name="lastmodifier"/><br/>
                     <fmt:message key="secondaryinfo.lastmodifieddate"/>: 
                     <mm:write referid="lastmodifieddate">
                        <mm:time format="dd-MM-yyyy hh:mm"/>
                     </mm:write><br/>
                  </mm:present>
                  </fmt:bundle>
                  <br/>
               </div>
               <div style="clear:both; float:left">
               <fmt:bundle basename="cmsc-repository">
                  <ul class="shortcuts">
                     <li class="close">
                        <a href="#" onClick="window.close()"><fmt:message key="urlinfo.close" /></a>
                     </li>
                  </ul>
               </fmt:bundle>
               </div>
            </mm:node>
         </mm:cloud>
      </div>
      <div class="side_block_end"></div>
   </div>
   </body>
</html:html>
</mm:content>