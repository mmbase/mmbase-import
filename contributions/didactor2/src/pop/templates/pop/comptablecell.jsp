              <td class="listItem">
                <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids,currentprofile">
                    <mm:param name="currentcomp"><%= thisCompetencie %></mm:param>
                    <mm:param name="command">editcomp</mm:param>
                   </mm:treefile>"><mm:field name="name"/>
                </a>
              </td>
                <% isEmpty = true; %>
                <mm:list nodes="$currentpop" path="pop,popfeedback,people" constraints="people.number='$user'">
                  <mm:field name="popfeedback.number" jspvar="thisFeedback" vartype="String">
                    <mm:list nodes="<%= thisCompetencie %>" path="competencies,popfeedback"
                        constraints="<%= "popfeedback.number LIKE " + thisFeedback %>">
                      <% isEmpty = false; %>
                      <td class="listItem"><mm:field name="popfeedback.rank"/></td>
                      <td class="listItem"><mm:field name="popfeedback.text"/></td>
                    </mm:list>
                  </mm:field>
                </mm:list>
                <% if (isEmpty) { %>
                  <td class="listItem">&nbsp;</td>
                  <td class="listItem">&nbsp;</td>
                <% } %>
              <td class="listItem">
                <mm:list nodes="<%= thisCompetencie %>" path="competencies,todoitems,pop" 
                    constraints="pop.number LIKE $currentpop" orderby="todoitems.number" directions="UP">
                  &bull; <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath"
                           referids="$referids,currentfolder,currentprofile">
                           <mm:param name="currentcomp"><%= thisCompetencie %></mm:param>
                           <mm:param name="todonumber"><mm:field name="todoitems.number"/></mm:param>
                           <mm:param name="command">addtodo</mm:param>
                           <mm:param name="returnto">no</mm:param>
                         </mm:treefile>"><mm:field name="todoitems.name" jspvar="todoName" vartype="String"
                         ><% if (todoName.length()>0) { %><%= todoName %><% } else { %>...<% } %></mm:field></a><br/>
                </mm:list>
              </td>