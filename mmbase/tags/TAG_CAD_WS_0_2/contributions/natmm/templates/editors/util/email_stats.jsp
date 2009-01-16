<%@page import="nl.leocms.authorization.*,org.mmbase.bridge.*,nl.leocms.util.*,nl.leocms.workflow.*,nl.leocms.content.*" %>
<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<mm:locale language="nl">
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
	 String pageId = request.getParameter("page"); 
	 if(pageId==null){ pageId = ""; }
    pageId = "statistieken";
	 String articleId = request.getParameter("article"); 
	 if(articleId==null){ articleId = ""; }
    articleId = fromTime + "_" + toTime + "_" + selection;
%>
<html>
<head>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
    <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
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
<body leftmargin="10" topmargin="10" marginwidth="0" marginheight="0" style="overflow:auto;">
    <h3>Formulier statistieken</h3>
    <table class="formcontent" style="width:500px;">
       <form name="date" method="post">
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
                 <a href="email_stats.jsp?action=this" onClick="return startSearch(this);">
                 <img alt="Toon deze periode" src="../img/go.gif" border='0' name='this'></a>
             </td>
	          <td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
             <% if(period>0) { %>
                <td>
                   <a href="email_stats.jsp?action=previous" onClick="return startSearch(this);">
                   <img alt="Toon vorige periode" src="../img/left.gif" border='0' name='previous'></a>&nbsp;
                </td>
                <td align="right">
                   <a href="email_stats.jsp?action=next" onClick="return startSearch(this);">
                   <img alt="Toon volgende periode" src="../img/right.gif" border='0' name='next'></a>
                </td>
             <% } %>
          </tr>
       </form>
    </table>
    <table class="formcontent" style="width:500px;margin-bottom:20px;">
        <tr>
           <td colspan="4">
	      <% String sStatistiekenPeriod = "Statistieken "; 
	         if(period==-1){ 
                    sStatistiekenPeriod += "vanaf "  + fromStr;
                 } else if(period==1){ 
                    sStatistiekenPeriod += "voor " + fromStr;
                 } else { 
                    sStatistiekenPeriod += "van " +  fromStr + " tot en met " + untillAndIncludingStr;
                 } %>
	         <%= sStatistiekenPeriod %>
                 <br/><br/>
          </td>
       </tr>

<%  String timeConstraint =  "email.mailedtime > " + fromTime;
    if(period>0) timeConstraint += " AND email.mailedtime < " + toTime ; %>

<%  int totalEmailsCount = 0;
    int totalPages = 0; 
    Hashtable pageCounts = new Hashtable();
%>
<mm:node number="form_template">
   <mm:relatednodes type="pagina">
      <mm:field name="number" jspvar="page_number" vartype="String" write="false">
      	  <% int pageCount = 0; %>
          <mm:list nodes="<%= page_number %>" path="pagina,related,email"  
   	         constraints="<%= timeConstraint %>" >
       	     <mm:first>
         		<mm:size jspvar="page_count" vartype="Integer" write="false">
         	           <% pageCount = page_count.intValue(); %>
         		</mm:size>
	            </mm:first>
	     </mm:list>
   	  <%  
         totalEmailsCount += pageCount;
      	Integer numberOfPages = (Integer) pageCounts.get(new Integer(pageCount));
	      if(numberOfPages==null) numberOfPages = new Integer(0);
   	   pageCounts.put(new Integer(pageCount),new Integer(numberOfPages.intValue()+1)); 
      	totalPages++; %>
      </mm:field>
   </mm:relatednodes>
</mm:node>

