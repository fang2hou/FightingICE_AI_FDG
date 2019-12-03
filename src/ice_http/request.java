package ice_http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

public class request {

	public static String get(String rURL, Map<String, String> rParams) throws IOException {
	    URL urlForGetRequest = new URL(rURL);
	    String readLine = null;
	    HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
	    conection.setRequestMethod("GET");
	    
	    rParams.forEach((k, v) -> {
	    	conection.setRequestProperty(k, v);
	    });
	    
	    int responseCode = conection.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        BufferedReader in = new BufferedReader(
	            new InputStreamReader(conection.getInputStream()));
	        StringBuffer response = new StringBuffer();
	        while ((readLine = in .readLine()) != null) {
	            response.append(readLine);
	        } in .close();
	        return response.toString();
	    } else {
	        System.out.println("[ICEHTTPRequest] GET Not worked.");
	        return null;
	    }
	}
	
	public static String get(String rURL) throws IOException {
	    URL urlForGetRequest = new URL(rURL);
	    String readLine = null;
	    HttpURLConnection conection = (HttpURLConnection) urlForGetRequest.openConnection();
	    conection.setRequestMethod("GET");
	    
	    int responseCode = conection.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK) {
	        BufferedReader in = new BufferedReader(
	            new InputStreamReader(conection.getInputStream()));
	        StringBuffer response = new StringBuffer();
	        while ((readLine = in .readLine()) != null) {
	            response.append(readLine);
	        } in .close();
	        return response.toString();
	    } else {
	        System.out.println("[ICEHTTPRequest] GET Not worked.");
	        return null;
	    }
	}
	
	public static String post(String rURL, Map<String, String> rParams) throws IOException {
		StringBuffer sb = new StringBuffer();  
        if (rParams != null) {  
            for (Entry<String, String> e : rParams.entrySet()) {  
                sb.append(e.getKey());  
                sb.append("=");  
                sb.append(e.getValue());  
                sb.append("&");  
            }  
            sb.substring(0, sb.length() - 1);  
        }
        
	    URL obj = new URL(rURL);
	    HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
	    postConnection.setRequestMethod("POST");
	    postConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
	    postConnection.setDoOutput(true);
	    OutputStream os = postConnection.getOutputStream();
	    os.write(sb.toString().getBytes());
	    os.flush();
	    os.close();
	    if (postConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	            postConnection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = in .readLine()) != null) {
	            response.append(inputLine);
	        } in .close();
	        return response.toString();
	    } else {
	    	System.out.println("[ICEHTTPRequest] POST Not worked.");
	    	return null;
	    }
	}
	public static String post(String rURL) throws IOException {
	    URL obj = new URL(rURL);
	    HttpURLConnection postConnection = (HttpURLConnection) obj.openConnection();
	    postConnection.setRequestMethod("POST");
	    
	    postConnection.setDoOutput(true);
	    OutputStream os = postConnection.getOutputStream();
	    os.flush();
	    os.close();
	    int responseCode = postConnection.getResponseCode();
	    if (responseCode == HttpURLConnection.HTTP_OK ||
	    	responseCode == HttpURLConnection.HTTP_CREATED) {
	        BufferedReader in = new BufferedReader(new InputStreamReader(
	            postConnection.getInputStream()));
	        String inputLine;
	        StringBuffer response = new StringBuffer();
	        while ((inputLine = in .readLine()) != null) {
	            response.append(inputLine);
	        } in .close();
	        return response.toString();
	    } else {
	    	System.out.println("[ICEHTTPRequest] POST Not worked.");
	    	return null;
	    }
	}
}
