package nl.leocms.vastgoed;

import org.apache.struts.action.ActionForm;

/**
 * @author
 * @version $Id: KaartenForm.java,v 1.1 2007-05-29 11:52:28 ieozden Exp $
 *
 * @struts:form name="KaartenForm"
 */

public class KaartenForm extends ActionForm{
	private String aantal;
	private String rad_Schaal;
	private String schaal;
	private String formaat;
	private String rad_Gevouwen;
	private String[] sel_Kaart;
	private String rad_Gebied;
	private String sel_Beheereenheden;
	private String[] sel_NatGeb;
	private String sel_gebieden;
	private String[] sel_Areaal;
	private String linksX;
	private String linksY;
	private String rechtsX;
	private String rechtsY;
	
	
	public KaartenForm() {
		aantal="1";
		rad_Schaal = "schaal";
		schaal = "1:5000";
		formaat = "A4";
		rad_Gevouwen = "gevouwen";
		rad_Gebied = "natuurgebied";
	}
	
	
	

	// Shopping Cart Getters
	
	public String getAantal() {
		return this.aantal;
	}
	
	public String getSchaalOfFormaat() {
		if ("schaal".equals(rad_Schaal)) {
			  return this.schaal;
		  } else if ("formaat".equals(rad_Schaal)) {
			  return this.formaat;
		  } else {return "unknown format";}
	}
	
	public String getGevouwenOfOpgerold() {
		return this.rad_Gevouwen;
	}
	
	public String getKaartSoort() {
		String kaartSoort = "";
		for(int i=0; ((sel_Kaart != null) && (i<sel_Kaart.length)); i++) {
			kaartSoort += (i!=0) ? ", " + sel_Kaart[i] :  sel_Kaart[i];
		}
		return kaartSoort;
	}
	
	public String getKaartType() {
		return this.rad_Gebied;
	}

	// getters and setters
	
	public String getFormaat() {
		return formaat;
	}

	public void setFormaat(String formaat) {
		this.formaat = formaat;
	}

	public String getRad_Gevouwen() {
		return rad_Gevouwen;
	}

	public void setRad_Gevouwen(String rad_Gevouwen) {
		this.rad_Gevouwen = rad_Gevouwen;
	}

	public String getRad_Schaal() {
		return rad_Schaal;
	}

	public void setRad_Schaal(String rad_Schaal) {
		this.rad_Schaal = rad_Schaal;
	}

	public String getSchaal() {
		return schaal;
	}

	public void setSchaal(String schaal) {
		this.schaal = schaal;
	}

	public String[] getSel_Kaart() {
		return sel_Kaart;
	}

	public void setSel_Kaart(String[] sel_Kaart) {
		this.sel_Kaart = sel_Kaart;
	}

	public void setAantal(String aantal) {
		this.aantal = aantal;
	}
	
	public String getLinksX() {
		return linksX;
	}

	public void setLinksX(String linksX) {
		this.linksX = linksX;
	}

	public String getLinksY() {
		return linksY;
	}

	public void setLinksY(String linksY) {
		this.linksY = linksY;
	}

	public String getRad_Gebied() {
		return rad_Gebied;
	}

	public void setRad_Gebied(String rad_Gebied) {
		this.rad_Gebied = rad_Gebied;
	}

	public String getRechtsX() {
		return rechtsX;
	}

	public void setRechtsX(String rechtsX) {
		this.rechtsX = rechtsX;
	}

	public String getRechtsY() {
		return rechtsY;
	}

	public void setRechtsY(String rechtsY) {
		this.rechtsY = rechtsY;
	}

	public String[] getSel_Areaal() {
		return sel_Areaal;
	}

	public void setSel_Areaal(String[] sel_Areaal) {
		this.sel_Areaal = sel_Areaal;
	}

	public String getSel_Beheereenheden() {
		return sel_Beheereenheden;
	}

	public void setSel_Beheereenheden(String sel_Beheereenheden) {
		this.sel_Beheereenheden = sel_Beheereenheden;
	}

	public String getSel_gebieden() {
		return sel_gebieden;
	}

	public void setSel_gebieden(String sel_gebieden) {
		this.sel_gebieden = sel_gebieden;
	}

	public String[] getSel_NatGeb() {
		return sel_NatGeb;
	}

	public void setSel_NatGeb(String[] sel_NatGeb) {
		this.sel_NatGeb = sel_NatGeb;
	}
	
	// copying values from another KaartenForm object
	public void copyValuesFrom(KaartenForm copyForm) {
		this.aantal = copyForm.getAantal();
		this.rad_Schaal = copyForm.getRad_Schaal();
		this.schaal = copyForm.getSchaal();
		this.formaat = copyForm.getFormaat();
		this.rad_Gevouwen = copyForm.getRad_Gevouwen();
		this.sel_Kaart = copyForm.getSel_Kaart();
		this.rad_Gebied = copyForm.getRad_Gebied();
		this.sel_Beheereenheden = copyForm.getSel_Beheereenheden();
		this.sel_NatGeb = copyForm.getSel_NatGeb();
		this.sel_gebieden = copyForm.getSel_gebieden();
		this.sel_Areaal = copyForm.getSel_Areaal();
		this.linksX = copyForm.getLinksX();
		this.linksY = copyForm.getLinksY();
		this.rechtsX = copyForm.getRechtsX();
		this.rechtsY = copyForm.getRechtsY();
	}
}
