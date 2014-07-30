package au.org.trogdor.xamarin.lib.nuget

import au.org.trogdor.xamarin.lib.PathContainer
import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.Project
import org.gradle.api.DefaultTask
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.TaskExecutionException

/**
 * Created by chrisfraser on 3/06/2014.
 */
class NugetRestoreTask extends DefaultTask {
    static String NUGET_PACKAGES_DIR = 'packages'
    static String NUGET_RESTORE_COMMAND = 'restore'

    String solutionFile
    PathContainer paths

    def setSolution(String solution) {
        solutionFile = solution
        outputs.dir("$solutionDir/$NUGET_PACKAGES_DIR/")
    }

    def getSolutionDir() {
        project.file(solutionFile).parent
    }

    @TaskAction
    def restore() {
        def slnFile = project.file(solutionFile)
        if (!slnFile.exists()) {
            throw new ProjectConfigurationException("Solution file location $slnFile does not exist!", null)
            return
        }

        def cmdLine = paths.nuget.endsWith('.exe') ?  [paths.mono] : []
        cmdLine += [paths.nuget, NUGET_RESTORE_COMMAND, slnFile]
        project.exec { commandLine cmdLine }

    }
}
