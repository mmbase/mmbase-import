<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"
%><%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" 
%><%@taglib tagdir="/WEB-INF/tags/di/core" prefix="di-t" 
%><%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"
%><%@page import="java.util.*"
%><%--
TODO: This JSP is much too big, and polluted with all kinds of functionality.

--%><mm:content postprocessor="reducespace" expires="0" language="${requestScope.language}">

<mm:cloud rank="didactor user">



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
         <mm:include page="tree.jspx" />
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
           onClick="parent.document.location.href='<mm:url referids="$referids,learnobjecttype,class,fb_madetest" page="index.jsp">
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
