<%  
String editorname = (String) session.getAttribute("editor");
String rootId = (String) session.getAttribute("website");
String rubriekId = (String) session.getAttribute("r");
String paginaID = (String) session.getAttribute("page");
if(editorname!=null) {
    String userconstraint="users.account='" + editorname + "'";    
    %><mm:notpresent referid="isowner"
        ><mm:list nodes="<%= rootId %>" path="rubriek,rolerel,users" 
            constraints="<%= userconstraint %>" max="1"
            ><mm:import id="isowner"
        /></mm:list
    ></mm:notpresent
    ><mm:notpresent referid="isowner"><%
        if(!rubriekId.equals("-1")) { 
            %><mm:list nodes="<%= rubriekId %>" path="rubriek,rolerel,users" 
                constraints="<%= userconstraint %>" max="1"
                ><mm:import id="isowner"
            /></mm:list><% 
        }
    %></mm:notpresent
    ><mm:notpresent referid="isowner"
        ><mm:list nodes="<%= paginaID %>" path="pagina,rolerel,users" 
            constraints="<%= userconstraint %>" max="1"
            ><mm:import id="isowner"
        /></mm:list
    ></mm:notpresent><%
} %>