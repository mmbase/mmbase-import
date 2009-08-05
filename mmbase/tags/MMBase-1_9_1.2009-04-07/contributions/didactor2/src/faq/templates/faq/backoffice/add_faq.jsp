<jsp:root
    version="2.0"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />

  <mm:import externid="listjsp"   from="request" required="true"/>
  <mm:import externid="wizardjsp" from="request" required="true"/>
  <mm:import from="request" externid="referrer" />


  <mm:listnodes id="e" type="educations" varStatus="status">
  	<mm:field name="name" write="false">
	  	<c:set var="education" value="${_}" />
	  </mm:field>
  	<mm:relatednodes type="faqnodes">
    <li>
      <span class="folder">
        <mm:link referid="wizardjsp" referids="_node@objectnumber,referrer">
          <mm:param name="wizard">config/faq/faqnodes</mm:param>
          <a href="${_}" target="text">Veelgestelde vragen voor ${education}</a>
        </mm:link>
      </span>
      <ul class="filetree">
        <mm:relatednodes role="posrel" type="faqnodes">
          <li>
            <span class="folder">
              <mm:field name="name" />
            </span>
            <mm:include page="leaf.jspx" />
          </li>
        </mm:relatednodes>
      </ul>
    </li>
    </mm:relatednodes>
  </mm:listnodes>
</jsp:root>
