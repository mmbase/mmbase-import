<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">

<mm:import externid="learnobject" required="true"/>
<mm:import id="madetest" externid="madetest"/>

<%-- Set default values --%>
<mm:import externid="questionsshowed" jspvar="questionsShowed" vartype="String"> </mm:import>
<mm:import externid="testpath" jspvar="testPath" vartype="String"></mm:import>
<mm:import externid="command" jspvar="sCommand" vartype="String">next</mm:import>
<mm:import externid="questionamount" jspvar="questionAmount" vartype="Integer">0</mm:import>
<mm:import externid="questionperpageamount">0</mm:import>
<mm:import externid="clearmadetest">false</mm:import>

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/tests/definitions.jsp" %>

<%-- find user's copybook --%>
<mm:node number="$user">
   <%@include file="find_copybook.jsp"%>
</mm:node>

<%-- remove old results (or use it) --%>
<mm:present referid="madetest" inverse="true">

 <mm:node number="$copybookNo" notfound="skip">
   <mm:node referid="learnobject">
      <mm:relatednodescontainer path="madetests,copybooks" element="madetests">
         <mm:constraint field="copybooks.number" referid="copybookNo"/>

         <mm:relatednodes>
            <mm:compare referid="clearmadetest" value="false">
               <mm:import id="madetest" reset="true"><mm:field name="number"/></mm:import>
            </mm:compare>
            <mm:compare referid="clearmadetest" value="true">
               <mm:relatednodescontainer type="givenanswers">
                  <%--Remove Made test with  <mm:size/> answers<br/> --%>
                  <mm:relatednodes>
                     <mm:maydelete>
                        <mm:deletenode deleterelations="true"/>
                     </mm:maydelete>
                  </mm:relatednodes>
               </mm:relatednodescontainer>
               <mm:maydelete>
                  <mm:deletenode deleterelations="true"/>
               </mm:maydelete>
            </mm:compare>
         </mm:relatednodes>

      </mm:relatednodescontainer>
   </mm:node>
 </mm:node>
</mm:present>

<html>
<head>
   <title>Content</title>
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath"/>"/>
</head>
<body>
<div class="learnenvironment">

<mm:node number="$copybookNo" notfound="skip">

<%-- Take care: form name is used in JavaScript of the specific question jsp pages! --%>
<form name="questionform" action="<mm:treefile page="/education/tests/rate.jsp" objectlist="$includePath" referids="$referids">
                                     <mm:param name="thismadetest"><mm:write referid="madetest"/></mm:param>
                                  </mm:treefile>" method="POST">

<mm:node number="$learnobject">
   <mm:field name="showtitle">
      <mm:compare value="1">
         <h1><mm:field name="name"/></h1>
      </mm:compare>
   </mm:field>

   <p/>
   <mm:field name="text" escape="none"/>


   <mm:compare referid="questionamount" value="0">
      <mm:remove referid="questionamount"/>
      <mm:import id="command" reset="true">next</mm:import>
      <mm:import id="questiontype"><mm:field name="questionamount"/></mm:import>
      <%-- Questiontype < 1 means undefined. Show all questions in the given order. --%>
      <mm:islessthan referid="questiontype" value="1">
         <mm:relatednodes type="questions" role="posrel" orderby="posrel.pos,title" id="my_questions">
            <mm:first>
               <mm:import id="questionamount"><mm:size/></mm:import>
            </mm:first>
         </mm:relatednodes>
      </mm:islessthan>
      <%-- Questionamount >= 1. Show the given amount of questions at random. --%>
      <mm:isgreaterthan referid="questiontype" value="0">
         <mm:relatednodes type="questions" max="$questiontype" comparator="SHUFFLE" id="my_questions">
            <mm:first>
               <mm:import id="questionamount"><mm:size/></mm:import>
            </mm:first>
         </mm:relatednodes>
      </mm:isgreaterthan>
      <%-- Check if questionamount is present --%>
      <mm:notpresent referid="questionamount">
         <mm:import id="questionamount">0</mm:import>
      </mm:notpresent>

      <mm:import id="questionperpageamount" reset="true"><mm:field name="questionsperpage"/></mm:import>
      <mm:islessthan referid="questionperpageamount" value="1">
         <mm:import id="questionperpageamount" reset="true"><mm:write referid="questionamount"/></mm:import>
      </mm:islessthan>
      <mm:isgreaterthan referid="questionamount" value="0">
         <mm:islessthan referid="questionamount" referid2="questionperpageamount">
            <mm:import id="questionperpageamount" reset="true"><mm:write referid="questionamount"/></mm:import>
         </mm:islessthan>
      </mm:isgreaterthan>
      <mm:islessthan referid="questionperpageamount" value="1">
            <mm:import id="questionperpageamount" reset="true">1</mm:import>
      </mm:islessthan>
   </mm:compare>

   <mm:compare referid="testpath" value="">
      <mm:node number="$madetest" notfound="skip">
         <mm:import id="testpath" reset="true"><mm:field name="testpath"/></mm:import>
      </mm:node>
   </mm:compare>
   <mm:compare referid="testpath" value="">
      <mm:relatednodes referid="my_questions">
         <mm:first>
            <mm:field name="number" jspvar="questionNo" vartype="String">
               <% testPath = questionNo; %>
            </mm:field>
         </mm:first>
         <mm:first inverse="true">
            <mm:field name="number" jspvar="questionNo" vartype="String">
               <% testPath += "," + questionNo; %>
            </mm:field>
         </mm:first>
      </mm:relatednodes>
      <mm:remove referid="testpath"/>
      <mm:import id="testpath"><%=testPath%></mm:import>
   </mm:compare>

   <mm:node number="$madetest" notfound="skip">
      <mm:setfield name="testpath"><mm:write referid="testpath"/></mm:setfield>
   </mm:node>

   <%-- Determine questions to show --%>
   <mm:import id="listtestpath" jspvar="listTestPath" vartype="List"><mm:write referid="testpath"/> </mm:import>
   <mm:import id="listqshowed" jspvar="listQShowed" vartype="List"><mm:write referid="questionsshowed"/> </mm:import>

   <mm:import id="dummy" jspvar="questionPerPageAmount" vartype="String"><mm:write referid="questionperpageamount"/></mm:import>
