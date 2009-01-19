<mm:list nodes="<%=paginaID%>" path="pagina,posrel,dossier" fields="dossier.number,dossier.naam" orderby="posrel.pos" directions="UP">
   <mm:field name="dossier.number" jspvar="dossier_number" vartype="String" write="false">
   	<%
      if(dossierID.equals("-1")){
   	   dossierID = dossier_number;
   	}
      %>      
      <mm:size id="number_of_dossiers">
         <mm:isgreaterthan value="1">
            <mm:first>
               <div style="margin:7px 0px 7px 0x">Maak een keuze uit de volgende categorie&euml;n:</div>
               <form method="get" name="dossiers" id="dossiers">
               	<select name="d" id="dossier" onChange="this.form.submit()">
            </mm:first>
            	<option <% if(dossierID.equals(dossier_number)){%>selected<% } %> value="<mm:field name="dossier.number" />"><mm:field name="dossier.naam"/></option>
            <mm:last>
                  </select> 
               </form>
               <br/>
            </mm:last>
         </mm:isgreaterthan>
      </mm:size>
   </mm:field>
</mm:list>