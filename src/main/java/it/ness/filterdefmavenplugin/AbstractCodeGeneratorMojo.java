package it.ness.filterdefmavenplugin;

import java.io.File;

import it.ness.filterdefmavenplugin.util.ModelFiles;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugin.logging.Log;

public abstract class AbstractCodeGeneratorMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     * @since 1.0
     */
    MavenProject project;

    /**
     * @parameter default-value="target/generated-sources/codebuilder"
     * @required
     */
    File outputDirectory;

    /**
     * @parameter default-value="true"
     * @required
     */
    boolean removeAnnotations;

    public void execute()
            throws MojoExecutionException, MojoFailureException {

        final String groupId = project.getGroupId();

        Log log = getLog();
        log.info(String.format("Begin generating sources for groupId {%s}", groupId));

        ModelFiles mf = new ModelFiles(log, groupId);

        try {
            generate(outputDirectory, mf, groupId, removeAnnotations);
        } catch (Exception e) {
            log.error(e);
        }

        log.info("Done generating sources");
    }

    protected abstract void generate(File outputDirectory, ModelFiles mf, String groupId, boolean removeAnnotations) throws Exception;


}
