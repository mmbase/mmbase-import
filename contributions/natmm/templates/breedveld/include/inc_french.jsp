<%! public String lan_french(String wordToTranslate) {
	String translation = "missing entry in dictionary";
	if (wordToTranslate != null) {
		if(wordToTranslate.equals(""))
			{ translation=""; }
		else if(wordToTranslate.equals("januari"))
			{ translation="janvier"; }
		else if(wordToTranslate.equals("februari"))
			{ translation="février"; }
		else if(wordToTranslate.equals("maart"))
			{ translation="mars"; }
		else if(wordToTranslate.equals("april"))
			{ translation="avril"; }
		else if(wordToTranslate.equals("mei"))
			{ translation="mai"; }
		else if(wordToTranslate.equals("juni"))
			{ translation="juin"; }
		else if(wordToTranslate.equals("juli"))
			{ translation="juillet"; }
		else if(wordToTranslate.equals("augustus"))
			{ translation="août"; }
		else if(wordToTranslate.equals("september"))
			{ translation="septembre"; }
		else if(wordToTranslate.equals("oktober"))
			{ translation="octobre"; }
		else if(wordToTranslate.equals("november"))
			{ translation="novembre"; }
		else if(wordToTranslate.equals("december"))
			{ translation="décembre"; }
		else if(wordToTranslate.equals("Aangekocht werk"))
			{ translation="Oeuvre dans la collection"; }
		else if(wordToTranslate.equals("Adviseurschap"))
			{ translation="Advisory board"; }
		else if(wordToTranslate.equals("Agenda"))
			{ translation="Evènements"; }
		else if(wordToTranslate.equals("Andere collecties"))
			{ translation="Autres collections"; }
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
			{ translation="Seminaire"; }
		else if(wordToTranslate.equals("Contact"))
			{ translation="Contact"; }
		else if(wordToTranslate.equals("Projecten en aanstellingen"))
			{ translation="Activitées diverses"; }
		else if(wordToTranslate.equals("Email adres"))
			{ translation="Adresse électronique"; }
		else if(wordToTranslate.equals("Groepstentoonstelling"))
			{ translation="Exposition collective"; }
		else if(wordToTranslate.equals("Groepstentoonstellingen"))
			{ translation="Expositions collectives"; }
		else if(wordToTranslate.equals("Klik voor afbeelding op orginele grootte"))
			{ translation="Click for original size image"; }
		else if(wordToTranslate.equals("Links"))
			{ translation="Liens"; }
		else if(wordToTranslate.equals("Naam"))
			{ translation="Nom"; }
		else if(wordToTranslate.equals("naar de homepage"))
			{ translation="au homepage"; }
		else if(wordToTranslate.equals("Nee, ik wil geen nieuws per email ontvangen"))
			{ translation="Non, je ne pas voudrais recevoir vos informations par courriel."; }
		else if(wordToTranslate.equals("olieverf op linnen"))
			{ translation="huile sur toile"; }
		else if(wordToTranslate.equals("Opleiding"))
			{ translation="Education"; }
		else if(wordToTranslate.equals("Opleidingen"))
			{ translation="Education"; }
		else if(wordToTranslate.equals("printbare versie"))
			{ translation="version à imprimer"; }
		else if(wordToTranslate.equals("print"))
			{ translation="imprimer"; }
		else if(wordToTranslate.equals("Privecollectie"))
			{ translation="Collection privée"; }
		else if(wordToTranslate.equals("Publicatie"))
			{ translation="Publication"; }
		else if(wordToTranslate.equals("Publicaties"))
			{ translation="Publications"; }
		else if(wordToTranslate.equals("selectie")) 
			{ translation="sélection"; }
		else if(wordToTranslate.equals("Solotentoonstelling")) 
			{ translation="Exposition personelle"; }
		else if(wordToTranslate.equals("Solotentoonstellingen"))
			{ translation="Expositions personelles"; }
		else if(wordToTranslate.equals("sluit dit venster"))
			{ translation="fermer"; }
		else if(wordToTranslate.equals("Tekst van de publicatie"))
			{ translation="Texte de la publication"; }
		else if(wordToTranslate.equals("tempera op papier"))
			{ translation="tempéra sur papier"; }
		else if(wordToTranslate.equals("terug"))
			{ translation="retour"; }
		else if(wordToTranslate.equals("terug naar het formulier"))
			{ translation="retour à la page précédente"; }
		else if(wordToTranslate.equals("top"))
			{ translation="haut de page"; }
		else if(wordToTranslate.equals("t/m"))
			{ translation="au"; }
		else if(wordToTranslate.equals("Uw vraag of reactie"))
			{ translation="Votre commentaire ou question"; }
		else if(wordToTranslate.equals("Verstuur"))
			{ translation="Envoye"; }
		else if(wordToTranslate.equals("volgende"))
			{ translation="suivant"; }
		else if(wordToTranslate.equals("vorige"))
			{ translation="précédent"; }
		else if(wordToTranslate.equals("Vragen over de technische support van deze site?"))
			{ translation="Questions about the technical support of this site?"; }
		else if(wordToTranslate.equals("Werkperiode"))
			{ translation="Résidence"; }
		else if(wordToTranslate.equals("Zonder titel"))
			{ translation="Sans titre"; }
		
	}
	return translation;
}
%>