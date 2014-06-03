package au.org.trogdor.xamarin.lib

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import groovy.transform.InheritConstructors

class XamarinProject implements NamedDomainObjectFactory<Configuration>{
	final Project project
    final NamedDomainObjectCollection<Configuration> configurationContainer
    private String mProjectName

	XamarinProject(Project prj) {
        this.project = prj
        prj.task("xamarinClean", description: "Clean the Xamarin project", group: "Xamarin", type: cleanTask()) {
            xamarinProject = this
        }
        configurationContainer = prj.container(Configuration, this)
    }

    def projectName(String name) {
        mProjectName = name
    }

    def getProjectName() {
        return mProjectName;
    }

    Configuration create(String name) {
        return new Configuration(name, project, this)
    }

    def configurations(Closure closure) {
        configurationContainer.configure(closure)
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
    Configuration create(String name) {
        return new AndroidConfiguration(name, project, this)
    }
}

@InheritConstructors
class MDToolProject extends XamarinProject {
	def mSolutionFile

    def solutionFile(String solutionFile) {
        mSolutionFile = solutionFile
    }

    def getSolutionFile() {
        return mSolutionFile
    }

	def buildTask() {
		return MDToolCompileTask
	}

	def cleanTask() {
		return MDToolCleanTask
	}
}