<mm:import externid="logid">-1</mm:import>
<mm:import externid="parentlog">-1</mm:import>

<mm:import externid="mode">phoneinfo</mm:import>

<!-- action check -->
<mm:import externid="action" />
<mm:present referid="action">
<mm:compare value="sendsms" referid="action">
        <mm:import externid="address" />
        <mm:import externid="message" />
        <mm:import id="smsfeedback"><mm:function set="mmsms" name="sendSMS" referids="address,message" /></mm:import>
	<mm:remove referid="address" />
</mm:compare>
<mm:compare value="sendwaplink" referid="action">
        <mm:import externid="address" />
        <mm:import externid="name" />
        <mm:import externid="link" />
        <mm:import id="smsfeedback"><mm:function set="mmsms" name="sendWAPLink" referids="address,name,link" /></mm:import>
	<mm:remove referid="address" />
</mm:compare>
<mm:compare value="sendincomingmessage" referid="action">
        <mm:import externid="address" />
        <mm:import externid="message" />
        <mm:import id="smsfeedback"><mm:function set="mmsms" name="simulateIncomingMessage" referids="address,message" /></mm:import>
	<mm:remove referid="address" />
</mm:compare>
</mm:present>
<!-- end action check -->



<table cellpadding="0" cellspacing="0" style="margin-top : 10px;" width="95%" border="0">
<tr>
<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Phone Information
	</th>
	</tr>
	  <mm:node referid="id">
 	    <tr><th>address</th><td align="left"><mm:field name="address" id="address" /></td>
 	    <tr><th>operator</th><td align="left"><mm:field name="operator" id="code"><mm:function set="mmsms" name="getOperatorName" referids="code" /></mm:field></td>
 	    <tr><th>create time</th><td align="left"><mm:field name="createtime"><mm:time format="dd MMM yyyy (hh:mm:ss)" /></mm:field></td>
 	    <tr><th>Incomming</th><td align="left">--</td>
 	    <tr><th>Outgoing</th><td align="left">--</td>
 	    <tr><th>Cost</th><td align="left">--</td>
 	    <tr><th>Profit</th><td align="left">--</td>
	  </mm:node>
</table>
</td>

<td width="50%" align="middle" valign="top">
<table cellpadding="0" cellspacing="0" class="list" style="margin-left : 10px;">
	<tr>
	<th colspan="2" width="225">
	Phone Actions
	</th>
	</tr>
 	    		<tr><th>Overview</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="phone" />
					<mm:param name="id" value="$id" />
					<mm:param name="mode">phoneinfo</mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
			</tr>
 	    		<tr><th>Send SMS</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="phone" />
					<mm:param name="id" value="$id" />
					<mm:param name="mode">sendsms</mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
			</tr>

 	    		<tr><th>Send WAP Link</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="phone" />
					<mm:param name="id" value="$id" />
					<mm:param name="mode">sendwaplink</mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
			</tr>

 	    		<tr><th>Simulate Incoming</th>
			<td>
			<A HREF="<mm:url page="index.jsp">
					<mm:param name="main" value="$main" />
					<mm:param name="sub" value="phone" />
					<mm:param name="id" value="$id" />
					<mm:param name="mode">simulateincomingmessage</mm:param>
				</mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>
			</td>
			</tr>
</table>
</td>

</tr>
</table>

<mm:compare referid="mode" value="phoneinfo">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 10px;" width="95%">
<tr>
	<th colspan="5">
	Last incoming messages of this phone	
	</th>
</tr>
<tr>
	<th>Command</th><th>Parameter</th><th>CreateTime</th><th>MessageId</th><th>&nbsp;</th>
</tr>
<mm:listnodescontainer type="mmsms">
<mm:constraint field="address" operator="EQUAL" value="$address" />
<mm:constraint field="direction" operator="EQUAL" value="incoming" />
<mm:sortorder field="createtime" direction="DOWN" />
<mm:listnodes max="25">
<tr>
	<td valign="top" align="left" width="10%">
		<mm:import id="id" reset="true"><mm:field name="number" /></mm:import>
		<mm:field name="substring(command,35,...)" />
	</td>
	<td valign="top" align="left" width="30%">
		<mm:field name="substring(parameter,20,...)" />
	</td>
	<td valign="top" align="left" width="20%">
		<mm:field name="createtime"><mm:time format="dd MMMM yyyy (hh:mm:ss)" /></mm:field>
	</td>
	<td valign="top" align="left" width="30%">
		<mm:field name="messageid" />
	</td>
        <td width="15"><A HREF="<mm:url page="index.jsp"><mm:param name="main" value="incoming" /><mm:param name="sub" value="sms" /><mm:param name="id" value="$id" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>                                     </td>
</tr>
</mm:listnodes>
</mm:listnodescontainer>
</table>


