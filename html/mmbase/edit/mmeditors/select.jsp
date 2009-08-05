<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@include file="header.jsp" %><html xmlns="http://www.w3.org/1999/xhtml">
  <mm:cloud method="http" jspvar="cloud" rank="$rank">
  <%  Stack states = (Stack)session.getValue("mmeditors_states");
      Properties state = (Properties)states.peek();
      String managerName = state.getProperty("manager");
      String transactionID = state.getProperty("transaction");
      // close current transaction
      if (transactionID != null) {
           Transaction trans = cloud.getTransaction(transactionID);
           trans.cancel();
           transactionID = null;
           state.remove("node");
           state.remove("transaction");
      }
     Module mmlanguage;
     try {
        mmlanguage = cloud.getCloudContext().getModule("mmlanguage");
     } catch (NotFoundException nfe) {
        mmlanguage = null;
     }
  %>
  <head>
    <title>Editor</title>
    <link rel="stylesheet" href="css/mmeditors.css" type="text/css" />
  </head>
  <body>
    <form method="post" action="listobjects.jsp" target="workarea">
      <table class="editlist">
        <% if (states.size() == 1) { %>
        <tr>
          <td class="editprompt"><%=translate(mmlanguage,"other_editors")%></td>
          <td class="navlink"><a href="index.jsp" target="_top">##</a></td>
        </tr>
        <% } %>
        <tr>
          <td class="editprompt"><%=translate(mmlanguage,"search")%></td>
          <td class="nolink">##</td>
        </tr>
      </table>

      <table class="editlist">
        <mm:fieldlist type="search" nodetype="<%=managerName%>">
        <tr>
          <td class="fieldsearchprompt"><mm:fieldinfo type="guiname" /></td>
          <td class="fieldsearch"><input type="text" name="search_form_<mm:fieldinfo type="name" />" class="fieldsearch" /><input type="hidden" name="search_form_<mm:fieldinfo type="name" />_search" value="true" /></td>
        </tr>
        </mm:fieldlist>
        <tr>
          <td class="fieldsearchprompt"><%=translate(mmlanguage,"age")%></td>
          <td class="fieldsearch"><input name="search_age" type="text"
             value="<mm:listnodes type="typedef" constraints="<%=\"[name]='\"+managerName+\"'\"%>" max="1"><mm:field name="defaultsearchage()" /></mm:listnodes>"
             class="fieldsearch"/></td>
        </tr>
      </table>

      <table class="editlist">
        <tr>
            <td class="editprompt"><%=translate(mmlanguage,"ok")%></td>
            <td class="navlink"><input type="image" src="gfx/btn.green.gif" /></td>
         </tr>
        <tr>
            <td class="editprompt"><%=translate(mmlanguage,"new")%></td>
            <td class="navlink"><a href="createnode.jsp" target="contentarea">##</a></td>
        </tr>
        <% if (states.size() != 1) { %>
        <tr>
          <td class="editprompt"><%=translate(mmlanguage,"back")%></td>
          <td class="navlink"><a href="<mm:url page="editor.jsp" ><mm:param name="manager">?</mm:param><mm:param name="depth"><%=states.size()-1%></mm:param></mm:url>" target="_top">##</a></td>
        </tr>
        <% } %>
      </table>
    </form>
  </body>
  </mm:cloud>
</html>
