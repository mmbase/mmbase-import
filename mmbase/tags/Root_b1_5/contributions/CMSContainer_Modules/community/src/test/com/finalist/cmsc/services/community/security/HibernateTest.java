package com.finalist.cmsc.services.community.security;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
//import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;
import org.springframework.util.Assert;


public class HibernateTest extends AbstractTransactionalDataSourceSpringContextTests {

    private SessionFactory sessionFactory;

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"spring-community.xml"};
    }

    public void testDatabase() {
        Assert.notNull(sessionFactory, "SessionFactory required");

        HibernateTemplate template = new HibernateTemplate(sessionFactory);

        Authentication marissa = buildUser("marissa", "a564de63c2d0da68cf47586ee05984d7", true);
        template.save(marissa);
        Authentication dianne = buildUser("dianne", "65d15fe9156f9c4bbffd98085992a44e", true);
        template.save(dianne);
        Authentication scott = buildUser("scott", "2b58af6dddbd072ed27ffc86725d7d3a", true);
        template.save(scott);
        Authentication peter = buildUser("peter", "22b5c9accc6e1ba628cedc63a72d57f8", false);
        template.save(peter);
        Authentication bill = buildUser("bill", "2b58af6dddbd072ed27ffc86725d7d3a", true);
        template.save(bill);
        Authentication bob = buildUser("bob", "2b58af6dddbd072ed27ffc86725d7d3a", true);
        template.save(bob);
        Authentication jane = buildUser("jane", "2b58af6dddbd072ed27ffc86725d7d3a", true);
        template.save(jane);

        Authority auth1 = new Authority();
        auth1.setName("ADMINISTRATOR");
        auth1.addAuthentication(marissa);
        auth1.addAuthentication(dianne);
        auth1.addAuthentication(scott);
        template.save(auth1);

        template.flush();


        List result = template.find("from Authentication where userId = 'marissa'");
        for (Iterator iter = result.iterator(); iter.hasNext();) {
            Authentication authentication = (Authentication) iter.next();
            System.out.println(authentication.getUserId() + ", " + authentication.getPassword());

            Set<Authority> authorities = authentication.getAuthorities();
            for (Iterator iter2 = authorities.iterator(); iter2.hasNext();) {
                Authority a = (Authority) iter2.next();
                System.out.println(a.getName());
            }
        }

        System.out.println("C'est tout!!");
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Authentication buildUser(String userId, String password, boolean enabled) {
        Authentication authentication = new Authentication();
        authentication.setUserId(userId);
        authentication.setPassword(password);
        authentication.setEnabled(enabled);
        return authentication;
    }
}