<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="95%">
<tr>
	<th colspan="5">
	Last outgoing messages to this phone	
	</th>
</tr>
<tr>
	<th>Command</th><th>Parameter</th><th>CreateTime</th><th>MessageId</th><th>&nbsp;</th>
</tr>
<mm:listnodescontainer type="mmsms">
<mm:constraint field="address" operator="EQUAL" value="$address" />
<mm:constraint field="direction" operator="EQUAL" value="outgoing" />
<mm:sortorder field="createtime" direction="DOWN" />
<mm:listnodes max="25">
<tr>
	<td valign="top" align="left" width="10%">
		<mm:import id="id" reset="true"><mm:field name="number" /></mm:import>
		<mm:field name="substring(command,35,...)" />
	</td>
	<td valign="top" align="left" width="30%">
		<mm:field name="substring(parameter,35,...)" />
	</td>
	<td valign="top" align="left" width="20%">
		<mm:field name="createtime"><mm:time format="dd MMM yyyy (hh:mm:ss)" /></mm:field>
	</td>
	<td valign="top" align="left" width="30%">
		<mm:field name="messageid" />
	</td>
        <td width="15"><A HREF="<mm:url page="index.jsp"><mm:param name="main" value="outgoing" /><mm:param name="sub" value="sms" /><mm:param name="id" value="$id" /></mm:url>"><IMG SRC="<mm:write referid="image_arrowright" />" BORDER="0" ALIGN="left"></A>                                     </td>
</tr>
</mm:listnodes>
</mm:listnodescontainer>
</table>
</mm:compare>
<mm:compare referid="mode" value="sendsms">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th colspan="2">
	Sending message to this phone : <mm:write referid="address" />
	</th>
</tr>
<tr>
        <form action="<mm:url page="index.jsp" referids="main,sub,id,address" />" method="post">
	<input name="mode" type="hidden" value="sendfeedback" />
        <input type="hidden" name="action" value="sendsms" />
	<td width="100"><center><b>Cost</b><br /><select name="cost">
			<option>0,00
			<option>0,25
			<option>0,35
			<option>0,40
			<option>0,55
			<option>0,70
			<option>0,80
			<option>0,90
			<option>1,10
		    </select>
		    </center>
	</td>
	<td>
	<textarea name="message" rows="3" style="width: 98%"></textarea>
	</td>
</tr>
<tr>
	<td colspan="2"><center><input type="submit" value="send sms now"></center></td>
</tr>
	</form>
</table>
</mm:compare>


<mm:compare referid="mode" value="sendwaplink">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th colspan="3">
	Sending wap link to this phone : <mm:write referid="address" />
	</th>
</tr>
<tr>
        <form action="<mm:url page="index.jsp" referids="main,sub,id,address" />" method="post">
	<input name="mode" type="hidden" value="sendfeedback" />
        <input type="hidden" name="action" value="sendwaplink" />
	<td width="100"><center><b>Cost</b><br /><select name="cost">
			<option>0,00
			<option>0,25
			<option>0,35
			<option>0,40
			<option>0,55
			<option>0,70
			<option>0,80
			<option>0,90
			<option>1,10
		    </select>
		    </center>
	</td>
	<td>
	name : <input name="name" size="20">
	</td>
	<td>
	link : <input name="link" size="45" value="http://" >
	</td>
</tr>
<tr>
	<td colspan="3"><center><input type="submit" value="send sms now"></center></td>
</tr>
	</form>
</table>
</mm:compare>


<mm:compare referid="mode" value="simulateincomingmessage">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th colspan="3">
	Simulate a incoming message
	</th>
</tr>
<tr>
        <form action="<mm:url page="index.jsp" referids="main,sub,id,address" />" method="post">
	<input name="mode" type="hidden" value="sendfeedback" />
        <input type="hidden" name="action" value="sendincomingmessage" />
	<td>
	address : <input name="address" size="20" value="<mm:write referid="address" />" />
	</td>
	<td>
	message : <input name="message" size="100" value="">
	</td>
</tr>
<tr>
	<td colspan="3"><center><input type="submit" value="send sms now"></center></td>
</tr>
	</form>
</table>
</mm:compare>

<mm:compare referid="mode" value="sendfeedback">
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 20px;" width="75%">
<tr>
	<th colspan="2">
	Feedback from the platform for : <mm:write referid="address" />
	</th>
</tr>
<tr>
        <form action="<mm:url page="index.jsp" referids="main,sub,id,address" />" method="post">
	<input name="mode" type="hidden" value="phoneinfo" />
	<td>
	<br />
<pre><mm:write referid="smsfeedback" /></pre>
	<br /><br />
	</td>
</tr>
<tr>
	<td colspan="2"><center><input type="submit" value="ok"></center></td>
</tr>
	</form>
</table>
</mm:compare>
