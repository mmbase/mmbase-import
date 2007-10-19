<%@page import="nl.leocms.authorization.*,org.mmbase.bridge.*,nl.leocms.util.*,nl.leocms.workflow.*,nl.leocms.content.*" %>
<%@include file="/taglibs.jsp"  %>

<%
   Calendar cal = Calendar.getInstance();
   int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
   
   // show warning within office times
   if (hourOfDay > 8 && hourOfDay < 17) {
%>

   <script language="JavaScript">
   <!--
   alert("Let op: i.v.m. de zware berekeningen die samenhangen met het opvragen van statistieken,\n"
         + "is het niet gewenst om statistieken op te vragen binnen kantoortijden (van 09.00 tot 17.00).");
   -->
   </script>

<% } %>

<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:locale language="nl">
<%@ page import="java.util.*" %>
<%! public Calendar addPeriod(Calendar cal, int period) {
        int offset = 1;
        if(period<0) offset = -1;
        if(Math.abs(period)==365) {
            cal.add(Calendar.YEAR,offset);
        } else if(Math.abs(period)==31) {
            cal.add(Calendar.MONTH,offset);
        } else {
            cal.add(Calendar.DATE,period);
        }
        return cal;
    }
%>

<%

int maxWidth = 300;
int tableWidth = 500;

Date dd = new Date();

cal.setTime(dd);
cal = addPeriod(cal,-7); // show last week

int day = cal.get(Calendar.DAY_OF_MONTH);
String dayId = (String) request.getParameter("day");
if(dayId!=null){ day = (new Integer(dayId)).intValue(); }

int month = cal.get(Calendar.MONTH);
String monthId = (String) request.getParameter("month");
if(monthId!=null){ month = (new Integer(monthId)).intValue(); }

int year = cal.get(Calendar.YEAR);
String yearId = (String) request.getParameter("year");
if(yearId!=null){ year = (new Integer(yearId)).intValue(); }

int period = 7;
String periodId = (String) request.getParameter("period");
if(periodId!=null){ period = (new Integer(periodId)).intValue(); }

String listtype = "Betaalwijze";
String listypeID = request.getParameter("listtype");
if(listypeID!=null){ listtype = listypeID;}

String statstype = "inschrijvingen";
String statstypeID = request.getParameter("statstype");
if(statstypeID!=null){ statstype = statstypeID;}

String statsperiod = "";
String statsperiodID = request.getParameter("statsperiod");
if(statsperiodID!=null){statsperiod = statsperiodID;}

String action = (String) request.getParameter("action");
if(action==null){ action = "this"; }

int selection = -1;
String selectionId = (String) request.getParameter("selection");
if(selectionId!=null){ selection = (new Integer(selectionId)).intValue(); }

cal.set(year,month,day,0,0,0);
if(action.equals("next")) {
   cal = addPeriod(cal,period);
   day = cal.get(Calendar.DAY_OF_MONTH);
   month = cal.get(Calendar.MONTH);
   year = cal.get(Calendar.YEAR);
} else if(action.equals("previous")) {
   cal = addPeriod(cal,-period);
   day = cal.get(Calendar.DAY_OF_MONTH);
   month = cal.get(Calendar.MONTH);
   year = cal.get(Calendar.YEAR);
}
long fromTime = (cal.getTime().getTime()/1000);
cal = addPeriod(cal,period);
long toTime = (cal.getTime().getTime()/1000);

%>
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
    <script language="JavaScript1.2" src="../scripts/mouseover.js" >
    </script>
    <script>
    function startSearch(obj) {
        showMessage('Uw opdracht wordt uitgevoerd. Een moment geduld a.u.b.');
        var href = obj.getAttribute("href");
        var day = document.forms[0].elements["day"].value;
        var month = document.forms[0].elements["month"].value;
        var year = document.forms[0].elements["year"].value;
        var period = document.forms[0].elements["period"].value;
        var selection = document.forms[0].elements["selection"].value;
		  var listtype = document.forms[0].elements["listtype"].value;
		  var statstype = document.forms[0].elements["statstype"].value;
		  href += "&day=" + day + "&month=" + month + "&year=" + year + "&period=" + period + "&selection=" + selection + "&listtype=" + listtype + "&statstype=" + statstype;
        document.location = href;
        return false;
    }
    function showMessage(contentString){
      theTarget = document.getElementById("message");
      if(theTarget != null){
         theTarget.innerHTML = contentString;
      }
   }
   </script>
