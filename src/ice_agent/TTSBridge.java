package ice_agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ice_http.request;

public class TTSBridge {
	
	public String voice_name;
	public String text;
	public String language_code;
	public float gain;
	public float rate;
	public float pitch;
	
	public TTSBridge() {
		this.voice_name = "en-US-Wavenet-D";
		this.language_code = "en-US";
		this.gain = 0f;
		this.rate = 1.0f;
		this.pitch = 0f;
	}
	
	public boolean speak(String text, boolean force) {
		
		Map <String, String> mySetting = new HashMap<String, String>();
		mySetting.put("voice_name", this.voice_name);
		mySetting.put("language_code", this.language_code);
		mySetting.put("gain", String.valueOf(this.gain));
		mySetting.put("rate", String.valueOf(this.rate));
		mySetting.put("pitch", String.valueOf(this.pitch));
		mySetting.put("text", text);
		if (force) {
			mySetting.put("force", "true");
		}
		try {
			String result = request.post("http://127.0.0.1:1688/icetts/play", mySetting);
			boolean isFound = result.contains("TTS is playing.");
			if (isFound) {
				System.out.println(result);
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean speak(String text) {
		
		Map <String, String> mySetting = new HashMap<String, String>();
		mySetting.put("voice_name", this.voice_name);
		mySetting.put("language_code", this.language_code);
		mySetting.put("gain", String.valueOf(this.gain));
		mySetting.put("rate", String.valueOf(this.rate));
		mySetting.put("pitch", String.valueOf(this.pitch));
		mySetting.put("text", text);
		try {
			String result = request.post("http://127.0.0.1:1688/icetts/play", mySetting);
			boolean isFound = result.contains("TTS is playing.");
			if (isFound) {
				System.out.println(result);
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
}
