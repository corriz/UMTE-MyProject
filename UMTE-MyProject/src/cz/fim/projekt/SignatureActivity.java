/**
 * 
 */
package cz.fim.projekt;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.samsung.samm.common.SObjectStroke;
import com.samsung.samm.common.SOptionSAMM;
import com.samsung.samm.common.SOptionSCanvas;
import com.samsung.spen.lib.image.SPenImageFilterConstants;
import com.samsung.spen.settings.SettingStrokeInfo;
import com.samsung.spensdk.SCanvasConstants;
import com.samsung.spensdk.SCanvasView;
import com.samsung.spensdk.applistener.HistoryUpdateListener;
import com.samsung.spensdk.applistener.SCanvasInitializeListener;

import cz.fim.project.spenfile.ExampleUtils;
import cz.fim.project.spenfile.PreferencesOfSAMMOption;
import cz.fim.project.spenfile.SPenSDKUtils;
import cz.fim.projekt.R;
import cz.fim.projekt.data.Clients;
import cz.fim.projekt.data.DatabaseManager;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * @author Roman
 * 
 */
public class SignatureActivity extends Activity {
	
	Clients client;
	private int clientID;
	

	private final String TAG = "SPenSDK Signature";
	private Context mContext = null;
	private File mFolder = null;
	private String  mSignatureFolderPath = null;

	//==============================
	// Application Identifier Setting
	// "SDK Sample Application 1.0"
	//==============================
	public static String APPLICATION_ID_NAME = "Umte-MyProject";
	public static int APPLICATION_ID_VERSION_MAJOR = 1;
	public static int APPLICATION_ID_VERSION_MINOR = 0;
	public static String APPLICATION_ID_VERSION_PATCHNAME = "Debug";

	public static final String DEFAULT_APP_IMAGEDATA_DIRECTORY = "Umte-MyProject";
	public static final String SAVED_FILE_EXTENSION = "png";

	private final int 	MENU_BG1 = 1000;
	private final int 	MENU_BG2 = 1001;
	private final int 	MENU_BG3 = 1002;
	private final int 	MENU_CLEARALL = 1003;



	private FrameLayout	mLayoutContainer;
	private RelativeLayout	mCanvasContainer;
	private SCanvasView	mSCanvas;

	private ImageView		mSaveBtn;
	private ImageView		mPenBtn;
	private ImageView		mEraserBtn;
	private ImageView		mUndoBtn;
	private ImageView		mRedoBtn;	


