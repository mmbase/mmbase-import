<mm:context>
  <mm:nodeinfo id="actualtype" type="nodemanager" write="false" />
  <li>
    <span class="editintro">
      <mm:field name="title" />:
    </span>
    <span class="editlinks">
      <mm:maywrite>
        <mm:import id="wizard">tasks/<mm:write referid="actualtype" />/<mm:write referid="level" /></mm:import>
        <mm:compare referid="level" value="topic">
          <mm:present referid="tri">
            <mm:import id="wizard" reset="true">tasks/<mm:write referid="actualtype" />/mediatopic</mm:import>
          </mm:present>
        </mm:compare>
        <a href="<mm:url referids="referrer,language,origin,wizard" page="${jsps}wizard.jsp">
          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
          </mm:url>">Wijzig Structuur
        </a>
      </mm:maywrite>
      <mm:field name="number" id="nodenumber" write="false" />
      <mm:maychangecontext>
        | <a href="<mm:url referids="tab,nodenumber" />" />Wijzig context (<mm:field name="owner" />)</a></mm:maychangecontext>
      </mm:maychangecontext>
      <mm:first inverse="true" list="mainlist">
        | <a href="<mm:url page="jumper.jsp" referids="level,topic,subtopic?,detail?" />">Jumper</a>
      </mm:first>
    </span>
  </li>
</mm:context>