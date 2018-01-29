/*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhangshuai.yinglong_demo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

import static android.view.Gravity.CENTER;


public class MultiPagerSlidingTabStrip extends LinearLayout {

	private LinearLayout container1;
	private LinearLayout container2;
	private LayoutParams layoutParamsMW;
	private static final String TAG = MultiPagerSlidingTabStrip.class.getName();
	private int tabIndicatorMaginTop = 14;

	public interface IconTabProvider {
		public int getPageIconResId(int position);
	}

	public interface TitleIconTabProvider {
		int NONE_ICON = -1;

		int getPageIconResId(int position);
	}

	// @formatter:off
	private static final int[] ATTRS = new int[] {
		android.R.attr.textSize,
		android.R.attr.textColor
    };
	// @formatter:on

	private LayoutParams defaultTabLayoutParams;
	private LayoutParams expandedTabLayoutParams;

	private final PageListener pageListener = new PageListener();
	public OnPageChangeListener delegatePageListener;

	private LinearLayout tabsContainer;
	private ViewPager pager;

	private int tabCount;

	private int currentPosition = 0;
	private float currentPositionOffset = 0f;

	private Paint rectPaint;
	private Paint dividerPaint;

	private int indicatorColor = 0xFF666666;
	private int underlineColor = 0x1A000000;
	private int dividerColor = 0x1A000000;

	private boolean shouldExpand = false;
	private boolean textAllCaps = true;

	private int scrollOffset = 52;
	private int indicatorHeight = 8;
	private int underlineHeight = 2;
	private int dividerPadding = 12;
	private int tabPaddingLeftRight = 24;
	private int tabPaddingTop = 24;
	private int dividerWidth = 1;

	private int tabTextSize = 12;
	private int tabTextColor = 0xFF666666;
	private int tabSelectedTextSize = 12;
	private int tabSelectedTextColor = 0xFF666666;
	private Typeface tabTypeface = null;
	private int tabTypefaceStyle = Typeface.BOLD;

	private int lastScrollX = 0;

	private int tabBackgroundResId = R.drawable.background_tab;

	private Locale locale;

	private Context mContext;
	private LayoutInflater inflater;

	public MultiPagerSlidingTabStrip(Context context) {
		this(context, null);
	}

	public MultiPagerSlidingTabStrip(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MultiPagerSlidingTabStrip(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		inflater = LayoutInflater.from(mContext);

		setWillNotDraw(false);

		container1 = new LinearLayout(context);
		container2 = new LinearLayout(context);

		layoutParamsMW = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0,1.0f);
		container1.setOrientation(HORIZONTAL);
		container2.setOrientation(HORIZONTAL);
		container1.setLayoutParams(layoutParamsMW);
		container2.setLayoutParams(layoutParamsMW);

		tabsContainer = new LinearLayout(context);
		tabsContainer.setOrientation(LinearLayout.VERTICAL);
		tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		tabsContainer.addView(container1);
		tabsContainer.addView(container2);

		addView(tabsContainer);

		DisplayMetrics dm = getResources().getDisplayMetrics();

		scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
		indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
		underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
		dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
		tabPaddingLeftRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPaddingLeftRight, dm);
		dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
		tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

		// get system attrs (android:textSize and android:textColor)

		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

		tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
		tabTextColor = a.getColor(1, tabTextColor);

		a.recycle();

		// get custom attrs

		a = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);

		indicatorColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsIndicatorColor, indicatorColor);
		underlineColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsUnderlineColor, underlineColor);
		dividerColor = a.getColor(R.styleable.PagerSlidingTabStrip_pstsDividerColor, dividerColor);
		indicatorHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsIndicatorHeight, indicatorHeight);
		underlineHeight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsUnderlineHeight, underlineHeight);
		dividerPadding = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsDividerPadding, dividerPadding);
		tabPaddingLeftRight = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsTabPaddingLeftRight, tabPaddingLeftRight);
		tabBackgroundResId = a.getResourceId(R.styleable.PagerSlidingTabStrip_pstsTabBackground, tabBackgroundResId);
		shouldExpand = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsShouldExpand, shouldExpand);
		scrollOffset = a.getDimensionPixelSize(R.styleable.PagerSlidingTabStrip_pstsScrollOffset, scrollOffset);
		textAllCaps = a.getBoolean(R.styleable.PagerSlidingTabStrip_pstsTextAllCaps, textAllCaps);

		a.recycle();

		rectPaint = new Paint();
		rectPaint.setAntiAlias(true);
		rectPaint.setStyle(Style.FILL);

		dividerPaint = new Paint();
		dividerPaint.setAntiAlias(true);
		dividerPaint.setStrokeWidth(dividerWidth);

		defaultTabLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		expandedTabLayoutParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

		if (locale == null) {
			locale = getResources().getConfiguration().locale;
		}
		Log.e(TAG, "MultiPagerSlidingTabStrip: end");
	}

	public void setViewPager(ViewPager pager) {
		this.pager = pager;

		Log.e(TAG, "setViewPager: start");
		if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
		Log.e(TAG, "setViewPager: setOnPageChangeListener");
		pager.addOnPageChangeListener(pageListener);

		notifyDataSetChanged();

		Log.e(TAG, "setViewPager: end");
	}

	public void setOnPageChangeListener(OnPageChangeListener listener) {
		this.delegatePageListener = listener;
	}

	public void notifyDataSetChanged() {

		Log.e(TAG, "notifyDataSetChanged: start");

		container1.removeAllViews();
		container2.removeAllViews();

		tabCount = pager.getAdapter().getCount();

		for (int i = 0; i < tabCount; i++) {

			if (pager.getAdapter() instanceof IconTabProvider) {
				addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
			}
			else if (pager.getAdapter() instanceof TitleIconTabProvider){
//				addTextIconTab(i,pager.getAdapter().getPageTitle(i).toString(),((TitleIconTabProvider) pager.getAdapter()).getPageIconResId(i));
				addTextIconTab(i,pager.getAdapter().getPageTitle(i).toString(),((TitleIconTabProvider) pager.getAdapter()).getPageIconResId(i),true);
			}
			else {
				addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
			}

		}

		Log.e(TAG, "notifyDataSetChanged: 2");

		updateTabStyles();

		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {

				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
				} else {
					getViewTreeObserver().removeOnGlobalLayoutListener(this);
				}

				currentPosition = pager.getCurrentItem();

				setCurrentSelectItem(currentPosition);

//				scrollToChild(currentPosition, 0);
			}
		});

		Log.e(TAG, "notifyDataSetChanged: ");

	}

	private void addTextTab(final int position, String title) {

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(CENTER);
		tab.setSingleLine();

		addTab(position, tab);
	}

	private void addTextIconTab(final int position, String title, int resId) {

		Log.e(TAG, "addTextIconTab: start");

		if (resId == TitleIconTabProvider.NONE_ICON) {
			addTextTab(position, title);
			return;
		}

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(CENTER);

		ImageView icon = new ImageView(getContext());
		icon.setImageResource(resId);

		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER);

		LayoutParams textLP = new LayoutParams(ViewGroup
				.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

		LayoutParams ImgLP = new LayoutParams(ViewGroup
				.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ImgLP.setMargins(0,tabIndicatorMaginTop,0,0);

		linearLayout.addView(tab, textLP);
		linearLayout.addView(icon, ImgLP);
		addTab(position, linearLayout);
	}

	private void addTextIconTab(final int position, String title, int resId,boolean isShowDot) {

		Log.e(TAG, "addTextIconTab: start");

		if (resId == TitleIconTabProvider.NONE_ICON) {
			addTextTab(position, title);
			return;
		}

		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(CENTER);

		ImageView icon = new ImageView(getContext());
		icon.setImageResource(resId);

		RelativeLayout relativeLayout = new RelativeLayout(getContext());
		LayoutParams layoutParamsRL = new LayoutParams(ViewGroup
				.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//		relativeLayout.setLayoutParams(layoutParamsRL);

		LinearLayout linearLayout = new LinearLayout(getContext());
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);

		RelativeLayout.LayoutParams textLP = new RelativeLayout.LayoutParams(ViewGroup
				.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		tab.setId(tab.hashCode());
		textLP.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		textLP.setMargins(Utils.dip2px(getContext(),6),Utils.dip2px(getContext(),4),Utils.dip2px(getContext(),6),0);
		relativeLayout.addView(tab,textLP);

		RelativeLayout.LayoutParams ImgLP = new RelativeLayout.LayoutParams(ViewGroup
				.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		ImgLP.addRule(RelativeLayout.BELOW,relativeLayout.getChildAt(0).getId());
		ImgLP.setMargins(0,tabIndicatorMaginTop,0,0);
		Log.i(TAG, "addTextIconTab: tabIndicatorMaginTop = "+tabIndicatorMaginTop);
		int id = relativeLayout.getChildAt(0).getId();
		Log.i(TAG, "addTextIconTab: id = "+id);
		ImgLP.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
		relativeLayout.addView(icon,ImgLP);

		if(position % 2 == 0){
			View dot = inflater.inflate(R.layout.view_dot_red, null);
			RelativeLayout.LayoutParams dotLP = new RelativeLayout.LayoutParams(ViewGroup
					.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			dotLP.addRule(RelativeLayout.ALIGN_TOP,relativeLayout.getChildAt(0).getId());
			dotLP.addRule(RelativeLayout.ALIGN_RIGHT,relativeLayout.getChildAt(0).getId());
			dotLP.setMargins(0,Utils.dip2px(getContext(),-3),Utils.dip2px(getContext(),-6),Utils.dip2px(getContext(),-6));
			relativeLayout.addView(dot,dotLP);
		}


		linearLayout.addView(relativeLayout);

		addTab(position, linearLayout);
	}

	private void addIconTab(final int position, int resId) {

		ImageButton tab = new ImageButton(getContext());
		tab.setImageResource(resId);

		addTab(position, tab);

	}

	private void addTab(final int position, View tab) {
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				pager.setCurrentItem(position);
			}
		});
		Log.i(TAG, "addTab: tabPaddingTop = "+tabPaddingTop);
		tab.setPadding(tabPaddingLeftRight, tabPaddingTop, tabPaddingLeftRight, 0);

		if (position < 3){
			container1.addView(tab, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
		}
		else {
			container2.addView(tab, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
		}

		Log.e(TAG, "addTab: ");

	}

	private void updateTabStyles() {

		Log.e(TAG, "updateTabStyles: start");

		Log.e(TAG, "updateTabStyles: tabsContainer.getChildCount() ="+tabsContainer.getChildCount());

		for (int i = 0; i < tabsContainer.getChildCount() ; i++) {

			LinearLayout childAt = (LinearLayout) tabsContainer.getChildAt(i);
			Log.e(TAG, "updateTabStyles: i ="+i);
			Log.e(TAG, "updateTabStyles: childAt ="+childAt);
			Log.e(TAG, "updateTabStyles: childAt.getChildCount() ="+childAt.getChildCount());
			for (int j = 0; j <childAt.getChildCount() ; j++) {

				View v = childAt.getChildAt(j);

				Log.e(TAG, "updateTabStyles: j ="+j );

				Log.e(TAG, "updateTabStyles: v ="+v );

//				v.setBackgroundResource(tabBackgroundResId);

				if (v instanceof LinearLayout) {
					v = ((LinearLayout) v).getChildAt(0);
					if (v instanceof RelativeLayout) {
						v = ((RelativeLayout) v).getChildAt(0);
					}
				}


				if (v instanceof TextView) {

					TextView tab = (TextView) v;
					Log.e(TAG, "updateTabStyles: tabTextSize ="+tabTextSize );
					tab.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
					tab.setTypeface(tabTypeface, tabTypefaceStyle);
					tab.setTextColor(tabTextColor);
					Log.e(TAG, "updateTabStyles: v instanceof TextView");
					// setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
					// pre-ICS-build
					if (textAllCaps) {
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
							tab.setAllCaps(true);
							Log.e(TAG, "updateTabStyles: 111");
						} else {
							Log.e(TAG, "updateTabStyles: 222");
							tab.setText(tab.getText().toString().toUpperCase(locale));
						}
					}
				}
			}



		}

		Log.e(TAG, "updateTabStyles: end");



	}

	private void scrollToChild(int position, int offset) {

		if (tabCount == 0) {
			return;
		}

		int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

		if (position > 0 || offset > 0) {
			newScrollX -= scrollOffset;
		}

		if (newScrollX != lastScrollX) {
			lastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Log.e(TAG, "onDraw: isInEditMode ="+isInEditMode());
		Log.e(TAG, "onDraw: tabCount ="+tabCount);

		if (isInEditMode() || tabCount == 0) {
			return;
		}

		final int height = getHeight()+30;

		// draw indicator line

		rectPaint.setColor(indicatorColor);

		// default: line below current tab
//		View currentTab = tabsContainer.getChildAt(currentPosition);
//		float lineLeft = currentTab.getLeft();
//		float lineRight = currentTab.getRight();

		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

//			View nextTab = tabsContainer.getChildAt(currentPosition + 1);
//			final float nextTabLeft = nextTab.getLeft();
//			final float nextTabRight = nextTab.getRight();

//			lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
//			lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
		}

//		canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);

		// draw underline

		rectPaint.setColor(underlineColor);
		canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

		// draw divider

		dividerPaint.setColor(dividerColor);
		for (int i = 0; i < tabsContainer.getChildCount() ; i++) {
			LinearLayout childAt = (LinearLayout) tabsContainer.getChildAt(i);

			for (int j = 0; j < childAt.getChildCount() - 1; j++) {
				View tab = childAt.getChildAt(j);
				canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), childAt.getHeight() - dividerPadding, dividerPaint);
			}

		}
		Log.e(TAG, "onDraw: end");

	}

	private class PageListener implements OnPageChangeListener {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			currentPosition = position;
			currentPositionOffset = positionOffset;

//			scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
//				scrollToChild(pager.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}

		@Override
		public void onPageSelected(int position) {
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}

			setCurrentSelectItem(position);
		}

	}

	private void setCurrentSelectItem(int position) {
		if (position < 3) {
            LinearLayout childAt = (LinearLayout) tabsContainer.getChildAt(0);
            for (int i = 0; i < childAt.getChildCount(); i++) {
                View v = childAt.getChildAt(i);
                if (v instanceof LinearLayout) {
					View child = ((LinearLayout) v).getChildAt(0);
                    if (child instanceof  RelativeLayout){
						View tabTextView = ((RelativeLayout) child).getChildAt(0);
						View tabImageView = ((RelativeLayout) child).getChildAt(1);

						if (tabTextView instanceof TextView && tabImageView instanceof ImageView) {
							TextView tabTv = (TextView) tabTextView;
							ImageView tabIv = (ImageView) tabImageView;
							if (i == position) {
								tabTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabSelectedTextSize);
								tabTv.setTextColor(tabSelectedTextColor);
								tabIv.setVisibility(VISIBLE);
							} else {
								tabTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
								tabTv.setTextColor(tabTextColor);
								tabIv.setVisibility(INVISIBLE);
							}
						}
					}
                }
            }

            LinearLayout childAt2 = (LinearLayout) tabsContainer.getChildAt(1);
            for (int i = 0; i < childAt2.getChildCount(); i++) {
                View v = childAt2.getChildAt(i);
                if (v instanceof LinearLayout) {
					View child = ((LinearLayout) v).getChildAt(0);
					if (child instanceof RelativeLayout){
						View tabTextView = ((RelativeLayout) child).getChildAt(0);
						View tabImageView = ((RelativeLayout) child).getChildAt(1);

						if (tabTextView instanceof TextView && tabImageView instanceof ImageView) {
							TextView tabTv = (TextView) tabTextView;
							ImageView tabIv = (ImageView) tabImageView;
							tabTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
							tabTv.setTextColor(tabTextColor);
							tabIv.setVisibility(INVISIBLE);
						}
					}
				}
            }
        } else {

            LinearLayout childAt = (LinearLayout) tabsContainer.getChildAt(1);
            for (int i = 0; i < childAt.getChildCount(); i++) {
                View v = childAt.getChildAt(i);
                if (v instanceof LinearLayout) {
					View child = ((LinearLayout) v).getChildAt(0);
					if (child instanceof RelativeLayout){
						View tabTextView = ((RelativeLayout) child).getChildAt(0);
						View tabImageView = ((RelativeLayout) child).getChildAt(1);

						if (tabTextView instanceof TextView && tabImageView instanceof ImageView) {
							TextView tabTv = (TextView) tabTextView;
							ImageView tabIv = (ImageView) tabImageView;
							if (i == position-3) {
								tabTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabSelectedTextSize);
								tabTv.setTextColor(tabSelectedTextColor);
								tabIv.setVisibility(VISIBLE);
							} else {
								tabTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
								tabTv.setTextColor(tabTextColor);
								tabIv.setVisibility(INVISIBLE);
							}
						}
					}
				}
            }

            LinearLayout childAt2 = (LinearLayout) tabsContainer.getChildAt(0);
            for (int i = 0; i < childAt2.getChildCount(); i++) {
                View v = childAt2.getChildAt(i);
                if (v instanceof LinearLayout) {
					View child = ((LinearLayout) v).getChildAt(0);
					if (child instanceof RelativeLayout){
						View tabTextView = ((RelativeLayout) child).getChildAt(0);
						View tabImageView = ((RelativeLayout) child).getChildAt(1);

						if (tabTextView instanceof TextView && tabImageView instanceof ImageView) {
							TextView tabTv = (TextView) tabTextView;
							ImageView tabIv = (ImageView) tabImageView;
							tabTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, tabTextSize);
							tabTv.setTextColor(tabTextColor);
							tabIv.setVisibility(INVISIBLE);
						}
					}
				}
            }

        }
	}

	public void setIndicatorColor(int indicatorColor) {
		this.indicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.indicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.indicatorColor;
	}

	public void setIndicatorHeight(int indicatorLineHeightPx) {
		this.indicatorHeight = indicatorLineHeightPx;
		invalidate();
	}

	public int getIndicatorHeight() {
		return indicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.underlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.underlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return underlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.dividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.dividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return dividerColor;
	}

	public void setUnderlineHeight(int underlineHeightPx) {
		this.underlineHeight = underlineHeightPx;
		invalidate();
	}

	public int getUnderlineHeight() {
		return underlineHeight;
	}

	@Override
	public void setDividerPadding(int dividerPaddingPx) {
		this.dividerPadding = dividerPaddingPx;
		invalidate();
	}

	@Override
	public int getDividerPadding() {
		return dividerPadding;
	}

	public void setScrollOffset(int scrollOffsetPx) {
		this.scrollOffset = scrollOffsetPx;
		invalidate();
	}

	public int getScrollOffset() {
		return scrollOffset;
	}

	public void setShouldExpand(boolean shouldExpand) {
		this.shouldExpand = shouldExpand;
		requestLayout();
	}

	public boolean getShouldExpand() {
		return shouldExpand;
	}

	public boolean isTextAllCaps() {
		return textAllCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.textAllCaps = textAllCaps;
	}

	public void setTextSize(int textSizePx) {
		this.tabTextSize = textSizePx;
		updateTabStyles();
	}

	public int getTextSize() {
		return tabTextSize;
	}

	public void setTextColor(int textColor) {
		this.tabTextColor = textColor;
		updateTabStyles();
	}

	public void setTextColorResource(int resId) {
		this.tabTextColor = getResources().getColor(resId);
		updateTabStyles();
	}

	public int getTextColor() {
		return tabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.tabTypeface = typeface;
		this.tabTypefaceStyle = style;
		updateTabStyles();
	}

	public void setTabBackground(int resId) {
		this.tabBackgroundResId = resId;
	}

	public int getTabBackground() {
		return tabBackgroundResId;
	}

	public void setTabPaddingLeftRight(int paddingPx) {
		this.tabPaddingLeftRight = paddingPx;
		updateTabStyles();
	}

	public void setTabPaddingTop(int paddingPx) {
		this.tabPaddingTop = paddingPx;
		updateTabStyles();
	}

	public int getTabPaddingLeftRight() {
		return tabPaddingLeftRight;
	}

	public void setTabTextSelectedColor(int color) {
		this.tabSelectedTextColor = color;
	}

	public void setTabTextSelectedSize(int size) {
		this.tabSelectedTextSize = size;
	}

	public void setTabIndicatorMaginTop(int top){
		this.tabIndicatorMaginTop = top;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		currentPosition = savedState.currentPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currentPosition = currentPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currentPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currentPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currentPosition);
		}

		public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
			@Override
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			@Override
			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}
