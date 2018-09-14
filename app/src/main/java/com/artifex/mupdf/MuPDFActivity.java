package com.artifex.mupdf;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.xinhui.electronicsignature.R;
import com.xinhui.handwrite.HandWriteToPDF;
import com.xinhui.handwrite.RecyclingResources;
import com.xinhui.utils.PaintConstants.PEN_TYPE;
import com.xinhui.view.ColorView;
import com.xinhui.view.HandWritingView;
import com.xinhui.view.PenCircleView;
import com.xinhui.view.ScreenShotView;
import com.xinhui.view.ShowLineView;

import static com.xinhui.utils.PaintConstants.COLOR1;
import static com.xinhui.utils.PaintConstants.COLOR10;
import static com.xinhui.utils.PaintConstants.COLOR11;
import static com.xinhui.utils.PaintConstants.COLOR13;
import static com.xinhui.utils.PaintConstants.COLOR2;
import static com.xinhui.utils.PaintConstants.COLOR3;
import static com.xinhui.utils.PaintConstants.COLOR4;
import static com.xinhui.utils.PaintConstants.COLOR5;
import static com.xinhui.utils.PaintConstants.COLOR7;
import static com.xinhui.utils.PaintConstants.COLOR8;
import static com.xinhui.utils.PaintConstants.COLOR_VIEW_SIZE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

/***
 * 项目：ElectronicSignature
 * 类名：MuPDFActivity
 * 功能：显示pdf主界面
 * 创建时间：2013-12-11
 * 创建人：LXH
 */
