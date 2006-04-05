<%  
String editorname = (String) session.getAttribute("editor");
String websiteId = (String) session.getAttribute("website");
String rubriekId = (String) session.getAttribute("r");
String pageId = (String) session.getAttribute("page");
if(editorname!=null) {
    String userconstraint="users.account='" + editorname + "'";    
    %><mm:notpresent referid="isowner"
        ><mm:list nodes="<%= websiteId %>" path="rubriek,rolerel,users" 
            constraints="<%= userconstraint %>" max="1"
            ><mm:import id="isowner"
        /></mm:list
    ></mm:notpresent
    ><mm:notpresent referid="isowner"><%
        if(!rubriekId.equals("")) { 
            %><mm:list nodes="<%= rubriekId %>" path="rubriek,rolerel,users" 
                constraints="<%= userconstraint %>" max="1"
                ><mm:import id="isowner"
            /></mm:list><% 
        }
    %></mm:notpresent
    ><mm:notpresent referid="isowner"
        ><mm:list nodes="<%= pageId %>" path="pagina,rolerel,users" 
            constraints="<%= userconstraint %>" max="1"
            ><mm:import id="isowner"
        /></mm:list
    ></mm:notpresent><%
} %>