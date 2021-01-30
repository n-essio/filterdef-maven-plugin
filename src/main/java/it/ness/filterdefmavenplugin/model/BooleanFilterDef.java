package it.ness.filterdefmavenplugin.model;

import it.ness.filterdefmavenplugin.annotations.CodeBuilderOption;
import it.ness.filterdefmavenplugin.util.StringUtil;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.Set;

public class BooleanFilterDef extends FilterDefBase<BooleanFilterDef> {

    public BooleanFilterDef(final Log log) {
        super(log);
    }

    @Override
    public void addAnnotationToModelClass(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        filterName = prefix + "." + name;
        logFilterDef(javaClass, filterName, oldLogSet, newLogSet);
        AnnotationSource<JavaClassSource> filterDefAnnotation = javaClass.addAnnotation();
        filterDefAnnotation.setName("FilterDef");
        filterDefAnnotation.setStringValue("name", filterName);
        AnnotationSource<JavaClassSource> paramAnnotation = filterDefAnnotation.addAnnotationValue("parameters");
        paramAnnotation.setName("ParamDef");
        paramAnnotation.setStringValue("name", name);
        paramAnnotation.setStringValue("type", type);

        AnnotationSource<JavaClassSource> filterAnnotation = javaClass.addAnnotation();
        filterAnnotation.setName("Filter");
        filterAnnotation.setStringValue("name", filterName);
        filterAnnotation.setStringValue("condition", String.format("%s = :%s", name, name));
    }

    @Override
    public BooleanFilterDef parseCodeBuilderFilterDef(FieldSource<JavaClassSource> f) {
        AnnotationSource<JavaClassSource> a = f.getAnnotation("CodeBuilderBooleanFilterDef");
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
            if ("boolean".equals(f.getType().getName())) {
                log.warn(String.format("boolean %s should be renamed to Boolean", f.getName()));
                type = "boolean";
            }
            if ("Boolean".equals(f.getType().getName())) {
                type = "boolean";
            }
            if (!"boolean".equals(type)) {
                log.error("CodeBuilderBooleanFilterDef for " + f.getName() + " field is not applicable");
                return null;
            }
            String condition = a.getLiteralValue("condition");
            if (null != condition) {
                condition = StringUtil.removeQuotes(condition);
            }

            BooleanFilterDef fd = new BooleanFilterDef(log);
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
        if (containsOption(CodeBuilderOption.EXECUTE_ALWAYS)) {
            return getStringEqualsAlways(prefix);
        }
        if (containsOption(CodeBuilderOption.WITHOUT_PARAMETERS)) {
            return getStringEqualsWithoutParameters(prefix);
        }
        if (null == condition) {
            String formatBody = "if (nn(\"%s\")) {" +
                    "search.filter(\"%s\", Parameters.with(\"%s\", get(\"%s\")));" +
                    "}";
            return String.format(formatBody, filterName, filterName, name, filterName);
        }
        String formatBody = "if (nn(\"%s\")) {" +
                "search.filter(\"%s\", Parameters.with(\"%s\", %s));" +
                "}";
        return String.format(formatBody, filterName, filterName, name, condition);
    }

    private String getStringEqualsAlways(String prefix) {
        if (null == condition) {
            String formatBody = "search.filter(\"%s\", Parameters.with(\"%s\", get(\"%s\")));";
            return String.format(formatBody, filterName, name, filterName);
        }
        String formatBody = "search.filter(\"%s\", Parameters.with(\"%s\", %s));";
        return String.format(formatBody, filterName, name, condition);
    }

    private String getStringEqualsWithoutParameters(String prefix) {
        String formatBody = "if (nn(\"%s\")) {" +
                "search.filter(\"%s\");" +
                "}";
        return String.format(formatBody, filterName, filterName);
    }

}
