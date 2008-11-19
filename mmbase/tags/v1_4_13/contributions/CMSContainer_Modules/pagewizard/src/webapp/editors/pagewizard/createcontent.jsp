<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="pagewizard.title">
	<script>
		function pickContent(type) {
			var form = document.forms["CreateContentForm"];
			form["contentType"].value = type;
			form.submit();
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
			
	            <h1><fmt:message key="pagewizard.title" /></h1>
				<p>
					<fmt:message key="pagewizard.createcontent.selectcontenttype" />:
					<html:form action="/editors/pagewizard/CreateContentAction">
				        <html:hidden property="creation"/>
				        <html:hidden property="contentType"/>
				        <html:hidden property="returnUrl"/>
				        
				        <ul>
							<c:forEach items="${typesList}" var="type">
		                        <li><a href="javascript:pickContent('${type.value}')">${type.label}</a></li>
							</c:forEach>
						</ul>
					</html:form>
				</p>
			</div>
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</html:html>
</mm:content>