<%@include file="/taglibs.jsp" %>

<%@page import="java.util.*" %>
<%@page import="java.text.*" %>
<%@page import="org.mmbase.bridge.Node" %>
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
	public String getName(Node thisEmployee) {
		String name = thisEmployee.getStringValue("firstname");
		if(!thisEmployee.getStringValue("suffix").equals("")) {
		   name += " " + thisEmployee.getStringValue("suffix");
		}
		name += " " + thisEmployee.getStringValue("lastname");
		return name;
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

int selection = 9999;
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

String pageId = request.getParameter("page");
if(pageId==null) { pageId = "-1"; }
String ownerId = request.getParameter("owner");
if(ownerId==null) { ownerId = ""; }
%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<% TreeMap ownersMap = new TreeMap(); %>
<mm:listnodes type="items">
	<mm:relatednodes type="medewerkers" jspvar="thisEmployee">
	   <% ownersMap.put(thisEmployee.getStringValue("number"),getName(thisEmployee)); %>
	</mm:relatednodes>
	<mm:related path="posrel,pagina,contentrel,medewerkers">
		<mm:node element="medewerkers" jspvar="thisEmployee">
		   <% ownersMap.put(thisEmployee.getStringValue("number"),getName(thisEmployee)); %>
		</mm:node>
	</mm:related>
</mm:listnodes>
<html>
<head>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/color/wizard.css" type="text/css" rel="stylesheet"/>
   <link href="<mm:url page="<%= editwizard_location %>"/>/style/layout/wizard.css" type="text/css" rel="stylesheet"/>
   <title>Shop Stats</title>
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
		var page = document.forms[0].elements["page"].value;
		var owner = document.forms[0].elements["owner"].value;
		href += "&day=" + day + "&month=" + month + "&year=" + year + "&period=" + period + "&selection=" + selection;
		href += "&page=" + page + "&owner=" + owner; 
		document.location = href; 
	    return false; 
	}
	</script>
</head>
<bodystyle="overflow:auto;" leftmargin="10" topmargin="10" marginwidth="0" marginheight="0">
<form name="date" method="post">
<table class="formcontent" style="width:500px;">
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
			<a href="shopstats.jsp?action=this" onClick="return startSearch(this);"
				onmouseover="changeImages('this', 'media/go_mo.gif'); window.status=''; return true;"
				onmouseout="changeImages('this', 'media/go.gif'); window.status=''; return true;">
				<img alt="Toon deze periode" src="media/go.gif" border='0' name='this'></a>
		</td>
		<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
		<% if(period>0) { %>
		<td>
			<a href="shopstats.jsp?action=previous" onClick="return startSearch(this);"
				onmouseover="changeImages('previous', 'media/previous_mo.gif'); window.status=''; return true;"
				onmouseout="changeImages('previous', 'media/previous.gif'); window.status=''; return true;">
				<img alt="Toon vorige periode" src="media/previous.gif" border='0' name='previous'></a>
		</td>
		<td>
			<a href="shopstats.jsp?action=next" onClick="return startSearch(this);"
				onmouseover="changeImages('next', 'media/next_mo.gif'); window.status=''; return true;"
				onmouseout="changeImages('next', 'media/next.gif'); window.status=''; return true;">
				<img alt="Toon volgende periode" src="media/next.gif" border='0' name='next'></a>
		</td>
		<% } %>
	</tr>
