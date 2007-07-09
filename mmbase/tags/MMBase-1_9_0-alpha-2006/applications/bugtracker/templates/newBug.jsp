<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud method="asis">
  <%@include file="parameters.jsp" %>
  <%@include file="login.jsp" %>
<mm:node number="$user">

<form action="<mm:url referids="parameters,$parameters"/>" method="post">
<table class="list" style="margin-top : 10px;">

<tr><th>Type</th><th>Priority</th><th>Version</th></tr>
<tr>
		<td>
			<select name="newbtype">
				<option value="1">bug</option>
				<option value="2">wish</option>
				<option value="3">docbug</option>
				<option value="4">docwish</option>
			</select>
		</td>
		<td>
			<select name="newbpriority">
				<option value="1">high</option>
				<option value="2" selected="selected">medium</option>
				<option value="3">low</option>
			</select>
		</td>
		<td>
			<input name="newversion" value="<%= org.mmbase.util.xml.UtilReader.get("bugtracker.xml").getProperties().getProperty("defaultversion", "1.8.0")%>" size="10">
		</td>
</tr>
</table>
<table  class="list" style="margin-top : 10px;">
<tr>
	<th>Area</th>
</tr>
<tr>
		<td>
		<mm:import id="noareas" />
		<mm:node number="BugTracker.Start">
		<select name="newarea">
   
			<mm:relatednodescontainer type="areas">
        <mm:sortorder field="name" direction="up"/>
			  <mm:relatednodes>
			  <mm:first><mm:remove referid="noareas" /></mm:first>
			    <option value="<mm:field name="number" />"
    			<mm:field name="name">
      			<mm:compare value="Misc">selected="selected"</mm:compare>
    			</mm:field>><mm:field name="substring(name,15,.)" /></option>
			</mm:relatednodes>
			</mm:relatednodescontainer>
		</select>
		</mm:node>
		</td>
</tr>
</table>
<table class="list" style="margin-top : 10px;">

<tr>
	<th>Issue : give the issue in one line </th>
</tr>
<tr>
    <td>
 <input style="width: 100%" name="newissue" />
		</td>
</tr>

</table>
<table  class="list" style="margin-top : 10px;">
<tr>
	<th>Description : Describe the issue as complete as possible </th>
</tr>
<tr>
		<td>
			<textarea name="newdescription" style="width: 100%" rows="15" wrap="wrap"></textarea>
		</td>
</tr>
</table>
<table  class="list" style="margin-top : 10px;">

<tr>
	<th colspan="3">Name this bug will be submitted under </th>
</tr>
<tr>
		<td>
			<input name="submitter" type="hidden" value="<mm:field name="number" />">
			<mm:field name="firstname" /> <mm:field name="lastname" /> ( <mm:field name="email" /> )
		</td>
		<td>
			<mm:present referid="noareas" inverse="true">
				<input type="hidden" name="action" value="newbug" />
				<input type="submit" value="submit report" />
			</mm:present>
			</form><!-- hmm -->
			<mm:present referid="noareas">
				No areas defined, admin needs to add areas !
			</mm:present>
		</td>
		<td>
			<p />
      <form action="<mm:url referids="parameters,$parameters"/>" method="post">
				<input type="submit" value="cancel" />
			</form>
		</td>
</tr>

</table>
</mm:node>
</mm:cloud>
