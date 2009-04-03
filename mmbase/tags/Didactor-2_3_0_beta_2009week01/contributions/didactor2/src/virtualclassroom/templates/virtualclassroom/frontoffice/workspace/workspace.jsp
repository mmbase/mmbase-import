<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@ page import="java.util.*"%>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<html>
<head>   
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/virtualclassroom/css/base.css" objectlist="$includePath" referids="$referids" />" /> 
  <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
</head>
<body>
  <mm:import jspvar="theReferids"><mm:write referid="referids"/></mm:import>
  <%
    Vector existingReferids = new Vector();
    if (theReferids!=null) {
      StringTokenizer st = new StringTokenizer(theReferids, ",");
      while (st.hasMoreTokens()) {
    	  existingReferids.add(st.nextToken().toString());        	
      }
    }  
    existingReferids.add("username");
    existingReferids.add("user");
    existingReferids.add("servername");
    existingReferids.add("typeof");
    existingReferids.add("destination");
    /*
    for (int i=0;i<existingReferids.size();i++) {
	  System.out.println(">>####" + existingReferids.get(i));        	
    }
    */
    Enumeration names = request.getParameterNames();
    String parameterName;
    //String parameterValue;
    String parameterList = "";
    while (names.hasMoreElements()){
     parameterName = names.nextElement().toString();
     if(!(existingReferids.contains(parameterName)||existingReferids.contains(parameterName+"?"))){
       %><mm:import externid="<%=parameterName%>"/><%
       parameterList += "," + parameterName; 
     }  
     //parameterValue = request.getParameter(parameterName);    
     //System.out.println(">>" + parameterName + ":" + parameterValue);  
    }  
  %>
  <mm:import id="parameterlist"><%=parameterList%></mm:import>
  <mm:import externid="typeof">1</mm:import>
  <mm:import externid="destination">/virtualclassroom/frontoffice/workspace/index.jsp</mm:import>
  <mm:treeinclude page="$destination" objectlist="$includePath" referids="$referids,destination,typeof,$parameterlist"/>
</body>
</mm:cloud>
</mm:content>			           
