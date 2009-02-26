<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="imageinfo.title" />
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="imageinfo.title" /></a>
                </div>
            </div>
        </div>
    </div>

   <div class="editor">
      <div class="body">
         <mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
            <mm:node number="${param.objectnumber}">
               <div style="float:left">
                     <mm:field name="width" jspvar="width" write="false"/>
                     <c:choose>
                        <c:when test="${width gt 430}"><mm:image template="s(430)" jspvar="imageSource" write="false"/>
                           <a href="<mm:image jspvar="imageSourceOriginal" />" title='<mm:field name="description" /> - (<fmt:message key="imageinfo.fullsize"/>)'><img src="${imageSource}" alt="<mm:field name="description" /> - (<fmt:message key="imageinfo.fullsize"/>)" /></a>
                        </c:when>
                        <c:otherwise><mm:image jspvar="imageSource" write="false"/>
                           <img src="${imageSource}" alt="<fmt:message key="imageinfo.originalsize"/> - <mm:field name="description" />" />
                        </c:otherwise>
                     </c:choose>
                     <br/>
                 </div>
                 <div style="float:left; padding:5px;">
                     <h1><mm:field name="filename"/></h1>
                       <fmt:message key="imageinfo.titlefield" />: <b><mm:field name="title"/></b><br/>
                       <fmt:message key="imageinfo.description" />: <mm:field name="description"/><br/>
                       <br/>
                       <fmt:message key="imageinfo.filesize" />: <mm:field name="filesize"/> <fmt:message key="imageinfo.bytes" /><br/>
                       <fmt:message key="imageinfo.width" />: <mm:field name="width"/><br/>
                       <fmt:message key="imageinfo.height" />: <mm:field name="height"/><br/>
                       <fmt:message key="imageinfo.itype" />: <mm:field name="itype"/><br/>
                       <br/>
                       <mm:field name="creationdate" id="creationdate" write="false"/>
                       <mm:present referid="creationdate">
                          <fmt:message key="secondaryinfo.creator" />: <mm:field name="creator"/><br/>
                          <fmt:message key="secondaryinfo.creationdate" />: <mm:write referid="creationdate"><mm:time format="dd-MM-yyyy hh:mm"/></mm:write><br/>
                    </mm:present>

                       <mm:field name="lastmodifieddate" id="lastmodifieddate" write="false"/>
                       <mm:present referid="lastmodifieddate">
                          <fmt:message key="secondaryinfo.lastmodifier" />: <mm:field name="lastmodifier"/><br/>
                          <fmt:message key="secondaryinfo.lastmodifieddate" />: <mm:write referid="lastmodifieddate"><mm:time format="dd-MM-yyyy hh:mm"/></mm:write><br/>
                    </mm:present>
                       <br/>
                     <b><fmt:message key="imageinfo.related" /></b>:<br/>
                     <ul>
                     <mm:relatednodes type="contentelement">
                        <li>
                           <mm:field name="title"/><br/>
                           <fmt:message key="imageinfo.otype" />: <mm:nodeinfo type="guitype"/><br/>
                           <fmt:message key="imageinfo.number" />: <mm:field name="number"/>
                        </li>
                     </mm:relatednodes>
                     <mm:relatednodes type="page">
                        <li>
                           <mm:field name="path"/><br/>
                           <fmt:message key="imageinfo.otype" />: <mm:nodeinfo type="guitype"/><br/>
                           <fmt:message key="imageinfo.number" />: <mm:field name="number"/>
                        </li>
                     </mm:relatednodes>
                       </ul>
               </div>
               <div style="clear:both; float:left">
                  <ul class="shortcuts">
                        <li class="close">
                           <a href="#" onClick="window.close()"><fmt:message key="imageinfo.close" /></a>
                     </li>
                  </ul>
               </div>
            </mm:node>
         </mm:cloud>
      </div>
      <div class="side_block_end"></div>
   </div>   
</body>
</html:html>
</mm:content>               