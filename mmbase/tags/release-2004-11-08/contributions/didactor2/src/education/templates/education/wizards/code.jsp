<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
	<%@include file="/shared/setImports.jsp"%>

<mm:import externid="showcode">false</mm:import>
<mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" /></mm:import>

<mm:compare referid="showcode" value="true" inverse="true">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title></title>

<script type="text/javascript" src="<mm:treefile page="/education/wizards/mtmcode.jsp" objectlist="$includePath" referids="$referids" write="true"/>"></script>

<script type="text/javascript">
// Framebuster script to relocate browser when MSIE bookmarks this
// page instead of the parent frameset.  Set variable relocateURL to
// the index document of your website (relative URLs are ok):
//var relocateURL = "<mm:treefile write="true" page="/education/wizards/index.jsp" objectlist="$includePath" referids="$referids" />";

//if (parent.frames.length == 0)
//{
//	if(document.images)
//	{
//    	location.replace(relocateURL);
//	}
//	else
//	{
//    	location = relocateURL;
//    }
//}
</mm:compare>
<mm:compare referid="showcode" value="true">
  <mm:content type="text/plain" />
</mm:compare>

// Morten's JavaScript Tree Menu
// version 2.3.2-macfriendly, dated 2002-06-10
// http://www.treemenu.com/

// Copyright (c) 2001-2002, Morten Wang & contributors
// All rights reserved.

// This software is released under the BSD License which should accompany
// it in the file "COPYING".  If you do not have this file you can access
// the license through the WWW at http://www.treemenu.com/license.txt
// Nearly all user-configurable options are set to their default values.
// Have a look at the section "Setting options" in the installation guide
// for description of each option and their possible values.

MTMDefaultTarget = "text";
MTMenuText = "<di:translate id="educations">Educations</di:translate>";
MTMSubsGetPlus = "always";
MTMUseCookies = "false";
MTMCookieName = "MTMCookie";
MTMTrackedCookieName = "MTMTracked";
MTMCookieDays = "7";
MTMUA.preHREF = "";
MTMenuImageDirectory = "";
var MTMIconList = new IconList();

<% int treeCount = 0; %>
<% int metatreeCount = 0; %>

// testing...
//MTMSubsAutoClose = true;

