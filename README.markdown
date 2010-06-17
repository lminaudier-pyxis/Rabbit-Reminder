Rabbit Reminder
===============

A simple geolocalized to-do list application for _Google Android_.

Build Rabbit Reminder
---------------------

You'll need a working _android sdk_ installed with the _android sdk 1.5_ image in order to build this application.

You must define an environment variable `ANDROID_HOME` which defines the path to your android sdk directory. The path to the android _tools_ folder must be in your `PATH` environment variable.

###Build the application with _Eclipse_

Open a new _Eclipse_ workspace and click _File > Import > General > Existing project into workspace_. Then in the _Select root directory_ field, write the path to the _Rabbit Reminder_ folder.

Do the same thing for the _Rabbit Reminder Test_ folder if you want to build the unit & functional tests.

The workspace should be built automatically by _Eclipse_: if not, click _Project > Build All_.

**If it don't build correctly**

- Check that projects are synchronized with Eclipse by selecting them and pressing _F5_
- Check that projects properties are correct by _right clicking_ on them and choose _Android Tools > Fix Project Properties_
- Suppress generated classes as the _R.java_ class and rebuild the whole project.

### Build the application with _Ant_

Go to the _Rabbit Reminder_ directory and enter the command 

`android update project -p .`

to update the _build.xml_ script with your local configuration. You can now build the main project with _Ant_.

To build the test project, you need to go into the _Rabbit Reminder Test_ directory and enter the command

`android update test-project -p . -m "../Rabbit Reminder Test"`

to update the build script. You can now build the test projet with `ant release` and you can run tests with `ant run-tests` (an emulator needs to be started before running any tests).

**If it don't build correctly**

- Check that both _emulator_ and _sdk images_ are **up-to-date**
- Check the state of your `ANDROID_HOME` environment variable
- Check that the path inside your _local.properties_ file points to the right directory (the root of the android sdk)


Setting Maps API key
--------------------

This application use the _Google Maps API_ to work, so you need a _Maps API key_ in order to make it work properly (refer to the Android document if you don't know how to generate a _Maps API key_).

To set up the key, simply create a XML file called `\Rabbit Reminder\res\values\keys.xml` with the following content :

`<?xml version="1.0" encoding="utf-8"?>
<resources>
	<string name="maps_api_key">[[YOUR API KEY GOES HERE]]</string>
</resources>`

