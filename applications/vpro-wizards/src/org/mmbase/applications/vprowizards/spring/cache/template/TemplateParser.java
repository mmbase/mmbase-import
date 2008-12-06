package org.mmbase.applications.vprowizards.spring.cache.template;

/**
 * A template is a String where certain placeholders can be prepended with (node) numbers.
 * A template parser can perform those operations on a template.
 * @author ebunders
 *
 */
public interface TemplateParser {
    public void insertNumber();
    public void removeNumber();
    public String getTemplate();
}
