<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:import externid="provider"/>
<mm:import externid="education"/>
<mm:import externid="class"/>


<mm:cloud method="delegate" jspvar="cloud">

   <mm:node number="component.assessment" notfound="skip">
      <div class="menuSeperator"> </div>
      <div class="menuItem">
         <a href='<mm:url referids="provider,education,class" page="<%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/assessment/index.jsp" %>"/>' class="menubar"><di:translate key="assessment.education_menu_item_assessment" /></a>
      </div>


      <mm:present referid="education">
         <mm:import id="education_link" reset="true"><mm:write referid="education" /></mm:import>
      </mm:present>

      <mm:notpresent referid="education">
         <mm:import id="education_link" reset="true">-1</mm:import>
         <mm:node number="component.assessment" id="component_assessment"/>
         <mm:node number="$provider" notfound="skip">
            <mm:related path="related,educations,settingrel,components" constraints="components.number=$component_assessment">
               <mm:import id="education_link" reset="true"><mm:field name="educations.number"/></mm:import>
            </mm:related>
         </mm:node>
      </mm:notpresent>

      <div class="menuSeperator"> </div>
      <div class="menuItem">
         <a href="<mm:url referids="provider,education,class" page="<%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/education/index.jsp" %>">
                     <mm:param name="frame"><mm:url referids="provider,education" page="../assessment/mail_to_coach.jsp"/></mm:param>
                  </mm:url>"
         class="menubar"><di:translate key="assessment.education_menu_item_mail_to_coach" /></a>
      </div>
   </mm:node>

   <mm:remove referid="education_link"/>

</mm:cloud>

