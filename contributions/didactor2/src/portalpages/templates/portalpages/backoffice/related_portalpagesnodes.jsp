<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:c="http://java.sun.com/jsp/jstl/core"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:import id="kupu_back"><mm:url page="${referrer}" /></mm:import>
  <mm:import externid="branchPath" />
  <mm:relatednodes role="childppnn"
                   orderby="order_number"
                   type="portalpagesnodes">

    <li>
      <mm:link referid="wizardjsp" referids="_node@objectnumber">
        <mm:param name="wizard">config/portalpages/leafportalpagesnodes</mm:param>
        <a href="${_}" title="edit" target="text"><mm:field name="name"/></a>
      </mm:link>
      <ul>
        <li>
          <di:icon name="new_education" />
          <mm:link referid="wizardjsp" referids="thischild@origin">
            <mm:param name="wizard">config/portalpages/newsimplecontents</mm:param>
            <mm:param name="objectnumber">new</mm:param>
            <a href="${_}" title="nieuwe content" target="text">nieuwe content</a>
          </mm:link>
        </li>
        <mm:relatednodes role="related" path="simplecontents" varStatus="status">
          <li>
            <di:icon name="learnblock" />
            <mm:link referid="wizardjsp" referids="_node@objectnumber">
              <mm:param name="wizard">config/portalpages/simplecontents</mm:param>
              <a href="${_}" target="text"><mm:field name="title"/></a>
            </mm:link>
          </li>
        </mm:relatednodes>
        <mm:relatednodes role="posrel"
                         type="simplexmlcontents" varStatus="status">
          <li>
            <di:icon name="kupu" />
            <di:kupulink
                node="${_node}"
                referrer="${referrer}"
                wizard="config/portalpages/simplexmlcontents">
              <a href="${_}" title="edit" target="text">
                <mm:field name="title"/>
              </a>
            </di:kupulink>
          </li>

        </mm:relatednodes>
      </ul>
    </li>
  </mm:relatednodes>

  <!-- WTF is the sort order here
       This is horrible.
       Only let for legacy reasons.
  -->
  <mm:relatednodes role="related"
                   type="simplecontents" varStatus="status">
    <li>
      <di:icon name="learnblock" />
      <mm:link referid="wizardjsp" referids="_node@objectnumber">
        <mm:param name="wizard">config/portalpages/simplecontents</mm:param>
        <a href="${_}" title="edit" target="text"><mm:field name="title"/></a>
      </mm:link>
    </li>
  </mm:relatednodes>

  <mm:relatednodes role="posrel"
                   type="simplexmlcontents" varStatus="status">
    <li>
      <di:icon name="kupu" />
      <di:kupulink
          referrer="${referrer}"
          wizard="config/portalpages/simplexmlcontents"
          node="${_node}">
          <a href="${_}" title="edit" target="text"><mm:field name="title"/></a>
      </di:kupulink>
    </li>
  </mm:relatednodes>
</jsp:root>
