<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ page import="java.util.*"%>
<%@ page import="com.finalist.newsletter.domain.Newsletter"%>
<%@ page import="com.finalist.newsletter.domain.Tag"%>
<html>
<h1>welcome</h1>
<table border="1">
<form action="" name="subscription">
<tr><td>title</td><td>tag</td><td>action</td></tr>
<%	List<Newsletter> list = (List<Newsletter>)request.getAttribute("newsletterList");
	Iterator it = list.iterator();
         for(int i=0;i<list.size();i++)
         {
        	 Newsletter newsletter = (Newsletter) it.next();
        	 String title = newsletter.getTitle();
			 List<Tag> tags = newsletter.getTags();
			 %>
			 <tr>
			 <td>
			 <%=title%>
			 </td>
			 <td>
			 <%  Iterator tagit = tags.iterator(); 
				  for(int j=0;j<tags.size();j++)
				{
					  Tag tag = (Tag)tagit.next();
					  String name = tag.getName();
			 %>
			 <c:set var="name">
			 <%=name%>
			 </c:set>
			 ${name}
			<cmsc:checkbox var="allowednewsletters" value="${name}"/>
			<input class="checkbox" type="checkbox" value="tagname1" name="allowednewsletters" style="width: 15px;" id="status_123" onclick="addOrRemoveTag('123,'t123',this)"/>
			tagname2
			<input class="checkbox" type="checkbox" value="tagname2" name="allowednewsletters" style="width: 15px;"/>
			 <%}%>
			 </td>
			 <td>
		
<SCRIPT LANGUAGE="JavaScript">
<!--
	function addOrRemoveTag(newsletterId,tagId,box){
		alert(v);
		alert(box.checked);
		new Ajax.Request('/cmsc-demo-staging/editors/newsletter/good.jsp', {
		method: 'get',
		parameters: {newsletterId: newsletterId, tagId:tagId, select: box.checked}
  });
	}
//-->
</SCRIPT>
			 </td>
			</tr>
			 <%}%>
</form>
</table>
</html>