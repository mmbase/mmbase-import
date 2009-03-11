<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="channeldelete.title">
   <script src="content.js" type="text/javascript"></script>
</cmscedit:head>
<body>
   <mm:cloud jspvar="cloud" rank="basic user" method='http'>
      <mm:import externid="number" id="parentchannel" jspvar="parentchannel" vartype="Integer" from="parameters" required="true" />

<div class="tabs">
    <div class="tab_active">
        <div class="body">
            <div>
                <a href="#"><fmt:message key="channeldelete.title" /></a>
            </div>
        </div>
    </div>
</div>

<div class="editor">
   <div class="body">
      <c:set var="relcount1" value="0"/>
      <c:set var="relcount2" value="0"/>
      
      <mm:list nodes="$parentchannel" path="contentchannel,creationrel,contentelement"> 
         <mm:field name="contentelement.number" id="cenumber" write="false"/>
         
         <mm:list nodes="$cenumber" path="contentelement,contentrel,contentchannel">
            <mm:first>
               <mm:size id="relcount" write="false"/>
               <c:choose>
                  <c:when test="${relcount gt 1}">
                     <c:set var="relcount2" value="${relcount2 + 1}"/>
                  </c:when>
                  <c:otherwise>
                     <c:set var="relcount1" value="${relcount1 + 1}"/>
                  </c:otherwise>
               </c:choose>
            </mm:first>
         </mm:list>
         
      </mm:list>

      <p>
         <fmt:message key="channeldelete.warning">
             <fmt:param><b><mm:node number="$parentchannel"><mm:field name="name" /></mm:node></b></fmt:param>
         </fmt:message>
         <ul>
            <li>
               <fmt:message key="channeldelete.message.1">
                  <fmt:param >${relcount1}</fmt:param>
               </fmt:message>
            </li>
            <c:if test="${relcount2 ne 0}">
               <li>
                  <fmt:message key="channeldelete.message.2">
                     <fmt:param >${relcount2}</fmt:param>
                  </fmt:message>            
               </li>
            </c:if>
         </ul>
         
         <br />
         <mm:node number="$parentchannel">
         	<mm:countrelations role="childrel" searchdir="destination" id="childrelSize" write="false"/>
			<mm:compare referid="childrelSize" value="0" inverse="true">
				<fmt:message key="channeldelete.warning.subchannels" />
			</mm:compare>
         </mm:node>
      </p>
      <p>
         <fmt:message key="channeldelete.choice" />
         <table>
         <tbody class="hover">
         <tr>
            <td style="width: 16px;">
               <a class="channeldelete" href="javascript:document.forms['deleteAllForm'].remove.value='delete';document.forms['deleteAllForm'].submit();">
               <img src="../gfx/icons/delete.png" width="16" height="16" alt="<fmt:message key="channeldelete.remove" />" /></a>
            </td>
            <td style="width: 130px;">
               <b><fmt:message key="channeldelete.remove.message" /></b>
            </td>
            <td>
               <fmt:message key="channeldelete.remove.message.text" />
            </td>
         </tr>
         
     <c:if test="${relcount2 ne 0}">
         <tr>
           <td>
              <a class="channeldelete" href="javascript:document.forms['deleteAllForm'].remove.value='move';document.forms['deleteAllForm'].submit();">
              <img src="../gfx/icons/arrow_right.png" width="16" height="16" alt="<fmt:message key="channeldelete.move" />" /></a>
           </td>
           <td>
              <b><fmt:message key="channeldelete.move.message" /></b>
            </td>
            <td>
               <fmt:message key="channeldelete.move.message.text" />
            </td>
         </tr>
     </c:if>
         <tr>
            <td>
               <a class="channeldelete" href="javascript:document.forms['deleteAllForm'].remove.value='cancel';document.forms['deleteAllForm'].submit();">
               <img src="../gfx/icons/arrow_undo.png" width="16" height="16" alt="<fmt:message key="channeldelete.cancel" />" /></a>
            </td>
            <td>
               <b><fmt:message key="channeldelete.cancel.message" /></b>
            </td>
            <td> 
            </td>
         </tr>
         </tbody>
         </table> 
      </p>
      
      <form action="ChannelDelete.do" method="post" onsubmit="return unlinkAll();" name="deleteAllForm">
         <input type="hidden" name="remove" value="" /> 
         <input type="hidden" name="number" value="<mm:write referid="parentchannel"/>" /> 
      </form>
      <div style="clear:both; height:10px;"></div>

      </div>
      <div class="editor">
      <br />
      <div class="ruler_green"><div><fmt:message key="channeldelete.content" /></div></div>

      <mm:import id="lastotype" />

      <mm:node number="$parentchannel">
         <mm:relatednodescontainer path="creationrel,contentelement" searchdirs="source" element="contentelement">
            <mm:sortorder field="contentelement.otype" direction="up" />
            <mm:sortorder field="contentelement.title" direction="up" />

            <mm:listnodes jspvar="node">
               <mm:field name="otype" write="false" id="otype" />
               <mm:field name="number" write="false" id="number" />

               <mm:compare referid="lastotype" value="" inverse="true">
                  </tr>
               </mm:compare>
               <mm:compare referid="otype" referid2="lastotype" inverse="true">
                  <mm:compare referid="lastotype" value="" inverse="true">
                     </table>
                  </mm:compare>

                  <mm:node referid="otype">
                     <br />
                     <fmt:message key="recyclebin.type">
                        <fmt:param>
                           <mm:field name="name" id="nodename">
                              <mm:nodeinfo nodetype="$nodename" type="guitype" />
                           </mm:field>
                        </fmt:param>
                     </fmt:message>
                  </mm:node>
                  <mm:import id="lastotype" reset="true"><mm:write referid="otype" /></mm:import>

                  <table class="listcontent">
               </mm:compare>

               <tr class="itemrow" >
                  <td><mm:field name="number" /></td>
                  <td nowrap>
                     <a href="javascript:info('<mm:field name="number" />');">
                        <img src="../gfx/icons/info.png" width="16" height="16" alt="<fmt:message key="channeldelete.info" />" />
                     </a>
                     <a href="javascript:unpublish('<mm:write referid="parentchannel" />','<mm:field name="number" />');">
                        <img src="../gfx/icons/delete.png" width="16" height="16" alt="<fmt:message key="channeldelete.unlink" />" />
                     </a>
                  </td>
                  <td width="100%"><mm:field name="title" /></td>

               <mm:last>
               <mm:compare referid="lastotype" value="" inverse="true">
                     </tr>
                  </table>
               </mm:compare>
               </mm:last>
            </mm:listnodes>
         </mm:relatednodescontainer>
      </mm:node>

   </div>
   <div class="side_block_end"></div>
</div>   

</mm:cloud>

   </body>
   </html:html>
</mm:content>
