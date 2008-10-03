<%@include file="/taglibs.jsp" %>
<%@page import="org.mmbase.bridge.*,java.io.*" %>
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
    <title>Import script for adding employees from a csv file</title>
</head>
<body style="overflow:auto;">
<mm:log jspvar="log">
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<%! public Date getDate(String thisDate,int defaultYear) {
	int thisDay = 1;
	int thisMonth = 1;
	int thisYear = defaultYear;
	if(thisDate.indexOf("-")>-1){
		int sPos = thisDate.indexOf("-");
		thisDay = (new Integer(thisDate.substring(0,sPos))).intValue();
		thisDate = thisDate.substring(sPos+1);
		sPos = thisDate.indexOf("-");
		thisMonth = (new Integer(thisDate.substring(0,sPos))).intValue()-1;
		thisDate = thisDate.substring(sPos+1);
		thisYear = 1900 + (new Integer(thisDate)).intValue();
	}
	Calendar cal = Calendar.getInstance();
	cal.set(thisYear,thisMonth,thisDay);
	return cal.getTime();
} %>
<%
String [] fields = { "externid", "lastname", "suffix", "initials", "gender", "firstname", "dayofbirth", "privatephone", "companyphone", "cellularphone", "email", "job" };
String [] values = new String[fields.length];

String dataFile = NMIntraConfig.getIncomingDir() + "medewerkers.csv";

BufferedReader dataFileReader = new BufferedReader(new FileReader(dataFile));
String nextLine = dataFileReader.readLine() + ";";
while(!"null;".equals(nextLine.trim())) {
  String sLog = "";
  for(int i=0; i<fields.length; i++) {
      int cPos = nextLine.indexOf(";");
      if(cPos!=-1) {
        values[i] = nextLine.substring(0,cPos);
        nextLine = nextLine.substring(cPos+1);
      } else {
        log.info("error on trying to set " + fields[i] + " from " + nextLine);
      }
      sLog += fields[i] + "=" + values[i] + "; ";
  }
  log.info(sLog);
  %>
  <mm:remove referid="thisemployee" />
  <mm:createnode type="medewerkers" id="thisemployee">
    <%
    for(int i=0; i<fields.length; i++) {
      if(fields[i].indexOf("phone")!=-1) {
        values[i] = values[i].replace('(',' ').replace(')','-').replaceAll(" ","");
        
      }
      if("gender".equals(fields[i])) {
        values[i] = ("De heer".equals(values[i]) ? "1" : "0");
      }
      if("dayofbirth".equals(fields[i])) {
        values[i] = "" + (getDate(values[i],1900).getTime()/1000);
      }
      %>
      <mm:setfield name="<%= fields[i] %>"><%= values[i] %></mm:setfield>
      <%
    } 
    %>
    <mm:setfield name="importstatus">active</mm:setfield>
  </mm:createnode>
  <%
  nextLine = dataFileReader.readLine() + ";";
}
dataFileReader.close();
%>
Employees are imported.
</mm:cloud>
</mm:log>
<body>
</html>