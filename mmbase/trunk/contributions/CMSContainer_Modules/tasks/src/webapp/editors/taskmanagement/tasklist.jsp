<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%!
/*
* Comparator to order mmbase nodes on status. Sorting is not done on alphabetical order but on the value of status where 
* status "init" has position 1; status "notified" position 2 and status "done" position 3.
*/
static public class StatusComparator implements Comparator {
	private static final String INIT = "task.status.init"; 
	private static final String NOTIFIED = "task.status.notified";
	private static final String DONE = "task.status.done";
	
	public int compare(Object o1, Object o2) {
		String status1 = ((org.mmbase.bridge.Node) o1).getStringValue("status"); 
		String status2 = ((org.mmbase.bridge.Node) o2).getStringValue("status");
		if (status1.equals(INIT) && status2.equals(INIT) || 
			status1.equals(NOTIFIED) && status2.equals(NOTIFIED) || 
			status1.equals(DONE) && status2.equals(DONE)) {
			
			return 0;
		}
		else if (status1.equals(INIT) && status2.equals(NOTIFIED) ||
				 status1.equals(INIT) && status2.equals(DONE) ||
				 status1.equals(NOTIFIED) && status2.equals(DONE)) {
			return -1;
		}
		else {
		 	return 1;
		}
	}
}
%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<head>
   <title><fmt:message key="tasks.title" /></title>
   <link rel="stylesheet" type="text/css" href="../css/main.css" />
   <script src="../utils/rowhover.js" type="text/javascript"></script>
   <style type="text/css" xml:space="preserve">
      body { behavior: url(../css/hover.htc);}
   </style>

</head>
	<body>      

	<mm:cloud jspvar="cloud" loginpage="../login.jsp">

      <div class="content_block_pink">
               <div class="header">
                  <div class="title"><fmt:message key="tasks.title" /></div>
                  <div class="header_end"></div>
               </div>
               
               <br />
               
               <div class="body_table">
                     <mm:cloudinfo type="user" id="cloudusername" write="false" />
                     <mm:listnodescontainer type="user">
                        <mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
                        <mm:maxnumber value="10" />
                        <mm:listnodes>

                           <mm:relatednodescontainer type="task" role="assignedrel" searchdirs="source">
                                 <table>
                                    <thead>
                                       <tr>
                                          <th><fmt:message key="task.created" /></th>                                                                                 
                                          <th><fmt:message key="task.deadline" /></th>                                          
                                          <th><fmt:message key="task.title" /></th>
                                          <th><fmt:message key="task.contenttitle" /></th>
                                          <th><fmt:message key="task.nodetype" /></th>
                                          <th><fmt:message key="task.description" /></th>
                                          <th><fmt:message key="task.status" /></th>
                                       </tr>
                                    </thead>

                                    <tbody class="hover">

                                       <mm:relatednodes comparator="StatusComparator">
                                          <c:set var="taskid"><mm:field name="number"/></c:set>
                                          <tr <mm:even inverse="true">class="swap"</mm:even>>
                                             <td><mm:field name="creationdate" id="created"><mm:time time="${created}" format="d/M/yyyy HH:mm" /></mm:field></td>
                                             <td><mm:field name="deadline" id="deadl"><mm:time time="${deadl}" format="d/M/yyyy HH:mm"/></mm:field></td>
											 <td><mm:field name="title"/></td>
                                             <c:set var="elementtitel"><mm:field name="title"/></c:set>
                                             <c:set var="elementnumber"/>
                                             <c:set var="elementtype"/>
                                             
                                             <mm:relatednodescontainer type="contentelement" role="taskrel" searchdirs="destination">
                                                <mm:maxnumber value="1" />
                                                <mm:relatednodes>
                                                   <c:set var="elementtitel"><mm:field name="title"/></c:set>
                                                   <c:set var="elementnumber"><mm:field name="number"/></c:set>
                                                   <c:set var="elementtype"><mm:field name="number"><mm:isnotempty><mm:nodeinfo type="guitype"/></mm:isnotempty></mm:field></c:set>
                                                </mm:relatednodes>
                                             </mm:relatednodescontainer>
                     
                                             <td><c:choose>
                                             	<c:when test="${empty elementnumber}">
                                             		<fmt:message key="task.noelement"/>
	                                             </c:when>
	                                             <c:otherwise>
	                                             <mm:hasrank minvalue="basic user">
			                                         <a href="<mm:url page="../WizardInitAction.do">
	    											 <mm:param name="objectnumber" value="${elementnumber}"/>
								       				 <mm:param name="returnurl" value="/nijmegen-staging/editors/taskmanagement/tasklist.jsp"/>
												   	 </mm:url>" target="rightpane"><img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="task.editelement"/>" title="<fmt:message key="task.editelement"/>"/></a> ${elementtitel}
												 </mm:hasrank>
	                                             </c:otherwise>
                                             </c:choose></td>

                                             <td>${elementtype}</td>

                                             <td>
                                             <mm:hasrank minvalue="basic user">
                                             	<mm:field name="number" jspvar="number" write="false"/>
	                                             <a href="<mm:url page="/editors/WizardInitAction.do">
	  											 <mm:param name="objectnumber" value="${number}"/>
		 										 <mm:param name="contenttype" value="task"/>
							       				 <mm:param name="returnurl" value="/nijmegen-staging/editors/taskmanagement/tasklist.jsp"/>
											   	 </mm:url>" target="rightpane"><img src="../gfx/icons/edit2.png" align="top" alt="<fmt:message key="task.edit"/>" title="<fmt:message key="task.edit"/>"/></a> <mm:field name="description" />
											 </mm:hasrank>
                                             </td>
                                             
                                             <c:set var="status"><mm:field name="status" /></c:set>
                                             <td><fmt:message key="${status}" /></td>

                                          </tr>
                                       </mm:relatednodes>
                                    </tbody>
                                 </table>
                              </mm:relatednodescontainer>
                           </mm:listnodes>
                        </mm:listnodescontainer>
      
      
                     </div>
                     
                     <div class="content_block_end"></div>
            </div>
      
      
	</mm:cloud>
	</body>
	</html:html>
</mm:content>
