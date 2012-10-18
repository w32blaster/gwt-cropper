package com.google.code.gwt.crop.client;

import com.google.code.gwt.crop.client.widget.DraggableHandle;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * GWT Cropper - widget allowing to select any area on the top of a picture 
 * in order to crop it.
 * 
 * @author ilja
 *
 */
public class GWTCropper extends HTMLPanel implements MouseMoveHandler, MouseUpHandler {
	
	private final IBundleResources bundleResources = GWT.create(IBundleResources.class);
	
	// canvas sizes
	private int nOuterWidth;
	private int nOuterHeight;
	
	// selection coordinates
	private int nInnerX;
	private int nInnerY;
	private int nInnerWidth;
	private int nInnerHeight;
	
	private boolean isDown = false;
	private byte action = Constants.DRAG_NONE;
	
	private int initX = -1;
	private int initY = -1;
	private int initW = -1;
	private int initH = -1;
	
	private int offsetX = -1;
	private int offsetY = -1;
	
	
	private final AbsolutePanel _container;
	private AbsolutePanel handlesContainer;
	private AbsolutePanel selectionContainer = new AbsolutePanel();
	private DraggableHandle draggableBackground;
	
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
		
		addDomHandler(this, MouseMoveEvent.getType());
		addDomHandler(this, MouseUpEvent.getType());
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
		
		this.buildSelectionArea(selectionContainer);
		
