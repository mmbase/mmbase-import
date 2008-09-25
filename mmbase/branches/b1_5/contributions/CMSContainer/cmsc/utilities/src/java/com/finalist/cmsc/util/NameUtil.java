package com.finalist.cmsc.util;

/**
 * NameHelper provides basic functionality to standardize the building of a full
 * name-String from the firstname and/or middlename and/or lastname of a person
 * in several nameformats.
 */
public final class NameUtil {
   /**
    * Use this value to indicate the nameformat to use is 'firstname middlename
    * lastname' (default).
    */
   public final static int NAMEFORMAT_FIRSTMIDDLELAST = 1;
   /**
    * Use this value to indicate the nameformat to use is 'lastname, firstname
    * middlename'.
    */
   public final static int NAMEFORMAT_LASTFIRSTMIDDLE = 2;


   private NameUtil() {
      // Do not create instances of this class.. just use the static methods!
   }


   /**
    * Returns the full name of a person using only the first- and last name. The
    * default nameformat will be used (NAMEFORMAT_FIRSTMIDDLELAST).
    *
    * @param firstName
    *           The first name of the person to retrieve the full name for
    * @param lastName
    *           The last name of the person to retrieve the full name for
    * @return A String containing the full name in the specified name format.
    * @see #NAMEFORMAT_FIRSTMIDDLELAST
    */
   public static String getFullName(String firstName, String lastName) {
      return getFullName(firstName, lastName, NAMEFORMAT_FIRSTMIDDLELAST);
   }


   /**
    * Returns the full name of a person using only the first- and last name.
    * It'll use the nameformat specified.
    *
    * @param firstName
    *           The first name of the person to retrieve the full name for
    * @param lastName
    *           The last name of the person to retrieve the full name for
    * @param nameFormat
    *           The name format to use for the full name
    * @return A String containing the full name in the specified name format.
    * @see #NAMEFORMAT_FIRSTMIDDLELAST
    * @see #NAMEFORMAT_LASTFIRSTMIDDLE
    */
   public static String getFullName(String firstName, String lastName, int nameFormat) {
      return getFullName(firstName, null, lastName, nameFormat);
   }


   /**
    * Returns the full name of a person using the first-, middle- and last name.
    * The default nameformat will be used (NAMEFORMAT_FIRSTMIDDLELAST).
    *
    * @param firstName
    *           The first name of the person to retrieve the full name for
    * @param middleName
    *           The middle name of the person to retreive the full name for
    * @param lastName
    *           The last name of the person to retrieve the full name for
    * @return A String containing the full name in the specified name format.
    * @see #NAMEFORMAT_FIRSTMIDDLELAST
    */
   public static String getFullName(String firstName, String middleName, String lastName) {
      return getFullName(firstName, middleName, lastName, NAMEFORMAT_FIRSTMIDDLELAST);
   }


   /**
    * Returns the full name of a person using the first-, middle- and last name.
    * It'll use the nameformat specified.
    *
    * @param firstName
    *           The first name of the person to retrieve the full name for
    * @param middleName
    *           The middle name of the person to retreive the full name for
    * @param lastName
    *           The last name of the person to retrieve the full name for
    * @param nameFormat
    *           The name format to use for the full name
    * @return A String containing the full name in the specified name format.
    * @see #NAMEFORMAT_FIRSTMIDDLELAST
    * @see #NAMEFORMAT_LASTFIRSTMIDDLE
    */
   public static String getFullName(String firstName, String middleName, String lastName, int nameFormat) {
      StringBuffer retValue = new StringBuffer();
      boolean firstNameAvailable = false;
      boolean middleNameAvailable = false;
      boolean lastNameAvailable = false;

      // If needed trim the name-parts and check to see if the Strings are
      // empty.
      if (firstName != null) {
         firstName = firstName.trim();
         if (firstName.length() > 0) {
            firstNameAvailable = true;
         }
      }
      if (middleName != null) {
         middleName = middleName.trim();
         if (middleName.length() > 0) {
            middleNameAvailable = true;
         }
      }
      if (lastName != null) {
         lastName = lastName.trim();
         if (lastName.length() > 0) {
            lastNameAvailable = true;
         }
      }

      // Build the correct full name, depending on the nameformat specified
      if (nameFormat == NAMEFORMAT_LASTFIRSTMIDDLE) {
         if (lastNameAvailable) {
            retValue.append(lastName);
            if (firstNameAvailable || middleNameAvailable) {
               retValue.append(", ");
            }
         }
         if (firstNameAvailable) {
            retValue.append(firstName);
         }
         if (middleNameAvailable) {
            if (lastNameAvailable) {
               retValue.append(' ');
            }
            retValue.append(middleName);
         }
      }
      else {
         // NAMEFORMAT_FIRSTMIDDLELAST
         if (firstNameAvailable) {
            retValue.append(firstName);
         }
         if (middleNameAvailable) {
            if (firstNameAvailable) {
               // There's already a firstname, so add a space
               retValue.append(' ');
            }
            retValue.append(middleName);
         }
         if (lastNameAvailable) {
            if (firstNameAvailable || middleNameAvailable) {
               // There's already something there, so we need a space
               retValue.append(' ');
            }
            retValue.append(lastName);
         }
      }
      return retValue.toString();
   }
}