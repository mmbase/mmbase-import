<jsp:root version="2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0"
          >
  <mm:content postprocessor="none">
    <mm:cloud rank="didactor user" >

      <mm:import externid="learnobject" required="true"/>

      <mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids,learnobject">
        <mm:param name="learnobjecttype">pages</mm:param>
      </mm:treeinclude>


      <mm:import externid="fb_madetest"/>
      <mm:present referid="fb_madetest">
        <mm:node number="$fb_madetest">
          <mm:relatednodes type="tests">
            <mm:treefile page="/education/tests/feedback.jsp" objectlist="$includePath" referids="$referids,_node@number,fb_madetest@madetest" write="false">
              <a href="${_}"><di:translate key="education.backtotestresults" /></a>
            </mm:treefile>
            <br /><!-- brs are evil -->
          </mm:relatednodes>
        </mm:node>
      </mm:present>

      <mm:node number="$learnobject">

        <mm:import externid="suppresstitle"/>

        <mm:notpresent referid="suppresstitle">
          <di:title field="name" />
        </mm:notpresent>


        <mm:import id="text">
          <mm:hasfield name="text">
            <mm:field name="text" escape="none"/> <!-- are we sure these fields are html fields always? -->
          </mm:hasfield>
          <mm:hasfield name="intro">
            <mm:field name="intro" escape="none"/>
          </mm:hasfield>
        </mm:import>

        <table width="100%" border="0" class="Font layout${_node.layout}">
          <!-- tables for layout are evil
               Who came up with this?
          -->
          <c:choose>
            <c:when test="${_node.layout eq 0}">
              <tr>
                <td width="50%">
                  <mm:write referid="text" escape="toxml" />
                </td>
              </tr>
              <tr><td><jsp:directive.include file="images.jsp" /></td></tr>
            </c:when>
            <c:when test="${_node.layout eq 1}">
              <tr><td  width="50%"><jsp:directive.include file="images.jsp" /></td></tr>
              <tr><td>
                <mm:write referid="text" escape="toxml" />
              </td></tr>
            </c:when>
            <c:when test="${_node.layout eq 2}">
              <tr>
                <td>
                  <mm:write referid="text" escape="toxml" />
                </td>
                <td><jsp:directive.include file="images.jsp" /></td>
              </tr>
            </c:when>
            <c:otherwise>
              <tr>
                <td><jsp:directive.include file="images.jsp" /></td>
                <td>
                  <mm:write referid="text" escape="toxml" />
                </td>
              </tr>
            </c:otherwise>
          </c:choose>
        </table>


        <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos">
          <h3><mm:field name="title"/></h3>
          <p>
            <i><mm:field name="description" escape="inline"/></i><!-- how about using CSS to style intro's? -->
            <br /> <!-- as mentioned, br's are evil -->

            <!--
                TODO
                Why not use icons dependent on the type? This is impossible with this.
                'Downloads' lack i18n.
            -->
            <mm:attachment>
              <a href="${_}">
                <img src="${mm:treelink('/education/gfx/attachment.gif', includePath)}" border="0"
                     title="Download ${_node.title}"
                     alt="Download ${_node.title}" />
              </a>
            </mm:attachment>
          </p>
        </mm:relatednodes>

        <div class="audiotapes">

          <!-- a certain browser does not yet support xhtml, so we can't use it (in content type), so we can't send empty divs -->
          <jsp:text> </jsp:text>

          <mm:relatednodes type="audiotapes" role="posrel" orderby="posrel.pos">
            <h3><mm:field name="title"/></h3>
            <p>
              <i><mm:field name="subtitle"/></i>
            </p>
            <i><mm:field name="intro" escape="p"/></i>
            <p>
              <mm:field name="body" escape="inline"/>
              <br /> <!-- silly! -->

              <!-- TODO
                   'Beluister' is dutch. It means: 'Listen to'
              -->
              <a href="${_node.url}">
                <img src="${mm:treelink('/education/gfx/audio.gif', includePath)}"
                     border="0"
                     title="Beluister ${_node.title}"
                     alt="Beluister ${_node.title}" />
              </a>
            </p>
          </mm:relatednodes>
        </div>

        <div class="videotapes">
          <jsp:text> </jsp:text>
          <mm:relatednodes type="videotapes" role="posrel" orderby="posrel.pos">
            <p>
              <h3><mm:field name="title"/></h3>
              <i><mm:field name="subtitle"/></i>
            </p>
            <i><mm:field name="intro" escape="p"/></i><!-- how about using CSS to style intro's? -->
            <p>
              <mm:field name="body" escape="inline"/>
              <br /> <!-- CSS, why would we use it, if we can litter the html with a few zillion of br's. -->

              <!-- TODO
                   'Bekijk' is dutch. It means: 'Watch'
              -->
              <a href="${_node.url}">
                <img src="${mm:treelink('/education/gfx/video.gif', includePath)}"
                     border="0"
                     title="Bekijk ${_node.title}"
                     alt="Bekijk ${_node.title}" />
              </a>
            </p>
          </mm:relatednodes>
        </div>

        <div class="urls">
          <jsp:text> </jsp:text>
          <mm:relatednodes type="urls" role="posrel" orderby="posrel.pos">
            <mm:field name="showtitle">
              <mm:compare value="1">
                <h3><mm:field name="name"/></h3>
              </mm:compare>
            </mm:field>
            <p>
              <i><mm:field name="description" escape="inline"/></i>
              <br /> <!-- it never stops -->

              <a href="${_node.url}"
                 target="_blank"><mm:field name="url"/></a>
            </p>
          </mm:relatednodes>
        </div>
      </mm:node>
    </mm:cloud>
  </mm:content>
</jsp:root>



