package au.org.trogdor.xamarin.lib.nuget

import au.org.trogdor.xamarin.lib.PathContainer
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

/**
 * Created by chrisfraser on 3/06/2014.
 */
class FetchNugetTask extends DefaultTask {
    static String NUGET_EXE_URL = 'http://nuget.org/nuget.exe'
    PathContainer paths


    def setPaths(PathContainer paths) {
        this.paths = paths
        onlyIf { !project.file(paths.nuget).exists() }
        outputs.file(paths.nuget)
    }

    @TaskAction
    def fetch() {
        println 'No nuget in solution, downloading...'
        def nugetFile = project.file(paths.nuget)
        nugetFile.parentFile.mkdirs()
        new URL(NUGET_EXE_URL).withInputStream { i ->
            nugetFile.withOutputStream { it << i }
        }
    }
}
