package de.hanneseilers.qrrallye;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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
import android.os.AsyncTask;
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
	
	/**
	 * Updates the number of solved items
	 */
	public void updateSolvedItems(){		
		(new AsyncTask<Void, Void, String[]>(){

			@Override
			protected String[] doInBackground(Void... params) {
				try{
					
					// get information about solved and total items
					if( mRallye != null ){
						String vUrl = mRallye.getUrl()+"?f=3&rID=" + mRallye.getRallyeID();
						String vItemsTotal = QRCodeReaderTask.readFromURL(vUrl);				
						System.out.println("items total: " + vItemsTotal);
						
						if( vItemsTotal != null ){
							vUrl = mRallye.getUrl()+"?f=4&rID=" + mRallye.getRallyeID()
										+ "&gHash=" + URLEncoder.encode(mGroupHash, "UTF-8");
							String vItemsSolved = QRCodeReaderTask.readFromURL(vUrl);
							System.out.println("solved items: " + vItemsSolved);
							
							if( vItemsSolved != null ){
								return new String[]{vItemsSolved, vItemsTotal};
							}
						}
					}
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void onPostExecute(String[] result) {
				if( result != null && result.length > 1 ){
					txtSolutionTitle.setText( getResources().getString(R.string.solution_title)
							+ " " + result[0] + "/" + result[1] );
					
					if( Integer.parseInt(result[1]) - Integer.parseInt(result[0]) <= 0 ){
						(new QRDialog(R.string.dialog_title_info, R.string.rallye_finished, R.string.dialog_button_ok, null))
						.show(getSupportFragmentManager(), "Solution");
						lstScannedItems.removeAllViews();
					}
				}
			}
			
		}).execute();		
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
