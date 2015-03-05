package au.org.trogdor.xamarin.plugins
import au.org.trogdor.xamarin.lib.*
import org.gradle.api.Project
/**
 * Created by chrisfraser on 7/07/2014.
 */
class XamarinBuildExtension {

    private Project project
    private XamarinProject mXamarinProject
    private PathContainer mPaths
    private String mSolutionFile

    XamarinBuildExtension(Project prj) {
        project = prj
        mPaths = new PathContainer(prj)
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

    def solution(String name) {
        mSolutionFile = name
    }

    def getSolution() {
        mSolutionFile
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

    def nunitProject(Closure closure) {
        setProject(new NUnitProject(project), closure)
    }

    def xunitProject(Closure closure) {
        setProject(new XUnitProject(project), closure)
    }

    def unifiediOSAppProject(Closure closure) {
        setProject(new UnifiediOSAppProject(project), closure)
    }

    def unifiediOSLibraryProject(Closure closure) {
        setProject(new UnifiediOSLibraryProject(project), closure)
    }
}
