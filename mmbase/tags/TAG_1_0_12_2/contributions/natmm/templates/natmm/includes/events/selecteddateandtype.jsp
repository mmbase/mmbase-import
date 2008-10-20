<%
int numberOfDays = 7;
try {
   int number = cloud.getNode(paginaID).getIntValue("titel_fra");
   if (number > 0) { //Only use this number if the value is valid and filled in (otherwise it returns -1)
      numberOfDays = 7 * number; 
   }
} catch (Exception e) { }

cal.setTime(now);
int iFromDay = cal.get(Calendar.DAY_OF_MONTH);
int iFromMonth = cal.get(Calendar.MONTH)+1;
int iFromYear = cal.get(Calendar.YEAR);

cal.add(Calendar.DATE,numberOfDays);
int iTillDay = cal.get(Calendar.DAY_OF_MONTH);
int iTillMonth = cal.get(Calendar.MONTH)+1;
int iTillYear = cal.get(Calendar.YEAR);
try {
   iFromDay = Integer.parseInt(request.getParameter("from_day"));
   iFromMonth = Integer.parseInt(request.getParameter("from_month"));
   iFromYear = Integer.parseInt(request.getParameter("from_year"));
   iTillDay = Integer.parseInt(request.getParameter("till_day"));
   iTillMonth = Integer.parseInt(request.getParameter("till_month"));
   iTillYear = Integer.parseInt(request.getParameter("till_year"));
} catch (Exception e) {}

String searchParams = "from_day=" + iFromDay
      + "&from_month=" + iFromMonth
      + "&from_year=" + iFromYear
      + "&till_day=" + iTillDay
      + "&till_month=" + iTillMonth
      + "&till_year=" + iTillYear;
if(!provID.equals("-1")) { searchParams += "&prov=" + provID; }
if(!natuurgebiedID.equals("-1")) { searchParams += "&n=" + natuurgebiedID; }

// *** Are activity-type criteria set?
// *** checkedActivityTypes is used in events_searchform; sActivityTypes is used in events/searchresults 
TreeSet checkedActivityTypes = new TreeSet();   
String sActivityTypes = "";
Enumeration enumParamNames = request.getParameterNames();
boolean bFirst = true;

while(enumParamNames.hasMoreElements()) {

   String sParamName = (String) enumParamNames.nextElement();
   if((sParamName.length() > 13) && (sParamName.substring(0, 14).equals("activity_type_"))) {
      String activityTypeNumber =  sParamName.substring(14);
      if(!bFirst) sActivityTypes += " OR ";
      bFirst = false;
      sActivityTypes += "evenement_type.number='" + activityTypeNumber  + "' ";
      checkedActivityTypes.add(activityTypeNumber);
      searchParams += "&activity_type_" + activityTypeNumber + "=on";
   }
}
String selectedEventTypes = ""; 
String selectedNatuurgebieden = ""; 
%>
<mm:list nodes="<%= paginaID %>" path="pagina,posrel,evenement_type" fields="evenement_type.number">
   <mm:first inverse="true"><% selectedEventTypes += ","; %></mm:first>
   <mm:field name="evenement_type.number" jspvar="et_number" vartype="String" write="false">
      <%
      selectedEventTypes += et_number;
      if(checkedActivityTypes.size()==0) {
         if(!bFirst) sActivityTypes += " OR ";
         bFirst = false;
         sActivityTypes += "evenement_type.number='" + et_number + "' ";   
      }
      %>
   </mm:field>
</mm:list>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,natuurgebieden" fields="natuurgebieden.number">
   <mm:first inverse="true"><% selectedNatuurgebieden += ","; %></mm:first>
   <mm:field name="natuurgebieden.number" jspvar="natuurgebieden_number" vartype="String" write="false">
      <% selectedNatuurgebieden += natuurgebieden_number; %>
   </mm:field>
</mm:list>
<% 
if(natuurgebiedID.equals("-1")&&!selectedNatuurgebieden.equals("")) {
   // if no natuurgebiedID has been selected but there are selectedNatuurgebieden,
   // then use the selectedNatuurgebieden to narrow down the search
   natuurgebiedID = selectedNatuurgebieden;
}
%>