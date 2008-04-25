
<form action="<mm:url referids="parameters,$parameters" />" method="post">

<table class="list">
  <tr class="listsearch">
    <td colspan="3">
      Maintainer:
      <select name="smaintainer">
        <option value="-1">any</option>
        <mm:listnodescontainer path="users,groups">
          <mm:constraint field="groups.name" value="BugTrackerCommitors" />
          <mm:sortorder  field="lastname" />
          <mm:listnodes>
            <option <mm:field name="number">value="<mm:write />" <mm:compare referid2="smaintainer">selected="selected"</mm:compare></mm:field> ><mm:field name="firstname" /> <mm:field name="lastname" /></option>
          </mm:listnodes>
        </mm:listnodescontainer>
      </select>
    </td>
    <td colspan="4">
      Submitter:
      <select name="ssubmitter">
        <option value="-1">any</option>
        <mm:listnodescontainer path="users">
          <mm:sortorder  field="lastname" />
          <mm:listnodes>
            <option <mm:field name="number">value="<mm:write />" <mm:compare referid2="ssubmitter">selected="selected"</mm:compare></mm:field> ><mm:field name="firstname" /> <mm:field name="lastname" /></option>
          </mm:listnodes>
        </mm:listnodescontainer>
      </select>
    </td>
  <td colspan="2">&nbsp;</td>
</tr>
<tr class="listsearch">
  <td width="50">
      <input name="sbugid" size="4" />
    </td>
    <td width="50">
     <mm:context>
      <mm:import id="sstatus_list" vartype="list">open,accepted,rejected,pending,integrated,closed</mm:import>
      <mm:import id="sstatustest" externid="sstatus"/>
	<select name="sstatus">
           <option value="">any</option>
              <mm:stringlist id="sstatusitem" referid="sstatus_list">
             <mm:index>
              <mm:compare referid2="sstatustest"><option value="<mm:write/>" selected="true"><mm:write referid="sstatusitem"/></option></mm:compare>
              <mm:compare referid2="sstatustest" inverse="true"><option value="<mm:write/>"><mm:write referid="sstatusitem"/></option></mm:compare>
             </mm:index>
        </mm:stringlist>
	</select>
        </mm:context>
	</td>
	<td width="50">
        <mm:context>
        <mm:import id="stype_list" vartype="list">bug,wish,doc,docwish</mm:import>
        <mm:import id="stypetest" externid="stype"/>
	<select name="stype">
              <option value="">any</option>
              <mm:stringlist id="stypeitem" referid="stype_list">
             <mm:index id="index">
              <mm:compare referid2="stypetest"><option value="<mm:write/>" selected="true"><mm:write referid="stypeitem"/></option></mm:compare>
              <mm:compare referid2="stypetest" inverse="true"><option value="<mm:write/>"><mm:write referid="stypeitem"/></option></mm:compare>
             </mm:index>
           </mm:stringlist>
	</select>
        </mm:context>
	</td>
	<td width="50">
        <mm:context>
        <mm:import id="spriority_list" vartype="list">high,medium,low</mm:import>
        <mm:import id="sprioritytest" externid="spriority"/>
	<select name="spriority">
              <mm:stringlist id="spriorityitem" referid="spriority_list">
             <mm:index>
              <mm:compare referid2="sprioritytest"><option value="<mm:write/>" selected="true"><mm:write referid="spriorityitem"/></option></mm:compare>
              <mm:compare referid2="sprioritytest" inverse="true"><option value="<mm:write/>"><mm:write referid="spriorityitem"/></option></mm:compare>
             </mm:index>
        </mm:stringlist>
	</select>
        </mm:context>
	</td>
	<td width="50">
	<input name="sversion" size="3" value="<mm:write referid="sversion" />" />
	</td>
	<td width="50">
	<input name="sfixedin" size="3" value="<mm:write referid="sfixedin" />" />
	</td>
	<td width="50">
	<select name="sarea">
		<option value="">any</option>
		<mm:listnodes type="areas" orderby="areas.name">
		<option <mm:field name="number"><mm:compare value="$sarea">selected="selected"</mm:compare>  value="<mm:write />" </mm:field>><mm:field name="substring(name,15,.)" />
		</mm:listnodes>
	</select>
	</td>
	<td width="300">
	<input name="sissue" size="20" value="<mm:write referid="sissue" />" />
	</td>
	<td>
	<input type="submit" value="search" />
	</td>
