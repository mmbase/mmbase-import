          <mm:relatednodes type="chatlogs" orderby="date" directions="down">

          <mm:import id="currentchatlog" reset="true"><mm:field name="number"/></mm:import>

          <%-- open default the first chatlog --%>
          <mm:first>
            <mm:compare referid="chatlog" value="-1">
              <mm:remove referid="chatlog"/>
              <mm:import id="chatlog"><mm:field name="number"/></mm:import>
            </mm:compare>
          </mm:first>

          <%-- folder is open --%>
          <mm:compare referid="currentchatlog" referid2="chatlog">
            <img src="<mm:treefile page="/chat/gfx/mapopen.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="chat.folderopened" />" alt="<di:translate key="chat.folderopened" />"/>
          </mm:compare>

          <%-- folder is closed --%>
          <mm:compare referid="currentchatlog" referid2="chatlog" inverse="true">
            <img src="<mm:treefile page="/chat/gfx/mapdicht.gif" objectlist="$includePath" referids="$referids"/>" title="<di:translate key="chat.folderclosed" />" alt="<di:translate key="chat.folderclosed" />"/>
          </mm:compare>

          <mm:import id="tempday" jspvar="tempDay" reset="true"><mm:field name="date"/></mm:import>
		  <%
		     SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		     Date d = null;
		     long temp1 = 0;
		     try {
		     			d = fmt.parse( tempDay );
		     			temp1 = d.getTime() / 1000;
		     		} catch (ParseException e) {
		     			e.printStackTrace();
		  		}
          %>
          <mm:import id="date" reset="true"><%=temp1%></mm:import>

			<a href="<mm:treefile page="/chat/chatlog.jsp" objectlist="$includePath" referids="$referids">
				<mm:param name="chatlog"><mm:field name="number"/></mm:param>
				</mm:treefile>">
              <mm:write referid="date"><mm:time format="dd/MM/yyyy"/></mm:write>
 			</a><br />

          </mm:relatednodes>


