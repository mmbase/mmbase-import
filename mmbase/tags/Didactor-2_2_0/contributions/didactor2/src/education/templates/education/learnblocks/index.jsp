<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.1" prefix="mm"%>
<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>

<%@ page import = "java.io.*" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.Date" %>
<%@ page import = "java.util.ListIterator" %>

<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">

<mm:import externid="learnobject" required="true"/>

<!-- TODO Need this page? -->

<%@include file="/shared/setImports.jsp" %>

<%
   String sUserSettings_PathBaseDirectory = getServletContext().getInitParameter("filemanagementBaseDirectory");
   String sUserSettings_BaseURL = getServletContext().getInitParameter("filemanagementBaseUrl");

   if (sUserSettings_PathBaseDirectory == null || sUserSettings_BaseURL == null)
   {
       throw new ServletException("Please set filemanagementBaseDirectory and filemanagementBaseUrl parameters in web.xml");
   }
%>


<%-- remember this page --%>
<mm:treeinclude page="/education/storebookmarks.jsp" objectlist="$includePath" referids="$referids">
    <mm:param name="learnobject"><mm:write referid="learnobject"/></mm:param>
    <mm:param name="learnobjecttype">learnblocks</mm:param>
</mm:treeinclude>



<html>
<head>
   <title>Learnblock content</title>
   <link rel="stylesheet" type="text/css" href="<mm:treefile page="/css/base.css" objectlist="$includePath" />" />
</head>
<body>

<div class="learnenvironment">

