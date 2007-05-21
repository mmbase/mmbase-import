<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="pagewizard.title">
	<script type="text/javascript">
	var picking = '';
	var mode = '';
	
	var newContentType = '';
	var newContentChannel = '';

	function selectElement(element, title, url) {
		document.getElementById("div_"+picking).innerHTML = title;
		document.forms["PageWizardForm"]["selected_"+picking].value = element;
	}
	
	function selectChannel(channel, path) {
		if(mode == 'selectchannel') {
			document.getElementById("div_"+picking).innerHTML = path;
			document.forms["PageWizardForm"]["selected_"+picking].value = channel;
		}
		else if(mode == 'newcontent') {
			newContentChannel = channel;
			url = "../../pagewizard/CreateContentAction.do";
			url += "?contentType="+newContentType;
			url += "&creation="+channel;
			openPopupWindow("newContent", 900, 500, url);    
		}
		else if(mode == 'newchannel') {
			newContentChannel = channel;
			url = "../../pagewizard/CreateChannelAction.do";
			url += "?contentType="+newContentType;
			url += "&parentNumber="+channel;
			openPopupWindow("newContentChannel", 900, 500, url);    
		}
	}
	
	function openSelectContentPopup(justClicked) {
		picking = justClicked;
		openPopupWindow('selectcontentelement', 970, 500);
	}

	function openNewContentPopup(justClicked, contentType) {
		picking = justClicked;
		newContentType = contentType;
		mode = 'newcontent';
		openPopupWindow('selectchannel', 340, 400);
	}
	
	function openNewChannelPopup(justClicked, contentType) {
		picking = justClicked;
		newContentType = contentType;
		mode = 'newchannel';
		openPopupWindow('selectchannel', 340, 400);
	}
	
	function openSelectChannelPopup(justClicked) {
		picking = justClicked;
		mode = 'selectchannel';
		openPopupWindow('selectchannel', 340, 400);
	}
	
	function checkComplete() {
		var form = document.forms["PageWizardForm"];
		if(form["pageName"].value == "") {
			alert("<fmt:message key="pagewizard.pickcontent.error.title" />");
			return false;
		}
		else {
			for(i = 0; i < form.elements.length; i++) {
				var name = form.elements[i].name;
				var value = form.elements[i].value;
				if(name.indexOf("selected_") != -1 && (value == undefined || value == "")) {
					alert("<fmt:message key="pagewizard.pickcontent.error.content" />");
					return false;
				}
			}		
		}
		
		return true;
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
	            <h3><fmt:message key="pagewizard.step2" /></h3>
				<h3><fmt:message key="pagewizard.pickcontent.selectedwizard" />: ${PageWizardForm.definition.name}<br/></h3>
				<p>
					${PageWizardForm.definition.description}
				</p>
				<p>
	         	<fmt:message key="pagewizard.pickcontent.intro" />
					<html:form action="/editors/pagewizard/CompleteWizardAction">
				        <html:hidden property="wizard"/>
				        <html:hidden property="parentPage"/>
				        <fmt:message key="pagewizard.pickcontent.pagename" />: <html:text property="pageName"/><br/>
				        
						<c:forEach items="${PageWizardForm.definition.portlets}" var="portlet">
							<c:forEach items="${portlet.choices}" var="choice">
								<input type="hidden" name="selected_${portlet.position}_${choice.parameter}" value=""/>
								<c:choose>
								
									<c:when test="${choice.type == 'content'}">
										<fmt:message key="pagewizard.pickcontent.contentportlet"> 
											<fmt:param>${portlet.contentTypeName}</fmt:param>
											<fmt:param>${portlet.position}</fmt:param>
										</fmt:message>
										<br/>
										
										<fmt:message key="pagewizard.pickcontent.selected" />: 
										<div id="div_${portlet.position}_${choice.parameter}" style="display:inline; color: #555">&lt; <fmt:message key="pagewizard.pickcontent.selectedcontent" /> &gt;</div>
										
										<a href="<c:url value='/editors/repository/select/SelectorChannel.do'><c:param name="role" value="editor"/><c:param name="message"><fmt:message key="pagewizard.pickcontent.selectchannel.newcontent" /></c:param></c:url>"
												target="selectchannel" onclick="openNewContentPopup('${portlet.position}_${choice.parameter}', '${portlet.contentType}')"> 
													<img src="<cmsc:staticurl page='/editors/gfx/icons/new.png'/>" alt="<fmt:message key="pagewizard.pickcontent.new" />"></a> 
										<a href="<c:url value='/editors/repository/select/index.jsp' />"
												target="selectcontentelement" onclick="openSelectContentPopup('${portlet.position}_${choice.parameter}')"> 
													<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="pagewizard.pickcontent.search" />"></a> <br/>
													
										<br/>
									</c:when>
									
									<c:when test="${choice.type == 'contentchannel'}">
										<fmt:message key="pagewizard.pickcontent.channelportlet"> 
											<fmt:param>${portlet.contentTypeName}</fmt:param>
											<fmt:param>${portlet.position}</fmt:param>
										</fmt:message>
										<br/>
										<fmt:message key="pagewizard.pickcontent.selected" />:
										<div id="div_${portlet.position}_${choice.parameter}" style="display:inline; color: #555">&lt; <fmt:message key="pagewizard.pickcontent.selectedchannel" /> &gt;</div>
										
										<a href="<c:url value='/editors/repository/select/SelectorChannel.do'><c:param name="role" value="editor"/><c:param name="message"><fmt:message key="pagewizard.pickcontent.selectchannel.newchannel" /></c:param></c:url>"
											target="selectchannel" onclick="openNewChannelPopup('${portlet.position}_${choice.parameter}', '${portlet.contentType}')"> 
												<img src="<cmsc:staticurl page='/editors/gfx/icons/new.png'/>" alt="<fmt:message key="pagewizard.pickcontent.new" />"></a> 
										<a href="<c:url value='/editors/repository/select/SelectorChannel.do' />"
											target="selectchannel" onclick="openSelectChannelPopup('${portlet.position}_${choice.parameter}')"> 
												<img src="<cmsc:staticurl page='/editors/gfx/icons/select.png'/>" alt="<fmt:message key="pagewizard.pickcontent.search" />"></a> <br/>
									</c:when>
								</c:choose>
							</c:forEach>
							<br/>
						</c:forEach>
						<html:submit styleClass="button" onclick="return checkComplete()"><fmt:message key="pagewizard.pickcontent.submit" /></html:submit>
						<html:cancel styleClass="button"><fmt:message key="pagewizard.pickcontent.cancel" /></html:cancel>
					</html:form>
				</p>
			</div>
		</div>
		<div class="side_block_end"></div>
	</div>	
</body>
</html:html>
</mm:content>