<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:content type="text/html" escaper="none"><mm:cloud>

<mm:import externid="pack" />
<mm:import externid="page" />
<mm:import externid="portal" />

<mm:node number="$pack"> 
<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%" border="0">
<tr class="layout">
  <td><a href="<mm:url page="index.jsp" referids="page?,portal?" />"><img src="/media/arrow-left.gif" alt="&lt;-" width="15" height="20" style="border:0;" /> back</a></td>
</tr><tr>
  <td width="50%" align="middle" valign="top">
  <table cellpadding="0" cellspacing="0" class="list" style="font-size: 1.4em;">
  <tr>
    <th colspan="2" width="225">Bundle Information</th>
  </tr><tr>
    <th>Name</th>
    <td><mm:field name="name" /> &nbsp;</td>
  </tr><tr>
    <th>Type</th>
    <td><mm:related path="related,packagetypes"><mm:field name="packagetypes.name" /></mm:related> &nbsp;</td>
  </tr><tr>
    <th>Version</th>
    <td><mm:field name="versionnumber" /> &nbsp;</td>
  </tr><tr>
    <th>Maintainer</th>
    <td>
    <mm:related path="rolerel,organisation" 
      fields="rolerel.role,organisation.name"
      constraints="rolerel.role='maintainer'" >
      <mm:first inverse="true">, </mm:first>
      <mm:field name="organisation.name" />
      <mm:import id="is_shown" reset="true" /> 
    </mm:related> 
    <mm:related path="rolerel,persons" 
      fields="rolerel.role,persons.firstname,persons.suffix,persons.lastname,persons.email"
      constraints="rolerel.role='maintainer'" >
      <mm:present referid="is_shown">, </mm:present>
      <mm:remove referid="is_shown" />
      <mm:first inverse="true">, </mm:first> 
      <mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" /> 
    </mm:related> &nbsp;
    </td>
  </tr><tr>
    <th>Creation-Date</th>
    <td>
    <mm:field name="creationdate" id="package_creationdate">
        <mm:time referid="package_creationdate" format="d MMMM yyyy"/>
    </mm:field>
    </td>
  </tr>
  </table>
  </td>
  <td width="50%" align="middle" valign="top">
  <table cellpadding="0" cellspacing="0" class="list" style="font-size: 1.4em; margin-left: 10px;">
  <tr>
    <th colspan="2" width="225">Bundle Actions</th>
  </tr>
  <mm:relatednodes type="urls">   
    <tr>
      <th><mm:field name="name" /></th>
      <td><a href="<mm:field name="url" />">Download <img src="/media/arrow-right.gif" alt="->" width="15" height="20" style="border:0;" /></a></td>
    </tr>
  </mm:relatednodes>
  <mm:relatednodes type="attachments">
    <tr>
      <th><mm:field name="title" /></th>
      <td><a href="<mm:attachment />">Download <img src="/media/arrow-right.gif" alt="->" width="15" height="20" style="border:0;" /></a></td>
    </tr>
  </mm:relatednodes>
  </table>
  </td>
</tr>
</table>

<table cellpadding="0" cellspacing="0" class="list" style="font-size: 1.2em; margin-top: 10px;" width="95%">
<tr>
  <th colspan="2">Bundle overview and creator information</th>
</tr><tr>
  <td colspan="2" valign="top">
    <p><strong>Bundle Description</strong></p>
    <p><mm:field name="description"/></p>
  </td>
