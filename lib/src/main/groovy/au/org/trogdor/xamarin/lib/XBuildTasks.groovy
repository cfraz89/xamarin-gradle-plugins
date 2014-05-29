package au.org.trogdor.xamarin.tasks

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction	
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XBuildTask extends DefaultTask {
	def xamarinProject
	def configuration

	protected def projectFilePath

	def generateCommand() {
		return []
	}

	@TaskAction
	def build() {
		projectFilePath = project.file(xamarinProject.projectFile).path
		def proc = generateCommand().execute()
		proc.in.eachLine { line-> println line}
		proc.waitFor()
		if(proc.exitValue())
			throw new TaskExecutionException(this, null)
	}
}

class XBuildCompileTask extends XBuildTask {
	def generateCommand() {
		def command = [project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${configuration}", '/t:Build']
	}
}


class XBuildAndroidPackageTask extends XBuildTask {
	def generateCommand() {
		def command = [project.xamarin.xbuildPath, projectFilePath, "/p:Configuration=${configuration}", '/t:PackageForAndroid']
	}
}

class XBuildCleanTask extends XBuildTask {
	def generateCommand() {
		def command = [project.xamarin.xbuildPath, projectFilePath,  '/t:Clean']
	}
}