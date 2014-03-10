package org.hbabcock.adbtm;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class MouseView extends View {
	private static final boolean DEBUG = true;
    private static final String TAG = "MouseView";

    public int height = 0;
	public int width = 0;

	public MouseView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
    @Override
    protected void onSizeChanged(int new_width, int new_height, int xOld, int yOld)
    {
    	super.onSizeChanged(new_width, new_height, xOld, yOld);
    	
    	width = new_width;
    	height = new_height;

    	if (DEBUG) Log.i(TAG, "onSizeChanged," + width + "," + height);		
    }    

}
