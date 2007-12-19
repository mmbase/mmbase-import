package com.finalist.cmsc.services.community;

import java.security.Principal;
import java.util.Set;
import java.util.TreeSet;

public class JAASAuthorityGranter implements org.springframework.security.providers.jaas.AuthorityGranter{

   public Set grant(Principal principal){
       System.out.println("JAASAUTHORITYGRANTER");
       Set roles = new TreeSet();
       roles.add(principal.getName());
       return roles;
   }
}