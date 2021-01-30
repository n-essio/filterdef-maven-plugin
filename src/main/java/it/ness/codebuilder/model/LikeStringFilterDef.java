package it.ness.codebuilder.model;

import it.ness.codebuilder.annotations.CodeBuilderOption;
import it.ness.codebuilder.util.StringUtil;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.Set;

public class LikeStringFilterDef extends FilterDefBase<LikeStringFilterDef> {

    public LikeStringFilterDef(final Log log) {
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
        filterAnnotation.setStringValue("condition", String.format("lower(%s) LIKE :%s", name, name));
    }

    @Override
    public LikeStringFilterDef parseCodeBuilderFilterDef(FieldSource<JavaClassSource> f) {
        AnnotationSource<JavaClassSource> a = f.getAnnotation("CodeBuilderLikeStringFilterDef");
        if (null != a) {
            String prefix = a.getLiteralValue("prefix");
            if (null == prefix)
                prefix = "like";
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
            if (f.getAnnotation("Enumerated") != null) {
                type = "string";
            }
            if ("String".equals(f.getType().getName())) {
                type = "string";
            }
            if (!"string".equals(type)) {
                log.error("CodeBuilderLikeStringFilterDef for " + f.getName() + " field is not applicable");
                return null;
            }
            String condition = a.getLiteralValue("condition");
            if (null != condition) {
                condition = StringUtil.removeQuotes(condition);
            }

            LikeStringFilterDef fd = new LikeStringFilterDef(log);
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
        String formatBody = "if (nn(\"%s\")) {" +
                "search.filter(\"%s\", Parameters.with(\"%s\", likeParamToLowerCase(\"%s\")));" +
                "}";
        return String.format(formatBody, filterName, filterName, name, filterName);
    }

    private String getStringEqualsAlways(String prefix) {
        String formatBody = "search.filter(\"%s\", Parameters.with(\"%s\", likeParamToLowerCase(\"%s\")));";
        return String.format(formatBody, filterName, name, filterName);
    }

    private String getStringEqualsWithoutParameters(String prefix) {
        String formatBody = "if (nn(\"%s\")) {" +
                "search.filter(\"%s\");" +
                "}";
        return String.format(formatBody, filterName, filterName);
    }

}
