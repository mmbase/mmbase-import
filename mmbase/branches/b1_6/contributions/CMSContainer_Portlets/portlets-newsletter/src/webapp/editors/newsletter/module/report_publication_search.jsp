<input type="hidden" name="newsletterId" value="${requestScope.newsletterId}"/>
<table border="0" cellspacing="0" cellpadding="0">
   <tr>
      <td width="110px"><fmt:message key="newsletter.publication.search.title"/></td>
      <td width="150px"><html:text property="title" size="30"/></td>
      <td width="100px" align="center"><fmt:message key="newsletter.publication.search.fromperiod"/>:</td>
      <td>
         <html:select property="period">
            <html:option value="0" key="newsletter.publication.search.period.all" bundle="newsletter"/>
            <html:option value="1" key="newsletter.publication.search.period.lastday" bundle="newsletter"/>
            <html:option value="7" key="newsletter.publication.search.period.lastweek" bundle="newsletter"/>
            <html:option value="14" key="newsletter.publication.search.period.lasst2week" bundle="newsletter"/>
            <html:option value="30" key="newsletter.publication.search.period.lastmonth" bundle="newsletter"/>
            <html:option value="365" key="newsletter.publication.search.period.lastyear" bundle="newsletter"/>
         </html:select>
      </td>
   </tr>
   <tr>
      <td><fmt:message key="newsletter.publication.search.subject"/></td>
      <td><html:text property="subject" size="30" /></td>
      <td align="center"><html:submit><fmt:message key="newsletter.publication.search"/></html:submit></td>
      <td>&nbsp;</td>
   </tr>
</table>