</tr>
</form>
<tr>
   <th width="50"> Bug # </th>	<th>Status</th> <th> Type </th> <th> Priority </th> <th> Version </th> <th> Fixed in</th> <th> Area </th> <th> Issue </th> <th> &nbsp; </th>
</tr>
<!-- the real searchpart -->
<mm:url id="pagingurl" referids="parameters,$parameters,sissue,sstatus,stype,sversion,sfixedin,sbugid,sarea,spriority,smaintainer,ssubmitter" write="false" />

<mm:write referid="smaintainer">
  <mm:compare value="-1">
    <mm:write referid="ssubmitter">
       <mm:compare value="-1">
          <mm:import id="root">pools</mm:import>
       </mm:compare>
       <mm:compare value="-1" inverse="true">
           <mm:import id="root">users,rolerel</mm:import>
       </mm:compare>
    </mm:write>
  </mm:compare>
  <mm:compare value="-1" inverse="true">
    <mm:import id="root">users,rolerel</mm:import>
  </mm:compare>
</mm:write>


<mm:listcontainer path="$root,bugreports,areas">
  <mm:write referid="root">
    <mm:compare value="pools">
      <mm:constraint field="pools.number" value="BugTracker.Start" />
    </mm:compare>
    <mm:compare value="pools" inverse="true">
      <mm:composite operator="or">
       <mm:write referid="smaintainer">
         <mm:isgreaterthan value="0">
           <mm:composite operator="and">	
             <mm:constraint field="rolerel.role" value="maintainer" />
             <mm:constraint field="users.number" value="$_" />
           </mm:composite>
         </mm:isgreaterthan>
       </mm:write>
       <mm:write referid="ssubmitter">
         <mm:isgreaterthan value="0">
           <mm:composite operator="and">	
             <mm:constraint field="rolerel.role" value="submitter" />
             <mm:constraint field="users.number" value="$_" />
           </mm:composite>
         </mm:isgreaterthan>
       </mm:write>
     </mm:composite>
    </mm:compare>
  </mm:write>

  
  <mm:sortorder  field="bugreports.bugid" direction="down" />

  <%-- what a mess --%>
  <mm:write referid="sissue">
    <mm:isnotempty><mm:constraint field="bugreports.issue" operator="LIKE" value="%$_%" /></mm:isnotempty>
  </mm:write>
  <mm:write referid="sstatus">
    <mm:isnotempty><mm:constraint field="bugreports.bstatus" value="$_" /></mm:isnotempty>
  </mm:write>
  <mm:write referid="stype">
    <mm:isnotempty><mm:constraint field="bugreports.btype" value="$_" /></mm:isnotempty>
  </mm:write>
  <mm:write referid="sversion">
    <mm:isnotempty><mm:constraint field="bugreports.version" operator="LIKE" value="%$_%" /></mm:isnotempty>
  </mm:write>
  <mm:write referid="sfixedin">
    <mm:isnotempty><mm:constraint field="bugreports.fixedin" operator="LIKE" value="%$_%" /></mm:isnotempty>
  </mm:write>
  <mm:write referid="sbugid">
    <mm:isnotempty><mm:constraint field="bugreports.bugid" value="$_" /></mm:isnotempty>
  </mm:write>
  <mm:write referid="sarea">
    <mm:isnotempty><mm:constraint field="areas.number" value="$_" /></mm:isnotempty>
  </mm:write>
  <mm:write referid="spriority">
    <mm:isnotempty><mm:constraint field="bugreports.bpriority" value="$_" operator="<=" /></mm:isnotempty>
  </mm:write>

  <mm:size id="total" write="false" />
  <mm:write value="$[+$total/15 + 1]" vartype="integer" id="lastpage" write="false" />

  <mm:offset value="$noffset" />
  <mm:maxnumber value="15" />


  <mm:list fields="bugreports.bugid,bugreports.bstatus,bugreports.btype,bugreports.bpriority,bugreports.version,bugreports.fixedin,areas.name,bugreports.issue">
  <tr <mm:even>class="even"</mm:even>>
      <td>#<mm:field name="bugreports.bugid" /></td>
      <td>
	<mm:field name="bugreports.bstatus">
		<mm:compare value="1">Open</mm:compare>
		<mm:compare value="2">Accepted</mm:compare>
		<mm:compare value="3">Rejected</mm:compare>
		<mm:compare value="4">Pending</mm:compare>
		<mm:compare value="5">Integrated</mm:compare>
		<mm:compare value="6">Closed</mm:compare>
	</mm:field>
      </td>
		<td>
			 <mm:field name="bugreports.btype">
				<mm:compare value="1">Bug</mm:compare>
				<mm:compare value="2">Wish</mm:compare>
				<mm:compare value="3">DocBug</mm:compare>
				<mm:compare value="4">DocWish</mm:compare>
			 </mm:field>
		</td>
		<td>
			 <mm:field name="bugreports.bpriority">
				<mm:compare value="1">High</mm:compare>
				<mm:compare value="2">Medium</mm:compare>
				<mm:compare value="3">Low</mm:compare>
			 </mm:field>
		</td>
		<td>
			 <mm:field name="bugreports.version" />&nbsp;
		</td>
		<td>
			 <mm:field name="bugreports.fixedin" />&nbsp;
		</td>
		<td>
			 <mm:field name="areas.name" />&nbsp;
		</td>
		<td>
			 <mm:field name="bugreports.issue" escape="inline"/>&nbsp;
		</td>
		<td>
		    <a href="<mm:url referids="parameters,$parameters"><mm:param name="btemplate" value="fullview.jsp" /><mm:param name="bugreport"><mm:field name="bugreports.number" /></mm:param></mm:url>"><img src="<mm:url page="images/arrow-right.png" />" border="0" align="right"></a>
		</td>