</table>
<table  class="formcontent" style="width:500px;">
	<tr><td>Selectie</td><td>Item</td><td>Eigenaar</td></tr>
	<tr>
		<td>
		<select name="selection">
			<option value="10" <% if(selection==10){ %> selected <% } %>>top 10</option>
			<option value="25" <% if(selection==25){ %> selected <% } %>>top 25</option>
			<option value="50" <% if(selection==50){ %> selected <% } %>>top 50</option>
			<option value="9999" <% if(selection==9999){ %> selected <% } %>>alles</option>
		</select>
		</td>
		<td>
		<select name="page">
			<option value="-1">alles</option>
		<mm:list nodes="shop" path="rubriek,posrel,pagina" orderby="posrel.pos" directions="UP">
		   <mm:last inverse="true"> <% // last page is shopping cart %>
			<mm:field name="pagina.number" vartype="String" jspvar="dummy" write="false">
			<option value="<%= dummy %>" <% if(pageId.equals(dummy)) { %> selected <% } 
			%> ></mm:field><mm:field name="pagina.titel"/></option>
			</mm:last>
		</mm:list>
		</select>
		</td>
		<td>
		<select name="owner">
			<option value="-1">alles</option>
			<%	while(ownersMap.size()>0) { 
					String thisOwner = (String) ownersMap.firstKey();
					String ownerName = (String) ownersMap.get(thisOwner); 
					%>
      			<option value="<%= thisOwner %>" <%= (ownerId.equals(thisOwner)? "selected" : "" ) 
      			      %>><%= ownerName %></option>
      			<%
      			ownersMap.remove(thisOwner);
				}
			%>
		</select>
		</td>
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

<%	// ************* Read response table ********************

String responseTitle = "IntraShop";

String timeConstraint =  "responses.responsedate > " + fromTime;
if(period>0) { timeConstraint += " AND responses.responsedate < " + toTime; }

String allConstraint = timeConstraint + " AND responses.title='" + responseTitle + "'"; 

String pageConstraint = "";
if (!pageId.equals("-1")) { pageConstraint = "pagina.number=" + pageId; }

String ownerConstraint = "";
if (ownerId!="") { ownerConstraint = "medewerkers.number=" + ownerId; }

int maxPageCount = 1;
int totalPages = 0; 
Hashtable orderCounts = new Hashtable();
Hashtable orderSumCounts = new Hashtable();
int maxSum = 1;
int maxWidth = 250;

boolean checkOnOwner = false;
%>
<mm:listnodes type="responses" orderby="responses.responsedate" directions="UP" constraints="<%= allConstraint %>" >
	<%	
	int i=0; 
	boolean containsOrderedProduct = true; 
	while (containsOrderedProduct) {
	  %>
		<mm:field name="<%= "question"+ (i+1) %>" vartype="String" jspvar="shop_itemId" write="false">
			<%
			if (shop_itemId==null || shop_itemId.equals("")) {
        containsOrderedProduct = false;
      } else {
        %>
        <mm:field name="<%= "answer"+ (i+1) %>" vartype="Integer" jspvar="numberOfItems" write="false">
          <mm:node number="<%= shop_itemId %>">
          <%
          boolean isVisible = true;
          if (!ownerId.equals("")) {
            isVisible = false;
            %><mm:related path="posrel,medewerkers" constraints="<%= ownerConstraint %>"><%
              isVisible = true; 
            %></mm:related><%
           
            %><mm:related path="posrel,pagina,contentrel,medewerkers" constraints="<%= ownerConstraint %>"><%
              isVisible = true; 
            %></mm:related><%
          }
          
          if (isVisible&&!pageId.equals("")) {
            isVisible = false;
            %><mm:related path="posrel,pagina" constraints="<%= pageConstraint %>"><%
              isVisible = true;
            %></mm:related><%
          }		
            
          if(!checkOnOwner||isVisible) {
            Integer tmp = (Integer) orderCounts.get(shop_itemId);
            if (tmp==null) { tmp = new Integer(0); }
            orderCounts.put(shop_itemId, new Integer(tmp.intValue() + 1));
            int sum = numberOfItems.intValue();
            tmp = (Integer) orderSumCounts.get(shop_itemId);
            if (tmp!=null) { sum += tmp.intValue(); }
            if (sum > maxSum) { maxSum = sum; }
            orderSumCounts.put(shop_itemId, new Integer(sum));
           }
           i++;
           if (i==100) { containsOrderedProduct = false; }
          %>
          </mm:node>
        </mm:field>
        <%
      }
      %>
    </mm:field>
    <%
  }
  %>
