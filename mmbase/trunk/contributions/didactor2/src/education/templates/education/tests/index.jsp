<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>


<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">


<mm:import id="testNo" externid="learnobject" required="true"/>



<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>


<%-- remember this page --%>
<mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
   <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
   <mm:param name="learnobjecttype">tests</mm:param>
</mm:treeinclude>


<%-- find user's copybook --%>
<mm:node number="$user">
   <%@include file="find_copybook.jsp"%>
</mm:node>


<mm:node number="$testNo">
  <mm:relatednodescontainer path="madetests,copybooks" element="madetests">

    <mm:constraint field="madetests.score" referid="TESTSCORE_INCOMPLETE" inverse="true"/>

    <mm:constraint field="copybooks.number" referid="copybookNo"/>

    <mm:relatednodes>

       <mm:field id="madetestNo" name="number" write="false"/>

       <mm:field id="madetestscore" name="score" write="false"/>

    </mm:relatednodes>

  </mm:relatednodescontainer>



  <mm:present referid="madetestNo">

    <html>

      <head>

        <title>Toets</title>

        <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />

      </head>

      <body>

<div class="learnenvironment">


        <mm:field name="showtitle">
          <mm:compare value="1">
            <h1><mm:field name="name"/></h1>
          </mm:compare>
        </mm:field>

        <mm:field id="maychange" name="maychange" write="false"/>

        <mm:field id="mayview" name="mayview" write="false"/>

        <mm:field id="feedback" name="feedbackpage" write="false"/>



        <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS">

     De toets is reeds gemaakt. De antwoorden moeten nog worden nagekeken.<p/>

        </mm:compare>

        <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS" inverse="true">

          <%-- if madestestscore larger or equal than requiredscore --%>

          <mm:field id="requiredscore" name="requiredscore" write="false"/>



         <mm:islessthan referid="feedback" value="1">

          <mm:islessthan referid="madetestscore" referid2="requiredscore" inverse="true">

            De toets is reeds gemaakt en behaald.<p/>

          </mm:islessthan>



          <mm:islessthan referid="madetestscore" referid2="requiredscore">

            De toets is reeds gemaakt en niet behaald.<p/>

          </mm:islessthan>

         </mm:islessthan>



          <mm:compare referid="feedback" value="1">

            Deze oefentoets is reeds gemaakt.<p/>

          </mm:compare>





         <mm:compare referid="mayview" value="1">

              <table><tr>

                 <td><div class="button1">

                   <a href="<mm:treefile page="/education/tests/viewanswersframe.jsp" objectlist="$includePath" referids="$referids">

                   <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>

                   <mm:param name="madetestNo"><mm:write referid="madetestNo"/></mm:param>

                   <mm:param name="userNo"><mm:write referid="user"/></mm:param>

                   </mm:treefile>"><di:translate id="view">Inzien</di:translate></a>

               </div></td>

            </mm:compare>

            <mm:compare referid="maychange" value="1">

               <td><div class="button1">

                    <a href="<mm:treefile page="/education/tests/buildtest.jsp" objectlist="$includePath" referids="$referids">

                 <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>

                </mm:treefile>"><mm:compare referid="feedback" value="1"><di:translate id="again">Opnieuw maken</di:translate></mm:compare><mm:compare referid="feedback" value="0"><di:translate id="retry">Herkans</di:translate></a></mm:compare></a>

               </div></td>

               </tr></table>

               </mm:compare>

            </mm:compare>

</div>

      </body>

    </html>

  </mm:present>

</mm:node>



<mm:present referid="madetestNo" inverse="true">

  <mm:treeinclude page="/education/tests/buildtest.jsp" objectlist="$includePath" referids="$referids">

    <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>

   </mm:treeinclude>

</mm:present>



</mm:cloud>

</mm:content>

