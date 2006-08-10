<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:import externid="provider"/>
<mm:import externid="education"/>
<mm:import externid="class"/>


<mm:cloud method="delegate" jspvar="cloud">

   <mm:node number="component.assessment" notfound="skip">
      <mm:present referid="education">
         <div class="menuSeperator"> </div>
         <div class="menuItem">
            <a href='<mm:url referids="provider,education,class" page="../assessment/index.jsp"/>' class="menubar"><di:translate key="assessment.education_menu_item_assessment" /></a>
         </div>

         <div class="menuSeperator"> </div>
         <div class="menuItem">
            <a href="<mm:url referids="provider,education,class" page="<%= request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/education/index.jsp" %>">
                        <mm:param name="frame"><mm:url referids="provider,education,class" page="../assessment/mail_to_coach.jsp"/></mm:param>
                     </mm:url>"
            class="menubar"><di:translate key="assessment.education_menu_item_mail_to_coach" /></a>
         </div>
      </mm:present>
   </mm:node>

</mm:cloud>

