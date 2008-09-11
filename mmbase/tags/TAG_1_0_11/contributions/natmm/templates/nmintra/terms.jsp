<%@include file="/taglibs.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/templateheader.jsp" %>
<%@include file="includes/cacheparams.jsp" %>
<% if(!termSearchId.equals("")) { expireTime = 0; } %>
<cache:cache groups="<%= paginaID %>" key="<%= cacheKey %>" time="<%= expireTime %>" scope="application">
<%@include file="includes/calendar.jsp" 
%><% 

      String termConstraint = "";
      boolean notFound = true;
      if(!termSearchId.equals("")) {
          abcId = "";
          termConstraint = "UPPER(terms.name) LIKE '%" + termSearchId.toUpperCase() + "%'";
          termConstraint += " OR UPPER(terms.description) LIKE '%" + termSearchId.toUpperCase() + "%'";
      } else {
          if(abcId.equals("")) abcId = "A";
          termConstraint = "UPPER(terms.name) LIKE '" + abcId + "%'";
      }
      String projectArchive = "-1";
      %><mm:list path="paginatemplate,gebruikt,pagina" constraints="paginatemplate.url LIKE '%archive.jsp'" max="1"
            ><mm:field name="pagina.number" jspvar="page_number" vartype="String" write="false"><%
                  projectArchive = page_number; 
               %></mm:field
            ></mm:list
      ><%@include file="includes/header.jsp" %> 
      <td><%@include file="includes/pagetitle.jsp" %></td>
      <td><%
         String rightBarTitle = "";
         rightBarTitle = "Zoek&nbsp;in&nbsp;begrippenlijst";
         %><%@include file="includes/rightbartitle.jsp" %></td>
      </tr>
      <tr>
      <td class="transperant">
      <div class="<%= infopageClass %>" id="infopage">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr><td colspan="3"><img src="media/spacer.gif" width="1" height="8"></td></tr>
        <tr><td><img src="media/spacer.gif" width="10" height="1"></td>
            <td><%@include file="includes/abclinks.jsp"
                  %><mm:list nodes="<%= paginaID %>" 
								path="pagina,contentrel,terms" constraints="<%= termConstraint %>"
                        orderby="terms.name" directions="UP" 
								><mm:first><%
									notFound = false;
									if(!termSearchId.equals("")) { 
										%>U heeft gezocht op: <%= termSearchId %>. De volgende resultaten zijn gevonden.<%
									}
									%><div style="font-size:48px;"><b><%= abcId %></b></div>
										<table border="0" cellpadding="0" cellspacing="0" width="100%">
								</mm:first
								><mm:node element="terms"
                  ><mm:field name="name" jspvar="term_name" vartype="String" write="false">
									<tr style="padding-top:15px;"><td>
										<a name="<mm:field name="number" />"></a>
										<p>
                    <mm:remove referid="readmore_page" />
										<mm:related path="readmore,pagina" orderby="pagina.titel">
                      <mm:first><mm:import id="readmore_page" /></mm:first>
											<mm:first inverse="true">, </mm:first>
											<mm:field name="pagina.number" jspvar="sThisPage" vartype="String" write="false">
												<b>
													<a href="<%= ph.createPaginaUrl(sThisPage,request.getContextPath()) 
														%>" style="text-decoration:underline;"
														title="Meer info op pagina <mm:field name="pagina.titel" />">
														<%= term_name %>
													</a>
												</b><br/>
											</mm:field>
										</mm:related>
                    <mm:notpresent referid="readmore_page">
                      <b><%= term_name %></b>
                    </mm:notpresent>
										<span class="black"><mm:field name="description"/></span>
										</p>
										<mm:related path="posrel,projects" orderby="posrel.pos" directions="UP" 
											  ><mm:first>[lees meer over <%= term_name %> in project: </mm:first><% 
											  String readmoreUrl = "terms.jsp?p=" + projectArchive + "&project=";
											  %><mm:field name="projects.number" jspvar="project_number" vartype="String" write="false"><%
													readmoreUrl += project_number; 
											  %></mm:field
											  ><a href="<%= readmoreUrl %>"><span style="text-decoration:underline;" class="dark"> <mm:field name="projects.titel"/></span></a>
											  <mm:last>]</mm:last>
										 </mm:related
										 ><mm:related path="rolerel,terms" orderby="terms.name" searchdir="destination"
											  ><mm:first> <i>[zie ook: </mm:first
											  ><mm:first inverse="true">, </mm:first
													><mm:field name="rolerel.role"><mm:isnotempty><mm:write/> </mm:isnotempty></mm:field
													><mm:field name="terms.name" jspvar="terms_word" vartype="String" write="false"
													><mm:field name="terms.number" jspvar="terms_number" vartype="String" write="false"><%
														String readmoreUrl = "terms.jsp" +templateQueryString +"&abc=" +terms_word.substring(0,1).toUpperCase() +"#" +terms_number;
														%><a href="<%= readmoreUrl %>"><%= terms_word %></a>
												</mm:field>
												</mm:field>
											<mm:last>]</i></mm:last>
										</mm:related>
										<mm:related path="synonymrel,terms" orderby="terms.name">
											<mm:field name="terms.number" jspvar="id" vartype="String" write="false">
												<mm:first> <i>[ook wel: </mm:first
												><mm:first inverse="true">, </mm:first
												><mm:field name="terms.name" jspvar="terms_word" vartype="String" write="false"
												><mm:field name="terms.number" jspvar="terms_number" vartype="String" write="false"><%
													String readmoreUrl = "terms.jsp" +templateQueryString +"&abc=" +terms_word.substring(0,1).toUpperCase() +"#" +terms_number;
													%><a href="<%= readmoreUrl %>"><%= terms_word %></a>
												</mm:field>
												</mm:field>
												<mm:last>]</i></mm:last>
											</mm:field>
										</mm:related>
								  </td></tr>
						      </mm:field>
                    </mm:node
                    ><mm:last></table></mm:last>
                </mm:list><%
                if(notFound) {
                    %><mm:listnodes type="terms" max="1"
                        >Er zijn geen begrippen gevonden, die voldoen aan uw zoek criterium.
                        <mm:import id="hasterms" 
                    /></mm:listnodes
                    ><mm:notpresent referid="hasterms"
                        >Deze begrippenlijst bevat geen begrippen.
                    </mm:notpresent><%
                }
               %><br/><br/>
               <%@include file="includes/abclinks.jsp" 
               %></td>
        <td><img src="media/spacer.gif" width="10" height="1"></td>
      </tr>
      </table>
      </div>
      </td><td><%
      // *************************************** right bar *******************************
      %><%@include file="includes/termsearch.jsp" 
      %></td>
<%@include file="includes/footer.jsp" %>
</cache:cache>
</mm:cloud>
