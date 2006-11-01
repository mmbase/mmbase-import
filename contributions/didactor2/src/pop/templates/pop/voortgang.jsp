<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@page import="java.util.*" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %> 
<%@ include file="getids.jsp" %>

<%	String intakeCompetencies = ""; 
	String notpassedIntakes = ""; 
%>

<mm:import externid="msg">-1</mm:import>
  <div class="contentBody">
    <div><table class="listTable">
      <tr style="vertical-align:top;">
        <th class="listHeader"><di:translate key="pop.progresstabletitleeducation" /></th>
        <th class="listHeader"><di:translate key="pop.progresstableintake" /></th>
        <th class="listHeader"><di:translate key="pop.progresstablestarted" /></th>
        <th class="listHeader"><di:translate key="pop.progress" /></th>
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
              ><mm:field name="educations.name"/></a><br/> <di:translate key="pop.class" />: <mm:field name="classes.name"/>
          </td>
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
                      <mm:param name="popcmd">detail</mm:param>
                    </mm:treefile>"><img src="/didactor/pop/gfx/1.gif" width=4 height=13 title="" alt="" border="0" 
                                      /><img src="/didactor/pop/gfx/2.gif" width=<%= progress %> height=13 
                                      title="<di:translate key="pop.gototestrelults" />"  alt="<di:translate key="pop.gototestrelults" />" border="0" 
                                      /><img src="/didactor/pop/gfx/3.gif" width=2 height=13 title="" alt="" 
                                      border="0" /><img src="/didactor/pop/gfx/4.gif" 
                                      width=<%= 100-progress.doubleValue() %> height=13 
                                      title="<di:translate key="pop.gototestrelults" />" alt="<di:translate key="pop.gototestrelults" />" border="0" 
                                      /><img src="/didactor/pop/gfx/5.gif" width=2 height=13 title="" alt="" 
                                      border="0" /></a><mm:write referid="progress"/>%
          </td>
          <td class="listItem">
            <mm:compare referid="intake" value="1">
              <mm:compare referid="startflag" value="1">
                <mm:compare referid="finished" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                    </mm:treefile>'" value="<di:translate key="pop.continuebutton" />" title="<di:translate key="pop.continuecoursebutton" />">
                </mm:compare>
              </mm:compare>
              <mm:compare referid="startflag" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                    </mm:treefile>'" value="<di:translate key="pop.startbutton" />"
                    title="<di:translate key="pop.begincoursebutton" />">
              </mm:compare>
            </mm:compare>
            <mm:compare referid="intake" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
                        referids="$popreferids,currentfolder">
                      <mm:param name="popcmd">intake</mm:param>
                    </mm:treefile>'" value="intake" 
                    title="<di:translate key="pop.intakecoursebutton" />">
            </mm:compare>
          </td>
        </tr>
      </mm:list>
      <mm:list nodes="$student" path="people,classrel,educations" fields="educations.number"
          orderby="educations.number" directions="UP">
        <mm:import id="education" reset="true"><mm:field name="educations.number"/></mm:import>
        <mm:import id="class" reset="true">null</mm:import>
        <%@ include file="getprogress.jsp" %>
        <mm:import id="progressvalue" jspvar="progress" vartype="Double" reset="true"><mm:write referid="progress"/></mm:import>

        <tr style="vertical-align:top;">
          <td class="listItem"><a href="<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$popreferids"/>"
              ><mm:field name="educations.name"/></a>
          </td>
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
                      <mm:param name="popcmd">detail</mm:param>
                      <mm:param name="direct_connection">true</mm:param>
                    </mm:treefile>"><img src="/didactor/pop/gfx/1.gif" width=4 height=13 title="" alt="" border="0" 
                                      /><img src="/didactor/pop/gfx/2.gif" width=<%= progress %> height=13 
                                      title="<di:translate key="pop.gototestrelults" />" alt="<di:translate key="pop.gototestrelults" />" border="0" 
                                      /><img src="/didactor/pop/gfx/3.gif" width=2 height=13 title="" alt="" 
                                      border="0" /><img src="/didactor/pop/gfx/4.gif" 
                                      width=<%= 100-progress.doubleValue() %> height=13 
                                      title="<di:translate key="pop.gototestrelults" />" alt="<di:translate key="pop.gototestrelults" />" border="0" 
                                      /><img src="/didactor/pop/gfx/5.gif" width=2 height=13 title="" alt="" 
                                      border="0" /></a><mm:write referid="progress"/>%
          </td>
          <td class="listItem">
            <mm:compare referid="intake" value="1">
              <mm:compare referid="startflag" value="1">
                <mm:compare referid="finished" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                    </mm:treefile>'" value="<di:translate key="pop.continuebutton" />" title="<di:translate key="pop.continuecoursebutton" />">
                </mm:compare>
              </mm:compare>
              <mm:compare referid="startflag" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="$referids">
                    </mm:treefile>'" value="<di:translate key="pop.startbutton" />"
                    title="<di:translate key="pop.begincoursebutton" />">
              </mm:compare>
            </mm:compare>
            <mm:compare referid="intake" value="0">
                  <input type="button" class="formbutton" onClick="top.location.href='<mm:treefile page="/pop/index.jsp" objectlist="$includePath" 
                        referids="$popreferids,currentfolder">
                      <mm:param name="popcmd">intake</mm:param>
                    </mm:treefile>'" value="intake" 
                    title="<di:translate key="pop.intakecoursebutton" />">
            </mm:compare>
          </td>
        </tr>
      </mm:list>
    </table></div>
  </div>
</mm:cloud>
</mm:content>