	private SettingStrokeInfo mStrokeInfoEraser;
	private SettingStrokeInfo mStrokeInfoScratch;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);        
		setContentView(R.layout.signature_activity);
		
		clientID = getIntent().getIntExtra(ClientDetailActivity.EXTRA_CLIENT_OBJECT, 0);
		findClientID(clientID);
		setTitle(client.getId() + " " + client.getFirstname() + " " + client.getLastname());
		
		mContext = this;

		//------------------------------------
		// UI Setting
		//------------------------------------ 

		mSaveBtn = (ImageView) findViewById(R.id.btSignature_save);
		mSaveBtn.setOnClickListener(saveBtnClickListener);

		mPenBtn = (ImageView) findViewById(R.id.btSignature_pen);
		mPenBtn.setOnClickListener(mBtnClickListener);
		mEraserBtn = (ImageView) findViewById(R.id.btSignature_erase);
		mEraserBtn.setOnClickListener(mBtnClickListener);

		mUndoBtn = (ImageView) findViewById(R.id.btSignature_undo);
		mUndoBtn.setOnClickListener(undoNredoBtnClickListener);
		mRedoBtn = (ImageView) findViewById(R.id.btSignature_redo);
		mRedoBtn.setOnClickListener(undoNredoBtnClickListener);



		//------------------------------------
		// Create SCanvasView
		//------------------------------------
		mLayoutContainer = (FrameLayout) findViewById(R.id.signature_layout_container);
		mCanvasContainer = (RelativeLayout) findViewById(R.id.signature_canvas_container);

		mSCanvas = new SCanvasView(mContext);        
		mCanvasContainer.addView(mSCanvas);

		// Set canvas size
		// setSCanvasViewLayout();

		//------------------------------------
		// SettingView Setting
		//------------------------------------
		// Resource Map for Layout & Locale
		HashMap<String,Integer> settingResourceMapInt = SPenSDKUtils.getSettingLayoutLocaleResourceMap(true, true, true, false);
		// Talk & Description Setting by Locale
		SPenSDKUtils.addTalkbackAndDescriptionStringResourceMap(settingResourceMapInt);
		// Resource Map for Custom font path
		HashMap<String,String> settingResourceMapString = SPenSDKUtils.getSettingLayoutStringResourceMap(true, true, true, false);
		// Create Setting View
		mSCanvas.createSettingView(mLayoutContainer, settingResourceMapInt, settingResourceMapString);

		// Initialize Stroke Setting
		mStrokeInfoScratch = new SettingStrokeInfo();
		mStrokeInfoScratch.setStrokeStyle(SObjectStroke.SAMM_STROKE_STYLE_ERASER);
		mStrokeInfoScratch.setStrokeWidth(3);		// small scratch

		mStrokeInfoEraser = new SettingStrokeInfo();
		mStrokeInfoEraser.setStrokeStyle(SObjectStroke.SAMM_STROKE_STYLE_SOLID);
		mStrokeInfoEraser.setStrokeColor(Color.BLACK); // assume that the foreground is black
		mStrokeInfoEraser.setStrokeWidth(50);	// 

		//====================================================================================
		//
		// Set Callback Listener(Interface)
		// Nejdùležitìjší metoda Lisener!!!!!
		//
		//====================================================================================
		//------------------------------------------------
		// SCanvas Listener
		//------------------------------------------------
		SCanvasInitializeListener mSCanvasInitializeListener = new SCanvasInitializeListener() {
			@Override
			public void onInitialized() { 
				//--------------------------------------------
				// Start SCanvasView/CanvasView Task Here
				//--------------------------------------------
				// Application Identifier Setting
				if(!mSCanvas.setAppID(APPLICATION_ID_NAME, APPLICATION_ID_VERSION_MAJOR, APPLICATION_ID_VERSION_MINOR,APPLICATION_ID_VERSION_PATCHNAME))
					Toast.makeText(mContext, "Fail to set App ID.", Toast.LENGTH_LONG).show();

				// Initial setting
				mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
				mSCanvas.setSettingViewStrokeInfo(mStrokeInfoScratch);

				mSCanvas.setStrokeLongClickSelectOption(false);

				// Set Pen Only Mode with Finger Control
				mSCanvas.setFingerControlPenDrawing(true);

				// Update button state
				updateModeState();

				// Set BG 
				setInitialBG();		
				
				

				Toast toast = Toast.makeText(mContext, "Vše je pøipraveno pro vložení podpisu", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				
				if(client.getObrSign() != null){
					loadSAMMFile(client.getObrSign().toString());
				}
			}
		};

		//------------------------------------------------
		// History Change
		//------------------------------------------------
		HistoryUpdateListener mHistoryUpdateListener = new HistoryUpdateListener() {
			@Override
			public void onHistoryChanged(boolean undoable, boolean redoable) {
				mUndoBtn.setEnabled(undoable);
				mRedoBtn.setEnabled(redoable);
			}
		};


		// Register Application Listener
		mSCanvas.setSCanvasInitializeListener(mSCanvasInitializeListener);
		mSCanvas.setHistoryUpdateListener(mHistoryUpdateListener);

		mUndoBtn.setEnabled(false);
		mRedoBtn.setEnabled(false);
		mPenBtn.setSelected(true);

		// create basic save/road file path
		File sdcard_path = Environment.getExternalStorageDirectory();
		mFolder =  new File(sdcard_path, DEFAULT_APP_IMAGEDATA_DIRECTORY);
		if(!mFolder.exists()){
			if(!mFolder.mkdirs()){
				Log.e(TAG, "Default Save Path Creation Error");
				return ;
			}
		}
		mSignatureFolderPath = mFolder.getAbsolutePath();

		// Caution:
		// Do NOT load file or start animation here because we don't know canvas size here.
		// Start such SCanvasView Task at onInitialized() of SCanvasInitializeListener
	}

	private void findClientID(int cId) {
		if(cId != 0){
			client = DatabaseManager.getInstance().getClientWithID(cId);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Release SCanvasView resources
		if(!mSCanvas.closeSCanvasView())
			Log.e(TAG, "Fail to close SCanvasView");
	}	

	@Override
	public void onBackPressed() {
		SPenSDKUtils.alertActivityFinish(this, "Exit");
	} 

	private OnClickListener saveBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mSaveBtn)) {
				//GPS EXIF
				if(client.getObrSign() == null){
					mSCanvas.setGeoTag((int)(client.getGpsLatitude() * 1e6), (int)(client.getGpsLongitude() * 1e6));
					mSCanvas.setAuthor(client.getFirstname() + " " + client.getLastname(), client.getMyPhonenumber(), "no-email", null);
					saveSAMMFile();
					finish();
				}else{
					finish();
				}
			} 
		}
	};

	private void saveSAMMFile() {
		String fileName = client.getLastname() + client.getFirstname();
		fileName = ExampleUtils.getUniqueFilename(mFolder, fileName, SAVED_FILE_EXTENSION);
		String savePath = mFolder.getPath() + '/' + fileName;	
		
		Log.d(TAG, "Save Path = " + savePath);
		updateClientImage(savePath);
		// canvas option setting
		SOptionSCanvas canvasOption = mSCanvas.getOption();					
		if(canvasOption == null)
			Toast.makeText(mContext, "Plocha je prázdná, podepište se!", Toast.LENGTH_LONG).show();
		canvasOption.mSAMMOption.setSaveImageSize(SOptionSAMM.SAMM_SAVE_OPTION_ORIGINAL_SIZE);
				
		mSCanvas.setOption(canvasOption);
		
		if(mSCanvas.saveSAMMFile(savePath)){
			Toast.makeText(mContext, APPLICATION_ID_NAME + " is saved as \"" +savePath+"\"", Toast.LENGTH_LONG).show();
		}
		else{
			Toast.makeText(mContext, "Nepodaøilo se uložit soubor : \"" +savePath+"\"", Toast.LENGTH_LONG).show();
		}
	}


	private void updateClientImage(String savePath) {
		client.setObrSign(savePath);
		DatabaseManager.getInstance().updateClient(client);
		Log.i(TAG, "" + client.getObrSign());
	}

	private OnClickListener undoNredoBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.equals(mUndoBtn)) {
				mSCanvas.undo();
			} else if (v.equals(mRedoBtn)) {
				mSCanvas.redo();
			}
			mUndoBtn.setEnabled(mSCanvas.isUndoable());
			mRedoBtn.setEnabled(mSCanvas.isRedoable());
		}
	};


	private OnClickListener mBtnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int nBtnID = v.getId();
			// If the mode is not changed, open the setting view. If the mode is same, close the setting view. 
			if(nBtnID == mPenBtn.getId()){				
				mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
				mSCanvas.setSettingViewStrokeInfo(mStrokeInfoScratch);
				updateModeState();
			}
			else if(nBtnID == mEraserBtn.getId()){
				mSCanvas.setCanvasMode(SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
				mSCanvas.setSettingViewStrokeInfo(mStrokeInfoEraser);
				updateModeState();
			}
		}
	};




	// Update tool button
	private void updateModeState(){
		int nCurMode = mSCanvas.getCanvasMode();
		mPenBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_ERASER);
		mEraserBtn.setSelected(nCurMode==SCanvasConstants.SCANVAS_MODE_INPUT_PEN);
	}

	// Get the minimum image scaled rect which is fit to current screen 
	Rect getMiniumCanvasRect(String strImagePath, int nMargin){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int nScreenWidth =  displayMetrics.widthPixels - nMargin*2;
		int nScreenHeight = displayMetrics.heightPixels - nMargin*2;

		// Make more small for screen rotation T.T
		if(nScreenWidth<nScreenHeight)
			nScreenHeight = nScreenWidth;
		else
			nScreenWidth = nScreenHeight;

		int nImageWidth = nScreenWidth;
		int nImageHeight = nScreenHeight;	


		float fResizeWidth = (float) nScreenWidth / nImageWidth;
		float fResizeHeight = (float) nScreenHeight / nImageHeight;
		float fResizeRatio;

		// Fit to height
		if(fResizeWidth>fResizeHeight){
			fResizeRatio = fResizeHeight;
		}
		// Fit to width
		else {	
			fResizeRatio = fResizeWidth;
		}

		return new Rect(0,0, (int)(nImageWidth*fResizeRatio), (int)(nImageHeight*fResizeRatio));
	}



	// Get the minimum image scaled rect which is fit to current screen 
	Rect getMaximumCanvasRect(int nMarginWidth, int nMarginHeight){
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		int nScreenWidth =  displayMetrics.widthPixels - nMarginWidth;
		int nScreenHeight = displayMetrics.heightPixels - nMarginHeight;

		// rough image size estimation
		int nImageWidth = 640;
		int nImageHeight = 480;			

		float fResizeWidth = (float) nScreenWidth / nImageWidth;
		float fResizeHeight = (float) nScreenHeight / nImageHeight;
		float fResizeRatio;

		// Fit to Height
		if(fResizeWidth>fResizeHeight){
			fResizeRatio = fResizeHeight;
		}
		// Fit to Width
		else {	
			fResizeRatio = fResizeWidth;
		}
		//return new Rect(0,0, (int)(nImageWidth*fResizeRatio), (int)(nImageHeight*fResizeRatio));
		// Adjust more detail
		int nResizeWidth = (int)(nImageWidth*fResizeRatio);
		int nResizeHeight = (int)(0.5 + (nResizeWidth * nImageHeight)/(float)nImageWidth);		
		return new Rect(0,0, nResizeWidth, nResizeHeight);
	}

	//Nastavení pozadí podpisové plochy
	private void setInitialBG(){
		// set BG Image
		Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.gradient_color1);
		if(bg == null)
			return;
		mSCanvas.setBackgroundImage(bg);

		// set FG as Black
		int [] nColors = new int[1];
		nColors[0] = 0xff000000; // black
		Bitmap fg = Bitmap.createBitmap(nColors, 1, 1, Bitmap.Config.ARGB_8888);
		if(fg == null)
			return;
		mSCanvas.setClearImageBitmap(fg, SPenImageFilterConstants.FILTER_ORIGINAL, SPenImageFilterConstants.FILTER_LEVEL_MEDIUM);
	}


	RectF getDefaultRect(String strImagePath){
		// Rect Region : Consider image real size
		BitmapFactory.Options opts = SPenSDKUtils.getBitmapSize(strImagePath);
		int nImageWidth = opts.outWidth;
		int nImageHeight = opts.outHeight;
		int nScreenWidth = mSCanvas.getWidth();
		int nScreenHeight = mSCanvas.getHeight();    			
		int nBoxRadius = (nScreenWidth>nScreenHeight) ? nScreenHeight/4 : nScreenWidth/4;
		int nCenterX = nScreenWidth/2;
		int nCenterY = nScreenHeight/2;
		if(nImageWidth > nImageHeight)
			return new RectF(nCenterX-nBoxRadius,nCenterY-(nBoxRadius*nImageHeight/nImageWidth),nCenterX+nBoxRadius,nCenterY+(nBoxRadius*nImageHeight/nImageWidth));
		else
			return new RectF(nCenterX-(nBoxRadius*nImageWidth/nImageHeight),nCenterY-nBoxRadius,nCenterX+(nBoxRadius*nImageWidth/nImageHeight),nCenterY+nBoxRadius);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{	
		menu.add(MENU_BG1, MENU_BG1, Menu.NONE, "White Pen");
		menu.add(MENU_BG2, MENU_BG2, Menu.NONE, "Blue Pen");
		menu.add(MENU_BG3, MENU_BG3, Menu.NONE, "Color Pen");
		menu.add(MENU_CLEARALL, MENU_CLEARALL, Menu.NONE, "Clear All");

		return super.onCreateOptionsMenu(menu);
	} 


	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		super.onOptionsItemSelected(item);

		switch(item.getItemId()) {
		case MENU_BG1:{
			// set BG Image
			Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.gradient_color1);
			if(bg != null) mSCanvas.setBackgroundImage(bg);
		}
		break;
		case MENU_BG2:{
			// set BG Image
			Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.gradient_color2);
			if(bg != null) mSCanvas.setBackgroundImage(bg);
		}
		break;
		case MENU_BG3:{
			// set BG Image
			Bitmap bg = BitmapFactory.decodeResource(getResources(), R.drawable.gradient_color3);
			if(bg != null) mSCanvas.setBackgroundImage(bg);
		}
		break;
		case MENU_CLEARALL:{
			mSCanvas.clearScreen();
		}
		break;

		}
		return true;
	}
	
	


	boolean loadSAMMFile(String strFileName){
		if(mSCanvas.isAnimationMode()){
			// It must be not animation mode.
		}
		else {
			// canvas option setting
			SOptionSCanvas canvasOption = mSCanvas.getOption();					
			if(canvasOption == null)
				return false;
			canvasOption.mSAMMOption.setConvertCanvasSizeOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasSize(mContext));
			canvasOption.mSAMMOption.setConvertCanvasHorizontalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasHAlign(mContext));
			canvasOption.mSAMMOption.setConvertCanvasVerticalAlignOption(PreferencesOfSAMMOption.getPreferenceLoadCanvasVAlign(mContext));
			canvasOption.mSAMMOption.setDecodePriorityFGData(PreferencesOfSAMMOption.getPreferenceDecodePriorityFGData(mContext));
			// option setting
			mSCanvas.setOption(canvasOption);					

			// show progress for loading data
			if(mSCanvas.loadSAMMFile(strFileName, true, false, false)){
				// Loading Result can be get by callback function
			}
			else{
				Toast.makeText(this, "Load AMS File("+ strFileName +") Fail!", Toast.LENGTH_LONG).show();
				return false;
			}
		}
		return true;
		}
}
