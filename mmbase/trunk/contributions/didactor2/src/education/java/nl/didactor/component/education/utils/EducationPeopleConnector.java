package nl.didactor.component.education.utils;

import java.util.HashSet;
import java.util.Iterator;

import org.mmbase.bridge.*;
import org.mmbase.module.core.*;

public class EducationPeopleConnector
{
   Cloud cloud;

   public EducationPeopleConnector(Cloud cloud)
   {
      this.cloud = cloud;
   }

   public HashSet relatedPersons(String educationNumber)
   {
      HashSet hsetResult = new HashSet();

      Node nodeEducation = cloud.getNode((new Integer(educationNumber)).intValue());

      NodeList nodelistPeople = nodeEducation.getRelatedNodes("people", "classrel", "destination");
      for(Iterator it = nodelistPeople.iterator(); it.hasNext(); )
      {
         hsetResult.add("" + ((Node) it.next()).getNumber());
      }

      NodeList nodelistClasses = nodeEducation.getRelatedNodes("classes", "related", "destination");
      for(Iterator it = nodelistClasses.iterator(); it.hasNext();)
      {
         Node nodeClass = (Node) it.next();
         nodelistPeople = nodeClass.getRelatedNodes("people", "classrel", "source");
         for(Iterator it2 = nodelistPeople.iterator(); it2.hasNext();)
         {
            hsetResult.add("" + ((Node) it2.next()).getNumber());
         }
      }

      return hsetResult;
   }

   public HashSet relatedEducations(String personNumber)
   {
      HashSet hsetResult = new HashSet();

      Node nodePerson = cloud.getNode((new Integer(personNumber)).intValue());

      NodeList nodelistEducations = nodePerson.getRelatedNodes("educations", "classrel", "source");
      for(Iterator it = nodelistEducations.iterator(); it.hasNext(); )
      {
         hsetResult.add("" + ((Node) it.next()).getNumber());
      }

      NodeList nodelistClasses = nodePerson.getRelatedNodes("classes", "classrel", "destination");
      for(Iterator it = nodelistClasses.iterator(); it.hasNext(); )
      {
         Node nodeClass = (Node) it.next();
         NodeList nodelistPeople = nodeClass.getRelatedNodes("educations", "related", "source");
         for(Iterator it2 = nodelistPeople.iterator(); it2.hasNext();)
         {
            hsetResult.add("" + ((Node) it2.next()).getNumber());
         }
      }

      return hsetResult;
   }

}