package au.org.trogdor

import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction	
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class MDToolTask extends DefaultTask {
	def mdtoolPath
	def projectName
	def solutionFile
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

class MDToolCompileTask extends MDToolTask {
	def generateCommand() {
		return [mdtoolPath, 'build', '-t:Build', "-p:${projectName}", "-c:${configuration}" , solutionFile]
	}
}

class MDToolCleanTask extends MDToolTask {
	def generateCommand() {
		return [mdtoolPath, 'build', '-t:Clean', "-p:${projectName}", solutionFile]
	}
}