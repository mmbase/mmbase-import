todo:

Y   zorg dat de vpro wizards compileren in de mmbase build.
N   zorg dat de hele applicatie goed installeerd met de build.
N   pas de paden aan van de edit jsp's en de tags.
N   refactor de java code
N   pas de documentatie aan
N   pas mmbase buildbase.properties aan: install.tagfiles
N   zorg dat de fck richtext editor wordt gedownlaod, en niet in de cvs zit.

wat is er qua paden veranderd:
1 het mapje /edit is nu /mmbase/vpro-wizards
2 de tags staan nu van tags/edit/ in /tags/vpro-wizards
3 de classes gaan verhuizen van nl.vpro.redactie naar org.mmbase.applications.vpro-wizards



vpro-wizards

De vpro-wizards zijn een beheer omgeving bouw doos, a la de editwizards, met de volgende verschillen:
-de editors zijn gewoon jsp. Een serie taglibs levert standaard bouwstenen.
-de backend is in java, en maakt gebruik van spring. Middels spring data binding
worden geposte formulieren omgezet in actie beans die worden gerund.

frontend en backend zijn totaal ontkoppeld. Alle standaard acties die je nodig hebt zijn aanwezig.
Deze zijn:

- node aanmaken
- node bewerken
- node verwijderen
- relatie maken
- relatie sorteren.

het is heel makkelijk om je eigen specialistische acties toe te voegen.

Tevens is er een kleine mmbase applicatie waarmee je in de start pagina van de editor een
sort desktop met links en notieties voor de redactie.

Er komt een voorbeeld beheer omgeving die werkt met een van de standaard demo applicaties.

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
