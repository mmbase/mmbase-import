<%-- @page import="org.mmbase.bridge.*" --%>
<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%
	String pasNummer = request.getParameter("n"); 
	if(pasNummer==null) { pasNummer=""; }
	String achterNaam = request.getParameter("a");
	if(achterNaam==null) { achterNaam=""; }
	String voorNaam = request.getParameter("v"); 
	if(voorNaam==null) { voorNaam=""; }
	String tussenVoegsel = request.getParameter("s"); 
	if(tussenVoegsel==null) { tussenVoegsel=""; }
	String programma = request.getParameter("p"); 
	if(programma==null) { programma=""; }
	String afdeling = request.getParameter("d"); 
	if(afdeling==null) { afdeling=""; }
	String functie = request.getParameter("f"); 
	if(functie==null) { functie=""; }
	String inDienst = request.getParameter("i"); 
	if(inDienst==null) { inDienst=""; }
	String uitDienst = request.getParameter("u"); 
	if(uitDienst==null) { uitDienst=""; }
	String toestel = request.getParameter("t"); 
	if(toestel==null) { toestel=""; }
	String  grip = request.getParameter("g"); 
	if(grip ==null) { grip =""; }
	String mobiel = request.getParameter("m"); 
	if(mobiel ==null) { mobiel =""; }
	String email = request.getParameter("e"); 
	if(email==null) { email =""; }
%><mm:cloud jspvar="mmbasecloud"
><%
	try {
		File f = new File("c:/temp/" + pasnummer + ".jpg");
		int fsize = (int)f.length();
		byte[] thedata = new byte[fsize];
            FileInputStream instream = new FileInputStream(f);
            instream.read(thedata);
            NodeManager imageItemManager = mmbasecloud.getNodeManager("images");
            Node imgNode = imageItemManager.createNode();
            imgNode.setValue("title",achterNaam + ", " + voorNaam + ", " + pasNummer);
            imgNode.setValue("handle",thedata);
            imgNode.setValue("description","Imported " + (new Date()));
            imgNode.commit();
	} catch (Exception e) {
             out.println("Exception: " + e + " (" + pasnummer + ")");
	}
      %><mm:list path="images"
			constraints="<%= "images.title='" + achterNaam + ", " + voorNaam + ", " + pasNummer + "'" %>" max="1"
			><mm:field name="images.number" id="imagenumber" write="false"
				><mm:node number="$imagenumber" id="thisimage" notfound="skipbody"
			/></mm:field
		></mm:list><%
	if(!afdeling.equals("")) { 
	%><mm:list path="departments" constraints="<%= "departments.name='" + afdeling + "'" %>" max="1"
		><mm:field name="departments.number" id="departmentnumber" write="false"
			><mm:node number="$departmentnumber" id="thisdepartment" notfound="skipbody"
		/></mm:field
	></mm:list
	><mm:notpresent referid="thisdepartment" 
		><mm:createnode type="departments" id="thisdepartment"
			><mm:setfield name="name"><%= afdeling %></mm:setfield
		></mm:createnode
	></mm:notpresent><% 
} if(!programma.equals("")) {
	%><mm:list path="programs" constraints="<%= "programs.title='" + programma + "'" %>" max="1"
		><mm:field name="programs.number" id="programnumber" write="false"
			><mm:node number="$programnumber" id="thisprogram" 
		/></mm:field
	></mm:list
	><mm:notpresent referid="thisprogram"
		><mm:createnode type="programs" id="thisprogram"
			><mm:setfield name="title"><%= programma %></mm:setfield
		></mm:createnode
	></mm:notpresent><% 
} 
%><mm:createnode type="employees" id="thisemployee"
	><mm:setfield name="lastname"><%= achterNaam %></mm:setfield
	><mm:setfield name="firstname"><%= voorNaam %></mm:setfield
	><mm:setfield name="initials"><%= tussenVoegsel %></mm:setfield
	><mm:setfield name="enrolldate"><%= inDienst %></mm:setfield
	><mm:setfield name="birthday"><%= uitDienst %></mm:setfield
	><mm:setfield name="position"><%= pasNummer %></mm:setfield
	><mm:setfield name="companyphone"><%= toestel %></mm:setfield
	><mm:setfield name="location"><%= grip %></mm:setfield
	><mm:setfield name="cellularphone"><%= mobiel %></mm:setfield
	><mm:setfield name="email"><%= email %></mm:setfield
	></mm:createnode
><mm:present referid="thisdepartment"
	><mm:createrelation role="readmore" source="thisemployee" destination="thisdepartment" 
		><mm:setfield name="readmore"><%= functie %></mm:setfield
	></mm:createrelation
></mm:present
><mm:present referid="thisprogram"
	><mm:createrelation role="readmore" source="thisemployee" destination="thisprogram" 
/></mm:present
><mm:present referid="thisimage"
	><mm:createrelation role="posrel" source="thisemployee" destination="thisimage" 
/></mm:present
><mm:remove referid="thisemployee" 
/><mm:remove referid="thisdepartment" 
/><mm:remove referid="thisprogram" 
/></mm:cloud>