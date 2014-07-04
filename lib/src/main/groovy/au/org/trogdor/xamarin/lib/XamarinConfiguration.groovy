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

    def XamarinConfiguration(String name, Project project, XamarinProject xamarinProject) {
        this.name = name
        this.project = project
        this.xPrj = xamarinProject
    }

    def makeTasks()
    {}

    protected setupTaskDependenciesFromProjectDependencies(Task task) {
        xPrj.referencedProjects.each { dependencyProjectName ->
            def dependencyProject = project.findProject(dependencyProjectName)
            def dependencyTask = dependencyProject.tasks.findByName(task.name)
            if (dependencyTask) {
                task.dependsOn(dependencyTask)
            }
        }
    }
}

class XamarinSingleBuildConfiguration extends XamarinConfiguration {
    protected String mBuildOutput
    protected String mResolvedBuildOutput
    protected String buildExtension

    def XamarinSingleBuildConfiguration(String name, Project project, XamarinProject xamarinProject) {
        super(name, project, xamarinProject)
        buildExtension = "dll"
    }

    protected def resolveBuildOutput(String overrideOutput) {
        String output
        if (!overrideOutput && xPrj.projectName)
            output = "bin/${name}/${xPrj.projectName}.${buildExtension}"
        else
            output = overrideOutput

        return output
    }

    protected def setTaskOutput(Task task, String output) {
        if (output) {
            task.outputs.file(output)
            task.outputs.upToDateWhen() { false }
        }
    }

    def makeTasks() {
        def taskName = "xamarinBuild-${name}"
        def task = project.task(taskName, description: "Build a Xamarin project using configuration ${name}", group: "Xamarin", dependsOn: "fetchXamarinDependencies${name}", type: xPrj.buildTask()) {
            xamarinProject = xPrj
            configuration = this
        }
        project.tasks.xamarinBuildAll.dependsOn(task)
        setTaskOutput(task, resolvedBuildOutput)
        setupTaskDependenciesFromProjectDependencies(task)

    }

    def setBuildOutput(String fileName) {
        mBuildOutput = fileName
    }

    def buildOutput(String fileName) {
        mBuildOutput = fileName
    }

    def getResolvedBuildOutput() {
        if (!mResolvedBuildOutput)
            mResolvedBuildOutput = resolveBuildOutput(mBuildOutput)
        mResolvedBuildOutput
    }
}

@InheritConstructors
class AndroidLibraryConfiguration extends XamarinSingleBuildConfiguration {
}

class AndroidAppConfiguration extends XamarinSingleBuildConfiguration {
    def AndroidAppConfiguration(String name, Project project, XamarinProject xamarinProject) {
        super(name, project, xamarinProject)
        buildExtension = "apk"
    }
}

@InheritConstructors
class iOSLibraryConfiguration extends XamarinSingleBuildConfiguration {
}

@InheritConstructors
class iOSAppConfiguration extends XamarinConfiguration {
    private String mIPhoneSimulatorOutput
    private String mIPhoneOutput

    private String resolvedIPhoneSimulatorOutput
    private String resolvedIPhoneOutput

    def makeTasks() {
        resolvedIPhoneSimulatorOutput = resolveBuildOutput(mIPhoneSimulatorOutput, 'iPhoneSimulator')
        resolvedIPhoneOutput = resolveBuildOutput(mIPhoneOutput, 'iPhone')

        def iPhoneSimulatorTask = project.task("xamarinBuild-${name}-iPhoneSimulator", description: "Build a Xamarin project using configuration ${name} for the iPhoneSimulator target", group: "Xamarin", dependsOn: "fetchXamarinDependencies${name}", type: xPrj.buildTask()) {
            xamarinProject = xPrj
            configuration = this
            device = "iPhoneSimulator"
        }
        def iPhoneTask = project.task("xamarinBuild-${name}-iPhone", description: "Build a Xamarin project using configuration ${name} for the iPhone target", group: "Xamarin", dependsOn: "fetchXamarinDependencies${name}", type: xPrj.buildTask()) {
            xamarinProject = xPrj
            configuration = this
            device = "iPhone"
        }
        setTaskOutput(iPhoneSimulatorTask, resolvedIPhoneSimulatorOutput)
        setTaskOutput(iPhoneTask, resolvedIPhoneOutput)

        setupTaskDependenciesFromProjectDependencies(iPhoneSimulatorTask)
        setupTaskDependenciesFromProjectDependencies(iPhoneTask)

        def buildTask = project.task("xamarinBuild-${name}", description: "Build a Xamarin project using configuration ${name}", group: "Xamarin", dependsOn: [iPhoneSimulatorTask, iPhoneTask])
        project.tasks.xamarinBuildAll.dependsOn(buildTask)

        setupTaskDependenciesFromProjectDependencies(buildTask)

    }

    protected def resolveBuildOutput(String overrideOutput, String device) {
        String output
        if (!overrideOutput && xPrj.projectName)
            output = "bin/${device}/${name}/${xPrj.projectName}.dll"
        else
            output = overrideOutput

        return output
    }

    protected def setTaskOutput(Task task, String output) {
        if (output) {
            task.outputs.file(output)
            task.outputs.upToDateWhen() { false }
        }
    }

    def getResolvedIPhoneSimulatorBuildOutput() {
        resolvedIPhoneSimulatorOutput
    }

    def setIPhoneSimulatorBuildOutput(String output) {
        mIPhoneSimulatorOutput = output
    }

    def iPhoneSimulatorBuildOutput(String output) {
        mIPhoneSimulatorOutput = output
    }

    def getResolvedIPhoneBuildOutput() {
        resolvedIPhoneOutput
    }

    def setIPhoneBuildOutput(String output) {
        mIPhoneOutput = output
    }

    def iPhoneBuildOutput(String output) {
        mIPhoneOutput = output
    }
}


@InheritConstructors
class GenericLibraryConfiguration extends XamarinSingleBuildConfiguration {
}

class GenericAppConfiguration extends XamarinSingleBuildConfiguration {
    def GenericAppConfiguration(String name, Project project, XamarinProject xamarinProject) {
        super(name, project, xamarinProject)
        buildExtension = "exe"
    }
}

