package au.org.trogdor.xamarin.lib

import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Configuration

/**
 * Created by chrisfraser on 3/06/2014.
 */
class DependencyFetchTask extends DefaultTask {
    private XamarinProject xProj
    private Configuration mConfiguration

    def setConfiguration(Configuration c) {
        mConfiguration = c
        inputs.source(mConfiguration)
        outputs.dir(xProj.dependencyDir)
        outputs.upToDateWhen { false }
    }

    def setXamarinProject(XamarinProject xp) {
        this.xProj = xp
    }

    def getXamarinProject() {
        this.xProj
    }

    def getConfiguration() {
        mConfiguration
    }

    @TaskAction
    def fetch() {
        configuration.resolvedConfiguration.resolvedArtifacts.each() {artifact->
            def newName = "${artifact.name}.${artifact.extension}"
            println "${xamarinProject.dependencyDir}/${newName}"
            project.copy {
                from artifact.file
                into xamarinProject.dependencyDir
                rename { newName }
            }
        }
    }
}
