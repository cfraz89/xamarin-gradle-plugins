package au.org.trogdor.xamarin.lib

import org.gradle.api.tasks.TaskAction
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class MDToolTask extends DefaultTask {
	XamarinProject xamarinProject
	XamarinConfiguration configuration

	protected def solutionFilePath

	def generateCommand() {
		return []
	}

	@TaskAction
	def build() {
		solutionFilePath = project.file(xamarinProject.solutionFile).path
		def proc = generateCommand().execute()
		proc.in.eachLine { line-> println line}
		proc.waitFor()
		if(proc.exitValue())
			throw new TaskExecutionException(this, null)
	}
}

class MDToolCompileTask extends MDToolTask {
	def generateCommand() {
		[project.xamarin.mdtoolPath, 'build', '-t:Build', "-p:${xamarinProject.projectName}", "-c:${configuration.name}" , solutionFilePath]
	}
}

class MDToolCleanTask extends MDToolTask {
	def generateCommand() {
		[project.xamarin.mdtoolPath, 'build', '-t:Clean', "-p:${xamarinProject.projectName}", solutionFilePath]
	}
}