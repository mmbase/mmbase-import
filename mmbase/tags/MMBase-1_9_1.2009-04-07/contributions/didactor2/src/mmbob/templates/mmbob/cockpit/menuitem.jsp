<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><mm:import externid="type" />
<mm:import externid="scope">none</mm:import>
<%-- mmbob is only valid in the 'education' scope --%>
<mm:compare referid="scope" value="education">
  <mm:cloud method="delegate">
  <jsp:directive.include file="/shared/setImports.jsp" />
  <jsp:directive.include file="/mmbob/check.jsp" />
  <mm:treefile id="template" page="/mmbob/index.jsp" objectlist="$includePath" referids="$referids" write="false" />
  <mm:compare referid="type" value="div">
    <mm:present referid="classforum">
      <mm:node number="$classforum" notfound="skip">
        <mm:import id="shownClassForum">${_node}</mm:import>
        <div class="menuSeparator"> </div>
        <div class="menuItem classForum" id="menuMMBob1">
          <mm:link referid="template" referids="classforum@forumid">
            <a href="${_}" class="menubar"><di:translate key="mmbob.groupforum" /></a>
          </mm:link>
        </div>
      </mm:node>
    </mm:present>
    <di:component name="mmbob">
      <di:settingvalue  name="students">
        <c:if test="${_ eq 'on' and empty shownClassForum}">
          <mm:present referid="educationforum">
            <mm:node number="$educationforum">
              <div class="menuSeparator"> </div>
              <div class="menuItem studentsForum" id="menuMMBob2">
                <mm:link referid="template" referids="educationforum@forumid">
                  <a href="${_}" class="menubar"><di:translate key="mmbob.educationforum" /></a>
                </mm:link>
              </div>
            </mm:node>
          </mm:present>
        </c:if>
      </di:settingvalue>
      <di:settingvalue name="coaches">
        <di:hasrole role="coach,teacher,systemadministrator">
          <c:if test="${_ eq 'on'}">
            <mm:present referid="educationforum_coaches">
              <mm:node number="$educationforum_coaches">
                <div class="menuSeparator"> </div>
                <div class="menuItem coachesForum" id="menuMMBob3">
                  <mm:link referid="template" referids="educationforum_coaches@forumid">
                    <a href="${_}" class="menubar"><di:translate key="mmbob.educationforum_coaches" /></a>
                  </mm:link>
                </div>
              </mm:node>
            </mm:present>
          </c:if>
        </di:hasrole>
      </di:settingvalue>
    </di:component>
  </mm:compare>

  <mm:compare referid="type" value="option">
    <mm:present referid="class">
      <mm:node number="$class" notfound="skip">
        <option value='<mm:url referid="template" referids="classforum@forumid"  />'>
          <di:translate key="mmbob.groupforum" />
        </option>
      </mm:node>
    </mm:present>
    <mm:present referid="education">
      <mm:node number="$education" notfound="skip">
        <option value='<mm:url referid="template" referids="educationforum@forumid" />'>
        <di:translate key="mmbob.educationforum" />
        </option>
      </mm:node>
    </mm:present>
  </mm:compare>
  </mm:cloud>
</mm:compare>
