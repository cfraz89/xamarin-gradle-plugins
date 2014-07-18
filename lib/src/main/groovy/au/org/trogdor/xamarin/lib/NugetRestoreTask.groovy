package au.org.trogdor.xamarin.lib

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
    static String DEFAULT_NUGET_RELATIVE_PATH = '.nuget/nuget.exe'
    static String NUGET_PACKAGES_DIR = 'packages'

    static String NUGET_CONFIG_FILE = 'packages.config'
    static String NUGET_EXE_URL = 'http://nuget.org/nuget.exe'
    static String NUGET_RESTORE_COMMAND = 'restore'

    XamarinProject xamarinProject
    PathContainer paths

    def setXamarinProject(XamarinProject prj) {
        xamarinProject = prj
        def nugetConfig = "$xamarinProject.projectDir/$NUGET_CONFIG_FILE"
        def configExists = project.file(nugetConfig).exists()
        println "$nugetConfig, $configExists"
        onlyIf { configExists }
        if (configExists) {
            inputs.file(nugetConfig)
            println "$xamarinProject.solutionDir/$NUGET_PACKAGES_DIR"
            outputs.dir("$xamarinProject.solutionDir/$NUGET_PACKAGES_DIR/")
        }
    }

    def getResolvedNugetPath() {
        if (!paths.nuget)
            return "$xamarinProject.solutionDir/$DEFAULT_NUGET_RELATIVE_PATH"
        else
            return paths.nuget
    }

    @TaskAction
    def restore() {
        def slnFile = project.file(xamarinProject.solutionFile)
        if (!slnFile.exists())
            throw new ProjectConfigurationException("Solution file location $slnFile does not exist!", null)

        def nugetBinary = ensureNugetBinary()
        assert nugetBinary.exists()

        def command =  "$paths.mono $resolvedNugetPath $NUGET_RESTORE_COMMAND $slnFile"
        def proc = command.execute()
        def serr = new ByteArrayOutputStream(4096)
        proc.waitForProcessOutput(System.out, serr)
        if(proc.exitValue())
            throw new TaskExecutionException(this, null)

    }

    def ensureNugetBinary() {
        def nugetFile = project.file(resolvedNugetPath)
        if (!nugetFile.exists()) {
            println 'No nuget in solution, downloading...'
            new URL(NUGET_EXE_URL).withInputStream { i ->
                nugetFile.withOutputStream { it << i }
            }
        }
        return nugetFile
    }
}
