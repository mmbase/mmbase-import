<%@include file="/taglibs.jsp" %>
<%@page import="java.text.SimpleDateFormat" %>
<mm:cloud method="http" rank="basic user" jspvar="cloud">

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

String action = (String) request.getParameter("action");
if(action==null){ action = "this"; }

int selection = -1;
String selectionId = (String) request.getParameter("selection");
if(selectionId!=null){ selection = (new Integer(selectionId)).intValue(); }

boolean isPast = false;

SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy");
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
String fromStr= formatter.format(cal.getTime());
cal = addPeriod(cal,period); 
long toTime = (cal.getTime().getTime()/1000);
if(toTime<(dd.getTime()/1000)) { isPast = true; }
cal.add(Calendar.DATE,-1);
String untillAndIncludingStr = formatter.format(cal.getTime());

String cacheKey = "stats" + fromTime + "_" + toTime + "_" + selection;
int expireTime =  3600*24*365; // cache for one year
%>
<cache:cache key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<!-- <%= new java.util.Date() %> -->

<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Simple Stats</title>
   <script language="JavaScript1.2" src="../scripts/mouseover.js" >
   </script>
   <script>
    function startSearch(el) {
        var href = el.getAttribute("href"); 
        var day = document.forms[0].elements["day"].value;
        var month = document.forms[0].elements["month"].value;
        var year = document.forms[0].elements["year"].value;
        var period = document.forms[0].elements["period"].value;
        var selection = document.forms[0].elements["selection"].value;
        href += "&day=" + day + "&month=" + month + "&year=" + year + "&period=" + period + "&selection=" + selection; 
        document.location = href; 
        return false; 
    }
    </script>
</head>
<body style="overflow:auto;" leftmargin="10" topmargin="10" marginwidth="0" marginheight="0">
<form name="date" method="post">
<table class="formcontent" style="width:500px;">
    <tr><td>Dag</td><td>Maand</td><td>Jaar</td><td>Periode</td><td>Selectie</td></tr>
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
        </td><td>
        <select name="selection">
            <option value="10" <% if(selection==10){ %> selected <% } %>>top 10</option>
            <option value="25" <% if(selection==25){ %> selected <% } %>>top 25</option>
            <option value="50" <% if(selection==50){ %> selected <% } %>>top 50</option>
            <option value="-1" <% if(selection==-1){ %> selected <% } %>>alles</option>
        </select>
        </td>
        <td>
            <a href="stats.jsp?action=this" onClick="return startSearch(this);"
                onmouseover="changeImages('this', 'media/go_mo.gif'); window.status=''; return true;"
                onmouseout="changeImages('this', 'media/go.gif'); window.status=''; return true;">
                <img alt="Toon deze periode" src="media/go.gif" border='0' name='this'></a>
        </td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <% if(period>0) { %>
        <td>
            <a href="stats.jsp?action=previous" onClick="return startSearch(this);"
                onmouseover="changeImages('previous', 'media/previous_mo.gif'); window.status=''; return true;"
                onmouseout="changeImages('previous', 'media/previous.gif'); window.status=''; return true;">
                <img alt="Toon vorige periode" src="media/previous.gif" border='0' name='previous'></a>
        </td>
        <td>
            <a href="stats.jsp?action=next" onClick="return startSearch(this);"
                onmouseover="changeImages('next', 'media/next_mo.gif'); window.status=''; return true;"
                onmouseout="changeImages('next', 'media/next.gif'); window.status=''; return true;">
                <img alt="Toon volgende periode" src="media/next.gif" border='0' name='next'></a>
        </td>
        <% } %>
    </tr>
</table>
</form>

<% if(period==-1){ %>
    Statistieken vanaf <%= fromStr %>
<% } else if(period==1){ %>
    Statistieken voor <%= fromStr %>
<% } else { %>
    Statistieken van <%= fromStr %> tot en met <%= untillAndIncludingStr %>
<% } %>
<br><br>

<%  String timeConstraint =  "mmevents.start > " + fromTime;
    if(period>0) timeConstraint += " AND mmevents.start < " + toTime ; 
	 SimpleStats ss = new SimpleStats();
	 ss.saveLast(application);
	 int[] res = ss.calculate(cloud,timeConstraint,selection,application);
	 int maxPageCount = res[0];
	 int visitorsCount = res[1];
	
	 int rowCount = 0; %>
<table class="formcontent" style="width:500px;">
<tr <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>>
    <td colspan="3">Bezoekers aantal</td>
    <td>
        <img src="media/bar-orange.gif" alt="" width="<%= (100*visitorsCount / maxPageCount) %>" height="5" border=0>&nbsp;(<%= visitorsCount %>)
    </td>
</tr>
<mm:listnodes type="rubriek" constraints="level='1'">
  <mm:node jspvar="subsite">
    <tr>
      <th colspan="4" <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>>
         <mm:field name="naam"/>
      </th>
    </tr>      
    <%
    // show all subObjects for the rootId, both pages and rubrieken
    RubriekHelper rubriekHelper = new RubriekHelper(cloud);
    
    TreeMap [] nodesAtLevel = new TreeMap[10];
    String rootId = "home";
    nodesAtLevel[0] = (TreeMap) rubriekHelper.getSubObjects(subsite.getStringValue("number"),true);
    int depth = 0;
    
    // invariant: depth = level of present leafs (root has level 0)
    while(depth>-1&&depth<10) { 
      if(nodesAtLevel[depth].isEmpty()) {
         
     // if this nodesAtLevel is empty, try one level back
         depth--; 
      }
      if(depth>-1&&!nodesAtLevel[depth].isEmpty()) {
    
        // show all subObjects, both pages and rubrieken
        while(! nodesAtLevel[depth].isEmpty()) { 
      
          Integer thisKey = (Integer) nodesAtLevel[depth].firstKey();
          String sThisObject = (String) nodesAtLevel[depth].get(thisKey);
          nodesAtLevel[depth].remove(thisKey);
          %><mm:node number="<%= sThisObject %>" jspvar="thisObject"
            ><mm:nodeinfo  type="type" write="false" jspvar="nType" vartype="String"><%
              if(nType.equals("pagina")) { // show page
                
                // the page can be a redirect to another page
                String sThisPage = sThisObject;
                %><mm:related path="rolerel,pagina" searchdir="destination"
                  ><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"><%
                    sThisPage = pagina_number; 
                  %></mm:field
                ></mm:related>
                <mm:node number="<%= sThisPage %>">
                  <% String page_number = sThisPage; %>
                  <mm:field name="titel" jspvar="page_title" vartype="String" write="false">
                           <%@include file="pageStats.jsp" %>
                    </mm:field>
                </mm:node>
                <%
                
              } else { // show rubriek, which is a link to the first page in the rubriek
                %>
                <tr <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>>
                      <td>&nbsp;</td><td><mm:field name="naam" /></td><td>&nbsp;</td><td>&nbsp;</td>
                </tr>
                <%
                depth++;
                nodesAtLevel[depth] = (TreeMap) rubriekHelper.getSubObjects(sThisObject,true);
              }
            %></mm:nodeinfo
          ></mm:node><%
        }
      }
    }
    %>
  </mm:node>
</mm:listnodes>
</table>
<br/>
<br/>
</body>
</html>
</cache:cache>

<% // flush the statistics if it does not fall in the past
if(!isPast) {
   %>
   <cache:flush key="<%= cacheKey %>" scope="application" />
   <%
} %>
</mm:cloud>

