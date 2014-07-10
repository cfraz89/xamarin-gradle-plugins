package au.org.trogdor.xamarin.lib

import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class MDToolTask extends DefaultTask {
	XamarinProject xamarinProject
    XamarinConfiguration configuration
    String device

	protected def solutionFilePath

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
            if (!it.exists())
                throw new ProjectConfigurationException("Project file location $it does not exist!", null)
        }

        solutionFilePath = project.file(xamarinProject.solutionFile).path
        def proc = generateCommand(config).execute()
        def serr = new ByteArrayOutputStream(4096)
        proc.waitForProcessOutput(System.out, serr)

        if(proc.exitValue())
            throw new TaskExecutionException(this, null)
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
		[project.xamarin.mdtoolPath, 'build', '-t:Build', "-p:${xamarinProject.resolvedProjectName}", "-c:${config.name}${deviceTag}", solutionFilePath]
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
		[project.xamarin.mdtoolPath, 'build', '-t:Clean', "-p:${xamarinProject.resolvedProjectName}", "-c:${config.name}${deviceTag}", solutionFilePath]
	}
}