Email application allows you to send emails from mmbase, it can be used in several ways and can mail to one several or groups of users. It also allows more complex mails that as multipart mail's and dynamicly generated mails.

web-app = the web-app you installed mmbase-webapp in for example tomcat/webapps/ROOT or tomcat/webapps/mmbase-webbapp for tomcat

How to install:

- build the applications with : ant email (from the applications dir)

- copy build/mmbase-email.jar to your web-app/WEB-INF/lib/

- make sure your application server has access to mail.jar and activation.jar
  for example by placing them (for tomcat) in commons/endorced.

copy examples/* to our web-app/emailexamples/ (for example)

setup your mailhosts in your application server for example
in tomcat do (in this example its the ROOT app) inside the <Engine><Host> tag:

        <!-- Tomcat Root Context -->
          <Context path="" docBase="ROOT" debug="0">

      <!-- You should use the following with Tomcat 5.5 and up  (active) -->
	  <Resource name="mail/Session" auth="Container" type="javax.mail.Session" mail.smtp.host="smtp.xs4all.nl" /> 
      <!-- end of Tomcat 5.5 -->

      <!-- for older then 5.5.0 tomcats (inactive)
          <Resource name="mail/Session" auth="Container" type="javax.mail.Session"/>
            <ResourceParams name="mail/Session">
            <parameter>
              <name>mail.smtp.host</name>
              <value>smtp.xs4all.nl</value>
            </parameter>
          </ResourceParams> 
      -->
          <ResourceLink name="linkToGlobalResource"
                    global="simpleValue"
                    type="java.lang.Integer"/>
        </Context>

restart your application server or webapp.

You should now see something like :

INFO    mmbase.applications.email.SendMail - Module SendMail started (datasource = mail/Session -> {mail.transport.protocol=smtp, scope=Shareable, auth=Container, mail.smtp.host=smtp.xs4all.nl})

Now you can use the examples found you installed !!

**** DON'T FORGET TO CHANGE THE TO AND FROM IN EACH EXAMPLE***

at best i will get more mail, but probably your isp will block
your mail.
 
