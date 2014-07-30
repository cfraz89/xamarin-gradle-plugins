package au.org.trogdor.xamarin.lib.xpkg

import au.org.trogdor.xamarin.lib.PathContainer
import au.org.trogdor.xamarin.lib.XamarinProject
import org.gradle.api.DefaultTask
import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction

/**
 * Created by chrisfraser on 3/06/2014.
 */
class ComponentRestoreTask extends DefaultTask {
    static String COMPONENTS_DIR = 'Components'

    static String XC_RESTORE_COMMAND = 'restore'

    static String COMPONENT_REFERENCE = 'XamarinComponentReference'

    String solutionFile
    PathContainer paths

    def setSolution(String solution) {
        solutionFile = solution
        outputs.dir("$solutionDir/$COMPONENTS_DIR/")
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

        def cmdLine = paths.xamarinComponent.endsWith('.exe') ?  [paths.mono] : []
        cmdLine += [paths.xamarinComponent, XC_RESTORE_COMMAND, slnFile]
        project.exec {
            workingDir project.file(paths.xamarinComponent).parent
            commandLine cmdLine
        }

    }
}
