package com.finalist.cmsc.services.community.domain;

import java.util.List;
import com.finalist.cmsc.services.community.person.Person;
import com.finalist.cmsc.services.community.preferences.Preference;
import com.finalist.cmsc.services.community.security.Authentication;

public class PersonExportImportVO extends Person {

   private Authentication authentication;

   private List < Preference > preferences;
   private Long authorityId = new Long(0);

   public Authentication getAuthentication() {
      return authentication;
   }

   public void setAuthentication(Authentication authentication) {
      this.authentication = authentication;
   }

   public List < Preference > getPreferences() {
      return preferences;
   }

   public void setPreferences(List < Preference > preferences) {
      this.preferences = preferences;
   }

   public Long getAuthenticationId() {
      return authentication.getId();
   }
   public void clean(){
      this.authentication = null;
      this.preferences.clear();
   }

   public Long getAuthorityId() {
      return authorityId;
   }

   public void setAuthorityId(Long authorityId) {
      this.authorityId = authorityId;
   }
}
