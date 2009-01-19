<%@page language="java" contentType="text/html;charset=UTF-8" %>
<%@page import="java.util.*" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<html>
<head>
<title></title>
</head>
<body onload="javascript:document.forms[0].submit()">
<mm:cloud name="mmbase" method="http" jspvar="cloud">
<mm:import externid="objectnumber"/>
	<mm:node referid="objectnumber">
   <form action="<%= nl.leocms.applications.NMIntraConfig.getSCorporateEditors() + "vacatures/vacature_receive.jsp" %>" method="POST">
	<% ArrayList al = new ArrayList();
	   al.add("titel"); 
		al.add("embargo"); 
		al.add("verloopdatum"); 
		al.add("functienaam"); 
		al.add("functieomvang"); 
		al.add("duur");
      al.add("omschrijving");
		al.add("omschrijving_de");
		al.add("afdeling"); 
		al.add("functieinhoud"); 
		al.add("functieeisen"); 
		al.add("opleidingseisen"); 
		al.add("competenties"); 
		al.add("salarisschaal"); 
      %>
	   <mm:setfield name="datumvan"><%= ((new Date()).getTime() / 1000) %></mm:setfield>
		<mm:field name="number" jspvar="sNodeNumber" vartype="String">
			<input type="hidden" name="bron" value="<%= sNodeNumber %>" />
		</mm:field>
	   <%	Iterator ial = al.iterator();
		while(ial.hasNext()) {
			String sElem = (String) ial.next(); %>
			<mm:field name="<%= sElem %>" jspvar="sValue" vartype="String">
		      <input type="hidden" name="<%= sElem %>" value="<%= sValue.replaceAll("\"","\'") %>" />
	      </mm:field>
		   <% 
      } 
      // ** add two extra fields to the vacature
      %>
      <mm:related path="posrel,ctexts">
         <mm:field name="posrel.pos" jspvar="iCTextType" vartype="Integer">
            <mm:field name="ctexts.body" jspvar="sValue" vartype="String">
               <input type="hidden" name="<%= (iCTextType.intValue()==1 ? "omschrijving_fra" : "omschrijving_eng" )%>" value="<%= sValue.replaceAll("\"","\'") %>" />
   	      </mm:field>
		   </mm:field>
      </mm:related>
      <input type="hidden" name="ref" value="<%= request.getHeader("referer") %>" />
   </form>
	</mm:node>
</mm:cloud>
</body>
</html>
