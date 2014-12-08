package de.hanneseilers.qrrallye;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class QRDialog extends DialogFragment {
	
	private int mMessage;
	private int mTitle;
	private int mPositiveButton;
	private int mNegativeButton;
	private OnClickListener mPositiveButtonListener;
	private OnClickListener mNegativeButtonListener;

	/**
	 * Constructor
	 * @param aMessage
	 * @param aTitle
	 */
	public QRDialog(int aTitle, int aMessage){
		this(aMessage, aTitle, -1, null);
	}
	
	/**
	 * Constructor
	 * @param aMessage
	 * @param aTitle
	 * @param aPositiveButton
	 * @param aPositiveButtonListener
	 */
	public QRDialog(int aTitle, int aMessage, int aPositiveButton, OnClickListener aPositiveButtonListener){
		this( aMessage, aTitle, aPositiveButton, -1, aPositiveButtonListener, null );
	}
	
	/**
	 * Constructor
	 * @param aMessage
	 * @param aTitle
	 * @param aPositiveButton
	 * @param aNegativeButton
	 * @param aPositveButtonListener
	 * @param aNegativeButtonListener
	 */
	public QRDialog(int aTitle, int aMessage, int aPositiveButton, int aNegativeButton,
			OnClickListener aPositveButtonListener, OnClickListener aNegativeButtonListener) {
		mMessage = aMessage;
		mTitle = aTitle;
		mPositiveButton = aPositiveButton;
		mNegativeButton = aNegativeButton;
		mPositiveButtonListener = aPositveButtonListener;
		mNegativeButtonListener = aNegativeButtonListener;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder vBuilder = new AlertDialog.Builder(getActivity());
		
		vBuilder.setTitle(mMessage)
		.setMessage(mTitle);
		
		if( mPositiveButton >= 0 )
			vBuilder.setPositiveButton(mPositiveButton, mPositiveButtonListener);
		if( mNegativeButton >= 0 )
			vBuilder.setNegativeButton(mNegativeButton, mNegativeButtonListener);
		
		
		return vBuilder.create();
	}
	
}
