<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0" >
  <mm:content>
    <mm:cloud rank="didactor user">
      <mm:import externid="question" required="true"/>
      <mm:import externid="madetest">-1</mm:import>

      <mm:node number="$question">


        <mm:isnotempty referid="madetest">
          <mm:relatedcontainer path="givenanswers,madetests">
            <mm:constraint field="madetests.number" value="$madetest"/>
            <mm:related>
              <mm:node element="givenanswers">
                <mm:field name="text" id="answer" write="false"/>
              </mm:node>
            </mm:related>
          </mm:relatedcontainer>
        </mm:isnotempty>

        <mm:field name="showtitle">
          <mm:compare value="1">
            <h2><mm:field name="title"/></h2>
          </mm:compare>
        </mm:field>

        <p/> <!-- wtf -->
        <mm:field name="text" escape="tagstripper(XSS)"/>
        <p/><!-- wtf -->

        <mm:import id="layout"><mm:field name="layout"/></mm:import>

        <!-- Generate large input field -->
        <mm:compare referid="layout" value="0">
          <textarea name="${question}"
                    class="question mm_validate mm_dt_requiredfield"
                    cols="80" rows="5">
            <mm:present referid="answer"><mm:write referid="answer" escape="text/plain"/></mm:present>
            <jsp:text> </jsp:text>
          </textarea>
          <br/> <!-- wtf -->
        </mm:compare>

        <!-- Generate small input field -->
        <mm:compare referid="layout" value="1">
          <input type="text" size="100"
                 class="question mm_validate mm_dt_requiredfield"
                 name="${question}" value="${answer}"/>
          <br/><!-- wtf -->
        </mm:compare>


      </mm:node>
    </mm:cloud>
  </mm:content>
</jsp:root>
