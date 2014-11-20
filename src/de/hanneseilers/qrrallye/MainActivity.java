package de.hanneseilers.qrrallye;

import com.google.gson.Gson;

import de.hanneseilers.qrrallye.R;
import de.hanneseilers.qrrallye.zxing.IntentIntegrator;
import de.hanneseilers.qrrallye.zxing.IntentResult;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings.Secure;

public class MainActivity extends ActionBarActivity implements OnClickListener {

	private static final String KEY_GROUPHASH = "de.hanneseilers.qrrallye.grouphash";
	private static final String KEY_QR_CODE_JSON = "de.hanneseilers.qrrallye.qrcodejson";
	private static final String KEY_SCANNED_DATA = "de.hanneseilers.qrrallye.scanneddata";
	
	public static String mGroupHash = null;
	public static MainActivity INSTANCE = null;
	private QRCodeJson mRallye = null;
	
	public TextView txtSolutionTitle;
	public EditText txtSolution;
	public EditText txtGroupname;
	public Button btnSubmit;
	public Button btnScan;
	public Button btnClearList;
	public LinearLayout lstScannedItems;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// set group id
		mGroupHash = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
		if(mGroupHash == null){
			mGroupHash = ((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
		}
		
		// get widgets
		txtSolutionTitle = (TextView) findViewById(R.id.txtSolutionTitle);
		txtSolution = (EditText) findViewById(R.id.txtSolution);
		txtGroupname = (EditText) findViewById(R.id.txtGroupname);
		btnClearList =(Button) findViewById(R.id.btnClearList);
		btnScan = (Button) findViewById(R.id.btnScan);
		btnSubmit = (Button) findViewById(R.id.btnSubmit);
		lstScannedItems = (LinearLayout) findViewById(R.id.lstScannedItems);
		
		// set listener
		btnClearList.setOnClickListener(this);
		btnScan.setOnClickListener(this);
		btnSubmit.setOnClickListener(this);
		
		// restore state
		if( savedInstanceState != null ){
			if( savedInstanceState.containsKey(KEY_GROUPHASH) )
				mGroupHash = savedInstanceState.getString(KEY_GROUPHASH);
			if( savedInstanceState.containsKey(KEY_QR_CODE_JSON) )
				mRallye = (new Gson()).fromJson(savedInstanceState.getString(KEY_QR_CODE_JSON), QRCodeJson.class);
			if( savedInstanceState.containsKey(KEY_SCANNED_DATA) ){
				for( String snippet : savedInstanceState.getString(KEY_SCANNED_DATA).split(";") ){
					lstScannedItems.addView( QRCodeReaderTask.getItemView(snippet) );
				}
			}
				
		}
		
		INSTANCE = this;
	}
	
	public synchronized void setRallye(QRCodeJson vRallye){
		mRallye = vRallye;
	}
	
	public QRCodeJson getRallye(){
		return mRallye;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult vScanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if( vScanResult != null && vScanResult.getContents() != null ){
			(new QRCodeReaderTask()).execute(new String[]{ vScanResult.getContents() });
		}
	}

	@Override
	public void onClick(View v) {
		if( v == btnClearList ){
			lstScannedItems.removeAllViews();
		} else if( v == btnScan ){
			(new IntentIntegrator(this)).initiateScan(); 
		} else if( v == btnSubmit ){
			(new SolutionSubmitTask()).execute(new String[]{ txtSolution.getText().toString() });
		}
	}
	

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(KEY_GROUPHASH, mGroupHash);
		if( mRallye != null ) outState.putString(KEY_QR_CODE_JSON, mRallye.toString());
		String vData = "";
		for( int i=0; i < lstScannedItems.getChildCount(); i++ ){
			TextView vTxt = (TextView) lstScannedItems.getChildAt(i);
			if( vData.length() > 0 ) vData += ";";
			vData += vTxt.getText().toString();
		}
		outState.putString(KEY_SCANNED_DATA, vData);
	}
}
