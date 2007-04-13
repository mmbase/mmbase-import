        <div class="body">
        <html:form action="/editors/resources/SearchGuestBookAction" method="post">
            <html:hidden property="action" value="guestmessages" />
            <html:hidden property="offset" />
            <html:hidden property="order" />
            <html:hidden property="direction" />

            <mm:import id="contenttypes" jspvar="contenttypes">guestmessage</mm:import>
            <%@include file="guestbookform.jsp"%>
        </html:form>
        </div>
