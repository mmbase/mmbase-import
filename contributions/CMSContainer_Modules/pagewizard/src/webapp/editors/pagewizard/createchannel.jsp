<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="pagewizard.title">
	<script>
		function checkCompleteNew() {
			var form = document.forms["CreateChannelForm"];
			if(form["channelName"].value == "") {
				alert("<fmt:message key="pagewizard.createchannel.error.name" />");
				return false;
			}
			return true;
		}
		function cancel() {
			window.close();
			return false;
		}
		function done() {
			var form = document.forms["CreateChannelForm"];
			window.opener.selectElement(form["channelNumber"].value, form["channelName"].value);
			window.close();
			return false;
		}
	</script>
</cmscedit:head>
<body>
    <div class="tabs">
        <div class="tab_active">
            <div class="body">
                <div>
                    <a href="#"><fmt:message key="pagewizard.title" /></a>
                </div>
            </div>
        </div>
    </div>

	<div class="editor">
		<div class="body">
			<div style="float:left">
				<img src="gfx/wizard.png" width="128" height="128"/>
			</div>
			<div style="float:left">
			
				<html:form action="/editors/pagewizard/CreateChannelAction">
					<html:hidden property="parentNumber"/>
					<html:hidden property="contentType"/>
					<c:choose>
						<c:when test="${CreateChannelForm.channelNumber == 0}">
							<fmt:message key="pagewizard.createchannel.intro" />
							<br/>
							<br/>
							<input type="hidden" name="action" value="new"/>
							<fmt:message key="pagewizard.createchannel.name" />: <html:text property="channelName"/>
							<br/>
							<html:submit styleClass="button" onclick="return checkCompleteNew()"><fmt:message key="pagewizard.createchannel.submit" /></html:submit>
							<html:submit styleClass="button" onclick="return cancel()"><fmt:message key="pagewizard.createchannel.cancel" /></html:submit>
						</c:when>
						<c:otherwise>
							<input type="hidden" name="action" value=""/>
							<html:hidden property="channelNumber"/>
							<html:hidden property="channelName"/>
							<fmt:message key="pagewizard.createchannel.name" />: ${CreateChannelForm.channelName}<br/>
							
							<fmt:message key="pagewizard.createchannel.channelcontent" />:
							<c:url var="returnUrl" value="pagewizard/CreateChannelAction.do">
								<c:param name="contentType" value="${CreateChannelForm.contentType}"/>
								<c:param name="channelNumber" value="${CreateChannelForm.channelNumber}"/>
								<c:param name="channelName" value="${CreateChannelForm.channelName}"/>
							</c:url>
							<c:url var="url" value="CreateContentAction.do">
								<c:param name="creation" value="${CreateChannelForm.channelNumber}"/>
								<c:param name="contentType" value="${CreateChannelForm.contentType}"/>
								<c:param name="returnUrl" value="${returnUrl}"/>
							</c:url>
							<a href="${url}">
								<img src="<cmsc:staticurl page='/editors/gfx/icons/new.png'/>" alt="<fmt:message key="pagewizard.createchannel.newcontent" />"></a> 
							
							<mm:cloud>
								<mm:node number="${CreateChannelForm.channelNumber}">
						            <ul>
						            <mm:relatednodes type="contentelement" role="contentrel">
						            	<li>
						            		<mm:field name="title"/> (<mm:nodeinfo type="guitype"/>)<br/>
						            	</li>
						            </mm:relatednodes>
						           	</ul>
								</mm:node>
							</mm:cloud>
							<html:submit styleClass="button" onclick="return done()"><fmt:message key="pagewizard.createchannel.submit" /></html:submit>
							<html:submit styleClass="button" onclick="return cancel()"><fmt:message key="pagewizard.createchannel.cancel" /></html:submit>
						</c:otherwise>
					</c:choose>
		        </html:form>
		    </div>
	    </div>
	</div>
</body>
</html:html>
</mm:content>
