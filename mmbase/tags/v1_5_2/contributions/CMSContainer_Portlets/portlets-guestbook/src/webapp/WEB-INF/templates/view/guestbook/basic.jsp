<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<mm:cloud>
	<mm:import externid="elementId" required="true" from="request" />
	<mm:node number="${elementId}" notfound="skip">
		<h3><mm:field name="title" /></h3>
		<mm:field name="body"><mm:isnotempty><p class="body"><mm:write /></p></mm:isnotempty></mm:field>
	</mm:node>
	<form name="<portlet:namespace />form" method="post"
		action="<cmsc:actionURL><cmsc:param name="action" value="edit"/></cmsc:actionURL>">
		<table>
			<tr>
				<td>
					<fmt:message key="view.name" />:
				</td>
				<td>
					<input type="text" name="name" />
				</td>
			</tr>
			<tr>
				<td>
                    <fmt:message key="view.email" />:
                </td>
                <td>
                    <input type="text" name="email" />
                </td>
            </tr>
			<tr>
				<td>
					<fmt:message key="view.title" />:
				</td>
				<td>
					<input type="text" name="title" />
				</td>
			</tr>
			<tr>
				<td>
					<fmt:message key="view.body" />:
				</td>
				<td>
					<textarea name="body"></textarea>
				</td>
			</tr>
			<c:if test="${!empty usevalidation}">
			<tr>
				<td>
					<fmt:message key="view.validation"/>
				</td>
				<td>
					<img src="<cmsc:staticurl page='/jcaptcha'/>"> <br/>
					<input type="text" name="j_captcha_response">
				</td>
			</tr>
			</c:if>
			<tr>
				<td colspan="2">
					<input type="submit" name="nieuw" value="<fmt:message key="view.submit" />"/>
				</td>
			</tr>
		</table>
	</form>
	<br/>
	<mm:node number="${elementId}" notfound="skip">
		<c:set var="elementsPerPage" >
			<mm:field name="pagesize"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
		</c:set>
		<c:if test="${elementsPerPage == -1}">
			<c:set var="elementsPerPage" value="50"/>
		</c:if>
		<mm:relatednodescontainer type="guestmessage" role="posrel">
			<c:set var="totalElements">
				<mm:size id="totalitems"/>
			</c:set>
			<cmsc:pager maxPageItems="${elementsPerPage}" 
					items="${totalElements}"
					index="center" maxIndexPages="${10}"
					export="offset,currentPage=pageNumber">
				<mm:relatednodes offset="${offset}" max="${offset + elementsPerPage}">
					<mm:field name="title"><mm:isnotempty><strong><mm:write /></strong></mm:isnotempty></mm:field>
					<mm:field name="body"><mm:isnotempty><p class="body"><mm:write /></p></mm:isnotempty></mm:field>
					<mm:field name="creationdate">
                        <mm:locale language="client">    
                        	<cmsc:dateformat displaytime="true" />                    			
                		</mm:locale>&nbsp;&nbsp;
                    </mm:field><i><mm:field name="name" /></i><br/>
					<br />
				</mm:relatednodes>
				<%@include file="pagerindex.jsp" %>
			</cmsc:pager>
		</mm:relatednodescontainer>
	</mm:node>
</mm:cloud>

