package com.getbase.floatingactionbutton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.ShapeDrawable.ShaderFactory;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class FloatingActionButton extends ImageButton {

  public static final int SIZE_NORMAL = 0;
  public static final int SIZE_MINI = 1;

  @Retention(RetentionPolicy.SOURCE)
  @IntDef({ SIZE_NORMAL, SIZE_MINI })
  public @interface FAB_SIZE {
  }

  private static final int HALF_TRANSPARENT_WHITE = Color.argb(128, 255, 255, 255);
  private static final int HALF_TRANSPARENT_BLACK = Color.argb(128, 0, 0, 0);

  int mColorNormal;
  int mColorPressed;
  String mTitle;
  @DrawableRes
  private int mIcon;
  private int mSize;

  private float mCircleSize;
  private float mShadowRadius;
  private float mShadowOffset;
  private int mDrawableSize;

  public FloatingActionButton(Context context) {
    this(context, null);
  }

  public FloatingActionButton(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public FloatingActionButton(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
    init(context, attrs);
  }

  void init(Context context, AttributeSet attributeSet) {
    TypedArray attr = context.obtainStyledAttributes(attributeSet, R.styleable.FloatingActionButton, 0, 0);
    mColorNormal = attr.getColor(R.styleable.FloatingActionButton_fab_colorNormal, getColor(android.R.color.holo_blue_dark));
    mColorPressed = attr.getColor(R.styleable.FloatingActionButton_fab_colorPressed, getColor(android.R.color.holo_blue_light));
    mSize = attr.getInt(R.styleable.FloatingActionButton_fab_size, SIZE_NORMAL);
    mIcon = attr.getResourceId(R.styleable.FloatingActionButton_fab_icon, 0);
    mTitle = attr.getString(R.styleable.FloatingActionButton_fab_title);
    attr.recycle();

    updateCircleSize();
    mShadowRadius = getDimension(R.dimen.fab_shadow_radius);
    mShadowOffset = getDimension(R.dimen.fab_shadow_offset);
    updateDrawableSize();

    updateBackground();
  }

  private void updateDrawableSize() {
    mDrawableSize = (int) (mCircleSize + 2 * mShadowRadius);
  }

  private void updateCircleSize() {
    mCircleSize = getDimension(mSize == SIZE_NORMAL ? R.dimen.fab_size_normal : R.dimen.fab_size_mini);
  }

  public void setSize(@FAB_SIZE int size) {
    if (size != SIZE_MINI && size != SIZE_NORMAL) {
      throw new IllegalArgumentException("Use @FAB_SIZE constants only!");
    }

    if (mSize != size) {
      mSize = size;
      updateCircleSize();
      updateDrawableSize();
      updateBackground();
    }
  }

  @FAB_SIZE
  public int getSize() {
    return mSize;
  }

  public void setIcon(@DrawableRes int icon) {
    if (mIcon != icon) {
      mIcon = icon;
      updateBackground();
    }
  }

  /**
   * @return the current Color for normal state.
   */
  public int getColorNormal() {
    return mColorNormal;
  }

  public void setColorNormalResId(@ColorRes int colorNormal) {
    setColorNormal(getColor(colorNormal));
  }

  public void setColorNormal(int color) {
    if (mColorNormal != color) {
      mColorNormal = color;
      updateBackground();
    }
  }

  /**
   * @return the current color for pressed state.
   */
  public int getColorPressed() {
    return mColorPressed;
  }

  public void setColorPressedResId(@ColorRes int colorPressed) {
    setColorPressed(getColor(colorPressed));
  }

  public void setColorPressed(int color) {
    if (mColorPressed != color) {
      mColorPressed = color;
      updateBackground();
    }
  }

  int getColor(@ColorRes int id) {
    return getResources().getColor(id);
  }

  float getDimension(@DimenRes int id) {
    return getResources().getDimension(id);
  }

  public void setTitle(String title) {
    mTitle = title;
    TextView label = (TextView) getTag(R.id.fab_label);
    if (label != null)
      label.setText(title);
  }

  public String getTitle() {
    return mTitle;
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    setMeasuredDimension(mDrawableSize, mDrawableSize);
  }

  void updateBackground() {
    final float strokeWidth = getDimension(R.dimen.fab_stroke_width);
    final float halfStrokeWidth = strokeWidth / 2f;

    LayerDrawable layerDrawable = new LayerDrawable(
        new Drawable[] {
            getResources().getDrawable(mSize == SIZE_NORMAL ? R.drawable.fab_bg_normal : R.drawable.fab_bg_mini),
            createFillDrawable(),
            createOuterStrokeDrawable(strokeWidth),
            createInnerStrokesDrawable(strokeWidth),
            getIconDrawable()
        });

    int iconOffset = (int) (mCircleSize - getDimension(R.dimen.fab_icon_size)) / 2;

    int circleInsetHorizontal = (int) (mShadowRadius);
    int circleInsetTop = (int) (mShadowRadius - mShadowOffset);
    int circleInsetBottom = (int) (mShadowRadius + mShadowOffset);

    layerDrawable.setLayerInset(1,
        circleInsetHorizontal,
        circleInsetTop,
        circleInsetHorizontal,
        circleInsetBottom);

    layerDrawable.setLayerInset(2,
        (int) (circleInsetHorizontal - halfStrokeWidth),
        (int) (circleInsetTop - halfStrokeWidth),
        (int) (circleInsetHorizontal - halfStrokeWidth),
        (int) (circleInsetBottom - halfStrokeWidth));

    layerDrawable.setLayerInset(3,
        (int) (circleInsetHorizontal + halfStrokeWidth),
        (int) (circleInsetTop + halfStrokeWidth),
        (int) (circleInsetHorizontal + halfStrokeWidth),
        (int) (circleInsetBottom + halfStrokeWidth));

    layerDrawable.setLayerInset(4,
        circleInsetHorizontal + iconOffset,
        circleInsetTop + iconOffset,
        circleInsetHorizontal + iconOffset,
        circleInsetBottom + iconOffset);

    setBackgroundCompat(layerDrawable);
  }

  Drawable getIconDrawable() {
    if (mIcon != 0) {
      return getResources().getDrawable(mIcon);
    } else {
      return new ColorDrawable(Color.TRANSPARENT);
    }
  }

  private StateListDrawable createFillDrawable() {
    StateListDrawable drawable = new StateListDrawable();
    drawable.addState(new int[] { android.R.attr.state_pressed }, createCircleDrawable(mColorPressed));
    drawable.addState(new int[] { }, createCircleDrawable(mColorNormal));
    return drawable;
  }

  private Drawable createCircleDrawable(int color) {
    ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

    final Paint paint = shapeDrawable.getPaint();
    paint.setAntiAlias(true);
    paint.setColor(color);

    return shapeDrawable;
  }

  private Drawable createOuterStrokeDrawable(float strokeWidth) {
    ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());

    final Paint paint = shapeDrawable.getPaint();
    paint.setAntiAlias(true);
    paint.setStrokeWidth(strokeWidth);
    paint.setStyle(Style.STROKE);
    paint.setColor(Color.BLACK);
    paint.setAlpha(opacityToAlpha(0.02f));

    return shapeDrawable;
  }

  private int opacityToAlpha(float opacity) {
    return (int) (255f * opacity);
  }

  private Drawable createInnerStrokesDrawable(float strokeWidth) {
    ShapeDrawable innerBottom = new ShapeDrawable(new OvalShape());

    final Paint bottomPaint = innerBottom.getPaint();
    bottomPaint.setAntiAlias(true);
    bottomPaint.setStrokeWidth(strokeWidth);
    bottomPaint.setStyle(Style.STROKE);
    bottomPaint.setAlpha(opacityToAlpha(0.04f));
    innerBottom.setShaderFactory(new ShaderFactory() {
      @Override
      public Shader resize(int width, int height) {
        return new LinearGradient(width / 2, 0, width / 2, height,
            new int[] { Color.TRANSPARENT, HALF_TRANSPARENT_BLACK, Color.BLACK },
            new float[] { 0f, 0.8f, 1f },
            TileMode.CLAMP
        );
      }
    });

    ShapeDrawable innerTop = new ShapeDrawable(new OvalShape());

    final Paint topPaint = innerTop.getPaint();
    topPaint.setAntiAlias(true);
    topPaint.setStrokeWidth(strokeWidth);
    topPaint.setStyle(Style.STROKE);
    topPaint.setAlpha(opacityToAlpha(0.8f));
    innerTop.setShaderFactory(new ShaderFactory() {
      @Override
      public Shader resize(int width, int height) {
        return new LinearGradient(width / 2, 0, width / 2, height,
            new int[] { Color.WHITE, HALF_TRANSPARENT_WHITE, Color.TRANSPARENT },
            new float[] { 0f, 0.2f, 1f },
            TileMode.CLAMP
        );
      }
    });

    return new LayerDrawable(new Drawable[] {
        innerBottom,
        innerTop
    });
  }

  @SuppressWarnings("deprecation")
  @SuppressLint("NewApi")
  private void setBackgroundCompat(Drawable drawable) {
    if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
      setBackground(drawable);
    } else {
      setBackgroundDrawable(drawable);
    }
  }
}
