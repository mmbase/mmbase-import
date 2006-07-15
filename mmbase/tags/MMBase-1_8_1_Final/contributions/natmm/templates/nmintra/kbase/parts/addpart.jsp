  <%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
  <%@include file="basics.jsp"%>
  <mm:cloud jspvar="wolk" method="asis" >
  <mm:import externid="kb_submit" />
  <mm:import externid="expanded"      jspvar="expanded" vartype="String"/>
  <mm:import externid="node"                jspvar="node" vartype="String"/>
  <mm:import externid="qnode"           jspvar="qnode" vartype="String"/>
  <mm:import externid="action"          jspvar="action" vartype="String" />
  <mm:import externid="type"                jspvar="type" vartype="String" />
    
  
  <div class="form">
    <mm:present referid="node">
        <mm:notpresent referid="kb_submit">
            <!-- the title of the form. some options must be covered -->

      <%-- formbody --%>
    
      <h3><mm:write referid="add" /> <mm:write referid="<%= type %>" /></h3>
      <form method="post" action="index.jsp">
                    <%-- first the reference params --%>
                <%=getParamsFormatted(request,"form",getExtraParams(request))%>
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
              <tr>
                <th><mm:fieldinfo type="guiname"/></th>
                <td><mm:fieldinfo type="input"/></td>
              </tr>
            </mm:fieldlist>
            <td  colspan="2"><input type="submit" name="kb_submit" value="<mm:write referid="commit" />" style="margin: 5px;"/><input style="margin: 5px;" type="button" name="cancel" value="<mm:write referid="cancel" />" onClick="history.back(1)"</td>
           </table>
           <h4><mm:write referid="markupexplanation" /></h4>
      </form>
    </body>
</html>
</mm:notpresent>
    
        
        
        
        <mm:present referid="kb_submit">
    
    <%--Eerst kijken of er een node moet worden aangemaakt--%>
    <%
      //eerst een nieuwe node aanmaken
      Node newNode=wolk.getNodeManager("kb_"+type).createNode();
      updateNode(newNode, request);

      newNode.setLongValue("date",new Date().getTime()/1000);
      newNode.setStringValue("visible","true");
      newNode.commit();
      
      //out.write("node="+node);
      //en nu een relatie naar de parent
      String s=(type.equals("answer")?qnode:node);
      Node parent=wolk.getNode(s);
      
      try{
        Relation r=parent.createRelation(newNode, wolk.getRelationManager("related"));
        r.commit();
        
        //en nu terug naar hoofdpagina
        
        //ik weet niet zeker of dit nodig is, volgens mij komt dit nooit voor
        if(expanded==null)expanded=",";
        
        //de qnode parameter moet worden aangepast als er een question is aange-
        //maakt
        String qnodeParam="";
        if(type.equals("question")) {
          qnodeParam="&qnode="+newNode.getStringValue("number");
        }else{
          if(qnode!=null)qnodeParam="&qnode="+qnode;
        }
        
        //de node en de expanded parameters moet worden aangepast als de nieuwe 
        //node een categorie is
        String nodeParam="";
        if(type.equals("category")){
          expanded+=node+",";
          nodeParam="?node="+newNode.getStringValue("number");
        }else {
          nodeParam="?node="+node;
        }
        
       //String portalpageParam=(portal!=null?"&portal="+portal+"&page="+kb_page:"");
       String extraParamsUrl=(getExtraParams(request)!=null?"&":"")+getParamsFormatted(request,"url",getExtraParams(request));
       String expandedParam="&expanded="+expanded;
        //response.sendRedirect("index.jsp?node="+node+qnodeParam+expanded);
        String redirect="index.jsp"+nodeParam+qnodeParam+extraParamsUrl+expandedParam;
%>
      <script language="javascript" >
        document.location="<%=redirect%>";
      </script>
<%
      } catch(Exception e){
        out.write(e.toString());
        out.write("damn!");
      }
    %>

    </mm:present>
    </mm:present>
  </mm:cloud>
