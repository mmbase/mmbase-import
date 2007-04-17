<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- mmbob is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <%@include file="/mmbob/check.jsp" %>
  <mm:import id="template" reset="true"><mm:treefile page="/mmbob/index.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
  <mm:compare referid="type" value="div">
    <mm:present referid="classforum">
      <mm:node number="$classforum" notfound="skip">
        <div class="menuSeperator"> </div>
        <div class="menuItem" id="menuMMBob">
          <a href='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="classforum"/>' class="menubar"><di:translate key="mmbob.groupforum" /></a>
        </div>
      </mm:node>
    </mm:present>
    <mm:present referid="educationforum">
      <mm:node number="$educationforum" notfound="skip">
        <div class="menuSeperator"> </div>
        <div class="menuItem" id="menuMMBob">
          <a href='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="educationforum"/>' class="menubar"><di:translate key="mmbob.educationforum" /></a>
        </div>
      </mm:node>
    </mm:present>
  </mm:compare>
  
  <mm:compare referid="type" value="option">
    <mm:present referid="class">
      <mm:node number="$class" notfound="skip">
        <option value='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="classforum"/>'>
          <di:translate key="mmbob.groupforum" />
        </option>
      </mm:node>
    </mm:present>
    <mm:present referid="education">
      <mm:node number="$education" notfound="skip">
        <option value='<mm:write referid="template" escape="text/plain" />&forumid=<mm:write referid="educationforum"/>'>
          <di:translate key="mmbob.educationforum" />
        </option>
      </mm:node>
    </mm:present>
  </mm:compare>
  </mm:cloud>
</mm:compare>
