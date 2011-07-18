package eu.numberfour.maven.plugins.lesscss;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.DirectoryScanner;

/**
 * <p>
 * Creates a JSON formatted list of files form a base directory, and optional 
 * includes and excludes.
 * </p>
 * 
 * @author <a href="mailto:leonard.ehrenfried@web.de">Leonard Ehrenfried</a>
 * 
 * @goal compile
 * @requiresDependencyResolution compile
 * @description Creates a JSON-formatted list of files
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
    public String baseDir;
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

    public void execute() throws MojoExecutionException, MojoFailureException {
        FileWriter fileWriter = null;
        try {
            
            if(includes == null){
                includes = new String[1];
                includes[0]="**/*.less";
            }
            
            Log log = getLog();
            log.info("");
            log.info("Creating file list ");
            log.info("Basedir:  " + baseDir);
            log.info("Output dir:   " + outputDir);
            log.info("Includes: " + Arrays.toString(includes));
            log.info("Excludes:  " + Arrays.toString(excludes));

            DirectoryScanner scanner = new DirectoryScanner();
            scanner.setBasedir(baseDir);
            scanner.setIncludes(includes);
            scanner.setExcludes(excludes);
            scanner.setCaseSensitive(caseSensitive);
            scanner.scan();

            String[] includedFiles = scanner.getIncludedFiles();
            
            log.info("Found " + includedFiles.length + " less files");
            
            // Instantiates a new LessEngine
            LessEngine engine = new LessEngine();
            
            for (String i:includedFiles){
                // Creates a new file containing the compiled content
                
                File inputFile = new File(baseDir+i);
                
                File outputFile = new File(outputDir+i);
                
                log.info("Compiling "+inputFile.toString() +" to "+outputFile.toString());
                
                engine.compile(inputFile, outputFile);
                
            }
            
            


        } catch (IOException ex) {
            throw new MojoFailureException("Could not write output file.");
        } catch (LessException ex) {
            throw new MojoFailureException("Could not write output file.");
        } finally {
            try {
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CompileMojo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
