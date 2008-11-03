<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud>
<mm:import externid="action" />
<mm:import externid="id" />

<mm:compare value="sendsms" referid="action">
	<mm:import externid="address" />
	<mm:import externid="message" />
	<mm:import id="smsfeedback"><mm:function set="mmsms" name="sendSMS" referids="address,message" /></mm:import>
</mm:compare>

</mm:cloud>
