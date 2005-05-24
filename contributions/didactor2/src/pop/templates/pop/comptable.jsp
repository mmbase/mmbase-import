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
    <mm:compare referid="msg" value="-1" inverse="true">
      <mm:write referid="msg"/>
    </mm:compare>
    <div><table class="poplistTable">
      <tr style="vertical-align:top;">
        <th class="listHeader">&nbsp;</th>
        <th class="listHeader">Competentie</th>
        <th class="listHeader">Aan gewerkt door middel van</th>
        <th class="listHeader">Zelfbeoordeling</th>
        <th class="listHeader">Score</th>
        <th class="listHeader">Voornemens</th>
      </tr>
      <mm:node number="$currentpop">
        <%@ include file="getcompetencies.jsp" %>
        <%  TreeMap competenciesIterator = (TreeMap) allCompetencies.clone();
            while(competenciesIterator.size()>0) { 
              String thisCompetencie = (String) competenciesIterator.firstKey();
              if (((Integer)allCompetencies.get(thisCompetencie)).intValue()==1) { %>
                <mm:node number="<%= thisCompetencie %>">
                  <tr style="vertical-align:top;">
                    <td class="listItem">
                      <img src="<mm:treefile page="/pop/gfx/present.gif" objectlist="$includePath" referids="$referids"/>" border="0"
                          alt="deze competentie was al aanwezig voor de cursus"/>
                    </td>
                    <%@ include file="comptablecell.jsp" %>
                  </tr>
                </mm:node>
        <%    } 
              competenciesIterator.remove(thisCompetencie);
            } 
            competenciesIterator = (TreeMap) allCompetencies.clone();
            while(competenciesIterator.size()>0) { 
              String thisCompetencie = (String) competenciesIterator.firstKey();
              if (((Integer)allCompetencies.get(thisCompetencie)).intValue()==2) { %>
                <mm:node number="<%= thisCompetencie %>">
                  <tr style="vertical-align:top;">
                    <td class="listItem">
                      <img src="<mm:treefile page="/pop/gfx/developed.gif" objectlist="$includePath" referids="$referids"/>" border="0"
                          alt="deze competentie is behaald tijdens de cursus"/>
                    </td>
                    <%@ include file="comptablecell.jsp" %>
                  </tr>
                </mm:node>
        <%    } 
              competenciesIterator.remove(thisCompetencie);
            } 
            competenciesIterator = (TreeMap) allCompetencies.clone();
            while(competenciesIterator.size()>0) { 
              String thisCompetencie = (String) competenciesIterator.firstKey();
              int flag = ((Integer)allCompetencies.get(thisCompetencie)).intValue();
              if (flag==3 || flag==4) { %>
                <mm:node number="<%= thisCompetencie %>">
                  <tr style="vertical-align:top;">
                    <td class="listItem">
                      <img src="<mm:treefile page="/pop/gfx/todevelop.gif" objectlist="$includePath" referids="$referids"/>" border="0"
                          alt="deze competentie is nodig"/>
                    </td>
                    <%@ include file="comptablecell.jsp" %>
                  </tr>
                </mm:node>
        <%    } 
              competenciesIterator.remove(thisCompetencie);
            } 
        %>
      </mm:node>
    </table></div>
</div>
</fmt:bundle>
</mm:cloud>
</mm:content>
