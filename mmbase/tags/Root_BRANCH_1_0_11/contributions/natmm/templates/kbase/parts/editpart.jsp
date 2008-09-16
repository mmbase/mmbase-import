  <%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  <%@ include file="basics.jsp"%>
  <mm:cloud jspvar="wolk" method="asis" >
    
  <mm:import externid="kb_submit" />
  <mm:import externid="expanded" jspvar="expanded" vartype="String"/>
  <mm:import externid="node" jspvar="node" vartype="String"/>
  <mm:import externid="qnode" jspvar="qnode" vartype="String"/>
  <mm:import externid="anode" jspvar="anode" vartype="String"/>
  <mm:import externid="action" jspvar="action" vartype="String" />
  <mm:import externid="type" jspvar="type" vartype="String" />
    <mm:import externid="newparent" jspvar="newParent" vartype="String" />
    <mm:present referid="node">
      <mm:notpresent referid="kb_submit">
    <!-- the title of the form. some options must be covered -->
    <div class="form">
    <h3><mm:write referid="edit" /> <mm:write referid="<%= type %>" /></h3>
        <%-- formbody --%>
   
      <mm:node number="<%=getEditNode(request)%>" notfound="skipbody" jspvar="editNode">
            
      
      <form method="post" action="index.jsp">
                    <%-- first the reference params --%>
          <%=getParamsFormatted(request,"form",getExtraParams(request))%>
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
                <th><mm:write referid="choosenewparentfolder" /></th>
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
            <th><mm:write referid="visible"/></th>
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
                <th><mm:fieldinfo type="guiname"/></th>
                <td><mm:fieldinfo type="input"/></td>
            </mm:fieldlist>
              </tr>
            </mm:node>
            <tr>
              <td  colspan="2"><input type="submit" name="kb_submit" value="<mm:write referid="commit" />" style="margin: 5px;"/><input style="margin: 5px;" type="button" name="cancel" value="<mm:write referid="cancel" />" onClick="history.back(1)"</td>
            </tr>
            </form>
            </table>
        <h4><mm:write referid="markupexplanation" /></h4>
           <%--/mm:present--%>
    </body>
</html>
      </mm:notpresent>
    
        
        
        
    <mm:present referid="kb_submit">
          
    <%
        
        //te editen node openen. dit is of een 'question' of een 'category'
        String editNodeNumber=getEditNode(request);
        String parent=getParent(editNodeNumber,wolk);
        Node editNode=wolk.getNode(editNodeNumber);
        boolean parentHasChanged=false;
        
        //kijken of parent moet worden veranderd. Alleen als type niet 'answer' is
        if(!"answer".equals(type)){
          if(!parent.equals(newParent)){
            //relatie tussen node en parent weggooien
            parentHasChanged=true;
            Node newParentNode=wolk.getNode(newParent);
            String whatPath="", whatConstraints="";
            if(type.equals("question")){
              whatPath="kb_category,related,kb_question";
              whatConstraints="kb_category.number='"+parent+"' AND kb_question.number='"+editNodeNumber+"'";
            }else{
              whatPath="kb_category,related,kb_category1";
              whatConstraints="kb_category.number='"+parent+"' AND kb_category1.number='"+editNodeNumber+"'";
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
        
        //als de parent is veranderd dan moet het pad ook worden aangepast
        if(parentHasChanged){
          if(type.equals("question")){
            node=newParent;
            expanded=mergePaths(expanded,getPath(newParent,wolk));
          }else if(type.equals("category")){
            //'node' blijft hetzelfde, want zelfde categrie blijft geselecteerd.
            //alleen het pad is veranderd.
            //TODO: filter pad naar oude locatie uit 'expanded'
            expanded=mergePaths(expanded,getPath(node,wolk));
          }
        }
        
        //en nu terug naar hoofdpagina
                String qnodeParam=(request.getParameter("qnode")!=null?"&qnode="+request.getParameter("qnode"):"");
        String extraParamsUrl=(getExtraParams(request)!=null?"&":"")+getParamsFormatted(request, "url", getExtraParams(request));
        String expandedParam=(expanded!=null?"&expanded="+expanded:"");
        String redirect="index.jsp?node="+node+extraParamsUrl+qnodeParam+expandedParam;
%>
      <script language="javascript" >
        document.location="<%=redirect%>";
      </script>
<%
    

    %>
        </mm:present>
    
    </mm:present>
        </mm:cloud>