</head>
<body style="overflow:auto;" leftmargin="10" topmargin="10" marginwidth="0" marginheight="0">
	<h2>CAD Statistieken (aantallen zijn indicatief)</h2>
   <div id="message" style="color:red;font-weight:bold">&nbsp;</div>
   <form name="date" method="post">
		<table class="formcontent" style="width:<%= tableWidth %>px;">
          <tr><td colspan="5">Periode waarin de activiteit valt:</td></tr>
          <tr><td>Dag</td><td>Maand</td><td>Jaar</td><td>Periode</td><%-- <td>Selectie</td> --%></tr>
          <tr><td>
             <select name="day">
                <% for(int i=1; i<32; i++) { %>
                   <option value="<%= i %>" <% if(day==i){ %> selected <% } %>><%= i %></option>
                <% } %>
             </select>
             </td><td>
             <select name="month">
                <option value="0" <% if(month==0){ %> selected <% } %>>januari</option>
                <option value="1" <% if(month==1){ %> selected <% } %>>februari</option>
                <option value="2" <% if(month==2){ %> selected <% } %>>maart</option>
                <option value="3" <% if(month==3){ %> selected <% } %>>april</option>
                <option value="4" <% if(month==4){ %> selected <% } %>>mei</option>
                <option value="5" <% if(month==5){ %> selected <% } %>>juni</option>
                <option value="6" <% if(month==6){ %> selected <% } %>>juli</option>
                <option value="7" <% if(month==7){ %> selected <% } %>>augustus</option>
                <option value="8" <% if(month==8){ %> selected <% } %>>september</option>
                <option value="9" <% if(month==9){ %> selected <% } %>>oktober</option>
                <option value="10" <% if(month==10){ %> selected <% } %>>november</option>
                <option value="11" <% if(month==11){ %> selected <% } %>>december</option>
             </select>
             </td><td>
             <select name="year">
                <% for(int i=2000; i<2012; i++) { %>
                   <option value="<%= i %>" <% if(year==i){ %> selected <% } %>><%= i %></option>
                <% } %>
             </select>
             </td><td>
             <select name="period">
                <option value="1" <% if(period==1){ %> selected <% } %>>1 dag</option>
                <option value="7" <% if(period==7){ %> selected <% } %>>1 week</option>
                <option value="31" <% if(period==31){ %> selected <% } %>>1 maand</option>
                <option value="365" <% if(period==365){ %> selected <% } %>>1 jaar</option>
                <option value="-1" <% if(period==-1){ %> selected <% } %>>alles</option>
             </select>
             </td>
             <input type="hidden" name="selection" value="-1" />
             <%--
             <td>
             <select name="selection">
                <option value="10" <% if(selection==10){ %> selected <% } %>>top 10</option>
                <option value="25" <% if(selection==25){ %> selected <% } %>>top 25</option>
                <option value="50" <% if(selection==50){ %> selected <% } %>>top 50</option>
                <option value="-1" <% if(selection==-1){ %> selected <% } %>>alles</option>
             </select>
             </td>
             --%>
             <td>
                 <a href="stats.jsp?action=this" onClick="return startSearch(this);">
                 <img alt="Toon deze periode" src="../img/go.gif" border='0' name='this'></a>
             </td>
	          <td width="100px"></td>
             <% if(period>0) { %>
                <td>
                   <a href="stats.jsp?action=previous" onClick="return startSearch(this);">
                   <img alt="Toon vorige periode" src="../img/left.gif" border='0' name='previous'></a>&nbsp;
                </td>
                <td align="right">
                   <a href="stats.jsp?action=next" onClick="return startSearch(this);">
                   <img alt="Toon volgende periode" src="../img/right.gif" border='0' name='next'></a>
                </td>
             <% } %>
          </tr>
    </table>
	 <br/>
	 <table class="formcontent" style="width:<%= tableWidth %>px;margin-bottom:20px;">
		 <tr>
		 	 <td>Waarop in te delen</td>
			 <td>Wat wordt getoond</td>
		 </tr>
		 <tr>
			 <td>
			 	 <select name="listtype">
				 	 <option value="Betaalwijze" <% if (listtype.equals("Betaalwijze")) {%> selected <% } %>>Betaalwijze</option>
					 <option value="Activiteitstype" <% if (listtype.equals("Activiteitstype")) {%> selected <% } %>>Activiteitstype</option>
					 <option value="Provincie / Natuurgebied" <% if (listtype.equals("Provincie / Natuurgebied")) {%> selected <% } %>>Provincie / Natuurgebied</option>
					 <option value="Bestelwijze" <% if (listtype.equals("Bestelwijze")) {%> selected <% } %>>Bestelwijze</option>
                     <option value="Afdelingen" <% if (listtype.equals("Afdelingen")) {%> selected <% } %>>Afdelingen</option>
				 </select>
			 </td>
			 <td>
			 	 <select name="statstype">
				 	 <option value="inschrijvingen" <% if (statstype.equals("inschrijvingen")) {%> selected <% } %>>aantal inschrijvingen</option>
				 	 <option value="deelnemers" <% if (statstype.equals("deelnemers")) {%> selected <% } %>>aantal deelnemers</option>
				 	 <option value="leden" <% if (statstype.equals("leden")) {%> selected <% } %>>aantal leden</option>
				 	 <option value="niet leden" <% if (statstype.equals("niet leden")) {%> selected <% } %>>aantal niet leden</option>
				 	 <option value="opbrengst" <% if (statstype.equals("opbrengst")) {%> selected <% } %>>totale opbrengst</option>
				 	 <option value="activiteiten" <% if (statstype.equals("activiteiten")) {%> selected <% } %>>aantal activiteiten</option>
				 	 <option value="geannuleerde activiteiten" <% if (statstype.equals("geannuleerde activiteiten")) {%> selected <% } %>>aantal geannuleerde activiteiten</option>
				 </select>
			 </td>
			 <td width="150px"></td>
		 </tr>
	</table>
   <%
	if ( (listtype.equals("Betaalwijze") && statstype.indexOf("activiteiten") > -1) ||
	     (listtype.equals("Bestelwijze") && statstype.indexOf("activiteiten") > -1)  ) {%>
      <div class="formcontent" style="width:<%= tableWidth %>px;margin-bottom:20px;">
         <font color="red">De keuzes "aantal activiteiten" en "aantal geannuleerde activiteiten" heeft geen zin in het geval u "betaalwijze" of "bestelwijze" heeft gekozen. Maak alstublieft een ander keuze.</font>
      </div>

      <%
  	} else if ( listtype.equals("Afdelingen") ) {
  	   %>

	   <jsp:useBean id="extrastats" scope="session" class="nl.leocms.evenementen.stats.ExtraStats" />
	   <table class="formcontent" style="width:<%= tableWidth %>px;margin-bottom:20px;">
  		   <tr>
	   	   <td colspan="3">
	   	      <%= extrastats.getStatsperiod(fromTime,toTime,period) %><br/>
   		   </td>
	  	   	<%
					String attachmentId = extrastats.write(cloud,fromTime,toTime,period);
	         	%>
					<td align="right">
							<mm:node number="<%= attachmentId %>" notfound="skipbody">
							   <a href="<mm:attachment />"><img src="../img/icexcel.gif"></a>
							</mm:node>
					</td>
   	   </tr>
		</table>

      <%
  	} else {
  	   %>
	   <jsp:useBean id="statistics" scope="session" class="nl.leocms.evenementen.stats.OptionedStats" />
	   <table class="formcontent" style="width:<%= tableWidth %>px;margin-bottom:20px;">
  		   <tr>
	   	   <td colspan="3">
	   	      <%= statistics.getStatsperiod(fromTime,toTime,period) %><br/>
   		   </td>
	  	   	<% TreeMap tmStats = statistics.getStatistics(cloud, fromTime,toTime,period,listtype,statstype,true);
					String attachmentId = statistics.write(cloud,fromTime,toTime,period,listtype);
	 		      int rowCount = 1;
					int iTotal = statistics.getTotal();
	         %>
					<td align="right">
						<% if (iTotal!=0) { %>
							<mm:node number="<%= attachmentId %>" notfound="skipbody">
							   <a href="<mm:attachment />"><img src="../img/icexcel.gif"></a>
							</mm:node>
						<% } %>
					</td>
   	   </tr>
		</table>
   	<table class="formcontent" style="width:<%= tableWidth %>px;margin-bottom:20px;">
		   <tr bgcolor="6B98BD">
  			   <td colspan="2"><strong>
  			      <% if(!statstype.equals("opbrengst")) { %>
  			         Totaal aantal <%= statstype %>
  			      <% } else { %>
  			         Totale opbrengst
  			      <% } %></strong></td>
 			   <td>
				   <%-- <img src="../img/bar-orange.gif" alt="" width="<%= maxWidth %>" height="5" border="0"> --%>
				   <% if(statstype.equals("opbrengst")) {
                     %>&euro; <%= (iTotal/100) %><%
                  } else {
				         %><%= iTotal %><%
				      } %>
			  	</td>
			   <td></td>
			</tr>
		      <% Set set = tmStats.entrySet();
   			   Iterator i = set.iterator();
				   while (i.hasNext()){
  					   Map.Entry me = (Map.Entry)i.next();
	   	   		int iCount =  ((Integer) me.getValue()).intValue(); %>
		   	      <tr <% if(rowCount%2==0) { %> bgcolor="6B98BD" <% } rowCount++; %>>
		  		   	   <td colspan="2"><%= (String) me.getKey() %>&nbsp;</td>
							<td>
    							<img src="../img/bar-orange.gif" alt="" width="<%= ( iTotal!=0 ? (maxWidth*iCount)/iTotal : 0 ) %>" height="5" border=0>
    							   (<% if(statstype.equals("opbrengst")) {
                                 %>&euro; <%= (iCount/100) %><%
                              } else {
				                     %><%= iCount %><%
				                  } %>)
						  	</td>
	   	   		  	<td></td>
	  		   	   </tr>
				<% } %>
		</table>
	   <%
	} %>
</form>
<br/>
</body>
</mm:locale>
</mm:cloud>
