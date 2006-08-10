<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm" %>
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

         <di:translate key="assessment.mail_to_coach___text_below_title" />

         <form action="mail_to_coach_send.jsp" method="post">
            <input type="hidden" name="provider" value="<mm:write referid="provider"/>"/>
            <input type="hidden" name="education" value="<mm:write referid="education"/>"/>
            <input type="hidden" name="class" value="<mm:write referid="class"/>"/>
            <table class="font" width="90%">
               <tr>
                  <td><textarea name="message" class="popFormInput" cols="50" rows="5"></textarea></td>
               </tr>
            </table>
            <br/>
            <input type="submit" class="formbutton" value="<di:translate key="assessment.mail_to_coach___send_button_text" />">
            <input type="reset" class="formbutton"  value="<di:translate key="assessment.mail_to_coach___cancel_button_text" />">
         </form>
      </div>
   </div>

</mm:cloud>
