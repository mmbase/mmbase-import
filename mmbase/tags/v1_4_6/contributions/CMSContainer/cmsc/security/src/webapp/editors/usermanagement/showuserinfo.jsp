<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
   <mm:import externid="objectnumber" vartype="Integer" required="true"/>
   <mm:node number="$objectnumber">
   <c:set var="showitemTitle"><fmt:message key="user.title"><fmt:param><mm:field name="username" /></fmt:param></fmt:message></c:set>
<cmscedit:head title="${showitemTitle}" titleMode="plain"/>
<body>
	<cmscedit:sideblock title="${showitemTitle}" titleMode="plain"
		 titleClass="side_block_green" titleStyle="width: 100%">         
            <table class="listcontent">
               <tr>
                  <td><fmt:message key="user.account"/></td>
                  <td><mm:field name="username" /></td>
               </tr>             
               <mm:relatednodes type="mmbaseranks" role="rank" orderby="name">
                  <tr>
                     <td>
                        <fmt:message key="user.rank"/>
                     </td>
                     <td>
                        <mm:field name="name"/>
                     </td>
                  </tr>
               </mm:relatednodes>   
               </table>
               
               <table>
               <tr>
                  <td>
                     <hr />
                     <fmt:message key="showitem.groups"/>
                  </td>
               </tr>                       
               <mm:relatednodes type="mmbasegroups" role="contains" orderby="name">
               <tr>
                  <td>
                     <b><mm:field name="name"/></b>
                  </td>
               </tr>
               </mm:relatednodes>                            
            </table>
            <br />
			<ul class="shortcuts">
               <li class="close">
	               <a href="#" onClick="window.close()"><fmt:message key="showitem.close" /></a>
				</li>
			</ul>
	</cmscedit:sideblock>            
   </body>
   </mm:node>
</mm:cloud>
</html:html>
</mm:content>