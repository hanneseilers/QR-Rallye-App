package de.hanneseilers.qrrallye;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class QRCodeReaderTask extends AsyncTask<String, Void, SnippetResponse>{
	
	@Override
	protected SnippetResponse doInBackground(String... arg0) {
		if( arg0.length > 0
				&& arg0[0].startsWith("{") && arg0[0].endsWith("}") ){
			
			// get code
			arg0[0] = arg0[0].replace("\\", "");
			try{
				QRCodeJson vCode = (new Gson()).fromJson(arg0[0], QRCodeJson.class);
				MainActivity.INSTANCE.setRallye(vCode);
				
				// get rallye information
				String vUrl = vCode.getUrl()+"?f=1&rID=" + vCode.getRallyeID();
				String vResponse = readFromURL(vUrl);
				if( vResponse != null
						&& vResponse.startsWith("{") && vResponse.endsWith("}") ){
					RallyeInformation vRallye = (new Gson()).fromJson(vResponse, RallyeInformation.class);
							
					try {
						// get groupname
						String vGroupName = MainActivity.INSTANCE.txtGroupname.getText().toString();
						if( vGroupName.length() < 3 ){
							return new SnippetResponse(null, ":"+Response.GROUPNAME_ERROR.name());
						}
						
						// get snipped
						vUrl = vCode.getUrl()+"?f=2&rID=" + vCode.getRallyeID()
								+ "&gHash=" + URLEncoder.encode(MainActivity.mGroupHash, "UTF-8")
								+ "&gName=" + URLEncoder.encode(vGroupName, "UTF-8")
								+ "&n=" + vCode.getSnippetNumber();
						vResponse = readFromURL(vUrl);
						if( vResponse != null ){
							Log.d(MainActivity.TAG, "Snippet: " + vResponse);
							return new SnippetResponse(vRallye, vResponse);
						}
						
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
						
				}				
			} catch( JsonSyntaxException e ){
				e.printStackTrace();
			}
			
			
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(SnippetResponse result) {
		if( result != null ){
			
			// check snippet
			if( result.getSnippet().startsWith(":") ){
				
				switch( Response.valueOf(result.getSnippet().replace(":", "")) ){					
				case SNIPPET_EOA:
					(new QRDialog(R.string.dialog_title_error,
							R.string.dialog_snippet_eoa, R.string.dialog_button_cancel, null))
					.show(MainActivity.INSTANCE.getSupportFragmentManager(), "DIALOG");
					break;
					
				case SNIPPET_TIMEOUT:
					(new QRDialog(R.string.dialog_title_error,
							R.string.dialog_snippet_timeout, R.string.dialog_button_cancel, null))
					.show(MainActivity.INSTANCE.getSupportFragmentManager(), "DIALOG");
					break;
					
				case SNIPPET_NOT_ONLINE:
					(new QRDialog(R.string.dialog_title_error,
							R.string.dialog_snippet_not_online, R.string.dialog_button_cancel, null))
					.show(MainActivity.INSTANCE.getSupportFragmentManager(), "DIALOG");
					break;
					
				case GROUPNAME_ERROR:
					(new QRDialog(R.string.dialog_title_error,
							R.string.groupname_fail, R.string.dialog_button_cancel, null))
					.show(MainActivity.INSTANCE.getSupportFragmentManager(), "DIALOG");
					break;
					
				default:
					break;				
				}				
				
				
			} else {
			
				// add snippet				
				MainActivity.INSTANCE.lstScannedItems.addView(getItemView(result.getSnippet()));
				
			}
			
			// update solved items
			MainActivity.INSTANCE.updateSolvedItems();
			
		}
	}
	
	/**
	 * Get snippet {@link TextView}
	 * @param aSnippet	{@link String} snippet to show
	 * @return			{@link TextView}
	 */
	public static TextView getItemView(String aSnippet){
		TextView vTxt = new TextView(MainActivity.INSTANCE);
		
		vTxt.setText( aSnippet );
		vTxt.setTextAppearance(MainActivity.INSTANCE, android.R.style.TextAppearance_Large);
		vTxt.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				String vSolution = MainActivity.INSTANCE.txtSolution.getText().toString();
				vSolution += ((TextView) v).getText().toString();
				MainActivity.INSTANCE.txtSolution.setText(vSolution);
			}
		});
		
		return vTxt;
	}
	
	/**
	 * Read data from url.
	 * @param aUrl	{@link String} url
	 * @return		{@link String} response
	 */
	public static String readFromURL(String aUrl){
		try {
			
			Log.d(MainActivity.TAG, "Read from " + aUrl);
			URL vUrl = new URL(aUrl);
			URLConnection vConnection = vUrl.openConnection();
			BufferedReader vInputReader = new BufferedReader(new InputStreamReader( vConnection.getInputStream() ));
			
			String vLine = "";
			String vResponse = "";
			while( (vLine = vInputReader.readLine()) != null){
				vResponse += vLine;
			}
			
			return vResponse;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
