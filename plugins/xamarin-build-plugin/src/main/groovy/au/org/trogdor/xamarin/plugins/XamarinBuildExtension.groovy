package au.org.trogdor.xamarin.plugins

import au.org.trogdor.xamarin.lib.AndroidAppProject
import au.org.trogdor.xamarin.lib.AndroidLibraryProject
import au.org.trogdor.xamarin.lib.GenericAppProject
import au.org.trogdor.xamarin.lib.GenericLibraryProject
import au.org.trogdor.xamarin.lib.MDToolProject
import au.org.trogdor.xamarin.lib.PathContainer
import au.org.trogdor.xamarin.lib.XBuildProject
import au.org.trogdor.xamarin.lib.XamarinProject
import au.org.trogdor.xamarin.lib.iOSAppProject
import au.org.trogdor.xamarin.lib.iOSLibraryProject
import org.gradle.api.Project

/**
 * Created by chrisfraser on 7/07/2014.
 */
class XamarinBuildExtension {

    private def Project project
    private def XamarinProject mXamarinProject
    private def PathContainer mPaths

    XamarinBuildExtension(Project prj) {
        project = prj
        mPaths = new PathContainer()
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

    def paths(Closure closure) {
        project.configure(mPaths, closure)
    }

    def getPaths() {
        mPaths
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
