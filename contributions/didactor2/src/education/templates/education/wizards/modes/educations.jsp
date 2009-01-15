<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:cloud>
    <!--
        TODO: This Jsp is much to complicated.
        - Too long
        - Too much component specific code
        - Too much javascript (should perhaps use event handlers)
    -->
    <div
        xmlns="http://www.w3.org/1999/xhtml"
        class="educations">
      <mm:import id="imageName"></mm:import> <!-- horrible idea -->
      <mm:import id="sAltText"></mm:import>
      <mm:content type="application/xml"
                  expires="${param.expires}">
        <mm:cloud rank="basic user">
          <jsp:directive.include file="../mode.include.jsp" />

          <!--
              Educations come from here
          -->
          <di:has editcontext="opleidingen"><!-- TODO DUTCH. 'opleidingen' means 'educations' -->
            <ul class="filetree treeview">
              <li>
                <span class="folder">
                  <mm:link referid="listjsp"
                           referids="provider">
                    <mm:param name="wizard">config/education/educations</mm:param>
                    <mm:param name="nodepath">educations</mm:param>
                    <mm:param name="searchfields">name</mm:param>
                    <mm:param name="fields">name</mm:param>
                    <mm:param name="orderby">name</mm:param>
                    <a href="${_}" target="text">
                      <di:translate key="education.educationmenueducations" />
                    </a>
                  </mm:link>
                </span>
                <ul>
                  <mm:node number="$user">
                    <mm:hasrank value="administrator">
                      <mm:listnodes id="educations" type="educations" />
                    </mm:hasrank>
                    <mm:hasrank value="administrator" inverse="true">
                      <mm:nodelistfunction id="educations" name="educations" />
                    </mm:hasrank>
                  </mm:node>
                  <mm:import id="number_of_educations" reset="true">${fn:length(educations)}</mm:import>

                  <di:has editcontext="create_education">
                    <li>
                      <di:icon name="new_education" />
                      <mm:link referid="wizardjsp" referids="user@origin,provider">
                        <mm:param name="wizard">config/education/educations-origin</mm:param>
                        <mm:param name="objectnumber">new</mm:param>
                        <mm:param name="path"></mm:param>
                        <a href="${_}"
                           title="${di:translate('education.createneweducationdescription')}"
                           target="text"><di:translate key="education.createneweducation" /></a>
                      </mm:link>
                    </li>
                  </di:has>

                  <mm:import externid="e">${education}</mm:import>

                  <mm:node number="${provider}">
                    <mm:relatednodes type="educations">
                      <li class="${_node eq e ? 'active' : 'inactive'}">
                        <span class="folder">
                          <mm:link referid="wizardjsp" referids="_node@objectnumber">
                            <mm:param name="wizard">config/education/educations</mm:param>
                            <mm:param name="title"><di:translate key="education.editeducation" /></mm:param>
                            <a href="${_}"><mm:field name="name" /></a>
                          </mm:link>
                          <mm:field id="eduname" write="false" name="name" />
                          <mm:write session="eduname" referid="eduname" />

                          <!-- WTF -->
                          <mm:present referid="pdfurl">
                            <mm:link page="${pdfurl}" referids="_node@number">
                              <a href="${_}"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                            </mm:link>
                          </mm:present>
                          <mm:hasnode number="component.metadata">
                            <a href="metaedit.jsp?number=${educationId}">
                              <img id="img_${educationId}" src="${imageName}"
                                   border="0" title="${sAltText}" alt="${sAltText}" /></a>
                          </mm:hasnode>
                          <mm:hasnode number="component.drm">
                            <mm:link referid="wizardjsp" referids="educationId@objectnumber">
                              <mm:param name="wizard">educationlicense</mm:param>
                              <a  href="${_}"
                                  title="Bewerk licentie" style="font-size: 1em; text-decoration: none">©</a>
                            </mm:link>
                          </mm:hasnode>
                          <mm:hasnode number="component.versioning">
                            <a href="versioning.jsp?nodeid=${educationId}"><img src="gfx/versions.gif" border="0" /></a>
                          </mm:hasnode>
                        </span>
                        <ul>
                          <mm:hasnode number="component.register">
                            <li>
                              <di:icon name="new_education" />
                              <mm:treefile write="false" page="/register/wizards/register.jsp"
                                           referids="$referids,educationId"
                                           objectlist="$includePath">
                                <a href="${_}" title="${di:translate('register.registrations')}">
                                  <di:translate key="register.registrations" />
                                </a>
                              </mm:treefile>
                            </li>

                          </mm:hasnode>


                          <!-- I think it is hackery -->
                          <mm:hasnode number="component.portal">

                            <li>
                              <di:icon name="new_education" />
                              <mm:treefile write="false" page="/portal/wizards/index.jspx"
                                           referids="language,educationId,$referids" objectlist="$includePath">
                                <a href="${_}"
                                   title="${di:translate('portal.portal')}"
                                   target="text"><di:translate key="portal.portal" />
                                </a>
                              </mm:treefile>
                            </li>
                          </mm:hasnode>

                          <mm:listrelations role="posrel" orderby="posrel.pos"
                                            type="learnobjects" searchdir="destination"
                                            directions="down" max="1">
                            <mm:field name="pos" write="false" id="maxpos" />
                          </mm:listrelations>
                          <!-- create new learnblock item -->
                          <li>
                            <di:icon name="new_education" />
                            <mm:link referid="wizardjsp" referids="_node@origin">
                              <mm:param name="wizard">config/learnblocks/learnblocks-origin</mm:param>
                              <mm:param name="objectnumber">new</mm:param>
                              <mm:param name="newpos">${maxpos + 1}</mm:param>
                              <a href="${_}" title="${di:translate('education.createnewlearnblockdescription')}">
                                <di:translate key="education.createnewlearnblock" />
                              </a>
                            </mm:link>
                          </li>


                          <!-- All learnblocks for current education -->
                          <mm:relatednodes role="posrel" orderby="posrel.pos"
                                           directions="up"
                                           varStatus="status"
                                           searchdir="destination" type="learnobjects">

                            <mm:index id="learnblockcounter" write="false" />
                            <jsp:directive.include file="../whichimage.jsp" />

                            <mm:nodeinfo type="type" id="this_node_type" write="false" />
                            <mm:import id="mark_error" reset="true"></mm:import>
                            <mm:compare referid="this_node_type" value="tests">
                              <mm:field name="questionamount" id="questionamount">
                                <mm:isgreaterthan value="0">
                                  <mm:countrelations type="questions">
                                    <mm:islessthan value="$questionamount">
                                      <!-- TODO DUTCH -->
                                      <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er gesteld moeten worden.</mm:import>
                                    </mm:islessthan>
                                  </mm:countrelations>
                                </mm:isgreaterthan>

                                <mm:field name="requiredscore" id="requiredscore">
                                  <mm:countrelations type="questions">
                                    <mm:islessthan value="$requiredscore">
                                      <!-- TODO DUTCH -->
                                      <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er goed beantwoord moeten worden.</mm:import>
                                    </mm:islessthan>
                                  </mm:countrelations>
                                  <mm:isgreaterthan referid="questionamount" value="0">
                                    <mm:islessthan referid="questionamount" value="$requiredscore">
                                      <!-- TODO DUTCH -->
                                      <mm:import id="mark_error" reset="true">Er worden minder vragen gesteld dan er goed beantwoord moeten worden.</mm:import>
                                    </mm:islessthan>
                                  </mm:isgreaterthan>
                                </mm:field>
                              </mm:field>
                            </mm:compare>

                            <li>
                              <span class="folder">
                                <mm:import id="guitype" ><mm:nodeinfo type="guitype" escape="lowercase" /></mm:import>
                                <mm:link referid="wizardjsp" referids="_node@objectnumber">
                                  <mm:param name="wizard">config/<mm:nodeinfo type="type"/>/<mm:nodeinfo type="type" /></mm:param>
                                  <a href="${_}" title="${di:translate('education.editexisting')} ${guitype}">
                                    <mm:field name="name" />
                                  </a>
                                </mm:link>
                                <mm:present referid="pdfurl">
                                  <mm:compare referid="this_node_type" valueset="pages,learnblocks">
                                    <mm:link page="${pdfurl}" referids="_node@number">
                                      <a href="${_}" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                                    </mm:link>
                                  </mm:compare>
                                </mm:present>
                                <mm:hasnode number="component.metadata">
                                  <mm:link page="metaedit.jsp" referids="_node@number">
                                    <a href="${_}" target="text">
                                      <img id="img_${_node}" src="${imageName}" border="0" title="${sAltText}"
                                           alt="${sAltText}" />
                                    </a>
                                  </mm:link>
                                </mm:hasnode>

                                <mm:hasnode number="component.versioning">
                                  <mm:link page="versioning.jsp" referids="_node@nodeid">
                                    <a href="${_}" target="text"><img src="gfx/versions.gif" border="0" /></a>
                                  </mm:link>
                                </mm:hasnode>
                              </span>
                              <ul>
                                <mm:treeinclude
                                    debug="html"
                                    page="/education/wizards/learnobject.jsp"
                                    objectlist="$includePath"
                                    referids="wizardjsp,_node@startnode">
                                  <mm:param name="branchPath">..${status.last ? '.' : ' '}</mm:param>
                                  <mm:param name="startnode"><mm:field name="number" /></mm:param>
                                </mm:treeinclude>
                              </ul>
                            </li>
                          </mm:relatednodes>
                        </ul>
                      </li>
                    </mm:relatednodes>
                  </mm:node>
                </ul>
              </li>
            </ul>
          </di:has>
        </mm:cloud>
      </mm:content>
    </div>
  </mm:cloud>
</jsp:root>
