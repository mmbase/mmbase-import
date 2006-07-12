<%! public String lan_english(String wordToTranslate) {
	String translation = "missing entry in dictionary";
	if (wordToTranslate != null) {
		if(wordToTranslate.equals(""))
			{ translation=""; }
		else if(wordToTranslate.equals("januari"))
			{ translation="January"; }
		else if(wordToTranslate.equals("februari"))
			{ translation="February"; }
		else if(wordToTranslate.equals("maart"))
			{ translation="March"; }
		else if(wordToTranslate.equals("april"))
			{ translation="April"; }
		else if(wordToTranslate.equals("mei"))
			{ translation="May"; }
		else if(wordToTranslate.equals("juni"))
			{ translation="June"; }
		else if(wordToTranslate.equals("juli"))
			{ translation="July"; }
		else if(wordToTranslate.equals("augustus"))
			{ translation="August"; }
		else if(wordToTranslate.equals("september"))
			{ translation="September"; }
		else if(wordToTranslate.equals("oktober"))
			{ translation="October"; }
		else if(wordToTranslate.equals("november"))
			{ translation="November"; }
		else if(wordToTranslate.equals("december"))
			{ translation="December"; }
		else if(wordToTranslate.equals("Aangekocht werk"))
			{ translation="Painting in collection"; }
		else if(wordToTranslate.equals("Adviseurschap"))
			{ translation="Advisory board"; }
		else if(wordToTranslate.equals("Agenda"))
			{ translation="Events"; }
		else if(wordToTranslate.equals("Andere collecties"))
			{ translation="Other collections"; }
		else if(wordToTranslate.equals("Collectie"))
			{ translation="Collection"; }
		else if(wordToTranslate.equals("Collecties"))
			{ translation="Collections"; }
		else if(wordToTranslate.equals("CV"))
			{ translation="CV"; }
		else if(wordToTranslate.equals("Docentschap"))
			{ translation="Appointment"; }
		else if(wordToTranslate.equals("Catalogus"))
			{ translation="Catalogue"; }
		else if(wordToTranslate.equals("Congres"))
			{ translation="Congres"; }
		else if(wordToTranslate.equals("Contact"))
			{ translation="Contact"; }
		else if(wordToTranslate.equals("Projecten en aanstellingen"))
			{ translation="Projects and appointments"; }
		else if(wordToTranslate.equals("Email adres"))
			{ translation="Email address"; }
		else if(wordToTranslate.equals("Groepstentoonstelling"))
			{ translation="Group exhibition"; }
		else if(wordToTranslate.equals("Groepstentoonstellingen"))
			{ translation="Group exhibitions"; }
		else if(wordToTranslate.equals("Klik voor afbeelding op orginele grootte"))
			{ translation="Click for original size image"; }
		else if(wordToTranslate.equals("Links"))
			{ translation="Links"; }
		else if(wordToTranslate.equals("Naam"))
			{ translation="Name"; }
		else if(wordToTranslate.equals("naar de homepage"))
			{ translation="to the homepage"; }
		else if(wordToTranslate.equals("Nee, ik wil geen nieuws per email ontvangen"))
			{ translation="No, I don't want to receive news by email."; }
		else if(wordToTranslate.equals("olieverf op linnen"))
			{ translation="oil on canvas"; }
		else if(wordToTranslate.equals("Opleiding"))
			{ translation="Education"; }
		else if(wordToTranslate.equals("Opleidingen"))
			{ translation="Education"; }
		else if(wordToTranslate.equals("printbare versie"))
			{ translation="printable version"; }
		else if(wordToTranslate.equals("print"))
			{ translation="print"; }
		else if(wordToTranslate.equals("Privecollectie"))
			{ translation="Private collection"; }
		else if(wordToTranslate.equals("Publicatie"))
			{ translation="Publication"; }
		else if(wordToTranslate.equals("Publicaties"))
			{ translation="Publications"; }
		else if(wordToTranslate.equals("selectie")) 
			{ translation="selection"; }
		else if(wordToTranslate.equals("Solotentoonstelling")) 
			{ translation="Solo exhibition"; }
		else if(wordToTranslate.equals("Solotentoonstellingen"))
			{ translation="Solo exhibitions"; }
		else if(wordToTranslate.equals("sluit dit venster"))
			{ translation="close this window"; }
		else if(wordToTranslate.equals("Tekst van de publicatie"))
			{ translation="Text of the publication"; }
		else if(wordToTranslate.equals("tempera op papier"))
			{ translation="tempera on paper"; }
		else if(wordToTranslate.equals("terug"))
			{ translation="back"; }
		else if(wordToTranslate.equals("terug naar het formulier"))
			{ translation="back to the form"; }
		else if(wordToTranslate.equals("top"))
			{ translation="top"; }
		else if(wordToTranslate.equals("t/m"))
			{ translation="to"; }
		else if(wordToTranslate.equals("Uw vraag of reactie"))
			{ translation="Your comment or question"; }
		else if(wordToTranslate.equals("Verstuur"))
			{ translation="Send"; }
		else if(wordToTranslate.equals("volgende"))
			{ translation="next"; }
		else if(wordToTranslate.equals("vorige"))
			{ translation="previous"; }
		else if(wordToTranslate.equals("Vragen over de technische support van deze site?"))
			{ translation="Questions about the technical support of this site?"; }
		else if(wordToTranslate.equals("Werkperiode"))
			{ translation="Research period"; }
		else if(wordToTranslate.equals("Zonder titel"))
			{ translation="Untitled"; }
		
	}
	return translation;
}
%>