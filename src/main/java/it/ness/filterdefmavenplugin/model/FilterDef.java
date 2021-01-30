package it.ness.filterdefmavenplugin.model;

import it.ness.filterdefmavenplugin.annotations.CodeBuilderOption;
import it.ness.filterdefmavenplugin.util.StringUtil;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class FilterDef implements Comparable<FilterDef> {
    public String prefix;
    public String name;
    public String type;
    public String condition;
    public CodeBuilderOption[] options;

    public FilterDef(final String prefix, final String name, final String type, final String condition) {
        this.prefix = prefix;
        this.name = name;
        this.type = type;
        this.condition = condition;
    }

    public FilterDef(final String prefix, final String name, final String type, final String condition, final CodeBuilderOption[] options) {
        this.prefix = prefix;
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.options = options;
    }

    public boolean containsOption(CodeBuilderOption codeBuilderOption) {
        if (options == null) {
            return false;
        }
        for (CodeBuilderOption option : options) {
            if (option.equals(codeBuilderOption))
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FilterDef filterDef = (FilterDef) o;
        return prefix.equals(filterDef.prefix) && name.equals(filterDef.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, name, type, condition);
    }

    public String toString() {
        return String.format("FilterDef={prefix=%s, name=%s, type=%s, condition=%s}", prefix, name, type, condition);
    }

    @Override
    public int compareTo(FilterDef filterDef) {
        return this.name.compareTo(filterDef.name);
    }

    public void addAnnotationToClass(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        switch (type) {
            case "string":
                if ("like".equals(condition)) {
                    stringlike_type(javaClass, oldLogSet, newLogSet);
                } else if ("not".equals(condition)) {
                    stringnot_type(javaClass, "not", oldLogSet, newLogSet);
                } else {
                    string_type(javaClass, oldLogSet, newLogSet);
                }
                break;
            case "big_decimal":
            case "int":
                numeric_type(javaClass, oldLogSet, newLogSet);
                break;
            case "boolean":
                boolean_type(javaClass, oldLogSet, newLogSet);
                break;
            case "LocalDateTime":
            case "LocalDate":
                localdatetime_type(javaClass, oldLogSet, newLogSet);
                break;
            case "list":
                list_type(javaClass, oldLogSet, newLogSet);
        }
    }

    public static void removeFilterDef(JavaClassSource javaClass, final String filterName) {
        List<AnnotationSource<JavaClassSource>> classAn = javaClass.getAnnotations();
        for (AnnotationSource<JavaClassSource> f : classAn) {
            if (f.getName().startsWith("FilterDef") || f.getName().startsWith("Filter")) {
                String name = f.getLiteralValue("name");
                if (name != null) {
                    name = StringUtil.removeQuotes(name);
                    if (name.equals(filterName)) {
                        javaClass.removeAnnotation(f);
                    }
                }
            }
        }
    }

    private void logFilterDef(JavaClassSource javaClass, final String filterName, Set<String> oldLogSet, Set<String> newLogSet) {
        removeFilterDef(javaClass, filterName);
        if (oldLogSet.contains(filterName))
            oldLogSet.remove(filterName);
        newLogSet.add(filterName);
    }

    private void list_type(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        String nameInPlural = null;
        if (name.contains("_")) {
            nameInPlural = StringUtil.getPlural(name.substring(name.indexOf("_") + 1));
            nameInPlural = name.substring(0, name.indexOf("_")+1) + nameInPlural;
        } else {
            nameInPlural = StringUtil.getPlural(name);
        }
        String filterName = prefix + "." + nameInPlural;
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
        if ("equals".equals(condition)) {
            filterAnnotation.setStringValue("condition", String.format("%s IN (:%s)", name, nameInPlural));
        } else {
            filterAnnotation.setStringValue("condition", condition);
        }
    }

    private void numeric_type(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        String filterName = prefix + "." + name;
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
        String operator = null;
        switch (condition) {
            case "lt":
                operator = "<";
                break;
            case "lte":
                operator = "<=";
                break;
            case "gt":
                operator = ">";
                break;
            case "gte":
                operator = ">=";
                break;
            case "equals":
                operator = "=";
                break;
        }
        filterAnnotation.setStringValue("condition", String.format("%s %s :%s", name, operator, name));
    }

    private void boolean_type(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        String filterName = prefix + "." + name;
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

    private void string_type(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        String filterName = prefix + "." + name;
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

    private void stringnot_type(JavaClassSource javaClass, String prefix, Set<String> oldLogSet, Set<String> newLogSet) {
        String filterName = prefix + "." + name;
        logFilterDef(javaClass, filterName, oldLogSet, newLogSet);
        AnnotationSource<JavaClassSource> filterDefAnnotation = javaClass.addAnnotation();
        filterDefAnnotation.setName("FilterDef");
        filterDefAnnotation.setStringValue("name", filterName);

        AnnotationSource<JavaClassSource> filterAnnotation = javaClass.addAnnotation();
        filterAnnotation.setName("Filter");
        filterAnnotation.setStringValue("name", filterName);
        filterAnnotation.setStringValue("condition", String.format("%s IS NULL", name));
    }

    private void stringlike_type(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
        String filterName = "like." + name;
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

    private void localdatetime_type(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet) {
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


}
