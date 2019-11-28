package ice_tts;


public class TTSICE {
	private volatile intObj threadToGo = new intObj();
	private volatile intObj stopSpeaking = new intObj();
	
	private volatile stringObj ttsText = new stringObj();
	private volatile stringObj ttsVoice = new stringObj();
	
	private TTSThread ttsThread = new TTSThread();
	
	private volatile floatObj ttsGainValue = new floatObj();
	
	public TTSICE() {
		setVoice("cmu-slt-hsmm");
	}
	
	private class intObj {
		public int value;
	}
	
	private class floatObj {
		public float value;
	}
	
	private class stringObj {
		public String value;
	}

	// Alias to the private TextToSpeech object
	public void speak(String text) {
		ttsText.value = text;
		threadToGo.value = 1;
	}
	
	public void setGainValue(float gainValue) {
		ttsGainValue.value = gainValue;
	}
			
	public void setVoice(String text) {
		ttsVoice.value = text;
	}
			
	public void stopSpeaking() {
		stopSpeaking.value = 1;
	}
	
	public void start(String text) {
		stopSpeaking.value = 0;
		
		speak(text);
		setGainValue(2.0f);
		
		ttsThread.setDaemon(true);
		ttsThread.start();
	}
	
	public class TTSThread extends Thread{
		private TextToSpeech tts;
		
	    public TTSThread() {
	       this.tts = new TextToSpeech();
	    }

		public void run() {
			while (true) {
				if (threadToGo.value==1) {
	            	tts.speak(ttsText.value, ttsGainValue.value, true, true);
	            	threadToGo.value = 0;
	            }
				
				if (stopSpeaking.value==1) {
					tts.stopSpeaking();
					stopSpeaking.value = 0;
				}
				
				try {
					sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}


