package it.ness.filterdefmavenplugin.util;

import it.ness.filterdefmavenplugin.annotations.CodeBuilderOption;
import it.ness.filterdefmavenplugin.model.FilterDef;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;

public class GetSearchMethodOld {
    protected Log log;
    protected Collection<FilterDef> fd;
    public boolean importLocalDateTime = false;
    public boolean importLocalDate = false;
    protected String modelName;

    public GetSearchMethodOld(Log log, Collection<FilterDef> fd, String modelName) {
        this.log = log;
        this.fd = fd;
        this.modelName = modelName;
        importLocalDateTime =
                checkImportLocalDateTime();
        importLocalDate = checkImportLocalDate();
    }

    private String getString(FilterDef filterDef, String prefix) {
        if (filterDef.containsOption(CodeBuilderOption.EXECUTE_ALWAYS)) {
            return getStringEqualsAlways(filterDef, prefix);
        }
        if (filterDef.containsOption(CodeBuilderOption.WITHOUT_PARAMETERS)) {
            return getStringEqualsWithoutParameters(filterDef, prefix);
        }
        String formatBody = "if (nn(\"%s\")) {" +
                "search.filter(\"%s\", Parameters.with(\"%s\", get(\"%s\")));" +
                "}";
        String filterName = "obj." + filterDef.name;
        return String.format(formatBody, filterName, filterName, filterDef.name, filterName);
    }

    private String getBooleanCondition(FilterDef f, String prefix) {
        String formatBody = "search.filter(\"%s\", Parameters.with(\"%s\", %s));";
        String filterName = prefix + "." + f.name;
        return String.format(formatBody, filterName, f.name, f.condition);
    }


    private String getStringEqualsAlways(FilterDef f, String prefix) {
        String formatBody = "search.filter(\"%s\", Parameters.with(\"%s\", get(\"%s\")));";
        String filterName = "obj." + f.name;
        return String.format(formatBody, filterName, f.name, filterName);
    }

    private String getStringEqualsWithoutParameters(FilterDef filterDef, String prefix) {
        String formatBody = "if (nn(\"%s\")) {" +
                "search.filter(\"%s\");" +
                "}";
        String filterName = prefix + "." + filterDef.name;
        return String.format(formatBody, filterName, filterName);
    }

    private String getStringLike(FilterDef f) {
        String formatBody = "if (nn(\"%s\")) {" +
                "search.filter(\"%s\", Parameters.with(\"%s\", likeParamToLowerCase(\"%s\")));" +
                "}";
        String filterName = "like." + f.name;
        return String.format(formatBody, filterName, filterName, f.name, filterName);
    }

    private String getLocalDateTimeEquals(FilterDef f, String prefix) {
        String formatBody = "if (nn(\"%s\")) {" +
                "LocalDateTime date = LocalDateTime.parse(get(\"%s\"));" +
                "search.filter(\"%s\", Parameters.with(\"%s\", date));" +
                "}";
        String filterName = prefix + f.name;
        return String.format(formatBody, filterName, filterName, filterName, f.name);
    }

    private String getIntegerEquals(FilterDef f) {
        String formatBody = "if (nn(\"%s\")) {" +
                "Integer numberof = _integer(\"%s\");" +
                "search.filter(\"%s\", Parameters.with(\"%s\", numberof));" +
                "}";
        String filterName = "obj." + f.name;
        return String.format(formatBody, filterName, filterName, filterName, f.name);
    }

    private String getBigDecimalEquals(FilterDef f) {
        String formatBody = "if (nn(\"%s\")) {" +
                "BigDecimal numberof = new BigDecimal(get(\"%s\"));" +
                "search.filter(\"%s\", Parameters.with(\"%s\", numberof));" +
                "}";
        String filterName = "obj." + f.name;
        return String.format(formatBody, filterName, filterName, filterName, f.name);
    }

    private String getLocalDateEquals(FilterDef f, String prefix) {
        String formatBody = "if (nn(\"%s\")) {" +
                "LocalDate date = LocalDate.parse(get(\"%s\"));" +
                "search.filter(\"%s\", Parameters.with(\"%s\", date));" +
                "}";
        String filterName = prefix + f.name;
        return String.format(formatBody, filterName, filterName, filterName, f.name);
    }


    private String getQuery(String modelName) {
        // using null,  Panache will generate query in Hibernate way (ie : " from com.flower.User ")
        String formatBody = "PanacheQuery<%s> search; Sort sort = sort(orderBy);" +
                "if (sort != null) {" +
                "search = %s.find(null, sort);" +
                "} else {" +
                "search = %s.find(null);" +
                "}";

        return String.format(formatBody, modelName, modelName, modelName);
    }

    public String create() {
        StringBuilder sb = new StringBuilder();
        sb.append(getQuery(modelName));
        for (FilterDef f : fd) {
            switch (f.type) {
                default:
                case "string":
                    string_type(f, sb);
                    break;
                case "LocalDateTime":
                    localdatetime_type(f, sb);
                    break;
                case "LocalDate":
                    localdate_type(f, sb);
                    break;
                case "boolean":
                    boolean_type(f, sb);
                    break;
                case "big_decimal":
                    bigdecimal_type(f, sb);
                    break;
                case "int":
                    integer_type(f, sb);
                    break;
            }
        }
        sb.append("return search;");
        return sb.toString();
    }

    private void string_type(FilterDef filterDef, StringBuilder sb) {
        switch (filterDef.condition) {
            case "like":
                sb.append(getStringLike(filterDef));
                break;
            case "not":
                sb.append(getString(filterDef, filterDef.condition));
                break;
            default:
            case "equals":
                sb.append(getString(filterDef, "obj"));
                break;
        }
    }

    private void localdatetime_type(FilterDef filterDef, StringBuilder sb) {
        switch (filterDef.condition) {
            default:
            case "equals":
                sb.append(getLocalDateTimeEquals(filterDef, "from."));
                sb.append(getLocalDateTimeEquals(filterDef, "to."));
                break;
        }
    }

    private void localdate_type(FilterDef filterDef, StringBuilder sb) {
        switch (filterDef.condition) {
            default:
            case "equals":
                sb.append(getLocalDateEquals(filterDef, "from."));
                sb.append(getLocalDateEquals(filterDef, "to."));
                break;
        }
    }

    private void bigdecimal_type(FilterDef filterDef, StringBuilder sb) {
        switch (filterDef.condition) {
            default:
            case "equals":
                sb.append(getBigDecimalEquals(filterDef));
                break;
        }
    }

    private void integer_type(FilterDef filterDef, StringBuilder sb) {
        switch (filterDef.condition) {
            default:
            case "equals":
                sb.append(getIntegerEquals(filterDef));
                break;
        }
    }

    private void boolean_type(FilterDef filterDef, StringBuilder sb) {
        switch (filterDef.condition) {
            default:
            case "equals":
                sb.append(getBooleanCondition(filterDef, filterDef.condition));
                break;
        }
    }

    private boolean checkImportLocalDateTime() {
        for (FilterDef f : fd) {
            if ("LocalDateTime".equals(f.type)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkImportLocalDate() {
        for (FilterDef f : fd) {
            if ("LocalDate".equals(f.type)) {
                return true;
            }
        }
        return false;
    }
}
