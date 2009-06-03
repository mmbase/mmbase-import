<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud name="mmbase" method="http" jspvar="cloud" rank="$rank">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String transactionID = state.getProperty("transaction");
      String nodeID = state.getProperty("node");
      String role = state.getProperty("role");
      String managerName = state.getProperty("manager");
      String currentState = state.getProperty("state");
     Module mmlanguage;
     try {
        mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
     } catch (NotFoundException nfe) {
        mmlanguage = null;
     }
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
  </head>
  <body>
    <% if ("none".equals(currentState) && !isRelation) { %>
    <table class="editlist">
      <tr>
        <td class="editprompt"><%=translate(mmlanguage,"other_editors")%></td>
        <td class="navlink"><a href="<mm:url page="index.jsp" />" target="_top">##</a></td>
      </tr>
      <tr>
        <td class="editprompt"><%=translate(mmlanguage,"new")%></td>
        <td class="navlink"><a href="<mm:url page="createnode.jsp" />" target="contentarea">##</a></td>
      </tr>
      <tr>
        <td class="editprompt"><%=translate(mmlanguage,"search")%></td>
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
        <td class="editprompt"><%=translate(mmlanguage,"go_to")%>
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
         path=path.substring(0,path.length()-13)+"/previews/"+managerName+".jsp";
      %>
      <% boolean fileExists = false;
         try {

           fileExists = new java.io.File(pageContext.getServletContext().getRealPath(path)).exists();


         } catch(Exception e) {}
         if (fileExists) { %>
      <tr>
        <td class="editprompt">Preview</td>
        <td class="navlink"><a href="<mm:url page="<%=path%>" ><mm:param name="node"><%=nodeID%></mm:param></mm:url>" target="workarea">##</a></td>
      </tr>
      <% } %>
      <tr>
        <td class="editprompt"><%=translate(mmlanguage,"fields")%></td>
        <td class="nolink">##</td>
      </tr>
    </table><table class="editlist">
      <mm:fieldlist type="edit" nodetype="<%=managerName%>">
      <tr>
        <td class="editlink" >
          <mm:fieldinfo type="guitype" vartype="String" jspvar="guitype" >
            <% String editpath=request.getContextPath()+request.getServletPath();
               editpath=editpath.substring(0,editpath.length()-12)+"editparts/"+guitype+".jsp";
            %>
            <% boolean editFileExists = false;
               try {
                 editFileExists = new java.io.File(pageContext.getServletContext().getRealPath(editpath)).exists();
               } catch(Exception e) {}
               if (editFileExists) { %>
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
        <td class="<%=editprompt%>"><%=translate(mmlanguage,"relations")%></td>
        <td class="<%=nolink%>">##</td>
      </tr>
    </table><table class="editlist">
      <%
         // basic security shortcut, add type-based security
         String authtype=null;
         try {
            Module mmbase = cloud.getCloudContext().getModule("mmbase");
            if (mmbase!=null) authtype = mmbase.getInfo("GETAUTHTYPE");
         } catch (NotFoundException nfe) {
         }

         RelationManagerList allowedRelations = manager.getAllowedRelations();
         class ARComparator implements java.util.Comparator {
            private NodeManager manager;

            ARComparator(NodeManager manager) {
               this.manager = manager;
            }

            public int compare(Object o1, Object o2) {
                String s1 = "";
                try {
                  NodeManager mn1 = ((RelationManager)o1).getDestinationManager();
                  if (mn1.equals(manager)) {
                    mn1 = ((RelationManager)o1).getSourceManager();
                  }
                  s1=mn1.getGUIName();
                } catch (Exception e) {}
                String s2 = "";
                try {
                  NodeManager mn2 = ((RelationManager)o2).getDestinationManager();
                  if (mn2.equals(manager)) {
                    mn2 = ((RelationManager)o2).getSourceManager();
                  }
                  s2=mn2.getGUIName();
                } catch (Exception e) {}
                return s1.toUpperCase().compareTo(s2.toUpperCase());
             }
         } // MMCI doesn't sort, do it ourselves.
         java.util.Collections.sort(allowedRelations, new ARComparator(manager));

         for(Iterator allrel=allowedRelations.iterator(); allrel.hasNext();) {
           RelationManager relman=(RelationManager)allrel.next();
           NodeManager mn = null;
           String relrole = null;
           boolean allowed = relman.mayCreateNode();
           if (allowed) {
              try {
                mn = relman.getDestinationManager();
                relrole = relman.getForwardGUIName();
                if (mn.getName().equals(manager.getName())) {
                  mn = relman.getSourceManager();
                  if (!mn.getName().equals(manager.getName())) relrole = relman.getReciprocalGUIName();
                }

                // check for older code
                if ("basic".equals(authtype)) {
                  allowed = mn.mayCreateNode();
                } else {
                  allowed = true;
                }

              } catch (Exception e) {
                allowed=false;
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

      <mm:list nodes="<%=nodeID%>" path='<%=managerName+","+relman.getForwardRole()+","+mn.getName()%>'>
        <% boolean mayeditrel=false; %>
        <mm:node element="<%=relman.getForwardRole()%>">
           <mm:maywrite><% mayeditrel=true; %></mm:maywrite>
        </mm:node>
        <% if (mayeditrel) { %>
        <tr>
          <td class="<%=relationlink%>">
          <% if ("none".equals(currentState)) { %>
            <a href="<mm:url page="editor.jsp">
                <mm:param name="node"><mm:field name='<%=relman.getForwardRole()+".number"%>' /></mm:param>
                <mm:param name="manager"><%=relman.getName()%></mm:param>
                <mm:param name="depth"><%=states.size()%></mm:param>
              </mm:url>" target="_top">##</a>
          <% } else { %>##<% } %>
          </td>
          <td class="<%=fieldprompt%>">
            <%=mn.getGUIName()%>
              <br />(<%=relrole%>)
          </td>
          <td class="<%=edittext%>"><mm:field name='<%=mn.getName()+".gui()"%>' /></td>
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
        <td class="editprompt"><%=translate(mmlanguage,"save_changes")%></td>
        <% if (role==null) { %>
          <td class="navlink"><a href="<mm:url page="editor.jsp"><mm:param name="action">save</mm:param></mm:url>" target="_top">##</a></td>
        <% } else { %>
          <td class="navlink"><a href="<mm:url page="editor.jsp"><mm:param name="action">save</mm:param></mm:url>" target="_top">##</a></td>
        <% } %>
      </tr>
      <tr>
        <td class="editprompt"><%=translate(mmlanguage,"do_not_save_changes")%></td>
        <td class="navlink"><a href="<mm:url page="editor.jsp"><mm:param name="action">cancel</mm:param></mm:url>" target="_top">##</a></td>
      </tr>
      <% } else if (isRelation) { %>
        <tr>
          <td class="editprompt"><%=translate(mmlanguage,"remove_relation")%></td>
          <td class="navlink" ><a href="deletenode.jsp" target="workarea">##</a></td>
        </tr>
        <tr>
          <td class="editprompt"><%=translate(mmlanguage,"back")%></td>
          <td class="navlink"><a href="<mm:url page="editor.jsp" ><mm:param name="manager">?</mm:param><mm:param name="depth"><%=states.size()-1%></mm:param></mm:url>" target="_top">##</a></td>
        </tr>
      <% } else if (node.hasRelations()) { %>
          <tr>
            <td class="disabledprompt"><%=translate(mmlanguage,"remove_object")%>
            (<%=node.countRelations()%> <%=translate(mmlanguage,"relations")%>)
            </td>
            <td class="disabledlink">##</td>
          </tr>
      <% } else { %>
        <tr>
          <td class="editprompt"><%=translate(mmlanguage,"remove_object")%></td>
          <td class="navlink" ><a href="deletenode.jsp" target="workarea">##</a></td>
        </tr>
      <% } %>
    </table>
  </mm:cloud>
  </body>
</html>