<% 
   int qCounter = 0;
   int i = 0;
   int flag = 1;
   int currentpos = 0;
   questionsShowed = " ";

   int iQPerPageAmount = new Integer (questionPerPageAmount).intValue();
   if (listQShowed.size() == 0) {
      currentpos = 0;
   } else {
      if (!sCommand.equals("back")) {
         currentpos = listTestPath.indexOf(listQShowed.get(listQShowed.size()-1)) + 1;
      } else {
         currentpos = listTestPath.indexOf(listQShowed.get(0)) - iQPerPageAmount;
      }
   }
   if (currentpos >= listTestPath.size()) {
      currentpos = listTestPath.size() - iQPerPageAmount;
   }
   if (currentpos < 0) {
      currentpos = 0;
   }

   i = currentpos;
   flag = 1; // first pass
   while (flag != 0) {
      String questionNo = (String) listTestPath.get(i);
%>
      <mm:node number="<%= questionNo %>" notfound="skip">
         <mm:import id="page" reset="true">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>
         <mm:treeinclude page="$page" objectlist="$includePath" referids="$referids">
            <mm:param name="question"><mm:field name="number"/></mm:param>
            <mm:param name="testnumber"><mm:write referid="learnobject"/></mm:param>
            <mm:param name="madetest"><mm:write referid="madetest"/></mm:param>
         </mm:treeinclude>

         <%-- Make field for rating this question --%>
         <input type="hidden" name="shown<mm:field name="number"/>" value="<mm:field name="number"/>"/>
<%
         if ( questionsShowed.equals(" ") ) {
            questionsShowed = questionNo;
         } else {
            questionsShowed = questionsShowed + "," + questionNo;
         }
         qCounter++;
         if (iQPerPageAmount == qCounter) {
            flag = 0;
         }
%>
      </mm:node>
<%
      // if end of list, check qCounter: if we show nothing, try one more time from begin of list
      i++;
      if (i == listTestPath.size()) {
         if (qCounter == 0 && flag == 1) {
            i = 0;
            flag = 2; // second pass
         } else {
            flag = 0;
         }
      }
   }

%>
   <mm:list nodes="$learnobject" path="tests,questions,givenanswers,madetests" fields="questions.number,madetests.number" 
          distinct="true" constraints="madetests.number = '$madetest'">
      <mm:size>
         <mm:compare referid2="questionamount">
            <mm:remove referid="testdone"/>
            <mm:import id="testdone">true</mm:import>
         </mm:compare>
     </mm:size>
  </mm:list>
   <br/>
   <br/>

   <%-- Arguments for rating --%>
   <input type="hidden" name="learnobject" value="<mm:write referid="learnobject"/>"/>
   <input type="hidden" name="thismadetest" value="<mm:write referid="madetest"/>"/>
   <input type="hidden" name="<mm:nodeinfo type="type"/>" value="<mm:field name="number"/>"/>
   <input type="hidden" name="questionsshowed" value="<%= questionsShowed %>"/>
   <input type="hidden" name="testpath" value="<mm:write referid="testpath"/>"/>
   <input type="hidden" name="questionamount" value="<mm:write referid="questionamount"/>"/>
   <input type="hidden" name="questionperpageamount" value="<mm:write referid="questionperpageamount"/>"/>
   <input type="hidden" name="command" value="next"/>

   <%-- Check if there are any questions --%>
   <% if (listTestPath.size() < 1) { %>
         <di:translate key="education.testwithoutquestions" />
         <p/>
   <% } %>

   <%-- Determine if all questions are showed --%>
   <mm:isgreaterthan referid="questionamount" referid2="questionperpageamount" inverse="true">
      <input type="submit" value="<di:translate key="education.buttontextdone" />" class="formbutton" onClick="questionform.command.value='done';questionform.submit()"/>
   </mm:isgreaterthan>
   <mm:isgreaterthan referid="questionamount" referid2="questionperpageamount">
      <% if ( currentpos > 0 ) { %>
         <input type="button" value="<di:translate key="education.buttontextprev" />" class="formbutton" onClick="questionform.command.value='back';questionform.submit()"/>
      <% } %>
      <% if ( currentpos < listTestPath.size()-1 ) { %>
         <input type="submit" value="<di:translate key="education.buttontextnext" />" class="formbutton"/>
      <% } else { %>
         <mm:import id="testdone" reset="true">true</mm:import>
      <% } %>
      <mm:present referid="testdone">
         <input type="submit" value="<di:translate key="education.buttontextdone" />" class="formbutton" onClick="questionform.command.value='done';questionform.submit()"/>
      </mm:present>
   </mm:isgreaterthan>
</mm:node>
</form>

</mm:node>
</div>
</body>
</html>
</mm:cloud>
</mm:content>
