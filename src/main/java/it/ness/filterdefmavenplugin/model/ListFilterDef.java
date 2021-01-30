package it.ness.filterdefmavenplugin.model;

import it.ness.filterdefmavenplugin.annotations.CodeBuilderOption;
import it.ness.filterdefmavenplugin.util.StringUtil;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.Set;

public class ListFilterDef extends FilterDefBase<ListFilterDef> {

    public ListFilterDef(final Log log) {
        super(log);
    }
    protected String nameInPlural;
    @Override
    public void addAnnotationToModelClass(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        nameInPlural = null;
        if (name.contains("_")) {
            nameInPlural = StringUtil.getPlural(name.substring(name.indexOf("_") + 1));
            nameInPlural = name.substring(0, name.indexOf("_")+1) + nameInPlural;
        } else {
            nameInPlural = StringUtil.getPlural(name);
        }
        filterName = prefix + "." + nameInPlural;
        logFilterDef(javaClass, filterName, oldLogSet, newLogSet);
        AnnotationSource<JavaClassSource> filterDefAnnotation = javaClass.addAnnotation();
        filterDefAnnotation.setName("FilterDef");
        filterDefAnnotation.setStringValue("name", filterName);
        AnnotationSource<JavaClassSource> paramAnnotation = filterDefAnnotation.addAnnotationValue("parameters");
        paramAnnotation.setName("ParamDef");
        paramAnnotation.setStringValue("name", nameInPlural);
        paramAnnotation.setStringValue("type", "string");

        AnnotationSource<JavaClassSource> filterAnnotation = javaClass.addAnnotation();
        filterAnnotation.setName("Filter");
        filterAnnotation.setStringValue("name", filterName);
        if (null == condition) {
            filterAnnotation.setStringValue("condition", String.format("%s IN (:%s)", name, nameInPlural));
        } else {
            filterAnnotation.setStringValue("condition", condition);
        }
    }

    @Override
    public ListFilterDef parseCodeBuilderFilterDef(FieldSource<JavaClassSource> f) {
        AnnotationSource<JavaClassSource> a = f.getAnnotation("CodeBuilderListFilterDef");
        if (null != a) {
            String prefix = a.getLiteralValue("prefix");
            if (null == prefix)
                prefix = "obj";
            else {
                prefix = StringUtil.removeQuotes(prefix);
            }
            String name = a.getLiteralValue("name");
            if (null == name)
                name = f.getName();
            else {
                name = StringUtil.removeQuotes(name);
            }

            String type = "";
            if ("String".equals(f.getType().getName())) {
                type = "string";
            }
            if (!"string".equals(type)) {
                log.error("CodeBuilderListFilterDef for " + f.getName() + " field is not applicable");
                return null;
            }

            String condition = a.getLiteralValue("condition");
            if (null != condition) {
                condition = StringUtil.removeQuotes(condition);
            }
            ListFilterDef fd = new ListFilterDef(log);
            fd.prefix = prefix;
            fd.name = name;
            fd.type = type;
            fd.condition = condition;
            String options = a.getLiteralValue("options");
            if (null != options) {
                options = StringUtil.removeQuotes(options);
                fd.options = CodeBuilderOption.from(options);
            }
            return fd;
        }
        return null;
    }

    @Override
    public String getSearchMethod() {

        String formatBody = "if (nn(\"%s\")) {" +
                "String[] %s = get(\"%s\").split(\",\");" +
                "getEntityManager().unwrap(Session.class)" +
                "         .enableFilter(\"%s\")" +
                "         .setParameterList(\"%s\", %s);" +
                "}";
        return String.format(formatBody, filterName, nameInPlural, filterName, filterName, nameInPlural, nameInPlural);
    }

}
