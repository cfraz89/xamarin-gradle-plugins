package au.org.trogdor.xamarin.lib

import au.org.trogdor.xamarin.tasks.XBuildCompileTask
import au.org.trogdor.xamarin.tasks.XBuildAndroidPackageTask
import au.org.trogdor.xamarin.tasks.XBuildCleanTask
import au.org.trogdor.xamarin.tasks.MDToolCompileTask
import au.org.trogdor.xamarin.tasks.MDToolCleanTask
import org.gradle.api.Project
import groovy.transform.InheritConstructors

class XamarinProject {
	Project project
	def configurations = ['Debug', 'Release']

	OutputContainer buildContainer

	XamarinProject(Project prj) {
	    this.project = prj
	    prj.task("xamarinClean", description: "Clean the Xamarin project", group: "Xamarin", type: cleanTask()) {
    		xamarinProject = this
		}
	}

	protected def getTaskName(String conf) {
		conf.replaceAll(~/\|/, "")
	}

	def build(Closure closure) {
		buildContainer = new OutputContainer()
		project.configure(buildContainer, closure)
		configurations.each() {conf->
			def taskName = getTaskName(conf)
	    	def task = project.task("xamarinBuild-${taskName}", description: "Build a Xamarin project using configuration ${conf}", group: "Xamarin", dependsOn: "fetchXamarinDependencies", type: buildTask()) {
	    		xamarinProject = this
	     		configuration = conf
	    	}
	    	if (buildContainer.output) {
	    		def output = buildContainer.output.replace("\${configuration}", conf)
		    	task.outputs.file(output)
		    	task.outputs.upToDateWhen() { false }
		    }
	    }
	}
}

class OutputContainer {
	def String output = null
}

@InheritConstructors
class XBuildProject extends XamarinProject {
	def projectFile = ''
	
	def buildTask() {
		return XBuildCompileTask
	}

	def cleanTask() {
		return XBuildCleanTask
	}
}

@InheritConstructors
class XBuildAndroidProject extends XBuildProject {
	OutputContainer packageContainer

	def androidPackage(Closure closure) {
		packageContainer = new OutputContainer()
		project.configure(packageContainer, closure)
		configurations.each() {conf->
			def taskName = getTaskName(conf)
			def task = project.task("xamarinPackage-${taskName}", description: "Build a Xamarin android apk using configuration ${conf}", group: "Xamarin", dependsOn: "fetchXamarinDependencies", type: XBuildAndroidPackageTask) {
		  		xamarinProject = this
	    		configuration = conf
	    	}
	    	if (packageContainer.output) {
		    	def output = packageContainer.output.replace("\${configuration}", conf)
		    	task.outputs.file(output)
		    	task.outputs.upToDateWhen() { false }
		    }
	    }
	}
}

@InheritConstructors
class MDToolProject extends XamarinProject {
	def projectName
	def solutionFile
	def configuration

	def buildTask() {
		return MDToolCompileTask
	}

	def cleanTask() {
		return MDToolCleanTask
	}
}