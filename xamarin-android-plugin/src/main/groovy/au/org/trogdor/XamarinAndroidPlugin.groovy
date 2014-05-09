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
				def taskName = conf.replaceAll(~/\|/, "")
		    	project.task("xamarinAndroidBuild-${taskName}", description: "Build a Xamarin Android app using configuration ${conf}", group: "Xamarin", type: XBuildCompileTask) {
		    		xbuildPath = project.xamarinAndroid.xbuildPath
		    		projectFile =  project.xamarinAndroid.projectFile
		    		configuration = conf
		    	}
	    	}
	    	project.task("xamarinAndroidClean", description: "Clean the Xamarin Android project", group: "Xamarin", type: XBuildCleanTask) {
		    		xbuildPath = project.xamarinAndroid.xbuildPath
		    		projectFile =  project.xamarinAndroid.projectFile
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
