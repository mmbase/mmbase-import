<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title>Assessment matrix</title>
      <link rel="stylesheet" type="text/css" href="css/assessment.css" />
    </mm:param>
  </mm:treeinclude>

  <script language="JavaScript" type="text/javascript">
    function toggle(number) {
      if( document.getElementById("toggle_div" + number).style.display=='none' ){
        document.getElementById("toggle_div" + number).style.display = '';
        document.getElementById("toggle_image" + number).src = "<mm:treefile page="/assessment/gfx/minus.gif"
                                  objectlist="$includePath" referids="$referids"/>";
      } else {
        document.getElementById("toggle_div" + number).style.display = 'none';
        document.getElementById("toggle_image" + number).src = "<mm:treefile page="/assessment/gfx/plus.gif"
                                  objectlist="$includePath" referids="$referids"/>";
      }
    }
  </script>

  <%@include file="variables.jsp" %>

  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <img src="<mm:treefile write="true" page="/gfx/icon_pop.gif" objectlist="$includePath" />" 
            width="25" height="13" border="0" title="<di:translate key="pop.popfull" />" alt="<di:translate key="pop.popfull" />" /> <di:translate key="pop.popfull" />
      </div>		
    </div>

<div class="folders">
  <div class="folderHeader">
  </div>
  <div class="folderBody">
  </div>
</div>


    <%-- right section --%>
    <div class="mainContent">
      <div class="contentBody">
        Goals
        <br/>
        <mm:node number="$user">
          <mm:relatednodes type="goals" path="posrel,goals" orderby="posrel.pos">
            <mm:first><table></mm:first>
              <mm:field name="number" jspvar="goal_number" vartype="String" write="false">
                <tr>
                  <td>
                    <a href="<mm:treefile page="/assessment/editgoal.jsp" objectlist="$includePath" referids="$referids">
                               <mm:param name="goal_n"><mm:field name="number"/></mm:param>
                             </mm:treefile>"
                      ><img src="<mm:treefile page="/assessment/gfx/edit_learnobject.gif" objectlist="$includePath" 
                            referids="$referids"/>" border="0" title="edit goal" alt="edit goal" /></a>
                    <a href="<mm:treefile page="/assessment/deleteobject.jsp" objectlist="$includePath" referids="$referids">
                               <mm:param name="object_n"><mm:field name="number"/></mm:param>
                             </mm:treefile>"
                      ><img src="<mm:treefile page="/assessment/gfx/remove.gif" objectlist="$includePath" 
                            referids="$referids"/>" border="0" title="delete goal" alt="delete goal" /></a>
                    <img src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
                         referids="$referids"/>" border="0" title="show goal" alt="show goal" 
                         onClick="toggle(<%=goal_number %>);" id="toggle_image<%=goal_number %>"/>
                  </td>
                  <td>
                    <mm:field name="name" jspvar="dummy" vartype="String" write="false">
                      <%= ( "".equals(dummy) ? "&nbsp;" : dummy )%>
                    </mm:field>
                  </td>
                </tr>
                <tr id="toggle_div<%=goal_number %>" style="display:none">
                  <td>&nbsp;</td>
                  <td>
                    <mm:field name="description" jspvar="dummy" vartype="String" write="false">
                      <%= ( "".equals(dummy) ? "&nbsp;" : dummy )%>
                    </mm:field>
                  </td>
                </tr>
              </mm:field>
            <mm:last></table></mm:last>
          </mm:relatednodes>
        </mm:node>
        <a href="<mm:treefile page="/assessment/editgoal.jsp" objectlist="$includePath" referids="$referids"/>"
          ><img src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
               referids="$referids"/>" border="0" title="add goal" alt="add goal" /></a>
        <br/>
        <br/>
        <br/>
        <% int lessonsNum = 0; %>
        <mm:node number="assessment.education" notfound="skip">
          <mm:relatednodes type="learnblocks" path="posrel,learnblocks">
            <mm:import id="dummy" jspvar="dummy" vartype="Integer" reset="true"><mm:size/></mm:import>
            <% lessonsNum = dummy.intValue(); %>
          </mm:relatednodes>
        </mm:node>
        <div><table class="poplistTable">
          <tr style="vertical-align:top;">
            <th class="listHeader">&nbsp;</th>
            <th class="listHeader">Problems</th>
            <th class="listHeader">Type</th>
            <mm:node number="assessment.education" notfound="skip">
              <mm:relatednodes type="learnblocks" path="posrel,learnblocks">
                <th class="listHeader"><mm:field name="name"/></th>
              </mm:relatednodes>
            </mm:node>
          </tr>
          <mm:node number="$user">
            <mm:relatednodes type="problems" path="posrel,problems" orderby="posrel.pos">
              <mm:field name="number" jspvar="problem_number" vartype="String" write="false">
              <tr style="vertical-align:top;">
                <td class="listItem">
                  <a href="<mm:treefile page="/assessment/editproblem.jsp" objectlist="$includePath" referids="$referids">
                             <mm:param name="problem_n"><mm:field name="number"/></mm:param>
                           </mm:treefile>"
                    ><img src="<mm:treefile page="/assessment/gfx/edit_learnobject.gif" objectlist="$includePath" 
                          referids="$referids"/>" border="0" title="edit problem" alt="edit problem" /></a>
                  <a href="<mm:treefile page="/assessment/deleteobject.jsp" objectlist="$includePath" referids="$referids">
                             <mm:param name="object_n"><mm:field name="number"/></mm:param>
                           </mm:treefile>"
                    ><img src="<mm:treefile page="/assessment/gfx/remove.gif" objectlist="$includePath" 
                          referids="$referids"/>" border="0" title="delete problem" alt="delete problem" /></a>
                </td>
                <%@ include file="matrixcell.jsp" %>
              </tr>
              </mm:field>
            </mm:relatednodes>
          </mm:node>
          <tr>
            <td class="listItem">
              <a href="<mm:treefile page="/assessment/editproblem.jsp" objectlist="$includePath" referids="$referids"/>"
                ><img src="<mm:treefile page="/assessment/gfx/plus.gif" objectlist="$includePath" 
                      referids="$referids"/>" border="0" title="add problem" alt="add problem" /></a>
            </td>
            <% for(int i=0; i<lessonsNum+2; i++) { %>
                 <td class="listItem">&nbsp;</td>
            <% } %>
          </tr>
          <tr>
            <td class="listItem">&nbsp;</td>
            <td class="listItem">Feedback coach</td>
            <mm:node number="assessment.education" notfound="skip">
              <mm:relatednodes type="learnblocks" path="posrel,learnblocks">
                <td class="listItem">
                  <% String feedback = ""; %>
                  <mm:relatedcontainer path="classrel,people">
                    <mm:constraint field="people.number" value="$user"/>
                    <mm:related>
                      <mm:node element="classrel">
                        <mm:relatednodes type="feedback">
                          <mm:field name="text" jspvar="dummy" vartype="String" write="false">
                            <% feedback = dummy; %>
                          </mm:field>
                        </mm:relatednodes>
                      </mm:node>
                    </mm:related>
                  </mm:relatedcontainer>
                  <%= "".equals(feedback) ? "&nbsp;" : feedback %>
                </td>
              </mm:relatednodes>
            </mm:node>
          </tr>
          <tr>
            <% for(int i=0; i<lessonsNum+3; i++) { %>
                 <td class="listItem">&nbsp;</td>
            <% } %>
          </tr>
        </table></div>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
