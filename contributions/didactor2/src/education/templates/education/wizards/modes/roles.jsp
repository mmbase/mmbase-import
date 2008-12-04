<jsp:root version="2.0"
          xmlns:jsp="http://java.sun.com/JSP/Page"
          xmlns:fn="http://java.sun.com/jsp/jstl/functions"
          xmlns:mm="http://www.mmbase.org/mmbase-taglib-2.0"
          xmlns:di="http://www.didactor.nl/ditaglib_1.0">
  <jsp:output omit-xml-declaration="yes" />
  <mm:cloud rank="basic user">
    <div>
    <jsp:directive.include file="../mode.include.jsp" />
    <mm:import externid="e">${education}</mm:import>
    <di:has editcontext="rollen" inverse="true">
      No rights
    </di:has>
    <di:has editcontext="rollen">
      <a href="javascript:clickNode('persons_0')">
        <img src='gfx/tree_minlast.gif' width="16" border='0' align='center' valign='middle'  id='img_persons_0' />
      </a>

      <img src='gfx/menu_root.gif' border='0' align='center' valign='middle'/>
      <nobr>
      <a href="javascript:clickNode('persons_0')"><di:translate key="education.personstab" /></a></nobr>
      <br />
      <div id='persons_0'>
         <!--
              edit people,rolerel, education
              doesn't work properly, so commented it out for the moment
              rolestree.addItem("<di:translate key="education.editpeoplerolereleducation" />",
              "<mm:treefile write="true" page="/education/wizards/roles.jsp" objectlist="$includePath" />",
              null,
              "<di:translate key="education.editpeoplerolereleducationdescription" />",
              "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         -->
         <!--create new role-->

         <di:leaf icon="learnblock"  branchPath=". ">
           <jsp:text> </jsp:text>
           <mm:link referid="listjsp">
             <mm:param name="wizard">config/people/people</mm:param>
             <mm:param name="nodepath">people</mm:param>
             <mm:param name="orderby">lastname,firstname</mm:param>
             <mm:param name="searchfields">firstname,suffix,lastname,username,externid</mm:param>
             <mm:param name="search">yes</mm:param>
             <a href="${_}${forbidtemplate}"
                title="${di:translate('education.persons')}" target="text">
               <di:translate key="education.persons" />
             </a>
           </mm:link>
         </di:leaf>
         <mm:listnodes type="roles" varStatus="status">
           <di:leaf  branchPath=". ${status.last ? '.' : ' '}">
             <jsp:text> </jsp:text>
             <mm:link referid="listjsp" referids="_node@startnodes,e@education">
               <mm:param name="wizard">config/people/people_unlink</mm:param>
               <mm:param name="nodepath">roles,people</mm:param>
               <mm:param name="orderby">people.lastname,people.firstname</mm:param>
               <mm:param name="searchfields">people.firstname,people.suffix,people.lastname,people.username,people.externid</mm:param>
               <mm:param name="search">yes</mm:param>
               <mm:param name="title">${_node.name}</mm:param>
               <mm:param name="origin">${_node}</mm:param>
               <mm:param name="relationOriginNode">${_node}</mm:param>
               <mm:param name="relationRole">related</mm:param>
               <a href="${_}${forbidtemplate}"
                  title="${di:translate('education.persons')}" target="text">
                 <mm:field name="name" />
               </a>
             </mm:link>
           </di:leaf>
         </mm:listnodes>
         <di:leaf icon="learnblock"  branchPath=". ">
           <mm:link referid="listjsp" referids="e@education">
             <mm:param name="wizard">config/class/classes-standalone</mm:param>
             <mm:param name="nodepath">classes</mm:param>
             <mm:param name="search">yes</mm:param>
             <mm:param name="orderby">number</mm:param>
             <mm:param name="directions">down</mm:param>
             <jsp:text> </jsp:text>
             <a href="${_}${forbidtemplate}"
                title="${di:translate('education.classes')}"
                target="text">
               <di:translate key="education.classes" />
             </a>
           </mm:link>
         </di:leaf>
         <di:leaf icon="learnblock"  branchPath=". ">
           <mm:link referid="listjsp">
             <mm:param name="wizard">config/workgroup/workgroups</mm:param>
             <mm:param name="nodepath">workgroups</mm:param>
             <mm:param name="search">yes</mm:param>
             <mm:param name="orderby">number</mm:param>
             <mm:param name="directions">down</mm:param>
             <nobr>
               <jsp:text> </jsp:text>
               <a href="${_}${forbidtemplate}"
                  title="${di:translate('education.workgroups')}"  target="text">
                 <di:translate key="education.workgroups" />
               </a>
             </nobr>
           </mm:link>
         </di:leaf>
         <di:leaf icon="learnblock"  branchPath=". ">
           <jsp:text> </jsp:text>
           <a href="roles.jsp" title="${di:translate('education.roles')}"
              target="text"><di:translate key="education.roles" /></a>
         </di:leaf>
         <di:leaf icon="learnblock"  branchPath="..">
           <mm:link referid="listjsp">
             <mm:param name="wizard">config/disallowedusernames/disallowedusernames</mm:param>
             <mm:param name="nodepath">disallowedusernames</mm:param>
             <mm:param name="search">yes</mm:param>
             <mm:param name="orderby">number</mm:param>
             <mm:param name="directions">down</mm:param>
             <nobr>
               <jsp:text> </jsp:text>
               <a href="${_}${forbidtemplate}"
                  target="text">
                 <mm:nodeinfo nodetype="disallowedusernames" type="plural_guinodemanager" />
               </a>
             </nobr>
           </mm:link>
         </di:leaf>
         <!-- sigh -->
         <mm:hasnode number="component.report">
           <!-- <di:hasrole role="teacher"> -->
           <table border="0" cellpadding="0" cellspacing="0">
             <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td><nobr><jsp:text> </jsp:text><a href='../../report/index.jsp' target="text">Rapport</a></nobr></td>
             </tr>
           </table>
           <!-- </di:hasrole> -->
         </mm:hasnode>

         <mm:hasnode number="component.isbo">
           <!-- <di:hasrole role="systemadministrator"> -->
           <table border="0" cellpadding="0" cellspacing="0">
             <tr>
               <td><img src="gfx/tree_spacer.gif" width="16px" height="16px" border="0" align="center" valign="middle"/></td>
               <td><img src="gfx/tree_leaflast.gif" border="0" align="middle"/></td>

               <td><img src="gfx/learnblock.gif" border="0" align="middle" /></td>
               <td>
                 <nobr>
                   <jsp:text> </jsp:text>
                   <a href='../../isbo/index.jsp' title="${di:translate('education.isboimport')}"
                      target="text"><di:translate key="education.isboimport" /></a>
                 </nobr>
               </td>
             </tr>
           </table>
           <!-- </di:hasrole> -->
         </mm:hasnode>


       </div>
    </di:has>
    </div>
  </mm:cloud>
</jsp:root>
