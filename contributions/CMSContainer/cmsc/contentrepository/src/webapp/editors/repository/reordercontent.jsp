<%@page language="java" contentType="text/html;charset=utf-8" import="com.finalist.tree.*" %>
<%@include file="globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="channelreorder.title">
  <script type="text/javascript" src="reorder.js"></script>
  <style type="text/css">
    input.button { width : 100; }
  </style>
</cmscedit:head>
<body onload="fillHidden();alphaImages();">
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
   <mm:import externid="parent" jspvar="parent" from="parameters"/>
<mm:node referid="parent" jspvar="node">

   <div class="tabs">
      <div class="tab_active">
         <div class="body">
            <div>
               <a name="activetab"><fmt:message key="channelreorder.title" /></a>
            </div>
         </div>
      </div>
   </div>
   <div class="editor">

      <div class="body">
         <p>
            <fmt:message key="channelreorder.reorder" /> <mm:field name="path" />
         </p>

<mm:relatednodescontainer path="contentrel,contentelement"
  searchdirs="destination" element="contentelement">
  <mm:sortorder field="contentrel.pos" direction="down" />
  
  <script type='text/javascript'>
      var values = new Array(<mm:size />);
  <mm:listnodes>
      values[<mm:index/> - 1] = <mm:field name="number"/>;
  </mm:listnodes>
  </script>
  <html:form action="/editors/repository/ReorderAction">
   
   <mm:import externid="returnurl" from="parameters"/>
   <mm:present referid="returnurl">
      <input type="hidden" name="returnurl" value="<mm:write referid="returnurl"/>"/>
   </mm:present>

   <table>
     <tr>
       <td width="300">
   <select size='20' style="width:100%" name="channels" multiple="true">
  <mm:listnodes>
      <option><mm:field name="title"/></option>
  </mm:listnodes>
   </select>
       </td>
       <td style="padding-top:50px"> 
        <img src="../gfx/icons/up.png" onmouseup="moveUp()" alt="" /><br />
        <img src="../gfx/icons/down.png" onmouseup="moveDown()" alt="" />
       </td>
     </tr>
   </table>
   <input type="hidden" name="ids"/>
   <input type="hidden" name="action" value="reorder"/>
   <input type="hidden" name="parent" value="<mm:write referid="parent" />"/>
   <html:submit styleClass="button"><fmt:message key="channelreorder.submit" /></html:submit>
   <html:cancel styleClass="button"><fmt:message key="channelreorder.cancel" /></html:cancel>
  </html:form>
  
</mm:relatednodescontainer>
   </div>
   </div>
</mm:node>
</mm:cloud>

</body>
</html:html>
</mm:content>