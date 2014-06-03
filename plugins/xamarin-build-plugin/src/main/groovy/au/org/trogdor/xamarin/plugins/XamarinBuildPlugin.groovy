package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.plugins.XamarinDependencyPlugin
import au.org.trogdor.xamarin.lib.XamarinProject
import au.org.trogdor.xamarin.lib.XBuildProject
import au.org.trogdor.xamarin.lib.XBuildAndroidProject
import au.org.trogdor.xamarin.lib.MDToolProject
import org.gradle.api.Project
import org.gradle.api.Plugin

class XamarinBuildPlugin implements Plugin<Project> {
	void apply(Project project) {
		project.plugins.apply XamarinDependencyPlugin
		project.extensions.create("xamarin", XamarinBuildExtension, project)
    }
}


class XamarinBuildExtension {
	private def Project project
	def XamarinProject xamarinProject

	private String mXBuildPath = "xbuild"
	private String mMDToolPath = "/Applications/Xamarin Studio.app/Contents/MacOS/mdtool"

	XamarinBuildExtension(Project prj) {
		project = prj
	}

	private def setProject(XamarinProject xprj, Closure closure) {
		if (this.xamarinProject != null)
			throw new Exception("You may only define one Xamarin project per Gradle project!")

		project.configure(xprj, closure)
		this.xamarinProject = xprj
	}

    def xbuildPath(String xbuildPath) {
        mXBuildPath = xbuildPath
    }

    def getxbuildPath() {
        return mXBuildPath
    }

    def mdtoolPath(String mdtoolpath) {
        mMDToolPath = mdtoolpath
    }

    def getmdtoolPath() {
        return mMDToolPath
    }

	def androidProject(Closure closure) {
		setProject(new XBuildAndroidProject(project), closure)
	}

	def iOSProject(Closure closure) {
		setProject(new MDToolProject(project), closure)
	}

	def genericProject(Closure closure) {
		setProject(new XBuildProject(project), closure)
	}

	def xbuildProject(Closure closure) {
		setProject(new XBuildProject(project), closure)
	}

	def mdtoolProject(Closure closure) {
		setProject(new MDToolProject(project), closure)
	}
}
