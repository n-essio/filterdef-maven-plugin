package it.ness.filterdefmavenplugin.util;

import it.ness.filterdefmavenplugin.model.*;
import it.ness.filterdefmavenplugin.model.FilterDefBase;
import org.apache.maven.plugin.logging.Log;

import java.util.Collection;

public class GetSearchMethod {
    protected Log log;
    protected Collection<FilterDefBase> fd;
    protected String modelName;

    public GetSearchMethod(Log log, Collection<FilterDefBase> fd, String modelName) {
        this.log = log;
        this.fd = fd;
        this.modelName = modelName;
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
        for (FilterDefBase f : fd) {
            sb.append(f.getSearchMethod());
        }
        sb.append("return search;");
        return sb.toString();
    }

}
