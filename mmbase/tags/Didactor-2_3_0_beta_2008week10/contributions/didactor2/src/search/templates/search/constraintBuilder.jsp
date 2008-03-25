<%@page import="java.util.StringTokenizer"%>
<%!
    String searchConstraints (String fields, HttpServletRequest request) {
	String type = request.getParameter("search_type");
	String query = request.getParameter("search_query");
	if (type.equals("exact")) {
	    return fields+" LIKE '%"+query+"%'";
	}
	StringTokenizer st = new StringTokenizer(query);
	StringBuffer constraints = new StringBuffer();
	while (st.hasMoreTokens()) {
	    if (constraints.length() > 0) {
		constraints.append("all".equals(type) ? " AND " : " OR ");
	    }
	    constraints.append(fields);
	    constraints.append(" LIKE '%"+st.nextToken()+"%'");
	}
	return constraints.toString();
    }
%>

