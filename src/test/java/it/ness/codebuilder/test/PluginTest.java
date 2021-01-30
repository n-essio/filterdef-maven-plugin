package it.ness.codebuilder.test;

import it.ness.codebuilder.BuilderBuilderMojo;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class PluginTest {
/*
    String fileServiceRsResult = "test-data/generated-sources/it/ness/sample/service/rs/BlankDeliveryOperationServiceRs.java";
    String fileModelResult = "test-data/generated-sources/it/ness/sample/model/BlankDeliveryOperation.java";

    String fileModelAnnotatedResult = "test-data/generated-sources/it/ness/sample/model/BlankDeliveryOperationAnnotated.java";
    String fileServiceAnnotatedRsResult = "test-data/generated-sources/it/ness/sample/service/rs/BlankDeliveryOperationAnnotatedServiceRs.java";

    String fileModelAnnotatedAllResult = "test-data/generated-sources/it/ness/sample/model/AllOptions.java";
    String fileServiceAnnotatedAllRsResult = "test-data/generated-sources/it/ness/sample/service/rs/AllOptionsServiceRs.java";


    @Test
    public void fromModelTest() throws Exception {
        BuilderBuilderMojo builderBuilderMojo = new BuilderBuilderMojo();
        File outputDirectory = new File("test-data/generated-sources");
        String path = "test-data/java/it/ness/sample/model";
        String modelFileName = "BlankDeliveryOperation.java";
        boolean removeAnnotations = false;
        String groupId = "it.ness.sample";
        builderBuilderMojo.generate(outputDirectory, path, modelFileName, groupId, removeAnnotations);
        File fileModel = new File(fileModelResult);
        File fileServiceRs = new File(fileServiceRsResult);
        Assert.assertTrue(fileModel.exists());
        Assert.assertTrue(fileServiceRs.exists());
        if (!removeAnnotations) {
            String content = Files.readString(Paths.get(fileModelResult));
            Assert.assertTrue(content.contains("@CodeBuilderFilterDef"));
            Assert.assertTrue(content.contains("import it.ness.codebuilder.annotations"));
        }
    }

    @Test
    public void fromModelWithAnnotationsTest() throws Exception {
        BuilderBuilderMojo builderBuilderMojo = new BuilderBuilderMojo();
        File outputDirectory = new File("test-data/generated-sources");
        String path = "test-data/java/it/ness/sample/model";
        String modelFileName = "BlankDeliveryOperationAnnotated.java";
        boolean removeAnnotations = false;
        String groupId = "it.ness.sample";
        builderBuilderMojo.generate(outputDirectory, path, modelFileName, groupId, removeAnnotations);
        File fileModel = new File(fileModelAnnotatedResult);
        File fileServiceRs = new File(fileServiceAnnotatedRsResult);
        Assert.assertTrue(fileModel.exists());
        Assert.assertTrue(fileServiceRs.exists());
        if (!removeAnnotations) {
            String content = Files.readString(Paths.get(fileModelAnnotatedResult));
            Assert.assertTrue(content.contains("@CodeBuilderFilterDef"));
            Assert.assertTrue(content.contains("import it.ness.codebuilder.annotations"));
        }
    }

    @Test
    public void fromModelAllTest() throws Exception {
        BuilderBuilderMojo builderBuilderMojo = new BuilderBuilderMojo();
        File outputDirectory = new File("test-data/generated-sources");
        String path = "test-data/java/it/ness/sample/model";
        String modelFileName = "AllOptions.java";
        boolean removeAnnotations = false;
        String groupId = "it.ness.sample";
        builderBuilderMojo.generate(outputDirectory, path, modelFileName, groupId, removeAnnotations);
        File fileModel = new File(fileModelAnnotatedAllResult);
        File fileServiceRs = new File(fileServiceAnnotatedAllRsResult);
        Assert.assertTrue(fileModel.exists());
        Assert.assertTrue(fileServiceRs.exists());
        if (!removeAnnotations) {
            String content = Files.readString(Paths.get(fileModelAnnotatedAllResult));
            Assert.assertTrue(content.contains("@CodeBuilderFilterDef"));
            Assert.assertTrue(content.contains("import it.ness.codebuilder.annotations"));
        }
    }

 */
}
