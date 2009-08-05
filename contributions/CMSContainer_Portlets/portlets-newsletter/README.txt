* Functional Description *
The Newsletter lets editors send publications of content inside the CMS to members of the newsletter. 
The publications can be scheduled or be send automatically.
People can subscribe to a newsletter and receive publications.

* Technical Description *
The newsletter bundle is composed of several components that each provide a set of features to the
bundle. The newsletter bundle provides additional functionality to the CMS Container. This is done by adding
new portlets, modules and back-end code. The newsletter bundle is not a separate application layer on
top of the CMS Container, but integrates with the existing applications.

** Usage **

1) Subscription portlet
Used for logged in users to sign up to a newsletter

* View
File: newsletter/newslettersubscription.jsp
Title: Newsletter Subscription

* Multi Portlet Definition
Title: Newsletter Subscription Portlet
Definition: newslettersubscriptionportlet
View: Newsletter Subscription

* Layouts
Add the portlet to your layout and configure it at the positions allowed.