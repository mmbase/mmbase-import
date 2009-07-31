<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="mm" uri="http://www.mmbase.org/mmbase-taglib-1.0"%>
<%@taglib prefix="util"tagdir="/WEB-INF/tags/vpro-wizards/util"%>

<script type="text/javascript" src="${pageContext.request.contextPath}/mmbase/vpro-wizards/system/javascript/javascript.js"></script>

<%-- a comma separated list of bookmarkfolder nodes representing the three columns--%>
<c:if test="${empty param.columns}">
    <util:throw message="je kunt bookmarks.jsp niet includen zonder parameter 'columns' deze moet als waarde drie door komma gescheiden aliases van nodes van het type Bookmarkfolder bevatten"/>
</c:if>
<c:set var="_columns" value="${param.columns}" />
<c:set var="_colmap" value="${fn:split(_columns, ',')}"/>


<mm:cloud method="asis">
    <c:set var="editbookmarks" >
        <c:if test="${not empty param.edit}">editBookmarks</c:if>
    </c:set>

    <div id="bookmarks" class="bookmarks ${editbookmarks}">
        <div class="header">
            <c:choose>
                <c:when test="${empty param.edit}">
                    <a id="bookmarksEdit" style="display:block" href="?edit=true" class="editButton">pas aan
                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/edit_white.png" class="icon" border="0" alt="Edit" title="Edit"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <a id="bookmarksReady" style="display:block" href="${pageContext.request.servletPath}" class="editButton">klaar
                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/edit_white.png" class="icon" border="0" alt="Edit" title="Edit"/>
                    </a>
                </c:otherwise>
            </c:choose>
            <h3>bookmarks</h3>
        </div>

        <div class="add">
        <a href="${pageContext.request.contextPath}/edit/desktop/categorie.jsp?folder=${_colmap[0]}" class="addButton">
            nieuwe categorie
            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/new2.png" class="icon" border="0" alt="">
        </a>
        </div>

        <%--
            iterate over the bookmarkfolders
        --%>
        <c:set var="counter" value="-1" />
        <c:forTokens var="__node" items="${_columns}" delims=",">
            <c:set var="counter" value="${counter + 1}" />
            <div class="categorieKolom">
                <mm:list nodes="${__node}" path="bookmarkfolders,posrel,categories" orderby="posrel.pos,posrel.number" directions="up,down">
                    <div class="categorie">

                        <%--the category header--%>
                        <div class="header">
                            <c:set var="_cat" ><mm:field name="categories.number" /></c:set>
                            <c:set var="_posrel" ><mm:field name="posrel.number" /></c:set>

                            <%--edit and delete category--%>
                            <div class="icons">
                                <a href="${pageContext.request.contextPath}/edit/desktop/categorie.jsp?nodenr=${_cat}">
                                    <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/edit_white.png" class="icon" border="0" alt="" title="Pas aan"/>
                                </a>
                                <mm:link page="/wizard/post">
                                    <mm:param name="deleteNodeActions[1].number" value="${_cat}" />
                                    <a href="${_}" onclick="return doConfirm(true, 'Weet je zeker dat je deze container wilt weg gooien?');">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/delete_white.png" class="icon" border="0" alt="" title="Verwijder">
                                    </a>
                                </mm:link>
                            </div>

                            <div class="move">
                                <%--can't move the first column left--%>
                                <c:if test="${counter gt 0}">
                                    <a href="${pageContext.request.contextPath}/mmbase/vpro-wizards/changecontainer.jsp?oldPosrel=${_posrel}&amp;newContainer=${_colmap[counter - 1]}&amp;nodenr=${_cat}">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/arrow_left_white.png" class="icon" border="0" alt=""/>
                                    </a>
                                </c:if>

                                <%-- can not move the first one up--%>
                                <mm:first inverse="true">
                                <%--
                                    <a href="${pageContext.request.contextPath}/mmbase/vpro-wizards/changeposrelnew.jsp?container=${_colmap[counter]}&amp;node=${_cat}&amp;direction=down&amp;type=categories">
                                    --%>
                                    <mm:link page="/wizard/post">
                                        <mm:param name="posrelSortActions[0].number" value="${_cat}"/>
                                        <mm:param name="posrelSortActions[0].direction" value="up" />
                                        <mm:param name="posrelSortActions[0].containerNode" value="${_colmap[counter]}" />
                                        <a href="${_}">
                                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/arrow_up_white.png" class="icon" border="0" alt=""/>
                                        </a>
                                    </mm:link>
                                </mm:first>

                                <%--can not move the last one down--%>
                                <mm:last inverse="true">
                                    <mm:link page="/wizard/post">
                                        <mm:param name="posrelSortActions[0].number" value="${_cat}"/>
                                        <mm:param name="posrelSortActions[0].direction" value="down" />
                                        <mm:param name="posrelSortActions[0].containerNode" value="${_colmap[counter]}" />
                                        <%--
                                        <a href="${pageContext.request.contextPath}/mmbase/vpro-wizards/changeposrelnew.jsp?container=${_colmap[counter]}&amp;node=${_cat}&amp;direction=up&amp;type=categories">
                                        --%>
                                        <a href="${_}">
                                            <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/arrow_down_white.png" class="icon" border="0" alt=""/>
                                        </a>
                                    </mm:link>
                                </mm:last>

                                <%--can not move the last column right--%>
                                <c:if test="${counter lt 2}">
                                    <a href="${pageContext.request.contextPath}/mmbase/vpro-wizards/changecontainer.jsp?oldPosrel=${_posrel}&amp;newContainer=${_colmap[counter + 1]}&amp;nodenr=${_cat}">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/arrow_right_white.png" class="icon" border="0" alt=""/>
                                    </a>
                                    </c:if>
                            </div>
                             <mm:field name="categories.name"/>
                        </div>

                        <ul>
                            <mm:node element="categories">
                                <mm:related path="posrel,bookmarks" orderby="posrel.pos">
                                    <mm:field name="bookmarks.url" jspvar="_url">
                                        <li><a href="${_url}" target="_blank"><mm:field name="bookmarks.title"/></a></li>
                                    </mm:field>
                                </mm:related>
                                <mm:related path="posrel,attachments" orderby="posrel.pos">
                                    <li class="bijlage">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/paperclip.png" class="icon" border="0" alt="" title="Bijlage">
                                        <mm:field name="attachments.number" jspvar="_at">
                                        <util:attachment nodenr="${_at}" urlvar="_url"/>
                                            <a href="${_url}"><mm:field name="attachments.title"/></a>
                                        </mm:field>
                                    </li>
                                </mm:related>
                                <mm:related path="freetext">
                                    <li class="note">
                                        <img src="${pageContext.request.contextPath}/mmbase/vpro-wizards/img/note.png" class="icon" border="0" alt="" title="Note">
                                        <mm:field name="freetext.title"/>
                                    </li>
                                </mm:related>
                            </mm:node>
                        </ul>
                    </div>
                </mm:list>
            </div>
        </c:forTokens>
    </div>
</mm:cloud>
