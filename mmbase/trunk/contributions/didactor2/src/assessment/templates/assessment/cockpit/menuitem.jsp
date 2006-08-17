<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>



<mm:cloud method="delegate" jspvar="cloud">

   <%@include file="/shared/setImports.jsp" %>

   <mm:import externid="provider" reset="true"/>
   <mm:import externid="education" reset="true"/>
   <mm:import externid="class" reset="true"/>


   <mm:node number="component.assessment" notfound="skip">

      <mm:node number="$user">
         <mm:related path="classes,educations,components,providers" constraints="providers.number=$provider AND components.name='assessment'" max="1">
            <mm:node element="educations">
               <mm:import id="link_education"><mm:field name="number"/></mm:import>
            </mm:node>
            <mm:node element="classes">
               <mm:import id="link_class"><mm:field name="number"/></mm:import>
            </mm:node>
         </mm:related>
      </mm:node>


      <div class="menuSeperator"> </div>
      <div class="menuItem">
         <a href='<mm:treefile page="/assessment/index.jsp" objectlist="$includePath" referids="provider?">
                     <mm:param name="class"><mm:write referid="link_class"/></mm:param>
                     <mm:param name="education"><mm:write referid="link_education"/></mm:param>
                  </mm:treefile>' class="menubar"><di:translate key="assessment.education_menu_item_assessment" /></a>
      </div>



      <div class="menuSeperator"> </div>
      <div class="menuItem">
         <a href='<mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="provider?">
                     <mm:param name="class"><mm:write referid="link_class"/></mm:param>
                     <mm:param name="education"><mm:write referid="link_education"/></mm:param>
                     <mm:param name="frame">
                        <mm:url referids="provider" page="../assessment/mail_to_coach.jsp">
                           <mm:param name="class"><mm:write referid="link_class"/></mm:param>
                           <mm:param name="education"><mm:write referid="link_education"/></mm:param>
                        </mm:url>
                     </mm:param>
                  </mm:treefile>' class="menubar"><di:translate key="assessment.education_menu_item_mail_to_coach" /></a>
      </div>



   </mm:node>

</mm:cloud>

