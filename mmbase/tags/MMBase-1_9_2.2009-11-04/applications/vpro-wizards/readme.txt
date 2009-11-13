vpro-wizards
===================================

De vpro-wizards zijn een beheer omgeving bouw doos, a la de editwizards, met de volgende verschillen:
-de editors zijn gewoon jsp. Een serie taglibs levert standaard bouwstenen.
-de backend is in java, en maakt gebruik van spring. Middels spring data binding
worden geposte formulieren omgezet in command beans die worden uitgevoerd.

frontend en backend zijn totaal ontkoppeld. Alle standaard commands die je nodig hebt zijn aanwezig.
Deze zijn:

- node aanmaken
- node bewerken
- node verwijderen
- relatie maken
- relatie sorteren.

het is heel makkelijk om je eigen specialistische commands toe te voegen (na de refactoring fase).

Tevens is er een kleine mmbase applicatie waarmee je in de start pagina van de editor een
sort desktop met links en notieties voor de redactie. Die is momenteel nog niet beschikbaar, maar wordt in 
een later stadium toegevoegd.

Er is een voorbeeld beheer omgeving die werkt met een van de standaard demo applicaties. Deze is te vinden op
<contextpad>/mmbase/mmexamples/vpro-wizards/

alhoewel de taglibs flexibel zijn opgebouwd, Wordt er wel uitgegaan van een structuur van drie soorten
pagina's.

1 list pagina: zoek een object van een bepaald type.
2 editor: toon de velden in een formulier en toon gerelateerde informatie
3 related pagina: toon alle gerelateerde items van een bepaald type. zoek andere items van dit type
 en koppel ze, of maak een nieuwe node van dit type aan.

De editors hebben nog een aantal beperkingen:
- validatie wordt nog niet ondersteund. dit staat wel hoog in de prioriteiten lijst.
- min-max constraints op gerelateerde objecten wordt niet ondersteund, en het is ook niet duidelijk
of en wanneer dat zal gaan gebeuren.
- er wordt nog geen gebruik gemaakt van ajax, hoewel dat voor bepaalde functies erg voor de hand ligt.
zal geleidelijk aan worden geimplementeerd.

De voordelen van het systeem zijn:
- Eenvoudig om mee te ontwikkelen
- Simpele architectuur (in tegestelling tot de editwizards)
- lekker snel
- zien er goed uit. redacteurs werken er graag mee.
- veel mogelijkheden voor verbeteringen.
- flexibel. een hoop tags zijn zeer configureerbaar door middel van fragment attributen.

Het zou goed kunnen dat dit systeem een goede vervanger zal blijken te zijn voor de editwizards.

integratie pad
===================================
De volgende stappen moeten worden gezet voor een volledige integratie van de 
vpro-wizards in mmbase:

status 		omschrijving
Y 		  	zorg dat de vpro wizards compileren in de mmbase build.
Y   		pas mmbase buildbase.properties aan: install.tagfiles
Y   		zorg dat de hele applicatie goed installeert met de build (de locaties van bestanden moeten veranderd worden).
Y   		maak een test beheer omgeving die samen werkt met een van de demo applicaties (news), zodat je kunt testen).
P   		refactor de java code.
N   		Documentatie: gedeeltijk bestaande omzetten naar docbook, gedeeltelijk nieuw.
P			Tool voor het generern van documentatie uit de tagfiles.
N   		zorg dat de fck richtext editor wordt gedownlaod, en niet in de cvs zit.
N			NekoHtml met xerces dependency: is er een vervanging?


Y = gedaan
P = onderweg
N = niet gedaan

Beperkingen:
==================================
- form validatie bestaat nog niet.
- datatypes worden nog niet ondersteund.
- het is niet mogelijk om bij het aanmaken van objecten een minimum aantal relaties van (een) ander(e) object(en) af 
te dwingen.
- het is nu nog niet mogelijk om je eigen command objecten toe te voegen (na de refactoring wel)

Issues:
==================================
-excepties worden nog niet goed afgevangen en als redelijke errors getoond.
-wanneer en object gerelateerde objecten heeft van hetzelfde type, raakt het pad in de war. wanneer
    je een editor opent voor zo'n object, wordt de entry van de 'parent' overschreven.
    tevens worden de link/unlink icoontjes niet goed getoond waneer parent en child van hetzelfde type zijn.
-related:edit: als je op een item in de lijst met gerelateerde items klikt, en er wordt voor dat
    item een mini-editor geopend, moet related:add dicht blijven.

Wensen:
===================================
- het zou mooi zijn als in de taglib de gui en de functionaliteit worden losgetrokken. dus tags voor alle 
interface elementen die weer door de functionele tags worden gebruikt. dit zou het nog makkelijker maken om
je eigen custom functionaliteit in de editors te integreren. dit is voor een klein gedeelte al gedaan.
-wanneer je via form:view een nieuw object wilt aanmaken, en er is geen newwizard attribuut gezet,
    dan is het beter om het formulier inline in de pagina te tonen, ipv. de related pagina. het
    zelfde geld voor edit. Het idee is dat wanneer de 'newwizard' en 'openwizard' attributen niet worden
    gezet het om kleine simpele objecten gaat, die:
    a) geen grote velden hebben
    b) geen gerelateerde objecten hebben.
    Het zou mooi zijn deze formulieren inline af te handelen.
-colom sortering in de zoek lijst.
-lazy loading versie van form:related
-in form:wizard kun je een wizardfile opgeven, maar dat is niet zo nuttig. in list:wizard is dat
    misschien nuttiger, aangezien list:add en list:search daar gebruik van kunnen maken. algemeen:
    het zou mooi zijn als je veel voorkomende attributen op de wizard kunt zetten (maar het moet
    niet gaan conflicteren).

Wijzigingen aan MMBase (1.8/1.9)
===================================
- commons.fileupload is geupdate naar 1.2
- hierdoor is een nieuwe dependencie ontstaan: commons.io
- de build ondersteunt nu ook tagfiles als standaard onderdeel van mmbase applicaties.
