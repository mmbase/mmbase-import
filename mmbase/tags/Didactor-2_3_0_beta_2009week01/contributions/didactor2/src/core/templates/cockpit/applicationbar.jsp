<jsp:root
    version="2.0"
    xmlns:c="http://java.sun.com/jsp/jstl/core"
    xmlns:fn="http://java.sun.com/jsp/jstl/functions"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
    xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <mm:cloud method="asis">
    <div class="applicationMenubar">
      <mm:import externid="showlogin"><mm:hasrank value="anonymous">yes</mm:hasrank></mm:import>
      <mm:compare referid="showlogin" value="yes">
        <div class="menuItemApplicationMenubar login">
          <mm:import externid="referrer">
            <mm:treefile
                absolute="context"
                page="/education/index.jsp" objectlist="$includePath" referids="$referids" />
          </mm:import>


          <form method="post" action="${mm:link(referrer)}">
            <p>
              <input type="hidden" name="authenticate"  value="plain"  />
              <input type="hidden" name="command"       value="login" />
              <input type="hidden" name="provider"       value="${provider}" />
              <input type="hidden" name="education"       value="${education}" />
              <mm:fieldlist nodetype="people" fields="username">
                <mm:fieldinfo type="guiname" />
              </mm:fieldlist>
              <input id="loginUsername" type="text" size="20" name="username" value="${sessionScope.registerPerson.username}" />
              <mm:fieldlist nodetype="people" fields="password">
                <mm:fieldinfo type="guiname" />
              </mm:fieldlist>
              <input id="loginPassword" type="password" size="20" name="password" value="${sessionScope.registerPassword}" />
              <input class="formbutton" id="loginSubmit" type="submit" value="${di:translate('core.login')}" />
              ${sessionScope["nl.didactor.security.reason"]}
            </p>
          </form>
          <!-- WTF WTF WTF WTF, all this explicit mentioning of 'components' is a bit silly -->
          <mm:node number="component.register" notfound="skipbody">
            <p class="noaccount">
              <di:translate key="register.noaccountyet" />
              <jsp:text> </jsp:text>
              <di:translate key="register.registeryourself" />
              <jsp:text> </jsp:text>
              <mm:treefile page="/register/index.jsp" objectlist="$includePath" referids="$referids" write="false">
                <a href="${_}"><di:translate key="register.here" /></a>
              </mm:treefile>
            </p>
          </mm:node>
        </div>
      </mm:compare>
      <mm:hasrank minvalue="didactor user">


        <div class="menuItemApplicationMenubar start">

          <mm:import externid="reset" />
          <mm:treefile page="/education/index.jsp" objectlist="$includePath" referids="reset?" write="false">
            <a title="${di:translate('core.home')}" href="${_}" class="menubar"><di:translate key="core.home" /></a>
          </mm:treefile>
        </div>

        <div class="menuSeparatorApplicationMenubar"><jsp:text> </jsp:text></div>
        <div class="menuItemApplicationMenubar">
              <mm:node number="${user}">
            <mm:treefile page="/logout.jsp" objectlist="$includePath" referids="$referids" write="false">
              <a title="${di:translate('core.logout')}"
                 href="${_}" class="menubar">
                <di:translate key="core.logout" />
                <jsp:text> </jsp:text>
                <di:person />
              </a>
            </mm:treefile>
          </mm:node>
        </div>

        <mm:node number="$user">

          <mm:nodelistfunction id="educations" name="educations" />
          <c:if test="${fn:length(educations) gt 1}">
            <div class="menuSeparatorApplicationMenubar"><jsp:text> </jsp:text></div>

            <mm:import externid="sub" />
            <div class="menuItemApplicationMenubar educationSelector">
              <form action="${_}" method="GET">
                <c:if test="${! empty sub}">
                  <input type="hidden" name="sub" value="${sub}"  />
                </c:if>
                <c:forEach items="node,${referids}" var="p">
                  <c:if test="${p ne 'education' and ! empty param[p]}">
                    <input type="hidden" name="${p}" value="${param[p]}"  />
                  </c:if>
                </c:forEach>
                <select name="education" onchange="this.form.submit()">
                  <mm:listnodes referid="educations">
                    <mm:option value="${_node}" compare="${education}">${_node.name}</mm:option>
                  </mm:listnodes>
                </select>
              </form>
            </div>
          </c:if>
        </mm:node>


        <di:blocks styleClass="menuItemApplicationMenubar"
                  classification="applicationbar">
          <jsp:attribute name="separator">
            <div class="menuSeparatorApplicationMenubar"><jsp:text> </jsp:text></div>
          </jsp:attribute>
        </di:blocks>

        <!--
            WTF WTF, specific code for yet another certain component
            This must be gone!
        -->

        <div class="menuSeparatorApplicationMenubar"><jsp:text> </jsp:text></div>


        <div class="menuItemApplicationMenubar">
          <mm:hasnode number="component.portfolio">
            <mm:treefile page="/portfolio/index.jsp?edit=true" objectlist="$includePath" referids="$referids" write="false">
              <a title="${di:translate('core.configuration')}"
                 href="${_}" class="menubar"><di:translate key="core.configuration" /></a>
            </mm:treefile>
            <div class="menuSeparatorApplicationMenubar"><jsp:text> </jsp:text></div>
          </mm:hasnode>

          <mm:hasnode number="component.portfolio" inverse="true">
            <c:if test="${di:setting('core', 'show_configuration')}">
              <mm:treefile page="/admin/index.jsp"
                           objectlist="$includePath" referids="$referids" write="false">
                <a title="${di:translate('core.configuration')}"
                   href="${_}" class="menubar"><di:translate key="core.configuration" /></a>
              </mm:treefile>
              <div class="menuSeparatorApplicationMenubar"><jsp:text> </jsp:text></div>
            </c:if>
          </mm:hasnode>
        </div>


        <div class="menuItemApplicationMenubar">
          <a title="${di:translate('core.print')}"
             href="javascript:printThis();"  class="menubar"><di:translate key="core.print" /></a>
        </div>

        <!--
            region cms help and faq
            WTF, why it this present in a generic JSP??
            Can people _please_ not hack their way?
        -->
        <mm:node number="$provider">
          <mm:relatednodescontainer path="settingrel,components">
            <mm:constraint field="components.name" value="cmshelp"/>
            <mm:relatednodes>
              <mm:import id="showcmshelp" />
            </mm:relatednodes>
          </mm:relatednodescontainer>
          <mm:relatednodescontainer path="settingrel,components">
            <mm:constraint field="components.name" value="faq"/>
            <mm:relatednodes>
              <mm:import id="showfaq" />
            </mm:relatednodes>
          </mm:relatednodescontainer>
        </mm:node>

        <mm:notpresent referid="showcmshelp"><!-- WTF -->
          <mm:present referid="education">
            <mm:node number="$education">
              <mm:relatednodescontainer path="settingrel,components">
                <mm:constraint field="components.name" value="cmshelp"/>
                <mm:relatednodes>
                  <mm:import id="showcmshelp" />
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:node>
          </mm:present>
        </mm:notpresent>

        <mm:notpresent referid="showfaq"> <!-- WTF -->
          <mm:present referid="education">
            <mm:node number="$education">
              <mm:relatednodescontainer path="settingrel,components">
                <mm:constraint field="components.name" value="faq"/>
                <mm:relatednodes>
                  <mm:import id="showfaq" />
                </mm:relatednodes>
              </mm:relatednodescontainer>
            </mm:node>
          </mm:present>

        </mm:notpresent>

        <mm:present referid="showcmshelp" >
          <mm:node number="component.cmshelp">
            <mm:treeinclude page="/cmshelp/cockpit/rolerelated.jsp" objectlist="$includePath" referids="$referids" >
              <mm:param name="scope">education</mm:param>
            </mm:treeinclude>
          </mm:node>
        </mm:present>

        <mm:present referid="showfaq" >
          <mm:node number="component.faq" notfound="skipbody">
            <mm:treeinclude page="/faq/cockpit/rolerelated.jsp" objectlist="$includePath" referids="$referids" />
          </mm:node>
        </mm:present>
        <!-- end of region cms help and faq -->

      </mm:hasrank>
    </div>


  </mm:cloud>
</jsp:root>
