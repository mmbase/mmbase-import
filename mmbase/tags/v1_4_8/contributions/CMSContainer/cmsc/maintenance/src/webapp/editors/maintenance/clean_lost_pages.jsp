<%-- 
Info http://jira.finalist.com/browse/NIJ-855
 --%>

<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp" %>
<%int deleted = -1;%>


<%while(deleted != 0) {%>
	<%deleted = 0;%>
	<%int failed = 0;%>
	<mm:cloud>

		<mm:listnodes type="page">
		
			<%boolean found = false;%>
			<mm:related path="navrel,page" searchdir="source">
				<%found = true;%>
			</mm:related> 

			<mm:remove referid="nodetype"/>
			<mm:nodeinfo type="type" id="nodetype" write="false"/>
			<mm:compare referid="nodetype" value="site">
				<%found = true;%>
			</mm:compare>
			
			<%if(!found) {%>
				<%try {%>
		   		<mm:field name="number"/>:<mm:field name="title"/><br/>
		   		<%out.flush();%>
		   		<mm:relatednodes type="portlet">
		   			<mm:import id="multiple" reset="true">false</mm:import>
		   			<mm:relatednodes type="portletdefinition" constraints="type='multiple'">
			   			<mm:import id="multiple" reset="true">true</mm:import>
		   			</mm:relatednodes>
		   			<mm:compare referid="multiple" value="true">
			   			<%try {%><mm:deletenode deleterelations="true"/><%}catch(Exception e){}%>
		   			</mm:compare>
		   		</mm:relatednodes>
	   			<mm:deletenode deleterelations="true"/>
		   		<%deleted++;%>
		   	<%}catch(Exception e){failed++;}%>
	   	<%}%>
		</mm:listnodes>
	</mm:cloud>
	<b>Verwijderd deze ronde: <%=deleted%></b> (nog niet verwijderbaar deze ronde, wacht op volgende: <%=failed%>)<br/>
<%}%>