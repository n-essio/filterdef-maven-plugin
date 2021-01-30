package it.ness.codebuilder.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import it.ness.codebuilder.model.*;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.AnnotationSource;
import org.jboss.forge.roaster.model.source.FieldSource;
import org.jboss.forge.roaster.model.source.JavaClassSource;

public class ModelFiles {

    protected Log log;
    protected StringUtil stringUtil;

    private String[] modelFileNames;
    private String path;

    private Map<String, LinkedHashSet<FilterDefBase>> filterDefMap = new LinkedHashMap<>();
    private Map<String, String> applicationContantsMap = new LinkedHashMap<>();
    private Map<String, String> rsPathMap = new LinkedHashMap<>();
    private Map<String, String> defaultOrderByMap = new LinkedHashMap<>();
    private Map<String, LinkedHashSet<String>> codeBuilderLog = new LinkedHashMap<>();

    public ModelFiles(Log log, String groupId) {
        this.log = log;
        stringUtil = new StringUtil(log);

        path = "src/main/java/";
        path += groupId.replaceAll("\\.", "/");
        path += "/model/";

        log.info("path = " + path);

        File f = new File(path);
        if (!f.exists()) {
            log.error(String.format("Path %s doesn't exist.", path));
            return;
        }

        modelFileNames = f.list(new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.endsWith(".java");
            }
        });

        log.info("Total model classes found : " + modelFileNames.length);
        log.info("model class file names : " + Arrays.toString(modelFileNames));
        resolveConstant();
        resolveFilterDefs();
    }

    public String getPath() {
        return path;
    }

    public Set<FilterDefBase> getFilterDef(final String className) {
        return filterDefMap.get(className);
    }

    public Set<String> getCodeBuilderLog(final String className) {
        return codeBuilderLog.get(className);
    }

    public String[] getModelFileNames() {
        return modelFileNames;
    }

    public String getRsPath(final String className) {
        return rsPathMap.get(className);
    }

    public String getAppConstant(final String className) {
        return applicationContantsMap.get(className);
    }

    public String getDefaultOrderBy(final String className) {
        return defaultOrderByMap.get(className);
    }

    private void resolveConstant() {
        for (String fileName : modelFileNames) {
            String className = stringUtil.getClassNameFromFileName(fileName);
            String constant = stringUtil.getConstantNameFromFileName(fileName);
            String rsPath = stringUtil.getRsPathFromConstantName(constant);
            String orderBy = "not_set";
            LinkedHashSet<String> logSet = new LinkedHashSet<>();
            // override rspath if annotation is present
            JavaClassSource javaClass = null;
            try {
                javaClass = Roaster.parse(JavaClassSource.class, new File(path, fileName));
                AnnotationSource<JavaClassSource> a = javaClass.getAnnotation("CodeBuilderRsPath");
                if (null != a) {
                    rsPath = a.getLiteralValue("path");
                    rsPath = StringUtil.removeQuotes(rsPath);
                }
                else {
                    rsPath = "NOT_SET";
                }
                a = javaClass.getAnnotation("CodeBuilderLog");
                if (null != a) {
                    String logvalue = a.getLiteralValue("log");
                    logvalue = StringUtil.removeQuotes(logvalue);
                    for (String l : logvalue.split(",")) {
                        logSet.add(l);
                    }
                }
                a = javaClass.getAnnotation("CodeBuilderDefaultOrderBy");
                if (null != a) {
                    orderBy = a.getLiteralValue("orderBy");
                    orderBy = StringUtil.removeQuotes(orderBy);
                }
            } catch (Exception e) {
                log.error(e);
            }
            applicationContantsMap.put(className, constant);
            rsPathMap.put(className, rsPath);
            defaultOrderByMap.put(className, orderBy);
            codeBuilderLog.put(className, logSet);
            log.debug(String.format("api constant for class %s : %s", className, constant));
            log.debug(String.format("rsPath for class %s : %s", className, rsPath));
        }
    }

    private void resolveFilterDefs() {
        Set<String> resolvedModels = new HashSet<>();
        // add all filterdef bases
        Set<FilterDefBase> filterDefBases = new HashSet<>();
        filterDefBases.add(new BooleanFilterDef(log));
        filterDefBases.add(new LikeStringFilterDef(log));
        filterDefBases.add(new ListFilterDef(log));
        filterDefBases.add(new LocalDateTimeFilterDef(log));
        filterDefBases.add(new LogicalDeleteFilterDef(log));
        filterDefBases.add(new NotNullStringFilterDef(log));
        filterDefBases.add(new NullBooleanFilterDef(log));
        filterDefBases.add(new NullStringFilterDef(log));
        filterDefBases.add(new StringFilterDef(log));

        // loop while in the resolvedModels all modelFile are resolved
        while (resolvedModels.size() < modelFileNames.length) {
            for (String fileName : modelFileNames) {
                final String modelName = stringUtil.getClassNameFromFileName(fileName);
                // if this class is not resolved, parse it
                if (!resolvedModels.contains(modelName)) {
                    JavaClassSource javaClass = null;
                    try {
                        log.info("Parsing : " + fileName);
                        javaClass = Roaster.parse(JavaClassSource.class, new File(path, fileName));
                        String superClassName = javaClass.getSuperType();
                        if (superClassName.contains(".")) {
                            superClassName = superClassName.substring(superClassName.lastIndexOf('.') + 1);
                        }
                        // if the superclass contains PanacheEntity or is resolved, continue with parsing filterdef
                        if (superClassName.contains("PanacheEntity") || resolvedModels.contains(superClassName)) {
                            LinkedHashSet<FilterDefBase> set = new LinkedHashSet<>();
                            if (resolvedModels.contains(superClassName)) {
                                // inherit all filterdefs
                                set.addAll(filterDefMap.get(superClassName));
                            }
                            List<FieldSource<JavaClassSource>> fields = javaClass.getFields();
                            for (FieldSource<JavaClassSource> f : fields) {
                                if (f.getName().endsWith("_uuid")) {
                                    long count = f.getName().chars().filter(ch -> ch == '_').count();
                                    if (count > 1) {
                                        log.warn(String.format("%s should have one underscore, because it ends with _uuid", f.getName()));
                                    }
                                }
                                for (FilterDefBase fdbase : filterDefBases) {
                                    FilterDefBase fd = fdbase.parseCodeBuilderFilterDef(f);
                                    if (null != fd) {
                                        if (set.contains(fd)) {
                                            log.info("Override of filterdef "+ fd.toString());
                                            set.remove(fd);
                                        }
                                        set.add(fd);
                                    }
                                }
                            }
                            // print log info
                            for (FilterDefBase fd : set) {
                                log.info(String.format("class %s extends %s: %s", modelName, superClassName, fd.toString()));
                            }
                            filterDefMap.put(modelName, set);
                            resolvedModels.add(modelName);
                        }
                        else {
                            log.error(String.format("class %s should extend PanacheEntityBase", modelName));
                        }
                    } catch (Exception e) {
                        log.error(e);
                    }
                }
            }
        }
    }
}
