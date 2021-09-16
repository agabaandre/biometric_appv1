package ug.app.ihrisbiometric;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class MyOutScroller extends ScrollView {
  public MyOutScroller(Context context) {
    super(context);
  }

  public MyOutScroller(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public MyOutScroller(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  @Override public boolean onInterceptTouchEvent(MotionEvent ev) {
    int action = ev.getAction();
    if (action == MotionEvent.ACTION_DOWN) {
      return false;
    }
    return super.onInterceptTouchEvent(ev);
  }
}
