              <di:cell>
                <a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath" referids="$referids,currentprofile">
                    <mm:param name="currentcomp"><%= thisCompetencie %></mm:param>
                    <mm:param name="command">editcomp</mm:param>
                   </mm:treefile>"><mm:field name="name"/>
                </a>
              </di:cell>
                <% isEmpty = true; %>
                <mm:list nodes="$currentpop" path="pop,popfeedback,people"
                    constraints="people.number LIKE $user">
                  <mm:field name="popfeedback.number" jspvar="thisFeedback" vartype="String">
                    <mm:list nodes="<%= thisCompetencie %>" path="competencies,popfeedback"
                        constraints="<%= "popfeedback.number LIKE " + thisFeedback %>">
                      <% isEmpty = false; %>
                      <di:cell><mm:field name="popfeedback.rank"/></di:cell>
                      <di:cell><mm:field name="popfeedback.text"/></di:cell>
                    </mm:list>
                  </mm:field>
                </mm:list>
                <% if (isEmpty) { %>
                  <di:cell>&nbsp;</di:cell>
                  <di:cell>&nbsp;</di:cell>
                <% } %>
              <di:cell>
                <mm:list nodes="<%= thisCompetencie %>" path="competencies,todoitems,pop" 
                    constraints="pop.number LIKE $currentpop" orderby="todoitems.number" directions="UP">
                  <mm:first><ul></mm:first>
                    <li><a href="<mm:treefile page="/pop/index.jsp" objectlist="$includePath"
                             referids="$referids,currentfolder,currentprofile">
                             <mm:param name="currentcomp"><%= thisCompetencie %></mm:param>
                             <mm:param name="todonumber"><mm:field name="todoitems.number"/></mm:param>
                             <mm:param name="command">addtodo</mm:param>
                             <mm:param name="returnto">no</mm:param>
                           </mm:treefile>"><mm:field name="todoitems.name"/></a></li>
                  <mm:last></ul></mm:last>
                </mm:list>
              </di:cell>