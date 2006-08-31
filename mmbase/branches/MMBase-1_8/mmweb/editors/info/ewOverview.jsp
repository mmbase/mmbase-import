<%-- Author: H.Hangyi / 26.06.2002 / www.mmatch.nl --%>

<%@ page import="java.io.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>

<% String ewRoot = request.getRealPath("/")+"editors/"; %>

<%@include file="../includes/getParam.jsp" %>

<%!	public String procesSearch(String wizard) {
		String startNodeCell = "";
		String nodePathCell  = "";
		String ageCell = "";
		try{
			BufferedReader wizardReader = new BufferedReader(new FileReader(wizard));
			String nextLine = wizardReader.readLine();
			int i = 1;
			while(nextLine!=null){
				String startNodes = getParam(nextLine, "startnodes=\"", "\"");
				if(startNodes.equals("")) { // try destination if line does not contain startnodes
					startNodes = getParam(nextLine, "relation destination=\"", "\"");
				}
				if(!startNodes.equals("")) {
					if(startNodeCell.equals("")) {
						startNodeCell =  "<td valign=\"top\" align=\"left\">" + i;
					} else {			
						startNodeCell += "<br>" + i;
					}
					startNodeCell += ". <a target=\"_blank\" href=\"/docs/taglib/examples/basictags/nodetag/nodeinfo.jsp?object=" + startNodes + "\">" + startNodes + "</a>";
				}
				String nodePath = getParam(nextLine, "nodepath=\"", "\"");
				if(!nodePath.equals("")) {
					if(nodePathCell.equals("")) {
						nodePathCell = "<td valign=\"top\" align=\"left\">" + i + ". " + nodePath;
					} else {			
						nodePathCell += "<br>" + i + ". " + nodePath;
					}
				}
				String age = getParam(nextLine, "age=\"", "\"");
				if(!age.equals("")) {
					if(ageCell.equals("")) {
						ageCell = "<td valign=\"top\" align=\"left\">" + i + ". " + age;
					} else {			
						ageCell += "<br>" + i + ". " + age;
					}
				}
				nextLine = wizardReader.readLine();
				i++;
			}
			if(startNodeCell.equals("")) { startNodeCell = "<td align=\"left\">"; }
			if(nodePathCell.equals("")) { nodePathCell = "<td align=\"left\">" ; }
			if(ageCell.equals("")) { ageCell = "<td align=\"left\">" ; }
			startNodeCell += " &nbsp;</td>";
			nodePathCell += " &nbsp;</td>";
			ageCell += " &nbsp;</td>";
			wizardReader.close();
		} catch(java.io.IOException e) {
			startNodeCell = "<td align=\"left\" colspan=\"3\"> Error on opening file: " + wizard +" </td>";
			nodePathCell = "";
		}
		return startNodeCell + nodePathCell + ageCell;
}
%>

<%!	public String procesIncludes(String wizard, String ewRoot) {
		String tableRows = "";
		try{
			BufferedReader wizardReader = new BufferedReader(new FileReader(wizard));
			String nextLine = wizardReader.readLine();
			String context = "";
			String include = "";
			boolean formSchema = false;
			while(nextLine!=null){
				context = getParam(nextLine,"<form-schema",">");
				if(context!=""){ formSchema=true; }
				context = getParam(nextLine,"</form-schema",">");
				if(context!=""){ formSchema=false; }
				if(formSchema){
					include = getParam(nextLine,"include=\"","\"");
					if(!include.equals("")) {
						tableRows += "<tr><td valign=\"top\" align=\"left\">" + include + "</td>" + procesSearch(ewRoot + include) + "</tr>";
					}
				}
				nextLine = wizardReader.readLine();
			}
			wizardReader.close();
		} catch(java.io.IOException e) {
			tableRows = "<tr><td align=\"left\" colspan=\"4\"> Error on opening: " + wizard + " </td></tr>"; 
		}
		return tableRows;
}
%>

