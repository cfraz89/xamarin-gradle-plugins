package au.org.trogdor.xamarin.lib

import au.org.trogdor.xamarin.tasks.XBuildCompileTask
import au.org.trogdor.xamarin.tasks.XBuildAndroidPackageTask
import au.org.trogdor.xamarin.tasks.XBuildCleanTask
import au.org.trogdor.xamarin.tasks.MDToolCompileTask
import au.org.trogdor.xamarin.tasks.MDToolCleanTask
import org.gradle.api.Project

import org.gradle.api.Task
import groovy.transform.InheritConstructors

class XamarinProject {
	Project project
	def configurations = ['Debug', 'Release']

	XamarinProject(Project prj) {
	    this.project = prj
	    prj.task("xamarinClean", description: "Clean the Xamarin project", group: "Xamarin", type: cleanTask()) {
    		xamarinProject = this
		}
	}

	protected def getTaskName(String conf) {
		conf.replaceAll(~/\|/, "")
	}

    protected def setTaskOutput(Task task, Closure closure, String configuration) {
        def container = new OutputContainer(configuration)
        project.configure(container, closure)
        if (container.output) {
            println ("Output:")
            println(container.output)
            task.outputs.file(container.output)
            task.outputs.upToDateWhen() { false }
        }
    }

	def build(Closure closure) {
		configurations.each() {configuration->
			def taskName = getTaskName(configuration)
	    	def task = project.task("xamarinBuild-${taskName}", description: "Build a Xamarin project using configuration ${configuration}", group: "Xamarin", dependsOn: "fetchXamarinDependencies", type: buildTask()) {
	    		xamarinProject = this
	     		activeConfiguration = configuration
	    	}
            setTaskOutput(task, closure, configuration)
	    }
	}
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
	Closure packageClosure

	def androidPackage(Closure closure) {
		packageClosure = closure
		configurations.each() {configuration->
			def taskName = getTaskName(configuration)
			def task = project.task("xamarinPackage-${taskName}", description: "Build a Xamarin android apk using configuration ${configuration}", group: "Xamarin", dependsOn: "fetchXamarinDependencies", type: XBuildAndroidPackageTask) {
		  		xamarinProject = this
	    		activeConfiguration = configuration
	    	}
            setTaskOutput(task, closure, configuration)
	    }
	}
}

@InheritConstructors
class MDToolProject extends XamarinProject {
	def projectName
	def solutionFile

	def buildTask() {
		return MDToolCompileTask
	}

	def cleanTask() {
		return MDToolCleanTask
	}
}

class OutputContainer {
    def GString output = null
    def configuration = null

    def OutputContainer(String conf) {
        configuration = conf
    }
}