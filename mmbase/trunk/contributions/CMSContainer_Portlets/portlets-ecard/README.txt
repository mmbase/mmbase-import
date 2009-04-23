Functionality

The user can select a gallery from a select box and the images contained in the 
gallery will be displayed. When a image is selected the user goes to the next 
screen where the selected image is displayed in a larger format. When the user 
selects the link "Omdraaien", a form is displayed which can be filled in for 
sending the ecard. If the data is valid, an ecard object is created from the 
entered values, an email is sent and a confirmation screen is displayed. 
The sent email uses the email settings from the ecard portlet and contains a 
generated link on which the receiver can click to view the ecard.
 
Configuration

The following steps are necessary for configuring the ecard:
On the Admin side: 
-	A view with the location ecard/funpage.jsp 
-	A multiportlet with definition ecardportlet which has the above defined template
-	An existing layout should include the ecardportlet for the location "content"
In the Bibliotheek: 
-	A few galleries with images should be created in a new channel.
On the Pagina Beheer 
-	On the "E-cards" page, on the "content" location, the ecardportlet should be selected and the 
	corresponding configured view. The channel with the image galleries should also be specified. 
	The names of these galleries will appear in the select box.
-	The following portlet properties should be configured, they are used when sending the email: 
		Sender email - The email address of the sender ( if it is not configured, no email is sent )
		Sender name - Email subject - The subject of the email ( if it is not configured, no email is sent )
		Email text before - The email text to be inserted before the generated link. The email can contain 
			the tags: #TO# and #FROM# which will be replaced with the "nameTo" and "nameFrom" filled in 
			in the ecard form.
		Email text after - The email text to be inserted after the generated link.
		Confirmation text - The confirmation text which is displayed on the last page. 
