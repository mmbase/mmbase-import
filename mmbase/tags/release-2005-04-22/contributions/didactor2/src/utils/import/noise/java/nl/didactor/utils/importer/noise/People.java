package nl.didactor.utils.importer.noise;

public class People
{
   private String sInitials;
   private String sFirstname;
   private String sLastname;
   private String sEmail;
   private String sPassword;
   private String sUsername;
   private String sAddress;
   private String sZipcode;
   private String sCity;
   private String sTelephone;
   private String sDescription;
   private String sGroup;
   private String sClass;

   public People()
   {
      sInitials = "";
      sFirstname = "";
      sLastname = "";
      sEmail = "";
      sPassword = "";
      sUsername = "";
      sAddress = "";
      sZipcode = "";
      sCity = "";
      sTelephone = "";
      sDescription = "";
      sGroup = "";
      sClass = "";
   }


   public People(String sInitials, String sFirstname, String sLastname, String sEmail, String sPassword, String sUsername, String sAddress, String sZipcode, String sCity, String sTelephone, String sDescription, String sGroup, String sClass)
   {
      this.sInitials = sInitials;
      this.sFirstname = sFirstname;
      this.sLastname = sLastname;
      this.sEmail = sEmail;
      this.sPassword = sPassword;
      this.sUsername = sUsername;
      this.sAddress = sAddress;
      this.sZipcode = sZipcode;
      this.sCity = sCity;
      this.sTelephone = sTelephone;
      this.sDescription = sDescription;
      this.sGroup = sGroup;
      this.sClass = sClass;
   }


   public void setInitials(String sInitials)
   {
      this.sInitials = sInitials;
   }

   public void setFirstname(String sFirstname)
   {
      this.sFirstname = sFirstname;
   }

   public void setLastname(String sLastname)
   {
      this.sLastname = sLastname;
   }

   public void setEmail(String sEmail)
   {
      this.sEmail = sEmail;
   }

   public void setPassword(String sPasssword)
   {
      this.sPassword = sPassword;
   }

   public void setUsername(String sUsername)
   {
      this.sUsername = sUsername;
   }

   public void setAddress(String sAddress)
   {
      this.sAddress = sAddress;
   }

   public void setZipcode(String sZipcode)
   {
      this.sZipcode = sZipcode;
   }

   public void setCity(String sCity)
   {
      this.sCity = sCity;
   }
   public void setTelephone(String sTelephone)
   {
      this.sTelephone = sTelephone;
   }

   public void setDescription(String sDescription)
   {
      this.sDescription = sDescription;
   }

   public void setGroup(String sGroup)
   {
      this.sGroup = sGroup;
   }

   public void setClasses(String sClass)
   {
      this.sClass = sClass;
   }



   public String getInitials()
   {
      return this.sInitials;
   }

   public String getFirstname()
   {
      return this.sFirstname;
   }

   public String getLastname()
   {
      return this.sLastname;
   }

   public String getEmail()
   {
      return this.sEmail;
   }

   public String getPassword()
   {
      return this.sPassword;
   }

   public String getUsername()
   {
      return this.sUsername;
   }

   public String getAddress()
   {
      return this.sAddress;
   }

   public String getZipcode()
   {
      return this.sZipcode;
   }

   public String getCity()
   {
      return this.sCity;
   }

   public String getTelephone()
   {
      return this.sTelephone;
   }

   public String getDescription()
   {
      return this.sDescription;
   }

   public String getGroup()
   {
      return this.sGroup;
   }

   public String getClasses()
   {
      return this.sClass;
   }

}