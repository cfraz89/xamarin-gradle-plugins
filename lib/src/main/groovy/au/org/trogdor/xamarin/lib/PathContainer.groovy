package au.org.trogdor.xamarin.lib

/**
 * Created by chrisfraser on 17/07/2014.
 */
class PathContainer {
    String mXBuildPath = 'xbuild'
    String mMDToolPath = '/Applications/Xamarin Studio.app/Contents/MacOS/mdtool'
    String mMonoPath = 'mono'
    String mNugetPath

    def xbuild(String xbuildPath) {
        mXBuildPath = xbuildPath
    }

    def mdtool(String mdtoolPath) {
        mMDToolPath = mdtoolPath
    }

    def mono(String monoPath) {
        mMonoPath = monoPath
    }

    def nuget(String nugetPath) {
        mNugetPath = nugetPath
    }

    def getXbuild() {
        mXBuildPath
    }

    def getMdtool() {
        mMDToolPath
    }

    def getMono() {
        mMonoPath
    }

    def getNuget() {
        mNugetPath
    }
}
