<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>

<mm:remove referid="provider"/>
<mm:remove referid="education"/>
<mm:remove referid="class"/>

<mm:import externid="provider"/>
<mm:import externid="education"/>
<mm:import externid="class">null</mm:import>



  <head>
    <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
    <link rel="stylesheet" type="text/css" href="css/assessment.css" />
  </head>


   <div class="mainContent" style="left:-10px">
      <div style="padding-left:20px; padding-top:20px; font-size:15px"><di:translate key="assessment.mail_to_coach___title" /></div>
      <div class="contentBody">


         <mm:node number="$user">
            <mm:import id="email_from" reset="true"><mm:field name="email"/></mm:import>

            <mm:compare referid="email_from" value="">
               <di:translate key="assessment.mail_to_coach___no_your_email" />
            </mm:compare>

            <mm:compare referid="email_from" value="" inverse="true">
               <%@ include file="includes\looks_for_coaches.jsp" %>

               <mm:import id="tested_coaches" reset="true"></mm:import>

               <mm:compare referid="list_of_coaches" value="" inverse="true">
                  <mm:list nodes="$list_of_coaches" path="people">
                     <mm:node element="people" jspvar="nodePeople">
                        <mm:import id="email" reset="true"><mm:field name="email"/></mm:import>

                        <mm:compare referid="email" value="">
                           <di:translate key="assessment.mail_to_coach___no_email_for_coach" arg0="<%= nodePeople.getStringValue("firstname") %>" arg1="<%= nodePeople.getStringValue("lastname") %>" />
                           <br/>
                        </mm:compare>

                        <mm:compare referid="email" value="" inverse="true">
                           <mm:compare referid="tested_coaches" value="">
                              <mm:import id="tmp" reset="true"><mm:field name="number"/></mm:import>
                           </mm:compare>
                           <mm:compare referid="tested_coaches" value="" inverse="true">
                              <mm:import id="tmp" reset="true"><mm:write referid="tested_coaches"/>,<mm:field name="number"/></mm:import>
                           </mm:compare>
                           <mm:import id="tested_coaches" reset="true"><mm:write referid="tmp"/></mm:import>
                        </mm:compare>
                     </mm:node>
                  </mm:list>
               </mm:compare>

               <br/>

               <mm:compare referid="tested_coaches" value="">
                  <mm:compare referid="list_of_coaches" value="">
                     <di:translate key="assessment.mail_to_coach___no_coach" />
                  </mm:compare>
               </mm:compare>

               <mm:compare referid="tested_coaches" value="" inverse="true">
                  <di:translate key="assessment.mail_to_coach___text_below_title" />

                  <form action="mail_to_coach_send.jsp" method="post">
                     <input type="hidden" name="provider" value="<mm:write referid="provider"/>"/>
                     <input type="hidden" name="education" value="<mm:write referid="education"/>"/>
                     <input type="hidden" name="class" value="<mm:write referid="class"/>"/>
                     <input type="hidden" name="list_of_coaches" value="<mm:write referid="tested_coaches"/>" />
                     <table class="font" width="90%">
                        <tr>
                           <td><textarea name="message" class="popFormInput" cols="50" rows="5"></textarea></td>
                        </tr>
                     </table>
                     <br/>
                     <input type="submit" class="formbutton" value="<di:translate key="assessment.mail_to_coach___send_button_text" />">
                     <input type="reset" class="formbutton"  value="<di:translate key="assessment.mail_to_coach___cancel_button_text" />">
                  </form>
               </mm:compare>
            </mm:compare>
         </mm:node>
      </div>
   </div>

</mm:cloud>
