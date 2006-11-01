package org.codehaus.mojo.appassembler;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.codehaus.mojo.appassembler.daemon.DaemonGeneratorException;
import org.codehaus.mojo.appassembler.daemon.DaemonGeneratorService;

import java.io.File;
import java.util.Iterator;
import java.util.Set;

/**
 * @goal generate-daemons
 * @requiresDependencyResolution runtime
 * @phase generate-resources
 *
 * @author <a href="mailto:trygve.laugstol@objectware.no">Trygve Laugst&oslash;l</a>
 * @version $Id$
 */
public class GenerateDaemonsMojo
    extends AbstractMojo
{
    // -----------------------------------------------------------------------
    // Parameters
    // -----------------------------------------------------------------------

    /**
     * @parameter
     * @required
     */
    private Set daemons;

    /**
     * @parameter expression="${basedir}"
     */
    private File basedir;

    /**
     * @parameter expression="${project.build.directory}"
     */
    private File target;

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    // -----------------------------------------------------------------------
    // Read-only parameters
    // -----------------------------------------------------------------------

    /**
     * @readonly
     * @parameter expression="${localRepository}"
     */
    private ArtifactRepository localRepository;

    // -----------------------------------------------------------------------
    // Components
    // -----------------------------------------------------------------------

    /**
     * @component org.codehaus.mojo.appassembler.daemon.DaemonGeneratorService
     */
    private DaemonGeneratorService daemonGeneratorService;

    // -----------------------------------------------------------------------
    // AbstractMojo Implementation
    // -----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        for ( Iterator it = daemons.iterator(); it.hasNext(); )
        {
            Daemon daemon = (Daemon) it.next();

            File descriptor = new File( basedir, daemon.getDescriptor( ) );

            try
            {
                for ( Iterator it2 = daemon.getPlatforms().iterator(); it2.hasNext(); )
                {
                    String platform = (String) it2.next();

                    File output = new File( new File( target, "generated-resources" ), platform );

                    daemonGeneratorService.generateDaemon( platform, descriptor, output , project, localRepository);
                }
            }
            catch ( DaemonGeneratorException e )
            {
                throw new MojoExecutionException( "Error while generating daemon.", e );
            }
        }
    }
}