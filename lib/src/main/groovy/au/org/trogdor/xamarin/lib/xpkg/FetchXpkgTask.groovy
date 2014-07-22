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
    static String DEFAULT_XC_RELATIVE_PATH = '.xpkg/xamarin-component'
    static String XPKG_URL = 'https://components.xamarin.com/submit/xpkg'

    String solutionFile
    PathContainer paths

    def setSolution(String solution) {
        solutionFile = solution
        onlyIf { !project.file(resolvedXCPath).exists() }
        outputs.file(resolvedXCPath)
    }

    def getSolutionDir() {
        project.file(solutionFile).parent
    }

    def getResolvedXCPath() {
        paths.xamarinComponent ?: "$solutionDir/$DEFAULT_XC_RELATIVE_PATH"
    }

    @TaskAction
    def fetch() {
        def xcFile = project.file(resolvedXCPath)
        def xpkgDir = xcFile.parentFile
        xpkgDir.mkdirs()
        def xpkgFile = project.file("$xpkgDir/xpkg.zip")
        println 'No xpkg in solution, downloading...'
        new URL(XPKG_URL).withInputStream { i ->
            xpkgFile.withOutputStream { it << i }
        }
        project.copy {
            from project.zipTree(xpkgFile)
            into xpkgDir
        }
    }
}
