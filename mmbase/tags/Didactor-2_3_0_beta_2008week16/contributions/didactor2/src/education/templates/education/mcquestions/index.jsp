<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0" >
  <mm:content postprocessor="none">
    <mm:cloud method="delegate">

      <mm:import externid="question" required="true"/>
      <mm:import externid="madetest">-1</mm:import>

      <!--
      Multiple choice questions
      type
      0: only 1 answer can be selected
      1: multiple answers can be selected

      layout	0: all answers beneath eachother / random order
      1: all answers next to eachother / random order
      2: pulldown menu (only for type 0) / random order
      3: all answers beneath eachother / fixed order
      4: all answers next to eachtother / fixed order
      5: pulldown menu (only for type 0) / fixed order
      -->

      <mm:node number="$question">
        <mm:field name="showtitle">
          <mm:compare value="1">
            <h2><mm:field name="title"/></h2>
          </mm:compare>
        </mm:field>

        <p /><!-- WTF, why don't we produce, say, for example, HTML!  This is pure horror -->

        <mm:field name="text" escape="tagstripper(xss)"/>
        <p/>

        <mm:import id="questiontype"><mm:field name="type"/></mm:import>
        <mm:import id="questionlayout"><mm:field name="layout"/></mm:import>

        <!-- Show answers in random order -->
        <mm:compare referid="questionlayout" valueset="0,1,2">
          <mm:relatednodes type="mcanswers" comparator="SHUFFLE" id="answerlist"/>
        </mm:compare>

        <!-- Show answers in fixed order -->
        <mm:compare referid="questionlayout" valueset="3,4,5">
          <mm:relatednodes type="mcanswers" role="posrel" orderby="posrel.pos" id="answerlist"/>
        </mm:compare>


        <!-- Generate layout for radiogroup / pulldown menu (only 1 correct answer to be chosen) -->
        <mm:compare referid="questiontype" value="0">

          <!-- Layout for radiogroup -->
          <mm:compare referid="questionlayout" valueset="0,1,3,4">
            <mm:listnodes referid="answerlist">
              <div class="images">
                <mm:relatednodes type="images">
                  <mm:field name="showtitle">
                    <mm:compare value="1">
                      <h3><mm:field name="title"/></h3>
                    </mm:compare>
                  </mm:field>
                  <mm:image mode="img" border="0" template="s(200)" /><br />
                  <mm:field name="description" escape="tagstripper(xss)"/>
                </mm:relatednodes>
              </div>
              <mm:import externid="${question}" id="answer" />
              <mm:radio  name="${question}" value="${_node}" compare="${answer}" />
              <mm:field name="text" />

              <!-- Each answer on a new line -->
              <mm:compare referid="questionlayout" valueset="0,3">
                <br/>
              </mm:compare>
            </mm:listnodes>
          </mm:compare>

          <!-- Layout for pulldown menu -->
          <mm:compare referid="questionlayout" valueset="2,5">
            <select name="${question}">
              <mm:import externid="${question}" id="answer" />
              <mm:listnodes referid="answerlist">
                <mm:option compare="question" value="${_node}"><mm:field name="text"/></mm:option>
              </mm:listnodes>
            </select>
          </mm:compare>
        </mm:compare>

        <!-- Generate layout for checkboxes (multiple correct answers to be chosen) -->
        <mm:compare referid="questiontype" value="1">
          <mm:listnodes referid="answerlist">
            <mm:import externid="${question}_${_node}" id="answer" />
            <mm:radio type="checkbox" name="${question}_${_node}"  value="${_node}" compare="${answer}" />
            <mm:field name="text"/>
            <!-- Each answer on a new line -->
            <mm:compare referid="questionlayout" valueset="0,3">
              <br/>
            </mm:compare>
          </mm:listnodes>
        </mm:compare>

      </mm:node>
    </mm:cloud>
  </mm:content>
</jsp:root>
