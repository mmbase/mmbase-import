<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ page import="java.util.Calendar"%>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="agenda.calendar" /></title>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/agenda/css/calendar.css" objectlist="$includePath" referids="$referids"/>" />
  </mm:param>
</mm:treeinclude>

<div class="rows">
  <div class="navigationbar">
    <div class="titlebar">
      <img src="<mm:treefile page="/gfx/icon_agenda.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="agenda.calendar" />" alt="<di:translate key="agenda.calendar" />"/>
      <di:translate key="agenda.calendar" />
    </div>
  </div>
  <%
    Calendar cal = Calendar.getInstance();
    int day = cal.get( Calendar.DATE );
    int month = cal.get( Calendar.MONTH ) + 1;
    int year = cal.get( Calendar.YEAR );
  %>
  <mm:import externid="year">-1</mm:import>
  <mm:import externid="month">-1</mm:import>
  <mm:import externid="day">-1</mm:import>
  <mm:compare referid="year" value="-1">
   <mm:remove referid="month"/>
   <mm:remove referid="day"/>
   <mm:remove referid="year"/>
    <mm:import id="year"><%=year%></mm:import>
    <mm:import id="month"><%=month%></mm:import>
    <mm:import id="day"><%=day%></mm:import>
  </mm:compare>
  <div class="folders">
    <div class="folderHeader">
      <di:translate key="agenda.calendar" />
    </div>
    <div class="folderCalendarBody">
      <mm:treeinclude page="/agenda/calendar.jsp" objectlist="$includePath" referids="$referids">
        <mm:param name="year"><mm:write referid="year"/></mm:param>
        <mm:param name="month"><mm:write referid="month"/></mm:param>
        <mm:param name="day"><mm:write referid="day"/></mm:param>
      </mm:treeinclude>
    </div>
  </div>
  
  <form action="<mm:treefile page="/agenda/deleteagendaitem.jsp" objectlist="$includePath" referids="$referids"/>" method="post">
    <input type="hidden" name="callerpage" value="/agenda/appointments.jsp" />
    <div class="mainContent">
      <div class="contentHeader">
        <di:translate key="agenda.appointments" />
      </div>
      <div class="contentSubHeader">    
        <a href="<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids"/>"><img src="<mm:treefile page="/agenda/gfx/bekijk_vandaag.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="agenda.viewcurrentagenda" />" alt="<di:translate key="agenda.viewcurrentagenda" />" border="0"></a>
        <input type="image" src="<mm:treefile page="/agenda/gfx/afspraak verwijderen.gif" objectlist="$includePath" referids="$referids"/>">
      </div>
      <div class="contentBody">
        <table class="listTable">
          <tr>
            <th class="listHeader"><input type="checkbox" onclick="selectAllClicked(thios.form, this.checked);"></th>
            <th class="listHeader"><di:translate key="agenda.type" /></th>
            <th class="listHeader"><di:translate key="agenda.name" /></th>
            <th class="listHeader"><di:translate key="agenda.date" /></th>
            <th class="listHeader"><di:translate key="agenda.status" /></th>
          </tr>
          <%
            String yesterday = ""+((cal.getTimeInMillis() / 1000L) - 2*24*60*60); // show yesterdays too
            String constraints = "eventrel.start > "+yesterday+" OR eventrel.stop > "+yesterday;
          %>
          <mm:list nodes="$user" path="people,invitationrel,items,eventrel,agendas" constraints="<%= constraints %>" orderby="eventrel.start,eventrel.stop" fields="items.number,invitationrel.status,eventrel.number" distinct="true">
            <mm:import id="itemnumber"><mm:field name="items.number"/></mm:import>
            <mm:import id="mystatus" reset="true"><mm:field name="invitationrel.status"/></mm:import>
            <mm:import id="eventrel"><mm:field name="eventrel.number"/></mm:import>
            <mm:node referid="eventrel">
              <mm:fieldlist nodetype="eventrel" fields="start">
                <mm:import id="invday" reset="true"><mm:fieldinfo type="value"><mm:time format="dd"/></mm:fieldinfo></mm:import>
                <mm:import id="invmonth" reset="true"><mm:fieldinfo type="value"><mm:time format="MM"/></mm:fieldinfo></mm:import>
                <mm:import id="invyear" reset="true"><mm:fieldinfo type="value"><mm:time format="yyyy"/></mm:fieldinfo></mm:import>
              </mm:fieldlist>
            </mm:node>
       
            <mm:list nodes="$itemnumber" path="items,invitationrel,people" constraints="$user != people.number" fields="invitationrel.status">
              <mm:import id="hisstatus" reset="true"><mm:field name="invitationrel.status"/></mm:import>
              <tr>
                <td class="listItem">
                  <mm:compare referid="mystatus" value="1">
                    <input type="checkbox" name="ids" value="<mm:write referid="itemnumber"/>">
                  </mm:compare>
                </td>
                <td colspan="4" class="listItem">
                  <a href="<mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="currentitem"><mm:write referid="itemnumber"/></mm:param>
                    <mm:param name="callerpage"><%= request.getRequestURL().toString() %></mm:param>
                    <mm:param name="day"><mm:write referid="invday"/></mm:param>
                    <mm:param name="year"><mm:write referid="invyear"/></mm:param>
                    <mm:param name="month"><mm:write referid="invmonth"/></mm:param>
                   </mm:treefile>"><mm:field name="items.title"/></a>
                </td>
              </tr>
              <tr>
                <td class="listItem">
                </td>
                <td class="listItem">
                  <mm:compare referid="mystatus" value="1">
                    <di:translate key="agenda.sentto" />
                  </mm:compare>
                  <mm:compare referid="hisstatus" value="1">
                    <di:translate key="agenda.recievedfrom" />
                  </mm:compare>
                </td>
                <td class="listItem"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></td>
            
                <mm:node referid="eventrel">
                  <mm:fieldlist nodetype="eventrel" fields="start">
                    <td class="listItem"><mm:fieldinfo type="value"><mm:time format="dd/MM/yyyy"/></mm:fieldinfo></td>
                  </mm:fieldlist>
                </mm:node>
            
                <td class="listItem">
                  <a href="<mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids">
                    <mm:param name="callerpage"><%= request.getRequestURL().toString() %></mm:param>
                    <mm:param name="currentitem"><mm:write referid="itemnumber"/></mm:param>
                    </mm:treefile>">
                    <mm:compare referid="mystatus" value="1">
                      <mm:compare referid="hisstatus" value="2">
                        <di:translate key="agenda.accepted" />
                      </mm:compare>
                      <mm:compare referid="hisstatus" value="3">
                        <di:translate key="agenda.declined" />
                      </mm:compare>
                      <mm:islessthan referid="hisstatus" value="2">
                        <di:translate key="agenda.accepted" />
                      </mm:islessthan>
                    </mm:compare>
                    <mm:compare referid="hisstatus" value="1">
                      <mm:compare referid="mystatus" value="2">
                        <di:translate key="agenda.accepted" />
                      </mm:compare>
                      <mm:compare referid="mystatus" value="3">
                        <di:translate key="agenda.declined" />
                      </mm:compare>
                      <mm:islessthan referid="mystatus" value="2">
                        <di:translate key="agenda.accepted" />
                      </mm:islessthan>
                    </mm:compare>
                  </a>
                </td>
              </tr>                    
            </mm:list>
          </mm:list>
        </table>
        <script type="text/javascript">
          function selectAllClicked(frm, newState) {
            if (frm.elements['ids'].length) {
              for(var count=0; count < frm.elements['ids'].length; count++) {
                var box = frm.elements['ids'][count];
                box.checked=newState;
              }
            } else {
              frm.elements['ids'].checked=newState;
            }
          }
        </script>
      </div>
    </div>
  </form>
</div>
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>

