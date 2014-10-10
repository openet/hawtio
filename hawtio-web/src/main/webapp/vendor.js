// add your own customisations here

// diable welcome page
localStorage['showWelcomePage'] = false;
 
// delete all tabs from default perspective
Perspective.metadata.container.topLevelTabs.excludes = [{}];
// add Camel and Connect to the default perspective
Perspective.metadata.container.topLevelTabs.includes = [{id: "camel"},{id: "connect"},{id: "dashboard"}];
Perspective.metadata.container.label = "Recent";
delete Perspective.metadata.container.icon;
