<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@ include file="getids.jsp" %>
<mm:import externid="msg">-1</mm:import>
  <form name="todolistform" action="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
          referids="$popreferids,currentfolder">
      </mm:treefile>" method="post">
    <input type="hidden" name="popcmd" value="no">
    <input type="hidden" name="returnto" value="no">
    <div class="contentSubHeader">
      <a href="#1" onclick="todolistform.popcmd.value='addtodo';todolistform.submit();return false;">
        <img src="<mm:treefile page="/pop/gfx/icon_add_todo.gif" objectlist="$includePath" referids="$popreferids"/>"
            border="0" title="<di:translate key="pop.compeditmakenewtodo"/>" alt="<di:translate key="pop.compeditmakenewtodo"/>" /></a>
      <a href="#1" onclick="if (!window.confirm('<di:translate key="pop.areyousuredeltodo" />'))
          return false;todolistform.popcmd.value='deltodo';todolistform.submit();return false;">
        <img src="<mm:treefile page="/pop/gfx/afspraak verwijderen.gif" objectlist="$includePath" referids="$popreferids"/>"
            border="0" title="<di:translate key="pop.compeditremoveselectedtodo"/>" alt="<di:translate key="pop.compeditremoveselectedtodo"/>" /></a>
    </div> 
    <div class="contentBody">
      <mm:compare referid="msg" value="-1" inverse="true">
        <mm:write referid="msg" escape="text/plain"/>
      </mm:compare>
      <mm:node number="$currentpop">
        <mm:relatedcontainer path="related,todoitems">
          <di:table>
            <di:row>
              <di:headercell>
                <input type="checkbox" onclick="selectAllClicked(this.form,this.checked)">
              </di:headercell>
              <di:headercell><di:translate key="pop.todotask" /></di:headercell>
              <di:headercell><di:translate key="pop.tododuration" /></di:headercell>
              <di:headercell><di:translate key="pop.description" /></di:headercell>
              <di:headercell><di:translate key="pop.competence" /></di:headercell>
            </di:row>

            <mm:related>
              <di:row>
                <mm:node element="todoitems">
                  <di:cell>
                    <input type="checkbox" name="ids" value="<mm:field name="number"/>">
                  </di:cell>
                  <di:cell>
                    <mm:field name="name" jspvar="todoName" vartype="String">
                      <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$popreferids,currentfolder">
                          <mm:param name="todonumber"><mm:field name="number"/></mm:param>
                          <mm:param name="popcmd">addtodo</mm:param>
                          <mm:param name="returnto">no</mm:param>
                        </mm:treefile>"><% if (todoName.length()>0) { %><%= todoName %><% } else { %>...<% } %></a>
                    </mm:field>
                  </di:cell>
                  <di:cell>                   
                    <mm:field name="durationvalue"/>
                    <mm:import id="durationmeasure" reset="true"><mm:field name="durationmeasure"/></mm:import>
                    <mm:compare referid="durationmeasure" value="1"><di:translate key="pop.todohour" /></mm:compare>
                    <mm:compare referid="durationmeasure" value="2"><di:translate key="pop.tododay" /></mm:compare>
                    <mm:compare referid="durationmeasure" value="3"><di:translate key="pop.todoweek" /></mm:compare>
                    <mm:compare referid="durationmeasure" value="4"><di:translate key="pop.todomonth" /></mm:compare>
                    <mm:compare referid="durationmeasure" value="5"><di:translate key="pop.todoyear" /></mm:compare>
                  </di:cell>
                  <di:cell>
                    <mm:field name="description"/>
                  </di:cell>
                  <di:cell>
                    <mm:related path="competencies">
                      <mm:field name="competencies.name"/>
                    </mm:related>
                  </di:cell>
                </mm:node>
              </di:row>
            </mm:related>
          </di:table>
        </mm:relatedcontainer>
      </mm:node>

      <script>
        function selectAllClicked(frm, newState) {
	  if (frm.elements['ids'].length) {
	    for(var count =0; count < frm.elements['ids'].length; count++ ) {
		var box = frm.elements['ids'][count];
		box.checked=newState;
	    }
	  }
	  else {
	      frm.elements['ids'].checked=newState;
	  }
        }
      </script> 
    </div>
  </form>
</mm:cloud>
</mm:content>
