package au.org.trogdor

import au.org.trogdor.XamarinDependencyPlugin
import au.org.trogdor.XBuildTask
import org.gradle.api.Project
import org.gradle.api.Plugin

class XamarinAndroidPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.plugins.apply XamarinDependencyPlugin
		project.extensions.create("xamarinAndroid", XamarinAndroidProjectExtension)
		project.afterEvaluate({
			def xamarinDependencyTask = project.tasks.findByPath("fetchXamarinDependencies")
			project.xamarinAndroid.configurations.each() {conf->
				def taskName = conf.replaceAll(~/\|/, "")
		    	def buildTask = project.task("xamarinAndroidBuild-${taskName}", description: "Build a Xamarin Android app using configuration ${conf}", group: "Xamarin", dependsOn: xamarinDependencyTask, type: XBuildCompileTask) {
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
