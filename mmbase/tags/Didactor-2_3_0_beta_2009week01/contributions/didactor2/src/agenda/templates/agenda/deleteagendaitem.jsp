<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@page import="java.util.Calendar, java.util.Date"%>

<%-- expires is set so renaming a folder does not show the old name --%>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="agenda.deleteappointment" /></title>
  </mm:param>
</mm:treeinclude>

<mm:import externid="callerpage"/>
<mm:import externid="action0"/>
<mm:import externid="action1"/>
<mm:import externid="action2"/>
<mm:import externid="year" jspvar="year" vartype="Integer"/>
<mm:import externid="month" jspvar="month" vartype="Integer"/>
<mm:import externid="day" jspvar="day" vartype="Integer"/>
<mm:import externid="ids" vartype="List"/>

<mm:notpresent referid="ids">
  <mm:present referid="year">
    <mm:redirect referids="$referids,year,month,day" page="$callerpage"/>
  </mm:present>
  <mm:redirect referids="$referids" page="$callerpage"/>
</mm:notpresent>
	
<mm:import id="list" jspvar="list" vartype="List"><mm:write referid="ids"/></mm:import>



<%-- Check if the yes (all) button is pressed --%>
<mm:present referid="action1">
 
<%-- Delete the item with the relation to the agenda --%>
<mm:listnodes type="items" constraints="number IN ($ids)">
  <mm:deletenode deleterelations="true"/>
</mm:listnodes>

    <%-- Show the previous page --%>
   <mm:present referid="year">
    <mm:redirect referids="$referids,year,month,day" page="$callerpage"/>
   </mm:present>
   <mm:redirect referids="$referids" page="$callerpage"/>
</mm:present>

<%-- Check if the yes (one) button is pressed --%>
<mm:present referid="action0">
  <%
  Calendar tmpCal = Calendar.getInstance();
  tmpCal.set(year.intValue(),month.intValue()-1,day.intValue(),0,0,0);
  int daystart = (int) (tmpCal.getTime().getTime() / 1000);
  tmpCal.add(Calendar.DATE,1);
  int dayend = ( (int) (tmpCal.getTime().getTime() / 1000)) -1;
  %>
  <mm:import id="dayend"><%= dayend %></mm:import>
  <mm:import id="daystart"><%= daystart %></mm:import>
    
  <%-- Delete the relations to the agenda --%>
  <mm:list path="items,eventrel,agendas" constraints="items.number IN ($ids) AND eventrel.start <= $dayend AND eventrel.stop >= $daystart">
    <mm:import id="relnumber" reset="true"><mm:field name="eventrel.number"/></mm:import>
    <mm:node number="$relnumber">
	<mm:deletenode/>
    </mm:node>
  </mm:list>

    <%-- Show the previous page --%>
   <mm:present referid="year">
    <mm:redirect referids="$referids,year,month,day" page="$callerpage"/>
   </mm:present>
   <mm:redirect referids="$referids" page="$callerpage"/>
</mm:present>



<%-- Check if the no button is pressed --%>
<mm:import id="action2text"><di:translate key="agenda.no" /></mm:import>
<mm:compare referid="action2" referid2="action2text">
  <mm:present referid="year">
  <mm:redirect referids="$referids,year,month,day" page="$callerpage"/>
  </mm:present>
  <mm:redirect referids="$referids" page="$callerpage"/>

</mm:compare>



  <%-- Delete the relations to the agenda --%>
  <mm:listnodes type="items" constraints="number IN ($ids)">
    <mm:import id="cr" reset="true"><mm:countrelations role="eventrel"/></mm:import>
    <mm:compare referid="cr" value="1" inverse="true">
	<mm:import id="hasseries" reset="true"/>
    </mm:compare>
  </mm:listnodes>



<div class="rows">

<div class="navigationbar">
  <div class="titlebar">

  </div>
</div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>

<div class="mainContent">

  <div class="contentHeader">
    <di:translate key="agenda.deleteappointment" />
  </div>

  <div class="contentBodywit">

    <%-- Show the form --%>
    <form name="deletecontact" class="formInput" method="post" action="<mm:treefile page="/agenda/deleteagendaitem.jsp" objectlist="$includePath" referids="$referids"/>">

      <di:translate key="agenda.deleteselectedappointmentsyesno" />
      <p/>
      <table class="font">
      </table>
      <p/>
      <mm:present referid="year">
      <input type="hidden" name="year" value="<mm:write referid="year"/>">
      <input type="hidden" name="month" value="<mm:write referid="month"/>">
      <input type="hidden" name="day" value="<mm:write referid="day"/>">
      </mm:present>
      <input type="hidden" name="callerpage" value="<mm:write referid="callerpage"/>"/>
      <input type="hidden" name="ids" value="<mm:write referid="ids"/>"/>
      
      <mm:present referid="hasseries">
	  <di:translate key="agenda.seriesfound" />
          <p>
       <input type="submit" class="formbutton" name="action0" value="<di:translate key="agenda.deleteone" />"/>
       
      <input type="submit" class="formbutton" name="action1" value="<di:translate key="agenda.deleteall" />"/>
      </mm:present>

      <mm:notpresent referid="hasseries">
      <input type="submit" class="formbutton" name="action1" value="<di:translate key="agenda.yes" />"/>
      </mm:notpresent>

      <input type="submit" class="formbutton" name="action2" value="<di:translate key="agenda.no" />"/>

    </form>

  </div>
</div>
</div>

</mm:cloud>
</mm:content>
