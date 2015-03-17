---
layout: default
title: Wiki GWT-Cropper
header: GWT-Cropper
description: All the common issues and ways of solving.
---

{% include SideBar.html %}

#Introduction

In this chapter we are going to collect all common issues and problems and all the ways how to solve it. If you have any problem and you know how to fix it, then please make a note at comments.

#Drag the background (rubber band)

*When I browse my application on a tablet, I try to drag the selection, but instead of this I drag the whole application background.*

It usually looks like this:

![Drag the whole application instead of only selection](/images/wiki/gwt-moving-background.jpg)

You can see, how I move the selection up, but the white background is moving instead. We even can see the native browser's grey background at the bottom.

The simplest way to solve it is to prevent native touch events. The classical code snippet is here:

```
   function stopScrolling( touchEvent ) { touchEvent.preventDefault(); }

   if (document.addEventListener) {
      document.addEventListener('touchmove', stopScrolling, false);
   } else if (document.attachEvent) {
      document.attachEvent('touchmove', stopScrolling);
   }
```

Put this code to your HTML or JSP host page. This code works in all browsers, even in IE.

You also could improve this function, if it conflicts with native scrolling areas. Please refer to [this](http://stackoverflow.com/questions/10357844/how-to-disable-rubber-band-in-ios-web-apps) discussion.

#Detect tablet/mobile device in compile time

*GWT Cropper* uses module [FormFactor](https://github.com/w32blaster/gwt-cropper/blob/master/src/main/java/com/google/code/gwt/crop/FormFactor.gwt.xml) to detect tablets. Do you want to re-use it in your code? No problem, this module will be available for you, while you use GWT Cropper. 

Let's consider a simple example. Say, you need to implement three different widgets for PC, tablets and mobile devices. And you want to generate different sources for that using [https://developers.google.com/web-toolkit/doc/latest/DevGuideCodingBasicsDeferred deferred binding]. 

![Screenshot of the browser on the PC showing the widget from this tutorial](/images/wiki/screenshot-desktop.jpeg)

![Screeshot of smartphone (Nokia N9) demonstrating the widget from this tutorial.](/images/wiki/screenshot-nokia-n9.jpg)

![Screeshot of the tablet iPad demonstrating the widget from this tutorial.](/images/wiki/screenshot-ipad.jpg)

Then you should create these three widgets (basic for desktops and two others expanding it).

*BasicWidget.java*

```
public class BasicWidget {

	public Widget build() {
		return new Button("Desktop specific widget (button)");
	}
}
```

*TabletWidget.java*

```
public class TabletWidget extends BasicWidget {

	@Override
	public Widget build() {
		return new Hyperlink("Tablet specific widget (link)", "http://www.example.com");
	}

}
```

*MobileWidget.java*

```
public class MobileWidget extends BasicWidget {

	@Override
	public Widget build() {
		return new Label("Mobile specific widget (label).");
	}

}
```

Then, inehrit FormFactor and declare deffered binding in your *.gwt.xml* file:

```
	<!-- Inherit FormFactor module from GWT-Cropper library -->
	<inherits name='com.google.code.gwt.crop.FormFactor'/>
	
	<!-- Default widget for desktops -->
	<replace-with class="com.google.code.gwt.cropper.demo.client.factor.BasicWidget">
		<when-type-is class="com.google.code.gwt.cropper.demo.client.factor.BasicWidget" />
	</replace-with>
	
	<!-- Tablet widget -->
	<replace-with class="com.google.code.gwt.cropper.demo.client.factor.TabletWidget">
		<when-type-is class="com.google.code.gwt.cropper.demo.client.factor.BasicWidget" />
		<when-property-is name="formfactor" value="tablet" />
	</replace-with>
	
	<!-- Mobile widget -->
	<replace-with class="com.google.code.gwt.cropper.demo.client.factor.MobileWidget">
		<when-type-is class="com.google.code.gwt.cropper.demo.client.factor.BasicWidget" />
		<when-property-is name="formfactor" value="mobile" />
	</replace-with>
```

Finally, use it in your code. Create widget using _GWT.create()_ method and GWT will replace for you this widget to appropriate implementation during the compilation time.

```
public class Application implements EntryPoint {

	private static final BasicWidget widget = GWT.create(BasicWidget.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		VerticalPanel container = new VerticalPanel();
		container.setSpacing(20);

		// header
		Label label = new Label("This demo of FormFactor module.");
		container.add(label);

		// specific widget for the current platform
		container.add(widget.build());

		RootLayoutPanel.get().add(container);
	}

}
```