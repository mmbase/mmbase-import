package nl.leocms.evenementen.stats;

import org.mmbase.util.logging.*;

public class AfdelingBean {

   private static final Logger log = Logging.getLoggerInstance(AfdelingBean.class);
   
   private String afdelingName;
   private String regioName;
   private int[] individueleBoekingenTotal;
   private int[] groepsBoekingenTotal;
   // to support percentages
   private int individueleBoekingenLedenTotal;
   private int individueleBoekingenDeelnemersTotal;
   private int groepsBoekingenLedenTotal;
   private int groepsBoekingenDeelnemersTotal;
   
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
   public int getGroepsBoekingenDeelnemersTotal() {
      return groepsBoekingenDeelnemersTotal;
   }
   public void setGroepsBoekingenDeelnemersTotal(int groepsBoekingenDeelnemersTotal) {
      this.groepsBoekingenDeelnemersTotal = groepsBoekingenDeelnemersTotal;
   }
   public int getGroepsBoekingenLedenTotal() {
      return groepsBoekingenLedenTotal;
   }
   public void setGroepsBoekingenLedenTotal(int groepsBoekingenLedenTotal) {
      this.groepsBoekingenLedenTotal = groepsBoekingenLedenTotal;
   }
   public int getIndividueleBoekingenDeelnemersTotal() {
      return individueleBoekingenDeelnemersTotal;
   }
   public void setIndividueleBoekingenDeelnemersTotal(int individueleBoekingenDeelnemersTotal) {
      this.individueleBoekingenDeelnemersTotal = individueleBoekingenDeelnemersTotal;
   }
   public int getIndividueleBoekingenLedenTotal() {
      return individueleBoekingenLedenTotal;
   }
   public void setIndividueleBoekingenLedenTotal(int individueleBoekingenLedenTotal) {
      this.individueleBoekingenLedenTotal = individueleBoekingenLedenTotal;
   }

}
