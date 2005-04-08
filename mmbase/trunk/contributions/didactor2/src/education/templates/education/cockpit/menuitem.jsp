<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
	<%@include file="/shared/setImports.jsp" %>
        
    <mm:import id="scope" externid="scope"/>
    <mm:compare referid="scope" value="provider">
	<mm:import id="roleId" jspvar="roleId" />
	<mm:remove referid="roleId" />

	<%--	sort smallest role first	--%>
	<di:hasrole role="contenteditor">
		<mm:import id="roleId" jspvar="roleId">contentmanagement</mm:import>
		<mm:import id="roleText">content management</mm:import>
	</di:hasrole>

	<di:hasrole role="courseeditor">
		<mm:present referid="roleId">
			<mm:remove referid="roleId" />
			<mm:remove referid="roleText" />
		</mm:present>
		<mm:import id="roleId" jspvar="roleId">coursemanagement</mm:import>
		<mm:import id="roleText">course management</mm:import>
	</di:hasrole>

	<di:hasrole role="administrator">
		<mm:present referid="roleId">
			<mm:remove referid="roleId" />
			<mm:remove referid="roleText" />
		</mm:present>
		<mm:import id="roleId" jspvar="roleId">administrator</mm:import>
		<mm:import id="roleText">administrator</mm:import>
	</di:hasrole>

	<mm:present referid="roleId">
<%--	<mm:compare referid="type" value="div">  --%>
			<div class="menuSeperator"></div>
			<div class="menuItem" id="<%=roleId%>">
				<a href="<mm:treefile page="/education/wizards/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar">
					<di:translate id="<%=roleId%>"><mm:write referid="roleText" /></di:translate>
				</a>
			</div>
<%--		</mm:compare> --%>
<%--		<mm:compare referid="type" value="option">
			<option value="<mm:treefile page="/education/wizards/index.jsp" objectlist="$includePath" referids="$referids" />" class="menubar">
				<di:translate id="<%=roleId%>"><mm:write referid="roleText" /></di:translate>
			</option>
		</mm:compare> --%>
	</mm:present>
    </mm:compare>
</mm:cloud>
</mm:content>
