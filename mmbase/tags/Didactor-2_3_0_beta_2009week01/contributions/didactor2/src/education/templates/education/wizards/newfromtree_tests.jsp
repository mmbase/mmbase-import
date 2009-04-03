<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />

  <mm:import externid="wizardjsp" from="request" />
  <mm:cloud method="delegate">
    <mm:node>

      <di:getsetting setting="new_questiontypes" component="education" vartype="list"
                     id="new_questiontypes" write="false" />

      <mm:stringlist referid="new_questiontypes" id="questiontype">
        <mm:hasnodemanager name="${_}">
          <di:leaf
              branchPath=".. "
              icon="new_education"
              >
            <mm:link referid="wizardjsp" referids="_node@origin">
              <mm:param name="wizard">config/question/${questiontype}-origin</mm:param>
              <mm:param name="objectnumber">new</mm:param>
              <mm:property nodemanager="${questiontype}" name="key:new:name" id="name" write="false"/>
              <mm:property nodemanager="${questiontype}" name="key:new:description" id="description" write="false" />
              <a href="${_}"
                 title="${di:translate(description)}" target="text">
                ${di:translate(name)}
              </a>
            </mm:link>
          </di:leaf>
        </mm:hasnodemanager>
      </mm:stringlist>
    </mm:node>
  </mm:cloud>
</jsp:root>
