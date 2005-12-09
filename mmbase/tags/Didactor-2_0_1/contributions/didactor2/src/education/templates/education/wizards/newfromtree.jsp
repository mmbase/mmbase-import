<%@ page import = "java.util.HashSet" %>

<mm:remove referid="type_of_node"/>
<mm:nodeinfo id="type_of_node" type="type" jspvar="sNodeType" vartype="String">

   <mm:compare referid="type_of_node" value="learnblocks">
      <table border="0" cellpadding="0" cellspacing="0">
         <tr>
            <mm:compare referid="the_last_parent" value="true" inverse="true">
               <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
            </mm:compare>
            <mm:compare referid="the_last_parent" value="true">
               <td><img src="gfx/tree_spacer.gif" width="48px" height="16px" border="0" align="center" valign="middle"/></td>
            </mm:compare>


            <%@include file="tree_shift_child.jsp" %>

            <mm:import id="dummyname" jspvar="dummyName" vartype="String" reset="true"><mm:nodeinfo nodetype="learnblocks" type="guitype"/></mm:import>
            <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
            <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
            <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/learnblocks/learnblocks-origin&objectnumber=new&origin=<mm:field name="number"/>' title='<di:translate key="education.new" /> <%= dummyName.toLowerCase() %>' target="text"><di:translate key="education.new" /> <%= dummyName.toLowerCase() %></a></nobr></td>
         </tr>
      </table>
      <table border="0" cellpadding="0" cellspacing="0">
         <tr>
            <mm:compare referid="the_last_parent" value="true" inverse="true">
               <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
            </mm:compare>
            <mm:compare referid="the_last_parent" value="true">
               <td><img src="gfx/tree_spacer.gif" width="48px" height="16px" border="0" align="center" valign="middle"/></td>
            </mm:compare>


            <%@include file="tree_shift_child.jsp" %>

            <mm:import id="dummyname" jspvar="dummyName" vartype="String" reset="true"><mm:nodeinfo nodetype="pages" type="guitype"/></mm:import>
            <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
            <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
            <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/pages/pages-origin&objectnumber=new&origin=<mm:field name="number"/>' title='<di:translate key="education.new" /> <%= dummyName.toLowerCase() %>' target="text"><di:translate key="education.new" /> <%= dummyName.toLowerCase() %></a></nobr></td>
         </tr>
      </table>
      <table border="0" cellpadding="0" cellspacing="0">
         <tr>
            <mm:compare referid="the_last_parent" value="true" inverse="true">
               <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
            </mm:compare>
            <mm:compare referid="the_last_parent" value="true">
               <td><img src="gfx/tree_spacer.gif" width="48px" height="16px" border="0" align="center" valign="middle"/></td>
            </mm:compare>


            <%@include file="tree_shift_child.jsp" %>

            <mm:import id="dummyname" jspvar="dummyName" vartype="String" reset="true"><mm:nodeinfo nodetype="tests" type="guitype"/></mm:import>
            <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
            <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
            <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/tests/tests-origin&objectnumber=new&origin=<mm:field name="number"/>' title='<di:translate key="education.new" /> <%= dummyName.toLowerCase() %>' target="text"><di:translate key="education.new" /> <%= dummyName.toLowerCase() %></a></nobr></td>
         </tr>
      </table>
      <table border="0" cellpadding="0" cellspacing="0">
         <tr>
            <mm:compare referid="the_last_parent" value="true" inverse="true">
               <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
            </mm:compare>
            <mm:compare referid="the_last_parent" value="true">
               <td><img src="gfx/tree_spacer.gif" width="48px" height="16px" border="0" align="center" valign="middle"/></td>
            </mm:compare>


            <%@include file="tree_shift_child.jsp" %>

            <mm:import id="dummyname" jspvar="dummyName" vartype="String" reset="true"><mm:nodeinfo nodetype="flashpages" type="guitype"/></mm:import>
            <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
            <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
            <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/flashpages/flashpages-origin&objectnumber=new&origin=<mm:field name="number"/>' title='<di:translate key="education.new" /> <%= dummyName.toLowerCase() %>' target="text"><di:translate key="education.new" /> <%= dummyName.toLowerCase() %></a></nobr></td>
         </tr>
      </table>
      <table border="0" cellpadding="0" cellspacing="0">
         <tr>
            <mm:compare referid="the_last_parent" value="true" inverse="true">
               <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
            </mm:compare>
            <mm:compare referid="the_last_parent" value="true">
               <td><img src="gfx/tree_spacer.gif" width="48px" height="16px" border="0" align="center" valign="middle"/></td>
            </mm:compare>


            <%@include file="tree_shift_child.jsp" %>

            <mm:import id="dummyname" jspvar="dummyName" vartype="String" reset="true"><mm:nodeinfo nodetype="htmlpages" type="guitype"/></mm:import>
            <mm:compare referid="the_last_element" value="true" inverse="true">
               <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
            </mm:compare>
            <mm:compare referid="the_last_element" value="true">
               <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
            </mm:compare>
            <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
            <td>&nbsp;<nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/htmlpages/htmlpages-origin&objectnumber=new&origin=<mm:field name="number"/>' title='<di:translate key="education.new" /> <%= dummyName.toLowerCase() %>' target="text"><di:translate key="education.new" /> <%= dummyName.toLowerCase() %></a></nobr></td>
         </tr>
      </table>
   </mm:compare>

</mm:nodeinfo>
