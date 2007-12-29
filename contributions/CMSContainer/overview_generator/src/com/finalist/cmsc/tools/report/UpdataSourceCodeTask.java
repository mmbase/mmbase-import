package com.finalist.cmsc.tools.report;

import org.apache.tools.ant.taskdefs.Cvs;
import org.tigris.subversion.svnant.Checkout;
import org.tigris.subversion.svnant.SvnTask;
import org.tigris.subversion.svnclientadapter.SVNUrl;

import java.io.File;
import java.util.List;
import java.net.MalformedURLException;

public class UpdataSourceCodeTask extends BatchOperationTask {

   public void execute() {

      List<VCSConfig> configs = getConfigs();

      for (VCSConfig vcsConfig : configs) {

         File path = new File(vcsConfig.getWorkingfolder());

         if ("svn".equals(vcsConfig.getType())) {
            updateFromSVN(vcsConfig, path);
         }

         if ("cvs".equals(vcsConfig.getType())) {
            updateFromCVS(vcsConfig, path);
         }
      }
   }

   private void updateFromCVS(VCSConfig vcsConfig, File dest) {

      Cvs cvs = new Cvs();
      cvs.setCvsRoot(vcsConfig.getUrl());
      cvs.setPackage(vcsConfig.getModule());
      cvs.setDest(dest);
      cvs.execute();

   }

   private void updateFromSVN(VCSConfig vcsConfig, File dest) {
      SVNUrl url = null;

      try {
         url = new SVNUrl(vcsConfig.getUrl());
      } catch (MalformedURLException e) {
         e.printStackTrace();
         log("The URL of SVN repo error");
      }

      SvnTask task = new SvnTask();
      Checkout checkout = new Checkout();
      checkout.setUrl(url);
      checkout.setDestpath(dest);
      task.setUsername(vcsConfig.getUsername());
      task.setPassword(vcsConfig.getPassword());

      task.addCheckout(checkout);

      task.execute();
   }
}
