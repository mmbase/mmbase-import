	<div class="form">
	<mm:present referid="node">
		<mm:notpresent referid="submit">
			<!-- the title of the form. some options must be covered -->

      <%-- formbody --%>
    
      <h3>Add a <%=type%></h3>
      <form method="post" action="edit.jsp">
					<%-- first the reference params --%>
                <input type="hidden" name="node" value="<mm:write referid="node"/>">
                <input type="hidden" name="type" value="<mm:write referid="type"/>">
                <input type="hidden" name="action" value="<mm:write referid="action"/>">
                <mm:present referid="qnode">
                <input type="hidden" name="qnode" value="<mm:write referid="qnode"/>">
                </mm:present>
                <mm:present referid="expanded">
                  <input type="hidden" name="expanded" value="<mm:write referid="expanded"/>">
                </mm:present>
                
          <table cellspacing="0" cellpadding="0" border="0" bordercolor="red" class="list" width="97%">
	   <mm:fieldlist nodetype="kb_${type}" fields="<%=getFieldList(type)%>">
            <tr><th><mm:fieldinfo type="name"/></th><td><mm:fieldinfo type="input"/></td></tr>
           </mm:fieldlist>
           <td  colspan="2"><input type="submit" name="submit" value="submit" style="margin: 5px;"/><input style="margin: 5px;" type="button" name="cancel" value="cancel" onClick="history.back(1)"</td>
           </table>
					<h4>By marking up codesamples with &lt;code&gt; .. &lt;/code&gt; tags, they will be formatted to stand out from the text.</h4>
      </form>
	</body>
</html>
      </mm:notpresent>
	
		
		
		
		<mm:present referid="submit">
    hallo
    <%--Eerst kijken of er een node moet worden aangemaakt--%>
    <%
      //eerst een nieuwe node aanmaken
      Node newNode=wolk.getNodeManager("kb_"+type).createNode();
      updateNode(newNode, request);

      newNode.setLongValue("date",new Date().getTime()/1000);
      newNode.setStringValue("visible","true");
      newNode.commit();
      
      out.write("node="+node);
      //en nu een relatie naar de parent
      String s=(type.equals("answer")?qnode:node);
      Node parent=wolk.getNode(s);
      
      try{
        Relation r=parent.createRelation(newNode, wolk.getRelationManager("related"));
        r.commit();
        
        //en nu terug naar hoofdpagina
        String qnodeParam=(request.getParameter("qnode")!=null?"&qnode="+request.getParameter("qnode"):"");
        String expanded=(request.getParameter("expanded")!=null?"&expanded="+request.getParameter("expanded"):"");
        response.sendRedirect("index.jsp?node="+node+qnodeParam+expanded);
        //out.write("index.jsp?node="+node+qnodeParam+expanded);
        
      } catch(Exception e){
        out.write(e.toString());
        out.write("damn!");
      }
    %>
      
    </mm:present>
    </mm:present>
  
