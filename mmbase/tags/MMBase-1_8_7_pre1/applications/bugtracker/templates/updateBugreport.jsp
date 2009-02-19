<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:cloud method="asis">
<%@include file="parameters.jsp" %>
<%@include file="login.jsp" %>
<mm:import externid="bugreport" required="true"/>
<%--
 MM: mm:fieldinfo should be able to _considerably_ simplify this jsp
--%>


<form action="<mm:url referids="parameters,$parameters,bugreport"><mm:param name="btemplate">fullview.jsp</mm:param><mm:param name="flap">change</mm:param></mm:url>" method="POST">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%"><!-- ugly ugly -->

<mm:node number="$bugreport">
<tr>
  <th>Type</th>
  <th>Priority</th>
  <th>Status</th>
</tr>
<tr>
  <td>
    <mm:field name="btype">
      <select name="newbtype">
        <!-- sigh, we need a field-type here -->
        <option value="1" <mm:compare value="1">selected="selected"</mm:compare>>bug</option>
        <option value="2" <mm:compare value="2">selected="selected"</mm:compare>>wish</option>
        <option value="3" <mm:compare value="3">selected="selected"</mm:compare>>docbug</option>
        <option value="4" <mm:compare value="4">selected="selected"</mm:compare>>docwish</option>
      </select>
     </mm:field>
  </td>
  <td>
    <mm:field name="bpriority">
      <select name="newbpriority">
        <option value="1" <mm:compare value="1">selected="selected"</mm:compare>>high</option>
        <option value="2" <mm:compare value="2">selected="selected"</mm:compare>>medium</option>
        <option value="3" <mm:compare value="3">selected="selected"</mm:compare>>low</option>
      </select>
     </mm:field>
  </td>
  <td>
    <mm:field name="bstatus">
      <select name="newbstatus">
        <option value="1" <mm:compare value="1">selected="selected"</mm:compare>>open</option>
        <option value="2" <mm:compare value="2">selected="selected"</mm:compare>>accepted</option>
        <option value="3" <mm:compare value="3">selected="selected"</mm:compare>>rejected</option>
        <option value="4" <mm:compare value="4">selected="selected"</mm:compare>>pending</option>
        <option value="5" <mm:compare value="5">selected="selected"</mm:compare>>integrated</option>
        <option value="6" <mm:compare value="6">selected="selected"</mm:compare>>closed</option>
      </select>
     </mm:field>
  </td>
</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr>
  <th>Version</th>
  <th>Area</th>
  <th>Expected fixed in</th>
  <th>Fixed in</th>
</tr>
<tr>
  <td>
    <input name="newversion" value="<mm:field name="version" />" size="10" />
  </td>
  <td>
  <select name="newarea">
    <mm:relatednodes type="areas" max="1">
      <option value="<mm:field name="number" />"><mm:field name="substring(name,15,.)" /></option>
    </mm:relatednodes>
    <mm:listnodes type="areas" orderby="name" >
      <option value="<mm:field name="number" />"><mm:field name="substring(name,15,.)" /></option>
    </mm:listnodes>
  </select>
  </td>
  <td>
    <input name="newefixedin" value="<mm:field name="efixedin" />" size="10" />
  </td>
  <td>
    <input name="newfixedin" value="<mm:field name="fixedin" />" size="10" />
  </td>
</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr><th colspan="5">Issue : give the issue in one line </th></tr>
<tr>
  <td colspan="5">
    &nbsp;&nbsp;<input size="70" name="newissue" value="<mm:field name="issue" escape="text/html/attribute"/>"  />
  </td>
</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr><th colspan="5">Description : Describe the issue as complete as possible </th></tr>
<tr>
  <td colspan="5">
    <textarea name="newdescription" cols="70" rows="15" wrap="wrap"><mm:field name="description" escape="text/html"/></textarea>
  </td>
</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr><th colspan="5">Rationale : explains the actions made by the maintainer</th></tr>
<tr>
  <td colspan="5">
    <textarea name="newrationale" cols="70" rows="15" wrap="wrap"><mm:field name="rationale" /></textarea>
  </td>
</tr>
</table>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="98%">
<tr>
  <th colspan="2">Submitter</th>
  <th colspan="2">&nbsp;</th>
</tr>
<tr>
  <td colspan="2">
    <mm:related path="insrel,areas" max="1">
      <input name="oldarea" type="hidden" value="<mm:field name="number.areas" />" />
      <input name="oldarearel" type="hidden" VALUE="<mm:field name="insrel.number" />" />
    </mm:related>
</mm:node>

<mm:node referid="user">

  <input name="updater" type="hidden" value="<mm:field name="number" />" />
  <input name="bugreport" type="hidden" value="<mm:write referid="bugreport" />" />
  &nbsp;&nbsp;
  <mm:field name="firstname" />
  <mm:field name="lastname" />
  ( <mm:field name="email" /> )
</mm:node>
    </td>
    <td colspan="2">
      <input type="hidden" name="action" value="updatebug" />
      <input type="submit" value="submit update" />
    </td>
</tr>
</table>


</mm:cloud>
</form>