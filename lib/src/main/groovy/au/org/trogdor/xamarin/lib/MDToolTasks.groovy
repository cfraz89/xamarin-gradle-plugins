package au.org.trogdor.xamarin.lib

import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class MDToolTask extends DefaultTask {
	XamarinProject xamarinProject
    XamarinConfiguration configuration
    String device

	def generateCommand() {
		return []
	}

    def getDeviceTag() {
        device ? "|" + device : ""
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

class MDToolCompileTask extends MDToolTask {
    def MDToolCompileTask() {
        super()
        onlyIf {
            !project.hasProperty('ide')
        }
    }
	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.paths.mdtool, 'build', '-t:Build', "-p:${xamarinProject.resolvedProjectName}", "-c:${config.name}${deviceTag}", xamarinProject.solutionFile]
	}
}

class MDToolCleanTask extends MDToolTask {
    @TaskAction
    def executeTask() {
        xamarinProject.configurationContainer.all() { config ->
            executeForConfiguration(config)
        }

        println "Deleting dependencies"
        project.delete(project.fileTree(dir:xamarinProject.dependencyDir, include: '*'))
    }

	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.paths.mdtool, 'build', '-t:Clean', "-p:${xamarinProject.resolvedProjectName}", "-c:${config.name}${deviceTag}", xamarinProject.solutionFile]
	}
}