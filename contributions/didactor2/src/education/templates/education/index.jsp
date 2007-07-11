<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib tagdir="/WEB-INF/tags/di/core" prefix="di-t" 
%><%@page import="java.util.*"
%><%--
TODO: This JSP is much too big, and polluted with all kinds of functionality.

--%><mm:content postprocessor="reducespace" expires="0" language="${requestScope.language}">

<mm:cloud rank="didactor user">

  <di:copybook><mm:relatednodes path="madetests,tests" id="madetests" element="tests"/></di:copybook>


  <mm:include page="/cockpit/cockpit_header.jsp">
    <mm:param name="reset" />
    <mm:param name="extraheader">
      <title><di:translate key="education.learnenvironmenttitle" /></title>
    </mm:param>
  </mm:include>

  <mm:hasnode number="component.drm">
    <mm:treeinclude page="/drm/testlicense.jsp" objectlist="$includePath" referids="$referids "/>
  </mm:hasnode>
  
  <mm:import externid="learnobject" jspvar="learnObject"/>
  <mm:import externid="learnobjecttype" jspvar="learnObjectType"/>
  <mm:import jspvar="educationNumber" externid="education" from="this" vartype="integer" />
  <mm:import externid="fb_madetest"/>
  
  <!--
  We are using it to show only one node in the tree
  For cross-education references  
  -->

  <mm:import externid="the_only_node_to_show"/>
  <mm:import externid="return_to"/>
  <mm:import externid="return_to_type"/>
  <%
  Set hsetThePath = null;
  %>
  <%
   //Tracing the route
   %>
   <mm:present referid="the_only_node_to_show">
     <mm:node number="$the_only_node_to_show" notfound="skip">
       <% hsetThePath = new HashSet(); %>
      <mm:tree type="learnobjects" role="posrel" searchdir="source" orderby="posrel.pos" directions="up" maxdepth="15">
         <mm:field name="number" jspvar="sLevelNumber" vartype="String">
            <%
               hsetThePath.add(sLevelNumber);
            %>
         </mm:field>
      </mm:tree>
     </mm:node>
   </mm:present>


   <%
   // It is a sad thing, but the left education menu can't be used as an include right now.
   // So if we want to use it we have to send here an exteranl URL as a parameter.
   // Probably the menu engine should be changed so that it become more readable and reusable.
   %>
   <mm:import externid="frame"/>

   <mm:node number="$user">
     <mm:nodelistfunction referids="education" name="blockedLearnBlocks" id="blocked" />
   </mm:node>

   <mm:import externid="reset" />
   <mm:present referid="reset">
     <jsp:scriptlet>session.setAttribute("educationBookmarks", null);</jsp:scriptlet>
   </mm:present>

<%
    if (educationNumber != null) {
        session.setAttribute("lasteducation", educationNumber);
    } else {
        educationNumber = (Integer) session.getAttribute("lasteducation");
    }
    if (educationNumber != null) {
        HashMap bookmarks = (HashMap) session.getAttribute("educationBookmarks");
        if (bookmarks== null) {
            bookmarks = new HashMap();
            session.setAttribute("educationBookmarks",bookmarks);
        }
        if (learnObject != null && learnObject.length() > 0) {
            bookmarks.put(educationNumber + ",learnobject", learnObject);
        } else {
            learnObject = (String) bookmarks.get(educationNumber+",learnobject");
            //System.err.println("read "+educationNumber+",learnobject="+learnObject+" from session");
            if (learnObject != null) {
                %><mm:import id="learnobject" reset="true"><%= learnObject %></mm:import><%
            }
        }
        if (learnObjectType != null && learnObjectType.length() > 0) {
            bookmarks.put(educationNumber+",learnobjecttype",learnObjectType);
        } else  {
            learnObjectType = (String) bookmarks.get(educationNumber+",learnobjecttype");
            if (learnObjectType != null) {
                %><mm:import id="learnobjecttype" reset="true"><%= learnObjectType %></mm:import><%
            }
        }
        %><mm:import id="education" reset="true"><%= educationNumber %></mm:import>
<%
    }
%>

