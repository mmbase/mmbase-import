package com.finalist.cmsc.services.community.domain;

import java.util.List;

public class CommunityExport {
   private List < PersonExportImportVO > users;

   public List < PersonExportImportVO > getUsers() {
      return users;
   }

   public void setUsers(List < PersonExportImportVO > users) {
      this.users = users;
   }

   public CommunityExport(List < PersonExportImportVO > users) {

      this.users = users;
   }

}
