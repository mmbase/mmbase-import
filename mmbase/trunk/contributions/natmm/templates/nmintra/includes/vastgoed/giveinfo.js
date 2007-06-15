function giveInfo(infoIndex)
{
var infoMessages = new Array();

infoMessages[0] = "Kies gebied(en) waarvan je kaart(en) wilt toevoegen aan je bestelling. Bij de keuzemogelijkheid 'natuurgebieden' kunnen meerdere natuurgebieden binnen één eenheid tegelijkertijd (gebruik de shift toets) worden geselecteerd en dus toegevoegd aan je bestelling. Bij de keuzemogelijkheden 'Eenheid/Regio/Provincie', 'Nederland' of 'coördinaten' kun je slechts één gebied per keer toevoegen aan je bestelling.";

infoMessages[1] = "Er kunnen meerdere kaartsoorten tegelijkertijd worden geselecteerd en dus worden toegevoegd aan je bestelling. Let op, niet alle kaartsoorten zijn bij elke schaal en/of gebied leverbaar. Bij een niet leverbare keuze krijg je een bericht. Geef speciale wensen op bij opmerkingen.";

infoMessages[2] = "Kies hier voor een schaal of een papierformaat. Maak hier ook de keuze of je de kaarten gerold of gevouwen wilt ontvangen en geef ook het aantal exemplaren op. Let op, niet elke schaal of papierformaat is bij elke kaartsoort en/of gebied leverbaar. Bij een niet leverbare keuze krijg je een bericht. Geef speciale wensen op bij opmerkingen.";

infoMessages[3] = "Vul hier eventueel je speciale wensen in en/of eventuele andere opmerkingen.";

alert(infoMessages[infoIndex]);
}