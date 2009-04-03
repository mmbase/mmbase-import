<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
  <%@include file="/shared/setImports.jsp" %>
  <mm:import externid="wizardjsp"/>
  <mm:import id="number_of_sessions" reset="true">0</mm:import>
  <mm:listnodes type="virtualclassroomsessions">
    <mm:import id="number_of_sessions" reset="true"><mm:size /></mm:import>
  </mm:listnodes>
  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
      <%// We have to detect the last element %>
      <mm:isgreaterthan referid="number_of_sessions" value="0">
        <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
      </mm:isgreaterthan>
      <mm:islessthan referid="number_of_sessions" value="1">
        <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
      </mm:islessthan>
      <td><img src="gfx/new_education.gif" width="16" border="0" align="middle" /></td>
      <td><nobr>&nbsp;<a href='<mm:write referid="wizardjsp"/>&wizard=config/virtualclassroom/virtualclassroomsessions&objectnumber=new' title='<di:translate key="virtualclassroom.createnewsessiondescription" />' target="text"><di:translate key="virtualclassroom.createnewsession" /></a></nobr></td>
    </tr>
  </table> 
  <mm:listnodes type="virtualclassroomsessions">
    <table border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
        <mm:last inverse="true">
          <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="middle"/></td>
        </mm:last>
        <mm:last>
          <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>
        </mm:last>        
        <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
        <td><nobr><a href='<mm:write referid="wizardjsp"/>&wizard=config/virtualclassroom/virtualclassroomsessions&objectnumber=<mm:field name="number" />' title='<mm:field name="name" />' target="text"><mm:field name="name" /></a></nobr></td>
      </tr>
    </table>         
  </mm:listnodes>             
</mm:cloud>
</mm:content>			           
