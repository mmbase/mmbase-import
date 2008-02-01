package com.finalist.cmsc.mmbase;

class TreePathCacheKey {

   private String path = "";


   TreePathCacheKey(String path) {
      if (path == null || path.trim().length() == 0) {
         throw new IllegalArgumentException("TreePathCacheKey.path can't be empty.");
      }
      this.path = path;
   }


   public String getPath() {
      return path;
   }


   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof TreePathCacheKey) {
         return path.toLowerCase().equals(((TreePathCacheKey) obj).path.toLowerCase());
      }
      if (obj instanceof String) {
         if (obj != null) {
            return path.toLowerCase().equals(((String) obj).toLowerCase());
         }
      }
      return false;
   }


   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return path.toLowerCase().hashCode();
   }


   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return path;
   }

}