<% int rowCount = 0; %>
<tr bgcolor="6B98BD">
    <td><strong>Totaal aantal verstuurde formulieren &nbsp;</strong></td>
    <td>&nbsp;</td>
    <td>
       <% String sParams = "?constraints=" + timeConstraint + "&sp=" + sStatistiekenPeriod ;
          sParams += "&day=" + day + "&month=" + month + "&year=" + year + "&period=" + period + "&selection=" + selection + "&count=";%>
       <a href="email_detail.jsp<%= sParams %><%= totalEmailsCount %>">	
          <img src="../../media/bar-orange.gif" alt="" width="<%= totalEmailsCount %>" height="5" border=0>
       </a>	  
       &nbsp;
       <a href="email_detail.jsp<%= sParams %><%= totalEmailsCount %>">(<%= totalEmailsCount %>)</a>
    </td>
</tr>
<% TreeMap tmPagesRelatedEmailsTree = new TreeMap(); 
   TreeMap tmRootsTree = new TreeMap(); 
   TreeMap tmRoots = new TreeMap(); %>
   <mm:node number="form_template">
      <mm:relatednodes type="pagina" >
         <% boolean bIsRelatedEmail = false;
            Integer iCountEmail = new Integer(0);%>
            <mm:relatednodes type="email" constraints="<%= timeConstraint %>">
               <mm:first>
                   <% bIsRelatedEmail = true; %>
                   <mm:size jspvar="iCount" vartype="Integer">
   	              <% iCountEmail = iCount; %>
		            </mm:size>
	            </mm:first>
            </mm:relatednodes>
	    <% if (bIsRelatedEmail) { %>
	       <mm:field name="number" jspvar="paginaID" vartype="String">
	          <mm:field name="titel" jspvar="sPaginaTitel" vartype="String">
	             <% Vector breadcrumbs = new Vector();
	                String rootID = "";
	                breadcrumbs = PaginaHelper.getBreadCrumbs(cloud, paginaID);
	                rootID = (String) breadcrumbs.get(breadcrumbs.size()-2); %>
	                <mm:node number="<%= rootID%>">
	                   <mm:field name="naam" jspvar="sNaam" vartype="String">
	                      <% tmRoots.put(sNaam,rootID); %>
	                   </mm:field>	
	                </mm:node>		
	                <% tmRootsTree.put(paginaID,rootID);
	                   tmPagesRelatedEmailsTree.put(paginaID,iCountEmail.toString()); %>
	          </mm:field>	
	       </mm:field>
	    <% } %>
    </mm:relatednodes>
   </mm:node>
<% Set set = tmRoots.entrySet(); 
   Iterator i = set.iterator();
   while (i.hasNext()){ 
      Map.Entry me = (Map.Entry)i.next(); 
      String sRootName = (String) me.getKey();%>
      <tr><td class="lightgrey" colspan="3"><%= sRootName %></tr> 
      <% String sRootId = (String) me.getValue(); 
         Set set1 = tmRootsTree.entrySet(); 
         Iterator i1 = set1.iterator(); 
         while (i1.hasNext()){ 
            Map.Entry me1 = (Map.Entry)i1.next();
            String sRootId1 = (String)me1.getValue();
            if (sRootId.equals(sRootId1)) { 
     	         String sPaginaID = (String) me1.getKey();%>
	            <mm:node number="<%= sPaginaID %>">
	            <tr <% if(rowCount%2==0) { %> bgcolor="6B98BD" <% } rowCount++; %>>
                     <td>&nbsp;</td><td><mm:field name="titel"/>&nbsp;</td>
                     <td>
			            <% String sParamsAdd = sParams + tmPagesRelatedEmailsTree.get(sPaginaID) + "&p=" + sPaginaID + "&r=" + sRootId; %>
   	               <a href="email_detail.jsp<%= sParamsAdd %>">
   	                   <img src="../img/bar-orange.gif" alt="" width="<%= tmPagesRelatedEmailsTree.get(sPaginaID) %>" height="5" border=0>
   	   	         </a>
            			<a href="email_detail.jsp<%= sParamsAdd %>">
            			   (<%= tmPagesRelatedEmailsTree.get(sPaginaID) %>)
            			</a>	  
                     </td>
                  </tr> 
   	         </mm:node>
         <% } %>
    <% } %>	
<% } %>
</table>
</body>
</html>
</mm:locale>
</mm:cloud>
