<%@ page import="java.awt.*" %>
<%	Dimension screenSize =  (Dimension) session.getAttribute("screenSize");
	if(screenSize==null) { 
	 	screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		session.setAttribute("screenSize", screenSize);
	}
%>
<!-- windowWidth in inc_screensize <%=  screenSize.width %> -->