/*
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Initial Developer of the Original Code is 'Media Competence'
 *
 * See license.txt in the root of the LeoCMS directory for the full license.
 */

package nl.leocms.connectors.Exporter.output.People;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.mmbase.util.logging.*;
import javax.xml.parsers.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

import org.mmbase.bridge.*;
import com.finalist.mmbase.util.CloudFactory;
import nl.leocms.util.ApplicationHelper;
import nl.leocms.util.tools.*;
import nl.leocms.connectors.Exporter.creatingXML;

public class Exporter implements Runnable

{
   private static final Logger log = Logging.getLoggerInstance(Exporter.class);
   private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy");

   public Exporter()
   {
   }


   public void run() {

      log.info("exporting people");

      try {
         DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
         Document document = domBuilder.newDocument();
         Element elemRoot = document.createElement("people");
         document.appendChild(elemRoot);
         Cloud cloud = CloudFactory.getCloud();
         ApplicationHelper ap = new ApplicationHelper(cloud);
         String tempDir = ap.getTempDir();

         SearchUtil su = new SearchUtil();
         String employeeConstraint = su.sEmployeeConstraint;
         creatingXML cxml = new creatingXML();
         NodeList nl = cloud.getNodeManager("medewerkers").getList(employeeConstraint,"medewerkers.number","up");
         for (int i = 0; i < nl.size(); i++){
            Element elemPerson = document.createElement("person");
            elemRoot.appendChild(elemPerson);

            Element elemPersonInfo = document.createElement("personalInformation");
            elemPerson.appendChild(elemPersonInfo);

            Element elemPersonId = document.createElement("id");
            elemPersonId.appendChild(document.createTextNode(nl.getNode(i).getStringValue("number")));
            elemPersonInfo.appendChild(elemPersonId);

            Element elemPersonInitials = document.createElement("initials");
            elemPersonInitials.appendChild(document.createTextNode(nl.getNode(i).getStringValue("initials")));
            elemPersonInfo.appendChild(elemPersonInitials);

            Element elemPersonFirstName = document.createElement("firstName");
            elemPersonFirstName.appendChild(document.createTextNode(nl.getNode(i).getStringValue("firstname")));
            elemPersonInfo.appendChild(elemPersonFirstName);

            Element elemPersonSuffix = document.createElement("suffix");
            elemPersonSuffix.appendChild(document.createTextNode(nl.getNode(i).getStringValue("suffix")));
            elemPersonInfo.appendChild(elemPersonSuffix);

            Element elemPersonLastName = document.createElement("lastName");
            elemPersonLastName.appendChild(document.createTextNode(nl.getNode(i).getStringValue("lastname")));
            elemPersonInfo.appendChild(elemPersonLastName);

            Element elemBirthDate = document.createElement("birthDate");
            elemBirthDate.appendChild(document.createTextNode(DATE_TIME_FORMAT.format(
            new Date(nl.getNode(i).getLongValue("dayofbirth")*1000))));
            elemPersonInfo.appendChild(elemBirthDate);

            Element elemPersonGender = document.createElement("gender");
            elemPersonGender.appendChild(document.createTextNode(nl.getNode(i).getStringValue("gender")));
            elemPersonInfo.appendChild(elemPersonGender);

            Element elemPersonTelephoneNo = document.createElement("telephoneNo");
            elemPersonTelephoneNo.appendChild(document.createTextNode(nl.getNode(i).getStringValue("companyphone")));
            elemPersonInfo.appendChild(elemPersonTelephoneNo);

            Element elemPersonCellularNo = document.createElement("cellularNo");
            elemPersonCellularNo.appendChild(document.createTextNode(nl.getNode(i).getStringValue("cellularphone")));
            elemPersonInfo.appendChild(elemPersonCellularNo);

            Element elemPersonEmailAddress = document.createElement("emailAddress");
            elemPersonEmailAddress.appendChild(document.createTextNode(nl.getNode(i).getStringValue("email")));
            elemPersonInfo.appendChild(elemPersonEmailAddress);

            Element elemPersonFaxNo = document.createElement("faxNo");
            elemPersonFaxNo.appendChild(document.createTextNode(nl.getNode(i).getStringValue("fax")));
            elemPersonInfo.appendChild(elemPersonFaxNo);

            Element elemJobInfo = document.createElement("jobInformation");
            elemPerson.appendChild(elemJobInfo);

            Element elemJobs = document.createElement("jobs");
            elemJobInfo.appendChild(elemJobs);

            document = buildJob(cloud,document,elemJobs,nl.getNode(i).getStringValue("number"),"afdelingen",su.sAfdelingenConstraints);
            document = buildJob(cloud,document,elemJobs,nl.getNode(i).getStringValue("number"),"locations",null);

            Element elemDescr = document.createElement("description");
            elemDescr.appendChild(document.createTextNode(nl.getNode(i).getStringValue("description")));
            elemJobInfo.appendChild(elemDescr);

            NodeList nlImages = nl.getNode(i).getRelatedNodes("images");
            document = getImages(nlImages,document,elemPerson,true,tempDir,cxml);

         }

         String[] sDirs = {"images"};
         cxml.create(document,"people",sDirs);

      }
      catch (Exception e){
         log.info(e.toString());
      }
   }

