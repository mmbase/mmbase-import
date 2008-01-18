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
        <mm:node number="$fb_madetest" notfound="skip">
          <mm:relatednodes type="tests">
            <mm:treefile page="/education/tests/feedback.jsp" objectlist="$includePath" referids="$referids,_node@number,fb_madetest@madetest" write="false">
              <a href="${_}"><di:translate key="education.backtotestresults" /></a>
            </mm:treefile>
            <br /><!-- brs are evil -->
          </mm:relatednodes>
        </mm:node>
      </mm:present>

      <mm:node number="$learnobject">

        <!-- a bit silly: -->
        <mm:hasfield name="layout">
          <mm:field id="layout" name="layout" write="false" />
        </mm:hasfield>
        <mm:hasfield name="layout" inverse="true">
          <mm:import id="layout">0</mm:import>
        </mm:hasfield>
        <mm:hasfield name="imagelayout">
          <mm:field id="imagelayout" name="imagelayout" write="false" />
        </mm:hasfield>
        <mm:hasfield name="imagelayout" inverse="true">
          <mm:import id="imagelayout"></mm:import>
        </mm:hasfield>

        <mm:import externid="suppresstitle"/>

        <mm:notpresent referid="suppresstitle">
          <mm:field name="showtitle">
            <mm:compare value="1">
              <h1><mm:field name="name"/></h1>
            </mm:compare>
          </mm:field>
        </mm:notpresent>


        <mm:import id="text">
          <mm:hasfield name="text">
            <mm:field name="text" escape="none"/> <!-- are we sure these fields are html fields always? -->
          </mm:hasfield>
          <mm:hasfield name="intro">
            <mm:field name="intro" escape="none"/>
          </mm:hasfield>
        </mm:import>

        <table width="100%" border="0" class="Font layout${layout}"> <!-- tables for layout are evil too -->
          <mm:compare referid="layout" value="0">
            <tr>
              <td width="50%">
                <mm:write referid="text" escape="toxml" />
              </td>
            </tr>
            <tr><td><jsp:directive.include file="images.jsp" /></td></tr>
          </mm:compare>

          <mm:compare referid="layout" value="1">
            <tr><td  width="50%"><jsp:directive.include file="images.jsp" /></td></tr>
            <tr><td>
              <mm:write referid="text" escape="toxml" />
            </td></tr>
          </mm:compare>

          <mm:compare referid="layout" value="2">
            <tr>
              <td>
                <mm:write referid="text" escape="toxml" />
              </td>
              <td><jsp:directive.include file="images.jsp" /></td>
            </tr>
          </mm:compare>

          <mm:compare referid="layout" value="3">
            <tr>
              <td><jsp:directive.include file="images.jsp" /></td>
              <td>
                <!--
                    This was horrible
                <jsp:directive.include file="/shared/cleanText.jsp" />
                -->
                <mm:write referid="text" escape="XSS" />
              </td>
            </tr>
          </mm:compare>

        </table>



        <mm:relatednodes type="attachments" role="posrel" orderby="posrel.pos">
          <h3><mm:field name="title"/></h3>
          <p>
            <i><mm:field name="description" escape="inline"/></i>
            <br /> <!-- as mentioned, br's are evil -->

            <!--
                TODO
                Why not use icons dependent on the type? This is impossible with this.
                'Download' lack i18n.
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
          <mm:relatednodes type="videotapes" role="posrel" orderby="posrel.pos">
            <p>
              <h3><mm:field name="title"/></h3>
              <i><mm:field name="subtitle"/></i>
            </p>
            <i><mm:field name="intro" escape="p"/></i>
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



