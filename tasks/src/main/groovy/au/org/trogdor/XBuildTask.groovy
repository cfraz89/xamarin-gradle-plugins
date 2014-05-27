package au.org.trogdor

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction	
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XBuildTask extends DefaultTask {
	def xbuildPath
	def projectFile
	def configuration

	def generateCommand() {
		return []
	}

	@TaskAction
	def build() {
		def proc = generateCommand().execute()
		proc.in.eachLine { line-> println line}
		proc.waitFor()
		if(proc.exitValue())
			throw new TaskExecutionException(this, null)
	}
}

class XBuildCompileTask extends XBuildTask {
	def generateCommand() {
		def command = [xbuildPath, projectFile, "/p:Configuration=${configuration}", '/t:PackageForAndroid']
	}
}

class XBuildCleanTask extends XBuildTask {
	def generateCommand() {
		def command = [xbuildPath, projectFile,  '/t:Clean']
	}
}