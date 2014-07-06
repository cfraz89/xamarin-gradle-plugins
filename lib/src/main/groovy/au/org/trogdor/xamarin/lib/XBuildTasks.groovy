package au.org.trogdor.xamarin.lib

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XBuildTask extends DefaultTask {
	XamarinProject xamarinProject
	XamarinConfiguration configuration

	protected def projectFilePath

	def generateCommand(XamarinConfiguration config) {
		return []
	}

    def generateProjectFilePath() {
        def unresolvedPath = xamarinProject.projectFile
        if (xamarinProject.getProjectName() && !unresolvedPath)
            unresolvedPath = xamarinProject.getProjectName() + ".csproj"
        project.file(unresolvedPath).path
    }

	@TaskAction
	def executeTask() {
        executeForConfiguration(configuration)
	}

    def executeForConfiguration(XamarinConfiguration config) {
        projectFilePath = generateProjectFilePath()
        def proc = generateCommand(config).execute()
        def serr = new ByteArrayOutputStream(4096)
        proc.waitForProcessOutput(System.out, serr)
        if(proc.exitValue())
            throw new TaskExecutionException(this, null)
    }
}

class XBuildCompileTask extends XBuildTask {
	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${config.name}", '/t:Build']
	}
}


class XBuildAndroidPackageTask extends XBuildTask {
	def generateCommand(XamarinConfiguration config) {
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${config.name}", '/t:PackageForAndroid']
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
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${config.name}", '/t:Clean']
	}
}