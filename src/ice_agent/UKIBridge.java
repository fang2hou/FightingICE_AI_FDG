package ice_agent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ice_http.request;

public class UKIBridge {
	public String read() throws IOException {
		return request.get("http://127.0.0.1:1688/fu_bridge/read");
	}
	
	public boolean save(String text) throws IOException {
		Map <String, String> mySetting = new HashMap<String, String>();
		mySetting.put("data", text);
		try {
			String result = request.post("http://127.0.0.1:1688/fu_bridge/save", mySetting);
			if (result == "Saved") {
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