<!-- TODO some learnblocks/learnobjects may not be visible because the are not ready for elearning (start en stop mmevents) -->
<!-- TODO when refreshing the page (F5) the old iframe content is shown -->
<!-- TODO pre and postassessment are showed in the tree -->
<!-- TODO split index and tree code in two seperate jsp templates -->
<mm:import id="gfx_item_none"><mm:treefile page="/gfx/spacer.gif" objectlist="$includePath" /></mm:import>
<mm:import id="gfx_item_opened"><mm:treefile page="/gfx/icon_arrow_tab_open.gif" objectlist="$includePath" /></mm:import>
<mm:import id="gfx_item_closed"><mm:treefile page="/gfx/icon_arrow_tab_closed.gif" objectlist="$includePath" /></mm:import>

<mm:import externid="justposted" />

<mm:link page="/education/tree.js.jsp" referids="$referids">
  <script type="text/javascript" src="${_}">
    
  </script>
</mm:link>

<%--
Something seems wrong. I think that currently, the 'lastpage' field is never filled.
--%>
<mm:listnodescontainer type="classrel">
  <mm:constraint field="snumber" value="${user}" />
  <mm:composite operator="or">
    <mm:constraint field="dnumber" value="${class}" />
    <mm:constraint field="dnumber" value="${education}" />
  </mm:composite>
  <mm:listnodes>
    <mm:field id="lastpage" name="lastpage" write="false" />
  </mm:listnodes>
</mm:listnodescontainer>
<mm:present referid="lastpage">
  <mm:hasnode number="${lastpage}">
  <script type="text/javascript">
    openContent("learnblocks", ${lastpage});
  </script>
  </mm:hasnode>
</mm:present>


