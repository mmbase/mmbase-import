<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%
   String imageName = "";
   String sAltText = "";
%>

<%
   if(session.getAttribute("education_topmenu_mode") == null)
   {//Default active element in education top menu
      session.setAttribute("education_topmenu_mode", "metadata");
   }
%>

<fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
<mm:content postprocessor="reducespace">
<mm:cloud loginpage="/login.jsp" jspvar="cloud">
  <%@include file="/shared/setImports.jsp"%>

  <mm:import externid="showcode">false</mm:import>
  <mm:import id="wizardjsp"><mm:treefile write="true" page="/editwizards/jsp/wizard.jsp" objectlist="$includePath" /></mm:import>
  <mm:import id="listjsp"><mm:treefile write="true" page="/editwizards/jsp/list.jsp" objectlist="$includePath" /></mm:import>
  <mm:import id="education_top_menu"><%= session.getAttribute("education_topmenu_mode") %></mm:import>

  <mm:compare referid="showcode" value="true" inverse="true">
  <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
    <html>
      <head>
        <title>Javascript menu</title>
        <style type="text/css">
            a {
                font-size: 8px;
            }
        </style>


        <script type="text/javascript" src="<mm:treefile page="/education/wizards/mtmcode.jsp" objectlist="$includePath" referids="$referids" write="true"/>"></script>

<mm:node number="component.pdf" notfound="skip">
    <mm:relatednodes type="providers" constraints="providers.number=$provider">
        <mm:import id="pdfurl"><mm:treefile write="true" page="/pdf/pdfchooser.jsp" objectlist="$includePath" referids="$referids" /></mm:import>
    </mm:relatednodes>
</mm:node>

<script type="text/javascript">
// Framebuster script to relocate browser when MSIE bookmarks this
// page instead of the parent frameset.  Set variable relocateURL to
// the index document of your website (relative URLs are ok):
//var relocateURL = "<mm:treefile write="true" page="/education/wizards/index.jsp" objectlist="$includePath" referids="$referids" />";

//if (parent.frames.length == 0)
//{
// if(document.images)
// {
//       location.replace(relocateURL);
// }
// else
// {
//       location = relocateURL;
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
MTMenuText = "<fmt:message key="educations"/>";
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
<% int comptreeCount = 0; %>

// testing...
//MTMSubsAutoClose = true;

// Main menu.
var menu = new MTMenu();

<mm:compare referid="education_top_menu" value="components">
   <di:hasrole role="systemadministrator">
      menu.addItem("<fmt:message key="editComponents"/>",
                   "<mm:treefile write="true" page="/components/index.jsp" objectlist="$includePath" />",
                   "_top",
                   "<fmt:message key="editComponentsDescription"/>",
                   "<mm:treefile write="true" page="/education/wizards/gfx/folder_closed.gif" objectlist="$includePath" />");
   </di:hasrole>
</mm:compare>


