package nl.leocms.evenementen.stats;

import org.mmbase.util.logging.*;

public class AfdelingBean {

   private static final Logger log = Logging.getLoggerInstance(AfdelingBean.class);
   
   private String afdelingName;
   private String regioName;
   private int[] individueleBoekingenTotal;
   private int[] groepsBoekingenTotal;
   
	public String getAfdelingName() {
		return afdelingName;
	}
	public void setAfdelingName(String afdelingName) {
		this.afdelingName = afdelingName;
	}
	public int[] getGroepsBoekingenTotal() {
		return groepsBoekingenTotal;
	}
	public void setGroepsBoekingenTotal(int[] groepsBoekingenTotal) {
		this.groepsBoekingenTotal = groepsBoekingenTotal;
	}
	public int[] getIndividueleBoekingenTotal() {
		return individueleBoekingenTotal;
	}
	public void setIndividueleBoekingenTotal(int[] individueleBoekingenTotal) {
		this.individueleBoekingenTotal = individueleBoekingenTotal;
	}
	public String getRegioName() {
		return regioName;
	}
	public void setRegioName(String regioName) {
		this.regioName = regioName;
	}

}
