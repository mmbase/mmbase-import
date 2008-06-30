<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/vpro-wizards/util"%>
<%@taglib prefix="edit" tagdir="/WEB-INF/tags/vpro-wizards"%>

<mm:cloud jspvar="cloud" method="loginpage" loginpage="/mmbase/vpro-wizards/system/login.jsp" />



<mm:content type="text/html" expires="0">
    <html>
        <head>
            <title>MyNews Editors met de VPRO-Wizards</title>
            <link rel="stylesheet" type="text/css"
                href="${pageContext.request.contextPath}/mmbase/vpro-wizards/stylesheets/edit.css"/>
            <style >
            body{
                font-family: "Lucida Grande",Verdana,Lucida,Helvetica,Arial,sans-serif;
            }
            .menu{

            }
            .menuSection{
                background-color: #fbfbf2;
                border: 1px solid #dfd6bd;
                margin: 2px;
                width: 200px;
            }
            .menuSection h3{
                text-align: center;
            }
            .menuitem{
                margin: 0px;
                padding: 0px;
            }

            .menuitem li{
                padding: 3px;
            }

            </style>
        </head>
        <body>
        <edit:header/>
        <util:setreferrer/>
        <edit:path name="Menu" session="menu" reset="true"/>
        <edit:sessionpath/>

        <div class="editors">
            <div class="menuSection">
                <h3>My News </h3>
                <ul class="menuitem">
                    <li>
                        <a href="mags/magazines.jsp">
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/list_grey.png" />
                            Magazines
                        </a>
                    </li>
                    <li>
                        <a href="news/allnews.jsp">
                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/img/list_grey.png" />
                            Nieuws berichten
                        </a>
                    </li>
                </ul>
            </div>
        </div>
        <%--
        the 'desktop' with bookmarks for the editors is currently proprietairy for the vpro.
        Plans are to publish it as a separate application. It needs some builders and so on.

        <mm:include page="/mmbase/vpro-wizards/bookmarks.jsp">
            <mm:param name="columns" value="pvhcol1,pvhcol2,pvhcol3" />
        </mm:include>
        --%>
        </body>
    </html>
</mm:content>
