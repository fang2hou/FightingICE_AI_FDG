package ice_agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ice_http.request;

public class TTSBridge {
	
	public String voice_name;
	public String text;
	public String language_code;
	public String gain;
	public String rate;
	public String pitch;
	
	public TTSBridge() {
		this.voice_name = "en-US-Wavenet-D";
		this.language_code = "en-US";
		this.gain = "0";
		this.rate = "1.0";
		this.pitch = "0";
	}
	
	public void speak(String text) {
		
		Map <String, String> mySetting = new HashMap<String, String>();
		mySetting.put("voice_name", this.voice_name);
		mySetting.put("language_code", this.language_code);
		mySetting.put("gain", this.gain);
		mySetting.put("rate", this.rate);
		mySetting.put("pitch", this.rate);
		mySetting.put("text", text);
		try {
			System.out.println(request.post("http://127.0.0.1:1688/icetts/play", mySetting));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
