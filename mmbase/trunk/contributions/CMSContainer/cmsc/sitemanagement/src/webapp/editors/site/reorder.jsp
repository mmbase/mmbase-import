<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@include file="globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="pagereorder.title">
  <script type="text/javascript" src="../repository/reorder.js"></script>
  <style type="text/css">
    input.button { width : 100; }
  </style>
</cmscedit:head>
<body onload="fillHidden();alphaImages();">
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
   <mm:import externid="parent" jspvar="parent"  from="parameters"/>
<mm:node referid="parent" jspvar="node">

   <div class="tabs">
      <div class="tab_active">
         <div class="body">
            <div>
               <a name="activetab"><fmt:message key="pagereorder.title" /></a>
            </div>
         </div>
      </div>
   </div>
   <div class="editor">

      <div class="body">
         <p>
            <fmt:message key="pagereorder.reorder" /> <mm:field name="path" />
         </p>

<mm:relatednodescontainer path="navrel,page"
  searchdirs="destination" element="page">
  <mm:sortorder field="navrel.pos" direction="up" />
  
  <script type='text/javascript'>
      var values = new Array(<mm:size />);
  <mm:listnodes>
		values[<mm:index/> - 1] = <mm:field name="number"/>;
  </mm:listnodes>
  </script>
  <html:form action="/editors/site/ReorderAction">
   
   <c:if test="${param.returnurl != null}">
      <input type="hidden" name="returnurl" value="${param.returnurl}"/>
   </c:if>
   
	<table>
	  <tr>
	    <td width='300'>
	<select size='20' style="width:100%" name="channels">
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
	<html:submit styleClass="button"><fmt:message key="pagereorder.submit" /></html:submit>
	<html:cancel styleClass="button"><fmt:message key="pagereorder.cancel" /></html:cancel>
  </html:form>
  
</mm:relatednodescontainer>
   </div>
   </div>
</mm:node>
</mm:cloud>

</body>
</html:html>
</mm:content>