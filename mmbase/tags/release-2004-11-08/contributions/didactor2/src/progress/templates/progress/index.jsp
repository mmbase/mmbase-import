<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title>Voortgang</title>
  </mm:param>
</mm:treeinclude>

<div class="rows">

<div class="navigationbar">
  <div class="titlebar">
    Voortgang
  </div>		
</div>

<div class="folders">
  <div class="folderHeader">
    &nbsp;
  </div>
  <div class="folderBody">
    &nbsp;
  </div>
</div>

<div class="mainContent">
  <div class="contentHeader">
<%--    Some buttons working on this folder--%>
  </div>
  <div class="contentBodywit">

<di:hasrole role="student">
<mm:import jspvar="progress" id="progress" vartype="Double"><mm:treeinclude page="/progress/getprogress.jsp" objectlist="$includePath" referids="$referids"/></mm:import>
Percentage doorlopen: <%= (int)(progress.doubleValue()*100.0)%>%
<p/>
</di:hasrole>

<%-- Look-up workgroup --%>

<mm:import id="workgroupno">0</mm:import>
<mm:import id="workgroupname">Unknown</mm:import>

<mm:node number="$user">
<mm:relatedcontainer path="workgroups,classes">
  <mm:related>
    <mm:remove referid="classno"/>
    <mm:field name="classes.number" id="classno" write="false"/>
    
    <mm:compare referid="classno" referid2="class">
      <mm:remove referid="workgroupno"/>
      <mm:remove referid="workgroupname"/>
      <mm:field id="workgroupno" name="workgroups.number" write="false"/>
      <mm:field id="workgroupname" name="workgroups.name" write="false"/>
    </mm:compare>
  </mm:related>
</mm:relatedcontainer>
</mm:node>


<%--
<mm:list nodes="$user" path="people,workgroups,classes" constraints="classes.number=$class">
    <mm:remove referid="workgroupno"/>
    <mm:remove referid="workgroupname"/>
    <mm:field id="workgroupno" name="workgroups.number" write="false"/>
    <mm:field id="workgroupname" name="workgroups.name" write="false"/>
</mm:list>
--%>
Werkgroep : <mm:write referid="workgroupname"/>
<p/>

	<mm:list fields="classrel.number" path="people,classrel,classes" constraints="people.number='${user}' and classes.number=${class}">
		<mm:field name="classrel.number" id="classrel" write="false"/>
	</mm:list>
	<mm:node referid="classrel">
		<di:translate>Aantal maal ingelogd</di:translate>: <mm:field name="logincount"/>
		<p />
		<di:translate>Duur inloggen</di:translate>: <mm:field name="onlinetime" jspvar="onlinetime" vartype="Integer" write="false">
			<%
				java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("H");
				Integer onlineValue = new Integer(onlinetime.intValue() / 1000);
				String hour = format.format(onlineValue);
				format = new java.text.SimpleDateFormat("m");
				String min = format.format(onlineValue);
			%>
<%--			<mm:import id="time" ><%=onlinetime%></mm:import>
			<mm:write referid="time">
				<mm:time format="hh:mm:ss"/>
			</mm:write>--%>
			<%=hour%> uur en <%=min%> minuten.
			<br />
<%--			<%=onlinetime.intValue()/3600%> uur en <%=(onlinetime.intValue()%3600)/60%> minuten.--%>
		</mm:field>
		<p />
	</mm:node>

<table class="font">
<tr>
<%-- print header: the names of the tests --%>
<% int count=0; %>
<mm:node number="$education" notfound="skip">
  <th><mm:field name="name" write="true"/></th>
  <mm:relatednodescontainer type="learnobjects" role="posrel">
    <mm:sortorder field="posrel.pos" direction="up"/>
    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" direction="up">

      <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>
      <mm:compare referid="nodetype" value="tests">
        <% count ++; %>
        <th>      <%=count%></th>
      </mm:compare>
      <mm:remove referid="nodetype"/>
    </mm:tree>
  </mm:relatednodescontainer>
</mm:node> <!-- education node -->
</tr>

<%-- List progress of all students in the group --%>

<di:hasrole role="teacher">
  <mm:node number="$user">
  <mm:relatednodescontainer type="workgroups">
    <mm:constraint field="number" referid="workgroupno"/>
    <mm:relatednodes>
      <mm:relatednodes type="people">
        <mm:field id="userNo" name="number" write="false"/>
        <di:hasrole referid="userNo" role="student">
          <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids">
            <mm:param name="userNoX"><mm:write referid="userNo"/></mm:param>
          </mm:treeinclude>
        </di:hasrole>
        <mm:remove referid="userNo"/>
      </mm:relatednodes><%-- people --%>
    </mm:relatednodes>
  </mm:relatednodescontainer> <%-- workgroups --%>

  </mm:node> <%-- user --%>

</di:hasrole> <%-- teacher --%>
<di:hasrole role="student">
  <mm:treeinclude page="/progress/progress_row.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="userNoX"><mm:write referid="user"/></mm:param>
  </mm:treeinclude>
</di:hasrole> <%-- student --%>

</table>
<br>
<table class="font">
<%-- print header: the names of the tests --%>
<% count=0; %>
<mm:node number="$education" notfound="skip">
  <tr><th>No</th><th>Toetsnaam</th></tr>
  <mm:relatednodescontainer type="learnobjects" role="posrel">
    <mm:sortorder field="posrel.pos" direction="up"/>
    <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" direction="up">

      <mm:import id="nodetype"><mm:nodeinfo type="type" /></mm:import>
      <mm:compare referid="nodetype" value="tests">
        <% count++; %>
        <tr><td><%=count%></td><td><mm:field name="name" write="true"/></td></tr>
      </mm:compare>
      <mm:remove referid="nodetype"/>
    </mm:tree>
  </mm:relatednodescontainer>
</mm:node> <%-- education node --%>
</table>


 </div>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
