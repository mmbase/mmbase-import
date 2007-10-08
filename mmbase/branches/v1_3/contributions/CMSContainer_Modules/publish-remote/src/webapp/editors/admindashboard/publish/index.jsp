<%@include file="../../publish-remote/globals.jsp"%>

<div class="editor">
     <div class="ruler_green"><div><fmt:message key="admindashboard.publish.header" /></div></div>
</div>
<div class="editor dashboard">
	<iframe src="publish/queue.jsp" frameborder="0" scrolling="no"></iframe>
	<iframe src="publish/published.jsp" frameborder="0" scrolling="no"></iframe>
	<iframe src="publish/failed.jsp" frameborder="0" scrolling="no"></iframe>
</div>