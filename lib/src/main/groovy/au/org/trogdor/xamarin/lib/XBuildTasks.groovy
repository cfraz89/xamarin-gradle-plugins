package au.org.trogdor.xamarin.lib

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XBuildTask extends DefaultTask {
	XamarinProject xamarinProject
	Configuration configuration

	protected def projectFilePath

	def generateCommand() {
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
		projectFilePath = generateProjectFilePath()
		def proc = generateCommand().execute()
		proc.in.eachLine { line-> println line}
		proc.waitFor()
		if(proc.exitValue())
			throw new TaskExecutionException(this, null)
	}
}

class XBuildCompileTask extends XBuildTask {
	def generateCommand() {
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${configuration.name}", '/t:Build']
	}
}


class XBuildAndroidPackageTask extends XBuildTask {
	def generateCommand() {
		[project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${configuration.name}", '/t:PackageForAndroid']
	}
}

class XBuildCleanTask extends XBuildTask {
	def generateCommand() {
		[project.xamarin.xbuildPath, projectFilePath,  '/t:Clean']
	}
}