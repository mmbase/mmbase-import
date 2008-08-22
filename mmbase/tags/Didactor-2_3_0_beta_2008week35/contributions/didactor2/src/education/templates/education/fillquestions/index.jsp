<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0" >
  <mm:content postprocessor="none">
    <mm:cloud method="delegate">

      <mm:import externid="question" required="true"/>
      <mm:import externid="madetest">-1</mm:import>

      <mm:node number="$question">
        <mm:relatedcontainer path="givenanswers,madetests">
          <mm:constraint field="madetests.number" value="$madetest"/>
          <mm:related>
            <mm:node element="givenanswers">
              <mm:field name="text" id="answer" write="false"/>
            </mm:node>
          </mm:related>
        </mm:relatedcontainer>


        <mm:import id="temp"><mm:field name="text" escape="none"/></mm:import>

        <di:title />
        <di:flash checkField="flashOrText" layoutField="layout" />


        <mm:field name="flashOrText">
          <!-- who came up with this ??
               This is much to explicit. Repulsive.
          -->
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

                    <mm:image mode="src" width="200" border="0" /><br />
                    <mm:field name="description" escape="none"/>
                  </mm:relatednodes>

                </div>
              </mm:compare>
            </mm:field>

            <mm:field name="impos">
              <mm:compare value="0">

                <div class="images">

                  <mm:relatednodes type="images">
                    <di:title />
                    <mm:image mode="src" width="200" border="0" /><br />
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
                    <di:title />

                    <table>
                      <tr>
                        <td><mm:write referid="temp"/></td>
                        <td>
                          <mm:image mode="src" width="200" border="0" align="right" />
                          <br/>
                        </td>
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
          <input  type="text" size="15"  dtmaxlength="15" name="${question}" value="${answer}" />
          <mm:field name="textSecond" escape="none"/>
        </p>

      </mm:node>

    </mm:cloud>
  </mm:content>
</jsp:root>
