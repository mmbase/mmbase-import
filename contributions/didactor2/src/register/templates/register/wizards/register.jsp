<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di"
%><%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"
%>
<mm:content postprocessor="reducespace" expires="0">
  <mm:cloud rank="editor">
    <jsp:directive.include file="/education/wizards/roles_defs.jsp" />

    <mm:import externid="educationid" />
    <mm:import externid="person" />
    <mm:import externid="chosenclass" />
    <mm:import externid="chosenworkgroup" />

    <html>
      <head>
        <title></title>
        <link rel="stylesheet" href="${mm:treelink('/css/base.css', includePath)}" />
        <link rel="stylesheet" href="${mm:treelink('/register/css/register.css', includePath)}"  />
        <style>
          body {
          width: 80%;
          padding: 30px;
          }
        </style>
      </head>
      <body>
        <div class="content">
          <p>
            <mm:import id="sep" escape="substring(1,2)"><fmt:formatNumber value="0.0" minFractionDigits="1"  /></mm:import>
            <mm:link page="export.jsp">
              <mm:param name="sep">${sep eq ',' ? ';' : ','}</mm:param>
              <a href="${_}">Export</a>
            </mm:link>
          </p>
          <mm:import id="editcontextname" reset="true">opleidingen</mm:import><!-- TODO, this is dutch -->
          <jsp:directive.include file="/education/wizards/roles_chk.jsp" /> <!-- TODO, this is silly -->
          <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
            <mm:isnotempty referid="chosenclass">
              <mm:compare referid="chosenworkgroup" value="-">
                <script>
                  alert("<di:translate key="register.chooseworkgroup" />");
                </script>
              </mm:compare>

              <mm:compare referid="chosenworkgroup" value="-" inverse="true">
                <mm:node id="n_education" number="$educationid" />
                <mm:createrelation source="person" destination="chosenclass" role="classrel" />
                <mm:createrelation source="person" destination="chosenworkgroup" role="related" />
                <mm:listnodes type="roles" constraints="roles.name='student'">
                  <mm:createrelation source="person" destination="_node" role="related" />
                </mm:listnodes>

                <mm:node referid="person">
                  <mm:listrelationscontainer role="related" type="educations">
                    <mm:constraint field="number" value="${educationid}" />
                    <mm:listrelations>
                      <mm:deletenode />
                    </mm:listrelations>
                  </mm:listrelationscontainer>
                </mm:node>
                <di:getsetting component="register" setting="send_email">
                  <mm:compare value="true">
                    <mm:treeinclude page="/register/wizards/welcome.mail.jspx"
                                    referids="chosenclass,chosenworkgroup,person@chosenstudent,educationid@choseneducation"
                                    objectlist="$includePath"
                                    />
                  </mm:compare>
                </di:getsetting>
              </mm:compare>
            </mm:isnotempty>

            <mm:isnotempty referid="person">
              <mm:import externid="delete" />
              <mm:isnotempty referid="delete">
                <mm:deletenode number="$person" deleterelations="true"/>
                <mm:import id="person" reset="true" />
              </mm:isnotempty>
              <mm:isnotempty referid="delete" inverse="true">
                <mm:node number="$person">
                  <mm:locale language="$language">
                    <mm:fieldlist fields="${di:setting('core', 'admin_personfields')},email">
                      <mm:fieldinfo type="guiname" />: <mm:field /> <br />
                    </mm:fieldlist>
                  </mm:locale>
                  <hr />
                  <mm:node number="$educationid">
                    <di:translate key="register.relate_to_class" /><br />
                    <table class="registerTable">
                      <tr>
                        <th><di:translate key="register.classname" /></th>
                        <th><di:translate key="register.classstart" /></th>
                        <th><di:translate key="register.workgroup" /></th>
                        <th></th>
                      </tr>
                      <mm:related path="classes,mmevents" fields="classes.number,classes.name,mmevents.start,mmevents.stop" orderby="mmevents.start" directions="down">
                        <tr>
                          <form method="post">
                            <input type="hidden" name="educationid" value="${educationid}" />
                            <input type="hidden" name="person" value="${person}" />
                            <input type="hidden" name="chosenclass" value="${_node.classes}" />
                            <td><nobr><mm:field name="classes.name" /></nobr></td>
                            <td><nobr><mm:field name="mmevents.start"><mm:time format=":LONG" /></mm:field></nobr></td>
                            <td>
                              <select name="chosenworkgroup">
                                <option value="-"><di:translate key="register.select_workgroup" /></option>
                                <mm:node element="classes">
                                  <mm:related path="workgroups" fields="workgroups.name,workgroups.number">
                                    <option value="${_node.workgroups}"><mm:field name="workgroups.name" /></option>
                                  </mm:related>
                                </mm:node>
                              </select>
                            </td>
                            <td>
                              <input type="submit" value="${di:translate('register.choose')}" />
                            </td>
                          </form>
                        </tr>
                      </mm:related>
                    </table>
                  </mm:node>
                </mm:node>
              </mm:isnotempty>
            </mm:isnotempty>
            <mm:isempty referid="person">
              <mm:node number="$educationid">
                <di:translate key="register.chooseregistration" /><br />
                <hr />
                <table class="listTable">
                  <tr>
                    <mm:fieldlist nodetype="people" fields="number,${di:setting('core', 'admin_personfields')},username">
                      <th><mm:fieldinfo type="guiname" /></th>
                    </mm:fieldlist>
                    <th><di:translate key="register.delete" /></th>
                  </tr>
                  <mm:relatednodes type="people" role="classrel" id="related" orderby="number"/> <!-- register/index.jsp used to do that -->
                  <mm:relatednodes type="people" role="related" add="related" orderby="number">
                    <mm:countrelations type="roles">
                      <mm:compare value="0">
                        <mm:treefile page="/register/wizards/register.jsp" objectlist="$includePath" referids="$referids,educationid,_node@person" id="url" write="false" />
                        <tr>
                          <mm:fieldlist nodetype="people" fields="number,${di:setting('core', 'admin_personfields')},username">
                            <td><a href="${url}"><mm:fieldinfo type="value" /></a></td>
                          </mm:fieldlist>
                          <td>
                            <mm:link referid="url">
                              <mm:param name="delete">true</mm:param>
                              <a href="${_}" onclick="return confirm('${di:translate('register.delete_areyousure')}');"><di:translate key="register.delete" /></a>
                            </mm:link>
                          </td>
                        </tr>
                      </mm:compare>
                    </mm:countrelations>
                  </mm:relatednodes>
                </table>
              </mm:node>
            </mm:isempty>
          </mm:islessthan>
        </div>
      </body>
    </html>
  </mm:cloud>
</mm:content>
