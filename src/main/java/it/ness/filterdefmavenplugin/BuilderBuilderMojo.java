package it.ness.filterdefmavenplugin;

import java.io.File;
import java.io.FileWriter;
import java.util.*;

import it.ness.filterdefmavenplugin.annotations.CodeBuilderLog;
import it.ness.filterdefmavenplugin.model.*;
import it.ness.filterdefmavenplugin.templates.*;
import it.ness.filterdefmavenplugin.model.FilterDef;
import it.ness.filterdefmavenplugin.templates.FreeMarkerTemplates;
import it.ness.filterdefmavenplugin.util.GetSearchMethod;
import it.ness.filterdefmavenplugin.util.ModelFiles;
import it.ness.filterdefmavenplugin.util.StringUtil;
import it.ness.filterdefmavenplugin.model.FilterDefBase;
import it.ness.filterdefmavenplugin.model.ListFilterDef;
import it.ness.filterdefmavenplugin.model.LocalDateTimeFilterDef;
import org.apache.maven.plugin.logging.Log;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.*;

/**
 * @goal generate-sources
 * @phase codebuilder
 */
public class BuilderBuilderMojo extends AbstractCodeGeneratorMojo {

    protected Log log;
    protected StringUtil stringUtil;

    @Override
    public void generate(File outputDirectory, ModelFiles mf, String groupId, boolean removeAnnotations) throws Exception {
        log = getLog();
        stringUtil = new StringUtil(log);

        final String[] modelFiles = mf.getModelFileNames();
        final String path = mf.getPath();

        for (String modelFileName : modelFiles) {
            String className = stringUtil.getClassNameFromFileName(modelFileName);
            Set<FilterDefBase> filterDefSet = mf.getFilterDef(className);
            Set<String> logSet = mf.getCodeBuilderLog(className);
            JavaClassSource javaClass = null;
            try {
                javaClass = Roaster.parse(JavaClassSource.class, new File(path, modelFileName));
                if (filterDefSet.size() > 0) {
                    log.debug("createModel for class : " + className);
                    createModel(filterDefSet, logSet, javaClass, modelFileName, outputDirectory.getAbsolutePath(), removeAnnotations);
                    String orderBy = mf.getDefaultOrderBy(className);
                    String rsPath = mf.getRsPath(className);
                    createRsService(filterDefSet, className, groupId, orderBy, rsPath, outputDirectory.getAbsolutePath());
                }
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    private void createModel(Collection<FilterDefBase> filterDefSet, Set<String> oldLogSet, JavaClassSource javaClass, String modelFileName, String
            outputDirectory, boolean removeAnnotations) throws Exception {
        for (Import i : javaClass.getImports()) {
            if (i.getPackage().contains("it.ness.codebuilder"))
                if (removeAnnotations)
                    javaClass.removeImport(i);
        }

        javaClass.addImport("org.hibernate.annotations.Filter");
        javaClass.addImport("org.hibernate.annotations.FilterDef");
        javaClass.addImport("org.hibernate.annotations.ParamDef");

        Set<String> newLogSet = new HashSet<>();
        for (FilterDefBase fd : filterDefSet) {
            fd.addAnnotationToModelClass(javaClass, oldLogSet, newLogSet);
        }
        // add the new log
        removeCodeBuilderLog(javaClass);
        AnnotationSource<JavaClassSource> codeBuilderLogAnnotation = javaClass.addAnnotation(CodeBuilderLog.class);
        codeBuilderLogAnnotation.setStringValue("log", String.join(",", newLogSet));
        //remove old log filterDefs
        for (String filterName : oldLogSet) {
            FilterDef.removeFilterDef(javaClass, filterName);
        }

        String packagePath = getPathFromPackage(javaClass.getPackage());
        File pd = new File(outputDirectory, packagePath);
        pd.mkdirs();


        FileWriter out = new FileWriter(new File(pd, modelFileName));
        try {
            out.append(javaClass.toString());
        } finally {
            out.flush();
            out.close();
        }
    }

    private void createRsService(Collection<FilterDefBase> fd, String modelName, String groupId, String
            orderBy, String rsPath, String outputDirectory) throws Exception {

        final Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("packageName", groupId);
        data.put("apiPackageName", groupId.substring(0, groupId.lastIndexOf('.')));
        data.put("modelName", modelName);
        data.put("rsPath", rsPath);
        data.put("defaultSort", orderBy);

        String serviceRsClass = FreeMarkerTemplates.processTemplate("servicers", data);
        JavaClassSource javaClassTemplate = Roaster.parse(JavaClassSource.class, serviceRsClass);
        GetSearchMethod getSearchMethod = new GetSearchMethod(log, fd, modelName);
        addImportsToClass(javaClassTemplate, fd);
        MethodSource<JavaClassSource> templateMethod = getMethodByName(javaClassTemplate, "getSearch");
        templateMethod.setBody(getSearchMethod.create());

        String packagePath = getPathFromPackage(javaClassTemplate.getPackage());
        File pd = new File(outputDirectory, packagePath);
        File filePath = new File(pd, modelName + "ServiceRs.java");
        if (filePath.exists()) {
            JavaClassSource javaClassOriginal = Roaster.parse(JavaClassSource.class, filePath);;
            // add imports to original
            addImportsToClass(javaClassOriginal, fd);
            MethodSource<JavaClassSource> method = getMethodByName(javaClassOriginal, "getSearch");
            if (method != null) {
                method.setBody(templateMethod.getBody());
            } else {
                javaClassOriginal.addMethod(templateMethod);
            }
            try (FileWriter out = new FileWriter(filePath)) {
                out.append(javaClassOriginal.toString());
            }
        } else {
            pd.mkdirs();
            try (FileWriter out = new FileWriter(filePath)) {
                out.append(javaClassTemplate.toString());
            }
        }
    }

    private void addImportsToClass(JavaClassSource javaClassSource, Collection<FilterDefBase> fd) {
        for (FilterDefBase f : fd) {
            if (f instanceof LocalDateTimeFilterDef) {
                javaClassSource.addImport("java.time.LocalDateTime");
            }
            if (f instanceof ListFilterDef) {
                javaClassSource.addImport("org.hibernate.Session");
            }
        }
    }

    private String getPathFromPackage(String packageName) {
        return packageName.replace(".", "/");
    }


    private MethodSource<JavaClassSource> getMethodByName(JavaClassSource javaClassSource, String name) {
        for (MethodSource<JavaClassSource> method : javaClassSource.getMethods()) {
            if (name.equals(method.getName())) {
                return method;
            }
        }
        return null;
    }

    private void removeCodeBuilderLog(JavaClassSource javaClass) {
        List<AnnotationSource<JavaClassSource>> classAn = javaClass.getAnnotations();
        for (AnnotationSource<JavaClassSource> f : classAn) {
            if (f.getName().startsWith("CodeBuilderLog")) {
                javaClass.removeAnnotation(f);
            }
        }
    }
}
