<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<%@include file="/shared/setImports.jsp" %>
<%@include file="/education/wizards/roles_defs.jsp" %>

<mm:import externid="educationid" />
<mm:import externid="person" />
<mm:import externid="chosenclass" />
<mm:import externid="chosenworkgroup" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title></title>
    <link rel="stylesheet" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
    <link rel="stylesheet" href="<mm:treefile page="/register/css/register.css" objectlist="$includePath" />" />
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
      <%@include file="/education/wizards/roles_chk.jsp" %>
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
            <mm:fieldlist fields="firstname,lastname,username,email">
              <mm:fieldinfo type="guiname" />: <mm:field /> <br />
            </mm:fieldlist>
            </mm:locale>
            <hr />
            <mm:node number="$educationid">
              <mm:related path="classes,mmevents" fields="classes.number,classes.name,mmevents.start,mmevents.stop" orderby="mmevents.start">
                <mm:first>
                  <di:translate key="register.relate_to_class" /><br />
                  <table class="registerTable">
                    <tr>
                      <th><di:translate key="register.classname" /></th>
                      <th><di:translate key="register.classstart" /></th>
                      <th><di:translate key="register.workgroup" /></th>
                      <th></th>
                    </tr>
                </mm:first>
                <tr>
                  <form method="post">
                    <input type="hidden" name="educationid" value="<mm:write referid="educationid" />" />
                    <input type="hidden" name="person" value="<mm:write referid="person" />" />
                    <input type="hidden" name="chosenclass" value="<mm:field name="classes.number" />" />
                    <td><nobr><mm:field name="classes.name" /></nobr></td>
                    <td><nobr><mm:field name="gui(mmevents.start)" /></nobr></td>
                    <td>
                      <select name="chosenworkgroup">
                        <option value="-"><di:translate key="register.select_workgroup" /></option>
                        <mm:node element="classes">
                          <mm:related path="workgroups" fields="workgroups.name,workgroups.number">
                            <option value="<mm:field name="workgroups.number" />"><mm:field name="workgroups.name" /></option>
                          </mm:related>
                        </mm:node>
                      </select>
                    </td>
                    <td>
                      <input type="submit" value="<di:translate key="register.choose" />" />
                    </td>
                  </form>
                </tr>
                <mm:last>
                  </table>
                </mm:last>
              </mm:related>
            </mm:node>
          </mm:node>
          </mm:isnotempty>
        </mm:isnotempty>
        <mm:isempty referid="person">
          <mm:node number="$educationid">
            <di:translate key="register.chooseregistration" /><br />
            <hr />
            <mm:relatednodes type="people" role="related">
              <mm:first>
                <table class="listTable">
                  <tr>
                    <th><di:translate key="register.number" /></th>
                    <th><di:translate key="register.name" /></th>
                    <th><di:translate key="register.username" /></th>
                    <th><di:translate key="register.delete" /></th>
                  </tr>
              </mm:first>
              <mm:field name="number" id="person" write="false"/>
              <mm:treefile page="/register/wizards/register.jsp" objectlist="$includePath" referids="$referids,educationid,person" id="url" write="false" />
              <tr>
                <td>
                  <a href="<mm:write referid="url" escape="none" />"><mm:field name="number" /></a>
                </td>
                <td>
                  <a href="<mm:write referid="url" escape="none" />"><mm:field name="firstname" /> <mm:field name="lastname" /></a>
                </td>
                <td>
                  <a href="<mm:write referid="url" escape="none" />"><mm:field name="username" /></a>
                </td>
                <td>
                  <a href="<mm:url referid="url"><mm:param name="delete">true</mm:param></mm:url>"><di:translate key="register.delete" /></a>
                </td>
              </tr>
              <mm:remove referid="person" />
            </mm:relatednodes>
          </mm:node>
        </mm:isempty>  
      </mm:islessthan>
    </div>
  </body>
</html>
</mm:cloud>
</mm:content>
