<mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false"
	><mm:field name="artikel.begindatum" jspvar="artikel_tdate" vartype="String" write="false"><%
	long td = Integer.parseInt(artikel_tdate); td = 1000 * td; Date dd = new Date(td); cal.setTime(dd);
	String tdateStr =  cal.get(Calendar.DAY_OF_MONTH)+ " " + months_lcase[(cal.get(Calendar.MONTH))] + " " + cal.get(Calendar.YEAR); 
	
	String summary = ""; 
	boolean readMore = false;
	%><mm:field name="artikel.intro" jspvar="artikel_intro" vartype="String" write="false"><% 
		summary = artikel_intro; 
	%></mm:field><%
	if(summary!=null) {
		summary = HtmlCleaner.cleanText(summary,"<",">");
		int spacePos = summary.indexOf(" ",250);
		if(spacePos>-1) { 
			summary = summary.substring(0,spacePos);
			readMore = true;
		}
	} else {
		summary = "";
	}
  if(!readMore) { // check if there are paragraphs
    %><mm:list nodes="<%= artikel_number %>" path="artikel,posrel,paragraaf" max="1"><% readMore = true; %></mm:list><%
  }
	%><mm:field name="artikel.titel" jspvar="artikel_titel" vartype="String" write="false"><% 
		if(readMore) { 
			%><a href="<%= readmoreUrl %>"><%
		} 
		%><mm:field name="artikel.titel_zichtbaar"
		   ><mm:compare value="0" inverse="true"
		      ><div class="pageheader"><%= artikel_titel %></div
		   ></mm:compare
		></mm:field><% 
		if(readMore) { 
			%></a><% 
		} 
		%></mm:field
	><%= tdateStr %><br>
	<%= summary 	
	%><% if(readMore){ 
		%>...<a href="<%= readmoreUrl %>"><span style="text-decoration:none;"> Lees meer >></span></a><% 
	} %><br><br><br>
</mm:field
></mm:field>
