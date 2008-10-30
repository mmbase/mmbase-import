<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="getids.jsp" %>
<% boolean isEmpty = true; %>
<mm:import externid="msg">-1</mm:import>
<div class="contentBody">
    <mm:compare referid="msg" value="-1" inverse="true">
      <mm:write referid="msg" escape="text/plain"/>
    </mm:compare>
    <div><table class="poplistTable">
      <tr style="vertical-align:top;">
        <th class="listHeader">&nbsp;</th>
        <th class="listHeader"><di:translate key="pop.competence" /></th>
        <th class="listHeader"><di:translate key="pop.comptableworkedon" /></th>
        <th class="listHeader"><di:translate key="pop.comptableselfassessment" /></th>
        <th class="listHeader"><di:translate key="pop.score" /></th>
        <th class="listHeader"><di:translate key="pop.comptabletodoitems" /></th>
        <th class="listHeader"><di:translate key="pop.portfolio" /></th>
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
                      <img src="<mm:treefile page="/pop/gfx/present.gif" objectlist="$includePath" referids="$popreferids"/>" border="0"
                          title="<di:translate key="pop.comphave"/>"  alt="<di:translate key="pop.comphave"/>" />
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
                      <img src="<mm:treefile page="/pop/gfx/developed.gif" objectlist="$includePath" referids="$popreferids"/>" border="0"
                          title="<di:translate key="pop.compdeveloped"/>"  alt="<di:translate key="pop.compdeveloped"/>" />
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
                      <img src="<mm:treefile page="/pop/gfx/todevelop.gif" objectlist="$includePath" referids="$popreferids"/>" border="0"
                          title="<di:translate key="pop.compneeded"/>" alt="<di:translate key="pop.compneeded"/>" />
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
</mm:cloud>
</mm:content>
