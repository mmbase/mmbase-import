<%@ page errorPage="exception.jsp" %>
<%
	String wizardname = request.getParameter("wizard");
	String objectnumber = request.getParameter("objectnumber");
	
	if (wizardname!=null && objectnumber!=null && !wizardname.equals("") && !objectnumber.equals("") && wizardname.indexOf("|")==-1) {
		String instancename = wizardname + "|" + new java.util.Date().getTime();
		response.sendRedirect("wizard.jsp?wizard="+instancename+"&objectnumber="+objectnumber+"&popup=true");
	} else {
		response.addHeader("Cache-Control","no-cache");
		response.addHeader("Pragma","no-cache");
		response.addHeader("Expires","0");

		%>
			<html>
			<body>
				Could not start a popup wizard because no wizardname and objectnumber are applied. <br />
				Please make sure you define a wizardname and objectnumber in the wizard schema.<br />
			</body>
			</html>
		<%
	}
%>

