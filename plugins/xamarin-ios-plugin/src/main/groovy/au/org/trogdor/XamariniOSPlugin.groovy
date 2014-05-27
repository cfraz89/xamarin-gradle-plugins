package au.org.trogdor

import org.gradle.api.Project
import org.gradle.api.Plugin

class XamariniOSPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.extensions.create("xamariniOS", XamarinIOSProjectExtension)
		project.afterEvaluate({
			def xamarinDependencyTask = project.tasks.findByPath("fetchXamarinDependencies")
			project.xamariniOS.configurations.each() {conf->
				def taskName = conf.replaceAll(~/\|/, "")
		    	project.task("xamariniOSBuild-${taskName}", description: "Build a Xamarin iOS app using configuration ${conf}", group: "Xamarin", dependsOn: xamarinDependencyTask, type: MDToolCompileTask) {
		    		mdtoolPath = project.xamariniOS.mdtoolPath
		    		projectName =  project.xamariniOS.projectName
		    		solutionFile = project.xamariniOS.solutionFile
		    		configuration = conf
		    	}
		    }
		    project.task("xamariniOSClean", description: "Clean the Xamarin iOS project", group: "Xamarin", type: MDToolCleanTask) {
		    		mdtoolPath = project.xamariniOS.mdtoolPath
		    		projectName =  project.xamariniOS.projectName
		    		solutionFile = project.xamariniOS.solutionFile
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
