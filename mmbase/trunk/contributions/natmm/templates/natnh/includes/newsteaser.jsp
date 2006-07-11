<mm:present referid="ishomepage"
	><mm:list nodes="news_template" path="paginatemplate,gebruikt,pagina,posrel,rubriek"
		constraints="<%= "rubriek.number='" + rubriekId + "'" %>" max="1"
		><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"><%
			String articleConstraint =	"artikel.embargo < " + (nowSec + 15*60) + "  AND (artikel.use_verloopdatum='0' OR artikel.verloopdatum > '" + nowSec + "' )";
			%><mm:list nodes="<%= pagina_number %>" path="pagina,contentrel,artikel" max="1"		
				orderby="artikel.embargo" directions="DOWN" 
				constraints="<%= articleConstraint %>"
				><mm:field name="artikel.number" jspvar="artikel_number" vartype="String" write="false"> 
				<div class="sidebarright"><span class="pageheader">Laatste nieuws</span>
				<p><span class="parkop"><a href="<%= ph.createItemUrl(artikel_number, pagina_number, null, request.getContextPath()) %>">
					<mm:field name="artikel.titel_zichtbaar"
						><mm:compare value="0" inverse="true"
							><mm:field name="artikel.titel" 
						/></mm:compare
						><mm:compare value="0"
								>Laatste nieuws
						</mm:compare	
					></mm:field>
					</a></span><br>
					<mm:field name="artikel.titel_fra" /></p>
				<p><a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>">Meer nieuws &gt;&gt;</a></p>
				</div>
				</mm:field>
			</mm:list
		></mm:field
	></mm:list
></mm:present
>