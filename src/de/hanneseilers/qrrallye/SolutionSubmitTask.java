package de.hanneseilers.qrrallye;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.os.AsyncTask;
import de.hanneseilers.qrrallye.SolutionSubmitTask.ReturnCodes;;

public class SolutionSubmitTask extends AsyncTask<String, Void, ReturnCodes> {

	@Override
	protected ReturnCodes doInBackground(String... params) {
		if( params.length > 0 ){
			String vSolution = params[0];
			QRCodeJson vCode = MainActivity.INSTANCE.getRallye();
			
			if( vCode != null ){
				try {
					
					// submit solution
					String vGroupname = MainActivity.INSTANCE.txtGroupname.getText().toString();
					if( vGroupname == null || vGroupname.length() < 3 ){
						return ReturnCodes.GROUPNAME_FAIL;
					}
					String vUrl = vCode.getUrl() + "?f=5&rID=" + vCode.getRallyeID()
							+ "&gHash=" + URLEncoder.encode(MainActivity.mGroupHash, "UTF-8")
							+ "&gName=" + URLEncoder.encode(vGroupname, "UTF-8")
							+ "&S=" + URLEncoder.encode(vSolution, "UTF-8");
					String vResponse = QRCodeReaderTask.readFromURL(vUrl);
					if( vResponse != null && vResponse.startsWith(":") ){
						switch( Response.valueOf(vResponse.replace(":", "")) ){
						case RALLEY_DONE:
							return ReturnCodes.RALLYE_DONE;
						case SOLUTION_OK:
							return ReturnCodes.SOLUTION_OK;
						default:
							break;						
						}
					}
					
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}				
			}
		}
		
		return ReturnCodes.FAIL;
	}
	
	protected void onPostExecute(ReturnCodes result) {
		if( result == ReturnCodes.SOLUTION_OK ){
			MainActivity.INSTANCE.lstScannedItems.removeAllViews();
			(new QRDialog(R.string.dialog_title_info, R.string.solution_ok, R.string.dialog_button_ok, null))
				.show(MainActivity.INSTANCE.getSupportFragmentManager(), "Solution");
		}
		else if( result == ReturnCodes.RALLYE_DONE ){
			MainActivity.INSTANCE.lstScannedItems.removeAllViews();
		}
		else if( result == ReturnCodes.GROUPNAME_FAIL ){
			(new QRDialog(R.string.dialog_title_error, R.string.groupname_fail, R.string.dialog_button_ok, null))
				.show(MainActivity.INSTANCE.getSupportFragmentManager(), "Solution");
		}
		else {
			(new QRDialog(R.string.dialog_title_error, R.string.solution_false, R.string.dialog_button_ok, null))
				.show(MainActivity.INSTANCE.getSupportFragmentManager(), "Solution");
		}
		
		// Update number of solved items
		MainActivity.INSTANCE.updateSolvedItems();
	}
	
	public enum ReturnCodes{
		
		SOLUTION_OK,
		RALLYE_DONE,
		FAIL,
		GROUPNAME_FAIL;
		
		
	}

}
