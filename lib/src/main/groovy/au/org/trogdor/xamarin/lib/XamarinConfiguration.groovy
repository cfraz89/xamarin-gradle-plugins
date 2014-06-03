package au.org.trogdor.xamarin.lib

import groovy.transform.InheritConstructors
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by chrisfraser on 30/05/2014.
 */
class XamarinConfiguration {
    final String name
    final Project project
    final XamarinProject xPrj
    private String buildOutput

    def XamarinConfiguration(String name, Project project, XamarinProject xamarinProject) {
        this.name = name
        this.project = project
        this.xPrj = xamarinProject
    }

    protected def setTaskOutput(Task task, String output) {
        if (!output && xPrj.projectName)
            output = "bin/${name}/${xPrj.projectName}.dll"
        if (output) {
            task.outputs.file(output)
            task.outputs.upToDateWhen() { false }
        }
        return output
    }

    protected def getTaskName() {
        name.replaceAll(~/\|/, "")
    }

    def build(String name) {
        def task = project.task("xamarinBuild-${taskName}", description: "Build a Xamarin project using configuration ${name}", group: "Xamarin", dependsOn: "fetchXamarinDependencies", type: xPrj.buildTask()) {
            xamarinProject = xPrj
            configuration = this
        }
        buildOutput = setTaskOutput(task, name)
    }

    def getBuildOutput() {
        return buildOutput
    }
}

@InheritConstructors
class AndroidConfiguration extends XamarinConfiguration {
    private String packageOutput

    def androidPackage(String output) {
        def task = project.task("xamarinPackage-${taskName}", description: "Build a Xamarin project using configuration ${name}", group: "Xamarin", dependsOn: "fetchXamarinDependencies", type: XBuildAndroidPackageTask) {
            xamarinProject = xPrj
            configuration = this
        }
        packageOutput = setTaskOutput(task, output)
    }

    def getPackageOutput() {
        return packageOutput;
    }
}