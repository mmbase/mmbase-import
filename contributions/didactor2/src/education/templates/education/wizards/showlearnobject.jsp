<mm:node number="<%= learnobjects2_number %>">
   <%@include file="whichimage.jsp"%>
   <mm:import id="objecttype" reset="true" jspvar="dummyName" vartype="String"><mm:nodeinfo type="type" /></mm:import>

   <mm:compare referid="objecttype" valueset="couplingquestions,dropquestions,hotspotquestions,mcquestions,openquestions,rankingquestions,valuequestions,fillquestions,fillselectquestions,opennumeralquestions,essayquestions,openvaluequestions" inverse="true">
   

      <mm:compare referid="objecttype" valueset="learnblocks" inverse="true">
         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <mm:compare referid="the_last_parent" value="true" inverse="true">
                  <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                  <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
               </mm:compare>
               <mm:compare referid="the_last_parent" value="true">
                  <td><img src="gfx/tree_spacer.gif" width="48px" height="16px" border="0" align="center" valign="middle"/></td>
               </mm:compare>

               <%@include file="tree_shift_child.jsp" %>

               <mm:compare referid="the_last_element" value="true" inverse="true">
                  <mm:compare referid="the_last_leaf_in_this_level" value="true">
                     <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
                  </mm:compare>
                  <mm:compare referid="the_last_leaf_in_this_level" value="true" inverse="true">
                     <td><img src="gfx/tree_vertline-leaf.gif" border="0" align="center" valign="middle"/></td>
                  </mm:compare>
               </mm:compare>
               <mm:compare referid="the_last_element" value="true">
                  <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
               </mm:compare>
               <td><img src="gfx/edit_learnobject.gif" width="16" border="0" align="middle" /></td>
               <td>
                 <nobr>
                   <a href='<mm:write referid="wizardjsp"/>&wizard=config/<mm:write referid="objecttype" />/<mm:write referid="objecttype" />&objectnumber=<mm:field name="number" />&origin=<mm:field name="number" />' title='<di:translate key="education.edit" /> <%= dummyName.toLowerCase() %>' target="text"><mm:field name="name"><mm:isempty><mm:field name="title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field></a>
                   <mm:present referid="pdfurl">
                     <mm:compare referid="objecttype" value="pages">
                       <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                     </mm:compare>
                     <mm:compare referid="objecttype" value="learnblocks">
                       <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                     </mm:compare>
                   </mm:present>
                   <mm:field write="false" name="number" id="node_number" />
                   <mm:node number="component.metadata" notfound="skip">
                     <a href='metaedit.jsp?number=<mm:write referid="node_number" />' target='text'><img id='img_<mm:field name="number"/>' src='<%= imageName %>' border='0' title='<%= sAltText %>' alt='<%= sAltText %>'></a>
                   </mm:node>
                   <mm:node number="component.versioning" notfound="skip">
                     <a href="versioning.jsp?nodeid=<mm:write referid="node_number" />" target="text"><img src="gfx/versions.gif" border="0"></a>
                   </mm:node>
                   <mm:remove referid="node_number" />
                </nobr>
              </td>
            </tr>
         </table>
<%--
         <mm:compare referid="the_last_leaf_in_this_level" value="true">
               end0
            </div>
         </mm:compare>
