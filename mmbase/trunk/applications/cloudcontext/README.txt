Context security in the cloud.

Use this in security.xml

	<authentication class="org.mmbase.security.implementation.cloudcontext.Authenticate" url="" />
	<authorization	class="org.mmbase.security.implementation.cloudcontext.Verify"	url="" />

Install the builders, and deploy the Security application.

(The builders are not present in the applications, install them by hand).