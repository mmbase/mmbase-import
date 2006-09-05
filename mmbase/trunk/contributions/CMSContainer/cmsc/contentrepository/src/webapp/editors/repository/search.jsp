<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
   <head>
      <link href="../css/main.css" type="text/css" rel="stylesheet"/>
      <title><fmt:message key="search.title" /></title>
      <script src="search.js"type="text/javascript" ></script>
      <script src="content.js"type="text/javascript" ></script>
      <script src="../utils/window.js" type="text/javascript"></script>
      <script src="../utils/rowhover.js" type="text/javascript"></script>
	  <script type="text/javascript" src="../utils/transparent_png.js" ></script>
   </head>
   <body>


<%@ page import="com.finalist.cmsc.repository.ContentElementUtil,
                 com.finalist.cmsc.repository.RepositoryUtil,
                 java.util.ArrayList"%>
<mm:import id="searchinit"><c:url value='/editors/repository/SearchInitAction.do'/></mm:import>
<mm:import externid="action">search</mm:import><%-- either: search, link, of select --%>
<mm:import externid="mode" id="mode">basic</mm:import>
<mm:import externid="returnurl"/>
<mm:import externid="linktochannel"/>
<mm:import externid="parentchannel" jspvar="parentchannel"/>
<mm:import externid="contenttypes" jspvar="contenttypes"><%= ContentElementUtil.CONTENTELEMENT %></mm:import>
<mm:import externid="results" jspvar="nodeList" vartype="List" />
<mm:import externid="offset" jspvar="offset" vartype="Integer">0</mm:import>
<mm:import externid="resultCount" jspvar="resultCount" vartype="Integer">0</mm:import>

