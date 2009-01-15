<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />

  <mm:import externid="b" from="request" />
  <mm:import externid="branchPath" required="true"  />

  <mm:nodeinfo id="objecttype" type="type" write="false" />

  <mm:link referid="wizardjsp" referids="_node@objectnumber,_node@origin">
    <mm:param name="wizard">config/<mm:write referid="objecttype" />/<mm:write referid="objecttype" /></mm:param>
    <mm:param name="path">${sessionScope.eduname}${sessionScope.path}</mm:param>
    <a href='${_}' title="${di:translate('education.edit')} ${objecttype}" target="text">
      <mm:hasfield name="name"><mm:field name="name" /></mm:hasfield>
      <mm:hasfield name="title"><mm:field name="title" /></mm:hasfield>
    </a>
  </mm:link>

  <!--
      <mm:present referid="pdfurl">
      <mm:compare referid="objecttype" value="pages">
      <mm:link referid="pdfurl" referids="_node@number">
      <a href='${_}' target='text'><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
      </mm:link>
      </mm:compare>
      <mm:compare referid="objecttype" value="learnblocks">
      <mm:link referid="pdfurl" referids="_node@number">
      <a href='${_}' target='text'><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
      </mm:link>
      </mm:compare>
      </mm:present>
  -->

  <mm:field write="false" name="number" id="node_number" />
  <mm:node number="component.metadata" notfound="skip"> <!-- WTF -->
    <mm:link page="metaedit.jsp" referids="node_number@number">
      <a href='${_}' target='text'><img id='img_${_}' src='' border='0' title='' alt='' /></a>
    </mm:link>
  </mm:node>
  <mm:node number="component.versioning" notfound="skip"> <!-- WTF -->
    <mm:link page="versioning.jsp" referids="node_number@nodeid">
      <a href="${_}" target="text"><img src="gfx/versions.gif" border="0" /></a>
    </mm:link>
  </mm:node>

</jsp:root>
