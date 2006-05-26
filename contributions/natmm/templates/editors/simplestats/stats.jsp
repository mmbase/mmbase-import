<%@include file="../includes/templateheader.jsp" %>
<mm:cloud>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

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

<%  Calendar cal = Calendar.getInstance();
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
%>


<%  SimpleDateFormat formatter = new SimpleDateFormat("EEE d MMM yyyy");
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
%>

<%  String templateTitle = "";
    pageId = "statistieken";
    articleId = fromTime + "_" + toTime + "_" + selection;
%>

<%@include file="../includes/cacheopen.jsp" %>

<html>
<head>
    <link rel="stylesheet" type="text/css" href="../css/editors.css">
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
<body>
<form name="date" method="post">
<table cellspacing="0" cellpadding="0" border="0">
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
                onmouseover="changeImages('this', '../media/go_mo.gif'); window.status=''; return true;"
                onmouseout="changeImages('this', '../media/go.gif'); window.status=''; return true;">
                <img alt="Toon deze periode" src="../media/go.gif" border='0' name='this'></a>
        </td>
        <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
        <% if(period>0) { %>
        <td>
            <a href="stats.jsp?action=previous" onClick="return startSearch(this);"
                onmouseover="changeImages('previous', '../media/previous_mo.gif'); window.status=''; return true;"
                onmouseout="changeImages('previous', '../media/previous.gif'); window.status=''; return true;">
                <img alt="Toon vorige periode" src="../media/previous.gif" border='0' name='previous'></a>
        </td>
        <td>
            <a href="stats.jsp?action=next" onClick="return startSearch(this);"
                onmouseover="changeImages('next', '../media/next_mo.gif'); window.status=''; return true;"
                onmouseout="changeImages('next', '../media/next.gif'); window.status=''; return true;">
                <img alt="Toon volgende periode" src="../media/next.gif" border='0' name='next'></a>
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
    if(period>0) timeConstraint += " AND mmevents.start < " + toTime ; %>

<%@include file="saveToday.jsp" %>

<%  int maxPageCount = 1;
    int totalPages = 0; 
    Hashtable pageCounts = new Hashtable();
%>
<%-- the following count does not take care of double and not attached pages,
    this will lead to minor deviations when using the selection --%>
<mm:listnodes type="page">
    <mm:field name="number" jspvar="page_number" vartype="String" write="false">
        <% int pageCount = 0; %>
        <mm:list nodes="<%= page_number %>" path="page,posrel,mmevents" fields="posrel.pos" 
            constraints="<%= timeConstraint %>" >
            <mm:field name="posrel.pos" jspvar="page_count" vartype="Integer" write="false">
                <% pageCount += page_count.intValue(); %>
            </mm:field>
        </mm:list>
        <%  if(pageCount>maxPageCount) maxPageCount = pageCount; 
            Integer numberOfPages = (Integer) pageCounts.get(new Integer(pageCount));
            if(numberOfPages==null) numberOfPages = new Integer(0);
            pageCounts.put(new Integer(pageCount),new Integer(numberOfPages.intValue()+1)); 
            totalPages++;
        %>
    </mm:field>
</mm:listnodes>

<%-- throw away pages untill selection is satisfied --%>
<%  int pageCountTD = 0;
    int surplus = totalPages - selection;
    if(selection==-1) surplus = 0;
    while(surplus>0) {
        Integer numberOfPages = (Integer) pageCounts.get(new Integer(pageCountTD));
        if(numberOfPages==null) {
            pageCountTD++;
        } else {
            int nOP = numberOfPages.intValue();
            if(surplus>=nOP) {
                pageCounts.remove(new Integer(pageCountTD));
                totalPages = totalPages - nOP;
                pageCountTD++;
            } else {
                pageCounts.put(new Integer(pageCountTD),new Integer(nOP-surplus));
                totalPages = selection;
            }
            surplus = totalPages - selection;
        }
    }
%>

<% int visitorsCount = 0; %>
<mm:list path="mmevents" constraints="<%= timeConstraint %>" >
    <%-- connected to a page ? --%>
    <% boolean isStatEvent = false; %>
    <mm:field name="mmevents.number" jspvar="mmevents_number" vartype="String" write="false">
        <mm:list nodes="<%= mmevents_number %>" path="mmevents,posrel,page" max="1">
                <% isStatEvent = true; %>
        </mm:list>
    </mm:field>
    <% if(isStatEvent) {%>
        <mm:field name="mmevents.name" jspvar="visitors_count" vartype="Integer" write="false">
            <% visitorsCount += visitors_count.intValue(); %>
        </mm:field>
    <% } %>
</mm:list>

<% int rowCount = 0; %>
<table cellpadding="0" cellspacing="0" >
<tr bgcolor="EEEEEE">
    <td colspan="3">Bezoekers aantal</td>
    <td>
        <img src="../../media/bar-orange.gif" alt="" width="<%= (100*visitorsCount / maxPageCount) %>" height="5" border=0>&nbsp;(<%= visitorsCount %>)
    </td>
</tr>
<mm:listnodes type="site" orderby="title" directions="UP">
    <tr><td class="lightgrey" colspan="4"><mm:field name="title" /></td></tr>
    <mm:related path="posrel,page" fields="page.number,page.title,posrel.pos"
                constraints="posrel.pos='1'">
        <mm:field name="page.number" jspvar="page_number" vartype="String" write="false">
        <mm:field name="page.title" jspvar="page_title" vartype="String" write="false">
                <%@include file="pageStats.jsp" %>
        </mm:field>
        </mm:field>
    </mm:related>
    <mm:related path="posrel,pijler" orderby="posrel.pos" directions="UP"  fields="pijler.number">
        <tr <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>>
        <td>&nbsp;</td><td><mm:field name="pijler.title" /></td><td>&nbsp;</td><td>&nbsp;</td></tr>
        <mm:field name="pijler.number" jspvar="pijler_number" vartype="String" write="false">
            <mm:node number="<%= pijler_number %>">
                <mm:related path="posrel,page" fields="page.number,page.title,posrel.pos"
                                orderby="posrel.pos" directions="UP">
                    <mm:field name="page.number" jspvar="page_number" vartype="String" write="false">
                    <mm:field name="page.title" jspvar="page_title" vartype="String" write="false">
                        <%@include file="pageStats.jsp" %>
                    </mm:field>
                    </mm:field>
                    <%-- lets look whether there are subpages under this page --%>
                <mm:field name="page.number" jspvar="super_page" vartype="String" write="false">
                <mm:list nodes="<%= super_page %>" path="page1,dposrel,page2" fields="page2.title,page2.number"
                    orderby="dposrel.pos" directions="UP" >
                    <mm:field name="page2.number" jspvar="page_number" vartype="String" write="false">
                    <mm:field name="page2.title" jspvar="page_title" vartype="String" write="false">
                        <% page_title = "&nbsp;&nbsp;&nbsp;" + page_title; %>
                        <%@include file="pageStats.jsp" %>
                    </mm:field>
                    </mm:field>
                </mm:list>
                </mm:field>
                </mm:related>
            </mm:node>
        </mm:field>
    </mm:related>
</mm:listnodes>
<tr><td class="lightgrey" colspan="4">Overig</td></tr>
<mm:node number="search"> <%-- zoekresultaten --%>
    <mm:field name="number" jspvar="page_number" vartype="String" write="false">
    <mm:field name="title" jspvar="page_title" vartype="String" write="false">
            <%@include file="pageStats.jsp" %>
    </mm:field>
    </mm:field>
</mm:node>
</table>
<%@include file="deleteToday.jsp" %>

</body>
</html>
<%@include file="../includes/cacheclose.jsp" %>

<%-- flush the statistics if it does not fall in the past --%>
<% if(!isPast) { %>
    <cache:flush key="<%= cacheKey %>" scope="application" />
<% } %>
</mm:cloud>

