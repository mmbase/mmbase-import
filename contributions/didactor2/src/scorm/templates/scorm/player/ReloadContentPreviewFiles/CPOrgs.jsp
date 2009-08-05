<%@page import=" nl.didactor.component.scorm.player.*,java.io.*,java.util.*" 
contentType="text/javascript; charset=UTF-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" 
%><mm:content postprocessor="reducespace" expires="0" type="text/javascript">
<mm:cloud>
  <mm:node number="${param.number}" jspvar="packageNode">

    <%
     List<Integer> subs = new ArrayList<Integer>();
    %>
    <mm:relatednodes type="learnblocks" role="posrel" searchdir="source">
      <mm:field name="path">
        <mm:compare  value="" inverse="true">          
          <mm:tree type="learnblocks" role="posrel" searchdir="source">
            <mm:field name="path">
              <mm:compare value="" inverse="true">
                <mm:write jspvar="sStep" vartype="String">
                  <%
                  String[] arrstrStep = sStep.split("-");
                  subs.add(Integer.parseInt(arrstrStep[1]));
                  %>
                </mm:write>
              </mm:compare>
            </mm:field>
          </mm:tree>
        </mm:compare>
      </mm:field>
    </mm:relatednodes>

    <%


      MenuCreator menuCreator = new MenuCreator(packageNode);
      String[] arrstrJSMenu = menuCreator.parse(true, subs.toArray(new Integer[] {}));
      for(int f = 0; f < arrstrJSMenu.length; f++) {
        out.println(arrstrJSMenu[f]);
      }
      %>
  </mm:node>
</mm:cloud>
    
</mm:content>