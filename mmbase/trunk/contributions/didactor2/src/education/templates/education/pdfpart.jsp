<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content postprocessor="reducespace" expires="0">
<%-- 
    we need to get the parameters from the request by hand
    for some reason mmbase keeps the old versions if we use
    referid (even with from="parameters" !)
--%>
<mm:import id="number" jspvar="number"><%= request.getParameter("partnumber") %></mm:import>
<mm:import id="level"  jspvar="level" vartype="Integer"><%= request.getParameter("level") %></mm:import>
<mm:cloud jspvar="cloud" method="anonymous">
<%@include file="/shared/setImports.jsp" %>
<fmt:bundle basename="nl.didactor.component.education.EducationMessageBundle">
<mm:node number="$number">
<mm:nodeinfo type="type" id="node_type" jspvar="nodeType">

<% System.err.println("rendering node "+number+" of type "+nodeType+" at level "+level); %>
<mm:compare referid="node_type" value="learnblocks">
    <mm:import id="display">1</mm:import>
</mm:compare>
<mm:compare referid="node_type" value="pages">
    <mm:import id="display">1</mm:import>
</mm:compare>

<mm:present referid="display">

    <%= "<h"+level.toString()+">" %><mm:field name="name"/><%= "</h"+level.toString()+">" %>
    <br/>
    <mm:import jspvar="text" reset="true"><mm:field name="intro" escape="none"/><mm:field name="text" escape="none"/></mm:import>
    <%

        //
        // remove some of the annoying html that messes up the PDFs
        // 
        text = text.replaceAll("</?font[^>]*>","");
        text = text.replaceAll("(<t[dh][^>])width=\"[^\"]*\"","$1");
        text = text.replaceAll("(<t[dh][^>])height=\"[^\"]*\"","$1");
        text = text.replaceAll("<p\\s*/>","");
        text = text.replaceFirst("\\A\\s*","");
        text = text.replaceFirst("\\s*\\z","");
        if (!text.startsWith("<p>")) {
            text = "<p>"+text;
        }
        if (!text.endsWith("</p>")) {
            text = text+"</p>";
        }
    %>
   
    <mm:compare referid="node_type" value="learnblocks">
       <%= text %>
    </mm:compare>

    <mm:compare referid="node_type" value="pages">
        <mm:field name="layout" id="layout" write="false"/>
        <mm:field name="imagelayout" id="imagelayout" write="false"/>


        <mm:compare referid="layout" value="0">
        <%= text %>
        <table><tr>
        <%@include file="pdfimages.jsp"%>
        </tr></table>
        </mm:compare>
        <mm:compare referid="layout" value="1">
        <table><tr>
        <%@include file="pdfimages.jsp"%>
        </tr></table>
        <%= text %>
        </mm:compare>
        <mm:compare referid="layout" value="2">
        <table><tr>
        <%@include file="pdfimages.jsp"%>
        <td>
        <%= text %>
        </td></tr>
        </mm:compare>
        <mm:compare referid="layout" value="3">
        <table><tr>
        <%@include file="pdfimages.jsp"%>
        <td>
        <%= text %>
        </td></tr>
        </table>
        </mm:compare>

        <mm:import id="providerurl">geen.standaard.aanbieders.url</mm:import>
        
        <mm:node referid="provider">
            <mm:relatednodes type="urls">
                <mm:first>
                    <mm:import id="providerurl" reset="true"><mm:field name="url"/></mm:import>
                </mm:first>
            </mm:relatednodes>
        </mm:node>
        
        <mm:relatednodes type="attachments">
            <br/>
            <p>
            <mm:field name="title"/>
            <br/>
            <mm:field name="description"/>
            <br/>
            http://<mm:write referid="providerurl"/>/attachment.db?<mm:field name="number"/>
            </p>
        </mm:relatednodes>

        <mm:relatednodes type="audiotapes">
        <br/>
        <p>
        <mm:field name="title"/>
        <br/>
        <mm:field name="subtitle"/>
        <br/>
        <mm:field name="playtime"/>
        <br/>
        <mm:field name="intro"/>
        <br/>
        <mm:field name="body"/>
        <br/>
        <mm:field name="url" />
        </p>
        </mm:relatednodes>

        <mm:relatednodes type="videotapes">
        <br/>
        <p>
            <mm:field name="title"/>
            <br/>
            <mm:field name="subtitle"/>
            <br/>
            <mm:field name="playtime"/>
            <br/>
            <mm:field name="intro"/>
            <br/>
            <mm:field name="body"/>
            <br/>
            <mm:field name="url" />
        </p>
        </mm:relatednodes>

        <mm:relatednodes type="urls">
        <br/>
        <p>
            <mm:field name="name"/>
            <br/>
            <mm:field name="description"/>
            <br/>
            <mm:field name="url" />
        </p>
        </mm:relatednodes>

        
        </mm:compare>
        <br/>

    <% if (level.intValue() < 20) { %>
    <mm:list  path="learnobjects1,posrel,learnobjects2" orderby="posrel.pos" fields="learnobjects2.number" searchdir="destination" distinct="true" constraints="learnobjects1.number = $number">
        <mm:remove referid="number"/>
        <mm:remove referid="level"/>
        <mm:field name="learnobjects2.number" jspvar="partnumber">
            <mm:include page="pdfpart.jsp">
                <mm:param name="partnumber"><%= partnumber %></mm:param>
                <mm:param name="level"><%= (level.intValue()+1) %></mm:param>
            </mm:include>
        </mm:field>
    </mm:list>
    <% } %>

</mm:present>
</mm:nodeinfo>
</mm:node>
</fmt:bundle>
</mm:cloud>
</mm:content>
