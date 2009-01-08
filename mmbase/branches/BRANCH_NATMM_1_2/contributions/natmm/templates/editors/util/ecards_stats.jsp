<%@page import="nl.leocms.authorization.*,org.mmbase.bridge.*,nl.leocms.util.*,nl.leocms.workflow.*,nl.leocms.content.*,
                nl.leocms.evenementen.stats.OptionedStats" %>
<%@include file="/taglibs.jsp"  %>
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

Calendar cal = Calendar.getInstance();
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
Date dFrom = cal.getTime();
long fromTime = (cal.getTime().getTime()/1000);
cal = addPeriod(cal,period);
Date dTo = cal.getTime();
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
		  href += "&day=" + day + "&month=" + month + "&year=" + year + "&period=" + period;
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
	<h2>Ecards statistieken</h2>
	
   <form name="date" method="post">
		<table class="formcontent" style="width:<%= tableWidth %>px;">
          <tr><td>Dag</td><td>Maand</td><td>Jaar</td><td>Periode</td></tr>
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
             <td>
                 <a href="ecards_stats.jsp?action=this" onClick="return startSearch(this);">
                 <img alt="Toon deze periode" src="../img/go.gif" border='0' name='this'></a>
             </td>
	          <td width="100px"></td>
             <% if(period>0) { %>
                <td>
                   <a href="ecards_stats.jsp?action=previous" onClick="return startSearch(this);">
                   <img alt="Toon vorige periode" src="../img/left.gif" border='0' name='previous'></a>&nbsp;
                </td>
                <td align="right">
                   <a href="ecards_stats.jsp?action=next" onClick="return startSearch(this);">
                   <img alt="Toon volgende periode" src="../img/right.gif" border='0' name='next'></a>
                </td>
             <% } %>
          </tr>
    </table>
	 <br/>
	   <table class="formcontent" style="width:<%= tableWidth %>px;margin-bottom:20px;">
  		   <tr>
	   	   <td colspan="4">
					<% OptionedStats os = new OptionedStats(); %>
	   	      <%= os.getStatsperiod(fromTime,toTime,period) %><br/>
   		   </td>
	  	   	<% TreeMap tmStats = new TreeMap();
				   DateUtil du = new DateUtil();
					String sFrom = du.getObjectNumber(cloud,dFrom);
					String sTo = du.getObjectNumber(cloud,dTo);
					cal.set(year,month,day,0,0,0);
					while (sFrom.equals("-1")&&dFrom.before(dTo)){
						cal = addPeriod(cal,1);
						dFrom = cal.getTime();
						sFrom = du.getObjectNumber(cloud,dFrom);
					}
					cal.set(year,month,day,0,0,0);
					dFrom = cal.getTime();
					cal = addPeriod(cal,period);
					while (sTo.equals("-1")&&dFrom.before(dTo)){
						cal = addPeriod(cal,-1);
						dTo = cal.getTime();
						sTo = du.getObjectNumber(cloud,dTo);
					}
	 		      int rowCount = 1;
					int iTotal = 0;
	         %>
				<mm:list path="dossier,posrel,images,related,ecard" constraints="<%= "ecard.number <= " + sTo + " AND ecard.number >= " + sFrom%>"
					fields="dossier.naam,images.title">
					<mm:first>
						<mm:size jspvar="size" vartype="integer" write="false">
						<% iTotal = size.intValue(); %>
						</mm:size>
					</mm:first>
					<mm:field name="dossier.naam" jspvar="dossier_naam" vartype="String" write="false">
						<mm:field name="images.title" jspvar="images_title" vartype="String" write="false">
						<% String sKey = dossier_naam + ":" + images_title;
							Integer iValue = new Integer(1);
							if (tmStats.containsKey(sKey)){
								iValue = new Integer(((Integer)tmStats.get(sKey)).intValue()+1);
							}
							tmStats.put(sKey,iValue);
						%>
						</mm:field>
					</mm:field>
				</mm:list>
   	   </tr>
		</table>
   	<table class="formcontent" style="width:<%= tableWidth %>px;margin-bottom:20px;">
		   <tr bgcolor="6B98BD">
  			   <td colspan="2"><strong>
  			      Totaal aantal verstuurde ecards</strong></td>
 			   <td>(<%= iTotal %>)</td>
			</tr>
		      <% Set set = tmStats.entrySet();
   			   Iterator i = set.iterator();
					String sDossierName = "";
				   while (i.hasNext()){
  					   Map.Entry me = (Map.Entry)i.next();
						String sKey = (String)me.getKey();
						int iColonIndex = sKey.indexOf(":");
						String sRealDossierName = sKey.substring(0,iColonIndex);
						String sImageTitle = sKey.substring(iColonIndex+1);
						if (!sRealDossierName.equals(sDossierName)){
							sDossierName = sRealDossierName; %>
							<tr <% if(rowCount%2==0) { %> bgcolor="6B98BD" <% } rowCount++; %>>
							<td><%= sDossierName %></td><td colspan="2"></td>
							</tr>
					<%	}
	   	   		int iCount =  ((Integer) me.getValue()).intValue(); %>
		   	      <tr <% if(rowCount%2==0) { %> bgcolor="6B98BD" <% } rowCount++; %>>
							<td></td>
		  		   	   <td><%= sImageTitle %>&nbsp;</td>
							<td>
    							<img src="../img/bar-orange.gif" alt="" width="<%= ( iTotal!=0 ? (maxWidth*iCount)/iTotal : 0 ) %>" height="5" border=0>
    							   (<%= iCount %>)
						  	</td>
	  		   	   </tr>
				<% } %>
		</table>
</form>
<br/>
</body>
</mm:locale>
</mm:cloud>
