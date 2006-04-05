<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>

<%@page import = "java.util.ArrayList" %>
<%@page import = "java.util.Iterator" %>
<%@page import = "java.util.HashSet" %>
<%@page import = "org.mmbase.bridge.*" %>
<%@page import = "org.mmbase.bridge.util.*" %>

<%@page import = "nl.didactor.metadata.util.MetaDataHelper" %>


<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%
   Node nodeUser = nl.didactor.security.Authentication.getCurrentUserNode(cloud);

   String sVocabularyID = (String) request.getParameter("vocabulary");
   String sMetaDefinitionID = (String) request.getParameter("metadefinition");
   String sNodeObjectID = (String) request.getParameter("object");

   Node thisMetaDefinition = cloud.getNode(sMetaDefinitionID);
   Node nodeObject = cloud.getNode(sNodeObjectID);

   String sMetastandartNodes = (String) request.getParameter("metastandarts");

   NodeList nlRelatedNodes = (NodeList) session.getAttribute("metaeditor_multilevel_metavocabulary_all_metadata");

   Node nodeTemporalRootMetaVocabulary = cloud.getNode(sVocabularyID);

   int iBlockedLevel = 255;
   if("true".equals(request.getParameter("blocked"))){
       iBlockedLevel = 0;
   }
%>



<%
//   Queries.createQuery(cloud, sVocabularyID, "metavocabulary", "", "", "metavocabulary.value", "", "destination", false).

   NodeQuery nodeQuery = Queries.createNodeQuery(nodeTemporalRootMetaVocabulary);

   GrowingTreeList tree = new GrowingTreeList(nodeQuery, 30, nodeTemporalRootMetaVocabulary.getNodeManager(), "posrel", "destination");
   Query query = tree.getTemplate();
//   query.addSortOrder(new BasicStepField(Step step, new FieldDefs()) , Queries.getSortOrder("UP"));

