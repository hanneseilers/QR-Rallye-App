package de.hanneseilers.qrrallye;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.AsyncTask;
import android.widget.Toast;

public class SolutionSubmitTask extends AsyncTask<String, Void, Boolean> {

	@Override
	protected Boolean doInBackground(String... params) {
		if( params.length > 0 ){
			String vSolution = params[0];
			QRCodeJson vCode = MainActivity.INSTANCE.getRallye();
			
			if( vCode != null ){
				try {
					
					// submit solution
					String vUrl = vCode.getUrl() + "?f=5&rID=" + vCode.getRallyeID()
							+ "&gHash=" + URLEncoder.encode(MainActivity.mGroupHash, "UTF-8")
							+ "&gName=" + URLEncoder.encode(
									MainActivity.INSTANCE.txtGroupname.getText().toString(), "UTF-8")
							+ "&S=" + URLEncoder.encode(vSolution, "UTF-8");
					String vResponse = QRCodeReaderTask.readFromURL(vUrl);
					if( vResponse != null && vResponse.startsWith(":") ){
						switch( Response.valueOf(vResponse.replace(":", "")) ){
						case RALLEY_DONE:
							return true;
						case SOLUTION_OK:
							return true;
						default:
							break;						
						}
					}
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}				
			}
		}
		
		return false;
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		if( result ){
			Toast.makeText(MainActivity.INSTANCE, R.string.solution_ok, Toast.LENGTH_LONG).show();
			MainActivity.INSTANCE.lstScannedItems.removeAllViews();
		} else {
			Toast.makeText(MainActivity.INSTANCE, R.string.solution_false, Toast.LENGTH_LONG).show();
		}
	}

}
