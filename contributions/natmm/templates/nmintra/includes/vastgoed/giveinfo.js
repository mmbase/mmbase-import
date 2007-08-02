function giveInfo(infoIndex)
{
var infoMessages = new Array();

infoMessages[0] = "Kies hier de gebieden waarvan je kaarten wilt toevoegen aan je bestelling. Bij de keuzemogelijkheid &#39;natuurgebieden&#39; kunnen meerdere natuurgebieden binnen &eacute;&eacute;n eenheid tegelijkertijd (gebruik de shift toets) worden geselecteerd en dus toegevoegd aan je bestelling. Bij de keuzemogelijkheden &#39;Eenheid/Regio/Provincie&#39;, &#39;Nederland&#39; of &#39;coördinaten&#39; kun je slechts &eacute;&eacute;n gebied per keer toevoegen aan je bestelling.";

infoMessages[1] = "Er kunnen meerdere kaartsoorten tegelijkertijd worden geselecteerd en dus worden toegevoegd aan je bestelling.\n\nLet op, niet alle kaartsoorten zijn bij elke schaal en/of gebied leverbaar. Bij een niet leverbare keuze krijg je een bericht van de GIS-afdeling. Geef speciale wensen op bij opmerkingen.";

infoMessages[2] = "Kies hier voor een schaal of een papierformaat. Maak hier ook de keuze of je de kaarten gerold of gevouwen wilt ontvangen en geef ook het aantal exemplaren op.\n\nLet op, niet elke schaal of papierformaat is bij elke kaartsoort en/of gebied leverbaar. Bij een niet-leverbare keuze krijg je een bericht van de GIS-afdeling. Geef speciale wensen op bij opmerkingen.";

infoMessages[3] = "Vul hier eventueel je speciale wensen in en/of eventuele andere opmerkingen.";

alert(infoMessages[infoIndex]);
}