<%!	public String procesCreates(String wizard, String ewRoot) {
		String tableRows = "";
		try{
			BufferedReader wizardReader = new BufferedReader(new FileReader(wizard));
			String nextLine = wizardReader.readLine();
			String context = "";
			String include = "";
			boolean actionInclude = false;
			while(nextLine!=null){
				context = getParam(nextLine,"<action",">");
				if(context!=""){ actionInclude =true; }
				if(actionInclude){
					include = getParam(nextLine,"include=\"","\"");
					if(!include.equals("")&&(include.indexOf("create")!=-1)) {
						tableRows += "<tr><td valign=\"top\" align=\"left\">" + include + "</td>" + procesSearch(ewRoot + include) + "</tr>";
					}
				}
				context = getParam(nextLine,"//",">");
				if(context!=""){ actionInclude =false; }
				nextLine = wizardReader.readLine();
			}
			wizardReader.close();
		} catch(java.io.IOException e) {
			tableRows = "<tr><td align=\"left\" colspan=\"4\"> Error on opening: " + wizard + " </td></tr>"; 
		}
		return tableRows;
}
%>


<mm:cloud>

<html>
<head>
<title>Editwizard overview</title>
<link rel="stylesheet" type="text/css" href="../css/editors.css">
</head>
<body bgcolor="#EFEFEF" topmargin="10" rightmargin="0" leftmargin="0">

<mm:import externid="command" />

<table border="1" align="center" cellpadding="1" cellspacing="0">

<mm:notpresent referid="command">
<form>
	<tr><td class="center">
		<h3> Available Editwizards</h3>
		Dit overzicht geeft voor elke wizard de startnodes, nodepath, ages en default relaties.<br>
		(overzicht is bedoeld voor ontwikkelaars en niet voor gebruikers)
	</td></tr>
	<tr><td>
			<mm:listnodes type="editwizards" orderby="title">
				<input type="checkbox" name="ew" value="<mm:field name="number" />" />
		         	<mm:field name="title" /> / <mm:field name="description" /> <br />
	       		</mm:listnodes>
	</td></tr>
	<tr><td class="center">
	     <input type="submit" value="ok" name="command" />
	</td></tr>
</form>
</mm:notpresent>
  
<mm:present referid="command">
	<tr><td class="center" >

<mm:import externid="ew" vartype="list" />
<mm:present referid="ew">
	<mm:aliaslist referid="ew">
	<mm:node number="$_">

<mm:field name="url" jspvar="ewUrl" vartype="String" write="false">

<%
String wizardLocalPath = getParam(ewUrl,"wizard=","&"); 
String wizardName = wizardLocalPath.substring(wizardLocalPath.lastIndexOf("/")+1); 
String wizard = ewRoot + wizardLocalPath + ".xml"; 
%>

<table border="1" align="center" cellpadding="1" cellspacing="0">
	<tr>
		<td class="center" colspan="4">
			<mm:field name="title" /> (<%= wizardName %>)
		</td>
	</tr>
	<tr>
		<td class="center">wizard</td>
    	<td class="center">startnodes</td>
		<td class="center">nodepath</td>
		<td class="center">age</td>
	</tr>
	<tr>
		<td align="left"><%= wizardLocalPath %>&nbsp;</td>
	    <td align="left">
			<a target="_blank" href="/docs/taglib/examples/basictags/nodetag/nodeinfo.jsp?object=<%= getParam(ewUrl, "startnodes=", "&") %>" ><%= getParam(ewUrl, "startnodes=", "&") %></a>&nbsp;
		</td>
		<td align="left"><%= getParam(ewUrl, "nodepath=", "&") %>&nbsp;</td>
		<td align="left"><%= getParam(ewUrl, "age=", "&") %>&nbsp;</td>
	</tr>
	<%= procesIncludes(wizard, ewRoot) %>
	<%= procesCreates(wizard,ewRoot) %>
</table><br><br>

</mm:field>
</mm:node>
</mm:aliaslist>
	</td></tr>
</mm:present>
<mm:notpresent referid="ew">
<tr><td class="center">
		Please select the editwizards you want to view.
</td></tr>
</mm:notpresent>
<tr><td class="center">
		<a href="ewOverview.jsp">index</a>		
</td></tr>
</mm:present>

</table>

</body>
</html>
</mm:cloud>
