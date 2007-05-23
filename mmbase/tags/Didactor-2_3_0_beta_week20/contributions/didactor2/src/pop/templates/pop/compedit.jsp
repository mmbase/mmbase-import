<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace" escaper="none">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="getids.jsp" %>
<% boolean isEmpty = true; %>
<mm:import externid="msg">-1</mm:import>
<div class="contentBody">
  <%@ include file="getmyfeedback.jsp" %>
    <mm:compare referid="msg" value="-1" inverse="true">
      <mm:write referid="msg" escape="text/plain"/>
    </mm:compare>

  <mm:node number="$currentcomp">
    <form name="editcompform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$popreferids,currentprofile,currentcomp">
        </mm:treefile>" method="post">
    <table class="font" width="70%">
    <input type="hidden" name="popcmd" value="savecomp">
    <input type="hidden" name="returnto" value="editcomp">
    <input type="hidden" name="todonumber" value="-1">
      <tr style="vertical-align:top;">
        <td width="100" style="vertical-align:top;"><di:translate key="pop.competence" /></td>
        <td><b><mm:field name="name"/></b></td>
      </tr>
      <tr style="vertical-align:top;">
        <td width="100" style="vertical-align:top;"><di:translate key="pop.description" /></td>
        <td><b><mm:field name="description" escape="pp"/></b></td>
      </tr>
      <tr style="vertical-align:top;">
        <td nowrap><di:translate key="pop.compeditfeedback1" /></td>
        <td><input name="myfeedback1" class="popFormInput" type="text" size="50" maxlength="255" value="<mm:write referid="myfeedback1"/>"></td>
      </tr>
      <tr style="vertical-align:top;">
        <td><di:translate key="pop.compeditfeedback2" /></td>
        <td><textarea name="myfeedback2" class="popFormInput" cols="50" rows="5"><mm:write referid="myfeedback2"/></textarea></td>
      </tr>
  </table>
  <table class="font" width="80%">
    <tr>
      <td>
        <input type="button" class="formbutton" onClick="editcompform.submit()" value="<di:translate key="pop.savebutton" />">
        <input type="button" class="formbutton" onClick="editcompform.popcmd.value='no';editcompform.submit()" value="<di:translate key="pop.backbuttonlc" />">
      </td>

    </tr>
  </table>
<br/>
  <table width="80%" border="0" class="popGreyTableHeader">
    <tr>
      <td colspan="3"><di:translate key="pop.todoitems" /></td>
    </tr>
  </table>
          <mm:list nodes="$currentpop" path="pop,todoitems,competencies" orderby="todoitems.number" directions="UP"
              constraints="competencies.number='$currentcomp'">
            <input type="checkbox" name="ids" value="<mm:field name="todoitems.number"/>"><a href="#1"
                onclick="editcompform.popcmd.value='addtodo';editcompform.todonumber.value='<mm:field name="todoitems.number"/>';editcompform.submit();return false;"
              ><mm:field name="todoitems.name" jspvar="todoName" vartype="String"
              ><% if (todoName.length()>0) { %><%= todoName %><% } else { %>...<% } %></mm:field></a><br/>
          </mm:list>
          <br/>
          <a href="#1" onclick="editcompform.popcmd.value='addtodo';editcompform.submit();return false;"
            ><img src="<mm:treefile page="/pop/gfx/icon_add_todo.gif" objectlist="$includePath" referids="$popreferids"/>"
                border="0" title="<di:translate key="pop.compeditmakenewtodo"/>" alt="<di:translate key="pop.compeditmakenewtodo"/>" /></a>
          <a href="#1" onclick="if (!window.confirm('<di:translate key="pop.areyousuredeltodo" />'))
                return false;editcompform.popcmd.value='deltodo';editcompform.submit();return false;">
            <img src="<mm:treefile page="/pop/gfx/afspraak verwijderen.gif" objectlist="$includePath" referids="$popreferids"/>"
                border="0" title="<di:translate key="pop.compeditremoveselectedtodo"/>" alt="<di:translate key="pop.compeditremoveselectedtodo"/>" /></a>
