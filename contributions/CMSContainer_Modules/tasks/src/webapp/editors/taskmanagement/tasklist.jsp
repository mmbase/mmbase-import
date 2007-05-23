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
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="tasks.title" />
<body>      
	<mm:cloud jspvar="cloud" loginpage="../login.jsp">
	<cmscedit:contentblock title="tasks.title" 
		titleClass="content_block_pink" bodyClass="body_table">
       <br />
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
	                                 <mm:url page="/editors/taskmanagement/tasklist.jsp" id="returnTasklist" write="false" />
                                     <a href="<mm:url page="../WizardInitAction.do">
									 <mm:param name="objectnumber" value="${elementnumber}"/>
				       				 <mm:param name="returnurl" value="${returnTasklist}"/>
								   	 </mm:url>" target="rightpane"><img src="../gfx/icons/edit.png" align="top" alt="<fmt:message key="task.editelement"/>" title="<fmt:message key="task.editelement"/>"/></a> ${elementtitel}
								 </mm:hasrank>
                                 </c:otherwise>
                             </c:choose></td>

                             <td>${elementtype}</td>

                             <td>
                             <mm:hasrank minvalue="basic user">
                             	<mm:field name="number" jspvar="number" write="false"/>
                                 <mm:url page="/editors/taskmanagement/tasklist.jsp" id="returnTaskedit" write="false" />
                                 <a href="<mm:url page="/editors/WizardInitAction.do">
  											 <mm:param name="objectnumber" value="${number}"/>
	 										 <mm:param name="contenttype" value="task"/>
						       				 <mm:param name="returnurl" value="${returnTaskedit}"/>
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
    </cmscedit:contentblock>  
	</mm:cloud>
	</body>
	</html:html>
</mm:content>