   public Document buildJob(Cloud cloud,Document document,Element elRoot,
   String sNodeNumber, String sBuilderName, String sConstraints){
      NodeList nl = cloud.getList(sNodeNumber,
      "medewerkers,readmore," + sBuilderName,"readmore.number,readmore.readmore, " +
      sBuilderName + ".number, " + sBuilderName + ".naam",sConstraints,
      "readmore.number," + sBuilderName + ".number","up,up",null,true);
      for (int j = 0; j < nl.size(); j++){
         Element elemJob = document.createElement("job");
         elRoot.appendChild(elemJob);

         Element elemJobId = document.createElement("id");
         elemJobId.appendChild(document.createTextNode(nl.getNode(j).getStringValue("readmore.number")));
         elemJob.appendChild(elemJobId);

         Element elemJobName = document.createElement("name");
         elemJobName.appendChild(document.createTextNode(nl.getNode(j).getStringValue("readmore.readmore")));
         elemJob.appendChild(elemJobName);

         Element elemDepartmment = document.createElement("department");
         elemJob.appendChild(elemDepartmment);

         Element elemDepartmentId = document.createElement("id");
         elemDepartmentId.appendChild(document.createTextNode(nl.getNode(j).getStringValue(sBuilderName + ".number")));
         elemDepartmment.appendChild(elemDepartmentId);

         Element elemDepartmentName = document.createElement("name");
         elemDepartmentName.appendChild(document.createTextNode(nl.getNode(j).getStringValue(sBuilderName + ".naam")));
         elemDepartmment.appendChild(elemDepartmentName);

         Element elemDepartmentType = document.createElement("type");
         elemDepartmentType.appendChild(document.createTextNode(sBuilderName.substring(0,3)));
         elemDepartmment.appendChild(elemDepartmentType);

      }
      return document;
   }

   public Document getImages(NodeList nl,Document document, Element elRoot, boolean bShowTag,String sTempDir,creatingXML cxml){
      File fAttachmentsDir = new File(sTempDir + "images/");
         if (!fAttachmentsDir.exists()){
            fAttachmentsDir.mkdir();
         }
      if(nl.size()==0&bShowTag){
         Element elemImage = document.createElement("image");
         elRoot.appendChild(elemImage);
      }
      for (int j = 0; j < nl.size(); j++){
         Element elemImage = document.createElement("image");
         elRoot.appendChild(elemImage);

         Element elemImageId = document.createElement("id");
         elemImageId.appendChild(document.createTextNode(nl.getNode(j).getStringValue("number")));
         elemImage.appendChild(elemImageId);

         Element elemImageTitle = document.createElement("title");
         String sTitel_zichtbaar = nl.getNode(j).getStringValue("titel_zichtbaar");
         if (sTitel_zichtbaar!=null&&!sTitel_zichtbaar.equals("0")){
            elemImageTitle.appendChild(document.createTextNode(nl.
               getNode(j).getStringValue("title")));
         }
         elemImage.appendChild(elemImageTitle);

         Element elemImageDescr = document.createElement("description");
         elemImageDescr.appendChild(document.createTextNode(nl.getNode(j).getStringValue("omschrijving")));
         elemImage.appendChild(elemImageDescr);

         Element elemImageFileName = document.createElement("filename");
         String sFileName = nl.getNode(j).getStringValue("number") + "." + nl.getNode(j).getStringValue("itype");
         elemImageFileName.appendChild(document.createTextNode(sFileName));
         elemImage.appendChild(elemImageFileName);

         byte[] thedata = nl.getNode(j).getByteValue("handle");
         cxml.writingFile(sTempDir + "images/" + sFileName, thedata);
      }

      return document;
   }
}
