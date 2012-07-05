AppaholicsUpdateChecker
=======================

This is a free and open source library to help you check for updates to apps which are not installed via any appstore.

UpdateChecker contains the source code for the library. 
Test contains a sample test project that demoes how to use this library


USAGE:

Using the library is extremely easy. Follow the steps outlined below, and you should be good to go:

Online stuff:

1) On any server, create a text file with only the latest version code as it's content. Nothing else at all. Just one version code.

2) Upload the latest APK to someplace on a server.

3) You're done with the server stuff.

Android stuff:

1) Add the "update checker.jar" to your /libs folder. Then add it to your build path.

2) In your Activity, add the following code to your onCreate():

	UpdateChecker checker = new UpdateChecker(this);

3) Now when you want to check for updates, use:
	
	checker.checkForUpdateByVersionCode("URL with http:// to the text file with the version code");
	
4) Once #3 has been done, you can check if an update is available by using:
	
	checker.isUpdateAvailable();

5) If an update is available, you can download and install it by using:
	checker.downloadAndInstall("URL with http:// to the location of the update apk");

6) You're done!!

See the test folder for a complete, working sample app that uses this.
 