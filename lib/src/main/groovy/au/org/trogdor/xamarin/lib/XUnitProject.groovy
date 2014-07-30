package au.org.trogdor.xamarin.lib

import groovy.transform.InheritConstructors

@InheritConstructors
class XUnitProject extends XBuildProject {
    XamarinConfiguration create(String name) {
        return new XUnitConfiguration(name, project, this)
    }
}
