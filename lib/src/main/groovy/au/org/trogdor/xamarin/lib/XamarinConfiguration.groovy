package au.org.trogdor.xamarin.lib

import groovy.transform.InheritConstructors
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException
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

    def getSourceFiles()
    {
        project.fileTree(dir:xPrj.sourceDir, include: ['**/*.cs',
                                                       '**/*.csproj',
                                                       '**/*.xml',
                                                       '**/*.axml',
                                                       '**/*.xib',
                                                       '**/*.storyboard',
                                                       '**/*.png',
                                                       '**/*.jpg',
                                                       '**/*.jpeg'],
        exclude: ['**/bin/**', '**/obj/**']).files
    }

    def dependOnReferences(Task task) {
        project.dependencies.xamarin.references.each {
            task.dependsOn("$it:build$name")
        }
    }
}

class XamarinSingleBuildConfiguration extends XamarinConfiguration {
    protected String mBuildOutput
    protected String buildExtension

    def XamarinSingleBuildConfiguration(String name, Project project, XamarinProject xamarinProject) {
        super(name, project, xamarinProject)
        buildExtension = "dll"
    }

    protected def resolveBuildOutput(String overrideOutput) {
        overrideOutput ?:  "${xPrj.projectDir}/bin/$name/${xPrj.resolvedProjectName}.$buildExtension"
    }

    def getResolvedBuildOutput() {
        resolveBuildOutput(null)
    }

    def makeTasks() {
        def taskName = "build${name}"
        def buildOutput = resolveBuildOutput(mBuildOutput)
        def task = project.task(taskName, description: "Build a Xamarin project using configuration ${name}", group: "Xamarin", dependsOn: "installDependencies${name}", type: xPrj.buildTask()) {
            xamarinProject = xPrj
            configuration = this
            inputs.files(sourceFiles)
            outputs.file(buildOutput)
        }
        project.tasks.buildAll.dependsOn(task)
        dependOnReferences(task)
    }

    def setBuildOutput(String fileName) {
        mBuildOutput = fileName
    }

    def buildOutput(String fileName) {
        mBuildOutput = fileName
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

    private String mResolvedIPhoneSimulatorOutput
    private String mResolvedIPhoneOutput

    def makeTasks() {
        mResolvedIPhoneSimulatorOutput = resolveBuildOutput(mIPhoneSimulatorOutput, 'iPhoneSimulator')
        mResolvedIPhoneOutput = resolveBuildOutput(mIPhoneOutput, 'iPhone')

        def iPhoneSimulatorTask = project.task("build${name}iPhoneSimulator", description: "Build a Xamarin project using configuration ${name} for the iPhoneSimulator target", group: "Xamarin", dependsOn: "installDependencies${name}", type: xPrj.buildTask()) {
            xamarinProject = xPrj
            configuration = this
            device = "iPhoneSimulator"
            inputs.dir(sourceFiles)
            outputs.dir(mResolvedIPhoneSimulatorOutput)
        }
        def iPhoneTask = project.task("build${name}iPhone", description: "Build a Xamarin project using configuration ${name} for the iPhone target", group: "Xamarin", dependsOn: "installDependencies${name}", type: xPrj.buildTask()) {
            xamarinProject = xPrj
            configuration = this
            device = "iPhone"
            inputs.dir(sourceFiles)
            outputs.dir(mResolvedIPhoneOutput)
        }

        def buildTask = project.task("build${name}", description: "Build a Xamarin project using configuration ${name}", group: "Xamarin", dependsOn: [iPhoneSimulatorTask, iPhoneTask])
        project.tasks.buildAll.dependsOn(buildTask)
        dependOnReferences(iPhoneSimulatorTask)
        dependOnReferences(iPhoneTask)
    }

    protected def resolveBuildOutput(String overrideOutput, String device) {
        overrideOutput ?: "${xPrj.projectDir}/bin/$device/$name/${xPrj.resolvedProjectName}.app"
    }

    def getResolvedIPhoneSimulatorBuildOutput() {
        mResolvedIPhoneSimulatorOutput
    }

    def setIPhoneSimulatorBuildOutput(String output) {
        mIPhoneSimulatorOutput = output
    }

    def iPhoneSimulatorBuildOutput(String output) {
        mIPhoneSimulatorOutput = output
    }

    def getResolvedIPhoneBuildOutput() {
        mResolvedIPhoneOutput
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

