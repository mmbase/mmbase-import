<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><mm:content postprocessor="reducespace" expires="0">
<mm:cloud rank="didactor user">
<%@include file="/shared/setImports.jsp" %>
<%@ page import="java.util.Calendar"%>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="agenda.calendar" /></title>
    <link rel="stylesheet" type="text/css" href="${mm:treefile('/agenda/css/calendar.css', pageContext, includePath)}" />
  </mm:param>
</mm:treeinclude>

<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
      <img src="<mm:treefile page="/gfx/icon_agenda.gif" objectlist="$includePath" />" title="<di:translate key="agenda.calendar" />" alt="<di:translate key="agenda.calendar" />"/>
      <di:translate key="agenda.calendar" />
    </div>
  </div>

  <mm:import externid="year"><mm:time time="now" format="yyyy" /></mm:import>
  <mm:import externid="month"><mm:time time="now" format="MM" /></mm:import>
  <mm:import externid="day"><mm:time time="now" format="dd" /></mm:import>

  <mm:time id="date" time="$year-$month-$day" write="false" />

  <div class="folders">
    <div class="folderHeader">
      <di:translate key="agenda.calendar" />
    </div>
    <div class="folderCalendarBody">
      <p>
        <mm:treeinclude page="/agenda/calendar.jspx" objectlist="$includePath" referids="$referids,year,month,day" />
      </p>
      <mm:node number="$user">
        <mm:relatednodescontainer path="invitationrel,people">
          <mm:constraint field="invitationrel.status" operator="LESS_EQUAL" value="0" />
          <mm:constraint field="people.number" operator="EQUAL" inverse="true" value="$user" />
          <mm:relatednodes max="1">
            <mm:treefile page="/agenda/appointments.jsp" objectlist="$includePath" referids="$referids" write="false">
              <a href="${_}"><di:translate key="agenda.newappointments" /></a>
            </mm:treefile>
          </mm:relatednodes>
        </mm:relatednodescontainer>
      </mm:node>
    </div>
  </div>

  <form action="<mm:treefile page="/agenda/deleteagendaitem.jsp" objectlist="$includePath" referids="$referids,year,month,day"/>" method="post">
    <input type="hidden" name="callerpage" value="/agenda/index.jsp" />
    <div class="mainContent">
      <div class="contentHeader">
        <mm:time referid="date" format=":FULL" />
      </div>
      <div class="contentSubHeader">
        <% int count = 0; %>
        <di:hasrole role="student">
          <%count++;%>
        </di:hasrole>
        <di:hasrole role="teacher">
          <%count++;%>
        </di:hasrole>
        <% if ( count > 0 ) { %>
          <%-- personal agenda items --%>
          <a href="<mm:treefile page="/agenda/addagendaitem.jspx" objectlist="$includePath" referids="$referids,year,month,day">
                    <mm:param name="callerpage">/agenda/index.jsp</mm:param>
                    <mm:param name="typeof">1</mm:param>
                    </mm:treefile>">
                    <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_person.gif" objectlist="$includePath" />" border="0" title="<di:translate key="agenda.addpersonalagendaitem" />" alt="<di:translate key="agenda.addpersonalagendaitem" />"/></a>
        <% }%>
        <%-- workgroup related agenda items --%>
        <mm:node number="$user">
          <%-- there must be at least one workgroup --%>
          <mm:relatednodes type="workgroups" max="1">
            <a href="<mm:treefile page="/agenda/addagendaitem.jspx" objectlist="$includePath" referids="$referids,year,month,day">
                       <mm:param name="callerpage">/agenda/index.jsp</mm:param>
                       <mm:param name="typeof">3</mm:param>
                     </mm:treefile>">
              <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_workgroup.gif" objectlist="$includePath" />" border="0" title="<di:translate key="agenda.addworkgroupagendaitem" />" alt="<di:translate key="agenda.addworkgroupagendaitem" />"/></a>
          </mm:relatednodes>
        </mm:node>

        <%-- class related agenda items --%>
        <di:hasrole role="teacher">
          <mm:node number="$user">
            <%-- there must be at least one class --%>
            <mm:relatednodes type="classes" max="1">
              <a href="<mm:treefile page="/agenda/addagendaitem.jspx" objectlist="$includePath" referids="$referids,year,month,day">
                         <mm:param name="callerpage">/agenda/index.jsp</mm:param>
                         <mm:param name="typeof">2</mm:param>
                       </mm:treefile>">
                <img src="<mm:treefile page="/agenda/gfx/icon_agenda_item_class.gif" objectlist="$includePath" />" border="0" title="<di:translate key="agenda.addclassagendaitem" />" alt="<di:translate key="agenda.addclassagendaitem" />"/></a>
            </mm:relatednodes>
          </mm:node>
        </di:hasrole>

        <%-- classmates related agenda items --%>
        <mm:node number="$user">
          <%-- there must be at least one class --%>
          <mm:relatednodes type="classes" max="1">
            <a href="<mm:treefile page="/agenda/addagendaitem.jspx" objectlist="$includePath" referids="$referids,year,month,day">
                       <mm:param name="callerpage">/agenda/index.jsp</mm:param>
                       <mm:param name="typeof">4</mm:param>
                     </mm:treefile>">
              <img src="<mm:treefile page="/agenda/gfx/icon_agenda_invitation.gif" objectlist="$includePath" />" border="0" title="<di:translate key="agenda.createinvitation" />" alt="<di:translate key="agenda.createinvitation" />"/></a>
          </mm:relatednodes>
        </mm:node>

        <a href="<mm:treefile page="/agenda/appointments.jsp"  objectlist="$includePath" referids="$referids"/>"><img src="<mm:treefile page="/agenda/gfx/bekijk_afspraken.gif" objectlist="$includePath" />" title="<di:translate key="agenda.listappointments" />" alt="<di:translate key="agenda.listappointments" />" border="0" /></a>
        <input type="image" src="<mm:treefile page="/agenda/gfx/afspraak verwijderen.gif" objectlist="$includePath" />"/>
      </div>
      <div class="contentBody">
        <mm:treeinclude page="/agenda/agenda.jsp" objectlist="$includePath" referids="$referids,year,month,day">
          <mm:param name="typeof">2</mm:param>
          </mm:treeinclude>
      </div>
    </div>
  </form>
</div>

<script type="text/javascript">
<!--
    function selectAllClicked(frm, newState) {
        if (frm.elements['ids'].length) {
            for(var count =0; count < frm.elements['ids'].length; count++ ) {
                var box = frm.elements['ids'][count];
                box.checked=newState;
            }
        } else {
            frm.elements['ids'].checked=newState;
        }
    }
//-->
</script>

<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>

