<mm:context>
  <li>
    <span class="editintro">
      <mm:field name="title" />:
    </span>
    <span class="editlinks">
      <mm:maywrite>
        <mm:import id="wizard">tasks/structure/categories</mm:import>
        <mm:relatednodescontainer type="categorytypes" role="related" searchdirs="destination">
          <mm:relatednodes>
            <mm:field name="wizard" >
              <mm:isnotempty>
                 <mm:import id="wizard" reset="true">tasks/<mm:field name="wizard" /></mm:import>
               </mm:isnotempty>
             </mm:field>
          </mm:relatednodes>
        </mm:relatednodescontainer>
        <a href="<mm:url referids="referrer,language,wizard" page="${jsps}wizard.jsp">
          <mm:param name="objectnumber"><mm:field name="number" /></mm:param>
          </mm:url>">Edit Structure
        </a>
      </mm:maywrite>
      <mm:field name="number" id="nodenumber" write="false" />
      <mm:maychangecontext>
        | <a href="<mm:url referids="tab,nodenumber" />" />Change context (<mm:field name="owner" />)</a></mm:maychangecontext>
      </mm:maychangecontext>
      <mm:relatednodescontainer type="templates" role="related" searchdirs="destination">
        <mm:relatednodes>
          <mm:field name="url">
            <mm:isnotempty>
              | <a href="<mm:url page="jumper.jsp" referids="level,maintopic,subtopic?,detail?" />">Jumper</a>
            </mm:isnotempty>
          </mm:field>
        </mm:relatednodes>
      </mm:relatednodescontainer>
    </span>
  </li>
</mm:context>