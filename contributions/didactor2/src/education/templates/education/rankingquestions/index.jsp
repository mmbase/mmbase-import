<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">


<mm:import externid="question" required="true"/>
<mm:import externid="madetest">-1</mm:import>

<%@include file="/shared/setImports.jsp" %>

<mm:node number="$question">

  <mm:field name="showtitle">
    <mm:compare value="1">
      <h2><mm:field name="title"/></h2>
    </mm:compare>
  </mm:field>
  <p/>
  <mm:field name="text" escape="none"/>
  <p/>

  <!-- TODO what is the purpose of the layout field value?! -->	
  <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>


  <%-- Compute size --%>
  <mm:import id="nof_rankinganswers" jspvar="nof_rankinganswers" vartype="Integer"> <mm:relatednodescontainer type="rankinganswers"><mm:size/></mm:relatednodescontainer></mm:import>

  <!-- layout depends if any question has images -->
  <mm:import id="questionHasImage">-1</mm:import>
  <mm:relatednodes type="rankinganswers" comparator="SHUFFLE">
      <mm:relatednodes type="images">
        <mm:import id="questionHasImage" reset="true">1</mm:import>
      </mm:relatednodes>
  </mm:relatednodes> 

  <table border="0">
    <mm:relatednodes type="rankinganswers" comparator="SHUFFLE">
      <tr>
        <!-- change layout depending of images exist -->
        <!-- if has images, combo box will be below answer and picture -->
        <mm:compare referid="questionHasImage" value="1">
          <td>
            <table border="0">
              <tr>
                <td>
                  <mm:field id="answer" name="number" write="false"/>
                  <mm:remove referid="hasimage"/>
                  <div class="images">
                    <mm:relatednodes type="images">
                      <table border="0">
                        <tr>
                          <td>
                            <mm:field name="showtitle">
                              <mm:compare value="1">
                                <h3><mm:field name="title"/></h3>
                              </mm:compare>
                            </mm:field>
                          </td>
                        </tr>
                        <tr>
                          <td>
                            <img src="<mm:image />" width="200" border="0" />
                          </td>
                        <tr>
                        <tr>
                          <td>
                            <mm:field name="description" escape="none"/> 
                          </td>
                        <tr>
                      </table>
                      <mm:import id="hasimage"/>
                    </mm:relatednodes>
                  </div>
                  <mm:notpresent referid="hasimage">
                    <mm:field name="text" escape="none"/>
                  </mm:notpresent>
              
                  <mm:import id="givenrank" reset="true">-1</mm:import>
                  <mm:node number="$question">
                    <mm:relatedcontainer path="givenanswers,madetests">
                      <mm:constraint field="madetests.number" value="$madetest"/>
                      <mm:related>
                        <mm:node element="givenanswers">
                          <mm:relatedcontainer path="posrel,rankinganswers">
                            <mm:constraint field="rankinganswers.number" value="$answer"/>
                            <mm:related>
                              <mm:remove referid="givenrank"/>
                              <mm:field name="posrel.pos" id="givenrank" write="false"/>
                            </mm:related>
                          </mm:relatedcontainer>
                        </mm:node>
                      </mm:related>
                    </mm:relatedcontainer>
                  </mm:node>
              
                  <mm:field name="showtitle">
                    <mm:compare value="1">
                      <h2><mm:field name="title"/></h2>
                    </mm:compare>
                  </mm:field>
                </td>
              </tr>
              <tr>
                <td>
                  <select name="<mm:write referid="question"/>_<mm:write referid="answer"/>">
                  <% for (int i= 1;i <= nof_rankinganswers.intValue();i++) { %>
                    <option <mm:compare referid="givenrank" value="<%=""+i%>">selected</mm:compare>><%=i%></option>
                  <% } %>
                  </select>
                </td>
              </tr>
            </table>
          </td>
          <mm:remove referid="answer"/>
        </mm:compare>
        <mm:compare referid="questionHasImage" value="1" inverse="true">
          <td>          
            <mm:field id="answer" name="number" write="false"/>
          </td>
          <td>
            <mm:field name="text" escape="none"/>
          </td>
          <mm:import id="givenrank" reset="true">-1</mm:import>
          <mm:node number="$question">
            <mm:relatedcontainer path="givenanswers,madetests">
              <mm:constraint field="madetests.number" value="$madetest"/>
              <mm:related>
                <mm:node element="givenanswers">
                  <mm:relatedcontainer path="posrel,rankinganswers">
                    <mm:constraint field="rankinganswers.number" value="$answer"/>
                    <mm:related>
                      <mm:remove referid="givenrank"/>
                      <mm:field name="posrel.pos" id="givenrank" write="false"/>
                    </mm:related>
                  </mm:relatedcontainer>
                </mm:node>
              </mm:related>
            </mm:relatedcontainer>
          </mm:node>
          <td>
            <!-- I think this code can be removed? -->
            <mm:hasfield name="showtitle">
              <mm:field name="showtitle">
                <mm:compare value="1">
                  <h2><mm:field name="title"/></h2>
                </mm:compare>
              </mm:field>
            </mm:hasfield>
          </td>
          <td>
            <select name="<mm:write referid="question"/>_<mm:write referid="answer"/>">
            
            <% for (int i= 1;i <= nof_rankinganswers.intValue();i++) { %>
              <option <mm:compare referid="givenrank" value="<%=""+i%>">selected</mm:compare>><%=i%></option>
            <% } %>
            </select>
          </td>
          <mm:remove referid="answer"/>
        </mm:compare>
      </tr>
      <mm:present referid="hasimage">
        <tr height="7px"></tr>
      </mm:present>
      <mm:notpresent referid="hasimage">
        <tr height="3px"></tr>
      </mm:notpresent>
    </mm:relatednodes>
  </table>
</mm:node>

</mm:cloud>
</mm:content>
