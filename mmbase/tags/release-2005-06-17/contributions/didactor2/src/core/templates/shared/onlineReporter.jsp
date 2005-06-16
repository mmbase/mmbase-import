<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud jspvar="cloud" method="asis">
	<%@include file="/shared/setImports.jsp"%>

<mm:isgreaterthan referid="user" value="0">
		<mm:node number="$user">
                <mm:field id="oldLastActivity" name="lastactivity" write="false"/>
                <mm:islessthan referid="oldLastActivity" value="2">
                    <mm:import id="oldLastActivity" reset="true"><%=System.currentTimeMillis()/1000%></mm:import>
                </mm:islessthan>
                
        	<mm:setfield name="lastactivity"><%=System.currentTimeMillis()/1000%></mm:setfield>
		</mm:node>

	<mm:present referid="education">
		<mm:present referid="class">
        	<mm:list fields="classrel.number,classrel.lastlogin" path="people,classrel,classes" max="1" constraints="people.number=${user} and classes.number=${class}" orderby="classrel.lastlogin" directions="down">
			<mm:field id="classrelNumber" name="classrel.number" jspvar="lastClassRel" write="false"/>
			<mm:node referid="classrelNumber">
		    	    <mm:write referid="oldLastActivity" jspvar="oldLastActivity" vartype="Integer">
			    <mm:field name="onlinetime" jspvar="onlinetime" vartype="Integer" write="false">
                            <mm:import id="newOnlineTime" jspvar="newOnlineTime" vartype="Integer"><%=onlinetime.intValue()+Math.min(120,(System.currentTimeMillis()/1000-oldLastActivity.intValue()))%></mm:import>
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
                            </mm:field>
                            </mm:write>
			    <mm:setfield name="onlinetime"><mm:write referid="newOnlineTime"/></mm:setfield>
			</mm:node>
		    </mm:list>
	        </mm:present>
	</mm:present>

</mm:isgreaterthan>
</mm:cloud>
</mm:content>
