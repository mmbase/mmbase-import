	<mm:present referid="node">
	<mm:notpresent referid="submit">
	<!-- the title of the form. some options must be covered -->
	<div class="form">
	<h3>Edit a <%=type%></h3>
      <%-- formbody --%>
 
      <mm:node number="<%=getEditNode(request)%>" notfound="skipbody" jspvar="editNode">
			
      
      <form method="post" action="edit.jsp">
					<%-- first the reference params --%>
          <input type="hidden" name="node" value="<mm:write referid="node"/>">
          <input type="hidden" name="type" value="<mm:write referid="type"/>">
          <input type="hidden" name="action" value="<mm:write referid="action"/>">
          <mm:present referid="qnode">
            <input type="hidden" name="qnode" value="<mm:write referid="qnode"/>">
          </mm:present>
          <mm:present referid="anode">
             <input type="hidden" name="anode" value="<mm:write referid="anode"/>">
          </mm:present>
          <mm:present referid="expanded">
            <input type="hidden" name="expanded" value="<mm:write referid="expanded"/>">
          </mm:present>          
          <table cellspacing="0" cellpadding="0" border="0" bordercolor="red" class="list" width="97%">
          <%-- then the dropdown with possible new parents --%>
          <%-- this applies to categories and questions, but not to answers --%>
            <mm:compare referid="type" value="answer" inverse="true">
              <%-- hier moet de iteratie komen voor de mogelijke parents--%>
              <tr>
                <th>choose a new parent folder</th>
                <td>
                <select option name="newparent">
              <%
                String editNodeNumber=editNode.getStringValue("number");
                //de hashmap is als zodanig ingericht:
                //key:    number van possible parentnode
                //value:  naam van possible parentnode
                HashMap options=getPossibleParents(editNodeNumber, wolk);
                Iterator i=options.keySet().iterator();
                Object key;
                String selected;
                String currentParent=getParent(editNodeNumber ,wolk);
                String possibleParent="";
                while (i.hasNext()){
                  key=i.next();
                  possibleParent=(String)key;
                  selected=(currentParent.equals(possibleParent)?"selected":"");
                  out.write("<option value=\""+((String)key)+"\""+selected+" >");
                  out.write(options.get(key)+"</option>");
                }
              %>
              </select>
                </td>
              </tr>
            </mm:compare>
            
          <%-- whatever the type, it is possible to make this item invisible--%>
          <tr>
            <th>visible</th>
            <td>
              <select option name="visible">
                <option value="true" <mm:field name="visible" id="v"><mm:compare referid="v" value="true">selected</mm:compare></mm:field>>true</option>
                <mm:remove referid="v"/>
                <option value="false" <mm:field name="visible" id="v"><mm:compare referid="v" value="false">selected</mm:compare></mm:field>>false</option>
              </select>
             </td>
          </tr>
            
					<%-- and now the list of editable fields--%>
	    <mm:fieldlist nodetype="kb_${type}" fields="<%=getFieldList(type)%>">
              <tr>
                <th><mm:fieldinfo type="name"/></th>
                <td><mm:fieldinfo type="input"/></td>
            </mm:fieldlist>
              </tr>
            </mm:node>
            <tr>
              <td  colspan="2"><input type="submit" name="submit" value="submit" style="margin: 5px;"/><input style="margin: 5px;" type="button" name="cancel" value="cancel" onClick="history.back(1)"</td>
            </tr>
            </form>
            </table>
	    <h4>By marking up codesamples with &lt;code&gt; .. &lt;/code&gt; tags, they will be formatted to stand out from the text.</h4>
           <%--/mm:present--%>
	</body>
</html>
      </mm:notpresent>
	
		
		
		
	<mm:present referid="submit">
    
    <%
        
        //te editen node openen. dit is of een 'question' of een 'category'
        String editNodeNumber=getEditNode(request);
        String parent=getParent(editNodeNumber,wolk);
        Node editNode=wolk.getNode(editNodeNumber);
        
        //kijken of parent moet worden veranderd. Alleen als type niet 'answer' is
        if(!"answer".equals(type)){
          if(!parent.equals(newParent)){
            //relatie tussen node en parent weggooien
            Node newParentNode=wolk.getNode(newParent);
            String whatPath="", whatConstraints="";
            if(type.equals("question")){
              whatPath="category,related,question";
              whatConstraints="category.number='"+parent+"' AND question.number='"+editNodeNumber+"'";
            }else{
              whatPath="category,related,category1";
              whatConstraints="category.number='"+parent+"' AND category1.number='"+editNodeNumber+"'";
            }
  
            //oude relatie weggooien
            NodeList nl=wolk.getList(null,whatPath,"related.number",whatConstraints,null,null,null,false);
            Relation rel=wolk.getRelation(nl.getNode(0).getStringValue("related.number"));
            if (rel!=null)rel.delete();
            
            //nieuwe relatie maken
            try{
              Relation r=newParentNode.createRelation(editNode, wolk.getRelationManager("related"));
              r.commit();
            } catch(Exception e){
              out.write(e.toString());
            }
          }
        }
        
        //visibility
        editNode.setStringValue("visible",request.getParameter("visible"));
        
        //en nu velden invoeren
	updateNode(editNode, request);
        editNode.commit();
        
        //en nu terug naar hoofdpagina
        String qnodeParam=(request.getParameter("qnode")!=null?"&qnode="+request.getParameter("qnode"):"");
        String expanded=(request.getParameter("expanded")!=null?"&expanded="+request.getParameter("expanded"):"");
        response.sendRedirect("index.jsp?node="+node+qnodeParam+expanded);
    

    %>
		</mm:present>
	
    </mm:present>
