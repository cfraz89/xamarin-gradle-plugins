package au.org.trogdor

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.tasks.TaskAction	
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration

class XamarinDependencyPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.extensions.create("xamarinDependencies", XamarinDependencyProjectExtension)
		project.configurations.create("xamarinCompile")
		project.afterEvaluate() {
			def fetchTask = project.task("fetchXamarinDependencies", description:"Copy dependency dlls into project", group:"Xamarin", type: DependencyFetchTask)
			fetchTask.libDir = project.xamarinDependencies.libDir
			fetchTask.configuration = project.configurations.xamarinCompile
	    }
	}
}

class XamarinDependencyProjectExtension {
	def libDir = "dependencies"
}

class DependencyFetchTask extends DefaultTask {
	def libDir
	private Configuration configuration

	def setConfiguration(Configuration c) {
		configuration = c

		inputs.source(configuration)
		outputs.dir(libDir)
	}

	def getConfiguration() {
		return configuration
	}

	@TaskAction
	def fetch() {
		println "Dependencies:"
		def artifacts = configuration.incoming.files
		artifacts.each(){file->
			println "Copying dependency $file"
			project.copy {
				from project.file(file)
				into project.file(libDir)
			}
		}
	}
}
