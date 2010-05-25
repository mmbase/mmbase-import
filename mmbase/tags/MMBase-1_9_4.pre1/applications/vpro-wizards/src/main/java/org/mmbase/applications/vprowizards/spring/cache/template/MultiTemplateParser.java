package org.mmbase.applications.vprowizards.spring.cache.template;


//TODO: the list of supported TemplateParsers is now hard coded.
public class MultiTemplateParser extends AbstractTemplateParser implements TemplateParser {

    private TemplateQueryRunner templateQueryRunner;

    public static boolean isTemplate(String template) {
        MultiTemplateValidator validator = new MultiTemplateValidator(template);
        validator.validate();
        return validator.isValidMultitemplate();
    }

    public static String stripTemplatesLeaveNodeNr(String template) {
        MultiTemplateParser mtp = new MultiTemplateParser("", "", template, null);
        mtp.stripTemplateLeaveNodenr();
        return mtp.getTemplate();
    }

    public MultiTemplateParser(String nodeType, String nodeNumber, String template,
            TemplateQueryRunner templateQueryRunner) {
        super(nodeType, nodeNumber, template);
        this.templateQueryRunner = templateQueryRunner;
    }

    public void stripTemplateLeaveNodenr() {
        process(new Processor() {
            @Override
            void process() {
                templateParser.stripTemplateLeaveNodenr();
            }

            @Override
            String processPrefix(String prefix) {
                //strip the opening bracket
                return prefix.substring(0, prefix.length()-1);
            }

            @Override
            String processSuffix(String suffix) {
                return suffix.substring(1);
            }
            
            
        });
    }

    public void insertNodeNumber() {
        process(new Processor() {
            @Override
            void process() {
                templateParser.insertNodeNumber();
            }
        });
    }


    public void removeNodeNumber() {
        process(new Processor() {
            @Override
            void process() {
                templateParser.removeNodeNumber();
            }
        });
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
    
            template = new Template(processor.processPrefix(templatePrefix) + 
                    processedTemplate + 
                    processor.processSuffix(templateSuffix));
            offset = begin + processedTemplate.length() + 1;
        }
    }

    private TemplateParser createParserForSubtemplate(String subTemplate) {
        //TODO: i would like a factory class for all this type specific stuff.
        if (QueryTemplateParser.isTemplate(subTemplate)) {
            QueryTemplate queryTemplate = new QueryTemplate(subTemplate);
            return new QueryTemplateParser(nodeType, nodeNumber, subTemplate, queryTemplate, templateQueryRunner);
        }
        if (SimpleTemplateParser.isTemplate(subTemplate)) {
            return new SimpleTemplateParser(nodeType, nodeNumber, subTemplate);
        }
        throw new RuntimeException(String.format("Could not find right parser for template %s", subTemplate));
    }


    @Override
    protected boolean matches(String template) {
        return MultiTemplateParser.isTemplate(template);
    }

    @Override
    protected Template instantiateTemplate(String templateStr) {
        //TODO: there should be a MultiTemplate type, that holds the structure of text/subtemplates as a model.
        
        return new Template(templateStr);
    }


    private static abstract class Processor {
        protected TemplateParser templateParser;
    
        void setTemplateParser(TemplateParser templateParser) {
            this.templateParser = templateParser;
        }
    
         TemplateParser getTemplateParser() {
            return templateParser;
        }
        
         String processPrefix(String prefix){
            return prefix;
        }
        
         String processSuffix(String suffix){
            return suffix;
        }
    
        abstract void process();
    }
}
