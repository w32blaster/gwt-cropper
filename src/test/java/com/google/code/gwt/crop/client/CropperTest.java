package com.google.code.gwt.crop.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwtmockito.GwtMock;
import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created on 31/08/15.
 *
 * @author w32blaster
 */
@RunWith(GwtMockitoTestRunner.class)
public class CropperTest {

    @GwtMock
    private MouseOutEvent mouseOutEvent;

    /**
     * If mouse coordinates are within canvas area, then coordinates are not modified
     */
    @Test
    public void should_prevent_dragging_out() {

        GWTCropper cropper = spy(new GWTCropper(""));

        // given
        cropper.nOuterWidth = 400;
        cropper.nOuterHeight = 250;

        // and
        when(mouseOutEvent.getRelativeX(any(Element.class))).thenReturn(10);
        when(mouseOutEvent.getRelativeY(any(Element.class))).thenReturn(20);

        // when
        cropper.onMouseOut(mouseOutEvent);

        // then expected the not modified X and Y
        verify(cropper).provideDragging(10, 20);
    }

    /**
     * When mouse is out of canvas (out of top or left border), then
     * its coordinates are set to 0, preventing moving selection out of the canvas
     */
    @Test
    public void should_stick_selection_to_left_border() {

        GWTCropper cropper = spy(new GWTCropper(""));

        // given
        cropper.nOuterWidth = 400;
        cropper.nOuterHeight = 250;

        // and
        when(mouseOutEvent.getRelativeX(any(Element.class))).thenReturn(-1);
        when(mouseOutEvent.getRelativeY(any(Element.class))).thenReturn(20);

        // when
        cropper.onMouseOut(mouseOutEvent);

        // then expected -1 turns to 0
        verify(cropper).provideDragging(0, 20);
    }
    @Test
    public void should_stick_selection_to_top_border() {

        GWTCropper cropper = spy(new GWTCropper(""));

        // given
        cropper.nOuterWidth = 400;
        cropper.nOuterHeight = 250;

        // and
        when(mouseOutEvent.getRelativeX(any(Element.class))).thenReturn(10);
        when(mouseOutEvent.getRelativeY(any(Element.class))).thenReturn(-2);

        // when
        cropper.onMouseOut(mouseOutEvent);

        // then expected -2 turns to 0
        verify(cropper).provideDragging(10, 0);
    }

    /**
     * When mouse cursor goes out of canvas (right or bottom borders), then
     * its coordinates should stay within the canvas
     */
    @Test
    public void should_stick_selection_to_bottom_border() {

        GWTCropper cropper = spy(new GWTCropper(""));

        // given
        cropper.nOuterWidth = 400;
        cropper.nOuterHeight = 250;

        // and
        when(mouseOutEvent.getRelativeX(any(Element.class))).thenReturn(20);
        when(mouseOutEvent.getRelativeY(any(Element.class))).thenReturn(251);

        // when
        cropper.onMouseOut(mouseOutEvent);

        // then expected 251 turns to 250
        verify(cropper).provideDragging(20, 250);

    }

    @Test
    public void should_stick_selection_to_right_border() {

        GWTCropper cropper = spy(new GWTCropper(""));

        // given
        cropper.nOuterWidth = 400;
        cropper.nOuterHeight = 250;

        // and
        when(mouseOutEvent.getRelativeX(any(Element.class))).thenReturn(401);
        when(mouseOutEvent.getRelativeY(any(Element.class))).thenReturn(30);

        // when
        cropper.onMouseOut(mouseOutEvent);

        // then expected 401 turns to 400
        verify(cropper).provideDragging(400, 30);

    }

}
