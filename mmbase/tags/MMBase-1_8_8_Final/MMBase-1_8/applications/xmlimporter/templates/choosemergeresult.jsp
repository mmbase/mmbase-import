<%@page language="java" contentType="text/html"
 import="java.util.*, java.io.*, org.mmbase.applications.xmlimporter.Consultant,
 org.mmbase.applications.xmlimporter.TmpObject, org.mmbase.module.core.MMObjectNode, 
 org.mmbase.util.logging.Logger,
 org.mmbase.util.logging.Logging" %>

<jsp:useBean id="consultant" scope="session"
 class="org.mmbase.applications.xmlimporter.Consultant" />

<%!  Logger log = Logging.getLoggerInstance(
     "org.mmbase.applications.xmlimporter.jsp.choosemergeresult.jsp"); %>

<html>
  <head>
    <title>XML Importer Process Duplicates</title>
    <link rel="stylesheet" href="css/mmbase.css" type="text/css">
  </head>
<body class="basic">

<H1>XML Importer Process Duplicates</H1>
<HR>
<H2>Original Object</H2>         
        <table WIDTH = "60%" BORDER="1">
            <tr>
               <th>FieldName</th>
               <th>FieldValue</th>
            </tr>
<%  try {
     TmpObject origObject = consultant.getOriginalObject();
     
     MMObjectNode tmpNode = origObject.getNode();
     Iterator it = tmpNode.getValues().entrySet().iterator();
	 while (it.hasNext()) {
		 Map.Entry entry = (Map.Entry) it.next();
		 String field = (String) entry.getKey();
		 Object value = entry.getValue();
     %>
         <tr>
            <td><%= field + ":  " %></td>
            <td><%= value %></td>
         </tr>
<%      }  %>
        <br>
        </table>

<BR><HR><H2>MergeResults</H2>
<%      List mergeResults = consultant.getMergeResults();
        int nrMergeResults = mergeResults.size();
        for (int i=0; i < nrMergeResults; i++ ) { %>
            <FORM METHOD="POST" ACTION="./continueimport.jsp">
            <BR><BR><BR><HR>
            <H3>MergeResult <%= i+1 %></H3>
            <table WIDTH = "60%" BORDER="1">
            <tr>
               <th>FieldName</th>
               <th>FieldValue</th>
            </tr>
<%          TmpObject t = (TmpObject)mergeResults.get(i);
            
            MMObjectNode tNode = t.getNode();
            Iterator itt = tNode.getValues().entrySet().iterator();
            while (itt.hasNext()) {
            	Map.Entry entry = (Map.Entry) itt.next();
            	String field = (String) entry.getKey();
            	Object value = entry.getValue();            
            %>
               <tr>
                  <td><%= field + ":  " %></td>
                  <td><%= value %></td>
               </tr>
<%          } %>
            </table>
            <br>
            <INPUT TYPE="HIDDEN" NAME="candidateNr" VALUE="<%= i %>">
            <INPUT TYPE="SUBMIT" VALUE="Continue Import with MergeResult <%=i+1%>">
            </FORM>
<%      }
    } catch (Exception e) {
        String s1 = "UNKNOWN ERROR";
        String s2 = e.toString();
        log.error(s1);
        out.println(s1);
        log.error(s2);
        out.println(s2);
    }
%>
</body>
</html>
