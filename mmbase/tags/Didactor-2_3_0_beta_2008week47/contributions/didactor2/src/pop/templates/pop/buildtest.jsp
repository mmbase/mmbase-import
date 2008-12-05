<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>



<mm:content postprocessor="reducespace" expires="0">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="learnobject" required="true"/>

<mm:import id="madetest" externid="madetest"/>



<%-- Set default values --%>

<mm:import externid="questionsshowed" jspvar="questionsShowed"> </mm:import>

<mm:import externid="pagecounter" jspvar="pageCounter" vartype="Integer">0</mm:import>



<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>

<mm:import externid="student" reset="true"><mm:write referid="user"/></mm:import>

<mm:import externid="currentfolder">-1</mm:import>


<%-- remove old results --%>
<mm:present referid="madetest" inverse="true">


<%-- find student's copybook --%>
<mm:node number="$student">
   <%@include file="find_copybook.jsp"%>
</mm:node>



<mm:node referid="learnobject">

  <mm:relatednodescontainer path="madetests,copybooks" element="madetests">

    <mm:constraint field="copybooks.number" referid="copybookNo"/>



    <mm:relatednodes>

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

    </mm:relatednodes>



  </mm:relatednodescontainer>

</mm:node>

</mm:present>





<html>

<head>

   <title>Content</title>

   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath"/>"/>

</head>

<body>





<%-- Take care: form name is used in JavaScript of the specific question jsp pages! --%>

<form name="questionform" action="<mm:treefile page="/pop/rate.jsp" objectlist="$includePath" referids="$popreferids,currentfolder"/>" method="POST">



<mm:node number="$learnobject">

  <mm:field name="showtitle">
    <mm:compare value="1">
      <h1><mm:field name="name"/></h1>
    </mm:compare>
  </mm:field>



  <%-- Count the different pages --%>

  <%

    int pageCount = pageCounter.intValue();

    pageCount++;

  %>

  <mm:remove referid="pagecounter"/>

  <mm:import id="pagecounter" jspvar="pageCounter" vartype="Integer"><%=pageCount%></mm:import>

  <p/>

  <mm:field name="text" escape="none"/>



  <mm:import id="questiontype"><mm:field name="questionamount"/></mm:import>

  <%-- Questiontype < 1 means undefined. Show all questions in the given order. --%>

  <mm:islessthan referid="questiontype" value="1">

    <mm:relatednodes type="questions" id="my_questions" role="posrel" orderby="posrel.pos,title">

      <mm:import id="questionamount"><mm:size/></mm:import>

    </mm:relatednodes>

  </mm:islessthan>

  <%-- Questionamount >= 1. Show the given amount of questions at random. --%>

  <mm:isgreaterthan referid="questiontype" value="0">

    <mm:relatednodes type="questions" id="my_questions" max="$questiontype" comparator="SHUFFLE">

      <mm:import id="questionamount"><mm:size/></mm:import>

    </mm:relatednodes>

  </mm:isgreaterthan>

  <%-- Check if questionamount is present --%>

  <mm:notpresent referid="questionamount">

    <mm:import id="questionamount">0</mm:import>

  </mm:notpresent>





  <mm:import id="questionperpageamount"><mm:field name="questionsperpage"/></mm:import>

  <mm:write referid="questionperpageamount">

     <mm:islessthan value="1">

      <mm:remove referid="questionperpageamount"/>

      <mm:import id="questionperpageamount"><mm:write referid="questionamount"/></mm:import>

     </mm:islessthan>

  </mm:write>



  <mm:write referid="questionperpageamount">

     <mm:islessthan value="1">

      <mm:remove referid="questionperpageamount"/>

      <mm:import id="questionperpageamount">1</mm:import>

     </mm:islessthan>

  </mm:write>









  <%-- Determine pages to show --%>

  <mm:import jspvar="questionAmount" vartype="Double"><mm:write referid="questionamount"/></mm:import>

  <mm:import jspvar="questionPerPageAmount" vartype="Double"><mm:write referid="questionperpageamount"/></mm:import>



  <%

     double  amount = questionAmount.doubleValue() / questionPerPageAmount.doubleValue();

     amount = Math.ceil( amount );

     Long pagesAmount = new Long( (long) amount );

  %>

  <mm:import id="pagesamount"><%=pagesAmount%></mm:import>



  <%

     final String SEPARATOR = "_";

     int showedCounter = 0;

  %>



  <%-- Show questions in right order --%>

  <mm:relatednodes referid="my_questions">



      <mm:first>

      <mm:import id="questionsintest"><mm:size/></mm:import>

      </mm:first>



      <%-- Show only questions that are not answerd before --%>

      <mm:import id="questionno" jspvar="questionNo" vartype="String"><%=SEPARATOR%><mm:field name="number"/><%=SEPARATOR%></mm:import>



      <%-- Make a list object from the comma seperated string with answered questions --%>

      <mm:import id="list" jspvar="list" vartype="List"><mm:write referid="questionsshowed"/></mm:import>

      <%

         int pos = list.indexOf( questionNo );

         if ( pos == -1 ) {

           if ( questionPerPageAmount.intValue() > showedCounter ) {

             showedCounter++;

      %>

             <%-- Show one question --%>

             <mm:import id="page">/education/<mm:nodeinfo type="type"/>/index.jsp</mm:import>

             <mm:treeinclude page="$page" objectlist="$includePath" referids="$popreferids">

               <mm:param name="question"><mm:field name="number"/></mm:param>

	       <mm:param name="testnumber"><mm:write referid="learnobject"/></mm:param>

             </mm:treeinclude>



             <%-- Make field for rating this question --%>

             <input type="hidden" name="shown<mm:field name="number"/>" value="<mm:field name="number"/>"/>



             <%-- See default value of questionsShowed --%>

      <%

             if ( questionsShowed.equals(" ") ) {

               questionsShowed = questionNo;

             } else {

               questionsShowed = questionsShowed + "," + questionNo;

             }

           }

         }

      %>



      <mm:remove referid="questionsshowed"/>

      <mm:import id="questionsshowed"><%=questionsShowed%></mm:import>



  </mm:relatednodes>

  <br/>

  <br/>



  <%-- Arguments for rating --%>

  <input type="hidden" name="learnobject" value="<mm:write referid="learnobject"/>"/>

  <input type="hidden" name="thismadetest" value="<mm:write referid="madetest"/>"/>

  <input type="hidden" name="<mm:nodeinfo type="type"/>" value="<mm:field name="number"/>"/>

  <input type="hidden" name="questionsshowed" value="<mm:write referid="questionsshowed"/>"/>

  <input type="hidden" name="pagecounter" value="<mm:write referid="pagecounter"/>"/>

  <input type="hidden" name="questionamount" value="<mm:write referid="questionamount"/>"/>



  <%-- Check if there are any questions --%>

  <mm:compare referid="pagesamount" value="0">

    <di:translate key="education.testwithoutquestions" />

    <p/>

  </mm:compare>



  <%-- Determine if all questions are showed --%>

  <% if ( pageCounter.intValue() == pagesAmount.intValue() || pagesAmount.intValue() == 0 ) { %>

       <input type="submit" value="<di:translate key="education.buttontextdone" />" class="formbutton"/>

  <% } else { %>

       <input type="submit" value="<di:translate key="education.buttontextnext" />" class="formbutton"/>

  <% } %>



</mm:node>

</form>



</body>

</html>

</mm:cloud>

</mm:content>

