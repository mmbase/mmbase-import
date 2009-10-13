<mm:previousbatches max="1"><a title="previous page" href='<mm:url referids="parameters,$parameters,url"><mm:param name="offset"><mm:write /></mm:param>
       <mm:fieldlist id="search_form" nodetype="mmbaseusers" fields="$fields"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
   </mm:url>' ><img src="<mm:url page="${location}images/mmbase-left.gif"  />" alt="&lt;-" /></a></a>
</mm:previousbatches>
<mm:write value="$[+$offset / 10 + 1]" vartype="integer" /> / <mm:write value="$[+$totalsize / 10 + 1]" vartype="integer" />
<mm:nextbatches     max="1"><a title="next page" href='<mm:url referids="parameters,$parameters,url"><mm:param name="offset"><mm:write  /></mm:param>
      <mm:fieldlist  nodetype="mmbaseusers" fields="$fields"><mm:fieldinfo type="reusesearchinput" /></mm:fieldlist>
   </mm:url>' ><img src="<mm:url page="${location}images/mmbase-right.gif" />" alt="-&gt;" /></a>
</mm:nextbatches>