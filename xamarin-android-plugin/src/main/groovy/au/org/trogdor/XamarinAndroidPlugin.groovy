package au.org.trogdor

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskAction	
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskExecutionException

class XamarinAndroidPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.extensions.create("xamarinAndroid", XamarinAndroidProjectExtension)
		project.afterEvaluate({
			project.xamarinAndroid.configurations.each() {conf->
		    	project.task("xamarinAndroidBuild-${conf}", description: "Build a Xamarin Android app using configuration ${conf}", group: "Xamarin", type: XBuildTask) {
		    		xbuildPath = project.xamarinAndroid.xbuildPath
		    		projectFile =  project.xamarinAndroid.projectFile
		    		configuration = conf
		    	}
	    	}
    	})
    }
}

class XamarinAndroidProjectExtension {
	def xbuildPath = 'xbuild'
	def projectFile = ''
	def configurations = ['Debug', 'Release']
}

class XBuildTask extends DefaultTask {
	def xbuildPath
	def projectFile
	def configuration

	@TaskAction
	def build() {
		def command = [xbuildPath, projectFile, "/p:Configuration=${configuration}", '/t:PackageForAndroid']
		println command
		def proc = command.execute()
		proc.in.eachLine { line-> println line}
		proc.waitFor()
		if(proc.exitValue())
			throw new TaskExecutionException(this, null)
	}
}
