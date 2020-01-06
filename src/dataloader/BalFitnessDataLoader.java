package dataloader;

import java.util.Map;

public class BalFitnessDataLoader {
	
	private String filePath;
	private float[] BFData;
	private String[] ActionTable;
	public float[] varData;
	//raw to action Map
	private Map<Integer, String> UkiToRealMap;
	
	// fp: The path of balance data from UKI
	public BalFitnessDataLoader(String fp) {
		this.filePath = fp;
		this.BFData = new float[23];
		this.ActionTable = new String[23];
		this.varData = new float[23];
		
		InitData();
	}
	
	private void InitData() {
		// This function will also init the action table, use updateData() instead if you want to update data.
		
		String BFRawString = FzReader.readFile(filePath);
		
		// 1-line Data format as: id,action,balance fitness,dec
		String[] BFDataString = BFRawString.split("\n");
		
		// Cleanup
		int cnt = 0;
		for(String BFDataStringSingleLine : BFDataString) {
			String[] singleBFData = BFDataStringSingleLine.split(",");

			int actionIndex = cnt;
			String actionName = singleBFData[1];
			float balFitness = Float.valueOf(singleBFData[2]);
			float varPoint = Float.valueOf(singleBFData[3]);
			
			BFData[actionIndex] = balFitness;
			ActionTable[actionIndex] = actionName;
			varData[actionIndex] = varPoint;
			cnt++;
		}
	}
	
	public void updateData() {

		String BFRawString = FzReader.readFile(filePath);
		
		// 1-line Data format as: id,action,balance fitness,dec
		String[] BFDataString = BFRawString.split("\n");
		
		// Cleanup
		int cnt = 0;
		for(String BFDataStringSingleLine : BFDataString) {
			String[] singleBFData = BFDataStringSingleLine.split(",");
			
			int actionIndex = cnt;
			float balFitness = Float.valueOf(singleBFData[2]);
			float varPoint = Float.valueOf(singleBFData[3]);
			
			BFData[actionIndex] = balFitness;
			varData[actionIndex] = varPoint;
			cnt++;
		}
	}

	public float getBalFitnessById(Integer id) {
		return BFData[id];
	}
	
	public String getActionNameById(Integer id) {
		return ActionTable[id];
	}
	
	public float getvarDataById(Integer id) {
		return varData[id];
	}
	
	public int getMaxVarActionId() {
		int cnt = 0;
		float maxVarValue = -100.0f;		
		int maxVarActionId = 0;		
		for (; cnt < 23; ++cnt) {
			if (getvarDataById(cnt) > maxVarValue) {
			maxVarValue = getvarDataById(cnt);
			maxVarActionId = cnt;
			}
		}
		
		return maxVarActionId;
	}
	
	
	
}