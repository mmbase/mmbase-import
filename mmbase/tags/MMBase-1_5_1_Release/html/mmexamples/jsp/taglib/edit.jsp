<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><html>
<body>
<%@ include file="menu.jsp"%>

<mm:cloud method="http">
<mm:context> <!-- the explicit use of context tag is only necessary in orion 1.5.2 -->

<mm:import externid="number" required="true" />
<mm:import externid="ok" />

<mm:node number="${number}">

<h1>Simple editor (<mm:field name="gui()" />)</h1>
<p>
This is an example of how one could make a simple editor with the
mmbase-taglib. It must be called with a `number' parameter. The page
has two appearances (with the use of the `present' Tag), one with a
form, and one which processes the form.
</p>

<mm:notpresent referid="ok">
  <h2>Creating a form</h2>
  <p>
    A form can easily be made with the use of the `fieldlist' and the
    `fieldinfo' tag. With the `fieldlist' tag a list of all the fields
    of this node can be generated, with the sub-tag `fieldinfo' then
    we show the name of the field, and create a form entry for it.
  </p>
  <form method="post" action="<mm:url referids="number" />">
  Node of type <mm:nodeinfo type="guinodemanager" /><br />
  <table> 
   <mm:fieldlist type="edit">
     <tr>
     <td><em><mm:fieldinfo type="guiname" /></em></td>
     <td><mm:fieldinfo type="input" /></td>
     </tr>
    </mm:fieldlist> 
   </table>
   <input class="submit"  type ="submit" name="ok" value="ok" />
   </form>
</mm:notpresent>

<mm:present referid="ok">
   <h2>Handling a form</h2>
   <p>
     To handle a form that was created with the `fieldinfo' tag, you
     should also use the fieldinfo tag.
   </p>
   <mm:context id="new">
   <mm:fieldlist type="edit">
      <mm:fieldinfo type="useinput" />
   </mm:fieldlist>  
   </mm:context>
   <p>
     Having done that, we show the new values.
   </p>
   <mm:fieldlist type="edit">
      <em><mm:fieldinfo type="guiname" /></em>: <mm:fieldinfo type="guivalue" /><br />
   </mm:fieldlist>
   <hr />
   <a href="<mm:url referids="number" />">back to editor</a>
</mm:present>

</mm:node>

</mm:context>
</mm:cloud>
</body>
</html>