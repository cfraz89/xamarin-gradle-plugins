package au.org.trogdor.xamarin.lib.nuget

import au.org.trogdor.xamarin.lib.PathContainer
import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.DefaultTask
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction

/**
 * Created by chrisfraser on 3/06/2014.
 */
class FetchNugetTask extends DefaultTask {
    static String DEFAULT_NUGET_RELATIVE_PATH = '.nuget/nuget.exe'
    static String NUGET_EXE_URL = 'http://nuget.org/nuget.exe'

    String solutionFile
    PathContainer paths

    def setSolution(String solution) {
        solutionFile = solution
        onlyIf { !project.file(resolvedNugetPath).exists() }
        outputs.file(resolvedNugetPath)
    }

    def getSolutionDir() {
        project.file(solutionFile).parent
    }

    def getResolvedNugetPath() {
        paths.nuget ?: "$solutionDir/$DEFAULT_NUGET_RELATIVE_PATH"
    }

    @TaskAction
    def fetch() {
        println 'No nuget in solution, downloading...'
        new URL(NUGET_EXE_URL).withInputStream { i ->
            project.file(resolvedNugetPath).withOutputStream { it << i }
        }
    }
}
