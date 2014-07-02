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
    private List<String> mReferences

	XamarinProject(Project prj) {
        this.project = prj
        configurationContainer = prj.container(XamarinConfiguration, this)
        mReferences = []
    }

    def projectName(String name) {
        mProjectName = name
    }

    def getProjectName() {
        return mProjectName;
    }

    XamarinConfiguration create(String name) {
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

    def references(String refProjectName) {
        mReferences.add(refProjectName)
    }

    def getReferencedProjects() {
        mReferences
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
class AndroidLibraryProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new AndroidLibraryConfiguration(name, project, this)
    }
}

@InheritConstructors
class AndroidAppProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new AndroidAppConfiguration(name, project, this)
    }

    def buildTask() {
        return XBuildAndroidPackageTask
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

@InheritConstructors
class iOSLibraryProject extends MDToolProject {
    XamarinConfiguration create(String name) {
        return new iOSLibraryConfiguration(name, project, this)
    }
}


@InheritConstructors
class iOSAppProject extends MDToolProject {
    XamarinConfiguration create(String name) {
        return new iOSAppConfiguration(name, project, this)
    }
}

@InheritConstructors
class GenericLibraryProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new GenericLibraryConfiguration(name, project, this)
    }
}

@InheritConstructors
class GenericAppProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new GenericAppConfiguration(name, project, this)
    }
}