</tr><tr>
  <td valign="top">
  <p><strong>Initiators</strong></p>
  <p>
  <mm:related path="rolerel,organisation"
    fields="rolerel.role,organisation.name"
    constraints="rolerel.role='initiator'">
    Company: <mm:field name="organisation.name" />
    <mm:last inverse="true"><br /></mm:last>
    <mm:import id="is_shown" reset="true" /> 
  </mm:related>                       
  <mm:related path="rolerel,persons"
    fields="rolerel.role"
    constraints="rolerel.role='initiator'" >
    <mm:present referid="is_shown"><br /></mm:present><mm:remove referid="is_shown" />
    <mm:node element="persons">
    Name: <mm:field name="firstname" /> <mm:field name="suffix" /> <mm:field name="lastname" />
    <mm:related path="rolerel,organisation">
      <br />Company: <mm:field name="organisation.name" />
    </mm:related>
    </mm:node>
    <mm:last inverse="true"><br /></mm:last>                        
  </mm:related>
  </p>
  </td>
  <td valign="top" align="left">
  <p><strong>Licence info</strong></p>
  <p>
  <mm:relatednodes type="licenceinfos">
    <mm:field name="type">
      <mm:fieldinfo type="description" />: <mm:write /><br />
    </mm:field>
    <mm:field name="name">
      <mm:fieldinfo type="description" />: <mm:write /><br />
    </mm:field>
    <mm:field name="version">
      <mm:fieldinfo type="description" />: <mm:write />
    </mm:field>                                     
  </mm:relatednodes>
  </p>
  </td>
</tr><tr>
  <td valign="top" width="50%">
  <p><strong>Supporters</strong></p>
  <p>
  <mm:related path="rolerel,organisation"
    fields="rolerel.role,organisation.name"
    constraints="rolerel.role='supporter'">
    <mm:field name="organisation.name" />
    <mm:last inverse="true"><br /></mm:last>
    <mm:import id="is_shown" reset="true" /> 
  </mm:related>       
  <mm:related path="rolerel,persons"
    fields="rolerel.role,persons.firstname,persons.suffix,persons.lastname"
    constraints="rolerel.role='supporter'">
    <mm:present referid="is_shown"><br /></mm:present>
    <mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" />
    <mm:last inverse="true"><br /></mm:last> 
  </mm:related>
  </p>
  <mm:remove referid="is_shown" />
  </td>
  <td valign="top" width="50%">
  <p><strong>Contact info</strong></p>
  <p>
  <mm:related path="rolerel,persons"
    fields="rolerel.role,persons.firstname,persons.suffix,persons.lastname,persons.email"
    constraints="rolerel.role='questions'">
    <mm:first>Questions:</mm:first>
    <mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" />
    <mm:field name="persons.email"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field>
    <mm:last inverse="true">,</mm:last><mm:last><br /></mm:last>
  </mm:related>
  <mm:related path="rolerel,persons"
    fields="rolerel.role,persons.firstname,persons.suffix,persons.lastname,persons.email"
    constraints="rolerel.role='bugs'">
    <mm:first>Bugreports:</mm:first>
    <mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" />
    <mm:field name="persons.email"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field>
    <mm:last inverse="true">,</mm:last><mm:last><br /></mm:last>
  </mm:related>
  <mm:related path="rolerel,persons"
    fields="rolerel.role,persons.firstname,persons.suffix,persons.lastname,persons.email"
    constraints="rolerel.role='support'">
    <mm:first>Sponsor/Support questions:</mm:first>
    <mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" />
    <mm:field name="persons.email"><mm:isnotempty>(<mm:write />)</mm:isnotempty></mm:field>
    <mm:last inverse="true">,</mm:last>    
  </mm:related>
  </p>
  </td>
</tr><tr>
  <td colspan="2" valign="top">
  <p><strong>Developers who have worked on this release</strong></p>
  <p>
  <mm:related path="rolerel,organisation" 
    constraints="rolerel.role='developer'"
    fields="rolerel.role,organisation.name">
    <mm:field name="organisation.name" />
    <mm:last inverse="true"><br /></mm:last>
    <mm:import id="is_shown" reset="true" />  
  </mm:related>       
  <mm:related path="rolerel,persons"
    fields="rolerel.role,persons.firstname,persons.suffix,persons.lastname"
    constraints="rolerel.role='developer'">
      <mm:present referid="is_shown"><br /></mm:present>
      <mm:remove referid="is_shown" /> 
      <mm:field name="persons.firstname" /> <mm:field name="persons.suffix" /> <mm:field name="persons.lastname" />
      <mm:node element="persons">      
        <mm:related path="rolerel,organisation"
          fields="organisation.name">
          from <mm:field name="organisation.name" />
        </mm:related>
      </mm:node>
      <mm:last inverse="true"><br /></mm:last>                        
  </mm:related> 
  </p>
  </td>
</tr>
</table>
</mm:node>

</mm:cloud></mm:content>