public class MuPDFActivity extends Activity implements OnClickListener,OnLongClickListener
,OnSeekBarChangeListener{
	/* The core rendering instance */
	
	private enum LinkState {DEFAULT, HIGHLIGHT, INHIBIT};
	private final int    TAP_PAGE_MARGIN = 5;
	public static MuPDFCore    core;
	private String       mFileName;
	private ReaderView   mDocView;
	private View         mButtonsView;
	private boolean      mButtonsVisible;
	private SeekBar      mPageSlider;
	private TextView     mPageNumberView;
	private ImageButton  mAddPicButton;
	private ImageButton  mScreenShot;
	private ImageButton  mClearButton;
	private ImageButton  mConfirmButton;
	private ImageButton  mCancelButton;
	private static HandWritingView handWritingView =null;
	/**
	 * 画笔设置对话框PopWindow
	 */
	private PopupWindow penSetPop = null ;
	private View penSetView ;
	private LayoutInflater layoutInflater = null;
	private LinearLayout setpenlayout = null;
	private LinearLayout penShowLayout = null ;
	private ImageButton penSizeButton = null;
	private ShowLineView showLineView =  null ;//显示画笔线条的粗细效果
	public static int mPenType = PEN_TYPE.PLAIN_PEN;
	private SeekBar penSizeSeekBar;
	private LinearLayout penSizeShowLayout;//显示画笔粗细圆形效果
	private PenCircleView penCircleView;//
	private RadioGroup colorRadioGroup = null;
	private RadioGroup colorRadioGroup2 = null;
	private List<ColorView> mColorViewList = null;
	private List<ColorView> mColorViewList2 = null;
	private RadioButton plainpen = null;
	private RadioGroup penRadioGroupf = null;
	public static int penSize = 5;
	public static int penColor = COLOR1;
	// 声明ColorView
	private ColorView colorView1 = null;
	private ColorView colorView2 = null;
	private ColorView colorView3 = null;
	private ColorView colorView4 = null;
	private ColorView colorView5 = null;
	private ColorView colorView7 = null;
	private ColorView colorView8 = null;
	private ColorView colorView10 = null;
	private ColorView colorView11= null;
	private ColorView colorView13= null;

	/**
	 * 绘画选择区域
	 */
	public static ScreenShotView screenShotView ;
	public static int x = 200;//绘画开始的横坐标
	public static int y = 300;//绘画开始的纵坐标
	public static int m;//绘画结束的横坐标
	public static int n;//绘画结束的纵坐标
	public static int oX;//标示区域的中心点横坐标
	public static int oY;//标示区域的中心点纵坐标
	/**
	 * 判断是否为预览pdf模式
	 */
	public static boolean isPreviewPDF = false;
	/**
	 * 判断是否正在书写
	 */
	public static boolean isWriting = false;
	/**
	 * 判断页面按钮是否显示
	 */
	private boolean showButtonsDisabled;
	/**
	 * 判断截屏视图框是否显示
	 */
	private static boolean isScreenShotViewShow = false;
	private ViewSwitcher mTopBarSwitcher;
// XXX	private ImageButton  mLinkButton;
	private boolean      mTopBarIsSearch;
	//private SearchTaskResult mSearchTaskResult;
	private AlertDialog.Builder mAlertBuilder;
	private LinkState    mLinkState = LinkState.DEFAULT;
	
	public static String PATH;
	private static String InPdfFilePath;
	public static String OutPdfFilePath;
	private static String InPicFilePath;
	private static  Bitmap storeInSDBitmap;
	public static  MuPDFActivity THIS;
	private Bundle InstanceState;
	private int writingPageNumble = -1;
	/**
	 * 功能：根据获取的文件路径，解析pdf，并返回core
	 * @param path
	 * @return core
	 */
	private MuPDFCore openFile(String path)
	{
		
		PATH = path;
		int lastSlashPos = path.lastIndexOf('/');
		mFileName = new String(lastSlashPos == -1
					? path
					: path.substring(lastSlashPos+1));
		System.out.println("Trying to open "+path);
		try
		{
			core = new MuPDFCore(path);
			// New file: drop the old outline data
		}
		catch (Exception e)
		{
			System.out.println(e);
			return null;
		}
		return core;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		InstanceState = savedInstanceState;
		THIS = MuPDFActivity.this;
		View screenView = this.getWindow().getDecorView();
		screenView.setDrawingCacheEnabled(true);
		screenView.buildDrawingCache();
		mAlertBuilder = new AlertDialog.Builder(this);
		if (core == null) {
			core = (MuPDFCore)getLastNonConfigurationInstance();

			if (savedInstanceState != null && savedInstanceState.containsKey("FileName")) {
				mFileName = savedInstanceState.getString("FileName");
			}
		}
		if (core == null) {
			Intent intent = getIntent();
			if (Intent.ACTION_VIEW.equals(intent.getAction())) {
				Uri uri = intent.getData();
				if (uri.toString().startsWith("content://media/external/file")) {
					Cursor cursor = getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
					if (cursor.moveToFirst()) {
						uri = Uri.parse(cursor.getString(0));
					}
				}
				core = openFile(Uri.decode(uri.getEncodedPath()));
			}
			if (core != null && core.needsPassword()) {
//				requestPassword(savedInstanceState);
				return;
			}
		}
		if (core == null)
		{
			AlertDialog alert = mAlertBuilder.create();
			alert.setTitle(R.string.open_failed);
			alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return;
		}
		
		createUI(savedInstanceState);
		initialSetPenBtV();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		//killSearch();

		if (mFileName != null && mDocView != null) {
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//将判断条件恢复初始化
		}
	public void onDestroy()
	{
		if (core != null)
			core.onDestroy();
		core = null;
		isPreviewPDF = false;//重新解析pdf，恢复初始值
		isScreenShotViewShow = false;//重新解析pdf，恢复初始值
		ReaderView.NoTouch = true;//重新释放对pdf手势操作
		isWriting = false;//判断画板是否打开
		x = 200;
		y = 300;
		//释放之前使用的所有bitmap对象
		RecyclingResources recyclingResources = new RecyclingResources();
		recyclingResources.recycleBitmap(ReaderView.screenShotBitmap);
		super.onDestroy();
	}
	

	public void createUI(Bundle savedInstanceState) {
		if (core == null)
			return;

			mDocView = new ReaderView(this) {

			public boolean onSingleTapUp(MotionEvent e) {
				if (e.getX() < super.getWidth()/TAP_PAGE_MARGIN) {
					super.moveToPrevious();
				} else if (e.getX() > super.getWidth()*(TAP_PAGE_MARGIN-1)/TAP_PAGE_MARGIN) {
					super.moveToNext();
				} else if (!showButtonsDisabled) {
					int linkPage = -1;
					if (mLinkState != LinkState.INHIBIT) {
						MuPDFPageView pageView = (MuPDFPageView) mDocView.getDisplayedView();
						if (pageView != null) {
// XXX							linkPage = pageView.hitLinkPage(e.getX(), e.getY());
						}
					}

					if (linkPage != -1) {
						mDocView.setDisplayedViewIndex(linkPage);
					} else {
						if (!mButtonsVisible) {
							showButtons();
						} else {
							hideButtons();
						}
					}
				}
				return super.onSingleTapUp(e);
			}

			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				if (!showButtonsDisabled)
					hideButtons();

				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			public boolean onScaleBegin(ScaleGestureDetector d) {
				showButtonsDisabled = true;
				return super.onScaleBegin(d);
			}

			public boolean onTouchEvent(MotionEvent event) {
				if (event.getActionMasked() == MotionEvent.ACTION_DOWN){
					showButtonsDisabled = false;
				}
				return super.onTouchEvent(event);
			}

			protected void onChildSetup(int i, View v) {

				((PageView)v).setLinkHighlighting(mLinkState == LinkState.HIGHLIGHT);
			}

			protected void onMoveToChild(int i) {
				if (core == null)
					return;
				HandWriteToPDF.writePageNumble = i+1;//将当前显示的pdf页码传给HandWriteToPDF
				mPageNumberView.setText(String.format("%d/%d", i+1, core.countPages()));
				mPageSlider.setMax(core.countPages()-1);
				mPageSlider.setProgress(i);
			}

			protected void onSettle(View v) {
				// When the layout has settled ask the page to render
				// in HQ
				((PageView)v).addHq();
			}

			protected void onUnsettle(View v) {
				// When something changes making the previous settled view
				// no longer appropriate, tell the page to remove HQ
				((PageView)v).removeHq();
			}
		};
		
		mDocView.setAdapter(new MuPDFPageAdapter(this, core));

		makeButtonsView();

		//设置pdf文件名称
		//mFilenameView.setText(mFileName);

		mPageSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onStopTrackingTouch(SeekBar seekBar) {
				mDocView.setDisplayedViewIndex(seekBar.getProgress());
			}

			public void onStartTrackingTouch(SeekBar seekBar) {}

			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				updatePageNumView(progress);
			}
		});
		
		// Reenstate last state if it was recorded
		if(writingPageNumble == -1){
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			mDocView.setDisplayedViewIndex(prefs.getInt("page"+mFileName, 0));
		}else{
			mDocView.setDisplayedViewIndex(writingPageNumble);
		}

		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocView);
		layout.addView(mButtonsView);
		layout.setBackgroundResource(R.drawable.tiled_background);
		setContentView(layout);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode >= 0)
			mDocView.setDisplayedViewIndex(resultCode);
		super.onActivityResult(requestCode, resultCode, data);
	}

	public Object onRetainNonConfigurationInstance()
	{
		MuPDFCore mycore = core;
		core = null;
		return mycore;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (mFileName != null && mDocView != null) {
			outState.putString("FileName", mFileName);
			SharedPreferences prefs = getPreferences(Context.MODE_PRIVATE);
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt("page"+mFileName, mDocView.getDisplayedViewIndex());
			edit.commit();
		}

		if (!mButtonsVisible)
			outState.putBoolean("ButtonsHidden", true);

		if (mTopBarIsSearch)
			outState.putBoolean("SearchMode", true);
	}
	
	/**
	 功能：显示按钮
	 */
	public void showButtons() {
		if (core == null)
			return;
		
		if (!mButtonsVisible) {
			mButtonsVisible = true;
			// Update page number text and slider
			int index = mDocView.getDisplayedViewIndex();
			updatePageNumView(index);
			mPageSlider.setMax(core.countPages()-1);
			mPageSlider.setProgress(index);
			if (mTopBarIsSearch) {
				//mSearchText.requestFocus();
				//showKeyboard();
			}

			Animation anim = new TranslateAnimation(0, 0, -mTopBarSwitcher.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mTopBarSwitcher.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, mPageSlider.getHeight(), 0);
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPageSlider.setVisibility(View.VISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mPageNumberView.setVisibility(View.VISIBLE);
				}
			});
			mPageSlider.startAnimation(anim);
		}
	}
	/**
	 * 功能：隐藏按钮
	 */
	public void hideButtons() {
		if (mButtonsVisible) {
			mButtonsVisible = false;
			//hideKeyboard();

			Animation anim = new TranslateAnimation(0, 0, 0, -mTopBarSwitcher.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mTopBarSwitcher.setVisibility(View.INVISIBLE);
				}
			});
			mTopBarSwitcher.startAnimation(anim);

			anim = new TranslateAnimation(0, 0, 0, mPageSlider.getHeight());
			anim.setDuration(200);
			anim.setAnimationListener(new Animation.AnimationListener() {
				public void onAnimationStart(Animation animation) {
					mPageNumberView.setVisibility(View.INVISIBLE);
				}
				public void onAnimationRepeat(Animation animation) {}
				public void onAnimationEnd(Animation animation) {
					mPageSlider.setVisibility(View.INVISIBLE);
				}
			});
			mPageSlider.startAnimation(anim);
		}
	}

	/**
	 * 功能：更新页面信息
	 * @param index
	 */
	public void updatePageNumView(int index) {
		if (core == null)
			return;
		mPageNumberView.setText(String.format("%d/%d", index+1, core.countPages()));
	}

	/**
	 * 功能：控件按钮的绑定和点击事件的处理
	 */
	public void makeButtonsView() {
		mButtonsView = getLayoutInflater().inflate(R.layout.read_pdf_main,null);
		
		mPageSlider = (SeekBar)mButtonsView.findViewById(R.id.pageSlider);
		mPageNumberView = (TextView)mButtonsView.findViewById(R.id.pageNumber);
		mTopBarSwitcher = (ViewSwitcher)mButtonsView.findViewById(R.id.switcher);
		mTopBarSwitcher.setVisibility(View.INVISIBLE);
		mPageNumberView.setVisibility(View.INVISIBLE);
		mPageSlider.setVisibility(View.INVISIBLE);
		
		mAddPicButton = (ImageButton) mButtonsView.findViewById(R.id.add_pic_bt);
		mScreenShot = (ImageButton) mButtonsView.findViewById(R.id.screenshot_ib);
		mClearButton = (ImageButton) mButtonsView.findViewById(R.id.clear_bt);
		mConfirmButton = (ImageButton) mButtonsView.findViewById(R.id.confirm_bt);
		mCancelButton = (ImageButton) mButtonsView.findViewById(R.id.cancel_bt);
		handWritingView = (HandWritingView) mButtonsView.findViewById(R.id.handwriteview);
		handWritingView.setVisibility(View.GONE);//
		mCancelButton.setOnClickListener(this);
		mAddPicButton.setOnClickListener(this);
		mAddPicButton.setOnLongClickListener(this);
		mScreenShot.setOnClickListener(this);
		mClearButton.setOnClickListener(this);
		mConfirmButton.setOnClickListener(this);
	}

	/**
	 * 功能：初始化设置画笔popupwindow视图里面的控件
	 */
	public void initialSetPenBtV(){
		penSetView = getLayoutInflater().inflate(R.layout.pen_set, null);
		setpenlayout = (LinearLayout) penSetView.findViewById(R.id.setpenlayout) ;
		penShowLayout = (LinearLayout) penSetView.findViewById(R.id.penShowLayout) ;
		penSizeSeekBar = (SeekBar) penSetView.findViewById(R.id.penSizeSeekBar) ;
		penSizeShowLayout = (LinearLayout) penSetView.findViewById(R.id.penSizeShowLayout) ;
		colorRadioGroup = (RadioGroup) penSetView.findViewById(R.id.radioGroupColor);
		colorRadioGroup2 = (RadioGroup) penSetView.findViewById(R.id.radioGroupColor2);
		//plainpen = (RadioButton) penSetView.findViewById(R.id.buttonPlainPen);
		penRadioGroupf = (RadioGroup) penSetView.findViewById(R.id.penRaidoGroup1);
		penSizeSeekBar.setOnSeekBarChangeListener(this);
		showLineView = new ShowLineView(this) ;
		penShowLayout.addView(showLineView);
		penCircleView = new PenCircleView(this) ;
		penSizeShowLayout.addView(penCircleView,80,80);
		showLineView.setAttr(6, Color.BLACK, mPenType) ;
		
		initpenRadioGroupf(penSetView);
	}

	/**
	 * 功能：操作设置画笔风格
	 * @param view
	 */
	private void initpenRadioGroupf(View view) {

		plainpen = (RadioButton) view.findViewById(R.id.buttonPlainPen);
		plainpen.setChecked(true);
		penRadioGroupf
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {				
						if (checkedId == -1) {
							return;
						}
						switch (checkedId) {
						case R.id.buttonBlurPen:
							setToolTyle(PEN_TYPE.BLUR);
							break;
						case R.id.buttonEmboss:
							setToolTyle(PEN_TYPE.EMBOSS);
							break;
						case R.id.buttonPlainPen:
							setToolTyle(PEN_TYPE.PLAIN_PEN);
							break;
						case R.id.buttonSelectBackGroundColor:
							setToolTyle(PEN_TYPE.TS_PEN);
							break;
						default:
							break;
						}
						updateLineShow();
					}
				});
	}

	/**
	 * 功能：设置画笔的样式
	 *  */
	private void setToolTyle(int type) {
		//mPaintView.setCurrentPainterType(type);
		mPenType = type;

	}

	/**
	 * 功能：显示颜色选择视图ColorRadioGroup
	 */
	private void initColorRadioGroup() {
		mColorViewList = new ArrayList<ColorView>();
		mColorViewList.add(colorView1);
		mColorViewList.add(colorView2);
		mColorViewList.add(colorView3);
		mColorViewList.add(colorView4);
		mColorViewList.add(colorView5);
		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
				COLOR_VIEW_SIZE, COLOR_VIEW_SIZE);
		params.setMargins(1, 2, 6, 6);

		for (ColorView colorView : mColorViewList) {
			colorRadioGroup.addView(colorView, params);
			colorView.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					for (ColorView colorView : mColorViewList) {
						if (buttonView.equals(colorView)
								&& buttonView.isChecked()) {
							
							colorRadioGroup2.clearCheck();
							penColor = colorView.getColor();
							updateLineShow();
						}
					}
				}
			});
		}
	}

	/**
	 * 功能：显示颜色选择视图ColorRadioGroup2
	 */
	private void initColorRadioGroup2() {
		mColorViewList2 = new ArrayList<ColorView>();
		mColorViewList2.add(colorView7);
		mColorViewList2.add(colorView8);
		//mColorViewList.add(colorView9);
		mColorViewList2.add(colorView10);
		mColorViewList2.add(colorView11);
		//mColorViewList.add(colorView12);
		mColorViewList2.add(colorView13);
		RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
				COLOR_VIEW_SIZE, COLOR_VIEW_SIZE);
		params.setMargins(1, 2, 6, 6);

		for (ColorView colorView2 : mColorViewList2) {
			colorRadioGroup2.addView(colorView2, params);
			colorView2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					for (ColorView colorView2 : mColorViewList2) {
						if (buttonView.equals(colorView2)
								&& buttonView.isChecked()) {
							//set the first row unchecked
							colorRadioGroup.clearCheck();
							penColor = colorView2.getColor();
							updateLineShow();
						}
					}
				}
			});
		}
	}

	/**
	 * 功能：初始化colorView
	 */
	private void initColorViews() {
		// preference
		SharedPreferences settings = getPreferences(Activity.MODE_PRIVATE);
		colorView1 = new ColorView(this, settings.getInt("color1", COLOR1));//��ɫ
		colorView2 = new ColorView(this, settings.getInt("color2", COLOR2));//��ɫ
		colorView3 = new ColorView(this, settings.getInt("color3", COLOR3));//����
		colorView4 = new ColorView(this, settings.getInt("color4", COLOR4));//��ɫ
		colorView5 = new ColorView(this, settings.getInt("color5", COLOR5));//��ɫ
		colorView7 = new ColorView(this, settings.getInt("color7", COLOR7));//��ɫ
		colorView8 = new ColorView(this, settings.getInt("color8", COLOR8));//ǳ��
		colorView10 =new ColorView(this, settings.getInt("color10", COLOR10));//���
		colorView11= new ColorView(this, settings.getInt("color11", COLOR11));//��ɫ
		colorView13= new ColorView(this, settings.getInt("color13", COLOR13));
		
		initColorRadioGroup();
		initColorRadioGroup2();
	}

	/**
	 * 功能：更新画笔线条的粗细
	 */
	private void updateLineShow(){
		showLineView.setAttr(penSize, penColor, mPenType) ;
		penCircleView.penAttrChange(penSize, penColor) ;
		//ColorDrawable colorDrawable = new ColorDrawable(mPaintView.getPenColor()) ;
		//pencolor.setBackgroundColor(mPaintView.getPenColor()) ;
	}
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		// TODO Auto-generated method stub
		penSize = progress;
		updateLineShow();
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		updateLineShow();
		if(penSetPop == null){
			penSetPop = new PopupWindow(penSetView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);		
			//penSetPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.popover_background_left));
			penSetPop.setBackgroundDrawable(getResources().getDrawable(R.drawable.tiled_background));
			penSetPop.setFocusable(true);
			penSetPop.setOutsideTouchable(true);
			penSetPop.showAsDropDown(mAddPicButton,0,0);
			initColorViews();
		}else{
			penSetPop.setFocusable(true);
			penSetPop.setOutsideTouchable(true);
			penSetPop.showAsDropDown(mAddPicButton,0,0);
			penSetPop.update();
		}
		return true;////返回false时，点击事件还会响应；返回true，长按事件后点击事件就不响应了
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.cancel_bt:////撤销已签名pdf文件
			if(isPreviewPDF){
				AlertDialog.Builder builder = new Builder(this);
				builder.setTitle("提醒：撤销后，已签名文件文件将无法恢复，是否继续？")
				.setPositiveButton("继续", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						try{
							core = openFile(InPdfFilePath);
							File file = new File(OutPdfFilePath);
							file.delete();
						}catch (Exception e) {
							// TODO: handle exception
							Toast.makeText(MuPDFActivity.this, "无法打开该文件", Toast.LENGTH_SHORT).show();
						}
						createUI(InstanceState);
						isPreviewPDF = false;//重新解析pdf，恢复初始值
						ReaderView.NoTouch = true;//重新释放对pdf手势操作
						isScreenShotViewShow = false;//重新解析pdf，恢复初始值
						isWriting = false;//
						showButtonsDisabled = false;
					}
				})
				.setNegativeButton("取消", null)
				.create()
				.show();
			}else{
				Toast.makeText(this, "没有要撤销的签名文件", Toast.LENGTH_SHORT).show();
			}
			break;
			case R.id.add_pic_bt://打开手写画板
				//记录当前签名页码
				if (null == screenShotView) {
					screenShotView = new ScreenShotView(this) ;
				}
				writingPageNumble = mDocView.getDisplayedViewIndex();
				if(mAddPicButton.getContentDescription().equals("开始签名")){
					if(screenShotView.isShown()){
						screenShotView.setVisibility(View.INVISIBLE);
						handWritingView.setVisibility(View.VISIBLE);

						mAddPicButton.setContentDescription("取消签名");
						mScreenShot.setContentDescription("锁定屏幕");
						isWriting = true;
					}else if(isPreviewPDF){
						Toast.makeText(MuPDFActivity.this, "预览模式", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(MuPDFActivity.this, "请先选定书写区域", Toast.LENGTH_SHORT).show();
					}
				}else{
					handWritingView.setVisibility(View.GONE);
					mAddPicButton.setContentDescription("开始签名");
					isWriting = false;
					ReaderView.NoTouch = true;//释放pdf手势操作
				}
				break;
			case R.id.screenshot_ib://打开区域选择view
				if(screenShotView == null){
					screenShotView = new ScreenShotView(this);
				}
				if(isPreviewPDF){
					Toast.makeText(MuPDFActivity.this, "预览模式", Toast.LENGTH_SHORT).show();
				}else if(!isPreviewPDF && isWriting){
					Toast.makeText(MuPDFActivity.this, "正在签名……", Toast.LENGTH_SHORT).show();
				}else{
					if(!screenShotView.isShown() && !isScreenShotViewShow){
						this.addContentView(screenShotView,
								new LayoutParams(LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT));


						screenShotView.setSeat(x, y, x+360, y+180);
						screenShotView.postInvalidate();
						isScreenShotViewShow = true;
					}
					if(mScreenShot.getContentDescription().equals("锁定屏幕")){
						ReaderView.NoTouch = false;
						mScreenShot.setContentDescription("释放屏幕");
						screenShotView.setVisibility(View.VISIBLE);
					}else{
						ReaderView.NoTouch = true;
						mScreenShot.setContentDescription("锁定屏幕");
						screenShotView.setVisibility(View.INVISIBLE);
					}
				}
				break;
			case R.id.confirm_bt://保存签名文件
				if(mAddPicButton.getContentDescription().equals("取消签名")){
					saveImageAsyncTask asyncTask = new saveImageAsyncTask(this);
					asyncTask.execute();
					ReaderView.NoTouch = true;
					handWritingView.setVisibility(View.INVISIBLE);
					mAddPicButton.setContentDescription("开始签名");
					isPreviewPDF = true;
					showButtonsDisabled = false;
				}else{
					Toast.makeText(this, "没有要保存的签名文件", Toast.LENGTH_SHORT).show();
				}
				break;
			default:
				break;
		}
	}

	/**
	 * 功能：将耗时的操作放到异步任务
	 * @author LI
	 *
	 */
	class saveImageAsyncTask extends AsyncTask<Void,Integer,Integer>{
		private Context context;

		public saveImageAsyncTask(Context context){
			this.context = context;
		}

		/**
		 * 运行在UI线程中，在调用doInBackground()之前执行
		 */
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			Toast.makeText(context,"正在处理……",Toast.LENGTH_SHORT).show();
		}

		/**
		 *后台运行的方法，可以运行非UI线程，可以执行耗时的方法
		 */
		@Override
		protected Integer doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			mSaveImage();
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
			createUI(InstanceState);
			Toast.makeText(context,"签名完成",Toast.LENGTH_SHORT).show();
		}
		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}
	}
	/**
	 * 功能：处理书写完毕的画板，重新生成bitmap
	 */
	public void mSaveImage(){
		HandWritingView.saveImage = Bitmap.createBitmap(handWritingView.HandWriting(HandWritingView.new1Bitmap));
		HandWritingView mv = handWritingView;
		storeInSDBitmap = mv.saveImage();
		Canvas canvas = new Canvas(storeInSDBitmap);
		Paint paint = new Paint();
		canvas.drawARGB(0, 0, 0, 0);
		canvas.isOpaque();
		paint.setAlpha(255);//设置签名水印透明度
		//这个方法  第一个参数是图片原来的大小，第二个参数是 绘画该图片需显示多少。
		//也就是说你想绘画该图片的某一些地方，而不是全部图片，
		//第三个参数表示该图片绘画的位置.
		canvas.drawBitmap(storeInSDBitmap, 0, 0, paint);
		storeInSD(storeInSDBitmap);//保存签名过的pdf文件
		previewPDFShow();
	}

	/**
	 * 功能：预览签名过的pdf
	 */
	public  void previewPDFShow(){
		String openNewPath = OutPdfFilePath;

		try{
			core = openFile(openNewPath);//打开已经签名好的文件进行预览
			//截屏坐标恢复默认
			x = 200;
			y = 200;
		}catch (Exception e) {
			// TODO: handle exception
			Log.e("info", "------打开失败");
		}
	}

	/**
	 * 功能：将签好名的bitmap保存到sd卡
	 * @param bitmap
	 */
	public static void storeInSD(Bitmap bitmap) {
		File file = new File("/sdcard/签名");//要保存的文件地址和文件名
		if (!file.exists()) {
			file.mkdir();
		}
		File imageFile = new File(file, "签名" + ".png");
		try {
			imageFile.createNewFile();
			FileOutputStream fos = new FileOutputStream(imageFile);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
			addTextToPdf();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void addTextToPdf(){
		String  SDCardRoot = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		Date curDate = new Date(System.currentTimeMillis());//获取当前时间
		String currentSystemTime = formatter.format(curDate);
		InPdfFilePath = MuPDFActivity.PATH;
		OutPdfFilePath = SDCardRoot+"/签名/已签名文件"+currentSystemTime+".pdf";
		InPicFilePath = SDCardRoot+"/签名/签名.png";
		HandWriteToPDF handWriteToPDF = new HandWriteToPDF(InPdfFilePath, OutPdfFilePath, InPicFilePath);
		handWriteToPDF.addText();
	}
}
