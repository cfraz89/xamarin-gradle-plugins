package au.org.trogdor.xamarin.lib

import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import groovy.transform.InheritConstructors

class XamarinProject implements NamedDomainObjectFactory<XamarinConfiguration>{
	final Project project
    final NamedDomainObjectCollection<XamarinConfiguration> configurationContainer
    private String mProjectName
    private String mDepDir = "dependencies"

	XamarinProject(Project prj) {
        this.project = prj
        prj.task("xamarinClean", description: "Clean the Xamarin project", group: "Xamarin", type: cleanTask()) {
            xamarinProject = this
        }
        configurationContainer = prj.container(XamarinConfiguration, this)
    }

    def projectName(String name) {
        mProjectName = name
    }

    def getProjectName() {
        return mProjectName;
    }

    protected def makeConfiguration(String name) {
        def config = project.configurations.create("xamarinCompile-$name") {
            extendsFrom project.configurations.xamarinCompile
        }
        project.task("fetchXamarinDependencies-$name", description: "Copy dependency dlls into project", group: "Xamarin", type: DependencyFetchTask) {
            xamarinProject = this
            configuration = config
        }
    }

    XamarinConfiguration create(String name) {
        makeConfiguration(name)
        return new XamarinConfiguration(name, project, this)
    }

    def configurations(Closure closure) {
        configurationContainer.configure(closure)
    }

    def getConfigurations() {
        configurationContainer
    }

    def dependencyDir(String depDir) {
        mDepDir = depDir
    }

    String getDependencyDir() {
        mDepDir
    }
}

@InheritConstructors
class XBuildProject extends XamarinProject {
	private String mProjectFile = ''

    def projectFile(String projectFile) {
        mProjectFile = projectFile
    }

    def getProjectFile() {
        return mProjectFile
    }
	
	def buildTask() {
		return XBuildCompileTask
	}

	def cleanTask() {
		return XBuildCleanTask
	}
}

@InheritConstructors
class XBuildAndroidProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        makeConfiguration(name)
        return new AndroidConfiguration(name, project, this)
    }
}

@InheritConstructors
class MDToolProject extends XamarinProject {
	private String mSolutionFile

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