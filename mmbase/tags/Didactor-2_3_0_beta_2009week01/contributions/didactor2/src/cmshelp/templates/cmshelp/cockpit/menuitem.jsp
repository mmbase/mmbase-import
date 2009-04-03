<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"%>

<mm:content postprocessor="reducespace">
  <mm:cloud method="delegate">
<!-- in education bar should be shown only help with educations set-->

    <mm:import id="scope" externid="scope" />
    <mm:present referid="education">
      <mm:import id="edu" externid="education" />
    </mm:present>

    <mm:compare referid="scope" value="education">
      <mm:import jspvar="link" id="link">
      <mm:treefile page="/cmshelp/frontoffice/index.jsp" objectlist="$includePath" referids="$referids" escapeamps="false" />&node=</mm:import>
      <mm:listnodes type="helpcontainers">
        <mm:first>
          <mm:field id="helpcontainer" name="number" write="false" />
        </mm:first>
      </mm:listnodes>
      <mm:present referid="helpcontainer">
        <mm:node number="$helpcontainer" notfound="skipbody">
          <mm:relatednodes type="helpnodes">

  <!--      if role is set for this cmshelp, test user role -->
            <mm:import id="roleok" reset="true">0</mm:import>
            <mm:remove referid="rolerelated" />

            <mm:relatednodes type="roles">
              <mm:compare inverse="true" referid="roleok" value="1">
                <mm:import id="role" jspvar="role" reset="true"><mm:field name="name"/></mm:import>
                <mm:import id="rolerelated" reset="true"/>
                <mm:node number="$user" notfound="skipbody">
                  <di:hasrole role="<%=role%>">
                    <mm:import id="roleok" reset="true">1</mm:import>
                  </di:hasrole>
                </mm:node>
              </mm:compare>
            </mm:relatednodes>

            <mm:notpresent referid="rolerelated">
              <mm:import id="roleok" reset="true">1</mm:import>
            </mm:notpresent>

  <!--      if education is present (user choose education), this cmshelp
            should be shown only if it was set for it -->
            <mm:compare referid="roleok" value="1">
              <mm:present referid="education">
                <mm:import id="helpname" reset="true"><mm:field name="name" /></mm:import>
                <mm:import id="helpnumber" jspvar="helpNumber" reset="true"><mm:field name="number" /></mm:import>
                <mm:relatednodes type="educations" constraints="educations.number='${edu}'">
                  <div class="menuSeparator"></div>
                  <div class="menuItem">
                    <a title="<mm:write referid="helpname"/>"
                       href="<%=link%><%=helpNumber%>" class="menubar"><mm:write referid="helpname" /></a>
                  </div>
                </mm:relatednodes>
              </mm:present>
            </mm:compare>
          </mm:relatednodes>
        </mm:node>
      </mm:present>
    </mm:compare>
  </mm:cloud>
</mm:content>
