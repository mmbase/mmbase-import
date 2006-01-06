<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page language="java" contentType="text/html; charset=utf-8"%>

<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>
<%-- @page import="org.mmbase.bridge.*" --%>
<%! public Date getDate(String thisDate,int defaultYear) {
	int thisDay = 1;
	int thisMonth = 1;
	int thisYear = defaultYear;
	if(thisDate.indexOf("-")>-1){
		int sPos = thisDate.indexOf("-");
		thisMonth = (new Integer(thisDate.substring(0,sPos))).intValue()-1;
		thisDate = thisDate.substring(sPos+1);
		sPos = thisDate.indexOf("-");
		thisDay = (new Integer(thisDate.substring(0,sPos))).intValue();
		thisDate = thisDate.substring(sPos+1);
		thisYear = (new Integer(thisDate)).intValue();
	}
	Calendar cal = Calendar.getInstance();
	cal.set(thisYear,thisMonth,thisDay);
	return cal.getTime();
} %>

<mm:cloud jspvar="mmbasecloud">

<%	boolean debug = false;
	String dataFile = "c:/temp/vprototal.csv";

BufferedReader dataFileReader = new BufferedReader(new FileReader(dataFile));
String nextLine = dataFileReader.readLine();
while(nextLine!=null)
{		int cPos = nextLine.indexOf(";");	
		String pasNummer = nextLine.substring(0,cPos);
		while(pasNummer.length()<6) pasNummer = "0" + pasNummer; 
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String achterNaam = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String voorNaam = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String tussenVoegsel = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String afdeling = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String programma = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String functie = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String inDienst = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String uitDienst = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String toestel = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String grip = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		cPos = nextLine.indexOf(";");
		String mobiel = nextLine.substring(0,cPos);
		nextLine = nextLine.substring(cPos+1);
		String email = nextLine;

		if(debug){ %>p<%= pasNummer %> | 
			a<%= achterNaam %> | 
			v<%= voorNaam %> | 
			t<%= tussenVoegsel %> | 
			d<%= afdeling %> | 
			f<%= functie %> | 
			g<%= grip %> | 
			m<%= mobiel %> | 
			e<%= email %> | 
			p<%= programma %> | 
			i<%= getDate(inDienst,1900) %> | 
			u<%=  getDate(uitDienst,2100) %> <br><% }
			

	try {
		File f = new File("c:/temp/" + pasNummer + ".jpg");
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
             out.println("Exception: " + e + " (" + pasNummer + ")");
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
	><mm:setfield name="suffix"><%= tussenVoegsel %></mm:setfield
	><mm:setfield name="enrolldate"><%= (getDate(inDienst,1900).getTime()/1000) %></mm:setfield
	><mm:setfield name="birthday"><%= (getDate(uitDienst,2100).getTime()/1000) %></mm:setfield
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
/><mm:remove referid="thisimage" 
/><mm:remove referid="imagenumber" 
/><mm:remove referid="departmentnumber" 
/><mm:remove referid="programnumber" 
/><%

	nextLine = dataFileReader.readLine();
}
dataFileReader.close();
%>
</mm:cloud>