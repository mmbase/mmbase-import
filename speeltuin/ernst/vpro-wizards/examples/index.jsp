<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="util" tagdir="/WEB-INF/tags/edit/util"%>
<%@taglib prefix="edit" tagdir="/WEB-INF/tags/edit"%>

<mm:cloud jspvar="cloud" method="loginpage" loginpage="/edit/login.jsp" />


<util:setreferrer/>
    <edit:path name="Menu" session="menu" reset="true"/>
    <edit:sessionpath/>

<mm:content type="text/html" expires="0">
    <html>
        <head>
            <title>Test Editors</title>
            <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/edit/stylesheets/edit.css"/>
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
        <div class="editors">
            <div class="menuSection">
                <h3>Plaats van herinnering </h3>
                <ul class="menuitem">
                    <li>
                        <%--flush de quick groep en de specifieke plaats pagina als die is aangepast--%>
                        <a href="plaats/plaatsen.jsp?flushname=plaatsvanherinneringen_quick, plaatsen_[memorylocation]">
                            <img src="${pageContext.request.contextPath}/edit/system/img/list_grey.png" />
                            Plaatsen
                        </a>
                    </li>
                    <li>
                        <%--alles flushen--%>
                        <a href="site/teksten.jsp?flushname=plaatsvanherinnering">
                            <img src="${pageContext.request.contextPath}/edit/system/img/list_grey.png" />
                            Statische teksten
                        </a>
                    </li>
                    <li>
                        <a href="attachments/attachments.jsp">
                            <img src="${pageContext.request.contextPath}/edit/system/img/list_grey.png" />
                            Bijlagen
                        </a>
                    </li>
                    <li>
                        <%--flush 'quick' eigenlijk wil de plaatsen_[nodenr] en de herinneringen_[nodenr] flushen waar
                        nodenr het nummer is van de plaats die aan de (nieuwe) herinnering is gekoppeld. Maar dat kan (nog) niet
                        met het huidige systeem
                        oplossing: plaatsen[memoryresponse.posrel.memorylocation]
                        met deze syntax zou je de eerste gerelateerde memorylocation node kunnen gebruiken die via
                        posrel aan memoryresponse hangt.
                        Ook beperkt, maar bied wel mogelijkheden.
                        --%>
                        <a href="plaats/nieuweherinneringen.jsp?flushname=plaatsen_[memoryresponse.related.memorylocation]">
                        <img src="${pageContext.request.contextPath}/edit/system/img/list_grey.png" />
                        Nieuwe herinneringen
                    </li>
                </ul>
            </div>

            <div class="menuSection">
                <h3>Cache flushen</h3>
                De cache moet worden geflushed wanneer er een plaats is toegevoegd, of wanneer er nieuwe content is
                gekoppeld aan een bestaande plaats.
                <ul class="menuitem">
                    <li>
                        <a href="cachetest.jsp?group=plaatsvanherinnering&back=true"/>
                            <img src="recycle.gif" width="16" />
                             Flush alle pagina's
                        </a>
                    </li>
                    <li>
                        <a href="cachetest.jsp?group=plaatsvanherinnering_quick&back=true"/>
                            <img src="recycle.gif" width="16" />
                             Flush laatste herinneringen en landkaart.
                        </a>
                    </li>
                </ul>
            </div>
        </div>

        <mm:include page="../edit/bookmarks.jsp">
            <mm:param name="columns" value="pvhcol1,pvhcol2,pvhcol3" />
        </mm:include>
        </body>
    </html>
</mm:content>