--%>

      </mm:compare>




      <mm:compare referid="objecttype" valueset="learnblocks">

         <table border="0" cellpadding="0" cellspacing="0">
            <tr>
               <mm:compare referid="the_last_parent" value="true" inverse="true">
                  <td><img src="gfx/tree_spacer.gif" width="32px" height="16px" border="0" align="center" valign="middle"/></td>
                  <td><img src="gfx/tree_vertline.gif" border="0" align="center" valign="middle"/></td>
               </mm:compare>
               <mm:compare referid="the_last_parent" value="true">
                  <td><img src="gfx/tree_spacer.gif" width="48px" height="16px" border="0" align="center" valign="middle"/></td>
               </mm:compare>


               <%@include file="tree_shift_child.jsp" %>

               <mm:compare referid="the_last_element" value="true" inverse="true">
                  <mm:compare referid="the_last_leaf_in_this_level" value="true">
                     <td><a href='javascript:clickNode("<%= learnobjects2_number %>")'><img src="gfx/tree_pluslast.gif" border="0" align="center" valign="middle" id="img_<%= learnobjects2_number %>"/></a></td>
                  </mm:compare>
                  <mm:compare referid="the_last_leaf_in_this_level" value="true" inverse="true">
                     <td><a href='javascript:clickNode("<%= learnobjects2_number %>")'><img src="gfx/tree_plus.gif" border="0" align="center" valign="middle" id="img_<%= learnobjects2_number %>"/></a></td>
                  </mm:compare>
               </mm:compare>
               <mm:compare referid="the_last_element" value="true">
                  <td><img src="gfx/tree_leaflast.gif" border="0" align="center" valign="middle"/></td>
               </mm:compare>
               <td><img src="gfx/folder_closed.gif" border="0" align="middle" id="img2_<%= learnobjects2_number %>"/></td>
               <td>&nbsp;<nobr>
                 <a href='<mm:write referid="wizardjsp"/>&wizard=config/<mm:write referid="objecttype" />/<mm:write referid="objecttype" />&objectnumber=<mm:field name="number" />&origin=<mm:field name="number" />' title='<di:translate key="education.edit" /> <%= dummyName.toLowerCase() %>' target="text"><mm:field name="name"><mm:isempty><mm:field name="title"/></mm:isempty><mm:isnotempty><mm:write/></mm:isnotempty></mm:field></a>
                 <mm:present referid="pdfurl">
                   <mm:compare referid="objecttype" value="pages">
                     <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                   </mm:compare>
                   <mm:compare referid="objecttype" value="learnblocks">
                     <a href='<mm:write referid="pdfurl"/>&number=<mm:field name="number"/>' target='text'><img src='gfx/icpdf.gif' border='0' title='(PDF)' alt='(PDF)'/></a>
                   </mm:compare>
                 </mm:present>
                 <mm:field write="false" name="number" id="node_number" />
                 <mm:node number="component.metadata" notfound="skip">
                   <a href='metaedit.jsp?number=<mm:write referid="node_number" />' target='text'><img id='img_<mm:field name="number"/>' src='<%= imageName %>' border='0' title='<%= sAltText %>' alt='<%= sAltText %>'></a>
                 </mm:node>
                 <mm:node number="component.versioning" notfound="skip">
                   <a href="versioning.jsp?nodeid=<mm:write referid="node_number" />" target="text"><img id="img_<mm:field name="number"/>" src="gfx/versions.gif" border="0"></a>
                 </mm:node>
                 <mm:remove referid="node_number" />
               </nobr></td>
            </tr>
         </table>
         <div id="<%= learnobjects2_number %>" style="display:none">

         <%
            //Teporaly increase the depth for next level of learnblock
            depth++;
         %>
         <%// Is this learnobject empty? If so, we have to close the branch %>
         <mm:import reset="true" id="the_last_element">true</mm:import>
         <mm:relatednodes type="learnobjects" searchdir="destination" max="1">
            <mm:import reset="true" id="the_last_element">false</mm:import>
         </mm:relatednodes>
         <mm:relatednodes type="tests" searchdir="destination" max="1">
            <mm:import reset="true" id="the_last_element">false</mm:import>
         </mm:relatednodes>
         <mm:relatednodes type="pages" searchdir="destination" max="1">
            <mm:import reset="true" id="the_last_element">false</mm:import>
         </mm:relatednodes>
         <mm:relatednodes type="flashpages" searchdir="destination" max="1">
            <mm:import reset="true" id="the_last_element">false</mm:import>
         </mm:relatednodes>
         <mm:relatednodes type="htmlpages" searchdir="destination" max="1">
            <mm:import reset="true" id="the_last_element">false</mm:import>
         </mm:relatednodes>

         <%@include file="newfromtree.jsp" %>
<%--
         <mm:compare referid="the_last_element" value="true">
               end1
            </div>
         </mm:compare>
--%>

         <mm:import reset="true" id="the_last_element">false</mm:import>
         <%
            //Back to current level
            depth--;
         %>
      </mm:compare>
   </mm:compare>

</mm:node>
