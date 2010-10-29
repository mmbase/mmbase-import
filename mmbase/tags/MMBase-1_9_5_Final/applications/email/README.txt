The email application allows you to send email from MMBase. It can be used in several ways: send mail from a form, mail a groups of users, use it as smtp mail etc. Refer to the documentation and examples.


INSTALLATION
------------

- Build the application: 
    mvn clean install (from this directory)

- Copy the resulting jar to the your web-app's '/WEB-INF/lib/' directory.

- Make sure your application server has access to 'mail.jar' and 'activation.jar'. In the case of Tomcat: by placing them in 'commons/endorced' (version 5) or 'lib' (version 6), or your webapp's 'WEB-INF/lib' directory. You can download them at: http://java.sun.com/products/javamail/

- If you would like to try out the examples copy the 'examples' directory to your web-app, if it isn't already installed in 'mmbase/examples' or 'mmexamples'.


CONFIGURATION
-------------
Setup your mail host in your application server. For example for Tomcat you can specify the following inside a context:

a. For Tomcat (versions 5.5 and up) for example the ROOT webapp:
    
    <Context path="" docBase="ROOT" debug="0">
      <!-- there is probably more stuff here... -->
      
      <Resource name="mail/Session" auth="Container" type="javax.mail.Session"
          mail.smtp.host="smtp.xs4all.nl" /> 
    </Context>
    
b. For older versions of Tomcat (< 5.5)

    <Context path="" docBase="ROOT" debug="0">
      <Resource name="mail/Session" auth="Container" type="javax.mail.Session"/>
        <ResourceParams name="mail/Session">
          <parameter>
            <name>mail.smtp.host</name>
            <value>smtp.xs4all.nl</value>
          </parameter>
        </ResourceParams> 
        <ResourceLink name="linkToGlobalResource"
          global="simpleValue"
          type="java.lang.Integer"/>
    </Context>

Restart your application server or webapp. You should now see something like :

    INFO    mmbase.applications.email.SendMail - Module SendMail started (datasource = mail/Session -> {mail.transport.protocol=smtp, scope=Shareable, auth=Container, mail.smtp.host=smtp.xs4all.nl})

Try out the examples!

**** DON'T FORGET TO CHANGE THE TO AND FROM IN EACH EXAMPLE ***
At best I will get more mail, but probably your ISP will block
your mail.
