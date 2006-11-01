<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate">
<jsp:directive.include file="/shared/setImports.jsp" />
<jsp:directive.include file="roles_defs.jsp" />

<mm:import id="wizardlang">en</mm:import>
<mm:compare referid="language" value="nl">
  <mm:import id="wizardlang" reset="true">nl</mm:import>
</mm:compare>

<mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" />?referrer=/education/wizards/ok.jsp&language=<mm:write referid="wizardlang" /></mm:import>
<mm:import externid="command">-1</mm:import>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
  <html>
    <head>
      <title>Roles editen</title>
    </head>
    <style type="text/css">
      table.tightborder { 
        border-color: #000000;
        border-style: solid;
        border-left-width: 1px;
        border-top-width: 1px;
        border-right-width: 0px;
        border-bottom-width: 0px;
      }
      td.tightborder { 
        border-color: #000000;
        border-style: solid;
        border-left-width: 0px;
        border-top-width: 0px;
        border-right-width: 1px;
        border-bottom-width: 1px;
      }
    </style>
    <script type="text/javascript" src="<mm:treefile page="/editwizards/javascript/list.js" objectlist="$includePath" referids="$referids"/>"></script>
    <body>
      <mm:import id="editcontextname" reset="true">rollen</mm:import>
      <jsp:directive.include file="roles_chk.jsp" />
      <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RW">
        <form name="roleform" action="<mm:treefile page="/education/wizards/roles_cmd.jsp" objectlist="$includePath" referids="$referids"/>" method="post">
          <input type="hidden" name="command" value="-1">
          <mm:import id="numofroles" jspvar="numOfRoles" vartype="Integer">0</mm:import>
          <table class="tightborder" border="1" cellpadding="0" cellspacing="0">
            <tr align="center">
              <td class="tightborder">&nbsp;</td>
              <mm:listnodes type="roles" orderby="name">
                <mm:import id="numofroles" jspvar="numOfRoles" vartype="Integer" reset="true"><mm:size/></mm:import>
                <mm:field name="name" jspvar="name" vartype="String">
                  <% name  = name.replaceAll("\\s+","_").replaceAll("\"","''"); %>
                  <mm:import id="template" reset="true">s(150!x30!)+font(mm:fonts/didactor.ttf)+fill(000000)+pointsize(13)+gravity(NorthWest)+text(0,20,"<%= name %>")+rotate(-90)</mm:import>
                </mm:field>
                <td class="tightborder">
                  <mm:node number="progresstextbackground">
                    <img src="<mm:image template="$template"/>">
                  </mm:node>
                </td>
              </mm:listnodes>
              <td class="tightborder" width="30">&nbsp;</td>
            </tr>
            <tr align="center" valign="middle" height="25">
              <td class="tightborder">&nbsp;</td>
              <mm:islessthan inverse="true" referid="rights" referid2="RIGHTS_RWD">
                <mm:listnodes type="roles" orderby="name">
                  <td class="tightborder"><a href="<mm:treefile page="/education/wizards/roles_cmd.jsp" objectlist="$includePath" referids="$referids">
                                                     <mm:param name="command">deleterole</mm:param>
                                                     <mm:param name="rolenumber"><mm:field name="number"/></mm:param>
                                                   </mm:treefile>" onClick="return doDelete('<di:translate key="education.areyousuredelrole" />');" 
                                              target="text"><img src="<mm:treefile page="/education/wizards/gfx/minus.gif" 
                                                                          objectlist="$includePath" referids="$referids"/>" border="0"
                                                                title="<di:translate key="education.deletethisrole"/>" alt="<di:translate key="education.deletethisrole"/>" /></a></td>
                </mm:listnodes>
              </mm:islessthan>
              <mm:islessthan referid="rights" referid2="RIGHTS_RWD">
                <% for(int i=0; i<numOfRoles.intValue();i++) { %>
                     <td class="tightborder">&nbsp;</td>
                <% } %>
              </mm:islessthan>
              <td class="tightborder"><a href='<mm:write referid="wizardjsp"/>&wizard=config/role/roles&objectnumber=new' target="text"><img src="<mm:treefile page="/education/wizards/gfx/plus.gif" objectlist="$includePath" referids="$referids"/>" border="0"
                                          title="<di:translate key="education.createnewrole"/>" alt="<di:translate key="education.createnewrole"/>" /></a></td>
            </tr>
            <% String sSelectName = ""; %>
            <mm:listnodes type="editcontexts" orderby="number">
              <mm:field name="number" id="this_editcontext" jspvar="dummy" vartype="String">
                <% sSelectName = "select_" + dummy + "_"; %>
              </mm:field>
              <tr>
                <td class="tightborder"><mm:field name="name"/></td>
                <mm:listnodes type="roles" orderby="name">
                  <% String sSelectFullName = ""; %>
                  <mm:field name="number" jspvar="dummy" vartype="String">
                    <% sSelectFullName = sSelectName + dummy; %>
                  </mm:field>
                  <mm:import id="rights" reset="true">0</mm:import>
                  <mm:related path="posrel,editcontexts" constraints="editcontexts.number='$this_editcontext'">
                    <mm:import id="rights" reset="true"><mm:field name="posrel.pos"/></mm:import>
                  </mm:related>
                  <td class="tightborder">
                    <select name="<%= sSelectFullName%>">
                      <option value="0" style="background-color:#FF3300" <mm:compare referid="rights" referid2="RIGHTS_NO">selected</mm:compare>></option>
                      <option value="2" style="background-color:#FFFF00" <mm:compare referid="rights" referid2="RIGHTS_RW">selected</mm:compare>>rw</option>
                      <option value="3" style="background-color:#33FF00" <mm:compare referid="rights" referid2="RIGHTS_RWD">selected</mm:compare>>rwd</option>
                    </select>
                  </td>
                </mm:listnodes>
                <td class="tightborder">&nbsp;</td>
              </tr>
            </mm:listnodes>
          </table>
        </form>
        <span style="background-color:#33FF00">rwd</span> = <di:translate key="education.abbreviationrwd" /><br/>
        <span style="background-color:#FFFF00">rw&nbsp;</span> = <di:translate key="education.abbreviationrw" /><br/>
        <br/>
        <input type="button" class="formbutton" onClick="roleform.command.value='accept';roleform.submit()" value="<di:translate key="education.save" />">
        <input type="button" class="formbutton" onClick="roleform.reset()" value="<di:translate key="education.reset" />">
      </mm:islessthan>
    </body>
  </html>
</mm:cloud>
</mm:content>
