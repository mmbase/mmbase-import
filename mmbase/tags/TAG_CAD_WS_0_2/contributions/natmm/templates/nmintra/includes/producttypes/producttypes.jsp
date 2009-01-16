<div class="pageheader" style="margin-top:15px;"><mm:node number="<%= paginaID %>"><mm:field name="titel"/></mm:node> Dienstenniveau</div>
<div class="pagesubheader" style="margin-top:10px;">Kies een productgroep om de diensten en het dienstenniveau over op te vragen</div>
<form name="producttypeform" method="post" action="">
     <select name="producttype" onChange="javascript:postPG();" style="width:300px;">
     <option value="" >...
        <mm:list nodes="<%= paginaID %>" path="pagina,posrel,producttypes"
            orderby="producttypes.title" directions="UP" fields="producttypes.number,producttypes.title"
            ><mm:field name="producttypes.number" jspvar="producttypes_number" vartype="String" write="false"
                ><option <% if(producttypes_number.equals(poolId)) { %>SELECTED<% } %>                   
                    value="<%= producttypes_number %>" ><mm:field name="producttypes.title" 
            /></mm:field
       ></mm:list
    ></select>
</form>
<script language="JavaScript" type="text/javascript">
<!--
function postPG() {
    var pool = document.producttypeform.elements["producttype"].value;
    document.location = "<%= thisPage %>?pool=" + pool;
}
//-->
</script><%

if(!poolId.equals("")) { // *** use mm:list to only show producttypes related to this page ***
    %><mm:list nodes="<%= paginaID %>" path="pagina,posrel,producttypes" 
        constraints="<%= "producttypes.number = '" + poolId  + "'" %>" max="1"
    ><div class="pagesubheader">Kies een product/dienst om het dienstenniveau over op te vragen</div>
    <form name="productform" method="post" action="">
       <select name="producttype" onChange="javascript:postP();" style="width:300px;">
       <option value="" >...
       <mm:list nodes="<%= poolId %>" path="producttypes,posrel,products"
                orderby="products.titel" directions="UP" fields="products.number,products.titel"
            ><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
                ><option <% if(products_number.equals(productId)) { %>SELECTED<% } %>                   
                    value="<%= products_number %>" ><mm:field name="products.titel" 
            /></mm:field
       ></mm:list
       ></select>
    </form>
    <script language="JavaScript" type="text/javascript">
    <!--
    function postP() {
        var product = document.productform.elements["producttype"].value;
        document.location = "<%= thisPage %>?pool=<%= poolId %>&product=" + product;
    }
    //-->
    </script>
    </mm:list><%
    
    if(!productId.equals("")) { 
        %><mm:node number="<%= productId %>"
           ><div class="pagesubheader"><mm:field name="name" /></div>
           <mm:field name="omschrijving" />
           <mm:relatednodes type="items"
         		><mm:first
         		   ><div class="subtitle">Gerelateerde producten in de interne webwinkel</div>
         	   </mm:first
         	   ><mm:field name="titel" id="shopitems_title" write="false" 
         	   /><mm:field name="number" id="shopitems_number" write="false" 
         	      /><mm:related path="posrel,pagina" max="1"
      	         ><a href="shop_items.jsp?p=<mm:field name="pagina.number" />&u=<mm:write referid="shopitems_number" 
      		            />"><li><mm:write referid="shopitems_title" /></a><br>
      		   </mm:related
         ></mm:relatednodes
         ></mm:node><br><br><%

    } 
} 

%>