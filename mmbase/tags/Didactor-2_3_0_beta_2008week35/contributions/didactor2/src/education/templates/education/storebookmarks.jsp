<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@page import="java.util.HashMap"%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<mm:import externid="learnobject" jspvar="learnObject"/>
<mm:import externid="learnobjecttype" jspvar="learnObjectType"/>
<mm:import jspvar="educationNumber"><mm:write referid="education"/></mm:import>
<%
    if (educationNumber != null && educationNumber.length() > 0) {
        session.setAttribute("lasteducation",educationNumber);
    }
    else {
        educationNumber = (String) session.getAttribute("lasteducation");
    }
    if (educationNumber != null && educationNumber.length() > 0) {
        HashMap bookmarks = (HashMap) session.getAttribute("educationBookmarks");
        if (bookmarks== null) {
            bookmarks = new HashMap();
            session.setAttribute("educationBookmarks",bookmarks);
            //System.err.println("made new bookmarks object");
        }
        if (learnObject != null && learnObject.length() != 0) {
            bookmarks.put(educationNumber+",learnobject",learnObject);
            //System.err.println("stored "+educationNumber+",learnobject ="+learnObject);
        }
        if (learnObjectType != null && learnObject.length() != 0) {
            bookmarks.put(educationNumber+",learnobjecttype",learnObjectType);
            //System.err.println("stored learnobjecttype ="+learnObjectType);
        }
    }
%>
</mm:cloud>
</mm:content>
