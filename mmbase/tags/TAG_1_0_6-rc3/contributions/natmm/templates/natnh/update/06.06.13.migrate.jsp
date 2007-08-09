<%@page import="java.util.*,nl.leocms.util.*,nl.leocms.util.tools.HtmlCleaner" %>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:cloud method="http" rank="basic user" jspvar="cloud">
<mm:log jspvar="log">
<html>
   <head>
   <LINK rel="stylesheet" type="text/css" href="/editors/css/editorstyle.css">
   <title>Natuurmonumenten</title>
   <style>
     table { width: 100%; }
     td { border: solid #000000 1px; padding: 3px; height: auto; vertical-align: top; } 
   </style>
   </head>
   <body style="width:100%;padding:5px;">
   Changes made in this update:<br/>
	1. Renumber paragraphs<br/>
	2. Renumber images<br/>
	Old:
	<option id="3">rechts klein</option>
	<option id="4">links klein</option>
	<option id="7">volle breedte</option>
	
	<option id="1">rechts</option>
	<option id="2">links</option>
	<option id="5">rechts medium</option>
	<option id="6">links medium</option><br/>
	
	New:
	<option id="2">klein links</option>
   <option id="3">klein rechts</option>
   <option id="8">medium links</option>
	<option id="9">medium rechts</option>
   <option id="4">groot</option><br/>
	
	3. Set verloopdatum of pages to 2038<br/>		
	<mm:node number="natuurherstel">
		<mm:relatednodes type="rubriek" searchdir="destination">
			<mm:relatednodes type="pagina" searchdir="destination">
				<mm:setfield name="verloopdatum">2145913200</mm:setfield>
				<% int a=0; %>
				<mm:related path="contentrel,artikel" orderby="contentrel.pos" directions="UP">
					<% a++; %>
					<mm:node element="contentrel">
						<mm:setfield name="pos"><%= "" + a %></mm:setfield>
					</mm:node>
					<mm:node element="artikel">
						<% int p=0; %>
						<mm:related path="posrel,paragraaf" orderby="posrel.pos" directions="UP">
							<% p++; %>
							<mm:node element="posrel">
								<mm:setfield name="pos"><%= "" + p %></mm:setfield>
							</mm:node>
							<mm:node element="paragraaf">
								<mm:related path="posrel,images" orderby="posrel.pos" directions="UP">
									<mm:node element="posrel">
										<mm:field name="pos" jspvar="posrel_pos" vartype="String" write="false">
											<% 
												if(posrel_pos.equals("3")) { posrel_pos = "3"; // rechts klein -> klein rechts
												} else if(posrel_pos.equals("4")) { posrel_pos = "2"; // links klein -> klein links
												} else if(posrel_pos.equals("7")) { posrel_pos = "4"; // rechts groot -> groot
												} else if(posrel_pos.equals("8")) { posrel_pos = "4"; // links groot -> groot
												} else if(posrel_pos.equals("1")) { posrel_pos = "9"; // rechts -> medium rechts
												} else if(posrel_pos.equals("2")) { posrel_pos = "8"; // links -> medium links
												} else if(posrel_pos.equals("5")) { posrel_pos = "9"; // rechts medium -> medium rechts
												} else if(posrel_pos.equals("6")) { posrel_pos = "8"; // links medium -> medium links
												} else { posrel_pos = "2"; // default klein links
												}
											%>
											<mm:setfield name="pos"><%= posrel_pos %></mm:setfield>
										</mm:field>
									</mm:node>
								</mm:related>
							</mm:node>
						</mm:related>
					</mm:node>
				</mm:related>
			</mm:relatednodes>
		</mm:relatednodes>
	</mm:node>
   Done.
   </body>
</html>
</mm:log>
</mm:cloud>
