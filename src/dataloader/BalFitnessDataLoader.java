package dataloader;
public class BalFitnessDataLoader {
	
	private String filePath;
	private float[] BFData;
	private String[] ActionTable;
	
	// fp: The path of balance data from UKI
	public BalFitnessDataLoader(String fp) {
		this.filePath = fp;
		this.BFData = new float[26];
		this.ActionTable = new String[26];
		
		InitData();
	}
	
	private void InitData() {
		// This function will also init the action table, use updateData() instead if you want to update data.
		
		String BFRawString = FzReader.readFile(filePath);
		
		// 1-line Data format as: id,action,balance fitness,dec
		String[] BFDataString = BFRawString.split("\n");
		
		// Cleanup
		for(String BFDataStringSingleLine : BFDataString) {
			String[] singleBFData = BFDataStringSingleLine.split(",");

			int actionIndex = Integer.valueOf(singleBFData[0]) - 1;
			String actionName = singleBFData[1];
			float balFitness = Float.valueOf(singleBFData[2]);
			
			BFData[actionIndex] = balFitness;
			ActionTable[actionIndex] = actionName;
		}
	}
	
	public void updateData() {

		String BFRawString = FzReader.readFile(filePath);
		
		// 1-line Data format as: id,action,balance fitness,dec
		String[] BFDataString = BFRawString.split("\n");
		
		// Cleanup
		for(String BFDataStringSingleLine : BFDataString) {
			String[] singleBFData = BFDataStringSingleLine.split(",");
			
			int actionIndex = Integer.valueOf(singleBFData[0]) - 1;
			float balFitness = Float.valueOf(singleBFData[2]);
			
			BFData[actionIndex] = balFitness;
		}
	}

	public float getBalFitnessById(Integer id) {
		return BFData[id];
	}
	
	public String getActionNameById(Integer id) {
		return ActionTable[id];
	}
}