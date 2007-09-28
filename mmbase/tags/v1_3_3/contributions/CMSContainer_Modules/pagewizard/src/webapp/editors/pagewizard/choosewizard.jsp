<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="pagewizard.title">
	<script>
	function select(wizard) {
		document.location.href="ChooseWizardAction.do?parentPage=${parentPage}&wizard="+wizard;
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
	            <h3><fmt:message key="pagewizard.step1" /></h3>
	            <p>
					<fmt:message key="pagewizard.choosewizard.intro" />:
					<ul>
						<c:forEach items="${definitions}" var="item">
							<li><a href="javascript:select(${item.number})">${item.name} - ${item.description}</a><br/></li>
						</c:forEach>
					</ul>
				</p>
			</div>
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</html:html>
</mm:content>