<%@ page import = "java.util.HashSet" %>

<mm:nodeinfo type="type" jspvar="sNodeType" vartype="String">

   <%
      HashSet hsetAllowedChildren = new HashSet();

      if(sNodeType.equals("learnblocks"))
      {
         hsetAllowedChildren.add("learnblocks");
         hsetAllowedChildren.add("pages");
         hsetAllowedChildren.add("tests");
         hsetAllowedChildren.add("flashpages");
      }
      if(sNodeType.equals("tests"))
      {
         hsetAllowedChildren.add("mcquestions");
         hsetAllowedChildren.add("openquestions");
         hsetAllowedChildren.add("rankingquestions");
         hsetAllowedChildren.add("valuequestions");
         hsetAllowedChildren.add("couplingquestions");
         hsetAllowedChildren.add("hotspotquestions");
         hsetAllowedChildren.add("dropquestions");
      }
      if(hsetAllowedChildren.contains("learnblocks")){
         %>
            <%= treeName %>.addItem("nieuwe leerblok",
                                     "<mm:write referid="wizardjsp"/>?wizard=learnblocks&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("pages")){
         %>
            <%= treeName %>.addItem("nieuwe pagina",
                                     "<mm:write referid="wizardjsp"/>?wizard=pages&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("tests")){
         %>
            <%= treeName %>.addItem("nieuwe toets",
                                     "<mm:write referid="wizardjsp"/>?wizard=tests&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("flashpages")){
         %>
            <%= treeName %>.addItem("nieuwe flash-pagina",
                                     "<mm:write referid="wizardjsp"/>?wizard=flashpages&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("mcquestions")){
         %>
            <%= treeName %>.addItem("nieuwe multiple-choice vraag",
                                     "<mm:write referid="wizardjsp"/>?wizard=mcquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("openquestions")){
         %>
            <%= treeName %>.addItem("nieuwe open vraag",
                                     "<mm:write referid="wizardjsp"/>?wizard=openquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("rankingquestions")){
         %>
            <%= treeName %>.addItem("nieuwe rangorde vraag",
                                     "<mm:write referid="wizardjsp"/>?wizard=rankingquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("valuequestions")){
         %>
            <%= treeName %>.addItem("nieuwe waarde vraag",
                                     "<mm:write referid="wizardjsp"/>?wizard=valuequestions&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("couplingquestions")){
         %>
            <%= treeName %>.addItem("nieuwe koppel vraag",
                                     "<mm:write referid="wizardjsp"/>?wizard=couplingquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("hotspotquestions")){
         %>
            <%= treeName %>.addItem("nieuwe hotspot vraag",
                                     "<mm:write referid="wizardjsp"/>?wizard=hotspotquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
      if(hsetAllowedChildren.contains("dropquestions")){
         %>
            <%= treeName %>.addItem("nieuwe drag-en-drop vraag",
                                     "<mm:write referid="wizardjsp"/>?wizard=dropquestions&objectnumber=new&origin=<mm:field name="number"/>",
                                     null,
                                     "",
                                     "<mm:treefile write="true" page="/education/wizards/gfx/new_education.gif" objectlist="$includePath" />");
         <%
      }
   %>

</mm:nodeinfo>