		this._container.add(this.handlesContainer, this.nInnerX, this.nInnerY);
	}

	/**
	 * Add special handles. User can drag these handle and change form of the selected area
	 * 
	 * @return container with handles and needed attached event listeners
	 */
	private AbsolutePanel buildSelectionArea(final AbsolutePanel selectionContainer) {
		
		// add selection handles
		this.handlesContainer = new AbsolutePanel();
		
		this.handlesContainer.setWidth(this.nInnerWidth + "px");
		this.handlesContainer.setHeight(this.nInnerHeight + "px");
		
		this.handlesContainer.setStyleName(this.bundleResources.css().handlesContainer());
		this.handlesContainer.getElement().getStyle().setOverflow(Overflow.VISIBLE);
		
		// append background
		this.draggableBackground = this.appendDraggableBackground();	

		// append top left corner handler
		this.appendTopLeftCornerHandle();
		
		// append top right corner handler
		this.appendTopRightCornerHandle();
		
		// append bottom left corner handler
		this.appendBottomLeftCornerHandle();
		
		// append bottom right corner handler
		this.appendBottomRightCornerHandle();
		
		return handlesContainer;
	}

	/**
	 * Appends the bottom left corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendBottomLeftCornerHandle() {
		
		DraggableHandle bottomLeftHandle = new DraggableHandle();
		bottomLeftHandle.setStyleName(this.bundleResources.css().handle());
		bottomLeftHandle.getElement().getStyle().setCursor(Cursor.SW_RESIZE);
		
		bottomLeftHandle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = Constants.DRAG_BOTTOM_LEFT_CORNER;
			}
		});
		
		bottomLeftHandle.getElement().getStyle().setLeft(-5, Unit.PX);
		bottomLeftHandle.getElement().getStyle().setBottom(-5, Unit.PX);
		this.handlesContainer.add(bottomLeftHandle);
		
	}
	
	/**
	 * Appends the bottom left corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendBottomRightCornerHandle() {
		
		DraggableHandle bottomRightHandle = new DraggableHandle();
		bottomRightHandle.setStyleName(this.bundleResources.css().handle());
		bottomRightHandle.getElement().getStyle().setCursor(Cursor.SE_RESIZE);
		
		bottomRightHandle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = Constants.DRAG_BOTTOM_RIGHT_CORNER;
			}
		});
		
		bottomRightHandle.getElement().getStyle().setRight(-5, Unit.PX);
		bottomRightHandle.getElement().getStyle().setBottom(-5, Unit.PX);
		this.handlesContainer.add(bottomRightHandle);
		
	}
	
	/**
	 * Appends the top right corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendTopRightCornerHandle() {
		
		DraggableHandle topRightHandle = new DraggableHandle();
		topRightHandle.setStyleName(this.bundleResources.css().handle());
		topRightHandle.getElement().getStyle().setCursor(Cursor.NE_RESIZE);
		
		topRightHandle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = Constants.DRAG_TOP_RIGHT_CORNER;
			}
		});
		
		topRightHandle.getElement().getStyle().setRight(-5, Unit.PX);
		topRightHandle.getElement().getStyle().setTop(-5, Unit.PX);
		this.handlesContainer.add(topRightHandle);
		
	}

	
	/**
	 * Appends the top left corner with handle and assigns appropriate event processing to it
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 * @param bgr - draggable background-container, holding all handles
	 */
	private void appendTopLeftCornerHandle() {
		DraggableHandle topLeftHandle = new DraggableHandle();
		topLeftHandle.setStyleName(this.bundleResources.css().handle());
		topLeftHandle.getElement().getStyle().setCursor(Cursor.NW_RESIZE);
		
		topLeftHandle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = Constants.DRAG_TOP_LEFT_CORNER;
			}
		});
		
		this.handlesContainer.add(topLeftHandle, -5, -5);
	}

	/**
	 * Append draggable selection background
	 * 
	 * @param sc - container of selection
	 * @param hc - container of handles
	 */
	private DraggableHandle appendDraggableBackground() {
		
		final DraggableHandle backgroundHandle = new DraggableHandle();
		backgroundHandle.setWidth(this.nInnerWidth + "px");
		backgroundHandle.setHeight(this.nInnerHeight + "px");
		backgroundHandle.getElement().getStyle().setCursor(Cursor.MOVE);
		
		backgroundHandle.addMouseDownHandler(new MouseDownHandler() {

			public void onMouseDown(MouseDownEvent event) {
				isDown = true;
				action = Constants.DRAG_BACKGROUND;
			}
		});
		
		this.handlesContainer.add(backgroundHandle, 0, 0);
		
		return backgroundHandle;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onMouseUp(MouseUpEvent event) {
		this.isDown = false;
		this.reset();
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void onMouseMove(MouseMoveEvent event) {
		
		if (this.isDown) {
			this.provideDragging(event.getRelativeX(this._container.getElement()),
						event.getRelativeY(this._container.getElement()));
		}
	}
	
	/**
	 * provides dragging action
	 * 
	 * @param cursorX
	 * @param cursorY
	 */
	private void provideDragging(int cursorX, int cursorY) {
		
		Element el = null;
		Element el2 = null;
		Element elImg = null;
		
		switch (this.action) {
			
			case Constants.DRAG_BACKGROUND:
				
				if (offsetX == -1) {
					offsetX = cursorX - _container.getWidgetLeft(this.handlesContainer);
				}
				if (offsetY == -1) {
					offsetY = cursorY - _container.getWidgetTop(this.handlesContainer);
				}
				
				el = this.handlesContainer.getElement();
				
				int x = cursorX - offsetX;
				int y = cursorY - offsetY;
				
				el.getStyle().setLeft(x, Unit.PX);
				el.getStyle().setTop(y, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setLeft(x, Unit.PX);
				el2.getStyle().setTop(y, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-x, Unit.PX);
				elImg.getStyle().setTop(-y, Unit.PX);
				break;
				
				
			case Constants.DRAG_TOP_LEFT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer);
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer);
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (initX - cursorX);
				nInnerHeight = initH + (initY - cursorY);
				
				el = this.handlesContainer.getElement();
				
				el.getStyle().setLeft(cursorX, Unit.PX);
				el.getStyle().setTop(cursorY, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setLeft(cursorX, Unit.PX);
				el2.getStyle().setTop(cursorY, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-cursorX, Unit.PX);
				elImg.getStyle().setTop(-cursorY, Unit.PX);
				
				Element el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);
				break;
				
			case Constants.DRAG_TOP_RIGHT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer) + nInnerWidth;
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer);
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (cursorX - initX);
				nInnerHeight = initH + (initY - cursorY);
				
				el = this.handlesContainer.getElement();
				
				el.getStyle().setTop(cursorY, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setTop(cursorY, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setTop(-cursorY, Unit.PX);
				
				el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);		
				break;
				
			case Constants.DRAG_BOTTOM_LEFT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer);
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer) + nInnerHeight;
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (initX - cursorX);
				nInnerHeight = initH + (cursorY - initY);
				
				el = this.handlesContainer.getElement();
				
				el.getStyle().setLeft(cursorX, Unit.PX);
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setLeft(cursorX, Unit.PX);
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				elImg = ((Image) this.selectionContainer.getWidget(0)).getElement();
				elImg.getStyle().setLeft(-cursorX, Unit.PX);
				
				el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);
				break;
				
			case Constants.DRAG_BOTTOM_RIGHT_CORNER:
				
				if (initX == -1) {
					initX = _container.getWidgetLeft(this.handlesContainer) + nInnerWidth;
					initW = nInnerWidth;
				}
				if (initY == -1) {
					initY = _container.getWidgetTop(this.handlesContainer) + nInnerHeight;
					initH = nInnerHeight;
				}
				
				nInnerWidth = initW + (cursorX - initX);
				nInnerHeight = initH + (cursorY - initY);
				
				el = this.handlesContainer.getElement();
				el.getStyle().setWidth(nInnerWidth, Unit.PX);
				el.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el2 = this.selectionContainer.getElement();
				el2.getStyle().setWidth(nInnerWidth, Unit.PX);
				el2.getStyle().setHeight(nInnerHeight, Unit.PX);
				
				el3 = this.draggableBackground.getElement();
				el3.getStyle().setWidth(nInnerWidth, Unit.PX);
				el3.getStyle().setHeight(nInnerHeight, Unit.PX);
				break;
				
			default:
				break;
		}
		
	}
	
	/**
	 * Resets all initial values.
	 * 
	 */
	private void reset() {
		
		this.initX = -1;
		this.initY = -1;
		this.initW = -1;
		this.initH = -1;
		this.offsetX = -1;
		this.offsetY = -1;
		this.action = Constants.DRAG_NONE;
	}
}