               <a href="<mm:treefile page="/portfolio/index.jsp" objectlist="$includePath" referids="$referids">
                           <mm:param name="contact"><mm:field name="number"/></mm:param>
                        </mm:treefile>" class="users">
                        <%-- Online/offline status is retrieved using the nl.didactor.builders.PeopleBuilder class  --%>
                  <mm:field name="isonline" id="isonline" write="false" />
                  <mm:compare referid="isonline" value="0">
                     <img src="<mm:treefile write="true" page="/gfx/icon_offline.gif" objectlist="$includePath" />" width="6" height="12" border="0" title="offline" alt="offline" />
                  </mm:compare>
                  <mm:compare referid="isonline" value="1">
                     <img src="<mm:treefile write="true" page="/gfx/icon_online.gif" objectlist="$includePath" />" width="6" height="12" border="0" title="online" alt="online" />
                  </mm:compare>
                  <mm:remove referid="isonline" />
                  <mm:field name="firstname"/> <mm:field name="lastname"/>
               </a>