<div class="rows">
  <div class="navigationbar">
    <div class="pathbar">
      <mm:node number="$education">
        <mm:field name="name"/>
      </mm:node>
    </div>
    
    <mm:import id="stepNavigator">
      <a href="javascript:previousContent();"><img src="${mm:treefile('/gfx/icon_arrow_last.gif', pageContext, includePath)}" width="14" height="14" border="0" 
                                                   title="${di:translate(pageContext, 'education.previous')}" 
                                                   alt="${di:translate(pageContext, 'education.previous')}" 
                                                   />
      </a>
      <a href="javascript:previousContent();" class="path"><di:translate key="education.previous" /></a><img src="${mm:url('gfx/spacer.gif', pageContext)}" width="15" height="1" title="" alt="" /><a href="javascript:nextContent();" class="path"><di:translate key="education.next" /></a>
      <a href="javascript:nextContent();"><img src="<mm:treefile write="true" page="/gfx/icon_arrow_next.gif" objectlist="$includePath" />" width="14" height="14" border="0" title="<di:translate key="education.next" />" alt="<di:translate key="education.next" />" /></a>
    </mm:import>
    <div class="stepNavigator">
      <mm:write referid="stepNavigator" escape="none" />
    </div>
     </div>
     <div class="folders">
       <div class="folderHeader">
         <di:translate key="education.education" />
       </div>
       
       <div class="folderLesBody">
         <mm:node number="$education" notfound="skip">
            <script type="text/javascript">
              <!--
               addContent('<mm:nodeinfo type="type"/>','<mm:field name="number"/>');
               //-->
            </script>

            <img class="imgClosed" src="${gfx_item_closed}" id="img${_node}" 
                 onclick="openClose('div${_node}','img${_node}')" title="" alt="" />
            <mm:nodeinfo type="type">
              <a href="#" onclick="if(openOnly('div${_node}','img${_node}')) { openContent( '${_}','${_node}' ); }"><mm:field name="name"/></a>
            </mm:nodeinfo>

            <mm:field id="previousnumber" name="number" write="false"/>
            <mm:relatednodescontainer type="learnobjects" role="posrel">
               <mm:sortorder field="posrel.pos" direction="up"/>
               <mm:import id="showsubtree" reset="true">true</mm:import>

               <mm:tree type="learnobjects" role="posrel" searchdir="destination" orderby="posrel.pos" directions="up" maxdepth="15">
                  <mm:import id="learnobjectnumber" jspvar="sCurrentTreeLeafID" vartype="String"><mm:field name="number"/></mm:import>
                  <%
                     if((hsetThePath == null) || (hsetThePath.contains(sCurrentTreeLeafID))){
                        %>
                           <mm:nodeinfo type="type" id="nodetype" write="false" />
                           <mm:relatednodes id="tests" type="tests" role="posrel" />   
                           <mm:import id="previousmadetest">${madetest}</mm:import>
                           <mm:import id="madetest">${mm:contains(madetests, tests)}</mm:import>
                           <mm:depth id="currentdepth" write="false" />

                           <mm:import id="block_this_first_htmlpage" reset="true">false</mm:import>
                           <mm:compare referid="nodetype" value="htmlpages">
                              <mm:related path="posrel,learnblocks" directions="up">
                                 <mm:node element="posrel">
                                    <mm:import id="htmlpage_number" reset="true"><mm:field name="pos"/></mm:import>
                                    <mm:compare referid="htmlpage_number" value="-1">
                                       <mm:import id="block_this_first_htmlpage" reset="true">true</mm:import>
                                    </mm:compare>
                                 </mm:node>
                              </mm:related>
                           </mm:compare>


                           <mm:compare referid="showsubtree" value="false">
                              <mm:isgreaterthan inverse="true" referid="currentdepth" referid2="ignoredepth">
                                 <%-- we are back on the same or lower level, so we must show the learnobject again --%>
                                 <mm:import id="showsubtree" reset="true">true</mm:import>
                              </mm:isgreaterthan>
                           </mm:compare>

                           <mm:compare referid="showsubtree" value="true">

                              <mm:grow>
                                <mm:depth>
                                  <div id="div${previousnumber}" class="lbLevel${_}">
                                    <mm:compare referid="nodetype" valueset="educations,learnblocks,tests,pages,flashpages,preassessments,postassessments">
                                      <script type="text/javascript">
                                        document.getElementById("img${previousnumber}").setAttribute("haschildren", 1);
                                      </script>
                                    </mm:compare>
                                    <mm:onshrink>
                                       </div>
                                   </mm:onshrink>
                                 </mm:depth>
                              </mm:grow>

                              <mm:remove referid="previousnumber"/>
                              <mm:field id="previousnumber" name="number" write="false" />

                              <%-- determine if we may show this learnobject and its children --%>
                              <mm:import id="mayshow"><di:getvalue component="education" name="showlo" arguments="${previousnumber}" /></mm:import>

                              <%-- if 'showlo' is 0, then we may not show the subtree, so we ignore everything with a depth HIGHER than the current depth --%>
                              <mm:compare referid="mayshow" value="0">
                                 <mm:import id="showsubtree" reset="true">false</mm:import>
                                 <mm:import id="ignoredepth" reset="true"><mm:write referid="currentdepth" /></mm:import>
                                 <!-- Ignored subtree at depth <mm:write referid="currentdepth" /> -->
                              </mm:compare>

                              <mm:compare referid="showsubtree" value="true">

                                 <%// have to skip the first entrance in scorm tree %>
                                 <mm:compare referid="block_this_first_htmlpage" value="false">
                                    <mm:compare referid="nodetype" valueset="educations,learnblocks,tests,pages,flashpages,preassessments,postassessments,htmlpages">
                                      <div 
                                          class="${madetest ? 'completed' : (previousmadetest ? 'first_non_completed' : 'non_completed')} ${mm:contains(blocked, _node) ? 'blocked' : ''}"
                                          style="padding: 0px 0px 0px ${currentdepth * 8 + 18}px;" id="content-${_node}">
                                        <script type="text/javascript">
                                          <!--
                                             addContent('<mm:nodeinfo type="type"/>','<mm:field name="number"/>');
                                          //-->
                                          </script>



                                          <mm:present referid="the_only_node_to_show">
                                             <img class="imgClosed" src="${gfx_item_closed}" id="img${_node}"
                                                  onclick="" style="margin: 0px 4px 0px -18px; padding: 0px 0px 0px 0px" title="" alt="" />
                                             <mm:link page="." referids="provider,learnobjecttype,education,class,fb_madetest,learnobjectnumber@learnobject">
                                               <a href="${_}" style="padding-left: 0px"><mm:field name="name"/></a>
                                             </mm:link>
                                          </mm:present>

                                          <mm:notpresent referid="the_only_node_to_show">                                            
                                            <mm:nodeinfo type="type" >                                              
                                              <img class="imgClosed" 
                                                   src="${gfx_item_closed}" id="img${_node}" 
                                                   onclick="openClose('div${_node}', 'img${_node}')" 
                                                   style="margin: 0px 4px 0px -18px; padding: 0px 0px 0px 0px" title="" alt="" />
                                              <a href="#" onclick="if (openOnly('div${_node}','img${_node}')) { openContent('${_}', '${_node}' ); }" 
                                                 style="padding-left: 0px"><mm:field name="name"/></a>
                                            </mm:nodeinfo>
                                          </mm:notpresent>

                                          <mm:node number="component.pop" notfound="skip">
                                             <mm:relatednodes type="providers" constraints="providers.number=$provider">
                                                <mm:list nodes="$user" path="people,related,pop">
                                                   <mm:first><%@include file="popcheck.jsp" %></mm:first>
                                                </mm:list>
                                             </mm:relatednodes>
                                          </mm:node>
                                       </div>
                                    </mm:compare>
                                 </mm:compare>
                              </mm:compare>
                              <mm:shrink/>
                           </mm:compare>
                        <%
                     }
                  %>
               </mm:tree>
            </mm:relatednodescontainer>
         </mm:node>
      </div>
   </div>



   <script type="text/javascript">

      rightframesrc = "---";
      function resize() {
/*
         var frameElem = document.getElementById("content");
         alert(frameElem.contentWindow.document.body.clientHeight + " " + frameElem.contentWindow.document.body.scrollHeight);
//         alert(divleftMenu.innerHTML());
         iframedoc.onupdate = resize;
         var frameContentHeight = frameElem.contentWindow.document.body.scrollHeight;
//         contentBodywit.style.height = frameContentHeight + 80;
         if(frameElem.contentWindow.document.body.clientHeight + 20 < frameElem.contentWindow.document.body.scrollHeight)
         {
            frameElem.height = frameContentHeight + 0;
         }
*/
         if(rightframesrc != frames['content'].location.href)
         {
            if(browserVersion()[0] == "IE")
            {
               var oBody = content.document.body;
               var oFrame = document.all("content");

               oFrame.style.height = oBody.scrollHeight + 280;
            }
            else
            {
               var frameElem = document.getElementById("content");
               frameElem.style.overflow = "";
               var frameContentHeight = frameElem.contentWindow.document.body.scrollHeight;
               frameElem.style.height = frameContentHeight + 80;
               frameElem.height = frameContentHeight + 80;
               frameElem.style.overflow = "hidden";
            }
//            alert(rightframesrc);
         }
         rightframesrc = frames['content'].location.href;
      }
   </script>

   <div class="mainContent">
      <div class="contentHeader">
         &nbsp;
      </div>
      <div class="contentBodywit" id="contentBodywit">
         <mm:present referid="the_only_node_to_show">
           <div align="right"><input type="submit" class="formbutton" value="<di:translate key="assessment.back_to_lession_button" />"
           onClick="parent.document.location.href='<mm:url referids="provider,learnobjecttype,education,class,fb_madetest" page="index.jsp">
           <mm:param name="learnobject"><mm:write referid="return_to"/></mm:param>
           <mm:param name="learnobjecttype"><mm:write referid="return_to_type"/></mm:param>
           </mm:url>'"
           /></div>
         </mm:present>
         <iframe width="100%" height="100%" onload="resize()" name="content" 
                 id="content" frameborder="0" style="overflow:hidden"></iframe>
      </div>
   </div>
