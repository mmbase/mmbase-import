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

    <di:leaf
        branchPath="${branchPath} "
        click="portal_child_${_node}"
        >
      <mm:link referid="wizardjsp" referids="_node@objectnumber">
        <mm:param name="wizard">config/portalpages/leafportalpagesnodes</mm:param>
        <td><a href="${_}" title="edit" target="text"><mm:field name="name"/></a></td>
      </mm:link>
    </di:leaf>
    <div id="portal_child_${_node}" style="display:none">
      <di:leaf
          branchPath="${branchPath}"
          icon="new_education"
          >
        <mm:link referid="wizardjsp" referids="thischild@origin">
          <mm:param name="wizard">config/portalpages/newsimplecontents</mm:param>
          <mm:param name="objectnumber">new</mm:param>
          <td>
            <a href="${_}" title="nieuwe content" target="text">nieuwe content</a>
          </td>
        </mm:link>
      </di:leaf>
      <mm:relatednodes role="related" path="simplecontents" varStatus="status">
        <di:leaf
            branchPath="${branchPath} ${status.last ? '.' : ' '}"
            icon="learnblock">
          <mm:link referid="wizardjsp" referids="_node@objectnumber">
            <mm:param name="wizard">config/portalpages/simplecontents</mm:param>
            <td>
              <a href="${_}" target="text"><mm:field name="title"/></a>
            </td>
          </mm:link>
        </di:leaf>
      </mm:relatednodes>
      <mm:relatednodes role="posrel"
                       type="simplexmlcontents" varStatus="status">
        <di:leaf
            branchPath="${branchPath} ${status.last ? '.' : ' '}"
            icon="kupu_icon">
          <mm:link page="/mmbase/kupu/mmbase" referids="_node@objectnumber,referrer,kupu_back">
            <mm:param name="templates">/editwizards/data</mm:param>
            <mm:param name="wizard">config/portalpages/simplexmlcontents</mm:param>
            <mm:param name="link_nodetypes">${di:setting('richtext', 'link_nodetypes')}</mm:param>
            <mm:param name="language">${locale.language}</mm:param>
            <mm:param name="style"><mm:treefile page="/kupu" absolute="context" objectlist="$includePath" /></mm:param>
            <a href="${_}" title="edit" target="text">
              <mm:field name="title"/>
            </a>
          </mm:link>
        </di:leaf>
      </mm:relatednodes>
    </div>
  </mm:relatednodes>

  <!-- WTF is the sort order here
       This is horrible.
       Only let for legacy reasons.
  -->
  <mm:relatednodes role="related"
                   type="simplecontents" varStatus="status">
    <di:leaf
        branchPath="${branchPath}${status.last ? '.' : ' '}"
        icon="learnblock">
      <mm:link referid="wizardjsp" referids="_node@objectnumber">
        <mm:param name="wizard">config/portalpages/simplecontents</mm:param>
        <a href="${_}" title="edit" target="text"><mm:field name="title"/></a>
      </mm:link>
    </di:leaf>
  </mm:relatednodes>

  <mm:relatednodes role="posrel"
                   type="simplexmlcontents" varStatus="status">
    <di:leaf
        branchPath="${branchPath}${status.last ? '.' : ' '}"
        icon="kupu_icon">
      <mm:link page="/mmbase/kupu/mmbase" referids="_node@objectnumber,referrer,kupu_back">
        <mm:param name="templates">/editwizards/data</mm:param>
        <mm:param name="wizard">config/portalpages/simplexmlcontents</mm:param>
        <mm:param name="link_nodetypes">${di:setting('richtext', 'link_nodetypes')}</mm:param>
        <mm:param name="language">${locale.language}</mm:param>
        <mm:param name="style"><mm:treefile page="/kupu" absolute="context" objectlist="$includePath" /></mm:param>
        <mm:hasrelationmanager sourcemanager="$_node" destinationmanager="images" role="background">
          <mm:param name="style"><mm:treefile page="/kupu" absolute="context" objectlist="$includePath" /></mm:param>
          <mm:relatednodescontainer id="current" type="images" role="background">
            <mm:maxnumber value="1" />
          </mm:relatednodescontainer>
          <mm:listnodescontainer id="repository" type="images" />
          <mm:write session="${prefix}current"    referid="current" />
          <mm:write session="${prefix}repository" referid="repository" />
          <mm:param name="tools">searchrelate</mm:param>
          <mm:param name="currentTitle"><di:translate key="portalpages.backgroundimage" /></mm:param>
        </mm:hasrelationmanager>

        <a href="${_}" title="edit" target="text"><mm:field name="title"/></a>
      </mm:link>
    </di:leaf>
  </mm:relatednodes>
</jsp:root>
