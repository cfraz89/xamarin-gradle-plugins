package au.org.trogdor

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction	
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XamariniOSPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.extensions.create("xamariniOS", XamarinIOSProjectExtension)
		project.afterEvaluate({
			project.xamariniOS.configurations.each() {conf->
		    	project.task("xamariniOSBuild-${conf}", description: "Build a Xamarin iOS app using configuration ${conf}", group: "Xamarin", type: MDToolTask) {
		    		mdtoolPath = project.xamariniOS.mdtoolPath
		    		projectName =  project.xamariniOS.projectName
		    		solutionFile = project.xamariniOS.solutionFile
		    		configuration = conf
		    	}
		    }
    	})
    }
}

class XamarinIOSProjectExtension {
	def mdtoolPath = '/Applications/Xamarin Studio.app/Contents/MacOS/mdtool'
	def projectName = ''
	def solutionFile = ''
	def configurations = ['Debug', 'Release']
}

class MDToolTask extends DefaultTask {
	def mdtoolPath
	def projectName
	def solutionFile
	def configuration

	@TaskAction
	def build() {
		def command = [mdtoolPath, 'build', '-t:Build', "-p:${projectName}", "-c:${configuration}" , solutionFile]
		def proc = command.execute()
		proc.in.eachLine { line-> println line}
		proc.waitFor()
		if(proc.exitValue())
			throw new TaskExecutionException(this, null)
	}
}
