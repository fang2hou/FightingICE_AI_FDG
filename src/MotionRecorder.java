
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class MotionRecorder {

	public ArrayList<String[]> castr;

	public String line;

	public String[] sline;
	//kusano program end
	
	//TODO PDA AI parameter start
	public File cmdFile;//CMD file
	
	public File tableFile;//body usage of motions' table
	
	public String playerTakingAction = "STAND";
	
	public int actionNumber;
	
	public float[] bodySegmentTotalUsage = {(float) 0.0,(float) 0.0,(float) 0.0,(float) 0.0};//movement of body in total of each game ArmR, ArmL, LegR, LrgL
	
	public float[] bodySegmentTotalUsageGap = {(float) 0.0,(float) 0.0};//difference between movement of body in total of each game Arm, Leg, Negative if R > L
	
	public float[] bodySegmentUsage = {(float) 0.0,(float) 0.0,(float) 0.0,(float) 0.0};//one time movement of body of each game ArmR, ArmL, LegR, LrgL
	
	public float[] bodySegmentUsageGap = {(float) 0.0,(float) 0.0};//difference between one time movement of body in total of each game Arm, Leg, Negative if R > L
	
	public float[] formerBodySegmentTotalUsage = {(float) 0.0,(float) 0.0,(float) 0.0,(float) 0.0};
	
	public float[] formerBodySegmentTotalUsageGap = {(float) 0.0,(float) 0.0};
	
	public ArrayList<String[]> str;
	
	public ArrayList<float[]> motionMovementList;
	
	public float[] fline = {(float)0.0,(float)0.0,(float)0.0,(float)0.0};// movement data
	
	public String[] movementNameList = new String[13];// name for movement data
	
	public float probability;//probability calculated by table
	
	public boolean decision = false;//decision calculated by probability
	
	public float mappingParameter = (float) 0.0;
	
	//For MIG
	//0 right 1 left
	
	public int[] standAcount = {0,0};//punch
	
	public int [] standBcount = {0,0};//knee
	
	public int [] crouchBcount = {0,0};//kick
	
	public int hadoken = 0;//hadoken
	
	//this contains motion names of MIG
	private static final String[] MotionName = { "FORWARD_WALK", "DASH", "BACK_STEP", "JUMP", "STAND_GUARD", "STAND_A", "STAND_B", 
	"CROUCH_B", "STAND_FA", "STAND_FB", "CROUCH_FB", "STAND_D_DF_FA" , "STAND"};

	//this contains motion's effect 
	//0 none
	//1 right leg
	//2 left leg
	//3 right wrist
	//4 left wrist
	private static final int[] MotionEffect = { 1, 0, 0, 0, 1, 4, 2, 2, 3, 1, 1, 0, 0};

	
	public int[] motionCount = {0,0,0,0,0,0,0,0,0,0,0,0,0};//count the motions
	
	//PDA AI parameter end	
	public MotionRecorder() {

	}
	
	
	
	//TODO PDA AI function start

	public void initPdaForHealth() {
		readMotionDataFromFile();
		playerTakingAction = readCMD(cmdFile);
	}
	
	public void runPdaForHealth() {
			
		//PDA addition start
		playerTakingAction = readCMD(cmdFile);
		//System.out.println("playerTakingAction = " + playerTakingAction);
		readMotionTable();
		//if actionNumber = -1 then they didn't get the new motion
		if (actionNumber != -1) {
			probability = calculateProbability();
			decision = decideOpponentAttitude(probability);			
		}

		//PDA addition end
	}
	
	public void readMotionDataFromFile() {
		//read motion file
			try {
				// 各モーションの運動量のファイル
				FileReader fr = new FileReader("data/aiData/MotionMIG.csv");
				BufferedReader br = new BufferedReader(fr);

				this.str = new ArrayList<String[]>();
				this.motionMovementList = new ArrayList<float[]>();
				
				
				line = br.readLine();
				//System.out.println("succeed read file");
				// 1行ごとにString[]化、リストstrに追加
				while (true) {

					line = br.readLine();

					if (line == null)
						break;				
					sline = line.split(",");
					str.add(sline.clone());
									
				}
				for(int i = 0; i < str.size(); i++) {
					String[] s = this.str.get(i);
					//System.out.println("raw =" + s[0] + " " + s[1] + " " + s[2] + " " + s[3] + " " + s[4]);
					//read motion data from string to float, 0 name 3 legR 4 legL 9 WristR 10 WristL
					//this.movementNameList[i] = s[0];
					this.fline[0] = Float.parseFloat(s[3]);
					this.fline[1] = Float.parseFloat(s[4]);
					this.fline[2] = Float.parseFloat(s[9]);
					this.fline[3] = Float.parseFloat(s[10]);
					//System.out.println("fline =" + this.fline[0] + " " + this.fline[1] + " " + this.fline[2] + " " + this.fline[3]);
					motionMovementList.add(fline.clone());
				}
				
				for ( int i = 0; i < motionMovementList.size() ;i++) {
					float[] f = motionMovementList.get(i);
					
					//System.out.println("raw =" + f[0] + " " + f[1] + " " + f[2] + " " + f[3]);
				}
				
				br.close();
			} catch (IOException ex) {
				// 例外発生時処理
				ex.printStackTrace();
			}
			

		//read motion file end
	}
	

	
	//read real-time action from uki
	public String readCMD(File cmdFile){
		try {	
			BufferedReader reader = new BufferedReader(new FileReader("C:\\FTG\\CMD1.txt"));
			String output;
			if (reader.ready()) {
				output = reader.readLine();
			//System.out.println("read from CMD " + output);
			} else {
				output = "STAND";
			}
			return output; 

			
		} catch(Exception e) {
			e.getStackTrace();
			return null;
		}
	}
	
	//read one player motion data from converted table of motion.csv
	//also add it into bodySegmentUsage for total count and calculate bodySegmentUsageGap
	public void readMotionTable(){
		
		int counter = 0;

		for(;counter < MotionRecorder.MotionName.length; ++counter) {
			//TODO debug
			//System.out.println("p= " + playerTakingAction);
			//System.out.println("m= " +movementNameList[counter].substring(1));
			
			if (this.playerTakingAction.equals(MotionName[counter])) {
				//System.out.println("get action " + counter);	
				this.actionNumber = counter;
			} else {
				//System.out.println("no");						
			}
		}
		//if successfully get actionNumber
		if (this.actionNumber != -1) {
			motionCount[actionNumber]++;
		}

	}

	public float calculateProbability() {		
		
		int counter;
		
		float prob = 33.33f;
		
		int intProb = 0;
		
		int controlParameter = 3;//maximum unhealthy motion attempts
		
		//calculate balance by the motionCount, right - left, if positive means player use right part more, if negative means player use left part more
		//both leg and wrist decide 40% of probability , 20% is for believability .
		int legBalance;
		
		int wristBalance;
		
		int totalBalance;
			
		//for friendly viewing, list all countable actions
		int forward_walk = motionCount[0];
		int stand_guard = motionCount[4];
		int stand_a_leftpunch = motionCount[5];
		int stand_b_leftknee = motionCount[6];
		int crouch_b_leftkick = motionCount[7];
		int stand_fa_rightpunch = motionCount[8];
		int stand_fb_rightknee = motionCount[9];
		int crouch_fb_rightkick = motionCount[10];
		
		legBalance = forward_walk + stand_guard + stand_fb_rightknee * 2 + crouch_fb_rightkick * 4 - stand_b_leftknee - crouch_b_leftkick * 2;
		
		wristBalance = stand_fa_rightpunch - stand_a_leftpunch;
		
		totalBalance = legBalance + wristBalance;
		
		//System.out.println("legBalance = " + legBalance + " " + "wristBalance = " + wristBalance + " " + "totalBalance = " + totalBalance + " ");
		//System.out.println(actionNumber + " = " + MotionEffect[actionNumber]);
		// find player's motion's effect 
		//0 none
		//1 right leg
		//2 left leg
		//3 right wrist
		//4 left wrist
		if (MotionEffect[actionNumber] == 0) 
		{
			intProb = 90 - totalBalance / controlParameter * 2;
		} else if (MotionEffect[actionNumber] == 1)
		{
			if (legBalance <= 0) {
				intProb = 90 -  wristBalance / controlParameter + legBalance / controlParameter;
				//System.out.println("healthy right leg motion");
				//System.out.println("calculated prob = " + intProb);
			} else if (legBalance > 0) 
			{
				intProb = 10 + wristBalance / controlParameter + legBalance / controlParameter;	
				//System.out.println("unhealthy right leg motion");		
				//System.out.println("calculated prob = " + intProb);
			}
		} else if (MotionEffect[actionNumber] == 2)
		{
			if (legBalance >= 0) {
				intProb = 90 - wristBalance / controlParameter - legBalance / controlParameter;
				//System.out.println("healthy left leg motion");
				//System.out.println("calculated prob = " + intProb);
			} else if (legBalance < 0) 
			{
				intProb = 10 + wristBalance / controlParameter - legBalance / controlParameter;	
				//System.out.println("unhealthy left leg motion");	
				//System.out.println("calculated prob = " + intProb);
			}
		} else if (MotionEffect[actionNumber] == 3)
		{
			if (legBalance <= 0) {
				intProb = 90 - legBalance / controlParameter + wristBalance / controlParameter;
				//System.out.println("healthy right wrist motion");
				//System.out.println("calculated prob = " + intProb);
			} else if (legBalance > 0) 
			{
				intProb = 10 + legBalance / controlParameter + wristBalance / controlParameter;	
//				System.out.println("unhealthy right wrist motion");		
//				System.out.println("calculated prob = " + intProb);
			}			
		} else if (MotionEffect[actionNumber] == 4)
		{
			if (legBalance >= 0) {
				intProb = 90 - legBalance / controlParameter - wristBalance / controlParameter;
//				System.out.println("healthy left wrist motion");
//				System.out.println("calculated prob = " + intProb);
			} else if (legBalance < 0) 
			{
				intProb = 10 + legBalance / controlParameter - wristBalance / controlParameter;	
//				System.out.println("unhealthy left wrist motion");	
//				System.out.println("calculated prob = " + intProb);
			}			
		} else 
		{
			intProb = 0;			
		}
		
			prob = (float)intProb / 100.0f;
//			System.out.println("prob = " + prob);
		
		this.actionNumber = -1;//initialize actionNumber
		
		//normalize it when out of range
		if (prob >= (float) 0.9) 
		{
			prob = (float) 0.9;
		} else if (prob <= (float) 0.1) 
		{
			prob = (float) 0.1;
		} else {
		}
		
		return prob;		
	}
	
	public boolean decideOpponentAttitude(float prob){
		float point = (float) Math.random();
		//TODO DEBUG
//		System.out.println("point =" + point);
//		System.out.println("prob =" + prob);
				
		if (point < prob && point > 0) {
			return true;
			
		} else {
			return false;
		}

	}
	
	//PDA AI function end	
	
}