<mm:cloud jspvar="cloud" loginpage="../../editors/login.jsp">

   <div class="content">
      <div class="tabs">
         <mm:compare referid="mode" value="basic">
            <div class="tab_active">
         </mm:compare>
         <mm:compare referid="mode" value="basic" inverse="true">
             <div class="tab">
         </mm:compare>
            <div class="body">
               <div>
                  <a href="#" onClick="selectTab('basic');"><fmt:message key="search.simple.search" /></a>
               </div>
            </div>
         </div>
         <mm:compare referid="mode" value="advanced">
            <div class="tab_active">
         </mm:compare>
         <mm:compare referid="mode" value="advanced" inverse="true">
            <div class="tab">
         </mm:compare>
            <div class="body">
               <div>
                  <a href="#" onClick="selectTab('advanced');"><fmt:message key="search.advanced.search" /></a>
               </div>
            </div>
         </div>
      </div>
   </div>
   <div class="editor">
   <br />
         <%-- If we want to link content: --%>
         <mm:compare referid="action" value="link">
            <div class="ruler_red"><div><fmt:message key="searchform.link.title"/></div></div>
            <mm:notpresent referid="results">
               <fmt:message key="searchform.link.text.step1" ><fmt:param ><mm:node number="${linktochannel}"> <mm:field name="name"/></mm:node></fmt:param></fmt:message>
            </mm:notpresent>
            <mm:present referid="results">
               <fmt:message key="searchform.link.text.step2" ><fmt:param ><mm:node number="${linktochannel}"> <mm:field name="name"/></mm:node></fmt:param></fmt:message>
            </mm:present>
            <br />
            <br />
            <mm:present referid="returnurl">
               <a href="<mm:url page="${returnurl}"/>" title="<fmt:message key="locate.back" />" class="button"><fmt:message key="locate.back" /></a>
            </mm:present>
            <br />
            <br />
            <hr />
         </mm:compare>

      <html:form action="/editors/repository/SearchAction" method="post">
         <html:hidden property="action" value="${action}"/>
         <html:hidden property="mode"/>
         <html:hidden property="search" value="true"/>
         <html:hidden property="linktochannel"/>
         <html:hidden property="offset"/>
         <html:hidden property="order"/>
         <html:hidden property="direction"/>
         <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>

         <table>
            <tr>
               <td><fmt:message key="searchform.title" /></td>
               <td colspan="3"><html:text property="title" style="width:200px"/></td>
               <td><fmt:message key="searchform.contenttype" /></td>
               <td>
                  <html:select property="contenttypes" onchange="selectContenttype('${searchinit}');" >
                     <html:option value="contentelement">&lt;<fmt:message key="searchform.contenttypes.all" />&gt;</html:option>
                     <html:optionsCollection name="typesList" value="value" label="label"/>
                  </html:select>
                  <mm:compare referid="mode" value="advanced" inverse="true">
                     <input type="submit" class="button" name="submitButton" onclick="setOffset(0);" value="<fmt:message key="searchform.submit" />"/>
                  </mm:compare>
               </td>
            </tr>

            <mm:compare referid="mode" value= "advanced">
               <tr>
                  <td></td>
                  <td><b><fmt:message key="searchform.dates" /></b></td>
                  <td></td>
                  <td><b><fmt:message key="searchform.users" /></b></td>
                  <td></td>
                  <td><b>
                     <mm:compare referid="contenttypes" value="contentelement" inverse="true">
                        <fmt:message key="searchform.searchfor">
                           <fmt:param><mm:nodeinfo nodetype="${contenttypes}" type="guitype"/></fmt:param>
                        </fmt:message>
                     </mm:compare>
                     </b>
                  </td>
               </tr>
               <tr valign="top">
                  <td><fmt:message key="searchform.creationdate" /></td>
                  <td>
                     <html:select property="creationdate" size="1">
                        <html:option value="0"> - </html:option>
                        <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                        <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                        <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                        <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                        <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                     </html:select>
                  </td>
                  <td><fmt:message key="searchform.personal" /></td>
                  <td>
                     <html:select property="personal" size="1">
                        <html:option value=""> - </html:option>
                        <html:option value="lastmodifier"><fmt:message key="searchform.personal.lastmodifier" /></html:option>
                        <html:option value="author"><fmt:message key="searchform.personal.author" /></html:option>
                     </html:select>
                  </td>
                  <td rowspan="5">
                  <% ArrayList fields = new ArrayList(); %>
                     <mm:compare referid="contenttypes" value="contentelement" inverse="true">
                        <table>
                           <mm:fieldlist nodetype="${contenttypes}">
                              <!-- check if the field is from contentelement -->
                              <% boolean showField = true; %>
                              <mm:fieldinfo type="name" id="fname">
                                  <mm:fieldlist nodetype="contentelement">
                                      <mm:fieldinfo type="name" id="cefname">
                                         <mm:compare referid="fname" referid2="cefname">
                                            <% showField=false; %>
                                         </mm:compare>
                                      </mm:fieldinfo>
                                  </mm:fieldlist>
                              </mm:fieldinfo>
                              <% if (showField) { %>
                                 <tr rowspan="5">
                                    <td height="22">
                                       <mm:fieldinfo type="guiname" jspvar="guiname"/>:
                                       <mm:fieldinfo type="name" jspvar="name" write="false">
                                          <% fields.add(contenttypes + "." + name); %>
                                       </mm:fieldinfo>
                                 </tr>
                              <% } %>
                           </mm:fieldlist>
                        </table>
                     </mm:compare>
                  </td>
                  <td rowspan="5">
                     <mm:compare referid="contenttypes" value="contentelement" inverse="true">
                        <table>
                           <% for (int i = 0; i < fields.size(); i++) {
                              String field = (String) fields.get(i); %>
                              <tr>
                                 <td>
                                    <input type="text" name="<%= field %>" value="<%= (request.getParameter(field) == null)? "" :request.getParameter(field) %>" />
                                 </td>
                              </tr>
                           <% } %>
                        </table>
                     </mm:compare>
                  </td>
               </tr>
               <tr>
                  <td><fmt:message key="searchform.lastmodifieddate" /></td>
                  <td>
                     <html:select property="lastmodifieddate" size="1">
                        <html:option value="0"> - </html:option>
                        <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                        <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                        <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                        <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                        <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                     </html:select>
                  </td>
                  <td>
                     <mm:hasrank minvalue="administrator">
                        <fmt:message key="searchform.useraccount" />
                     </mm:hasrank>
                  </td>
                  <td>
                     <mm:hasrank minvalue="administrator">
                        <html:select property="useraccount" size="1">
                           <html:option value=""> - </html:option>
                            <mm:listnodes type='user' orderby='username'>
                                <mm:field name="username" id="useraccount" write="false"/>
                               <html:option value="${useraccount}"> <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /> </html:option>
                            </mm:listnodes>
                        </html:select>
                     </mm:hasrank>
                  </td>
               </tr>
               <tr>
                  <td><fmt:message key="searchform.publishdate" /></td>
                  <td>
                     <html:select property="publishdate" size="1">
                        <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                        <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                        <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                        <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                        <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                        <html:option value="0"> - </html:option>
                        <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                        <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                        <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                        <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                        <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                     </html:select>
                  </td>
                  <td></td>
                  <td><b><fmt:message key="searchform.other" /></b></td>
               </tr>
               <tr>
                  <td><fmt:message key="searchform.expiredate" /></td>
                  <td>
                     <html:select property="expiredate" size="1">
                        <html:option value="365"><fmt:message key="searchform.futureyear" /></html:option>
                        <html:option value="120"><fmt:message key="searchform.futurequarter" /></html:option>
                        <html:option value="31"><fmt:message key="searchform.futuremonth" /></html:option>
                        <html:option value="7"><fmt:message key="searchform.futureweek" /></html:option>
                        <html:option value="1"><fmt:message key="searchform.futureday" /></html:option>
                        <html:option value="0"> - </html:option>
                        <html:option value="-1"><fmt:message key="searchform.pastday" /></html:option>
                        <html:option value="-7"><fmt:message key="searchform.pastweek" /></html:option>
                        <html:option value="-31"><fmt:message key="searchform.pastmonth" /></html:option>
                        <html:option value="-120"><fmt:message key="searchform.pastquarter" /></html:option>
                        <html:option value="-365"><fmt:message key="searchform.pastyear" /></html:option>
                     </html:select>
                  </td>
                  <td><fmt:message key="searchform.number" /></td>
                  <td><html:text property="objectid"/></td>
               </tr>
               <tr>
                  <td>
                  </td>
                  <td></td>
                  <td>
                     <mm:compare referid="action" value="link">
	                  	<fmt:message key="searchform.select.channel" />
	                 </mm:compare>
                  </td>
                  <td>
                     <mm:compare referid="action" value="link">
                     <html:hidden property="parentchannel" />
                        <input type="text" name="parentchannelpath" value="" disabled value="..."/><br />
                        <a href="#" onClick="window.open('select/SelectorChannel.do', 'select', 'width=300,height=600')"><img src="/nijmegen-webapp/editors/gfx/icons/select.png" alt="<fmt:message key="searchform.select.channel" />"></a>
                        <a href="#" onClick="selectChannel('', '');" ><img src="/nijmegen-webapp/editors/gfx/icons/erase.png" alt="<fmt:message key="searchform.clear.channel.button" />"></a>
                     </mm:compare>
                  </td>
               </tr>
               <tr>
                  <td>
                     <input type="submit" class="button" name="submitButton" onclick="setOffset(0);" value="<fmt:message key="searchform.submit" />"/>
                  </td>
               </tr>
            </mm:compare>
   </html:form>
         </table>
   </div>


   <div class="editor" style="height:500px">
   <div class="body">
         <div class="ruler_green"><div><fmt:message key="searchform.results" /></div></div>



   <%-- Now print if no results --%>
   <mm:isempty referid="results">
      <fmt:message key="searchform.searchpages.nonefound" />
   </mm:isempty>

   <%-- Now print the results --%>
   <mm:node number="<%= RepositoryUtil.ALIAS_TRASH %>">
	   <mm:field id="trashnumber" name="number" write="false"/>
   </mm:node>
   <mm:list referid="results">
      <mm:first>
         <%@include file="searchpages.jsp" %>


            <mm:compare referid="action" value="link" >
                  <form action="LinkToChannelAction.do" name="linkForm">
                     <input type="submit" class="button" value="<fmt:message key="searchform.link.submit" />"/>
            </mm:compare>

          <table>
            <thead>
               <tr>
                  <th>
                     <mm:compare referid="action" value="link" >
                        <input type="hidden" name="channelnumber" value="<mm:write referid="linktochannel"/>" />
                        <input type="hidden" name="channel" value="<mm:write referid="linktochannel"/>" />
                        <mm:present referid="returnurl"><input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/></mm:present>
                        <input type="checkbox" onChange="selectAll(this.checked, 'linkForm', 'link_');" value="on" name="selectall" />
                     </mm:compare>
                  </th>
                  <th><a href="#" class="headerlink" onclick="orderBy('otype');"><fmt:message key="locate.typecolumn" /></a></th>
                  <th><a href="#" class="headerlink" onclick="orderBy('title');" ><fmt:message key="locate.titlecolumn" /></a></th>
                  <th><fmt:message key="locate.creationchannelcolumn" /></th>
                  <th><fmt:message key="locate.authorcolumn" /></th>
                  <th><fmt:message key="locate.lastmodifiedcolumn" /></th>
                  <th><fmt:message key="locate.numbercolumn" /></th>
               </tr>
            </thead>
            <tbody class="hover">
      </mm:first>

      <tr <mm:even inverse="true">class="swap"</mm:even>>
         <td nowrap width="60">
	        <%-- also show the edit icon when we return from an edit wizard! --%>
         	<mm:write referid="action" jspvar="action" write="false"/>
         	<c:if test="${action == 'search' || action == 'save' || action == 'cancel'}">
                <a href="<mm:url page="../WizardInitAction.do">
                                          <mm:param name="objectnumber"><mm:field name="${contenttypes}.number" /></mm:param>
                                          <mm:param name="returnurl" value="<%="../editors/repository/SearchAction.do" + request.getAttribute("geturl")%>" />
                                       </mm:url>">
                   <img src="../gfx/icons/page_edit.png" title="<fmt:message key="searchform.icon.edit.title" />" /></a>
			</c:if>
            <mm:compare referid="action" value="link">
               <input type="checkbox" value="<mm:field name="${contenttypes}.number"/>" name="link_<mm:field name="${contenttypes}.number"/>" onClick="document.forms['linkForm'].elements.selectall.checked=false;"/>
            </mm:compare>
            <mm:compare referid="action" value="select">
               <a href="#" onClick="selectElement('<mm:field name="${contenttypes}.number" />', '<mm:field name="${contenttypes}.title" />', '<cmsc:staticurl page="/content/" /><mm:field name="${contenttypes}.number"/>');">
                   <img src="../gfx/icons/link.png" title="<fmt:message key="searchform.icon.select.title" />" /></a>
            </mm:compare>
            <a href="#" oncLick="showItem(<mm:field name="${contenttypes}.number"/>);" ><img src="../gfx/icons/info.png"  title="<fmt:message key="searchform.icon.info.title" />" /></a>
            <mm:field name="${contenttypes}.number"  write="false" id="nodenumber">
               <a href="<cmsc:contenturl number="${nodenumber}"/>" target="_blanc"><img src="../gfx/icons/preview.png" title="<fmt:message key="searchform.icon.preview.title" />" /></a>
            </mm:field>
         </td>
         <mm:field name="${contenttypes}.number" id="number">
            <mm:node number="${number}">
               <td>
            	  <mm:nodeinfo type="guitype"/>
               </td>
               <td><mm:field name="title"/></td>
               <td width="50">
                  <mm:relatednodes role="creationrel" type="contentchannel">
                     <mm:field name="number" id="creationnumber" write="false"/>
                     <mm:compare referid="trashnumber" referid2="creationnumber">
                     	<fmt:message key="search.trash" />
                     </mm:compare>
                     <mm:compare referid="trashnumber" referid2="creationnumber" inverse="true">
	                     <mm:field name="name"/>
	                 </mm:compare>
                  </mm:relatednodes>
               </td>
               <td width="50"><mm:field name="lastmodifier" /></td>
            </mm:node>
         </mm:field>
         <td width="120"><mm:field name="${contenttypes}.lastmodifieddate"><cmsc:dateformat displaytime="true" /></mm:field></td>
         <td width="60"><mm:field name="${contenttypes}.number"/></td>
      </tr>

      <mm:last>
            </tbody>
         </table>
            <mm:compare referid="linktochannel" value="" inverse="true">
                     <input type="submit" class="button" value="<fmt:message key="searchform.link.submit" />"/>
               </form>
            </mm:compare>
         <%@include file="searchpages.jsp" %>
      </mm:last>
   </mm:list>
   </div>
   </div>
</mm:cloud>

   </body>
</html:html>
</mm:content>