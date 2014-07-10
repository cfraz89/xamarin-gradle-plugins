package au.org.trogdor.xamarin.lib

import org.gradle.api.ProjectConfigurationException
import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XBuildTask extends DefaultTask {
    XBuildProject xamarinProject
	XamarinConfiguration configuration

	protected def projectFilePath

	def generateCommand(XamarinConfiguration config) {
		return []
	}

	@TaskAction
	def executeTask() {
        executeForConfiguration(configuration)
	}

    def executeForConfiguration(XamarinConfiguration config) {
        project.file(xamarinProject.projectFile).with {
            if (!it.exists())
                throw new ProjectConfigurationException("Project file location $it does not exist!", null)
        }

        projectFilePath = project.file(xamarinProject.projectFile).path
        def proc = generateCommand(config).execute()
        def serr = new ByteArrayOutputStream(4096)
        proc.waitForProcessOutput(System.out, serr)
        if(proc.exitValue())
            throw new TaskExecutionException(this, null)
    }
}

class XBuildCompileTask extends XBuildTask {
	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${config.name}", "/p:SolutionDir=${xamarinProject.solutionDir}", "/p:ProjectDir=${xamarinProject.projectDir}", '/t:Build']
	}
}


class XBuildAndroidPackageTask extends XBuildTask {
	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${config.name}", "/p:SolutionDir=${xamarinProject.solutionDir}", "/p:ProjectDir=${xamarinProject.projectDir}", '/t:PackageForAndroid']
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
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${config.name}", "/p:SolutionDir=${xamarinProject.solutionDir}", "/p:ProjectDir=${xamarinProject.projectDir}", '/t:Clean']
	}
}