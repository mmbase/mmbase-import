<%@taglib uri="http://www.didactor.nl/ditaglib_1.0" prefix="di" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm" %>
<mm:content postprocessor="reducespace">
<mm:cloud method="delegate" jspvar="cloud">
<%@include file="/shared/setImports.jsp" %>
<%@include file="/search/constraintBuilder.jsp"%>
<mm:import externid="search_query"/>
<% if ("".equals(request.getParameter("search_component")) || "mmbob".equals(request.getParameter("search_component"))) { %>
   <% String containsForums = ""; %>
   <mm:list nodes="$user" path="people,classrel,classes,forums" fields="classes.number" distinct="true">
      <mm:field name="classes.number" jspvar="dummy" vartype="String">
         <% containsForums += dummy + ","; %>
      </mm:field>
   </mm:list>
   <mm:list nodes="$user" path="people,classrel,educations,forums" fields="educations.number" distinct="true">
      <mm:field name="educations.number" jspvar="dummy" vartype="String">
         <% containsForums += dummy + ","; %>
      </mm:field>
   </mm:list>
   <mm:list nodes="$user" path="people,classrel,classes,educations,forums" fields="educations.number" distinct="true">
      <mm:field name="educations.number" jspvar="dummy" vartype="String">
         <% containsForums += dummy + ","; %>
      </mm:field>
   </mm:list>
   <% if (!"".equals(containsForums)) { %>
      <mm:list nodes="<%= containsForums %>" path="object,forums" orderby="forums.number" fields="forums.number" distinct="true">
         <mm:import id="forumid" reset="true"><mm:field name="forums.number"/></mm:import>
         <mm:list nodes="$forumid" path="forums,postareas,postthreads,postings" constraints="<%= searchConstraints("CONCAT(postings.subject, postings.body)", request) %>">
            <mm:import id="postareaid" reset="true"><mm:field name="postareas.number"/></mm:import>
            <mm:import id="postthreadid" reset="true"><mm:field name="postthreads.number"/></mm:import>
            <mm:import id="postingid" reset="true"><mm:field name="postings.number"/></mm:import>
            <tr>
               <td class="listItem"><a href="<mm:url page="/mmbob/start.jsp">
		                                <mm:param name="forumid" value="$forumid" />
		                             </mm:url>"><mm:field name="forums.name"/></a></td>
               <td class="listItem">
                  <a href="<mm:url page="/mmbob/thread.jsp">
		              <mm:param name="forumid" value="$forumid" />
		              <mm:param name="postareaid" value="$postareaid" />
		              <mm:param name="postthreadid" value="$postthreadid" />
		           </mm:url>"><mm:field name="postthreads.subject"/></a> /
                  <a href="<mm:url page="/mmbob/thread.jsp">
		              <mm:param name="forumid" value="$forumid" />
		              <mm:param name="postareaid" value="$postareaid" />
		              <mm:param name="postthreadid" value="$postthreadid" />
		              <mm:param name="postingid" value="$postingid" />
		              <mm:param name="fromsearch" value="true" />
		           </mm:url>#<mm:write referid="postingid"/>"><mm:field name="postings.subject"/></a>
               </td>
            </tr>
         </mm:list>
      </mm:list>
   <% } %>
<% } %>
</mm:cloud>
</mm:content>