<mm:node number="$learnobject" jspvar="nodeLearnObject">
   <%//checking the type of the learnblock %>
   <%//Does it belong to Scorm package%>

   <mm:remove referid="it_is_a_package"/>
   <%
      ArrayList arliPath = new ArrayList();
      String sPackageNode = "";
   %>


   <mm:field name="path" jspvar="sStep" vartype="String">
      <%
         String[] arrstrStep = sStep.split("-");
         if(arrstrStep.length == 2)
         {
            sPackageNode = arrstrStep[0];
            arliPath.add(arrstrStep[1]);
         }
      %>
   </mm:field>


   <%
      if(nodeLearnObject.getNodeManager().getName().equals("htmlpages"))
      {
         %>
            <mm:related path="posrel,learnblocks">
               <mm:node element="posrel">
                  <mm:field name="pos" jspvar="sPosNumber" vartype="String">
                     <%
                        arliPath.add(sPosNumber);
                     %>
                  </mm:field>
               </mm:node>
            </mm:related>
         <%
      }
   %>

   <mm:import id="path" reset="true"><mm:field name="path"/></mm:import>
   <mm:compare referid="path" value="" inverse="true">
      <mm:import id="it_is_a_package" reset="true">true</mm:import>
   </mm:compare>


   <mm:present referid="it_is_a_package">
      <mm:relatednodes type="learnblocks" role="posrel" searchdir="source">
         <mm:import id="path" reset="true"><mm:field name="path"/></mm:import>
         <mm:compare referid="path" value="" inverse="true">

            <mm:tree type="learnblocks" role="posrel" searchdir="source">
               <mm:import id="temp_path"><mm:field name="path"/></mm:import>
               <mm:compare referid="temp_path" value="" inverse="true">
                  <mm:write referid="temp_path" jspvar="sStep" vartype="String">
                     <%
                        String[] arrstrStep = sStep.split("-");
                        arliPath.add(arrstrStep[1]);
                        sPackageNode = arrstrStep[0];
                     %>
                  </mm:write>
               </mm:compare>
            </mm:tree>
            <mm:import id="it_is_a_package" reset="true">true</mm:import>
         </mm:compare>
      </mm:relatednodes>
   </mm:present>


   <mm:notpresent referid="it_is_a_package">
      <mm:treeinclude page="/education/pages/content.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="learnobject"><mm:field name="number"/></mm:param>
      </mm:treeinclude>

      <mm:treeinclude page="/education/paragraph/paragraph.jsp" objectlist="$includePath" referids="$referids">
         <mm:param name="node_id"><mm:write referid="learnobject"/></mm:param>
         <mm:param name="path_segment">../</mm:param>
      </mm:treeinclude>
   </mm:notpresent>


   <mm:present referid="it_is_a_package">
      <mm:remove referid="loaded"/>

      <mm:node number="component.scorm" notfound="skip">
         <%
            String sPath = "";
            for(ListIterator it = arliPath.listIterator(arliPath.size()); it.hasPrevious();)
            {
               if(it.previousIndex() < arliPath.size() - 1)
               {
                  sPath += ",";
               }
               sPath += (String) it.previous();
            }

            //System.out.println("path=" + sPath);


            String sScormDir = sUserSettings_PathBaseDirectory + File.separator + "scorm";
            String sNodePlayer = sScormDir + File.separator + sPackageNode + "_player";

            //package checking
            File filePackageDir = new File(sScormDir + File.separator + sPackageNode);
            File filePackageUnzippedDir = new File(sScormDir + File.separator + sPackageNode + "_");
            File filePackagePlayerDir = new File(sNodePlayer);
            if(filePackageDir.exists() && filePackageUnzippedDir.exists() && filePackagePlayerDir.exists())
            {//The package exists
               File fileCustomMenu = new File(sNodePlayer + File.separator + "ReloadContentPreviewFiles" + File.separator + "CPOrgs" + nodeLearnObject.getNumber() +  ".js");
               if(!fileCustomMenu.exists())
               {

                  Class classMenuCreater = null;
                  nl.didactor.component.scorm.player.InterfaceMenuCreator menuCreator = null;

                  try
                  {
                     classMenuCreater = Class.forName("nl.didactor.component.scorm.player.MenuCreator");
                     menuCreator = (nl.didactor.component.scorm.player.InterfaceMenuCreator) classMenuCreater.getConstructors()[0].newInstance(new Object[]{new File(sScormDir + File.separator + sPackageNode + "_" + File.separator + "imsmanifest.xml"), "http://", sUserSettings_BaseURL + "/scorm/" + sPackageNode + "_" + "/"});

                  }
                  catch (Exception e)
                  {
                     throw new ServletException ("Can't load SCORM player class! Nested exception is:" + e.toString());
                  }

                  String[] arrstrJSMenu = menuCreator.parse(true, "" + sPackageNode, sPath);

                  RandomAccessFile rafileMenuConfig = new RandomAccessFile(fileCustomMenu, "rw");
                  for(int f = 0; f < arrstrJSMenu.length; f++)
                  {
                     rafileMenuConfig.writeBytes(arrstrJSMenu[f]);
                     rafileMenuConfig.writeByte(13);
                     rafileMenuConfig.writeByte(10);
                  }
                  rafileMenuConfig.close();
                  fileCustomMenu.deleteOnExit();
               }
               %>
                  <script>
                     parent.frames['content'].location.href='<%= sUserSettings_BaseURL %>/scorm/<%= sPackageNode %>_player/index.jsp?path=<%= nodeLearnObject.getNumber() %>';
                  </script>

                  <%--
                  <iframe src="<%= sUserSettings_BaseURL %>/scorm/<%= sPackageNode %>_player/index.jsp?path=<%= nodeLearnObject.getNumber() %>" width="100%" height="100%"></iframe>
                  --%>
               <%
            }
            else
            {//The package isn't exist
               %>
                  <di:translate key="scorm.package_dir_is_missing" arg0="<%= sPackageNode %>"/>
                  <br/><br/>
                  <%= filePackageDir.getAbsolutePath() %><br />
                  <%= filePackageUnzippedDir.getAbsolutePath() %><br/>
                  <%= filePackagePlayerDir.getAbsolutePath() %><br />
               <%
            }


         %>

         <mm:import id="loaded">true</mm:import>
      </mm:node>

      <mm:notpresent referid="loaded">
         <di:translate key="scorm.you_have_to_turn_on_the_scorm_module" />
      </mm:notpresent>

   </mm:present>
</mm:node>


<%@include file="../includes/descriptionrel_link.jsp"%>

</div>


<mm:node number="$learnobject" jspvar="nodeLearnObject">
   <%@include file="../includes/component_link.jsp"%>
</mm:node>

</body>
</html>
</mm:cloud>
</mm:content>
