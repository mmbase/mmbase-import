<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page language="java" contentType="text/html; charset=utf-8"%>

<%@ page import="java.io.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

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

<%	boolean debug = false;
	String dataFile = "c:/temp/vpro.csv";

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
			
		%><mm:include page="createemployee.jsp"
			><mm:param name="n"><%= pasNummer %></mm:param
			><mm:param name="a"><%= achterNaam %></mm:param
			><mm:param name="v"><%= voorNaam %></mm:param
			><mm:param name="s"><%= tussenVoegsel %></mm:param
			><mm:param name="d"><%= afdeling %></mm:param
			><mm:param name="f"><%= functie %></mm:param
			><mm:param name="p"><%= programma %></mm:param
			><mm:param name="i"><%= (getDate(inDienst,1900).getTime()/1000) %></mm:param
			><mm:param name="u"><%= (getDate(uitDienst,1900).getTime()/1000) %></mm:param
			><mm:param name="t"><%= toestel %></mm:param
			><mm:param name="g"><%= grip %></mm:param
			><mm:param name="m"><%= mobiel %></mm:param
			><mm:param name="e"><%= email %></mm:param
		></mm:include><%
	
	nextLine = dataFileReader.readLine();
}
dataFileReader.close();
%>
