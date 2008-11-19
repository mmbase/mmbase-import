package com.finalist.cmsc.taglib;

import com.finalist.cmsc.beans.om.Page;
import com.finalist.cmsc.services.sitemanagement.SiteManagement;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class InsertPageImageTag extends CmscTag {

   private static Log log = LogFactory.getLog(InsertPageImageTag.class);

   // tag parameters
   private String var;
   private String name;
   private String inherit;
   private String random;


   public void setName(String name) {
      this.name = name;
   }


   public void setVar(String var) {
      this.var = var;
   }


   public void setInherit(String inherit) {
      this.inherit = inherit;
   }


   public void setRandom(String random) {
      this.random = random;
   }


   @Override
   public void doTag() {
      PageContext ctx = (PageContext) getJspContext();
      HttpServletRequest request = (HttpServletRequest) ctx.getRequest();

      Integer image = null;
      if (StringUtils.isNotEmpty(name)) {
            List<Integer> images = SiteManagement.getPageImagesForPage(name, getPath());
            if (images != null && !images.isEmpty()) {
                image = images.get(0);
            }
      }
      else {
         image = getImagesFromBatch();
      }

      // handle result
      if (image != null) {
         if (var != null) {
            request.setAttribute(var, image);
         }
         else {
            HttpServletResponse response = (HttpServletResponse) ctx.getResponse();
            try {
               response.getWriter().print(image);
            }
            catch (IOException e) {
               log.error("Unable to write image to the output: " + e);
            }
         }
      }
      else {
         log.debug("Image with name " + name + " not found for path: " + getPath());
      }
   }

    private Integer getImagesFromBatch() {
      boolean directly = StringUtils.equals(this.inherit, "directly");
      boolean random = StringUtils.equals(this.random, "true");
      boolean override = StringUtils.equals(this.inherit, "override");

      List<Integer> images = getCurrentPageImages();
      if ((override && images.size() == 0) || directly) { // inherit from parent.
         images.addAll(getImagesOfParent());
      }

      if (images.size() > 0) {
         if (random) {
            return images.get(random(images.size()));
         }
         else {
            return images.get(0);
         }
      }
      return null;
   }

   /**
    * Search for images of parent pages and return an image when found.
    * @return the image of a parent page
    */
   private List<Integer> getImagesOfParent() {
      List<Page> pages = SiteManagement.getPagesFromPath(getPath());

      if (pages.size() > 1) {

         for (int i = pages.size() - 2; i >= 0; i--) {
            if (pages.get(i).getPageImages().size() > 0)
               return pages.get(i).getImages(); // when an image is found in a parent,
                                                // stop recursing
         }

      }
      return new ArrayList<Integer>();
   }


   private List<Integer> getCurrentPageImages() {
      List<Page> pages = SiteManagement.getPagesFromPath(getPath());
      return (pages.get(pages.size() - 1)).getImages();
   }


   private int random(int length) {
      Random rand = new Random();
      return rand.nextInt(length);
   }
}
