 <mm:related path="category" fields="category.number,category.title" max="1">
        <mm:node element="category">
        <mm:import id="cat_nr" />
        <mm:field name="title" />
                <mm:related path="category,pages" fields="pages.number" max="1">
                        <mm:field name="pages.number" id="page_nr" />
                </mm:related>
                <mm:related path="posrel,news,mmevents"
                        fields="news.title,news.intro,mmevents.start"
                        orderby="mmevents.start" directions="DOWN"
                        max="3">
                       <p><a href="<mm:url page="index.jsp" referids="portal"><mm:present referid="page_nr"><mm:param name="page"><mm:write referid="page_nr" /></mm:param></mm:present><mm:param name="newsnr"><mm:field name="news.number" /></mm:param></mm:url>"><mm:field name="news.title" /></a><br />
                        <mm:field name="news.intro" /> - <mm:field name="mmevents.start"><mm:time format="dd/MM/yyyy" /></mm:field></p>
                        <mm:last>
                        <p><a href="<mm:url page="index.jsp" referids="portal"><mm:present referid="page_nr"><mm:param name="page"><mm:write referid="page_nr" /></mm:param></mm:present></mm:url>">Newsarchive</a>
                        </mm:last>
                </mm:related>
       </mm:node>
</mm:related>

