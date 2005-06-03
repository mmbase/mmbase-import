<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %> 
<%@ include file="getids.jsp" %>

<%	String intakeCompetencies = ""; 
	String notpassedIntakes = ""; 
%>

<mm:import externid="msg">-1</mm:import>
<fmt:bundle basename="nl.didactor.component.workspace.WorkspaceMessageBundle">
  <div class="contentBody">
    <div><table class="listTable">
      <tr style="vertical-align:top;">
        <th class="listHeader">Titel opleiding</th>
        <th class="listHeader">Intake</th>
        <th class="listHeader">Gestart</th>
        <th class="listHeader">Voortgang</th>
        <th class="listHeader">&nbsp;</th>
      </tr>
      <mm:list nodes="$student" path="people,classrel,classes,educations" fields="educations.number"
          orderby="educations.number" directions="UP">
        <mm:import id="education" reset="true"><mm:field name="educations.number"/></mm:import>
        <mm:import id="class" reset="true"><mm:field name="classes.number"/></mm:import>
        <%@ include file="getprogress.jsp" %>
        <mm:import id="progressvalue" jspvar="progress" vartype="Double" reset="true"><mm:write referid="progress"/></mm:import>

        <tr style="vertical-align:top;">
          <td class="listItem"><a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$popreferids"/>"
              ><mm:field name="educations.name"/></a><br/> from class <mm:field name="classes.name"/></td>
          <td class="listItem" style="text-align:center">
            <mm:compare referid="intake" value="1">
              <img src="<mm:treefile page="/pop/gfx/check.gif" objectlist="$includePath" referids="$popreferids"/>"
                border="0"/>
            </mm:compare>
            <mm:compare referid="intake" value="0">
              <img src="<mm:treefile page="/pop/gfx/cross.gif" objectlist="$includePath" referids="$popreferids"/>"
                border="0"/>
            </mm:compare>
          </td>
          <td class="listItem" style="text-align:center">
            <mm:compare referid="startflag" value="1">
              <img src="<mm:treefile page="/pop/gfx/check.gif" objectlist="$includePath" referids="$popreferids"/>"
                border="0"/>
            </mm:compare>
            <mm:compare referid="startflag" value="0">
              <img src="<mm:treefile page="/pop/gfx/cross.gif" objectlist="$includePath" referids="$popreferids"/>"
                border="0"/>
            </mm:compare>
          </td>
          <td class="listItem">
              <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
                        referids="$popreferids,currentfolder">
                      <mm:param name="command">detail</mm:param>
                    </mm:treefile>">
<img src="/didactor/pop/gfx/1.gif" width=4 height=13 alt="" border="0" /><img src="/didactor/pop/gfx/2.gif" width=<%= progress %> height=13 alt="Bekijk de testresultaten" border="0" /><img src="/didactor/pop/gfx/3.gif" width=2 height=13 alt="" border="0" /><img src="/didactor/pop/gfx/4.gif" width=<%= 100-progress.doubleValue() %> height=13 alt="Bekijk de testresultaten" border="0" /><img src="/didactor/pop/gfx/5.gif" width=2 height=13 alt="" border="0" /></a><mm:write referid="progress"/>%</td>
          <td class="listItem">
            <mm:compare referid="intake" value="1">
              <mm:compare referid="startflag" value="1">
                <mm:compare referid="finished" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$popreferids">
                    </mm:treefile>'" value="verder" title="Ga verder met deze cursus">
                </mm:compare>
              </mm:compare>
              <mm:compare referid="startflag" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$popreferids">
                    </mm:treefile>'" value="start"
                    title="Begin met deze cursus">
              </mm:compare>
            </mm:compare>
            <mm:compare referid="intake" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
                        referids="$popreferids,currentfolder">
                      <mm:param name="command">intake</mm:param>
                    </mm:treefile>'" value="intake" 
                    title="Doe de intake voor deze cursus">
            </mm:compare>
          </td>
        </tr>
      </mm:list>
    </table></div>
  </div>
</fmt:bundle>
</mm:cloud>
</mm:content>