<br/>
<br/>
    <mm:relatedcontainer path="popfeedback,pop">
      <mm:constraint field="pop.number" referid="currentpop" operator="EQUAL"/>
      <table width="80%" border="0" class="popSpecialTableHeader">
        <tr>
          <td colspan="3"><di:translate key="pop.compeditgrades" /></td>
        </tr>
      </table>
      <div><table class="poplistTable">
        <tr>
          <th class="listHeader"><di:translate key="pop.compeditby" /></th>
          <th class="listHeader"><di:translate key="pop.compeditworktogetheretc" /></th>
          <th class="listHeader"><di:translate key="pop.score" /></th>
          <th class="listHeader"><di:translate key="pop.compeditgrade" /></th>
        </tr>
        <mm:related>
          <mm:node element="popfeedback">
            <tr>
              <mm:relatedcontainer path="people">
                <mm:constraint field="people.number" referid="student" operator="EQUAL" inverse="true"/>
                <mm:related>
                  <td class="listItem"><mm:field name="people.firstname"/> <mm:field name="people.lastname"/></td>
                </mm:related>
              </mm:relatedcontainer>
              <mm:field name="status" jspvar="isAnswered" vartype="String">
                <% if (!isAnswered.equals("0")) { %>
                  <td class="listItem"><mm:field name="rank"/></td>
                  <td class="listItem">
                    <mm:related path="ratings">
                      <mm:field name="ratings.name" id="rating" write="true"/>
                    </mm:related>
                  </td>
                  <td class="listItem"><mm:field name="text" escape="p"/></td>
                <% } else { %>
                  <td class="listItem"><i><di:translate key="pop.notanswered" /></i></td>
                  <td class="listItem">&nbsp;</td>
                  <td class="listItem">&nbsp;</td>
                <% } %>
              </mm:field>
            </tr>
          </mm:node>
        </mm:related>
      </table></div>
    </mm:relatedcontainer>
        <a href="#1" onclick="editcompform.popcmd.value='invite';editcompform.submit();return false;">
          <img src="<mm:treefile page="/pop/gfx/icon_invitation.gif" objectlist="$includePath" referids="$popreferids"/>"
            border="0" title="<di:translate key="pop.compeditinvitecolleague"/>" alt="<di:translate key="pop.compeditinvitecolleague"/>" /></a>
    <br/><br/><br/>
    <table width="80%" border="0" class="popGreyTableHeader">
      <tr>
        <td colspan="3"><di:translate key="pop.portfolio" /></td>
      </tr>
    </table>
    <mm:compare referid="thisfeedback" value="-1" inverse="true">
      <mm:node number="$thisfeedback">
        <mm:relatednodes type="attachments">
          <input type="checkbox" name="portfolio_items_ids" value="<mm:field name="number"/>">
          <mm:related path="folders,portfolios,people" constraints="people.number='$student'">
            <mm:import id="foldername" reset="true"><mm:field name="folders.name"/></mm:import>
            <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$popreferids">
                <mm:param name="typeof"><mm:field name="portfolios.type"/></mm:param>
                <mm:param name="currentfolder"><mm:field name="folders.number"/></mm:param>
              </mm:treefile>"
            ></mm:related><mm:write referid="foldername"/> > <mm:field name="title"/></a><br/>
        </mm:relatednodes>
        <mm:relatednodes type="folders">
          <mm:import id="foldernumber" reset="true"><mm:field name="number"/></mm:import>
          <input type="checkbox" name="portfolio_items_ids" value="<mm:field name="number"/>">
          <mm:related path="portfolios,people" constraints="people.number='$student'">
            <mm:import id="foldername" reset="true"><mm:field name="folders.name"/></mm:import>
            <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$popreferids">
                <mm:param name="typeof"><mm:field name="portfolios.type"/></mm:param>
                <mm:param name="currentfolder"><mm:write referid="foldernumber"/></mm:param>
              </mm:treefile>"
            ></mm:related><mm:field name="name"/></a><br/>
        </mm:relatednodes>
        <mm:relatednodes type="urls">
          <input type="checkbox" name="portfolio_items_ids" value="<mm:field name="number"/>">
          <mm:related path="folders,portfolios,people" constraints="people.number='$student'">
            <mm:import id="foldername" reset="true"><mm:field name="folders.name"/></mm:import>
            <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$popreferids">
                <mm:param name="typeof"><mm:field name="portfolios.type"/></mm:param>
                <mm:param name="currentfolder"><mm:field name="folders.number"/></mm:param>
              </mm:treefile>"
            ></mm:related><mm:write referid="foldername"/> > <mm:field name="name"/></a><br/>
        </mm:relatednodes>
        <mm:relatednodes type="pages">
          <input type="checkbox" name="portfolio_items_ids" value="<mm:field name="number"/>">
          <mm:related path="folders,portfolios,people" constraints="people.number='$student'">
            <mm:import id="foldername" reset="true"><mm:field name="folders.name"/></mm:import>
            <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$popreferids">
                <mm:param name="typeof"><mm:field name="portfolios.type"/></mm:param>
                <mm:param name="currentfolder"><mm:field name="folders.number"/></mm:param>
              </mm:treefile>"
            ></mm:related><mm:write referid="foldername"/> > <mm:field name="name"/></a><br/>
        </mm:relatednodes>
        <mm:relatednodes type="chatlogs">
          <input type="checkbox" name="portfolio_items_ids" value="<mm:field name="number"/>">
          <mm:related path="folders,portfolios,people" constraints="people.number='$student'">
            <mm:import id="foldername" reset="true"><mm:field name="folders.name"/></mm:import>
            <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$popreferids">
                <mm:param name="typeof"><mm:field name="portfolios.type"/></mm:param>
                <mm:param name="currentfolder"><mm:field name="folders.number"/></mm:param>
              </mm:treefile>"
            ></mm:related><mm:write referid="foldername"/> > <mm:field name="date"/></a><br/>
        </mm:relatednodes>
      </mm:node>
    </mm:compare>
    <br/>
    <a href="#1" onclick="editcompform.popcmd.value='adddoc';editcompform.submit();return false;">
      <img src="<mm:treefile page="/portfolio/gfx/document plaatsen.gif" objectlist="$includePath" referids="$popreferids"/>" 
        border="0" alt="<di:translate key="pop.portfolioadddoc"/>" /></a>
     <a href="#1" onclick="if (!window.confirm('<di:translate key="pop.areyousuredeldoc" />'))
        return false;editcompform.popcmd.value='deldocs';editcompform.submit();return false;">
      <img src="<mm:treefile page="/pop/gfx/afspraak verwijderen.gif" objectlist="$includePath" referids="$popreferids"/>"
        border="0" alt="<di:translate key="pop.compeditremoveselecteddoc"/>" /></a>
  </form>
  </mm:node>
</div>
</mm:cloud>
</mm:content>
