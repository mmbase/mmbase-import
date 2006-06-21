<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<mm:cloud jspvar="cloud" rank="basic user" method='http'>
   <mm:import externid="objectnumber" vartype="Integer" required="true"/>
   <mm:node number="$objectnumber">
         <head>
            <title><fmt:message key="showcitem.title"><fmt:param><mm:field name="title" /></fmt:param></fmt:message></title>
            <link href="../style.css" type="text/css" rel="stylesheet" />
         </head>
         <body>
         	<h1><mm:nodeinfo type="guitype"/>: <mm:field name="title" /></h1>
            <table class="listcontent">
               <mm:field name="number">
               <tr>
                  <td><mm:fieldinfo type="guiname" /></td>
                  <td><mm:fieldinfo type="guivalue" /></td>
               </tr>
               </mm:field> 
               <mm:fieldlist nodetype="contentelement" type="edit">
               <tr>
                  <td><mm:fieldinfo type="guiname" /></td>
                  <td><mm:fieldinfo type="guivalue" /></td>
               </tr>
               </mm:fieldlist> 
            </table>
         
            <table class="listcontent">
               <tr>
                  <td>
                     <fmt:message key="showchannels.linked">
                     	<fmt:param><mm:write referid="objectnumber" /></fmt:param>
                     </fmt:message>
                     <table>
                        <mm:relatednodes type="contentchannel" role="contentrel" id="contentrels">
                           <tr>
                              <td>
                                 <b><mm:field name="path" /></b>
                              </td>
                           </tr>
                        </mm:relatednodes>
                        <mm:isempty referid="contentrels"  >
                           <tr>
                              <td>
                                 <b><fmt:message key="showchannels.notlinked" /></b>
                              </td>
                           </tr>
                        </mm:isempty>
                     </table>
                  </td>
               </tr>
               <tr>
                  <td>
                     <hr />
                     <fmt:message key="showchannels.created">
                     	<fmt:param><mm:write referid="objectnumber" /></fmt:param>
                     </fmt:message>
                  </td>
               </tr>
               <mm:relatednodes type="contentchannel" role="creationrel" id="creationrels">
                  <tr>
                     <td>
                        <b><mm:field name="path" /></b>
                     </td>
                  </tr>
               </mm:relatednodes>
               <mm:isempty referid="creationrels"  >
                  <tr>
                     <td>
                        <b><fmt:message key="showchannels.notcreated" /></b>
                     </td>
                  </tr>
               </mm:isempty>
            </table>
            <br />
            <a href="#" onClick="window.close()" ><fmt:message key="showitem.close" /></a>
         </body>
   </mm:node>
</mm:cloud>
</html:html>
</mm:content>