//   System.out.println(Queries.getSortOrder("UP"));

   TreeIterator it = tree.treeIterator();


   boolean bIsMoreThanOneElement = false;
   if(it.hasNext()){
       it.next();
       if(it.hasNext()){
           bIsMoreThanOneElement = true;
       }
   }


   //TreeIterator returns to already passed nodes when it finishes subbranches
   //So we have to put them in skiplist
   //Otherwise users will see them many times
   HashSet hsetNodesToSkip = new HashSet();

   //nodeTemporalRootMetaVocabulary has been already shown
   hsetNodesToSkip.add(nodeTemporalRootMetaVocabulary);


   ArrayList arliPathToCheckBox = new ArrayList();
   arliPathToCheckBox.add(0, cloud.getNode(sVocabularyID));

   if(bIsMoreThanOneElement){
      if (iBlockedLevel == 0){
         %>
            <img id="img_layer_controller_<%= sVocabularyID %>" onClick="switchMetaVocabularyTreeVisibility('<%= sVocabularyID %>')" src="gfx/show.gif"/>
         <%
      }
      else{
         %>
            <img id="img_layer_controller_<%= sVocabularyID %>" onClick="switchMetaVocabularyTreeVisibility('<%= sVocabularyID %>')" src="gfx/hide.gif"/>
         <%
      }
      %>
         <br/>
         <div id="checkbox_layer_<%= sVocabularyID %>" style="display:;">
      <%



      it = tree.treeIterator();

      int depth = -1;

      //I can't get a copy of the treeIterator,but do have to get one.
      //So we have to count steps.
      //Stupid slution, but I hasn't find a better one
      int iCounter = 0;
      while(it.hasNext()){
         Node nodeMetaVocabulary = it.nextNode();

//         System.out.println(nodeMetaVocabulary.getNumber());

         if(hsetNodesToSkip.contains(nodeMetaVocabulary)){
             //We have returned to the top element from one of the subbranches
             //Do skip it.
             continue;
         }

         int iPreviousDepth = depth;
         depth = it.currentDepth();

         //Adding a new node number to the chain of MetaVocabularies
         //"null" means the end of the tree
         arliPathToCheckBox.add(depth - 2, nodeMetaVocabulary);
         arliPathToCheckBox.add(depth - 1, null);


         //Generating unique checkbox id
         String sCheckBoxUniqueID = new String(sVocabularyID);
         for(Iterator itVocabularyNode = arliPathToCheckBox.iterator(); itVocabularyNode.hasNext();){
            Node nodeMetaVocabularyCheckBox = (Node) itVocabularyNode.next();


            if(nodeMetaVocabularyCheckBox == null){
            //We current branch is over, but the list can have got rubbish from other branches
            //So the "null" is a end-marker.
               break;
            }

            sCheckBoxUniqueID += "_" + nodeMetaVocabularyCheckBox.getNumber();
         }


         String sElemVisibility = "";
         if(iBlockedLevel < depth){
            sElemVisibility = "none";
         }


         //"switchMetaVocabularyTree(this)" is declared in metaedit_form.jsp
         %>
            <div id="checkbox_layer_<%= sCheckBoxUniqueID %>" style="display:<%= sElemVisibility %>;">
               <mm:node number="<%= "" + nodeMetaVocabulary.getNumber() %>">
                  <%
                     if(MetaDataHelper.isTheMetaVocabularyActive(nodeMetaVocabulary, nodeObject, thisMetaDefinition, sMetastandartNodes, application)){
                        %>
                           <mm:field name="number" jspvar="sID" vartype="String" write="false">
                              <span style="padding-left:<%= (depth - 1) * 30 %>px;">&nbsp;</span>
                              <input type="checkbox" name="m<%= sMetaDefinitionID %>" value="<%= sID %>" checkbox_id="<%= sCheckBoxUniqueID %>" onClick="switchMetaVocabularyTree(this)"
                                 <%
                                    if(iBlockedLevel < depth){
                                       %> disabled="disabled" <%
                                    }

                                    if(iBlockedLevel > depth){
                                       iBlockedLevel = depth;
                                    }
                                 %>

                                 <mm:relatednodes type="metadata" jspvar="mNode">
                                    <%
                                       if(nlRelatedNodes.contains(mNode))
                                       {
                                          %> checked="checked" <%
                                           if(iBlockedLevel >= depth){
                                              iBlockedLevel = depth + 1;
                                           }
                                       }
                                    %>
                                 </mm:relatednodes>

                              />
                              <mm:field name="number" jspvar="sMetavocabularyID" vartype="String">
                                 <span onClick="switchMetaVocabularyTreeVisibility('<%= sCheckBoxUniqueID %>')">
                                    <%= MetaDataHelper.getAliasForObject(cloud, sMetavocabularyID, nodeUser.getNumber()) %>
                                 </span>
                              </mm:field>
                           </mm:field>

                        <%
                        String sSubMetaVocabulariesControllerImage = null;
                        String sState = null;

                        if(iPreviousDepth < it.currentDepth()){
                           //We don't know it is the last leaf for this branch or not
                           //So we have to check
                           boolean bIsItTheLastLevel = true;

                           TreeIterator it2 = tree.treeIterator();
                           int iLocalCounter = 0;
                           while(iLocalCounter <= iCounter + 1 ){
                               it2.next();
                               iLocalCounter++;
                           }

                           while((it.hasNext())  && (depth == it.currentDepth())){
                               it.next();
                           }

                           //we have right the node which has got different deep
                           if(depth < it.currentDepth()){
                              //This node is deepper, so it wasn't the last level
                              bIsItTheLastLevel = false;
                           }

                           //Rol the pointer back
                           it = it2;


                           if(!bIsItTheLastLevel){
                              if(iBlockedLevel - 1 < depth){
                                 sSubMetaVocabulariesControllerImage = "gfx/show.gif";
                                 sState = "closed";
                              }
                              else{
                                 sSubMetaVocabulariesControllerImage = "gfx/hide.gif";
                                 sState = "opened";
                              }
                              %><img id="img_layer_controller_<%= sCheckBoxUniqueID %>" onClick="switchMetaVocabularyTreeVisibility('<%= sCheckBoxUniqueID %>')" src="<%= sSubMetaVocabulariesControllerImage %>"/><%
                           }
                        }
                     }
                     else{
                         iBlockedLevel = it.currentDepth();
                     }
                  %>
               </mm:node>
            </div>
         <%
         iCounter ++;
      }
      %>
         </div>
      <%
   }
   else{
       %>
          <br/>
       <%
   }
%>
</mm:cloud>
