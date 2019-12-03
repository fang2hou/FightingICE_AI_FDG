package ice_agent;

import java.io.IOException;

import ice_http.request;

public class UKIBridge {
	public String read() throws IOException {
		return request.get("http://127.0.0.1:1688/fu_bridge/read");
	}
	
	public boolean save(String text) throws IOException {
		String result = request.post("http://127.0.0.1:1688/fu_bridge/save/"+text);
		if (result == "Saved") {
			return true;
		} else {
			return false;
		}
	}
}
