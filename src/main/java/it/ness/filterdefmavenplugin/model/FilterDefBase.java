package it.ness.filterdefmavenplugin.model;

import it.ness.filterdefmavenplugin.annotations.CodeBuilderOption;
import it.ness.filterdefmavenplugin.util.StringUtil;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.apache.maven.plugin.logging.Log;

public abstract class FilterDefBase<T extends FilterDefBase> implements Comparable<FilterDefBase> {

    protected Log log;

    public String prefix;
    public String name;
    public String type;
    public String condition;
    public String filterName;
    public CodeBuilderOption[] options;

    public FilterDefBase(final Log log) {
        this.log = log;
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
        FilterDefBase filterDef = (FilterDefBase) o;
        return prefix.equals(filterDef.prefix) && name.equals(filterDef.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prefix, name);
    }

    public String toString() {
        return String.format("FilterDef={prefix=%s, name=%s, type=%s, condition=%s}", prefix, name, type, condition);
    }

    @Override
    public int compareTo(FilterDefBase filterDef) {
        return this.name.compareTo(filterDef.name);
    }

    public abstract void addAnnotationToModelClass(JavaClassSource javaClass, Set<String> oldLogSet, Set<String> newLogSet);
    public abstract T parseCodeBuilderFilterDef(FieldSource<JavaClassSource> f);
    public abstract String getSearchMethod();

    private void removeFilterDef(JavaClassSource javaClass, final String filterName) {
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

    protected void logFilterDef(JavaClassSource javaClass, final String filterName, Set<String> oldLogSet, Set<String> newLogSet) {
        removeFilterDef(javaClass, filterName);
        if (oldLogSet.contains(filterName))
            oldLogSet.remove(filterName);
        newLogSet.add(filterName);
    }


}
