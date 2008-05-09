<%@include file="/WEB-INF/templates/portletglobals.jsp"%>
<cmsc:portletmode name="view">
	<li>
</cmsc:portletmode>

<mm:content type="text/html" encoding="UTF-8">
	<mm:cloud method="asis">
		<mm:node number="${elementId}">
			<cmsc:renderURL page="${page}" window="${window}" var="renderUrl">
				<cmsc:param name="elementId" value="${elementId}" />
			</cmsc:renderURL>
			<a href="${renderUrl}">
				<h4><mm:field name="starttime" id="start">
					<mm:time time="$start" format="EEEE d MMMM yyyy - H.mm " /> uur
				</mm:field> <br />
				<mm:field name="title" /></h4>
			</a>
			<p class="location"><mm:field name="location" /></p>
			<p class="bodytext"><mm:field jspvar="intro" name="intro" escape="none" /> <c:if
				test="${fn:length(intro) == 0}">
				<mm:field jspvar="body" name="body" escape="none" write="false" />
				<cmsc:removehtml var="cleanbody" maxlength="300" html="${body}" />
					${cleanbody}
				</c:if></p>
		</mm:node>
	</mm:cloud>
</mm:content>

<cmsc:portletmode name="view">
	</li>
</cmsc:portletmode>
