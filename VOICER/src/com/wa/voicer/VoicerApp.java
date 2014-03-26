package com.wa.voicer;

import java.util.ArrayList;
import java.util.Hashtable;

import android.os.Bundle;
import android.os.PowerManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class VoicerApp extends Activity {

	private static final String TAG = "VOICER";
	ListView mList;
	TextView mInputStatus;
	String mError = "";

	private SpeechRecognizer mSpeechRecognizer;
	private Intent mRecognizerIntent;
	private PowerManager.WakeLock mWakeLock;

	Hashtable<String, String> voiceMatches = new Hashtable<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mList = (ListView) findViewById(R.id.voiceResults);
		mInputStatus = (TextView) findViewById(R.id.inputState);

		startSR();
	}

	
	private void startSR() {
		
		Log.d(TAG,"speech recognition available: "+ SpeechRecognizer.isRecognitionAvailable(getBaseContext()));
		//
		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(getBaseContext());
		mSpeechRecognizer.setRecognitionListener(mRecognitionListener);
		mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH); // or LANGUAGE_MODEL_FREE_FORM
		mRecognizerIntent.putExtra("calling_package", "com.wa.voicer");
		mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 12);
		//
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
				"DoNotDimScreen");
		//
		mWakeLock.acquire();
		mSpeechRecognizer.startListening(mRecognizerIntent);
	}

	
	private RecognitionListener mRecognitionListener = new RecognitionListener() {

		@Override
		public void onBeginningOfSpeech() {
		}

		@Override
		public void onBufferReceived(byte[] buffer) {
		}

		@Override
		public void onEndOfSpeech() {
			mInputStatus.setText("Processing...");
		}

		@Override
		public void onError(int error) {
			
			switch (error) {
			case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
				mError = " network timeout";
				mInputStatus.setText(mError);
				// startListening();
				mSpeechRecognizer.startListening(mRecognizerIntent);
				break;
			case SpeechRecognizer.ERROR_NETWORK:
				mError = " network";
				mInputStatus.setText(mError);
				//possibly used if Android < 4.1
				return;
			case SpeechRecognizer.ERROR_AUDIO:
				mError = " audio";
				mInputStatus.setText(mError);
				break;
			case SpeechRecognizer.ERROR_SERVER:
				mError = " server";
				mInputStatus.setText(mError);
				// startListening();
				mSpeechRecognizer.startListening(mRecognizerIntent);
				break;
			case SpeechRecognizer.ERROR_CLIENT:
				mError = " client";
				mInputStatus.setText(mError);
				break;
			case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
				mError = " speech time out";
				mInputStatus.setText(mError);
				mSpeechRecognizer.startListening(mRecognizerIntent);
				break;
			case SpeechRecognizer.ERROR_NO_MATCH:
				mError = " no match";
				mInputStatus.setText(mError);
				// startListening();
				mSpeechRecognizer.startListening(mRecognizerIntent);
				break;
			case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
				mError = " recogniser busy";
				mInputStatus.setText(mError);
				cancelSpeechRecognition();
				mSpeechRecognizer.destroy();
				startSR();
				break;
			}
			Log.i(TAG, "Error: " + error + " - " + mError);

		}

		@Override
		public void onEvent(int eventType, Bundle params) {
		}

		@Override
		public void onPartialResults(Bundle partialResults) {
		}

		@Override
		public void onReadyForSpeech(Bundle params) {
			Log.d(TAG, "onReadyForSpeech");
			mInputStatus.setText("Speak now");
		}

		@Override
		public void onResults(Bundle results) {
			String cmd;

			Log.d(TAG, "onResults");

			ArrayList<String> matches = results
					.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
			cmd = getCommand(matches);
			if (cmd == null) {
				for (String s : matches) {
					Log.d(TAG, s);
					if ("forward".equals(s)) {
						Toast.makeText(getApplicationContext(),"matched forward", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched forward");
						cmd = "forward";
						addCommandMatches(cmd, matches);
						break;
					} else if ("stop".equals(s)) {
						Toast.makeText(getApplicationContext(), "matched stop",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched stop");
						cmd = "stop";
						addCommandMatches(cmd, matches);
						break;
					} else if ("reverse".equals(s)) {
						Toast.makeText(getApplicationContext(),"matched reverse", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched reverse");
						cmd = "reverse";
						addCommandMatches(cmd, matches);
						break;
					} else if ("right".equals(s)) {
						Toast.makeText(getApplicationContext(), "matched right",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched right");
						cmd = "right";
						addCommandMatches(cmd, matches);
						break;

					} else if ("left".equals(s)) {
						Toast.makeText(getApplicationContext(), "matched left",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched left");
						cmd = "left";
						addCommandMatches(cmd, matches);
						break;
					} else if ("center".equals(s)) {
						Toast.makeText(getApplicationContext(),"matched center", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched center");
						cmd = "center";
						addCommandMatches(cmd, matches);
						break;
					} else if (Integer.toString(1).equals(s)) {
						Toast.makeText(getApplicationContext(), "matched 1",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched 1");
						cmd = Integer.toString(1);
						addCommandMatches(cmd, matches);
						break;
					} else if (Integer.toString(2).equals(s)) {
						Toast.makeText(getApplicationContext(), "matched 2",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched 2");
						cmd = Integer.toString(2);
						addCommandMatches(cmd, matches);
						break;
					} else if (Integer.toString(3).equals(s)) {
						Toast.makeText(getApplicationContext(), "matched 3",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched 3");
						cmd = Integer.toString(3);
						addCommandMatches(cmd, matches);
						break;
					} else if (Integer.toString(4).equals(s)) {
						Toast.makeText(getApplicationContext(), "matched 4",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched 4");
						cmd = Integer.toString(4);
						addCommandMatches(cmd, matches);
						break;
					} else if (Integer.toString(5).equals(s)) {
						Toast.makeText(getApplicationContext(), "matched 5",Toast.LENGTH_SHORT).show();
						Log.d(TAG, "matched 5");
						cmd = Integer.toString(5);
						addCommandMatches(cmd, matches);
						break;
					}

				}
			}
			if (cmd == null)
				mList.setAdapter(new ArrayAdapter<String>(getBaseContext(),
						android.R.layout.simple_list_item_1, matches));
			else
				parseCommand(cmd);
			mInputStatus.setText("Initializing...");
			mSpeechRecognizer.startListening(mRecognizerIntent);
		}

		@Override
		public void onRmsChanged(float rmsdB) {
		}

	};

	private void parseCommand(String cmd) {

	}

	

	private void addCommandMatches(String cmd, ArrayList<String> matches) {
		if (!voiceMatches.containsKey(cmd)) {
			voiceMatches.put(cmd, cmd);
		}
		for (String s : matches) {
			voiceMatches.put(s, cmd);
		}
	}

	
	private String getCommand(ArrayList<String> matches) {
		for (String s : matches) {
			String cmd = voiceMatches.get(s);
			if (cmd != null)
				return cmd;
		}
		return null;
	}

	
	public void onResume() {
		super.onResume();
		Log.d(TAG, "restarting speech recognition");
		mSpeechRecognizer.startListening(mRecognizerIntent);
		mWakeLock.acquire();
	}

	
	@Override
	public void onDestroy() {

		Log.d(TAG, "onDestroy: stopping listening");
		cancelSpeechRecognition();
		mSpeechRecognizer.destroy();

		super.onDestroy();
	}

	
	@Override
	public void onPause() {
		super.onPause();
		Log.d(TAG, "onPause: stopping speech recognition");
		cancelSpeechRecognition();
		if (mWakeLock.isHeld())
			mWakeLock.release();
	}

	
	private void cancelSpeechRecognition() {
		mSpeechRecognizer.stopListening();
		mSpeechRecognizer.cancel();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
