<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="getids.jsp" %>
<% boolean isEmpty = true; %>
<mm:import externid="msg">-1</mm:import>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>
    <mm:compare referid="msg" value="-1" inverse="true">
      <mm:write referid="msg"/>
    </mm:compare>

  <mm:node number="$currentcomp">
    <table class="font" width="70%">
    <form name="editcompform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$referids,currentprofile,currentcomp">
        </mm:treefile>" method="post">
    <input type="hidden" name="command" value="savecomp">
    <input type="hidden" name="returnto" value="editcomp">
    <input type="hidden" name="todonumber" value="-1">
      <tr style="vertical-align:top;">
        <td width="100" style="vertical-align:top;">Competentie</td>
        <td><b><mm:field name="name"/></b></td>
      </tr>
      <tr style="vertical-align:top;">
        <td nowrap>Aan gewerkt<br/>door middel van</td>
        <td><input name="myfeedback1" class="popFormInput" type="text" size="50" maxlength="255" value="<mm:write referid="myfeedback1"/>"></td>
      </tr>
      <tr style="vertical-align:top;">
        <td>Zelfbeoordeling</td>
        <td><textarea name="myfeedback2" class="popFormInput" cols="50" rows="5"><mm:write referid="myfeedback2"/></textarea></td>
      </tr>
      <tr style="vertical-align:top;">
        <td>Persoonlijke taken</td>
        <td>
          <% isEmpty = true; %>
          <mm:list nodes="$currentpop" path="pop,todoitems,competencies" orderby="todoitems.number" directions="UP"
              constraints="competencies.number LIKE $currentcomp">
            <% isEmpty = false; %>
            <input type="checkbox" name="ids" value="<mm:field name="todoitems.number"/>"><a href="#1"
                onclick="editcompform.command.value='addtodo';editcompform.todonumber.value='<mm:field name="todoitems.number"/>';editcompform.submit();return false;"
              ><mm:field name="todoitems.name" jspvar="todoName" vartype="String"
              ><% if (todoName.length()>0) { %><%= todoName %><% } else { %>...<% } %></mm:field></a><br/>
          </mm:list>
          <% if (isEmpty) { %>&nbsp<% } %>
        </td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td>
          <a href="#1" onclick="editcompform.command.value='addtodo';editcompform.submit();return false;"
            ><img src="<mm:treefile page="/pop/gfx/icon_add_todo.gif" objectlist="$includePath" referids="$referids"/>"
                border="0" alt="Maak een nieuwe persoonlijke taak aan"/></a>
          <a href="#1" onclick="if (!window.confirm('Weet u zeker dat u de geselecteerde persoonlijke taken wilt verwijderen?'))
                return false;editcompform.command.value='deltodo';editcompform.submit();return false;">
            <img src="<mm:treefile page="/pop/gfx/afspraak verwijderen.gif" objectlist="$includePath" referids="$referids"/>"
                border="0" alt="Verwijder de geselecteerde persoonlijke taken"/></a>
        </td>
      </tr>
  </form>
  </table>
  <table class="font" width="80%">
    <tr>
      <td>
        <input type="button" class="formbutton" onClick="editcompform.submit()" value="aanmaken">
        <input type="button" class="formbutton" onClick="editcompform.command.value='no';editcompform.submit()" value="terug">
      </td>
      <td align="right">
        <a href="#1" onclick="editcompform.command.value='invite';editcompform.submit();return false;">
          <img src="<mm:treefile page="/pop/gfx/icon_invitation.gif" objectlist="$includePath" referids="$referids"/>"
              border="0" alt="Nodig een collega uit om een feedback te geven op deze competentie"/></a>
      </td>
    </tr>
  </table>
    <mm:relatedcontainer path="popfeedback,pop">
      <mm:constraint field="pop.number" referid="currentpop" operator="EQUAL"/>
      <table width="80%" border="0" class="popSpecialTableHeader">
        <tr>
          <td colspan="3">Beoordelingen</td>
        </tr>
      </table>
      <div><table class="poplistTable">
        <tr>
          <th class="listHeader">Door</th>
          <th class="listHeader">Aan samengewerkt door middel van</th>
          <th class="listHeader">Beoordeling</th>
        </tr>
        <mm:related>
          <mm:node element="popfeedback">
            <mm:relatedcontainer path="people">
              <mm:constraint field="people.number" referid="user" operator="EQUAL" inverse="true"/>
              <mm:related>
                <tr>
                  <td class="listItem"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></td>
                  <mm:field name="popfeedback.status" jspvar="isAnswered" vartype="String">
                    <% if (!isAnswered.equals("0")) { %>
                      <td class="listItem"><mm:field name="popfeedback.rank"/></td>
                      <td class="listItem"><mm:field name="popfeedback.text"/></td>
                    <% } else { %>
                      <td class="listItem"><i>niet geantwoord</i></td>
                      <td class="listItem">&nbsp;</td>
                    <% } %>
                   </mm:field>
                </tr>
              </mm:related>
            </mm:relatedcontainer>
          </mm:node>
        </mm:related>
      </table></div>
    </mm:relatedcontainer>
  </mm:node>
</div>
</fmt:bundle>
</mm:cloud>
</mm:content>