// Main menu.
var menu = new MTMenu();
<di:hasrole role="courseeditor">
	menu.addItem("<di:translate id="createNewEducation">Create new education</di:translate>",
    	         "<mm:write referid="wizardjsp"/>?wizard=educations&objectnumber=new",
        	     null,
            	 "<di:translate id="createNewEducationDescription">Create a new education</di:translate>",
	             "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
</di:hasrole>

<di:hasrole role="filemanager">
	menu.addItem("<di:translate id="filemanagement">Filemanagement</di:translate>",
    	         "<mm:treefile write="true" page="/education/filemanagement/index.jsp" objectlist="$includePath" />",
        	     null,
            	 "<di:translate id="filemanagement">Filemanagement</di:translate>",
	          "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
</di:hasrole>



<di:hasrole role="systemadministrator">
	menu.addItem("<di:translate id="editComponents">Componenten editor</di:translate>",
    	         "<mm:treefile write="true" page="/components/index.jsp" objectlist="$includePath" />",
        	     "_top",
            	 "<di:translate id="editComponentsDescription">Ga naar de componenten editor</di:translate>",
	             "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
menu.addItem("<di:translate id="Roles">Roles</di:translate>","",null,"");
var rolestree = new MTMenu();
<%-- edit people -- rolerel -- education --%>
<%-- doesn't work properly, so commented it out for the moment
    rolestree.addItem("<di:translate id="editPeopleRoleRelEducation">Add/Edit roles for educations</di:translate>",
     	         "<mm:treefile write="true" page="/education/wizards/roles.jsp" objectlist="$includePath" />",
        	     null,
            	 "<di:translate id="editPeopleRoleRelEducationDescription">Edit roles</di:translate>",
	             "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
--%>
<%-- create new role --%>
    rolestree.addItem("<di:translate id="createNewRoles">Create new rol</di:translate>",
     	         "<mm:write referid="wizardjsp"/>?wizard=roles&objectnumber=new",
        	     null,
            	 "<di:translate id="createNewRolesDescription">Create a new rol</di:translate>",
	             "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");

<%-- edit existing roles --%>
<mm:listnodes type="roles">
  rolestree.addItem("<mm:field name="name" />",
                  "<mm:write referid="wizardjsp"/>?wizard=roles&objectnumber=<mm:field name="number" />",
                  null,                  "<di:translate id="treatroles">treat roles</di:translate>",
                  "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
</mm:listnodes>

menu.makeLastSubmenu(rolestree, true);
</di:hasrole>


<%-- has to be only for admin I believe --%>
menu.addItem("<di:translate id="Metadefinition">Metadata</di:translate>","",null,"");
var metatree = new MTMenu();

<%-- create new metadata standard --%>
metatree.addItem("<di:translate id="createNewMetadatastandard">Create new Metadata Standard</di:translate>",
    	         "<mm:write referid="wizardjsp"/>?wizard=metastandard&objectnumber=new",
        	     null,
            	 "<di:translate id="createNewMetadatastandardDescription">Create a new metadata standard</di:translate>",
	             "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");

<%-- edit existing metadata standards --%>
<mm:listnodes type="metastandard">
<mm:remove referid="metastandardNumber"/>
<mm:field id="metastandardNumber" name="number" write="true"/>
  metatree.addItem("<mm:field name="name" />",
                  "<mm:write referid="wizardjsp"/>?wizard=metastandard&objectnumber=<mm:field name="number" />",
                  null,
                  "<di:translate id="treatmetastandard">treat metastandard</di:translate>",
                  "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");

var metadeftree<%= metatreeCount %> = new MTMenu(); 
<%-- create new metadefinition --%>
metadeftree<%= metatreeCount %>.addItem("<di:translate id="createNewMetadefinition">Create new Metadefinition</di:translate>",
    	         "<mm:write referid="wizardjsp"/>?wizard=metadefinition&objectnumber=new&origin=<mm:write referid="metastandardNumber" />",
        	     null,
            	 "<di:translate id="createNewMetadefinitionDescription">Create a new metadata definition</di:translate>",
	             "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");


<mm:related path="metadefinition"><%-- orderby="metadefinition.name" directions="up" searchdir="destination">--%>
<mm:first>

</mm:first>
 <mm:node element="metadefinition">
  metadeftree<%= metatreeCount %>.addItem("<mm:field name="name" />",
                  "<mm:write referid="wizardjsp"/>?wizard=<mm:nodeinfo type="type" />&objectnumber=<mm:field name="number" />",
                  null,
                  "<di:translate id="treat">treat</di:translate> <mm:nodeinfo type="type" />",
                  "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
<%--<mm:treeinclude write="true" page="/education/wizards/learnobject.jsp" objectlist="$includePath" referids="wizardjsp">
    <mm:param name="parenttree">metatree<%= treeCount %></mm:param>
    <mm:param name="startnode"><mm:field name="number" /></mm:param>
    <mm:param name="depth">10</mm:param>
  </mm:treeinclude>--%>
 
 </mm:node> 
<mm:last>

 </mm:last>
</mm:related>
 metatree.makeLastSubmenu(metadeftree<%= metatreeCount++ %>, true);
</mm:listnodes>


<%-- edit existing metadefinitions
<mm:listnodes type="metadefinition">
  metatree.addItem("<mm:field name="name" />",
                  "<mm:write referid="wizardjsp"/>?wizard=<mm:nodeinfo type="type" />&objectnumber=<mm:field name="number" />",
                  null,
                  "<di:translate id="treat">treat</di:translate> <mm:nodeinfo type="type" />",
                  "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");

</mm:listnodes>--%>
menu.makeLastSubmenu(metatree, true);



<mm:listnodes type="educations">
menu.addItem("<mm:field name="name" />",
             "<mm:write referid="wizardjsp"/>?wizard=educations&objectnumber=<mm:field name="number" />",
             null,
             "<di:translate id="changeEducation">Change education</di:translate>",
             "<mm:treefile write="true" page="/education/wizards/gfx/edit_education.gif" objectlist="$includePath" />");
<mm:related path="posrel,learnobjects" orderby="posrel.pos" directions="up" searchdir="destination">
<mm:first>
var edutree<%= treeCount %> = new MTMenu(); 
</mm:first>
 <mm:node element="learnobjects">
  edutree<%= treeCount %>.addItem("<mm:field name="name" />",
                  "<mm:write referid="wizardjsp"/>?wizard=<mm:nodeinfo type="type" />&objectnumber=<mm:field name="number" />",
                  null,
                  "<di:translate id="treat">treat</di:translate> <mm:nodeinfo type="type" />",
                  "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
<mm:treeinclude write="true" page="/education/wizards/learnobject.jsp" objectlist="$includePath" referids="wizardjsp">
    <mm:param name="parenttree">edutree<%= treeCount %></mm:param>
    <mm:param name="startnode"><mm:field name="number" /></mm:param>
    <mm:param name="depth">10</mm:param>
  </mm:treeinclude>
 
 </mm:node> 
<mm:last>
 menu.makeLastSubmenu(edutree<%= treeCount++ %>, true);
 </mm:last>
</mm:related>
</mm:listnodes>
<mm:compare referid="showcode" value="true" inverse="true">
</script>
</head>
<body onLoad="MTMStartMenu(true)"></body>
</html>
</mm:compare>
</mm:cloud>
</mm:content>