<mm:compare referid="education_top_menu" value="roles">
   <di:hasrole role="systemadministrator">

      menu.addItem("<fmt:message key="roles"/>","",null,"");
      var rolestree = new MTMenu();
      <%-- edit people,rolerel, education --%>
   <%-- doesn't work properly, so commented it out for the moment
      rolestree.addItem("<fmt:message key="editPeopleRoleRelEducation"/>",
                        "<mm:treefile write="true" page="/education/wizards/roles.jsp" objectlist="$includePath" />",
                        null,
                        "<fmt:message key="editPeopleRoleRelEducationDescription"/>",
                        "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
   --%>
      <%-- create new role --%>
      rolestree.addItem("<fmt:message key="createNewRoles"/>",
                        "<mm:write referid="wizardjsp"/>?wizard=roles&objectnumber=new",
                        null,
                        "<fmt:message key="createNewRolesDescription"/>",
                        "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");

      <%-- edit existing roles --%>
      <mm:listnodes type="roles">
         rolestree.addItem("<mm:field name="name" />",
                           "<mm:write referid="wizardjsp"/>?wizard=roles&objectnumber=<mm:field name="number" />",
                           null,
                           "<fmt:message key="treatRoles"/>",
                           "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
      </mm:listnodes>

      menu.makeLastSubmenu(rolestree, true);
   </di:hasrole>
</mm:compare>


<mm:compare referid="education_top_menu" value="filemanagement">
   <di:hasrole role="filemanager">
      menu.addItem("<fmt:message key="filemanagement"/>",
                   "<mm:treefile write="true" page="/education/filemanagement/index.jsp" objectlist="$includePath" />",
                   null,
                   "<fmt:message key="filemanagement"/>",
                   "<mm:treefile write="true" page="/education/wizards/gfx/folder_closed.gif" objectlist="$includePath" />");
   </di:hasrole>
</mm:compare>



<mm:compare referid="education_top_menu" value="competence">
   <%-- has to be only for admin I believe --%>

   menu.addItem("Competentie beheer","",null,"");
   var comptree = new MTMenu();

   comptree.addItem("Competenties",
                    "<mm:write referid="listjsp"/>?wizard=competencies&nodepath=competencies&searchfields=name&fields=name",
                    null,
                    "Bewerk competenties",
                    "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
   comptree.addItem("Preassessments",
                    "<mm:write referid="listjsp"/>?wizard=preassessments&nodepath=preassessments&searchfields=name&fields=name",
                    null,
                    "Bewerk preassessments",
                    "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
   comptree.addItem("Postassessments",
                    "<mm:write referid="listjsp"/>?wizard=postassessments&nodepath=postassessments&searchfields=name&fields=name",
                    null,
                    "Bewerk postassessments",
                    "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
   comptree.addItem("Profielen",
                    "<mm:write referid="listjsp"/>?wizard=profiles&nodepath=profiles&searchfields=name&fields=name",
                    null,
                    "Bewerk profielen",
                    "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
   comptree.addItem("P.O.P.",
                    "<mm:write referid="listjsp"/>?wizard=pop&nodepath=pop&searchfields=name&fields=name",
                    null,
                    "Bewerk persoonlijk opleidingplan",
                    "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
   menu.makeLastSubmenu(comptree, true);
</mm:compare>





<mm:compare referid="education_top_menu" value="metadata">

   menu.addItem("<fmt:message key="metadata"/>","<mm:write referid="listjsp"/>?wizard=metastandard&nodepath=metastandard&fields=name&orderby=name",null,"");
   var metatree = new MTMenu();

   <%-- create new metadata standard --%>
      metatree.addItem("<fmt:message key="createNewMetadatastandard"/>",
                  "<mm:write referid="wizardjsp"/>?wizard=metastandard&objectnumber=new",
                 null,
                   "<fmt:message key="createNewMetadatastandardDescription"/>",
                   "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");

   <%-- edit existing metadata standards --%>
   <mm:listnodes type="metastandard">
   <mm:remove referid="metastandardNumber"/>
   <mm:field id="metastandardNumber" name="number" write="false"/>
      metatree.addItem("<mm:field name="name" /></a>&nbsp;<a href='metaedit.jsp?number=<mm:field name="number"/>&set_defaults=true' target='text'><img src='gfx/metavalid.gif' border='0' alt='Bewerk standaard waarden voor metadatastandaard'>",
                     "<mm:write referid="wizardjsp"/>?wizard=metastandard&objectnumber=<mm:field name="number" />",
                     null,
                     "<fmt:message key="treatMetastandard"/>",
                     "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");

   var metadeftree<%= metatreeCount %> = new MTMenu();
   <%-- create new metadefinition --%>
      metadeftree<%= metatreeCount %>.addItem("<fmt:message key="createNewMetadefinition"/>",
                  "<mm:write referid="wizardjsp"/>?wizard=metadefinition&objectnumber=new&origin=<mm:write referid="metastandardNumber" />",
                 null,
                   "<fmt:message key="createNewMetadefinitionDescription"/>",
                   "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");


   <mm:related path="metadefinition" orderby="metadefinition.name"><%-- orderby="metadefinition.name" directions="up" searchdir="destination">--%>

    <mm:node element="metadefinition">
     metadeftree<%= metatreeCount %>.addItem("<mm:field name="name" />",
                     "<mm:write referid="wizardjsp"/>?wizard=<mm:nodeinfo type="type" />&objectnumber=<mm:field name="number" />",
                     null,
                     "<fmt:message key="treatMetadefinition"/> <mm:nodeinfo type="type" />",
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
                       "<fmt:message key="treatMetadefinition"/> <mm:nodeinfo type="type" />",
                       "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");

   </mm:listnodes>
   --%>
   menu.makeLastSubmenu(metatree, true);
</mm:compare>



<mm:compare referid="education_top_menu" value="educations">
   <% //----------------------- Educations came from here ----------------------- %>
   menu.addItem("<fmt:message key="educations"/>","<mm:write referid="listjsp"/>?wizard=educations&nodepath=educations&fields=name&orderby=name",null,"<mm:treefile write="true" page="/education/wizards/gfx/edit_education.gif" objectlist="$includePath" />");

   var edutree = new MTMenu();
   <% //new education item %>
   <di:hasrole role="courseeditor">
      edutree.addItem("<fmt:message key="createNewEducation"/>",
                      "<mm:write referid="wizardjsp"/>?wizard=educations&objectnumber=new",
                      null,
                      "<fmt:message key="createNewEducationDescription"/>",
                      "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
   </di:hasrole>


   <% //We go throw all educations for CURRENT USER%>
   <%
      int iNumberOfRelations = 0;
   %>
   <mm:node number="$user">
      <mm:related path="classrel,classes,related,educations">
         <mm:size jspvar="NumberOfRelations" vartype="Integer">
            <%
               iNumberOfRelations = NumberOfRelations.intValue();
            %>
         </mm:size>
      </mm:related>
   </mm:node>
   <%
      String sEducationConstraints = new String();
      if (iNumberOfRelations > 1) sEducationConstraints = "educations.number=" + session.getAttribute("education_topmenu_course");
   %>
   <mm:node number="$user">
      <mm:related path="classrel,classes">
         <mm:node element="classes">
            <mm:related path="related,educations" constraints="<%=sEducationConstraints%>">
               <% //Show current education from here %>
               <mm:node element="educations">
                     <%@include file="whichimage.jsp"%>
                     edutree.addItem("<mm:field name="name" /><mm:present referid="pdfurl"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' alt='(PDF)'/></mm:present></a> <a href='metaedit.jsp?number=<mm:field name="number"/>' target='text'><img id='img_<mm:field name="number"/>' src='<%= imageName %>' border='0' alt='<%= sAltText %>'>",
                                     "<mm:write referid="wizardjsp"/>?wizard=educations&objectnumber=<mm:field name="number" />",
                                     null,
                                     "<fmt:message key="editEducation"/>",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/edit_education.gif" objectlist="$includePath" />");


                     var edutree<%= treeCount %> = new MTMenu();

                     <%-- create new learnblock item --%>
                     edutree<%= treeCount %>.addItem("<fmt:message key="createNewLearnblock"/>",
                                                     "<mm:write referid="wizardjsp"/>?wizard=learnblocks&objectnumber=new&origin=<mm:field name="number"/>",
                                                     null,
                                                     "<fmt:message key="createNewLearnblockDescription"/>",
                                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");

                     <% //All learnblocks for current education %>
                     <mm:related path="posrel,learnobjects" orderby="posrel.pos" directions="up" searchdir="destination">
                        <mm:node element="learnobjects">
                           <%@include file="whichimage.jsp"%>
                           <mm:nodeinfo type="type" id="this_node_type">
                              <mm:import id="mark_error" reset="true"></mm:import>
                              <mm:compare referid="this_node_type" value="tests">
                                 <mm:field name="questionamount" id="questionamount">
                                    <mm:isgreaterthan value="0">
                                       <mm:countrelations type="questions">
                                          <mm:islessthan value="$questionamount">
                                             <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er gesteld moeten worden.</mm:import>
                                          </mm:islessthan>
                                       </mm:countrelations>
                                    </mm:isgreaterthan>

                                    <mm:field name="requiredscore" id="requiredscore">
                                       <mm:countrelations type="questions">
                                          <mm:islessthan value="$requiredscore">
                                             <mm:import id="mark_error" reset="true">Er zijn minder vragen ingevoerd dan er goed beantwoord moeten worden.</mm:import>
                                          </mm:islessthan>
                                       </mm:countrelations>
                                       <mm:isgreaterthan referid="questionamount" value="0">
                                          <mm:islessthan referid="questionamount" value="$requiredscore">
                                             <mm:import id="mark_error" reset="true">Er worden minder vragen gesteld dan er goed beantwoord moeten worden.</mm:import>
                                          </mm:islessthan>
                                       </mm:isgreaterthan>
                                    </mm:field>
                                 </mm:field>
                              </mm:compare>
                                         edutree<%= treeCount %>.addItem("<mm:field name="name" /><mm:present referid="pdfurl"><mm:compare referid="this_node_type" value="pages"></a> <a href='<mm:write referid="pdfurl" />&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' alt='(PDF)'/></mm:compare><mm:compare referid="this_node_type" value="learnblocks"></a> <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' alt='(PDF)'/></mm:compare></mm:present></a> <a href='metaedit.jsp?number=<mm:field name="number"/>' target='text'><img id='img_<mm:field name="number"/>' src='<%= imageName %>' border='0' alt='<%= sAltText %>'>",
                                                                         "<mm:write referid="wizardjsp"/>?wizard=<mm:nodeinfo type="type" />&objectnumber=<mm:field name="number" />",
                                                                         null,
                                                                         "<fmt:message key="treatLearnobject"/> <mm:nodeinfo type="type" />",
                                                                         "<mm:treefile write="true" page="/education/wizards/gfx/learnblock.gif" objectlist="$includePath" />");
                           </mm:nodeinfo>

                           <mm:treeinclude write="true" page="/education/wizards/learnobject.jsp" objectlist="$includePath" referids="wizardjsp">
                              <mm:param name="parenttree">edutree<%= treeCount %></mm:param>
                              <mm:param name="startnode"><mm:field name="number" /></mm:param>
                              <mm:param name="depth">10</mm:param>
                           </mm:treeinclude>

                        </mm:node>

                     </mm:related>

                     edutree.makeLastSubmenu(edutree<%= treeCount++ %>, true);
               </mm:node>
            </mm:related>
         </mm:node>
      </mm:related>
   </mm:node>

   menu.makeLastSubmenu(edutree, true);
</mm:compare>



<mm:compare referid="showcode" value="true" inverse="true">
   </script>
   </head>

   <body onLoad="MTMStartMenu(true); parent.frames['text'].document.location.href='<mm:treefile write="true" page="/education/wizards/loaded.jsp" objectlist="$includePath"/>'"/>
   </html>
</mm:compare>

</mm:cloud>

</mm:content>
</fmt:bundle>

