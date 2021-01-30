package it.ness.codebuilder.model;

import it.ness.codebuilder.annotations.CodeBuilderOption;
import it.ness.codebuilder.util.StringUtil;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.Set;

public class LocalDateTimeFilterDef extends FilterDefBase<LocalDateTimeFilterDef> {

    public LocalDateTimeFilterDef(final Log log) {
        super(log);
    }

    @Override
    public void addAnnotationToModelClass(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        String filterNameFrom = "from." + name;
        String filterNameTo = "to." + name;
        logFilterDef(javaClass, filterNameFrom, oldLogSet, newLogSet);
        logFilterDef(javaClass, filterNameTo, oldLogSet, newLogSet);
        AnnotationSource<JavaClassSource> filterDefAnnotationFrom = javaClass.addAnnotation();
        filterDefAnnotationFrom.setName("FilterDef");
        filterDefAnnotationFrom.setStringValue("name", filterNameFrom);
        AnnotationSource<JavaClassSource> paramAnnotationFrom = filterDefAnnotationFrom.addAnnotationValue("parameters");
        paramAnnotationFrom.setName("ParamDef");
        paramAnnotationFrom.setStringValue("name", name);
        paramAnnotationFrom.setStringValue("type", type);

        AnnotationSource<JavaClassSource> filterAnnotationFrom = javaClass.addAnnotation();
        filterAnnotationFrom.setName("Filter");
        filterAnnotationFrom.setStringValue("name", filterNameFrom);
        filterAnnotationFrom.setStringValue("condition", String.format("%s >= :%s", name, name));

        AnnotationSource<JavaClassSource> filterDefAnnotationTo = javaClass.addAnnotation();
        filterDefAnnotationTo.setName("FilterDef");
        filterDefAnnotationTo.setStringValue("name", filterNameTo);
        AnnotationSource<JavaClassSource> paramAnnotationTo = filterDefAnnotationTo.addAnnotationValue("parameters");
        paramAnnotationTo.setName("ParamDef");
        paramAnnotationTo.setStringValue("name", name);
        paramAnnotationTo.setStringValue("type", type);

        AnnotationSource<JavaClassSource> filterAnnotationTo = javaClass.addAnnotation();
        filterAnnotationTo.setName("Filter");
        filterAnnotationTo.setStringValue("name", filterNameTo);
        filterAnnotationTo.setStringValue("condition", String.format("%s <= :%s", name, name));
    }

    @Override
    public LocalDateTimeFilterDef parseCodeBuilderFilterDef(FieldSource<JavaClassSource> f) {
        AnnotationSource<JavaClassSource> a = f.getAnnotation("CodeBuilderLocalDateTimeFilterDef");
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
            if ("LocalDateTime".equals(f.getType().getName())) {
                type = "LocalDateTime";
            }
            if (!"LocalDateTime".equals(type)) {
                log.error("CodeBuilderLocalDateTimeFilterDef for " + f.getName() + " field is not applicable");
                return null;
            }
            String condition = "equals";

            LocalDateTimeFilterDef fd = new LocalDateTimeFilterDef(log);
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
        StringBuilder sb = new StringBuilder();
        sb.append(getLocalDateTimeEquals("from."));
        sb.append(getLocalDateTimeEquals("to."));
        return sb.toString();
    }

    private String getLocalDateTimeEquals(String prefix) {
        filterName = prefix + name;
        String formatBody = "if (nn(\"%s\")) {" +
                "LocalDateTime date = LocalDateTime.parse(get(\"%s\"));" +
                "search.filter(\"%s\", Parameters.with(\"%s\", date));" +
                "}";
        return String.format(formatBody, filterName, filterName, filterName, name);
    }

}
