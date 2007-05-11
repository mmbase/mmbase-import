<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace" expires="0">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="question" required="true"/>
<mm:import externid="madetest">-1</mm:import>

<%@include file="/shared/setImports.jsp" %>

<mm:node number="$question">
   <% String allGivenAnswers = ""; %>
   <mm:relatedcontainer path="givenanswers,madetests">
      <mm:constraint field="madetests.number" value="$madetest"/>
      <mm:related>
         <mm:node element="givenanswers">
            <mm:relatednodes type="answers">
               <mm:first>
                  <mm:field name="number" jspvar="answerNo" vartype="String">
                     <% allGivenAnswers = answerNo; %>
                  </mm:field>
               </mm:first>
               <mm:first inverse="true">
                  <mm:field name="number" jspvar="answerNo" vartype="String">
                     <% allGivenAnswers += "," + answerNo; %>
                  </mm:field>
               </mm:first>
            </mm:relatednodes>
         </mm:node>
      </mm:related>
   </mm:relatedcontainer>

    <mm:import id="temp" jspvar="temp"><mm:field name="text" escape="none"/></mm:import>
    <mm:import id="questiontype"><mm:field name="type"/></mm:import>
    <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>
    
    <%-- Show answers in random order --%>
        <mm:compare referid="questionlayout" valueset="2">
            <mm:import id="order">SHUFFLE</mm:import>
            <mm:relatednodes type="mcanswers" comparator="$order" id="answerlist"/>
        </mm:compare>	

    <%-- Show answers in fixed order --%>
        <mm:compare referid="questionlayout" valueset="5">
            <mm:relatednodes type="mcanswers" role="posrel" orderby="posrel.pos" id="answerlist"/>
        </mm:compare>
        
	 <mm:field name="showtitle">
	    <mm:compare value="1">
	      <h2><mm:field name="title"/></h2>
	    </mm:compare>
	  </mm:field>


	<p/>

<mm:field name="flashOrText">
<mm:compare value="1">
          <mm:relatednodes type="attachments">
              <object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,47,0" width="760" height="440" id="flashpage">
              <param name="movie" value="<mm:attachment/>">
              <param name="quality" value="high">
              <embed src="<mm:attachment/>" quality="high" pluginspage="http://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash" type="application/x-shockwave-flash" width="500" height="500" name="flashpage" swLiveConnect="true">
              </embed> 			  
          </object>
  </mm:relatednodes>
</mm:compare>
</mm:field>



<mm:field name="flashOrText">
<mm:compare value="0">

    <mm:field name="impos">
    <mm:compare value="1">	
        <mm:field name="text" escape="none"/>
	       <div class="images">

            <mm:relatednodes type="images">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3><mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>

              <img src="<mm:image />" width="200" border="0" /><br/>

              <mm:field name="description" escape="none"/> 
            </mm:relatednodes>

          </div>
    </mm:compare>
    </mm:field>

    <mm:field name="impos">
    <mm:compare value="0">	
 
	   <div class="images">

            <mm:relatednodes type="images">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3><mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>

              <img src="<mm:image />" width="200" border="0" /><br/>

              <mm:field name="description" escape="none"/> 
            </mm:relatednodes>

          </div>
          <mm:field name="text" escape="none"/>
    </mm:compare>
    </mm:field>



    <mm:field name="impos">
    <mm:compare value="2">	
	   <div class="images">
            <mm:relatednodes type="images">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h3><mm:field name="title"/></h3>
                </mm:compare>
              </mm:field>

            <table>
             <tr>
              
              
              <td><mm:write referid="temp"/></td>
              <td><img src="<mm:image />" width="200" border="0" align="right" /><br/></td>
            </tr>
            </table>
              <mm:field name="description" escape="none"/> 
              <br/>

            </mm:relatednodes>
          
      </div>
    </mm:compare>
    </mm:field>

</mm:compare>
</mm:field>

    <p>
    
    
	<mm:field name="textFirst" escape="none"/>
		

    <%-- Generate layout for pulldown menu (only 1 correct answer to be chosen) --%>

    <mm:compare referid="questiontype" value="0">
  
    <mm:compare referid="questionlayout" valueset="2,5">
        <mm:listnodes referid="answerlist"> 
            <mm:field name="number" id="answer_id" write="false"/>
    
            <mm:first><select name="<mm:write referid="question"/>"></mm:first>

            <option <mm:compare referid="answer_id" valueset="<%= allGivenAnswers %>">selected</mm:compare> ><mm:field name="text"/></option>

            <mm:last></select></mm:last>
    
        </mm:listnodes>
    </mm:compare> 
  
    </mm:compare>
    
    
    
    
    
    	

	<mm:field name="textSecond" escape="none"/>
   
	<p/>
  
  

</mm:node>

</mm:cloud>
</mm:content>
