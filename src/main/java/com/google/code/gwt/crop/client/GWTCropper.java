package com.google.code.gwt.crop.client;

import com.google.code.gwt.crop.client.widget.DraggableHandle;
import com.google.code.gwt.crop.client.widget.DraggableHandle.IOnGrag;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DragEvent;
import com.google.gwt.event.dom.client.DragHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * GWT Cropper - widget allowing to select any area on the top of a picture 
 * in order to crop it.
 * 
 * @author ilja
 *
 */
public class GWTCropper extends HTMLPanel {
	private final IBundleResources bundleResources = GWT.create(IBundleResources.class);
	
	// canvas sizes
	private int nOuterWidth;
	private int nOuterHeight;
	
	// selection coordinates
	private int nInnerX;
	private int nInnerY;
	private int nInnerWidth;
	private int nInnerHeight;
	
	private final AbsolutePanel _container;
	
	/**
	 * Bundle of all resources
	 * 
	 * @author ilja
	 *
	 */
	public interface IBundleResources extends ClientBundle {

		interface GWTCropperStyle extends CssResource {
		    String base();
		    String imageCanvas();
		    String selection();
		    String handlesContainer();
		    String handle();
		}
	
		@Source("GWTCropper.css")
		GWTCropperStyle css();
		
		/*
		@Source("imageBundle/launch.png")
		ImageResource launch();
		*/
	}
	/**
	 * Constructor. Requires URL to the full image to be cropped
	 * 
	 * @param strImageURL
	 */
	public GWTCropper(String strImageURL) {
		super("");
		
		bundleResources.css().ensureInjected();
		this._container = new AbsolutePanel();
		this.addCanvas(strImageURL);
	}

	/**
	 * Adds a canvas with background image
	 * 
	 * @param src - image URL
	 */
	private void addCanvas(final String src) {
		
		super.setStyleName(bundleResources.css().base());
		
		final Image image = new Image(src);
		image.setStyleName(bundleResources.css().imageCanvas());
		image.addLoadHandler(new LoadHandler() {

			public void onLoad(LoadEvent event) {
				nOuterWidth = image.getWidth();
				nOuterHeight = image.getHeight();
				_container.setWidth(nOuterWidth + "px");
				_container.setHeight(nOuterHeight + "px");
				addSelection(src);
			}
			
		});
		this._container.add(image, 0, 0);
		this.add(this._container);
	}
	
	/**
	 * Adds initial selection
	 * 
	 */
	private void addSelection(final String src) {
		AbsolutePanel selectionContainer = new AbsolutePanel();
		selectionContainer.addStyleName(this.bundleResources.css().selection());
		
		// set initial coordinates
		this.nInnerX = (int) (nOuterWidth * 0.2);
		this.nInnerY = (int) (nOuterHeight * 0.2);
		this.nInnerWidth = 300;
		this.nInnerHeight = 200;
		
		selectionContainer.setWidth(this.nInnerWidth + "px");
		selectionContainer.setHeight(this.nInnerHeight + "px");
		
		// add background image for selection
		selectionContainer.add(new Image(src), -this.nInnerX, -this.nInnerY);
		this._container.add(selectionContainer, this.nInnerX, this.nInnerY);
		
		AbsolutePanel handlesContainer = this.addSelectionHandles(selectionContainer);
		
		this._container.add(handlesContainer, this.nInnerX, this.nInnerY);
	}

	/**
	 * Add special handles. User can drag these handle and change form of the selected area
	 * @return
	 */
	private AbsolutePanel addSelectionHandles(final AbsolutePanel selectionContainer) {
		
		// add selection handles
		final AbsolutePanel handlesContainer = new AbsolutePanel();
		
		handlesContainer.setWidth(this.nInnerWidth + "px");
		handlesContainer.setHeight(this.nInnerHeight + "px");
		
		handlesContainer.setStyleName(this.bundleResources.css().handlesContainer());
		handlesContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		
		final DraggableHandle backgroundHandle = new DraggableHandle();
		backgroundHandle.setParentElement(this._container.getElement());
		backgroundHandle.setWidth(this.nInnerWidth + "px");
		backgroundHandle.setHeight(this.nInnerHeight + "px");
		backgroundHandle.getElement().getStyle().setCursor(Cursor.MOVE);
		backgroundHandle.setOnDrag(new IOnGrag() {

			int offsetX = -1;
			int offsetY = -1;
			
			public void onDrag(int cursorX, int cursorY) {
				
				if (offsetX == -1) {
					offsetX = cursorX - _container.getWidgetLeft(handlesContainer);
				}
				if (offsetY == -1) {
					offsetY = cursorY - _container.getWidgetTop(handlesContainer);
				}
				
				Element el = handlesContainer.getElement();
				
				int x = cursorX - offsetX;
				int y = cursorY - offsetY;
				
				el.getStyle().setLeft(x, Unit.PX);
				el.getStyle().setTop(y, Unit.PX);
				
				Element el2 = selectionContainer.getElement();
				el2.getStyle().setLeft(x, Unit.PX);
				el2.getStyle().setTop(y, Unit.PX);
				
			}

			public void resetCursorOffset() {
				this.offsetX = -1;
				this.offsetY = -1;
			}
			
		});
		handlesContainer.add(backgroundHandle, 0, 0);	

		HTMLPanel topLeftHandle = new HTMLPanel("");
		topLeftHandle.setStyleName(this.bundleResources.css().handle());
		handlesContainer.add(topLeftHandle, -5, -5);
		
		HTMLPanel topRightHandle = new HTMLPanel("");
		topRightHandle.setStyleName(this.bundleResources.css().handle());
		handlesContainer.add(topRightHandle, this.nInnerWidth - 5, -5);
		
		HTMLPanel bottomLeftHandle = new HTMLPanel("");
		bottomLeftHandle.setStyleName(this.bundleResources.css().handle());
		handlesContainer.add(bottomLeftHandle, -5, this.nInnerHeight - 5);
		
		HTMLPanel bottomRightHandle = new HTMLPanel("");
		bottomRightHandle.setStyleName(this.bundleResources.css().handle());
		handlesContainer.add(bottomRightHandle, this.nInnerWidth - 5, this.nInnerHeight - 5);
		return handlesContainer;
	}
}