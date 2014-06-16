package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.XamarinProject
import au.org.trogdor.xamarin.lib.XBuildProject
import au.org.trogdor.xamarin.lib.AndroidAppProject
import au.org.trogdor.xamarin.lib.AndroidLibraryProject
import au.org.trogdor.xamarin.lib.iOSAppProject
import au.org.trogdor.xamarin.lib.iOSLibraryProject
import au.org.trogdor.xamarin.lib.GenericLibraryProject
import au.org.trogdor.xamarin.lib.GenericAppProject
import au.org.trogdor.xamarin.lib.MDToolProject
import au.org.trogdor.xamarin.lib.DependencyFetchTask
import org.gradle.api.Project
import org.gradle.api.Plugin

class XamarinBuildPlugin implements Plugin<Project> {
	void apply(Project project) {
        project.configurations.create("xamarinCompile")
		project.extensions.create("xamarin", XamarinBuildExtension, project)

        project.afterEvaluate() {
            if (project.xamarin.xamarinProject) {
                def xProj = project.xamarin.xamarinProject
                project.task("xamarinBuildAll", description: "Build all configurations", group: "Xamarin")
                project.task("xamarinClean", description: "Clean the Xamarin project", group: "Xamarin", type: xProj.cleanTask()) {
                    xamarinProject = xProj
                }
                xProj.configurations.each() { xConf ->
                    def config = project.configurations.findByName("xamarinCompile${xConf.name}")
                    project.task("fetchXamarinDependencies-${xConf.name}", description: "Copy dependency dlls into project", group: "Xamarin", type: DependencyFetchTask) {
                        xamarinProject = xProj
                        configuration = config
                    }

                    xConf.makeTasks()
                }
            }
        }
    }
}


class XamarinBuildExtension {
	private def Project project
	def XamarinProject mXamarinProject

	private String mXBuildPath = "xbuild"
	private String mMDToolPath = "/Applications/Xamarin Studio.app/Contents/MacOS/mdtool"

	XamarinBuildExtension(Project prj) {
		project = prj
	}

	private def setProject(XamarinProject xprj, Closure closure) {
		if (this.mXamarinProject != null)
			throw new Exception("You may only define one Xamarin project per Gradle project!")

		project.configure(xprj, closure)
		this.mXamarinProject = xprj
	}

    XamarinProject getXamarinProject() {
        mXamarinProject
    }

    def xbuildPath(String xbuildPath) {
        mXBuildPath = xbuildPath
    }

    def getXbuildPath() {
        return mXBuildPath
    }

    def mdtoolPath(String mdtoolpath) {
        mMDToolPath = mdtoolpath
    }

    def getMdtoolPath() {
        return mMDToolPath
    }

	def androidAppProject(Closure closure) {
		setProject(new AndroidAppProject(project), closure)
	}

    def androidLibraryProject(Closure closure) {
        setProject(new AndroidLibraryProject(project), closure)
    }

	def iOSAppProject(Closure closure) {
		setProject(new iOSAppProject(project), closure)
	}

    def iOSLibraryProject(Closure closure) {
        setProject(new iOSLibraryProject(project), closure)
    }

    def genericLibraryProject(Closure closure) {
        setProject(new GenericLibraryProject(project), closure)
    }

	def genericAppProject(Closure closure) {
		setProject(new GenericAppProject(project), closure)
	}

	def xbuildProject(Closure closure) {
		setProject(new XBuildProject(project), closure)
	}

	def mdtoolProject(Closure closure) {
		setProject(new MDToolProject(project), closure)
	}
}
