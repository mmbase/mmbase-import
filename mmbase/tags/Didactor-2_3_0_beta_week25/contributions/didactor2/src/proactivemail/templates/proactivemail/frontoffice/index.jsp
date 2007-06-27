<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  
  <script type="text/javascript">
  
  function executeScript(name, link, question) {
    var answer = confirm (question + " '"+ name +"'?")
    if ( answer == true ) 
      document.location.href = link;
  }
  
  </script>
  
  <mm:import id="scriptname" externid="scriptname" jspvar="jsp_scriptname" required="false"/>
  <mm:import id="scriptcode" jspvar="jsp_scriptcode"/>
  
  <mm:import id="scriptexecuteresult" jspvar="jsp_scriptexecuteresult"/>
  <mm:import id="scriptsuccedd" jspvar="jsp_scriptsucceed"><di:translate key="proactivemail.executesuccess"/></mm:import>
  <mm:import id="scriptbad" jspvar="jsp_scriptbad"><di:translate key="proactivemail.executebad"/></mm:import>
  <% 
  if ( jsp_scriptname != null && jsp_scriptname.length() > 0 ) {
  %>  
    <mm:listnodescontainer type="proactivemailscripts">
      <mm:constraint operator="LIKE" field="name" referid="scriptname" />
      <mm:listnodes>
        <mm:import id="scriptcode" jspvar="jsp_scriptcode" reset="true">
          <mm:field name="code" write="true" escape="text/plain"/>            
        </mm:import>
      </mm:listnodes>
    </mm:listnodescontainer>
  <%    
    if ( jsp_scriptcode != null && jsp_scriptcode.length() > 0 ) {
      try {
        jsp_scriptexecuteresult = jsp_scriptsucceed; 
        org.mozilla.javascript.Context cx = org.mozilla.javascript.Context.enter();
        org.mozilla.javascript.Scriptable scope = cx.initStandardObjects();
        Object result = cx.evaluateString(scope, jsp_scriptcode, jsp_scriptname, 0, null);
        if ( result != null && result.getClass() == Boolean.class && ((Boolean)result).booleanValue() == false )
            jsp_scriptexecuteresult = jsp_scriptbad; 
        org.mozilla.javascript.Context.exit();
      } catch (Exception e) {
          jsp_scriptexecuteresult = jsp_scriptbad; 
      }
    }
  }
  %>
  
  <mm:treeinclude page="/cockpit/cockpit_header.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="extraheader">
      <title><di:translate key="proactivemail.proactivemail"/></title>
      <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" referids="$referids" />" />
    </mm:param>
  </mm:treeinclude>  
  <div class="rows">
    <div class="navigationbar">
      <div class="titlebar">
        <di:translate key="proactivemail.proactivemail"/>
      </div>
    </div>
    <div class="contentBody">
      <b style="color:red;font-size:1.2em;"><%=jsp_scriptexecuteresult%></b>
      <br/><br/>
      <table >
        <tr>
            <td></td>            
            <td><di:translate key="proactivemail.scriptname"/></td>            
            <td></td>            
            <td><di:translate key="proactivemail.runscript"/></td>            
        </tr>
        <tr style="height:1px;background-color:black;"> <td colspan="4"/></tr>
        <% int counter = 1; %>
        <mm:listnodes path="proactivemailscripts" orderby="name">
          <tr>
            <td><%=counter%>.</td>            
            <td><b><mm:field name="name" write="true" /></b></td>            
            <td></td>            
            <td align="center">
              <mm:import id="href" reset="true" escape="text/plain"><mm:treefile escape="text/plain" page="/proactivemail/frontoffice/index.jsp" objectlist="$includePath" referids="$referids"><mm:param name="scriptname"><mm:field name="name"/></mm:param></mm:treefile></mm:import>
              <a href="javascript:executeScript('<mm:field name="name"/>', '<mm:write referid="href" escape="text/plain"/>', '<di:translate key="proactivemail.executequestion" />')">
                <img src="<mm:treefile write="true" page="/gfx/icon_arrow_next.gif" objectlist="$includePath" />" 
                  width="14" 
                  height="14" 
                  border="0"   
                  title="<di:translate key="proactivemail.runscript"/>" 
                  alt="<di:translate key="proactivemail.runscript"/>" />
               </a>
            </td>            
          </tr>
          <% counter++; %>
        </mm:listnodes>
      </table>
    </div>
  </div>
  <mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids" />
</mm:cloud>
</mm:content>
