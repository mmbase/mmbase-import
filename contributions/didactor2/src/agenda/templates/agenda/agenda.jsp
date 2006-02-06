<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<%@ include file="/shared/setImports.jsp"%>

<%@ page import="java.text.SimpleDateFormat,
                 java.text.ParseException,
                 java.util.Date,
                 java.util.Calendar"%>

<mm:import externid="year" jspvar="year" vartype="Integer"/>
<mm:import externid="day" jspvar="day" vartype="Integer"/>
<mm:import externid="month" jspvar="month" vartype="Integer"/>

<mm:import id="selecteddatefrom"><mm:time time="$year/$month/$day" inputformat="yyyy/M/d"/></mm:import>
<mm:import id="linkedlist" jspvar="linkedlist" vartype="List"/>

<%
 Calendar tmpCal = Calendar.getInstance();
 tmpCal.set(year.intValue(),month.intValue()-1,day.intValue(),0,0,0);
 int startseconds = (int) (tmpCal.getTime().getTime() / 1000L);
 tmpCal.add(Calendar.DATE,1);
 int endseconds = ((int) (tmpCal.getTime().getTime() / 1000L)) -1;
%>
<mm:import id="startseconds"><%= startseconds %></mm:import>
<mm:import id="endseconds"><%= endseconds %></mm:import>


<%-- Get the personal agendas --%>
<mm:node number="$user">
  <mm:relatednodes type="agendas" id="agenda">
   <%@include file="getselecteditems.jsp"%>
  </mm:relatednodes>

<%-- Get the workgroups agendas of the user--%>
  <mm:relatednodes type="workgroups">
    <mm:relatednodes type="agendas" id="agenda">
     <%@include file="getselecteditems.jsp"%>
    </mm:relatednodes>
  </mm:relatednodes>

<%-- Get the classes agendas of the user--%>
  <mm:relatednodes type="classes">
    <mm:relatednodes type="agendas" id="agenda">
     <%@include file="getselecteditems.jsp"%>
    </mm:relatednodes>
  </mm:relatednodes>

<%-- get invitations --%>

  <mm:related path="invitationrel,items,eventrel,agendas" constraints="eventrel.stop > $startseconds AND eventrel.start < $endseconds">
     <mm:field name="items.number" jspvar="itemNumber" vartype="String" write="false">
     <%
      linkedlist.add( itemNumber );
     %>
     </mm:field>
  </mm:related>

</mm:node>
  
<mm:listnodescontainer type="items">

 <mm:constraint field="number" referid="linkedlist" operator="IN"/>

  <di:table maxitems="10">

    <di:row>

      <di:headercell><input type="checkbox" onclick="selectAllClicked(this.form, this.checked);" /></di:headercell>
      <di:headercell><di:translate key="agenda.calendar" /></di:headercell>
      <di:headercell sortfield="title" default="true"><di:translate key="agenda.appointment" /></di:headercell>
      <di:headercell><di:translate key="agenda.starttime" /></di:headercell>
      <di:headercell><di:translate key="agenda.endtime" /></di:headercell>
    </di:row>

    <mm:listnodes>
      <di:row>
        <mm:remove referid="link"/>
        <mm:import id="link">
            <a href="<mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids">
		               <mm:param name="currentitem"><mm:field name="number"/></mm:param>
		               <mm:param name="callerpage">/agenda/index.jsp</mm:param>
			       <mm:param name="day"><mm:write referid="day"/></mm:param>
			       <mm:param name="month"><mm:write referid="month"/></mm:param>
		               <mm:param name="year"><mm:write referid="year"/></mm:param>
		             </mm:treefile>">
        </mm:import>

        
        <mm:relatednodes type="agendas" max="1">
            <mm:relatednodes type="classes">
                <mm:import id="agendaname" reset="true">
                    <di:translate key="agenda.agenda_of" /> <di:translate key="agenda.class" /> <mm:field name="name"/>
                </mm:import>    
            </mm:relatednodes>
            <mm:relatednodes type="workgroups">
                <mm:import id="agendaname" reset="true">
                    <di:translate key="agenda.agenda_of" /> <di:translate key="agenda.workgroup" /> <mm:field name="name"/>
                </mm:import>    
            </mm:relatednodes>
            <mm:relatednodes type="people">
                <mm:import id="agendaname" reset="true">
                    <di:translate key="agenda.agenda_of" /> <mm:field name="firstname"/> <mm:field name="suffix"/> <mm:field name="lastname"/>
                </mm:import>    
            </mm:relatednodes>
        </mm:relatednodes>

	<di:cell>
	<mm:import id="num" reset="true"><mm:field name="number"/></mm:import>
	<mm:list nodes="$user" path="people,invitationrel,items" constraints="items.number=$num and invitationrel.status=1" max="1">
	    <mm:first>
	    <input type="checkbox" name="ids" value="<mm:write referid="num"/>">
	    </mm:first>
	</mm:list>
	</di:cell>
        <di:cell>
          <mm:relatednodes type="agendas" max="1">
            <mm:write escape="none" referid="link"/><mm:write referid="agendaname"/></a>
          </mm:relatednodes>
        </di:cell>
        <di:cell><mm:write escape="none" referid="link"/><mm:field name="title"/></a></di:cell>
        <mm:listrelations role="eventrel" max="1">
          <di:cell><mm:write escape="none" referid="link"/><mm:field name="start"><mm:time format="HH:mm:ss"/></mm:field></a></di:cell>
          <di:cell><mm:write escape="none" referid="link"/><mm:field name="stop"><mm:time format="HH:mm:ss"/></mm:field></a></di:cell>
        </mm:listrelations>
      </di:row>

    </mm:listnodes>

  </di:table>

</mm:listnodescontainer>

</mm:cloud>
</mm:content>


