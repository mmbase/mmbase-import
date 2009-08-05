package nl.didactor.component.competence.cron;

import java.util.Date;
import nl.didactor.component.competence.builders.CompetencesBuilder;

public class CompetenceUpdater implements Runnable
{
   private CompetencesBuilder competenceBuilder;

   public CompetenceUpdater()
   {
      Thread thread = Thread.currentThread();
      thread.setPriority(Thread.MIN_PRIORITY + 1);
      competenceBuilder = new CompetencesBuilder();
   }
   public void run()
   {
      competenceBuilder.commitAll();
   }
}