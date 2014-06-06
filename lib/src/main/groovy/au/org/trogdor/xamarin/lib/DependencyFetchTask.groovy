package au.org.trogdor.xamarin.lib

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Configuration

/**
 * Created by chrisfraser on 3/06/2014.
 */
class DependencyFetchTask extends DefaultTask {
    String libDir
    private Configuration mConfiguration

    def setConfiguration(Configuration c) {
        mConfiguration = c

        inputs.source(mConfiguration)
        outputs.dir(libDir)
        outputs.upToDateWhen {
            false
        }
    }

    @TaskAction
    def fetch() {
        mConfiguration.resolvedConfiguration.resolvedArtifacts.each() {artifact->
            project.copy {
                from artifact.file
                into (libDir)
                rename { "${artifact.name}.${artifact.extension}" }
            }
        }
    }
}