</mm:listnodes>
<%--
orderSumCounts: <%= orderSumCounts %><br/>
orderCounts: <%= orderCounts %><br/>
--%>
<%
int rowCount = 0; 
String pageName = "";
boolean isOutpage = false;

Iterator it = orderSumCounts.keySet().iterator();
int count=0;                                       // number of products
String[][] tmp = new String[100][2];               // sorted array of orderSumCounts
if (it.hasNext()) {     
	tmp[0][0] = (String) it.next();
	tmp[0][1] = ((Integer) orderSumCounts.get(tmp[0][0])).toString();
	count++;
}
while(it.hasNext()) {
	String thisShopItem = (String) it.next();
	int thisSum = ((Integer) orderSumCounts.get(thisShopItem)).intValue();
	int i=0;
	while(i<count && thisSum < Integer.parseInt(tmp[i][1])) {
		i++;
	}
	if (i<selection) {
		for(int k = count-1; k>=i; k--) {
			tmp[k+1][0] = tmp[k][0];
			tmp[k+1][1] = tmp[k][1];
		}
		tmp[i][0] = thisShopItem;
		tmp[i][1] = String.valueOf(thisSum);
		if (count<selection) count++;
	}
}
if (count>0) {
	 String allShopItems = "";
	 String tmpSepar = "";
	 for(int i=0;i<count;i++) {
		 allShopItems += tmpSepar + tmp[i][0];
		 tmpSepar = ",";
   }
   // *** for all pages related to a selected shop_item
   %>
   <table class="formcontent" style="width:500px;">
   	<tr <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>>
   		<td width="100"><b>Pagina</b></td>
   		<td width="100"><b>Product</b></td>
   		<td><b>Aantal (aantal bestellingen / aantal bestelde producten)</b></td>
   	</tr>
    <mm:list nodes="<%= allShopItems %>" path="items,posrel,pagina"
            orderby="pagina.titel" directions="UP" distinct="yes" fields="pagina.number">
      	<mm:field name="pagina.number" vartype="String" jspvar="pageNumber" write="false">
      	   <% 
           isOutpage = false; 
      	   for(int i=0;i<count;i++) { // *** show all the items that belong to this page **
      	      
      	      String shop_itemId = tmp[i][0];
      	      boolean belongsToPage = false;
      	      %><mm:list nodes="<%= shop_itemId %>" path="items,posrel,pagina" constraints="<%= "pagina.number = '" + pageNumber + "'" %>"><%
      	         belongsToPage = true;
      	      %></mm:list><%
      	      
      	      if(belongsToPage) {   	      
         	   	if (!isOutpage) { 
         				isOutpage = true; 
                %>
         			  <tr <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>><td colspan="3"><mm:field name="pagina.titel" /></td></tr>
                <%	
              }
              Integer thisCounts = (Integer) orderCounts.get(shop_itemId);
              Integer thisSumCounts = (Integer) orderSumCounts.get(shop_itemId);
              if (thisCounts==null) { thisCounts = new Integer(0); }
              if (thisSumCounts==null) { thisSumCounts = new Integer(0); }
              %>
              <tr <% if(rowCount%2==0) { %> bgcolor="EEEEEE" <% } rowCount++; %>>
                <td></td>
                <td><mm:node number="<%= shop_itemId %>"><mm:field name="titel" /></mm:node>&nbsp;</td>
                <td><img src="../img/bar-orange.gif" alt="" width="<%= maxWidth*thisSumCounts.intValue()/maxSum %>" height="5" border=0>
                  &nbsp;(<%= thisCounts %>/<%= thisSumCounts %>)</td>
              </tr>
              <% 
         	   } 
      	   } 
           %>
      	</mm:field>
    </mm:list>
    </table><%	
}
%>

</body>
</html>
</mm:cloud>