</div>


<mm:present referid="frame">
   <script>
      closeAll();
      openContent('${learnobjectype}','${education}');
      openOnly('div${learnobject}','img${education}');


      <mm:write referid="frame" jspvar="sFrameURL" vartype="String">
         content.location.href='<%= sFrameURL.replaceAll("&amp;","&") %>'; // wtf
      </mm:write>
   </script>
</mm:present>

<mm:notpresent referid="frame">
<script type="text/javascript">
   closeAll();

   <%-- we open need menu item in case it is a reference from another education --%>
   <mm:present referid="the_only_node_to_show">
      openContent('${learnobjectype}','${the_only_node_to_show}');
      openOnly('div${the_only_node_to_show}','img${the_only_node_to_show}');
   </mm:present>


   <mm:notpresent referid="the_only_node_to_show">
      <mm:present referid="learnobject">
         openContent('${learnobjecttype}','${learnobject}');
         openOnly('div${learnobject}','img${learnobject}');
      </mm:present>

      <mm:notpresent referid="learnobject">
        if (contentnumber.length >= 1) {
           openContent(contenttype[0],contentnumber[0]);
           openOnly('div'+contentnumber[0],'img'+contentnumber[0]);
        }
      </mm:notpresent>
   </mm:notpresent>

</script>
</mm:notpresent>

<br />
<mm:treeinclude page="/cockpit/cockpit_footer.jsp" objectlist="$includePath" referids="$referids "/>
</mm:cloud>

</mm:content>
