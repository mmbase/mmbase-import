<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%>
<mm:content postprocessor="reducespace" expires="0">
  <mm:cloud method="delegate">

    <jsp:directive.include file="/shared/setImports.jsp" />
    <mm:locale language="$language">
      <jsp:directive.include file="/education/wizards/roles_defs.jsp" />
      
      <mm:import externid="educationid" />
      <mm:import externid="person" />
      <mm:import externid="chosenclass" />
      <mm:import externid="chosenworkgroup" />
      
      <html>
        <head>
          <title></title>
          <link rel="stylesheet" href="${mm:treefile('/css/base.css', pageContext, includePath)}" />
          <link rel="stylesheet" href="${mm:treefile('/register/css/register.css', pageContext, includePath)}"  />
          <style>
            body {
            width: 80%;
            padding: 30px;
            }
          </style>
        </head>
        <body>
          <div class="content">
            <mm:import id="editcontextname" reset="true">opleidingen</mm:import>
            <jsp:directive.include file="/education/wizards/roles_chk.jsp" />
            <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
              <mm:isnotempty referid="chosenclass">
                <mm:compare referid="chosenworkgroup" value="-">
                  <script>
                    alert("<di:translate key="register.chooseworkgroup" />");
                  </script>
                </mm:compare>
                <mm:compare referid="chosenworkgroup" value="-" inverse="true">
                  <mm:node id="n_student" number="$person" />
                  <mm:node id="n_education" number="$educationid" />
                  <mm:node id="n_class" number="$chosenclass" />
                  <mm:node id="n_workgroup" number="$chosenworkgroup" />
                  <mm:createrelation source="n_student" destination="n_class" role="classrel" />
                  <mm:createrelation source="n_student" destination="n_workgroup" role="related" />
                  <mm:listnodes type="roles" constraints="roles.name='student'">
                    <mm:node id="n_role" />
                    <mm:createrelation source="n_student" destination="n_role" role="related" />
                    <mm:remove referid="n_role" />
                  </mm:listnodes>

                  <mm:node referid="n_student">
                    <mm:related path="related,educations" fields="related.number,educations.number">
                      <mm:import id="ed"><mm:field name="educations.number" /></mm:import>
                      <mm:import id="rel"><mm:field name="related.number" /></mm:import>
                      <mm:compare referid="ed" referid2="educationid">
                        <mm:deletenode number="$rel" />
                      </mm:compare>
                      <mm:remove referid="rel" />
                      <mm:remove referid="ed" />
                    </mm:related>
                  </mm:node>
                  <mm:remove referid="n_student" />
                  <mm:remove referid="n_education" />
                  <mm:remove referid="n_class" />
                  <mm:remove referid="n_workgroup" />
                  <mm:remove referid="person" />
                  <mm:import id="person" />
                  <mm:remove referid="chosenworkgroup" />
                  <mm:remove referid="chosenclass" />
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
                      <mm:fieldlist fields="${di:setting(pageContext, 'core', 'admin_personfields')},email">
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
                                <input type="submit" value="${di:translate(pageContext, 'register.choose')}" />
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
                <mm:remove referid="person" />
                <mm:node number="$educationid">
                  <di:translate key="register.chooseregistration" /><br />
                  <hr />
                  <table class="listTable">
                    <tr>
                      <mm:fieldlist nodetype="people" fields="number,${di:setting(pageContext, 'core', 'admin_personfields')},username">
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
                            <mm:fieldlist nodetype="people" fields="number,${di:setting(pageContext, 'core', 'admin_personfields')},username">
                              <td><a href="${url}"><mm:fieldinfo type="value" /></a></td>
                            </mm:fieldlist>
                            <td>
                              <mm:link referid="url">
                                <mm:param name="delete">true</mm:param>
                                <a href="${_}"><di:translate key="register.delete" /></a>
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
    </mm:locale>
  </mm:cloud>
</mm:content>
