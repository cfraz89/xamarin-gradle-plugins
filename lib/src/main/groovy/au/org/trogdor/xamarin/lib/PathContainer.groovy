package au.org.trogdor.xamarin.lib

import org.gradle.api.Project

/**
 * Created by chrisfraser on 17/07/2014.
 */
class PathContainer {
    static String DEFAULT_NUGET_RELATIVE_PATH = '.nuget/nuget.exe'
    static String DEFAULT_XC_RELATIVE_PATH = '.xpkg/xamarin-component'

    Project project

    def xbuild = 'xbuild'
    def mdtool = '/Applications/Xamarin Studio.app/Contents/MacOS/mdtool'
    def mono =  'mono'
    def nuget = "${->solutionPath}/${->DEFAULT_NUGET_RELATIVE_PATH}"
    def xamarinComponent = "${->solutionPath}/${->DEFAULT_XC_RELATIVE_PATH}"
    def nunitConsole = 'nunit-console'
    def xunitConsole = 'xunit-console'

    def PathContainer(Project project) {
        this.project = project
    }

    def xbuild(def xbuildPath) {
        xbuild = xbuildPath
    }

    def mdtool(def mdtoolPath) {
        mdtool = mdtoolPath
    }

    def mono(def monoPath) {
        mono = monoPath
    }

    def nuget(def nugetPath) {
        nuget = nugetPath
    }

    def xamarinComponent(def xcPath) {
        xamarinComponent = xcPath
    }

    def nunitConsole(def nunitConsolePath) {
        nunitConsole = nunitConsolePath
    }

    def xunitConsole(def xunitConsolePath) {
        xunitConsole = xunitConsolePath
    }

    def getSolutionPath() {
        if (project.xamarin.solution)
            project.file(project.xamarin.solution).parent
        else
            project.file(project.xamarin.xamarinProject.solutionFile).parent
    }
}
