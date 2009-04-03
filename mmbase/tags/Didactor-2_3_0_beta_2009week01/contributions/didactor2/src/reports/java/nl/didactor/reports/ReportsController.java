/*
 * Copyright (c) 2006 Levi9 Global Sourcing. All Rights Reserved.
 * This software is the confidential and proprietary information of
 * Levi9 Global Sourcing. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it
 * only in accordance with the terms of the license agreement you
 * entered into with Levi9 Global Sourcing.
 * Levi9 Global Sourcing makes no representations or warranties about the
 * suitability of the software, either express or implied,
 * including but not limited to the implied warranties of merchantability,
 * fitness for a particular purpose, or non-infringement. Levi9 Global Sourcing
 * shall not be liable for any damages suffered by licensee as a
 * result of using, modifying or distributing this software or its
 * derivatives.
 */

package nl.didactor.reports;

import nl.didactor.reports.util.EventManager;
import nl.didactor.reports.util.ReportsPages;

import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;


/**
 * @author p.becic
 */
public class ReportsController implements Controller {

    public ModelAndView handleRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        HashMap map = new HashMap();

        // get page and class from request
        String page = request.getParameter("page");

        String classNumberAsString = request.getParameter("class");
        int classNumber = 0;
        if (classNumberAsString != null) {
            classNumber = Integer.decode(classNumberAsString).intValue();
        }
        if (page.equals(ReportsPages.LOGIN_REPORTS)) {
            map = EventManager.getAverageTimePerLogin();
        }
        else if (page.equals(ReportsPages.EDUCATION_REPORTS)) {
            map = EventManager.getAverageTimePerEducation(classNumber);
        }
        else if (page.equals(ReportsPages.LEARNOBJECT_REPORTS)) {
            map = EventManager.getLearnobjectStatistic(classNumber);
        }
        else if (page.equals(ReportsPages.DOCUMENT_REPORTS)) {
            String start = request.getParameter("startdate");
            String end = request.getParameter("enddate");
            long startTime = Long.decode(start).longValue();
            long endTime = Long.decode(end).longValue();
            map = EventManager.getDocumentStatistic(startTime, endTime);
        }

        return new ModelAndView("index", "map", map);
    }
}