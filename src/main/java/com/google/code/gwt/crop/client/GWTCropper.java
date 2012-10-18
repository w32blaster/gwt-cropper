package com.google.code.gwt.crop.client;

import com.google.code.gwt.crop.client.widget.DraggableHandle;
import com.google.code.gwt.crop.client.widget.DraggableHandle.IOnGrag;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

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
		
		AbsolutePanel handlesContainer = this.buildSelectionArea(selectionContainer);
		
		this._container.add(handlesContainer, this.nInnerX, this.nInnerY);
	}

	/**
	 * Add special handles. User can drag these handle and change form of the selected area
	 * 
	 * @return container with handles and needed attached event listeners
	 */
	private AbsolutePanel buildSelectionArea(final AbsolutePanel selectionContainer) {
		
		// add selection handles
		final AbsolutePanel handlesContainer = new AbsolutePanel();
		
		handlesContainer.setWidth(this.nInnerWidth + "px");
		handlesContainer.setHeight(this.nInnerHeight + "px");
		
		handlesContainer.setStyleName(this.bundleResources.css().handlesContainer());
		handlesContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		
		// append background
		DraggableHandle draggableBackground = this.appendDraggableBackground(selectionContainer, handlesContainer);	

		// append top left corner handler
		this.appendTopLeftCornerHandle(selectionContainer, handlesContainer, draggableBackground);
		
		// append top right corner handler
		this.appendTopRightCornerHandle(selectionContainer, handlesContainer, draggableBackground);
		
		// append bottom left corner handler
		this.appendBottomLeftCornerHandle(selectionContainer, handlesContainer, draggableBackground);
		
		// append bottom right corner handler
		this.appendBottomRightCornerHandle(selectionContainer, handlesContainer, draggableBackground);
		
		return handlesContainer;
	}

	/**
	 * Appends the bottom left corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendBottomLeftCornerHandle(final AbsolutePanel sc, final AbsolutePanel hc, final DraggableHandle bgr) {
		
		DraggableHandle bottomLeftHandle = new DraggableHandle();
		bottomLeftHandle.setParentElement(this._container.getElement());
		bottomLeftHandle.setStyleName(this.bundleResources.css().handle());
		bottomLeftHandle.getElement().getStyle().setCursor(Cursor.SW_RESIZE);
		
		bottomLeftHandle.setOnDrag(new IOnGrag() {

			int initX = -1;
			int initY = -1;
			int initW = -1;
			int initH = -1;
			
			/**
			 * {@inheritDoc}
			 */
			public void onDrag(int cursorX, int cursorY) {
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(hc);
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(hc) + nInnerHeight;
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (initX - cursorX);
				nInnerHeight = initH + (cursorY - initY);
				
				Element el = hc.getElement();
				
				el.getStyle().setLeft(cursorX, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Element el2 = sc.getElement();
				el2.getStyle().setLeft(cursorX, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Image backgroundImage = (Image) sc.getWidget(0);
				Element elImg = backgroundImage.getElement();
				elImg.getStyle().setLeft(-cursorX, Unit.PX);
				
				Element el3 = bgr.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);		
			}

			/**
			 * {@inheritDoc}
			 */
			public void resetInitials() {
				this.initX = -1;
				this.initY = -1;
				this.initW = -1;
				this.initH = -1;
			}
			
		});
		
		bottomLeftHandle.getElement().getStyle().setLeft(-5, Unit.PX);
		bottomLeftHandle.getElement().getStyle().setBottom(-5, Unit.PX);
		hc.add(bottomLeftHandle);
		
	}
	
	/**
	 * Appends the bottom left corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendBottomRightCornerHandle(final AbsolutePanel sc, final AbsolutePanel hc, final DraggableHandle bgr) {
		
		DraggableHandle bottomRightHandle = new DraggableHandle();
		bottomRightHandle.setParentElement(this._container.getElement());
		bottomRightHandle.setStyleName(this.bundleResources.css().handle());
		bottomRightHandle.getElement().getStyle().setCursor(Cursor.SE_RESIZE);
		
		bottomRightHandle.setOnDrag(new IOnGrag() {

			int initX = -1;
			int initY = -1;
			int initW = -1;
			int initH = -1;
			
			/**
			 * {@inheritDoc}
			 */
			public void onDrag(int cursorX, int cursorY) {
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(hc) + nInnerWidth;
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(hc) + nInnerHeight;
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (cursorX - initX);
				nInnerHeight = initH + (cursorY - initY);
				
				Element el = hc.getElement();
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Element el2 = sc.getElement();
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Element el3 = bgr.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);		
			}

			/**
			 * {@inheritDoc}
			 */
			public void resetInitials() {
				this.initX = -1;
				this.initY = -1;
				this.initW = -1;
				this.initH = -1;
			}
			
		});
		
		bottomRightHandle.getElement().getStyle().setRight(-5, Unit.PX);
		bottomRightHandle.getElement().getStyle().setBottom(-5, Unit.PX);
		hc.add(bottomRightHandle);
		
	}
	
	/**
	 * Appends the top right corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendTopRightCornerHandle(final AbsolutePanel sc, final AbsolutePanel hc, final DraggableHandle bgr) {
		
		DraggableHandle topRightHandle = new DraggableHandle();
		topRightHandle.setParentElement(this._container.getElement());
		topRightHandle.setStyleName(this.bundleResources.css().handle());
		topRightHandle.getElement().getStyle().setCursor(Cursor.NE_RESIZE);
		
		topRightHandle.setOnDrag(new IOnGrag() {

			int initX = -1;
			int initY = -1;
			int initW = -1;
			int initH = -1;
			
			/**
			 * {@inheritDoc}
			 */
			public void onDrag(int cursorX, int cursorY) {
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(hc) + nInnerWidth;
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(hc);
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (cursorX - initX);
				nInnerHeight = initH + (initY - cursorY);
				
				Element el = hc.getElement();
				
				el.getStyle().setTop(cursorY, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Element el2 = sc.getElement();
				el2.getStyle().setTop(cursorY, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Image backgroundImage = (Image) sc.getWidget(0);
				Element elImg = backgroundImage.getElement();
				elImg.getStyle().setTop(-cursorY, Unit.PX);
				
				Element el3 = bgr.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);		
			}

			/**
			 * {@inheritDoc}
			 */
			public void resetInitials() {
				this.initX = -1;
				this.initY = -1;
				this.initW = -1;
				this.initH = -1;
			}
			
		});
		
		topRightHandle.getElement().getStyle().setRight(-5, Unit.PX);
		topRightHandle.getElement().getStyle().setTop(-5, Unit.PX);
		hc.add(topRightHandle);
		
	}

	
	/**
	 * Appends the top left corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendTopLeftCornerHandle(final AbsolutePanel sc, final AbsolutePanel hc, final DraggableHandle bgr) {
		DraggableHandle topLeftHandle = new DraggableHandle();
		topLeftHandle.setParentElement(this._container.getElement());
		topLeftHandle.setStyleName(this.bundleResources.css().handle());
		topLeftHandle.getElement().getStyle().setCursor(Cursor.NW_RESIZE);
		
		topLeftHandle.setOnDrag(new IOnGrag() {

			int initX = -1;
			int initY = -1;
			int initW = -1;
			int initH = -1;
			
			/**
			 * {@inheritDoc}
			 */
			public void onDrag(int cursorX, int cursorY) {
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(hc);
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(hc);
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (initX - cursorX);
				nInnerHeight = initH + (initY - cursorY);
				
				Element el = hc.getElement();
				
				el.getStyle().setLeft(cursorX, Unit.PX);
				el.getStyle().setTop(cursorY, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Element el2 = sc.getElement();
				el2.getStyle().setLeft(cursorX, Unit.PX);
				el2.getStyle().setTop(cursorY, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				Image backgroundImage = (Image) sc.getWidget(0);
				Element elImg = backgroundImage.getElement();
				elImg.getStyle().setLeft(-cursorX, Unit.PX);
				elImg.getStyle().setTop(-cursorY, Unit.PX);
				
				Element el3 = bgr.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);		
			}

			/**
			 * {@inheritDoc}
			 */
			public void resetInitials() {
				this.initX = -1;
				this.initY = -1;
				this.initW = -1;
				this.initH = -1;
			}
			
		});
		
		hc.add(topLeftHandle, -5, -5);
	}

	/**
	 * Append draggable selection background
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 */
	private DraggableHandle appendDraggableBackground(final AbsolutePanel sc, final AbsolutePanel hc) {
		
		final DraggableHandle backgroundHandle = new DraggableHandle();
		backgroundHandle.setParentElement(this._container.getElement());
		backgroundHandle.setWidth(this.nInnerWidth + "px");
		backgroundHandle.setHeight(this.nInnerHeight + "px");
		backgroundHandle.getElement().getStyle().setCursor(Cursor.MOVE);
		
		backgroundHandle.setOnDrag(new IOnGrag() {

			int offsetX = -1;
			int offsetY = -1;
			
			/**
			 * {@inheritDoc}
			 */
			public void onDrag(int cursorX, int cursorY) {
				
				if (offsetX == -1) {
					offsetX = cursorX - _container.getWidgetLeft(hc);
				}
				if (offsetY == -1) {
					offsetY = cursorY - _container.getWidgetTop(hc);
				}
				
				Element el = hc.getElement();
				
				int x = cursorX - offsetX;
				int y = cursorY - offsetY;
				
				el.getStyle().setLeft(x, Unit.PX);
				el.getStyle().setTop(y, Unit.PX);
				
				Element el2 = sc.getElement();
				el2.getStyle().setLeft(x, Unit.PX);
				el2.getStyle().setTop(y, Unit.PX);
				
				Image backgroundImage = (Image) sc.getWidget(0);
				Element elImg = backgroundImage.getElement();
				elImg.getStyle().setLeft(-x, Unit.PX);
				elImg.getStyle().setTop(-y, Unit.PX);
				
			}

			/**
			 * {@inheritDoc}
			 */
			public void resetInitials() {
				this.offsetX = -1;
				this.offsetY = -1;
			}
			
		});
		hc.add(backgroundHandle, 0, 0);
		
		return backgroundHandle;
	}
}