<%@include file="header.jsp" %>
  <mm:cloud name="mmbase" method="http" jspvar="cloud">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String transactionID = state.getProperty("transaction");
      String nodeID = state.getProperty("node");
      String role = state.getProperty("role");
      String managerName = state.getProperty("manager");
      String currentState = state.getProperty("state");
      Module mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
    %>
  <mm:import externid="node"  vartype="String" jspvar="nodealias" />
  <mm:present referid="node">
  <%  if (transactionID != null) try {
         Transaction oldtrans = cloud.getTransaction(transactionID);
         oldtrans.cancel();
      } catch (Exception e) {}
      nodeID = nodealias;
      state.put("node",nodealias);
      state.put("state","none");
    %>
  </mm:present>
  <%
     Transaction trans = null;
     if (transactionID == null) {
         trans = cloud.createTransaction();
         transactionID = trans.getName();
         state.put("transaction",transactionID);
     } else {
         trans = cloud.getTransaction(transactionID);
     }
     NodeManager manager = trans.getNodeManager(managerName);
     Node node = trans.getNode(nodeID);
     boolean isRelation = node.isRelation();
  %>
  <mm:import id="nodetype"><%=managerName%></mm:import>
  <head>
    <title>Editor</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
    <style>
<%@include file="css/mmeditors.css" %>
    </style>
  </head>
  <body>
    <% if ("none".equals(currentState) && !isRelation) { %>
    <table class="editlist">
      <tr>
        <td class="editprompt"><%=mmlanguage.getInfo("GET-other_editors")%></td>
        <td class="navlink"><a href="<mm:url page="index.jsp" />" target="_top">##</a></td>
      </tr>
      <tr>
        <td class="editprompt"><%=mmlanguage.getInfo("GET-new")%></td>
        <td class="navlink"><a href="<mm:url page="createnode.jsp" />" target="contentarea">##</a></td>
      </tr>
      <tr>
        <td class="editprompt"><%=mmlanguage.getInfo("GET-search")%></td>
        <td class="navlink"><a href="<mm:url page="select.jsp" />">##</a></td>
      </tr>
      <tr><td colspan="2">&nbsp;</td></tr>
    </table>
    <br/>
    <% } %>
    <% if (isRelation) { %>
    <table class="editlist">
      <%
         Properties sourceState = (Properties)states.get(states.size()-2);
         String sourceAlias = sourceState.getProperty("node");
         Node source = trans.getNode(sourceAlias);
         Node destination = ((Relation)node).getDestination();
         if (source.equals(destination)) {
             destination = ((Relation)node).getSource();
         }
      %>
      <tr>
        <td class="editprompt"><%=mmlanguage.getInfo("GET-go_to")%>
        <%=destination.getNodeManager().getGUIName()%>(<%=destination.getStringValue("gui()")%>)</td>
        <td class="navlink"><a href="<mm:url page="editor.jsp" >
              <mm:param name="manager"><%=destination.getNodeManager().getName()%></mm:param>
              <mm:param name="node" ><%=destination.getNumber()%></mm:param>
            </mm:url>" target="_top">##</a></td>
      </tr>
      <tr><td colspan="2">&nbsp;</td></tr>
    </table>
    <br/>
    <% } %>
    <table class="editlist">
      <% String path=request.getContextPath()+request.getServletPath();
         path=path.substring(0,path.length()-13)+"previews/"+managerName+".jsp";
      %>
      <% if (new java.io.File(page.getServletConfig().getServletContext().getRealPath(path)).exists()) { %>
  	  <tr>
        <td class="editprompt">Preview</td>
        <td class="navlink"><a href="<mm:url page="<%=path%>" ><mm:param name="node"><%=nodeID%></mm:param></mm:url>" target="workarea">##</a></td>
      </tr>
      <% } %>
      <tr>
        <td class="editprompt"><%=mmlanguage.getInfo("GET-fields")%></td>
        <td class="nolink">##</td>
      </tr>
    </table><table class="editlist">
      <mm:fieldlist type="edit" nodetype="<%=managerName%>">
      <tr>
        <td class="editlink" >
          <mm:fieldinfo type="guitype" vartype="String" jspvar="guitype" >
            <% String editpath=request.getContextPath()+request.getServletPath();
               editpath=editpath.substring(0,editpath.length()-13)+"editparts/"+guitype+".jsp";
            %>
            <% if (new java.io.File(page.getServletConfig().getServletContext().getRealPath(path)).exists()) { %>
              <a href="<mm:url page="editparts/${_}.jsp"><mm:param name="field"><mm:fieldinfo type="name" /></mm:param></mm:url>" target="workarea">##</a>
            <% } else { %>
              <a href="<mm:url page="editparts/autoedit.jsp"><mm:param name="field"><mm:fieldinfo type="name" /></mm:param></mm:url>" target="workarea">##</a>
            <% } %>
          </mm:fieldinfo>
        </td>
        <td class="fieldprompt"><mm:fieldinfo type="guiname" /></td>
        <td ><mm:fieldinfo type="name" vartype="String" jspvar="fieldname"
         ><% String guivalue=node.getStringValue("gui("+fieldname+")");
             if (guivalue.length()>14 && guivalue.indexOf("<")==-1) {
               guivalue=guivalue.substring(0,14)+"...";
             }
          %><%=guivalue%> </mm:fieldinfo></td>
      </tr>
      </mm:fieldlist>
    </table>
    <br/>
    <%
     if (!"new".equals(currentState) && !isRelation) {
       String fieldprompt = "fieldprompt";
       String editprompt = "editprompt";
       String nolink = "nolink";
       String relationlink = "relationlink";
       String edittext = "edittext";
       if ("edit".equals(currentState)) {
          fieldprompt = "disabledprompt";
          editprompt = "disabledprompt";
          nolink = "disabledlink";
          relationlink = "disabledlink";
          edittext = "disabledtext";
       }
    %>
    <table class="editlist">
      <tr>
        <td class="<%=editprompt%>"><%=mmlanguage.getInfo("GET-relations")%></td>
        <td class="<%=nolink%>">##</td>
      </tr>
    </table><table class="editlist">
      <%
         // basic security shortcut, add type-based security
         String authtype=null;
         Module mmbase = cloud.getCloudContext().getModule("mmbase");
         if (mmbase!=null) authtype = mmbase.getInfo("GETAUTHTYPE");

         for(Iterator allrel=manager.getAllowedRelations().iterator(); allrel.hasNext();) {
           RelationManager relman=(RelationManager)allrel.next();
           NodeManager mn = null;
           String relrole = null;
           boolean allowed = relman.mayCreateNode();
           if (allowed) {
              try {
                mn = relman.getDestinationManager();
                relrole = relman.getForwardGUIName();
                if (mn.equals(manager)) {
                  mn = relman.getSourceManager();
                  relrole = relman.getReciprocalGUIName();
                }
              } catch (Exception e) {}
              
              // check for older code
              if ("basic".equals(authtype)) {
                allowed = mn.mayCreateNode();
              } else {
                allowed = true;
              }
           }                
           if (allowed) {
      %>
      <tr>
        <td class="<%=relationlink%>">
        <% if ("none".equals(currentState)) { %>
          <a href="<mm:url page="editor.jsp">
              <mm:param name="depth"><%=states.size()%></mm:param>
              <mm:param name="role"><%=relman.getForwardRole()%></mm:param>
              <mm:param name="manager"><%=mn.getName()%></mm:param>
            </mm:url>" target="_top">##</a>
        <% } else { %>##<% } %>
        </td>
        <td class="<%=fieldprompt%>">
          <%=mn.getGUIName()%>
          <% if (!"related".equals(relman.getForwardRole())) { %>
            <br />(<%=relrole%>)
          <% } %>
        </td>
        <td>&nbsp;</td>
      </tr>

      <mm:list nodes="<%=nodeID%>" path="<%=managerName+","+relman.getForwardRole()+","+mn.getName()%>">
        <% boolean mayeditrel=false; %>
        <mm:node element="<%=relman.getForwardRole()%>">
           <mm:maywrite><% mayeditrel=true; %></mm:maywrite>
        </mm:node>
        <% if (mayeditrel) { %>
        <tr>
          <td class="<%=relationlink%>">
          <% if ("none".equals(currentState)) { %>
            <a href="<mm:url page="editor.jsp">
                <mm:param name="node"><mm:field name="<%=relman.getForwardRole()+".number"%>" /></mm:param>
                <mm:param name="manager"><%=relman.getName()%></mm:param>
                <mm:param name="depth"><%=states.size()%></mm:param>
              </mm:url>" target="_top">##</a>
          <% } else { %>##<% } %>
          </td>
          <td class="<%=fieldprompt%>">
            <%=mn.getGUIName()%>
              <br />(<%=relrole%>)
          </td>
          <td class="<%=edittext%>"><mm:field name="<%=mn.getName()+".gui()"%>" /></td>
        </tr>
        <% } %>
      </mm:list>
      <%     }
           }
         }
      %>
    </table>

    <table class="editlist">
      <% if (!"none".equals(currentState)) { %>
      <tr>
        <td class="editprompt"><%=mmlanguage.getInfo("GET-save_changes")%></td>
        <% if (role!=null) { %>
          <td class="navlink"><a href="<mm:url page="editor.jsp"><mm:param name="action">save</mm:param></mm:url>" target="_top">##</a></td>
        <% } else { %>
          <td class="navlink"><a href="<mm:url page="editor.jsp"><mm:param name="action">save</mm:param><mm:param name="createrelation">true</mm:param></mm:url>" target="_top">##</a></td>
        <% } %>
      </tr>
      <tr>
        <td class="editprompt"><%=mmlanguage.getInfo("GET-do_not_save_changes")%></td>
        <td class="navlink"><a href="<mm:url page="editor.jsp"><mm:param name="action">cancel</mm:param></mm:url>" target="_top">##</a></td>
      </tr>
      <% } else if (isRelation) { %>
          <tr>
            <td class="editprompt"><%=mmlanguage.getInfo("GET-remove_relation")%></td>
            <td class="navlink" ><a href="deletenode.jsp" target="workarea">##</a></td>
          </tr>
        <tr>
          <td class="editprompt"><%=mmlanguage.getInfo("GET-back")%></td>
          <td class="navlink"><a href="<mm:url page="editor.jsp" ><mm:param name="manager">?</mm:param><mm:param name="depth"><%=states.size()-1%></mm:param></mm:url>" target="_top">##</a></td>
        </tr>
      <% } else if (node.hasRelations()) { %>
          <tr>
            <td class="disabledprompt"><%=mmlanguage.getInfo("GET-remove_object")%></td>
            <td class="disabledlink">##</td>
          </tr>
      <% } else { %>
        <tr>
          <td class="editprompt"><%=mmlanguage.getInfo("GET-remove_object")%></td>
          <td class="navlink" ><a href="deletenode.jsp" target="workarea">##</a></td>
        </tr>
      <% } %>
    </table>
  </mm:cloud>
  </body>
</html>

