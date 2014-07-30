package au.org.trogdor.xamarin.lib

import org.gradle.api.DefaultTask
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction

/**
 * Created by chrisfraser on 21/07/2014.
 */
class XUnitConsoleTask extends DefaultTask {
    XBuildProject xamarinProject
    PathContainer paths
    XamarinConfiguration configuration

    protected def projectFilePath

    @TaskAction
    def runTest() {
        def testAssembly = project.file(configuration.resolvedBuildOutput)
        if (!testAssembly.exists())
            throw new ProjectConfigurationException("Test dll $testAssembly does not exist!", null)

        def cmdLine = paths.xunitConsole.endsWith('.exe') ?  [paths.mono] : []
        cmdLine += [paths.xunitConsole, testAssembly]

        project.exec {
            commandLine cmdLine
        }
    }
}
