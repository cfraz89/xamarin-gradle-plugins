package au.org.trogdor.xamarin.lib

import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XBuildTask extends DefaultTask {
    XBuildProject xamarinProject
	XamarinConfiguration configuration

	def generateCommand(XamarinConfiguration config) {
		return []
	}

	@TaskAction
	def executeTask() {
        executeForConfiguration(configuration)
	}

    def executeForConfiguration(XamarinConfiguration config) {
        project.files(xamarinProject.projectFile, xamarinProject.solutionFile).each {
            if (!it.exists()) {
                throw new ProjectConfigurationException("Project file location $it does not exist!", null)
                return
            }
        }
        project.exec { commandLine generateCommand(config) }
    }
}

class XBuildCompileTask extends XBuildTask {
	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.paths.xbuild, xamarinProject.projectFile, "/p:Configuration=${config.name}", "/p:SolutionDir=${xamarinProject.solutionDir}", "/p:ProjectDir=${xamarinProject.projectDir}", '/t:Build']
	}
}


class XBuildAndroidPackageTask extends XBuildTask {
	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.paths.xbuild, xamarinProject.projectFile, "/p:Configuration=${config.name}", "/p:SolutionDir=${xamarinProject.solutionDir}", "/p:ProjectDir=${xamarinProject.projectDir}", '/t:PackageForAndroid']
	}
}

class XBuildCleanTask extends XBuildTask {
    @TaskAction
    def executeTask() {
        xamarinProject.configurationContainer.all() { config ->
            executeForConfiguration(config)
        }
        println "Deleting dependencies"
        project.delete(project.fileTree(dir:xamarinProject.dependencyDir, include: '*'))
    }

	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.paths.xbuild, xamarinProject.projectFile, "/p:Configuration=${config.name}", "/p:SolutionDir=${xamarinProject.solutionDir}", "/p:ProjectDir=${xamarinProject.projectDir}", '/t:Clean']
	}
}