<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>

<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">

<mm:cloud method="delegate" jspvar="cloud">



<mm:import externid="testNo" required="true"/>

<mm:import externid="madetestNo" required="true"/>

<mm:import externid="userNo" required="true"/>

<mm:import externid="feedback" required="false"/>



<%@include file="/shared/setImports.jsp" %>

<%@include file="/education/tests/definitions.jsp" %>



<di:may component="education" action="isSelfOrTeacherOf" arguments="userNo">



 

<mm:list nodes="$madetestNo" path="madetests,related,givenanswers" orderby="related.number" directions="UP">

  <mm:import id="givenanswersNo" reset="true"><mm:field name="givenanswers.number"/></mm:import>

  <mm:node referid="givenanswersNo">

    <mm:relatednodes type="questions">

    <p>

      <mm:import id="questiontype"><mm:nodeinfo type="type"/></mm:import>



      <mm:field id="questiontext" name="text" write="false"/>

      <b>Vraag:</b> <mm:write referid="questiontext" escape="none"/>

      <br/>

      <mm:remove referid="questiontext"/>

      <mm:field id="questionNo" name="number" write="false"/>

    </mm:relatednodes>



    <mm:notpresent referid="questiontype">

      <b>questiontype not found </b>

      <br/>

      <mm:import id="questiontype">none</mm:import>

    </mm:notpresent>



    

    <mm:compare referid="questiontype" value="hotspotquestions">

        <mm:node referid="questionNo">

	<div style="position: relative">

        <mm:relatednodes type="images" max="1">

        <img src="<mm:image />" border="0" /><br/>

        </mm:relatednodes>

 

        <% int i = 1; %>

        <mm:relatednodes type="hotspotanswers" orderby="x1,y1">

            <a href="#" class="hotspot" style="position: absolute; left: <mm:field name="x1" />px; top: <mm:field name="y1" />px; width:<mm:field name="x2" />px; height: <mm:field name="y2" />px;"><%= i++ %></a>

        </mm:relatednodes>

        </div>

        </mm:node>



      <b>Gegeven antwoord:</b> <mm:field name="text" escape="none"/>

      <br/>

    </mm:compare>



   

    <mm:compare referid="questiontype" value="dropquestions">

      <b>Gegeven antwoord:</b> <mm:field name="text" escape="none"/>

      <br/>

    </mm:compare>



    <mm:compare referid="questiontype" value="openquestions">

      <b>Gegeven antwoord:</b> <mm:field name="text"/>

      <br/>

      <mm:relatednodescontainer type="feedback">

        <mm:relatednodes>

          <%-- Open Question Feedback (from teacher) --%>

          <b>Feedback:</b> <mm:field name="text" escape="none"/>

          <br/>
        <mm:field name="text" escape="none"/>
            <mm:field name="number" id="number" write="false">
            <mm:list nodes="$number" path="feedback,descriptionrel,learnobjects" searchdir="both">
                <mm:import id="description" reset="true"><mm:field name="descriptionrel.description"/></mm:import>
                <mm:isempty referid="description">
                  <mm:import id="description" reset="true"><mm:field name="learnobjects.name"/></mm:import>
                </mm:isempty>
                <mm:isempty referid="description">
                  <mm:import id="description" reset="true">Voor meer uitleg, zie deze pagina</mm:import>
                </mm:isempty>
                
                <a target="_blank" href="<mm:treefile 
                    page="/education/index.jsp" 
                    objectlist="$includePath" 
                    referids="$referids">
                    
                        <mm:param name="learnobject"><mm:field name="learnobjects.number" /></mm:param>
                    </mm:treefile>"><mm:write referid="description"/></a>
            </mm:list>
            </mm:field>


        </mm:relatednodes>

      </mm:relatednodescontainer>

    </mm:compare>



    <mm:compare referid="questiontype" value="mcquestions">

      <mm:node referid="questionNo">

        <mm:field name="type" id="type" write="false"/>

      </mm:node>



      <mm:compare referid="type" value="0">

        <mm:relatednodes type="answers">

          <b>Gegeven antwoord:</b>

          <mm:field name="text"/>

          <br/>

        </mm:relatednodes>

      </mm:compare>

      <mm:compare referid="type" value="1">

        <b>Gegeven antwoorden:</b>

        <mm:relatednodes type="answers">

          <mm:field name="text"/>

          <mm:last inverse="true">, </mm:last>

        </mm:relatednodes>

        <br/>

      </mm:compare>

    </mm:compare>



    <mm:compare referid="questiontype" value="couplingquestions">

        <b>Gegeven antwoord:</b>

      <% int counter;%>

      <mm:import id="size" jspvar="size" vartype="Integer">

        <mm:relatednodescontainer type="answers" role="leftanswer">

        <mm:size/></mm:relatednodescontainer>

      </mm:import>

        <% for (int i= 0;i < size.intValue();i++) { %>

        <%counter=0;%>

        <mm:relatednodes type="answers" role="leftanswer" orderby="posrel.pos">

	  <% counter++;if (counter-1 == i) { %>

            <mm:field id="leftanswer" name="number" write="false"/>

	  <% } %>

        </mm:relatednodes>



        <%counter=0;%>

        <mm:relatednodes type="answers" role="rightanswer" orderby="posrel.pos">

	  <% counter++;if (counter-1 == i) { %>

            <mm:field id="rightanswer" name="number" write="false"/>

	  <% } %>

        </mm:relatednodes>

        <mm:node referid="leftanswer"><mm:field name="text1"/></mm:node> gekoppeld aan

        <mm:node referid="rightanswer"><mm:field name="text2"/></mm:node><br/>

        <mm:remove referid="leftanswer"/>

        <mm:remove referid="rightanswer"/>

      <% } %>



      <mm:remove referid="size"/>

    </mm:compare>





    <mm:compare referid="questiontype" value="rankingquestions">

      <b>Gegeven antwoord:</b> 

      <mm:related path="posrel,rankinganswers">

        <mm:field id="rankinganswersnumber" name="rankinganswers.number" write="false"/>

          

          <mm:node referid="rankinganswersnumber">

            <mm:relatednodes type="images">
              <mm:first><div class="images"></mm:first>
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <mm:field name="title"/><br/>
                </mm:compare>
              </mm:field>
              <img src="<mm:image />" width="200" border="0" /><br/>
              <mm:field name="description" escape="none"/> 
              <mm:remove referid="hasimage"/>
              <mm:import id="hasimage"/>
              <mm:last></div></mm:last>
            </mm:relatednodes>

          </mm:node>



        <mm:notpresent referid="hasimage">

          <mm:field name="rankinganswers.text" escape="none"/>

        </mm:notpresent>

         Gegeven volgorde: <mm:field name="posrel.pos"/><br/>

         <mm:remove referid="rankinganswersnumber"/>

      </mm:related>

    </mm:compare>



    <mm:compare referid="questiontype" value="valuequestions">

    </mm:compare>



    <mm:compare referid="questiontype" value="reusequestions">

    </mm:compare>



    <mm:field id="questionscore" name="score" write="false"/>

    <mm:compare referid="questionscore" value="1">

      <b>Antwoord is goed</b>

    </mm:compare>

    <mm:compare referid="questionscore" value="0">

       <b>Antwoord is fout</b>

    </mm:compare>

    <mm:compare referid="questionscore" referid2="TESTSCORE_TBS">

       <b>Moet nog nagekeken worden.</b>

    </mm:compare>

    <br/>



    <%-- Feedback (from the question) --%>

    <mm:node number="$questionNo">

    <mm:relatednodescontainer type="feedback">

      <mm:constraint field="maximalscore" referid="questionscore" operator=">="/>

      <mm:constraint field="minimalscore" referid="questionscore" operator="<="/>

      <mm:relatednodes>

	<b>Feedback: <mm:field name="name"/></b><br/>

	<mm:relatednodes type="images">

	    <img src="<mm:image template="s(150x150)"/>" title="<mm:field name="title"/>" alt="<mm:field name="title"/>">

            <mm:last><br/></mm:last>

	</mm:relatednodes>
        <mm:field name="text" escape="none"/>
            <mm:field name="number" id="number" write="false">
            <mm:list nodes="$number" path="feedback,descriptionrel,learnobjects" searchdir="both">
                <mm:import id="description" reset="true"><mm:field name="descriptionrel.description"/></mm:import>
                <mm:isempty referid="description">
                  <mm:import id="description" reset="true"><mm:field name="learnobjects.name"/></mm:import>
                </mm:isempty>
                <mm:isempty referid="description">
                  <mm:import id="description" reset="true">Voor meer uitleg, zie deze pagina</mm:import>
                </mm:isempty>
                
                <a target="_blank" href="<mm:treefile 
                    page="/education/index.jsp" 
                    objectlist="$includePath" 
                    referids="$referids">
                    
                        <mm:param name="learnobject"><mm:field name="learnobjects.number" /></mm:param>
                    </mm:treefile>"><mm:write referid="description"/></a>
            </mm:list>
            </mm:field>

      </mm:relatednodes>

    </mm:relatednodescontainer>

    </mm:node>

    </p>



    <mm:remove referid="questionscore"/>

    <mm:remove referid="questiontype"/>

    <mm:remove referid="questionNo"/>

    </mm:node>

  </mm:list>



</di:may>



</mm:cloud>

</mm:content>

