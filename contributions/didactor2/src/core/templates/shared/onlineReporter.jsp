<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
	<%@include file="/shared/setImports.jsp"%>

	<mm:present referid="education">
		<mm:listnodes type="people" constraints="username='${username}'">
        	<mm:setfield name="lastactivity"><%=System.currentTimeMillis()/1000%></mm:setfield>
		</mm:listnodes>

		<mm:present referid="class">
        	<mm:list fields="classrel.number,classrel.lastlogin" path="people,classrel,classes" max="1" constraints="username='${username}' and classes.number=${class}" orderby="classrel.lastlogin" directions="down">
				<mm:field id="classrelNumber" name="classrel.number" jspvar="lastClassRel" write="false"/>
				<mm:node referid="classrelNumber">
					<mm:write referid="oldLastActivity" jspvar="oldLastActivity" vartype="Integer">
						<mm:field name="onlinetime" jspvar="onlinetime" vartype="Integer" write="false">
                <mm:import id="newOnlineTime"><%=onlinetime.intValue()+Math.min(120,(System.currentTimeMillis()/1000-oldLastActivity.intValue()))%></mm:import>
<%--
								<mm:write referid="newOnlineTime">
									<mm:time format="hh:mm:ss"/>
								</mm:write>
--%>
						</mm:field>
					</mm:write>
					<%
						Object oldEduObject = session.getAttribute("educationId");
						String oldEducationId = null;
						String educationId = request.getParameter("education") + "-" + username + "-" + session.getId();
						session.setAttribute("educationId", educationId);
						
						if (oldEduObject != null)
						{
							oldEducationId = oldEduObject.toString();
						}
						if (!educationId.equals(oldEducationId))
						{
					%>
							<mm:field name="logincount" jspvar="logincount" vartype="Integer" write="false">
								<mm:setfield name="logincount"><%=logincount.intValue()+1%></mm:setfield>
							</mm:field>
					<%
						}
        		    %>
					<mm:setfield name="onlinetime"><mm:write referid="newOnlineTime"/></mm:setfield>
				</mm:node>
			</mm:list>
		</mm:present>
	</mm:present>
</mm:cloud>
</mm:content>