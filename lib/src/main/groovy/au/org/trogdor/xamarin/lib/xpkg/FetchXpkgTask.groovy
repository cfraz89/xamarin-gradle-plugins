package au.org.trogdor.xamarin.lib.xpkg

import au.org.trogdor.xamarin.lib.PathContainer
import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.DefaultTask
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction

/**
 * Created by chrisfraser on 3/06/2014.
 */
class FetchXpkgTask extends DefaultTask {
    PathContainer paths

    def setPaths(PathContainer paths) {
        this.paths = paths
        onlyIf { !project.file(paths.xamarinComponent).exists() }
        outputs.file(paths.xamarinComponent)
    }

    @TaskAction
    def fetch() {
        def xcFile = project.file(paths.xamarinComponent)
        def xpkgDir = xcFile.parentFile
        xpkgDir.mkdirs()
        def xpkgFile = project.file("$xpkgDir/xpkg.zip")
        println 'No xpkg in solution, downloading...'
        new URL(paths.xpkgExeUrl).withInputStream { i ->
            xpkgFile.withOutputStream { it << i }
        }
        project.copy {
            from project.zipTree(xpkgFile)
            into xpkgDir
        }
    }
}
