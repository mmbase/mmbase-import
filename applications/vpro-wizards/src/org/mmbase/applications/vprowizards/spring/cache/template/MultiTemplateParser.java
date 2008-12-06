package org.mmbase.applications.vprowizards.spring.cache.template;

import java.util.ArrayList;
import java.util.List;

//TODO: the list of supported TemplateParsers is now hard coded.
public class MultiTemplateParser extends AbstractTemplateParser implements TemplateParser {

    private TemplateQueryRunner templateQueryRunner;

    public MultiTemplateParser(String nodeType, String nodeNumber, String template,
            TemplateQueryRunner templateQueryRunner) {
        super(nodeType, nodeNumber, template);
        this.templateQueryRunner = templateQueryRunner;
    }

    public void insertNumber() {
        process(new Processor() {
            @Override
            void process() {
                templateParser.insertNumber();
            }
        });
    }

//    public void setTemplate(String template) {
//        if (!MultiTemplateParser.isTemplate(template)) {
//            throw new IllegalStateException(String.format("template %s is not a multi template.", template));
//        }
//        this.template = template;
//    }

    public void removeNumber() {
        process(new Processor() {
            @Override
            void process() {
                templateParser.removeNumber();
            }
        });
    }

    public static boolean isTemplate(String template) {
        boolean result = true, insideTemplate = false;
        int templatesOpened = 0, templatesClosed = 0;
        boolean nextCharEscaped = false;
        String subTemplate = "";
        List<String> templates = new ArrayList<String>();

        char[] chars = template.toCharArray();
        for (char c : chars) {
            switch (c) {
            case '\\':
                if (!nextCharEscaped) {
                    nextCharEscaped = true;
                    break;
                }
            case '[':
                // if escape is on, and you are not inside a subtemplate, start a new subtemplate
                //if you are inside a template: disregard escape, write it
                if(insideTemplate){
                    subTemplate = subTemplate + c;
                }else{
                    if(!nextCharEscaped){
                        insideTemplate = true;
                        templatesOpened++;
                    }
                }
                nextCharEscaped = false;
                break;

            case ']':
                    // if escape is off, and you are inside a subtemplate, finish this.
                    // if escape is on and you are inside a template, write it.
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
                break;

            default:
                if (insideTemplate) {
                    subTemplate = subTemplate + c;
                }
                nextCharEscaped = false;
            }
        }

        for (String t : templates) {
            if (!SimpleTemplateParser.isTemplate(t) && !QueryTemplateParser.isTemplate(t))
                result = false;
        }
        if (templatesOpened != templatesClosed)
            result = false;
        if (templates.size() == 0)
            result = false; /* multitemplate should contain at least one sub template in square brackets */
        return result;
    }

    public static String cleanTemplate(String template) {
        MultiTemplateParser mtp = new MultiTemplateParser("", "", template, null);
        mtp.removeNumber();
        return mtp.getTemplate();
    }

    private void process(Processor processor) {
        int offset = 0;
        while (offset < template.getTemplate().length() && MultiTemplateParser.isTemplate(template.getTemplate().substring(offset))) {
            int begin = template.getTemplate().indexOf("[", offset) + 1;
            int end = template.getTemplate().indexOf("]", offset);
            String subTemplate = template.getTemplate().substring(begin, end);
            String templatePrefix = template.getTemplate().substring(0, begin);
            String templateSuffix = template.getTemplate().substring(begin + subTemplate.length());

            processor.setTemplateParser(createParserForSubtemplate(subTemplate));
            processor.process();
            String processedTemplate = processor.getTemplateParser().getTemplate();

            template = new Template(templatePrefix + processedTemplate + templateSuffix);
            offset = begin + processedTemplate.length() + 1;
        }
    }

    private TemplateParser createParserForSubtemplate(String subTemplate) {
        if (QueryTemplateParser.isTemplate(subTemplate)) {
            QueryTemplate queryTemplate = new QueryTemplate(subTemplate);
            return new QueryTemplateParser(nodeType, nodeNumber, subTemplate, queryTemplate, templateQueryRunner);
        }
        if (SimpleTemplateParser.isTemplate(subTemplate)) {
            return new SimpleTemplateParser(nodeType, nodeNumber, subTemplate);
        }
        throw new RuntimeException(String.format("Could not find right parser for template %s", subTemplate));

    }


    private static abstract class Processor {
        protected TemplateParser templateParser;

        void setTemplateParser(TemplateParser templateParser) {
            this.templateParser = templateParser;
        }

        public TemplateParser getTemplateParser() {
            return templateParser;
        }

        abstract void process();
    }

    @Override
    protected boolean matches(String template) {
        return MultiTemplateParser.isTemplate(template);
    }

    @Override
    protected Template instantiateTemplate(String templateStr) {
        return new Template(templateStr);
    }
}
