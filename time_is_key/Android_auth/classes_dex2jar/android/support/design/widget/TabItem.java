package android.support.design.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.design.R.styleable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;

public final class TabItem
  extends View
{
  final int mCustomLayout;
  final Drawable mIcon;
  final CharSequence mText;
  
  public TabItem(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TabItem(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.TabItem);
    this.mText = localTintTypedArray.getText(R.styleable.TabItem_android_text);
    this.mIcon = localTintTypedArray.getDrawable(R.styleable.TabItem_android_icon);
    this.mCustomLayout = localTintTypedArray.getResourceId(R.styleable.TabItem_android_layout, 0);
    localTintTypedArray.recycle();
  }
}
