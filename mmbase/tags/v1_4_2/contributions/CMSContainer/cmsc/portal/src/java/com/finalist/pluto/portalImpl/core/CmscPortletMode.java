package com.finalist.pluto.portalImpl.core;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletMode;

/**
 * CMSC supported Portlet modes
 * 
 * @author Wouter Heijke
 */
public class CmscPortletMode extends PortletMode {

   public final static PortletMode ABOUT = new PortletMode("about");

   public final static PortletMode CONFIG = new PortletMode("config");

   public final static PortletMode DELETE = new PortletMode("delete");

   public final static PortletMode EDIT_DEFAULTS = new PortletMode("edit_defaults");

   public final static PortletMode PREVIEW = new PortletMode("preview");

   public final static PortletMode PRINT = new PortletMode("print");

   public static List<PortletMode> adminModes = new ArrayList<PortletMode>();
   public static List<PortletMode> editModes = new ArrayList<PortletMode>();
   public static List<PortletMode> editOnlyModes = new ArrayList<PortletMode>();
   public static List<PortletMode> viewModes = new ArrayList<PortletMode>();
   static {
      adminModes.add(DELETE);
      adminModes.add(EDIT_DEFAULTS);
      adminModes.add(CONFIG);

      editModes.add(EDIT);
      editModes.add(PREVIEW);

      editOnlyModes.add(PREVIEW);

      viewModes.add(VIEW);
      viewModes.add(ABOUT);
      viewModes.add(PRINT);
      viewModes.add(HELP);
   }


   public CmscPortletMode(String name) {
      super(name);
   }


   public static boolean isAdminMode(PortletMode mode) {
      return adminModes.contains(mode);
   }


   public static boolean isEditMode(PortletMode mode) {
      return editModes.contains(mode);
   }


   public static boolean isViewMode(PortletMode mode) {
      return viewModes.contains(mode);
   }


   public static boolean isEditOnlyMode(PortletMode mode) {
      return editOnlyModes.contains(mode);
   }

}