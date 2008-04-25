<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.finalist.newsletter.domain.Newsletter"%>
<%@ page import="com.finalist.newsletter.domain.Tag"%>
<%@ page import="com.finalist.newsletter.domain.Subscription"%>
<html>
<SCRIPT LANGUAGE="JavaScript">
	function addOrRemoveTag(newsletterId,tagId,box){
		new Ajax.Request
				('/cmsc-demo-staging/editors/newsletter/services.jsp', 
					{
					method: 'get',
					parameters: {newsletterId: newsletterId, tagId:tagId, select: box.checked , action: 'modifyTag'}
					}
				);
			}
	
	function modifyFormat(newsletterId,format){
			new Ajax.Request
				('/cmsc-demo-staging/editors/newsletter/services.jsp', 
					{
					method: 'get',
					parameters: {newsletterId: newsletterId, format:format, action: 'modifyFormat'}
					}
				);
			}
	
	function modifyStatus(newsletterId,status,box){
			if("ACTIVE"==status)
		{
				if(box.checked){
				ableCheckBox('pause-'+newsletterId);
				ableCheckBox('format-'+newsletterId);
				ableCheckBox('tag-'+newsletterId);
				}else {
				disableCheckBox('pause-'+newsletterId);
				disableCheckBox('format-'+newsletterId);
				disableChecked('pause-'+newsletterId);
				disableChecked('tag-'+newsletterId);
				disableCheckBox('tag-'+newsletterId);
			}
		}
			new Ajax.Request
				('/cmsc-demo-staging/editors/newsletter/services.jsp', 
					{
					method: 'get',
					parameters: {newsletterId: newsletterId, status:status, select: box.checked ,action: 'modifyStatus'}
					}
				);
			}

	function disableCheckBox(elementName) {
			var obj=document.getElementsByName(elementName)
			for(var i=0;i<obj.length;i++)
			{
			obj[i].disabled = true;
			}
	}
	function ableCheckBox(elementName) {
			var obj=document.getElementsByName(elementName)
			for(var i=0;i<obj.length;i++)
			obj[i].disabled = false;
			}

	function disableChecked(elementName) {
			var obj=document.getElementsByName(elementName)
			for(var i=0;i<obj.length;i++)
			obj[i].checked = false;
			}
	
</SCRIPT>
<h1>LIST</h1>
<table border="1">
<form action="" name="subscription">
<tr><td>title</td><td>tag</td><td>action</td><td>format</td></tr>
<%	List<Subscription> list = (List<Subscription>)request.getAttribute("subscriptionList");
	Iterator it = list.iterator();
         for(int i=0;i<list.size();i++)
         {
        	 Subscription subscription = (Subscription) it.next();
			 Newsletter newsletter = subscription.getNewsletter();
			 String title = newsletter.getTitle();
			 Set<Tag> tags = subscription.getTags();
			 int newsletterId = newsletter.getId();
			 String format = subscription.getMimeType();
			 String status = subscription.getStatus().toString();
			 boolean  isText = false;
			 boolean  isSubscription = false;
			 boolean  isPause = false;
			 boolean  disabledFormat =true;
			 boolean  disabledPause =true;
			 boolean  disabledTag =true;
			
			 if(format!=null){
					 if("text".equals(format)){
						isText = true;
					 }else{
						isText = false;
					 }
			 }
			if(status!=null)
			 {
					 if("ACTIVE".equals(status)){
						isSubscription = true;
						disabledFormat = false;
						disabledPause = false;
						disabledTag = false;
					 }else{
						isSubscription = false;
					 }
					  if("PAUSED".equals(status)){
						isSubscription = true;
						isPause = true;
						disabledFormat = false;
						disabledPause = false;
						disabledTag = false;
					 }else{
						isPause = false;
					 }
			 }
			 %>
			 <tr>
			 <c:set var="newsletterId">
			 <%=newsletterId%>
			 </c:set>
			  <c:set var="isText">
			 <%=isText%>
			 </c:set>
			  <c:set var="isSubscription">
			 <%=isSubscription%>
			 </c:set>
			  <c:set var="isPause">
			 <%=isPause%>
			 </c:set>
			 <c:set var="disabledFormat">
			 <%=disabledFormat%>
			 </c:set>
			 <c:set var="disabledPause">
			 <%=disabledPause%>
			 </c:set>
			 <c:set var="disabledTag">
			 <%=disabledTag%>
			 </c:set>
			 <td>
			 <%=title%>
			 </td>
			 <td>
			 <%  Iterator tagit = tags.iterator(); 
				  for(int j=0;j<tags.size();j++)
				{
					  Tag tag = (Tag)tagit.next();
					  String name = tag.getName();
					  int tagId = tag.getId();
					  boolean select = tag.isSubscription();
			 %>
			 <c:set var="name">
			 <%=name%>
			 </c:set>
			  <c:set var="tagId">
			 <%=tagId%>
			 </c:set>
			 <c:set var="selected">
			 <%=select%>
			 </c:set>
			 ${name}
			<input class="checkbox" type="checkbox" value='${tagId}' name="tag-${newsletterId}" style="width: 15px;" id="tag-${newsletterId}" onclick="addOrRemoveTag(${newsletterId},${tagId},this)" selected="${selected}" <c:if test="${disabledTag}">disabled</c:if> 
			<c:if test="${selected}">checked</c:if> />
			 <%}%>
			 </td>
			  <td>
			 subscription<input class="checkbox" type="checkbox" value="${newsletterId}" name="subscription-${newsletterId}" style="width: 15px;" id="subscription-${newsletterId}" onclick="modifyStatus(${newsletterId},'ACTIVE',this)"   
			 <c:if test="${isSubscription}">checked</c:if>/>
			 pause<input class="checkbox" type="checkbox" value="${newsletterId}" name="pause-${newsletterId}" style="width: 15px;" id="pause-${newsletterId}" onclick="modifyStatus(${newsletterId},'PAUSED',this)" <c:if test="${isPause}">checked</c:if>
			 <c:if test="${disabledPause}">disabled</c:if>/>
			 </td>
			 <td>
			 <select name="format-${newsletterId}" onchange="modifyFormat(${newsletterId},this.value)" <c:if test="${disabledFormat}">disabled</c:if>>
			 <option  name="html" value="html" <c:if test="${!isText}">selected</c:if>>html</option>
			 <option  name="text" value="text" <c:if test="${isText}">selected</c:if>>text</option>${newsletterId}
			 </select>
			 </td>
			</tr>
<%}%>
</form>
</table>
</html>