</tr>
 <mm:last><mm:index id="lastindex" write="false" /></mm:last>
</mm:list>

<tr>
   <td colspan="1" class="listpaging">
     &nbsp;
     <mm:write referid="noffset">
       <mm:isgreaterthan value="0">
         <mm:isgreaterthan value="15">
           <a href="<mm:url referid="pagingurl"><mm:param name="noffset" value="0" /></mm:url>" alt="first page" title="first page"><img src="<mm:url page="images/arrow-left.png" />" border="0" /></a>
         </mm:isgreaterthan>
         <mm:write id="previouspage" value="$[+ $noffset - 15]" vartype="integer" write="false" />
         <a href="<mm:url referid="pagingurl"><mm:param name="noffset" value="$previouspage" /></mm:url>" alt="previous page" title="previous page"><img src="<mm:url page="images/arrow-left.png" />" border="0" /></a>
       </mm:isgreaterthan>
     </mm:write>
   </td>
   <td colspan="3" class="listpaging">
    &nbsp;
    <mm:previousbatches indexoffset="1" max="10">
	<mm:first><mm:index><mm:compare value="1" inverse="true">...</mm:compare></mm:index></mm:first>
        <a href="<mm:url referid="pagingurl"><mm:param name="noffset"><mm:write /></mm:param></mm:url>"><mm:index /></a>
        <mm:last inverse="true">,</mm:last>
    </mm:previousbatches>
   </td>
   <td colspan="3" class="listpaging" align="middle">
     <mm:write referid="total">
	<mm:compare value="0">
	  No bugs found in MMBase (ok, not the one you are looking for)
        </mm:compare>
	<mm:compare value="0" inverse="true">
	  <mm:index offset="1" />: <mm:write value="$[+ $noffset + 1]" vartype="integer" /> - <mm:write value="$[+ $noffset + $lastindex]" vartype="integer" /> / <mm:write />
        </mm:compare>
     </mm:write>
  </td>
  <td colspan="1" class="listpaging">
   <mm:nextbatches indexoffset="1" max="10">
       <a href="<mm:url referid="pagingurl"><mm:param name="noffset"><mm:write /></mm:param></mm:url>"><mm:index /></a>
       <mm:last inverse="true">, </mm:last>
       <mm:last><mm:index><mm:compare referid2="lastpage" inverse="true">...</mm:compare></mm:index></mm:last>
   </mm:nextbatches>

  </td>
  <td colspan="1" class="listpaging">
     <mm:write value="$[+ $noffset + 15]" vartype="integer">
       <mm:islessthan value="$total">
         <a href="<mm:url referid="pagingurl"><mm:param name="noffset" value="$_" /></mm:url>" alt="next page" title="next page"><img src="<mm:url page="images/arrow-right.png" />" border="0" /></a>
         <mm:islessthan value="$[+ $total - 15]">
           <mm:write id="lastoffset" value="$[+ ($lastpage  - 1) * 15]" vartype="integer" write="false" />
           <a href="<mm:url referid="pagingurl"><mm:param name="noffset" value="$lastoffset" /></mm:url>" alt="last page" title="last page"><img src="<mm:url page="images/arrow-right.png" />" border="0" /></a>
         </mm:islessthan>
       </mm:islessthan>
     </mm:write>
  </td>
</tr>

</mm:listcontainer>
</table>

</form>
