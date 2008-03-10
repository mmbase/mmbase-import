<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="egemmail.title">
  <script src="../repository/search.js"type="text/javascript"></script>
  <script type="text/javascript">  
    function doForward(to) {
      var elem = document.getElementById("exportForm");
      if (elem != null) {
        elem.forward.value = to;
        elem.submit();
        return false;
      }
      return true;
    }
    
    function doChangePage(newPage) {
      var elem = document.getElementById('exportForm');
      if (elem != null) {
        elem.page.value = newPage;
        return doForward('search');
      }
      return false;
    }
  </script>
</cmscedit:head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="egemmail.title" /></a>
                </div>
            </div>
        </div>
    </div>
<mm:cloud>
	<div class="editor">
		<div class="body">
            <html:form action="/editors/egemmail/EgemSearchAction">
                <label><fmt:message key="egemmail.field.title" />:</label>
				<html:text property="title"/><br/>
				<label><fmt:message key="egemmail.field.keywords" />:</label>
				<html:text property="keywords"/><br/>
				<label><fmt:message key="egemmail.field.author" />:</label>
 				<html:select property="author">
					<html:option value=""><fmt:message key="egemmail.all_users" /></html:option>
 					<mm:listnodes type="user" orderby="username">
						<c:set var="username"><mm:field name="username"/></c:set>
						<c:if test="${username != 'anonymous'}">
                     <mm:field name="username" id="useraccount" write="false"/>
                     <html:option value="${useraccount}"> <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" /> </html:option>
	 					</c:if>
					</mm:listnodes>
				</html:select><br/>
				<html:checkbox property="limitToLastWeek"><fmt:message key="egemmail.field.lastWeek" /></html:checkbox><br/>
				<html:checkbox property="selectResults"><fmt:message key="egemmail.field.selectResults" /></html:checkbox><br/>
				<br/>
				<html:submit><fmt:message key="egemmail.button.search" /></html:submit>
			</html:form>

            <mm:present referid="results">
            <html:form action="/editors/egemmail/EgemExportAction" styleId="exportForm">
                <html:hidden property="title" />
                <html:hidden property="keywords" />
                <html:hidden property="author" />
                <html:hidden property="page" />
                <html:hidden property="forward" />
                <html:hidden property="limitToLastWeek" />
				<mm:list referid="results" max="${resultsPerPage}" offset="${offset*resultsPerPage}">
				    <mm:first>
				        <egem:paging offset="${offset}" resultsPerPage="${resultsPerPage}" totalNumberOfResults="${totalNumberOfResults}" />
				        <table>
				            <thead>
				             <tr>
				                 <th></th>
				                 <th><fmt:message key="egemmail.field.title" /></th>
				                 <th><fmt:message key="egemmail.field.type" /></th>
				                 <th><fmt:message key="egemmail.field.author" /></th>
				                 <th><fmt:message key="egemmail.field.number" /></th>
				             </tr>
				         </thead>
				     </mm:first>
				     <tbody>
				         <tr <mm:even inverse="true">class="swap"</mm:even>>
				             <mm:field name="number" jspvar="number" write="false"/>
                             <c:choose>
                                <c:when test="${EgemExportForm.selectedNodes[number]}">
				                    <td><input type="checkbox" name="export_${number}" checked="checked"/></td>
				                </c:when>
				                <c:otherwise>
				                    <td><input type="checkbox" name="export_${number}"/></td>
				                </c:otherwise>
				                </c:choose>
				             <td>
				                <mm:field jspvar="title" write="false" name="title" />
				                <c:if test="${fn:length(title) > 50}">
				                    <c:set var="title">${fn:substring(title,0,49)}...</c:set>
				                </c:if>
				                    ${title}
				                </td>
				                <td><mm:nodeinfo type="guitype"/></td>
				                <td width="50" style="white-space: nowrap;">
				                    <mm:field name="lastmodifier" jspvar="lastmodifier" write="false"/>
				                    <mm:listnodes type="user" constraints="username = '${lastmodifier}'">
				                        <c:set var="lastmodifierFull">
				                            <mm:field name="firstname" /> <mm:field name="prefix" /> <mm:field name="surname" />
				                        </c:set>
				                        <c:if test="${lastmodifierFull != ''}">
				                            <c:set var="lastmodifier" value="${lastmodifierFull}"/>
				                        </c:if>
				                    </mm:listnodes>
				                    ${lastmodifier}
				                </td>
				                <td>${number}</td>
				         </tr>
				     </tbody>
				     <mm:last>
				            <tfoot>
				                <tr>
				                    <td><input type="checkbox" onChange="selectAll(this.checked, 'exportForm', 'export_');" value="on" name="selectall"/></td>
				                </tr>
				            </tfoot>
				        </table>
                        <egem:paging offset="${offset}" resultsPerPage="${resultsPerPage}" totalNumberOfResults="${totalNumberOfResults}" />				        
				        <html:submit onclick="return doForward('export');"><fmt:message key="egemmail.button.export" /></html:submit>
				     </mm:last>
				 </mm:list>
            </html:form>
            </mm:present>
		</div>
		<div class="side_block_end"></div>
	</div>	
</mm:cloud>
</body>
</html:html>
</mm:content>