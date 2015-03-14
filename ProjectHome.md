<img src='http://wiki.gwt-cropper.googlecode.com/hg/gwt-cropper.png' alt='GWT Cropper logo' title='GWT Cropper logo' width='280' height='105' />

**GWT Cropper** is a widget for [Google Web Toolkit](http://www.gwtproject.org/), that allows you to select an area of a picture and get the coordinates of this selection. It is useful, if you want to crop a picture on the server side.

# Demo #
Try the working demo:
  * Simple cropper: [demo](http://wiki.gwt-cropper.googlecode.com/hg/demo/Application.html)
  * Cropper with preview: [demo](http://wiki.gwt-cropper.googlecode.com/hg/demo/Application2.html)

<img src='http://wiki.gwt-cropper.googlecode.com/hg/gwt-cropper-1.jpg' alt='Gwt Cropper plugin to crop an image' width='669' height='530' />

# Get started #

The plugin is very simple. In order to use it, you should follow next steps:

## 1. Import plugin to your project ##

If you use [Maven](http://maven.apache.org/), you can add this dependency to your POM:

```
      <dependency>
            <groupId>com.googlecode.gwt-cropper</groupId>
            <artifactId>gwt-crop</artifactId>
            <version>0.5.1</version>
      </dependency>
```

If you need JAR files, you can download them from the [GWT-Cropper Maven Repository](http://search.maven.org/#search|gav|1|g%3A%22com.googlecode.gwt-cropper%22%20AND%20a%3A%22gwt-crop%22|) page. Just select any link **jar**, **javadoc.jar** either **sources.jar** next to the each version to download an archive you need. Unfortunately, Google Code [no longer supports file uploading](http://code.google.com/p/support/wiki/DownloadsFAQ) to a page since 2014, and you have to download them directly from the Central Repository.

## 2. Declare the plugin in your project ##
Add this line to your **.gwt.xml** file:
```xml

<!-- Inherit the GWT-Cropper -->
<inherits name='com.google.code.gwt.crop.GWTCropper'/>
```

## 3. Use it in your project ##
Add **GWT Cropper** to any panel in your code. Simple example (on Java):

```java


final GWTCropper crop = new GWTCropper("url/to/your/uncropped/image.jpg");
crop.setAspectRatio(1); // square selection (optional)
panel.add(crop);

```

Or sample code in _.ui.xml_ (since [v. 0.5.0](http://code.google.com/p/gwt-cropper/issues/detail?id=17)):
```xml

<ui:UiBinder xmlns:ui='urn:ui:com.google.gwt.uibinder'
xmlns:g='urn:import:com.google.gwt.user.client.ui'
xmlns:my="urn:import:com.google.code.gwt.crop.client">

<my:GWTCropper  ui:field="cropper" aspectRatio="1.5" imageURL="url/to/your/uncropped/image.jpg" />

```

# Tablets #
**GWT Cropper** supports tablets since v.0.4.0. Tested on the IPad (2, 3, 4, mini), several Android devices and since v.0.4.1 the widget supports also Win8 tablets (including Microsoft Surface).

<img src='http://wiki.gwt-cropper.googlecode.com/hg/gwt-cropper-magnifier.jpg' alt='Gwt Cropper plugin to crop an image, works also on tablets' width='650' height='480' />

# Documentation #
The documentation is pretty short, but in any case, please, refer to [Wiki-page](http://code.google.com/p/gwt-cropper/wiki/Usage) or [JavaDoc](http://wiki.gwt-cropper.googlecode.com/hg/apidocs/index.html) for details.

Take a look at the [Tips & tricks page](TipsAndTricks.md) to see the most common issues.

# Changelog #
  * **0.5.1** The harvesting of the selection position [is calibrated](http://code.google.com/p/gwt-cropper/issues/detail?id=18).
  * **0.5.0** The new [Preview widget](http://wiki.gwt-cropper.googlecode.com/hg/apidocs/com/google/code/gwt/crop/client/GWTCropperPreview.html) is added ([issue16](http://code.google.com/p/gwt-cropper/issues/detail?id=16)). Now you can declare GWT Cropper in ui.xml files with UiBuilder ([issue17](http://code.google.com/p/gwt-cropper/issues/detail?id=17)). Created new Wiki page [How to use Preview widget](HowToUsePreviewWidget.md).
  * **0.4.6** The cropper is adjusted for using with [GWT-Bootstrap](http://gwtbootstrap.github.io) project ([issue14](http://code.google.com/p/gwt-cropper/issues/detail?id=14)); bug fixes ([issue11](http://code.google.com/p/gwt-cropper/issues/detail?id=11), [issue12](http://code.google.com/p/gwt-cropper/issues/detail?id=12))
  * **0.4.5** Removed hardcoded minimal selection size value ([issue9](http://code.google.com/p/gwt-cropper/issues/detail?id=9)); _StyleResource_ was changed to **public** in order to give developers an opportunity to overwrite default CSS styles ([issue10](http://code.google.com/p/gwt-cropper/issues/detail?id=10), read Wiki tutorial [How to apply custom styles](http://code.google.com/p/gwt-cropper/wiki/Styling))
  * **0.4.4** Added method _setSize(int width, int height)_ ([issue7](http://code.google.com/p/gwt-cropper/issues/detail?id=7))
  * **0.4.2, 0.4.3:** Added couple of public methods to set up initial size and position for the selection ([issue6](https://code.google.com/p/gwt-cropper/issues/detail?id=6), [issue8](http://code.google.com/p/gwt-cropper/issues/detail?id=8))
  * **0.4.1:** Resolved issues with Internet Explorer browser and Win8 tablets. Tested on IE version 7-10 and Microsoft Surface tablet. ([issue4](http://code.google.com/p/gwt-cropper/issues/detail?id=4))
  * **0.4.0:** Added tablet support. Tested on iPad 2,3 and a handful of Android devices. ([issue1](http://code.google.com/p/gwt-cropper/issues/detail?id=1))
  * **0.3.5:** Fixed bug with "sticky" edges

# Contribution #
Dear colleagues, **GWT Cropper** is an open source project, so any contribution is highly appreciated. If you wish to send me any bugfix, improvement or new feature, please create a new issue in the [Issue Tracker](https://code.google.com/p/gwt-cropper/issues) and attach a [patch file](https://www.google.co.uk/search?q=hg+patch) with your changeset.
Thanks!