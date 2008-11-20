<jsp:root
    xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0" >
  <mm:content>
    <mm:cloud rank="didactor user">

      <di:question
          question="${param.question}"
          madetest="${param.madetest}">

        <mm:present referid="answernode">
          <mm:node referid="answernode">
            <mm:field name="text" id="answer" write="false"/>
          </mm:node>
        </mm:present>

        <c:choose>
          <c:when test="${_node.layout eq 0}">
            <!-- Generate large input field -->
            <textarea name="${_node}"
                      class="question mm_validate mm_dt_requiredfield"
                      cols="80" rows="5">
              <mm:present referid="answer"><mm:write referid="answer" escape="text/plain"/></mm:present>
              <jsp:text></jsp:text>
            </textarea>
          </c:when>
          <c:otherwise>
            <!-- Generate small input field -->
            <input type="text" size="100"
                   class="question mm_validate mm_dt_requiredfield"
                   name="${_node}" value="${answer}"/>
          </c:otherwise>
        </c:choose>

      </di:question>
    </mm:cloud>
  </mm:content>
</jsp:root>
