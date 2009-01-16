horizontalMenuDelay = true; 
_menuCloseDelay=500;
_menuOpenDelay=350;
_scrollAmount=3;
_scrollDelay=20;
_followSpeed=5;
_followRate=40;
_subOffsetTop=10;
_subOffsetLeft=-5;
with(style1=new mm_style()){
   offcolor="#FFFFFF";
   offbgcolor="";
   oncolor="#FFFFFF";
   onbgcolor="";
   bordercolor="";
   borderstyle="";
   borderwidth=0;
   fontsize=11;
   fontstyle="normal";
   fontweight="bold";
   fontfamily="Arial";
   padding=2;
   pagebgcolor="";
   headercolor="";
   headerbgcolor="";
   align="center";
}
with(style2=new mm_style()){
   styleid=1;
   bordercolor="#96ADD9";
   borderstyle="solid";
   borderwidth=1;
   fontsize=12;
   fontstyle="normal";
   fontweight="normal";
   fontfamily="Arial";
   padding=1;
   pagebgcolor="#96ADD9";
   headercolor="#FFFFFF";
   headerbgcolor="#000099";
   offcolor="#050080";
   offbgcolor="#96ADD9";
   oncolor="#050080";
   onbgcolor="#FFFFFF";
}
         with(milonic=new menuname("mainmenu")){
         top=0;
         screenposition="center";
         style=style1;
         alwaysvisible=1;
         alignment="center";
         orientation="horizontal";
         aI("text=&nbsp;&nbsp;HOME&nbsp;&nbsp;;;url=/natmm-internet/home.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;STEUN ONS&nbsp;&nbsp;;showmenu=r516;url=/natmm-internet/steun_ons/steun_ons.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;NATUURGEBIEDEN&nbsp;&nbsp;;showmenu=r513;url=/natmm-internet/natuurgebieden/344_afwisselende_natuurgebieden.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;DE NATUUR IN&nbsp;&nbsp;;showmenu=r525;url=/natmm-internet/de_natuur_in/agenda.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;NIEUWS&nbsp;&nbsp;;showmenu=r495;url=/natmm-internet/nieuws/actualiteit.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;VERENIGING&nbsp;&nbsp;;showmenu=r510;url=/natmm-internet/vereniging/natuurmonumenten.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;WINKEL&nbsp;&nbsp;;showmenu=r1718283;url=/natmm-internet/winkel/welkom_in_de_winkel.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;VRAGEN?&nbsp;&nbsp;;showmenu=r519;url=/natmm-internet/vragen_/veel_gestelde_vragen.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;FUN&nbsp;&nbsp;;showmenu=r504;url=/natmm-internet/fun/e-cards.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;LINKS&nbsp;&nbsp;;showmenu=r507;url=/natmm-internet/links/natuur-_en_milieu.htm;separatorsize=1")
                  aI("text=&nbsp;&nbsp;WERKEN BIJ...&nbsp;&nbsp;;showmenu=r1138268;url=/natmm-internet/werken_bij.../vacatures.htm;separatorsize=1")
   }
         with(milonic=new menuname("r516")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Steun ons;;url=/natmm-internet/steun_ons/steun_ons.htm;separatorsize=1")
                  aI("text=Acties en voordeel voor leden;;url=/natmm-internet/steun_ons/acties_en_voordeel_voor_leden.htm;separatorsize=1")
                  aI("text=Bedrijven;;url=/natmm-internet/steun_ons/bedrijven.htm;separatorsize=1")
                  aI("text=Lid worden;;url=/natmm-internet/steun_ons/lid_worden.htm;separatorsize=1")
                  aI("text=Lid werft lid;;url=/natmm-internet/steun_ons/lid_werft_lid.htm;separatorsize=1")
                  aI("text=Cadeaulidmaatschap;;url=/natmm-internet/steun_ons/cadeaulidmaatschap.htm;separatorsize=1")
                  aI("text=Schenken en nalaten;;url=/natmm-internet/steun_ons/schenken_en_nalaten.htm;separatorsize=1")
                  aI("text=Postcode Loterij;;url=/natmm-internet/steun_ons/postcode_loterij.htm;separatorsize=1")
                  aI("text=Sponsors;;url=/natmm-internet/steun_ons/sponsors.htm;separatorsize=1")
   }
         with(milonic=new menuname("r513")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=344 afwisselende natuurgebieden;;url=/natmm-internet/natuurgebieden/344_afwisselende_natuurgebieden.htm;separatorsize=1")
                  aI("text=Drenthe;;url=/natmm-internet/natuurgebieden/drenthe.htm;separatorsize=1")
                  aI("text=Flevoland;;url=/natmm-internet/natuurgebieden/flevoland.htm;separatorsize=1")
                  aI("text=Friesland;;url=/natmm-internet/natuurgebieden/friesland.htm;separatorsize=1")
                  aI("text=Gelderland;;url=/natmm-internet/natuurgebieden/gelderland.htm;separatorsize=1")
                  aI("text=Groningen;;url=/natmm-internet/natuurgebieden/groningen.htm;separatorsize=1")
                  aI("text=Limburg;;url=/natmm-internet/natuurgebieden/limburg.htm;separatorsize=1")
                  aI("text=Noord-Brabant;;url=/natmm-internet/natuurgebieden/noord-brabant.htm;separatorsize=1")
                  aI("text=Noord-Holland;;url=/natmm-internet/natuurgebieden/noord-holland.htm;separatorsize=1")
                  aI("text=Overijssel;;url=/natmm-internet/natuurgebieden/overijssel.htm;separatorsize=1")
                  aI("text=Utrecht;;url=/natmm-internet/natuurgebieden/utrecht.htm;separatorsize=1")
                  aI("text=Zeeland;;url=/natmm-internet/natuurgebieden/zeeland.htm;separatorsize=1")
                  aI("text=Zuid-Holland;;url=/natmm-internet/natuurgebieden/zuid-holland.htm;separatorsize=1")
   }
         with(milonic=new menuname("r525")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Agenda;;url=/natmm-internet/de_natuur_in/agenda.htm;separatorsize=1")
                  aI("text=Routes;;url=/natmm-internet/de_natuur_in/routes.htm;separatorsize=1")
                  aI("text=Bezoekerscentra;showmenu=r501;url=/natmm-internet/de_natuur_in/bezoekerscentra/bezoekerscentra_.htm;separatorsize=1")
                  aI("text=Wildzoekers;;url=/natmm-internet/de_natuur_in/wildzoekers.htm;separatorsize=1")
                  aI("text=Tips;showmenu=r903334;url=/natmm-internet/de_natuur_in/tips/teken.htm;separatorsize=1")
   }
         with(milonic=new menuname("r501")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Bezoekerscentra:;;url=/natmm-internet/de_natuur_in/bezoekerscentra/bezoekerscentra_.htm;separatorsize=1")
                  aI("text=Brunssummerheide;;url=/natmm-internet/de_natuur_in/bezoekerscentra/brunssummerheide.htm;separatorsize=1")
                  aI("text=De Wieden;;url=/natmm-internet/de_natuur_in/bezoekerscentra/de_wieden.htm;separatorsize=1")
                  aI("text=Dwingelderveld;;url=/natmm-internet/de_natuur_in/bezoekerscentra/dwingelderveld.htm;separatorsize=1")
                  aI("text=Oisterwijk;;url=/natmm-internet/de_natuur_in/bezoekerscentra/oisterwijk.htm;separatorsize=1")
                  aI("text=Veluwezoom;;url=/natmm-internet/de_natuur_in/bezoekerscentra/veluwezoom.htm;separatorsize=1")
                  aI("text='s-Graveland;;url=/natmm-internet/de_natuur_in/bezoekerscentra/s-graveland.htm;separatorsize=1")
   }
         with(milonic=new menuname("r903334")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Teken;;url=/natmm-internet/de_natuur_in/tips/teken.htm;separatorsize=1")
   }
         with(milonic=new menuname("r495")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Actualiteit;;url=/natmm-internet/nieuws/actualiteit.htm;separatorsize=1")
                  aI("text=Persberichten;;url=/natmm-internet/nieuws/persberichten.htm;separatorsize=1")
                  aI("text=Dossiers;;url=/natmm-internet/nieuws/dossiers.htm;separatorsize=1")
                  aI("text=Natuurbrief;;url=/natmm-internet/nieuws/natuurbrief.htm;separatorsize=1")
                  aI("text=Natuurbehoud;;url=/natmm-internet/nieuws/natuurbehoud.htm;separatorsize=1")
                  aI("text=Van Nature;;url=/natmm-internet/nieuws/van_nature.htm;separatorsize=1")
   }
         with(milonic=new menuname("r510")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Natuurmonumenten;;url=/natmm-internet/vereniging/natuurmonumenten.htm;separatorsize=1")
                  aI("text=Organisatie;;url=/natmm-internet/vereniging/organisatie.htm;separatorsize=1")
                  aI("text=Sponsors;;url=/natmm-internet/vereniging/sponsors.htm;separatorsize=1")
                  aI("text=Jaarverslag;;url=/natmm-internet/vereniging/jaarverslag.htm;separatorsize=1")
                  aI("text=Contact;;url=/natmm-internet/vereniging/contact.htm;separatorsize=1")
                  aI("text=Districtscommissies;showmenu=r87900;url=/natmm-internet/vereniging/districtscommissies/districtscommissies.htm;separatorsize=1")
   }
         with(milonic=new menuname("r87900")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Districtscommissies;;url=/natmm-internet/vereniging/districtscommissies/districtscommissies.htm;separatorsize=1")
                  aI("text=Flevoland;;url=/natmm-internet/vereniging/districtscommissies/flevoland.htm;separatorsize=1")
                  aI("text=Zeeland;;url=/natmm-internet/vereniging/districtscommissies/zeeland.htm;separatorsize=1")
                  aI("text=Zuid-Holland;;url=/natmm-internet/vereniging/districtscommissies/zuid-holland.htm;separatorsize=1")
                  aI("text=Drenthe;;url=/natmm-internet/vereniging/districtscommissies/drenthe.htm;separatorsize=1")
                  aI("text=Friesland;;url=/natmm-internet/vereniging/districtscommissies/friesland.htm;separatorsize=1")
                  aI("text=Gelderland;;url=/natmm-internet/vereniging/districtscommissies/gelderland.htm;separatorsize=1")
                  aI("text=Groningen;;url=/natmm-internet/vereniging/districtscommissies/groningen.htm;separatorsize=1")
                  aI("text=Limburg;;url=/natmm-internet/vereniging/districtscommissies/limburg.htm;separatorsize=1")
                  aI("text=Noord-Brabant;;url=/natmm-internet/vereniging/districtscommissies/noord-brabant.htm;separatorsize=1")
                  aI("text=Noord-Holland;;url=/natmm-internet/vereniging/districtscommissies/noord-holland.htm;separatorsize=1")
                  aI("text=Overijssel;;url=/natmm-internet/vereniging/districtscommissies/overijssel.htm;separatorsize=1")
                  aI("text=Utrecht;;url=/natmm-internet/vereniging/districtscommissies/utrecht.htm;separatorsize=1")
                  aI("text=Amsterdam;;url=/natmm-internet/vereniging/districtscommissies/amsterdam.htm;separatorsize=1")
   }
         with(milonic=new menuname("r1718283")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Welkom in de Winkel;;url=/natmm-internet/winkel/welkom_in_de_winkel.htm;separatorsize=1")
                  aI("text=Jeugdproducten;;url=/natmm-internet/winkel/jeugdproducten.htm;separatorsize=1")
                  aI("text=Boeken;;url=/natmm-internet/winkel/boeken.htm;separatorsize=1")
                  aI("text=Agenda en kalender;;url=/natmm-internet/winkel/agenda_en_kalender.htm;separatorsize=1")
                  aI("text=Natuurgidsen- en waaiers;;url=/natmm-internet/winkel/natuurgidsen-_en_waaiers.htm;separatorsize=1")
                  aI("text=Wandelen en fietsen;;url=/natmm-internet/winkel/wandelen_en_fietsen.htm;separatorsize=1")
                  aI("text=Verzamelbanden Natuurbehoud;;url=/natmm-internet/winkel/verzamelbanden_natuurbehoud.htm;separatorsize=1")
                  aI("text=Verkoopadressen en Bezoekerscentra;;url=/natmm-internet/winkel/verkoopadressen_en_bezoekerscentra.htm;separatorsize=1")
                  aI("text=Contact;;url=/natmm-internet/winkel/contact.htm;separatorsize=1")
                  aI("text=Leveringsvoorwaarden;;url=/natmm-internet/winkel/leveringsvoorwaarden.htm;separatorsize=1")
                  aI("text=Disclaimer;;url=/natmm-internet/winkel/disclaimer.htm;separatorsize=1")
   }
         with(milonic=new menuname("r519")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Veel Gestelde Vragen;;url=/natmm-internet/vragen_/veel_gestelde_vragen.htm;separatorsize=1")
                  aI("text=Wijzigingen doorgeven;;url=/natmm-internet/vragen_/wijzigingen_doorgeven.htm;separatorsize=1")
                  aI("text=Bewijs van lidmaatschap;;url=/natmm-internet/vragen_/bewijs_van_lidmaatschap.htm;separatorsize=1")
                  aI("text=Vragenformulier;;url=/natmm-internet/vragen_/vragenformulier.htm;separatorsize=1")
                  aI("text=Cadeaulidmaatschap;;url=/natmm-internet/vragen_/cadeaulidmaatschap.htm;separatorsize=1")
   }
         with(milonic=new menuname("r504")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=E-cards;;url=/natmm-internet/fun/e-cards.htm;separatorsize=1")
                  aI("text=Wallpaper;;url=/natmm-internet/fun/wallpaper.htm;separatorsize=1")
                  aI("text=WILDzoekers;;url=/natmm-internet/fun/wildzoekers.htm;separatorsize=1")
                  aI("text=Podwalk;;url=/natmm-internet/fun/podwalk.htm;separatorsize=1")
                  aI("text=Natuurrap Goldy;;url=/natmm-internet/fun/natuurrap_goldy.htm;separatorsize=1")
                  aI("text=Second Life;;url=/natmm-internet/fun/second_life.htm;separatorsize=1")
                  aI("text=Goede Doelen Kaartje;;url=/natmm-internet/fun/goede_doelen_kaartje.htm;separatorsize=1")
                  aI("text=Weblog met cam;;url=/natmm-internet/fun/weblog_met_cam.htm;separatorsize=1")
                  aI("text=Webcam Jurn;;url=/natmm-internet/fun/webcam_jurn.htm;separatorsize=1")
   }
         with(milonic=new menuname("r507")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Natuur- en milieu;;url=/natmm-internet/links/natuur-_en_milieu.htm;separatorsize=1")
                  aI("text=Subsidiegevers;;url=/natmm-internet/links/subsidiegevers.htm;separatorsize=1")
                  aI("text=Sponsors;;url=/natmm-internet/links/sponsors.htm;separatorsize=1")
                  aI("text=Startpagina's;;url=/natmm-internet/links/startpaginas.htm;separatorsize=1")
                  aI("text=Wandel- en fietsroutes;;url=/natmm-internet/links/wandel-_en_fietsroutes.htm;separatorsize=1")
                  aI("text=Overige relaties;;url=/natmm-internet/links/overige_relaties.htm;separatorsize=1")
   }
         with(milonic=new menuname("r1138268")){
         itemwidth=150;
         style=style2;
         alignment="center";
         aI("text=Vacatures;;url=/natmm-internet/werken_bij.../vacatures.htm;separatorsize=1")
                  aI("text=Vrijwilligers gevraagd!;;url=/natmm-internet/werken_bij.../vrijwilligers_gevraagd_.htm;separatorsize=1")
                  aI("text=Vrijwilligers;;url=/natmm-internet/werken_bij.../vrijwilligers.htm;separatorsize=1")
   }
drawMenus();