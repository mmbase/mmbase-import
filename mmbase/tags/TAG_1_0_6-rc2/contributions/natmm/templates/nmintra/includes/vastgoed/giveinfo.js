function giveInfo(infoIndex)
{
var infoMessages = new Array();

infoMessages[0] = "Kies één gebied waarvan je kaart wilt toevoegen aan je bestelling. Je kunt slechts één gebied per keer toevoegen aan je bestelling.";

infoMessages[1] = "Let op, niet alle kaartsoorten zijn bij elke schaal en/of gebied leverbaar. Bij een niet leverbare keuze krijg je een bericht van de GIS-afdeling. Geef speciale wensen op bij opmerkingen.";

infoMessages[2] = "Kies hier voor een schaal of een papierformaat. Maak hier ook de keuze of je de kaarten gerold of gevouwen wilt ontvangen en geef ook het aantal exemplaren op. Let op, niet elke schaal of papierformaat is bij elke kaartsoort en/of gebied leverbaar. Bij een niet leverbare krijg je een bericht van de GIS-afdeling. Geef speciale wensen op bij opmerkingen.";

infoMessages[3] = "Vul hier eventueel je speciale wensen in en/of eventuele andere opmerkingen.";

alert(infoMessages[infoIndex]);
}