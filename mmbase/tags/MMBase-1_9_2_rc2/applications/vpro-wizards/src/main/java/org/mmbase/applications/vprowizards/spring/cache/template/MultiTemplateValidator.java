package org.mmbase.applications.vprowizards.spring.cache.template;

import java.util.ArrayList;
import java.util.List;

/**
 * This class parses and validates a multitemplate, which is a string containing at least one simple or query template
 * between square brackets.
 * 
 * Instances of this class can be reused, but the class is not thread safe.
 * 
 * @author ebunders
 * 
 */
public class MultiTemplateValidator {
    private static List<String> templates = new ArrayList<String>();;
    private static boolean isTemplate = false;
    private boolean insideTemplate = false;
    private int templatesOpened = 0, templatesClosed = 0;
    private boolean nextCharEscaped = false;
    private String subTemplate = "";
    private String template;

    public MultiTemplateValidator(String template) {
        this.template = template;
    }

    public void validate() {
        init();
        tokenizeTemplate();
        isTemplate = allTemplatesAreOpenedAndClosed() && hasAnySubtempates() && allSubtemplatesAreValid();
    }

    private boolean allTemplatesAreOpenedAndClosed() {
        return templatesOpened == templatesClosed;
    }

    private boolean hasAnySubtempates() {
        return templates.size() > 0;
    }

    private boolean allSubtemplatesAreValid() {
        for (String t : templates) {
            if (!SimpleTemplateParser.isTemplate(t) && !QueryTemplateParser.isTemplate(t))
                return false;
        }
        return true;
    }

    private void init() {
        templates = new ArrayList<String>();
        ;
        isTemplate = false;
        insideTemplate = false;
        templatesOpened = 0;
        templatesClosed = 0;
        nextCharEscaped = false;
        subTemplate = "";
    }

    public int getTemplatesOpened() {
        return templatesOpened;
    }

    public int getTemplatesClosed() {
        return templatesClosed;
    }

    private void tokenizeTemplate() {
        char[] chars = template.toCharArray();
        for (char c : chars) {
            switch (c) {
            case '\\':
                if (!nextCharEscaped) {
                    nextCharEscaped = true;
                    break;
                }
            case '[':
                handleOpeningBracket(c);
                break;

            case ']':
                handleClosingBracket(c);
                break;

            default:
                handleDefault(c);
            }
        }
    }

    private void handleDefault(char c) {
        if (insideTemplate) {
            subTemplate = subTemplate + c;
        }
        nextCharEscaped = false;
    }

    /**
     * If escape is off, and you are inside a subtemplate, finish this. If escape is on and you are inside a template,
     * write it.
     * 
     * @param c
     */
    private void handleClosingBracket(char c) {
        if (insideTemplate) {
            if (nextCharEscaped) {
                subTemplate = subTemplate + c;
            } else {
                insideTemplate = false;
                templates.add(subTemplate);
                subTemplate = "";
                templatesClosed++;
            }
        }
        nextCharEscaped = false;
    }

    /**
     * If escape is on, and you are not inside a subtemplate, start a new subtemplate If you are inside a template:
     * disregard escape, write it
     * 
     * @param c
     */
    private void handleOpeningBracket(char c) {
        if (insideTemplate) {
            subTemplate = subTemplate + c;
        } else {
            if (!nextCharEscaped) {
                insideTemplate = true;
                templatesOpened++;
            }
        }
        nextCharEscaped = false;
    }

    public boolean isValidMultitemplate() {
        return isTemplate;
    }

    public List<String> getTemplates() {
        return templates;
    }
}
