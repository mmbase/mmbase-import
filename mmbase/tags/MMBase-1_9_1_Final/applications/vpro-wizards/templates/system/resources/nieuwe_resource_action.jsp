<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Maak een nieuwe resource aan</title>
</head>
<body>

<mm:import externid="key" required="true"/>
<mm:import externid="value" required="true"/>

<mm:cloud username="community" password="L8keF7eEF1sh">
	<mm:node id="_3voor12text" number="33858345" />
	<mm:createnode type="bundlekeys" id="bundlekey">
		<mm:setfield name="key"><mm:write referid="key" /></mm:setfield>
		<mm:setfield name="description"><mm:write referid="key" /></mm:setfield>
	</mm:createnode>
	<mm:createnode type="bundlevalues" id="bundlevalue">
		<mm:setfield name="locale">nl_NL</mm:setfield>
		<mm:setfield name="value"><mm:write referid="value" /></mm:setfield>
	</mm:createnode>
	<mm:createrelation source="bundlekey" destination="bundlevalue" role="related" />
	<mm:createrelation source="bundlekey" destination="_3voor12text" role="related" />
</mm:cloud>

<script>
	javascript:setTimeout("history.back()", 1);
</script>

</body>
</html>