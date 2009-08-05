package com.finalist.cmsc.workflow.form;

import com.finalist.cmsc.workflow.forms.Utils;
import junit.framework.TestCase;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;

import javax.servlet.jsp.PageContext;

public class TestUtils extends TestCase {

   private Mockery context = new Mockery() {
      {
         setImposteriser(ClassImposteriser.INSTANCE);
      }
   };


   private void initMockObject(Mockery context, final PageContext pagecontext) {

      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("status");
            will(returnValue("status"));
         }
      });
   }

   public void testGenerateSortingOnclick() {
      final PageContext pagecontext = context.mock(PageContext.class);
      initMockObject(context, pagecontext);

      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("orderby");
            will(returnValue("title"));
         }
      });

      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("lastvalue");
            will(returnValue(true));
         }
      });


      assertEquals("onClick=\"selectTab('status','title','true')\" class=\"sortup\"", Utils.onClickandStyle(pagecontext, "title"));
      assertEquals("onClick=\"selectTab('status','faketitle','false')\"", Utils.onClickandStyle(pagecontext, "faketitle"));

      context.assertIsSatisfied();

   }

   public void testGenerateSortingOnclickLastValue() {
      final PageContext pagecontext = context.mock(PageContext.class);
      initMockObject(context, pagecontext);

      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("orderby");
            will(returnValue("title"));
         }
      });


      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("lastvalue");
            will(returnValue(false));
         }
      });

      assertEquals("onClick=\"selectTab('status','title','false')\" class=\"sortdown\"", Utils.onClickandStyle(pagecontext, "title"));
   }

   public void testGenerateSortingOnclickDefault() {
      final PageContext pagecontext = context.mock(PageContext.class);
      initMockObject(context, pagecontext);

      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("orderby");
            will(returnValue("undefined"));
         }
      });


      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("lastvalue");
            will(returnValue(false));
         }
      });

      assertEquals("onClick=\"selectTab('status','lastmodifieddate','true')\" class=\"sortup\"", Utils.onClickandStyle(pagecontext, "undefined"));

   }

   public void testTabClass() {
      final PageContext pagecontext = context.mock(PageContext.class);

      context.checking(new Expectations() {
         {
            allowing(pagecontext).findAttribute("status");
            will(returnValue("stat"));
         }
      });

      assertEquals("tab_active", Utils.tabClass(pagecontext, "stat"));
      assertEquals("tab", Utils.tabClass(pagecontext, "sttt"));

   }
}
