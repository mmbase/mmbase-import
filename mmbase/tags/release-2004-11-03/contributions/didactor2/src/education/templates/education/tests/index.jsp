<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import id="testNo" externid="learnobject" required="true"/>

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<%-- find user's copybook --%>
<mm:import id="copybookNo"/>
<mm:node number="$user">
  <mm:relatedcontainer path="classrel,classes">
    <mm:constraint field="classes.number" value="$class"/>
    <mm:related>
      <mm:node element="classrel">
        <mm:relatednodes type="copybooks">
          <mm:remove referid="copybookNo"/>
          <mm:field id="copybookNo" name="number" write="false"/>
        </mm:relatednodes>
      </mm:node>
    </mm:related>  
  </mm:relatedcontainer>
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
        <h1><mm:field name="name"/></h1>
        <mm:field id="maychange" name="maychange" write="false"/>
        <mm:field id="mayview" name="mayview" write="false"/>

        <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS">
	  De toets is reeds gemaakt. De antwoorden moeten nog worden nagekeken.<p/>
        </mm:compare>
        <mm:compare referid="madetestscore" referid2="TESTSCORE_TBS" inverse="true">
          <%-- if madestestscore larger or equal than requiredscore --%>
          <mm:field id="requiredscore" name="requiredscore" write="false"/>
          <mm:islessthan referid="madetestscore" referid2="requiredscore" inverse="true">
            De toets is reeds gemaakt en behaald.<p/>
            <mm:compare referid="mayview" value="1">
              <table><tr>
                 <td><div class="button1">
                   <a href="<mm:treefile page="/education/tests/viewanswersframe.jsp" objectlist="$includePath" referids="$referids">
                   <mm:param name="testNo"><mm:write referid="testNo"/></mm:param>
                   <mm:param name="madetestNo"><mm:write referid="madetestNo"/></mm:param>
                   <mm:param name="userNo"><mm:write referid="user"/></mm:param>
                   </mm:treefile>"><di:translate id="view">Inzien</di:translate></a>
               </div></td>
               <td><div class="button1">
                    <a href="<mm:treefile page="/education/tests/buildtest.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
                </mm:treefile>"><di:translate id="again">Opnieuw maken</di:translate></a>
               </div></td>
               </tr></table>
            </mm:compare>
          </mm:islessthan>
          <mm:islessthan referid="madetestscore" referid2="requiredscore">
            De toets is reeds gemaakt en niet behaald.<p/>
<%--
score: <mm:write referid="madetestscore"/>&lt;<mm:write referid="requiredscore"/>
--%>
            <mm:compare referid="maychange" value="1">
              <table><tr><td><div class="button1">
                    <a href="<mm:treefile page="/education/tests/buildtest.jsp" objectlist="$includePath" referids="$referids">
                 <mm:param name="learnobject"><mm:write referid="testNo"/></mm:param>
                </mm:treefile>"><di:translate id="retry">Herkans</di:translate></a>
               </div></td></tr></table>
            </mm:compare>
          </mm:islessthan>
        </mm:compare>
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
