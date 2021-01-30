package it.ness.filterdefmavenplugin.model;

import it.ness.filterdefmavenplugin.annotations.CodeBuilderOption;
import it.ness.filterdefmavenplugin.util.StringUtil;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.Set;

public class NullBooleanFilterDef extends FilterDefBase<NullBooleanFilterDef> {

    public NullBooleanFilterDef(final Log log) {
        super(log);
    }

    @Override
    public void addAnnotationToModelClass(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        filterName = prefix + "." + name;
        logFilterDef(javaClass, filterName, oldLogSet, newLogSet);
        AnnotationSource<JavaClassSource> filterDefAnnotation = javaClass.addAnnotation();
        filterDefAnnotation.setName("FilterDef");
        filterDefAnnotation.setStringValue("name", filterName);

        AnnotationSource<JavaClassSource> filterAnnotation = javaClass.addAnnotation();
        filterAnnotation.setName("Filter");
        filterAnnotation.setStringValue("name", filterName);
        filterAnnotation.setStringValue("condition", String.format("%s IS NULL", name));
    }

    @Override
    public NullBooleanFilterDef parseCodeBuilderFilterDef(FieldSource<JavaClassSource> f) {
        AnnotationSource<JavaClassSource> a = f.getAnnotation("CodeBuilderNullBooleanFilterDef");
        if (null != a) {
            String prefix = a.getLiteralValue("prefix");
            if (null == prefix)
                prefix = "nil";
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
            if ("boolean".equals(f.getType().getName())) {
                log.warn(String.format("boolean %s should be renamed to Boolean", f.getName()));
                type = "boolean";
            }
            if ("Boolean".equals(f.getType().getName())) {
                type = "boolean";
            }
            if (!"boolean".equals(type)) {
                log.error("CodeBuilderNullBooleanFilterDef for " + f.getName() + " field is not applicable");
                return null;
            }
            String condition = a.getLiteralValue("condition");
            if (null != condition) {
                condition = StringUtil.removeQuotes(condition);
            }

            NullBooleanFilterDef fd = new NullBooleanFilterDef(log);
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
                "search.filter(\"%s\");" +
                "}";
        return String.format(formatBody, filterName, filterName);
    }
}
