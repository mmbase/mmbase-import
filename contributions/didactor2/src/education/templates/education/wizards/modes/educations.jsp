<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <!--
      TODO: This Jsp is much to complicated.
      - Too long
      - Too much component specific code
      - Too much javascript (should perhaps use event handlers)
  -->
  <div class="educations">
    <mm:import id="imageName"></mm:import> <!-- horrible idea -->
    <mm:import id="sAltText"></mm:import>
    <mm:content type="application/xml" expires="${param.expires}">
      <mm:cloud rank="basic user">
        <jsp:directive.include file="../mode.include.jsp" />

        <!--
            Educations come from here
        -->
        <di:has editcontext="opleidingen">
          <a href="javascript:clickNode('node_0')">
            <img src='gfx/tree_pluslast.gif' width="16" border='0' align='center' valign='middle' id='img_node_0'/>
          </a>
          <img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>
          <span style='width:100px; white-space: nowrap'>
            <mm:link referid="listjsp${forbidtemplate}">
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
          <br /><!-- br's are silly -->
          <div id='node_0' style='display: none'>
            <mm:node number="$user">
              <mm:hasrank value="administrator">
                <mm:listnodes id="educations" type="educations" />
              </mm:hasrank>
              <mm:hasrank value="administrator" inverse="true">
                <mm:nodelistfunction id="educations" name="educations" />
              </mm:hasrank>
            </mm:node>
            <mm:import id="number_of_educations" reset="true">${fn:length(educations)}</mm:import>

            <table border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
                <!-- We have to detect the last element -->
                <mm:isgreaterthan referid="number_of_educations" value="0">
                  <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
                </mm:isgreaterthan>

                <mm:islessthan    referid="number_of_educations" value="1">
                  <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
                </mm:islessthan>

                <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
                <mm:link referid="wizardjsp" referids="user@origin">
                  <mm:param name="wizard">config/education/educations-origin</mm:param>
                  <mm:param name="objectnumber">new</mm:param>
                  <mm:param name="path"></mm:param>
                  <td>
                    <nobr>
                      <a href="${_}"
                         title="${di:translate('education.createneweducationdescription')}"
                         target="text"><di:translate key="education.createneweducation" /></a>
                  </nobr></td>
                </mm:link>
              </tr>
            </table>

            <mm:import id="educationId" externid="e">${education}</mm:import>

            <mm:isgreaterthan referid="number_of_educations" value="0">
              <!-- The Education starts from here -->
              <mm:node number="${educationId}">
                <jsp:directive.include file="../whichimage.jsp" />
                <table border="0" cellpadding="0" cellspacing="0">
                  <tr>
                    <td>
                      <!-- spacer.gifs are _evil_, silly and cause noise. JSP is code _too_. -->
                      <img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/>
                    </td>
                    <td>
                      <a href="javascript:clickNode('education_0')">
                        <img src="gfx/tree_pluslast.gif" border="0"
                             align="center" valign="middle" id="img_education_0"/>
                      </a>
                    </td>
                    <td>
                      <img src="gfx/folder_closed.gif" border="0" align="middle" id="img2_education_0"/>
                    </td>
                    <td>
                      <nobr>
                        <mm:link referid="wizardjsp" referids="_node@objectnumber">
                          <mm:param name="wizard">config/education/educations</mm:param>
                          <mm:param name="title"><di:translate key="education.editeducation" /></mm:param>
                          <a href="${_}" target="text"><mm:field name="name" /></a>
                        </mm:link>
                        <mm:field id="eduname" write="false" name="name" />
                        <mm:write session="eduname" referid="eduname" />

                        <!--
                            follows horrible code specific for some components
                            probably broken, btw.
                        -->
                        <mm:present referid="pdfurl">
                          <mm:link page="${pdfurl}" referids="_node@number">
                            <a href="${_}" target="text"><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                          </mm:link>
                        </mm:present>
                        <mm:hasnode number="component.metadata">
                          <a href="metaedit.jsp?number=${educationId}" target="text">
                            <img id="img_${educationId}" src="${imageName}"
                                 border="0" title="${sAltText}" alt="${sAltText}" /></a>
                        </mm:hasnode>
                        <mm:hasnode number="component.drm">
                          <mm:link referid="wizardjsp" referids="educationId@objectnumber">
                            <mm:param name="wizard">educationlicense</mm:param>
                            <!-- Todo: DUTCH -->
                            <a target="text" href="${_}"
                               title="Bewerk licentie" style="font-size: 1em; text-decoration: none">©</a>
                          </mm:link>
                        </mm:hasnode>
                        <mm:hasnode number="component.versioning">
                          <a href="versioning.jsp?nodeid=${educationId}" target="text"><img src="gfx/versions.gif" border="0" /></a>
                        </mm:hasnode>
                      </nobr>
                    </td>
                  </tr>
                </table>

                <!--  We have to count all learnblocks.
                     MM: I can see that. But why?
                -->
                <mm:relatednodescontainer role="posrel" type="learnobjects" searchdirs="destination">
                  <mm:size id="number_of_learnblocks" write="false"/>
                </mm:relatednodescontainer>


                <div id="education_0" style='display: none'>
                  <!-- Registration -->
                  <mm:hasnode number="component.register">
                    <table border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                        <td><img src='gfx/tree_vertline-leaf.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        <td><img src='gfx/new_education.gif' width="16" border='0' align='middle' /></td>
                        <mm:treefile write="false" page="/register/wizards/register.jsp"
                                     referids="$referids,educationId"
                                     objectlist="$includePath">
                          <td>
                            <nobr>
                              <a href="${_}" title="${di:translate('register.registrations')}"
                                 target="text"><di:translate key="register.registrations" /></a>
                            </nobr>
                          </td>
                        </mm:treefile>
                      </tr>
                    </table>
                  </mm:hasnode>


                  <!-- I think it is hackery -->
                  <mm:hasnode number="component.portal">
                    <table border="0" cellpadding="0" cellspacing="0">
                      <tr>
                        <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                        <td><img src='gfx/tree_vertline-leaf.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        <td><img src='gfx/new_education.gif' width="16" border='0' align='middle' /></td>
                        <mm:treefile write="false" page="/portal/wizards/index.jspx"
                                     referids="language,educationId,$referids" objectlist="$includePath">

                          <td>
                            <nobr>
                              <a href="${_}"
                                 title="${di:translate('portal.portal')}"
                                 target="text"><di:translate key="portal.portal" /></a>
                            </nobr>
                          </td>
                        </mm:treefile>
                      </tr>
                    </table>
                  </mm:hasnode>

                  <mm:relatednodes path="posrel,learnblocks" element="posrel" orderby="posrel.pos" directions="down" max="1">
                    <mm:field name="pos" id="maxpos" write="false" />
                  </mm:relatednodes>

                  <!-- create new learnblock item -->
                  <table border="0" cellpadding="0" cellspacing="0">
                    <tr>
                      <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                      <mm:isgreaterthan referid="number_of_learnblocks" value="0">
                        <td><img src='gfx/tree_vertline-leaf.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        </mm:isgreaterthan>
                        <mm:islessthan    referid="number_of_learnblocks" value="1">
                          <td><img src='gfx/tree_leaflast.gif' border='0' align='center' valign='middle' id='img_node_0_1_2'/></td>
                        </mm:islessthan>
                        <td><img src='gfx/new_education.gif' width="16" border='0' align='middle' /></td>
                        <mm:link referid="wizardjsp" referids="_node@origin">
                          <mm:param name="wizard">config/learnblocks/learnblocks-origin</mm:param>
                          <mm:param name="objectnumber">new</mm:param>
                          <mm:param name="newpos">${maxpos + 1}</mm:param>
                          <td>
                            <nobr>
                              <a href="${_}" title="${di:translate('education.createnewlearnblockdescription')}" target="text">
                              <di:translate key="education.createnewlearnblock" /></a>
                            </nobr>
                          </td>
                        </mm:link>
                      </tr>
                    </table>


                    <!-- All learnblocks for current education -->
                    <mm:relatednodes role="posrel" orderby="posrel.pos"
                                     directions="up"
                                     searchdir="destination" type="learnobjects">
                      <mm:index id="learnblockcounter" write="false" />
                      <jsp:directive.include file="../whichimage.jsp" />
                      <mm:nodeinfo type="type" id="this_node_type">
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

                        <table border="0" cellpadding="0" cellspacing="0">
                          <tr>
                            <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                            <td>
                              <!-- horrible code duplication, just for, as i can see it correctly _one_ word -->
                              <mm:last inverse="true">
                                <a href="javascript:clickNode('node_0_0_${learnblockcounter}')">
                                <img src="gfx/tree_plus.gif" border="0" align="center" valign="middle" id="img_node_0_0_${learnblockcounter}"/></a>
                              </mm:last>
                              <mm:last>
                                <a href="javascript:clickNode('node_0_0_${learnblockcounter}')">
                                <img src="gfx/tree_pluslast.gif" border="0" align="center" valign="middle" id="img_node_0_0_${learnblockcounter}"/></a>
                              </mm:last>
                            </td>
                            <td>
                              <img src="gfx/folder_closed.gif" border="0" align="middle" id='img2_node_0_0_${learnblockcounter}'/>
                            </td>
                            <td>
                              <nobr>
                                <mm:import id="guitype" ><mm:nodeinfo type="guitype" escape="lowercase" /></mm:import>
                                <mm:link referid="wizardjsp" referids="_node@objectnumber">
                                  <mm:param name="wizard">config/<mm:nodeinfo type="type"/>/<mm:nodeinfo type="type" /></mm:param>
                                  <a href="${_}" title="${di:translate('education.editexisting')} ${guitype}" target="text">
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
                              </nobr>
                            </td>
                          </tr>
                        </table>
                      </mm:nodeinfo>

                      <div id="node_0_0_${learnblockcounter}" style="display:none">
                        <mm:treeinclude write="true" page="/education/wizards/learnobject.jsp" objectlist="$includePath"
                                        referids="wizardjsp">
                          <mm:param name="startnode"><mm:field name="number" /></mm:param>
                          <mm:param name="depth">10</mm:param>
                          <mm:last>
                            <mm:param name="the_last_parent">true</mm:param>
                          </mm:last>
                          <mm:last inverse="true">
                            <mm:param name="the_last_parent">false</mm:param>
                          </mm:last>
                        </mm:treeinclude>
                      </div>
                    </mm:relatednodes>
                </div>
              </mm:node>
            </mm:isgreaterthan>
          </div>
        </di:has>
      </mm:cloud>
    </mm:content>
  </div>
</jsp:root>
