<%@include file="../relatedteaser.jsp" %><%

// *** are there locations which can be shown ? ***
HashSet locations = new HashSet();
%><mm:list nodes="<%= paginaID %>" path="pagina,rolerel,teaser,posrel0,producttypes,posrel1,products,readmore,locations" 
    ><mm:field name="locations.number"  jspvar="locations_number" vartype="String" write="false"><%
        locations.add(locations_number);
    %></mm:field
></mm:list><%
if(!locations.isEmpty()) {
    String locationsStr = locations.toString();
    locationsStr = locationsStr.substring(1,locationsStr.length()-1);

    %><form name="locationform" method="post" action="">
        <select name="location" onChange="javascript:postL();" style="width:300px;">
        <option value="default" >...
        <mm:list nodes="<%= locationsStr %>" path="locations" orderby="locations.naam" directions="UP">
            <mm:field name="locations.number" jspvar="locations_number" vartype="String" write="false"
                ><option <% if(locations_number.equals(locationId)) { %>SELECTED<% } %>                   
                    value="<%= locations_number %>" ><mm:field name="locations.titel" 
            /></mm:field
        ></mm:list>
    </select>
    </form>
    <script language="JavaScript" type="text/javascript">
    <!--
    function postL() {
        var location = document.locationform.elements["location"].value;
        document.location = "<%= thisPage %>?pool=<%= poolId %>&location=" + location;
    }
    //-->
    </script><%

    if(!locationId.equals("default")) {

        // *** are there productgroups which can be shown ? ***
        HashSet productgroups = new HashSet();
        %><mm:list nodes="<%= locationId %>" path="locations,readmore,products,posrel0,producttypes,posrel1,teaser,rolerel,pagina" 
            constraints="<%= "pagina.number= '" + paginaID + "'" %>"
            ><mm:field name="producttypes.number"  jspvar="producttypes_number" vartype="String" write="false"><%
                productgroups.add(producttypes_number);
            %></mm:field
        ></mm:list><%

        if(!productgroups.isEmpty()) {
            String productgroupsStr = productgroups.toString();
            productgroupsStr = productgroupsStr.substring(1,productgroupsStr.length()-1);

            %><form name="lpgform" method="post" action="">
                <select name="producttype" onChange="javascript:postLPG();" style="width:300px;">
                <option value="" >...
                <mm:list nodes="<%= productgroupsStr %>" path="producttypes"
                        orderby="producttypes.title" directions="UP" fields="producttypes.number,producttypes.title"
                        ><mm:field name="producttypes.number" jspvar="producttypes_number" vartype="String" write="false"
                            ><option <% if(producttypes_number.equals(poolId)) { %>SELECTED<% } %>                   
                                value="<%= producttypes_number %>" ><mm:field name="producttypes.title" 
                        /></mm:field
                 ></mm:list>
            </select>
            </form>
            <script language="JavaScript" type="text/javascript">
            <!--
            function postLPG() {
                var pool = document.lpgform.elements["producttype"].value;
                document.location = "<%= thisPage %>?location=<%= locationId %>&pool=" + pool;
            }
            //-->
            </script><%
            
            if(!poolId.equals("")) { 
            
                %><mm:list nodes="<%= locationId %>" path="locations,readmore,products,posrel,producttypes" 
                     constraints="<%= "producttypes.number= '" + poolId + "'" %>" orderby="products.titel" directions="UP"
                        ><mm:first><table border="1" cellpadding="0" cellspacing="0"></mm:first>
                        <tr>
                            <td style="padding:10px;padding-top:2px;padding-bottom:2px;width:275px;">
                                <mm:field name="products.titel"><mm:isnotempty><mm:write /></mm:isnotempty><mm:isempty>&nbsp;</mm:isempty></mm:field>
                            </td>
                            <td style="padding:10px;padding-top:2px;padding-bottom:2px;">
                                <mm:field name="readmore.readmore"><mm:isnotempty><mm:write /></mm:isnotempty><mm:isempty>&nbsp;</mm:isempty></mm:field>
                            </td>
                            <td style="padding:10px;padding-top:2px;padding-bottom:2px;">
                                <mm:field name="products.omschrijving"><mm:isnotempty><mm:write /></mm:isnotempty><mm:isempty>&nbsp;</mm:isempty></mm:field>
                            </td>
                        </tr>
                        <mm:last></table></mm:last
                ></mm:list><%
            } 
        } 
    } 
}
%>





