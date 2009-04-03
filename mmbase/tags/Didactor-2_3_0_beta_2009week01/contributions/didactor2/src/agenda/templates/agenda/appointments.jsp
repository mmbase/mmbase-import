<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
 <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" >
<%@include file="/shared/setImports.jsp" %>
<%@ page import="java.util.Calendar"%>
<mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
  <mm:param name="extraheader">
    <title><di:translate key="agenda.calendar" /></title>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/agenda/css/calendar.css" objectlist="$includePath" />" />
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
      <mm:treeinclude page="/agenda/calendar.jspx" objectlist="$includePath" referids="$referids,year,month,day" />
    </div>
  </div>

  <mm:treefile page="/agenda/deleteagendaitem.jsp" objectlist="$includePath" referids="$referids" write="false" id="post" />
  <form action="${post}" method="post">
    <input type="hidden" name="callerpage" value="/agenda/appointments.jsp" />
    <div class="mainContent">
      <div class="contentHeader">
        <di:translate key="agenda.appointments" />
      </div>
      <div class="contentSubHeader">    
        <a href="<mm:treefile page="/agenda/index.jsp" objectlist="$includePath" referids="$referids"/>"><img src="<mm:treefile page="/agenda/gfx/bekijk_vandaag.gif" objectlist="$includePath" />" title="<di:translate key="agenda.viewcurrentagenda" />" alt="<di:translate key="agenda.viewcurrentagenda" />" border="0"></a>
        <input type="image" src="<mm:treefile page="/agenda/gfx/afspraak verwijderen.gif" objectlist="$includePath" />">
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
          <mm:time id="yesterday" time="yesterday" write="false" />
          <mm:node referid="user">
            <mm:relatedcontainer path="invitationrel,items,eventrel,agendas" 
                                 fields="eventrel.start,invitationrel.status"
                                 >
              <mm:sortorder field="eventrel.start" />
              <mm:sortorder field="eventrel.stop" />
              <mm:composite operator="OR">
                <mm:constraint field="eventrel.start" operator=">" value="${yesterday}" />
                <mm:constraint field="eventrel.stop" operator=">" value="${yesterday}" />
              </mm:composite>
              <mm:related id="agendas">
                <mm:field name="eventrel.start">
                  <mm:import id="invday" reset="true"><mm:time format="dd"/></mm:import>
                  <mm:import id="invmonth" reset="true"><mm:time format="MM"/></mm:import>
                  <mm:import id="invyear" reset="true"><mm:time format="yyyy"/></mm:import>                
                </mm:field>
                <mm:node element="items" id="item">
                  <mm:relatedcontainer  path="invitationrel,people" fields="invitationrel.status">
                    <mm:constraint field="people.number" operator="!=" value="$user" />
                    <mm:related id="people">
                      <tr>
                        <td class="listItem">
                          <c:if test="${_node.invitationrel.status eq 1}">
                            <input type="checkbox" name="ids" value="${_node.items}" />
                          </c:if>
                        </td>
                        <td colspan="4" class="listItem">
                          <mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids,invday@day,invyear@year,invmonth@month" write="false">
                            <mm:param name="currentitem">${_node.items}</mm:param>
                            <mm:param name="callerpage"><%= request.getRequestURL().toString() %></mm:param>
                            <a href="${_}"><mm:field node="item" name="title"/></a>
                          </mm:treefile>                        
                        </td>
                      </tr>
                      <tr>
                        <td class="listItem">
                        </td>
                        <td class="listItem">
                          <c:if test="${agendas.invitationrel.status eq 1}">
                            <di:translate key="agenda.sentto" />
                          </c:if>
                          <c:if test="${people.invitationrel.status eq 1}">
                            <di:translate key="agenda.recievedfrom" />
                          </c:if>
                        </td>
                        <td class="listItem"><di:person element="people" /></td>
                        
                        <mm:field node="agendas" name="eventrel.start">
                          <mm:time format=":FULL" />
                        </mm:field>
                        
                        <td class="listItem">
                          <mm:treefile page="/agenda/showagendaitem.jsp" objectlist="$includePath" referids="$referids,item@currenitem" write="false">
                            <mm:param name="callerpage"><%= request.getRequestURL().toString() %></mm:param>
                            <a href="${_}">
                              <c:if test="${agendas.invitationrel.status eq 1}">
                                <c:if test="${people.invitationrel.status eq 2}">
                                  <di:translate key="agenda.accepted" />
                                </c:if>
                                <c:if test="${people.invitationrel.status eq 3}">
                                  <di:translate key="agenda.declined" />
                                </c:if>
                                <c:if test="${people.invitationrel.status lt 2}">
                                  <di:translate key="agenda.accepted" />
                                </c:if>
                              </c:if>
                              <c:if test="${people.invitationrel.status eq 1}">
                                <c:if test="${agendas.invitationrel.status eq 2}">
                                  <di:translate key="agenda.accepted" />
                                </c:if>
                                <c:if test="${agendas.invitationrel.status eq 3}">
                                  <di:translate key="agenda.declined" />
                                </c:if>
                                <c:if test="${agendas.invitationrel.status lt 2}">
                                  <di:translate key="agenda.accepted" />
                                </c:if>
                              </c:if>
                            </a>
                          </mm:treefile>                          
                        </td>
                      </tr>                   
                    </mm:related>
                  </mm:relatedcontainer>
                </mm:node>
              </mm:related>
            </mm:relatedcontainer>
          </mm:node>
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

