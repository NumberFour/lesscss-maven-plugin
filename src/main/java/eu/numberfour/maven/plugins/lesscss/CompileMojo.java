package eu.numberfour.maven.plugins.lesscss;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * <p>
 * Compiles LessCSS files as part of the standard Maven build process.
 * </p>
 * 
 * @author <a href="mailto:leonard.ehrenfried@web.de">Leonard Ehrenfried</a>
 * 
 * @goal compile
 * @requiresDependencyResolution compile
 * @description Compiles LessCSS files
 */
public class CompileMojo extends AbstractMojo {

    /**
     * The Maven project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;
    /**
     * Output directory to put compiled CSS files
     * 
     * @parameter default-value="${basedir}/target/"
     */
    private String outputDir;
    
    /**
     * Base directory of the file discovery process
     * 
     * @parameter default-value="${basedir}/src/main/less/"
     */
    public String sourceDir;
    /**
     * Ant-style include pattern.
     * 
     * For example **.* is all files, defaults to **\/*.less
     * 
     * @parameter
     */
    public String[] includes;
    /**
     * Ant-style exclude pattern.
     * 
     * For example **.* is all files
     * 
     * @parameter
     */
    public String[] excludes;
    /**
     * Whether to ignore case
     * 
     * @parameter
     */
    public boolean caseSensitive;

    /**
     * Encoding to use for output file.
     * Defaults to "project.build.sourceEncoding" or platform encoding if not specified.
     *
     * @parameter default-value="${project.build.sourceEncoding}"
     */
    private String encoding;

    public void execute() throws MojoExecutionException, MojoFailureException {
        if(includes == null){
            includes = new String[1];
            includes[0]="**/*.less";
        }

        Charset charset = Charset.defaultCharset();
        if(encoding != null) {
            charset = Charset.forName(encoding);
        }

        Log log = getLog();
        log.info("");
        log.info("Creating file list");
        log.info("Source dir:   " + sourceDir);
        log.info("Output dir:   " + outputDir);
        log.info("Includes:     " + Arrays.toString(includes));
        log.info("Excludes:     " + Arrays.toString(excludes));
        log.info("Using '" + charset.displayName() + "' encoding for output file(s)");

        DirectoryScanner scanner = new DirectoryScanner();
        scanner.setBasedir(sourceDir);
        scanner.setIncludes(includes);
        scanner.setExcludes(excludes);
        scanner.setCaseSensitive(caseSensitive);
        scanner.scan();

        String[] includedFiles = scanner.getIncludedFiles();

        log.info("Found " + includedFiles.length + " less files.");
        log.info("");

        LessEngine engine = new LessEngine();

        for (String i:includedFiles){
            File inputFile = new File(sourceDir+i);
            File outputFile = new File(outputDir+'/'+i.replaceAll(".less$", ".css"));

            File outputDir_ = new File(outputFile.getParent());
            if(!outputDir_.exists()){
                outputDir_.mkdirs();
            }

            if(log.isDebugEnabled()){
                log.debug("Compiling "+inputFile.toString() +" to "+outputFile.toString());
            }
            else{
                log.info("Compiling "+i);
            }

            BufferedWriter bw = null;
            try {
                String output = engine.compile(inputFile);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }
                bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), charset));
                bw.write(output);
            } catch (IOException ex) {
                throw new MojoFailureException(ex, "IO Exception", "IO Exception while compiling less files.");
            } catch (LessException ex) {
                throw new MojoFailureException(ex, ex.getMessage(), ex.getMessage());
            } finally {
                try {
                  bw.close();
                } catch (IOException ex2) {
                    throw new MojoFailureException(ex2, "IO Exception", "IO Exception while closing output stream.");
                }
            }
        }
    }
}
