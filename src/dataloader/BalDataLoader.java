package dataloader;
public class BalDataLoader {
	
	private String filePath;
	private float[] BalData;
	
	// fp: The path of balance data from UKI
	public BalDataLoader(String fp) {
		this.filePath = fp;
		this.BalData = new float[5];
		updateData();
	}
	
	public void updateData() {
		String balRawString = FzReader.readFile(filePath);
		
		// Data format as: Total_Bal \n ArmR,ArmL,LegR,LegL
		String[] balDataString = balRawString.split("((\n)|,)");
		
		// Cleanup
		for(int index = 0; index < 5; index++) {
			BalData[index] = Float.valueOf(balDataString[index].replaceAll("\r|\n", ""));
		}
	}

	public float getTotal() {
		return BalData[0];
	}
	
	public float getArmR() {
		return BalData[1];
	}
	
	public float getArmL() {
		return BalData[2];
	}
	
	public float getLegR() {
		return BalData[3];
	}
	
	public float getLegL() {
		return BalData[4];
	}
	
}