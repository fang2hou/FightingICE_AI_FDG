import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import aiinterface.AIInterface;
import aiinterface.CommandCenter;
import dataloader.BalDataLoader;
import dataloader.BalFitnessDataLoader;
import dataloader.FzReader;
import enumerate.Action;
import enumerate.State;
import mcts.HighlightMCTS;
import mcts.MCTS;
import mcts.Node;
import mcts.Prediction;
import parameter.FixParameter;
import simulator.Simulator;
import struct.CharacterData;
import struct.FrameData;
import struct.GameData;
import struct.Key;
import struct.MotionData;
import ice_agent.TTSBridge;
import ice_agent.UKIBridge;

/**
 * Environment: FightingICE 4.40+
 *
 * @author Zhou Fang, Junjie Xu
 **/
public class FightingICE_AI_FDG implements AIInterface {

	private Simulator simulator;
	private Key key;
	private CommandCenter commandCenter;
	private boolean playerNumber;
	
	/** 大本のFrameData */
	private FrameData frameData;

	/** 大本よりFRAME_AHEAD分遅れたFrameData */
	private FrameData simulatorAheadFrameData;

	/** 自分が行える行動全て */
	private LinkedList<Action> myNormalActions;
	private LinkedList<Action> myHighlightActions;
	private LinkedList<Action> myHarmlessActions;

	/** 相手が行える行動全て */
	private LinkedList<Action> oppNormalActions;
	private LinkedList<Action> oppHighlightActions;
	
	/** 自分の情報 */
	private CharacterData myCharacter;

	/** 相手の情報 */
	private CharacterData oppCharacter;

	private Action[] normalActionAir;
	private Action[] highlightActionAir;
	private Action[] harmlessActionAir;
	
	private Action[] normalActionGround;
	private Action[] highlightActionGround;
	private Action[] harmlessActionGround;

	private Action highlightSpSkill;

	/** STAND_D_DF_FCの回避行動用フラグ */
	private boolean isFcFirst = true;

	/** 敵がSTAND_D_DF_FCを使ってくるかどうか */
	private boolean canFC = true;

	/** STAND_D_DF_FCの回避行動時間を計測する */
	private long firstFcTime;

	private ArrayList<MotionData> myMotion;

	private ArrayList<MotionData> oppMotion;

	
	private Node rootNode;

	private HighlightMCTS highlightMcts;
	private MCTS normalMcts;
	private MCTS harmlessMcts;
	
	Logger logger;
	
	//MiG Version
	float pdaProbability;
	boolean pdaOn;
	
	// FDG Version
//	private BalDataLoader balDataLoader;
	private BalFitnessDataLoader balFitnessDataLoader;
	private BalDataLoader balDataLoader;
	private TTSBridge tts;
	private UKIBridge uki;
	

	
	//TTS text generator	
	int gameState; // initialize game state, 0 start, 1 early game, 2 mid game, 3 near end game, 4 end game, 5 specific mode
	int AIState; // 0 highlight, 1 mcts, 2 harmless
	String opponentCurrentMove;	
	String opponentPreviousMove;
	private LinkedList<String> tempOpponentActionList;	
	boolean isSpeaking;//check if it's speaking
	float balancednessFitness;
	
	TTSSkillMap ttsSkillMap;
	
	String opponentActionPath;
	String opponentCurrentAction;
	String opponentPreviousAction;
	//health action
//	long startTime;
//	long currentTime;
//	long countStartTime;
//	long previousHealthActionTime;
//	String niceSkill;
	
	
	
	//avatar action
	private Map<String, Integer> avatarSkillMap;
	
	//UKI Map
	private Map<String, String> ukiSkillMap;
	private Map<String, Integer> realToUkiMap;	
	
	private void setTTSParameters() {
		if (gameState == 0) {
			tts.gain = 10.0f;
			tts.rate = 1.0f;
			tts.pitch = 2.0f;
			gameState = 1;
		} else if (gameState == 1) {
			if (pdaOn) {
				tts.gain = 1.0f + (pdaProbability - 0.5f) * 5.0f;
				tts.rate = 1.25f - (pdaProbability - 0.5f);	
				tts.pitch = 1.0f + (pdaProbability - 0.5f) * 3.0f;
			}
			else {
				tts.gain = 1.0f;
				tts.rate = 1.25f;				
			}

			if (opponentCurrentMove.contains("Default")) {				
				tts.pitch = -4.0f;				
			} else {
				tts.pitch = 0f;		
			}

		} else if (gameState == 2) {
			tts.gain = 1.0f;
			tts.rate = 1.25f;			
		} else if (gameState == 3) {
			tts.gain = 1.5f;
			tts.rate = 1.25f;			
		} else if (gameState == 4) {
			tts.gain = 2.0f;
			tts.rate = 1.0f;			
		} else if (gameState == 5) {
			tts.gain = 1.0f;
			tts.rate = 1.0f;
			tts.pitch = 2.0f;			
		} else {
			
		}
	}
	
	private void getOpponentCurrentMoveInformation() {
		//TODO
//		if (opponentPreviousMove != ttsSkillMap.getActionRealName(this.frameData.getCharacter(!playerNumber).getAction().name())) {
//			opponentPreviousMove = opponentCurrentMove;
			opponentCurrentMove = ttsSkillMap.getActionRealName(this.frameData.getCharacter(!playerNumber).getAction().name());
			if (isSpeaking) {
				if (!opponentCurrentMove.contains("Default")){		
					tempOpponentActionList.add(opponentCurrentMove);		
				}
			} else {
				if (tempOpponentActionList.size() != 0) {
					opponentCurrentMove = tempOpponentActionList.get(ttsSkillMap.getRandomNumber(tempOpponentActionList.size()));
					tempOpponentActionList.clear();
				}
			}			
		}


//	}
	
	// from PDA MIG version
	//	MotionRecorder motionRecorder;

	@Override
	public void close() {
		tts.speak(ttsSkillMap.generateEndCommentary(),true);
//		logger.outputLog();
	}

	@Override
	public void getInformation(FrameData frameData, boolean arg1) {
		this.frameData = frameData;
		this.commandCenter.setFrameData(this.frameData, playerNumber);
		this.myCharacter = this.frameData.getCharacter(playerNumber);
		this.oppCharacter = this.frameData.getCharacter(!playerNumber);
	}

	@Override
	public int initialize(GameData gameData, boolean playerNumber)  {
		
		this.playerNumber = playerNumber;

		this.key = new Key();
		this.frameData = new FrameData();
		this.commandCenter = new CommandCenter();

		this.myNormalActions = new LinkedList<Action>();
		this.myHighlightActions = new LinkedList<Action>();
		this.myHarmlessActions = new LinkedList<Action>();
		this.oppNormalActions = new LinkedList<Action>();
		this.oppHighlightActions = new LinkedList<Action>();

		this.simulator = gameData.getSimulator();
		this.myMotion = gameData.getMotionData(playerNumber);
		this.oppMotion = gameData.getMotionData(!playerNumber);
		this.opponentPreviousMove = "Default";
		this.balDataLoader = new BalDataLoader("uki/bal.txt");
		this.balFitnessDataLoader = new BalFitnessDataLoader("uki/fitness.txt");
		this.tempOpponentActionList = new LinkedList<String>();
//		logger = new Logger(playerNumber);
	

		// Init
		setPerformAction();
		gameState = 0;
		AIState = 0;
		isSpeaking = false;
		ttsSkillMap = new TTSSkillMap();
		
		tts = new TTSBridge();
		setTTSParameters();
		tts.speak(ttsSkillMap.generateBeginCommentary());
//		startTime = System.currentTimeMillis();
//		previousHealthActionTime = startTime;
		uki = new UKIBridge();
		ukiSkillMap = new HashMap<String, String>();
		realToUkiMap = new HashMap<String, Integer>();		
		initUkiSkillMap();
		initRealUkiSkillMap();

		
		
		
		//PDA init
		opponentActionPath = "C:\\FTG\\CMD1.txt";
		opponentCurrentAction = "STAND";
		opponentPreviousAction = "STAND";
		pdaProbability = 1.0f;
		pdaOn = true;
		balancednessFitness = 1.0f;
//		try {
//			if (uki.save("123456 > . !")) {
//				System.out.println("success");
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		return 0;
	}
	
	@Override
	public void processing() {

		if (canProcessing()) {
			getOpponentCurrentMoveInformation();
			
			
			//Commentary Generation
			setTTSParameters();
			//currentTime = System.currentTimeMillis();
			
			
			opponentCurrentAction = FzReader.readFile(opponentActionPath);
			opponentCurrentAction = opponentCurrentAction.replaceAll("\r\n", "");
			System.out.println(opponentCurrentAction);
			if (opponentCurrentAction != opponentPreviousAction) {
				//get balanceFitness of that action
				balancednessFitness = getBalancednessFitness(opponentCurrentAction);
				opponentPreviousAction = opponentCurrentAction;
			}
			
			if (pdaOn && balancednessFitness != -1.0f && balancednessFitness != 0.0f) {
				pdaProbability = balancednessFitness;
				System.out.println("pda" + pdaProbability);
			} else if (pdaProbability == -1.0f){
				
			} else {
				pdaProbability = 0.0f;
			}
			
			tts.speak(ttsSkillMap.generateNormalCommentary(opponentCurrentMove,balFitnessDataLoader.getMaxVarActionId()));
			
			// refresh recommendation
//			if (!((currentTime - startTime) < 20 * 1000)) {
//				if (currentTime - previousHealthActionTime > 10 * 1000) {
//					if (balDataLoader.getTotal() < .85) {
//						int i = 0;
//						while (ukiNameToReal(balFitnessDataLoader.getActionNameById(i)) != "Default") {
//							niceSkill = ukiNameToReal(balFitnessDataLoader.getActionNameById(i));							
//						}
//						countStartTime = currentTime;
//						tts.speak(ttsSkillMap.generateHealthQuestion(niceSkill));
//					} else {
//						tts.speak(ttsSkillMap.generateNormalCommentary(opponentCurrentMove));
//					}
//					
//					previousHealthActionTime = currentTime;
//				}
//				
//				if ((currentTime - countStartTime) < 3 * 1000) {
//					// the action is matched
//					boolean isMatched = opponentCurrentMove.contains(niceSkill);
//					if (isMatched) {
//						// positive
//						tts.speak(ttsSkillMap.generateHealthCommentary(niceSkill, true), true);				
//						// Reset counter
//						countStartTime = currentTime - 3000;
//					}
//				} else {
//					tts.speak(ttsSkillMap.generateHealthCommentary(niceSkill, false), true);
//				}
//
//				
//			} else {
//				tts.speak(ttsSkillMap.generateNormalCommentary(opponentCurrentMove));
//			}
			

			//System.out.println(isSpeaking);
	

			// フラグによって予測をするか選択
			if (FixParameter.PREDICT_FLAG) {
				if (oppMotion.get(oppCharacter.getAction().ordinal()).getFrameNumber() == oppCharacter
						.getRemainingFrame()) {
					Prediction.getInstance().countOppAction(this.frameData, oppCharacter, commandCenter);
				}
			}

			if (commandCenter.getSkillFlag()) {	
				
					key = commandCenter.getSkillKey();		

			} else {
				key.empty();
				commandCenter.skillCancel();
				aheadFrame(); // 遅れフレーム分進める
				// フラグによって回避行動をするかどうか選択
				if (FixParameter.AVOID_FLAG) {
					String enemyAction = this.frameData.getCharacter(!playerNumber).getAction().name();
					int enemyEnergy = this.frameData.getCharacter(!playerNumber).getEnergy();

					if (enemyAction.equals("STAND_D_DF_FC")) {
						canFC = true;
						isFcFirst = true;
					}

					if (enemyEnergy >= 150 && canFC) {
						if (isFcFirst) {
							firstFcTime = frameData.getRemainingTimeMilliseconds() / 1000;
							isFcFirst = false;
						}
						if (firstFcTime
								- frameData.getRemainingTimeMilliseconds() / 1000 >= FixParameter.AVOIDANCE_TIME) {
							canFC = false;
							isFcFirst = true;
						} else {
							commandCenter.commandCall("STAND_D_DB_BA");
							rootNode = null;
							return;
						}
					}
				}

				if (FixParameter.PREDICT_FLAG) {
					Prediction.getInstance().getInfomation(); // 回数順でソート
				}

				
				// FDG Version TODO
				// MCTSによる行動決定
				Action bestAction = Action.STAND_D_DB_BA;


				balFitnessDataLoader.updateData();
				float rightPunchBF = balFitnessDataLoader.getBalFitnessById(6);
				float leftPunchBF = balFitnessDataLoader.getBalFitnessById(7);
				
				
				Random rand = new Random();
				float probabilityNumber = rand.nextFloat();
				System.out.println("randomprob=" + probabilityNumber);
				//TODO
				if (probabilityNumber < pdaProbability) {
					AIState = 0;
				} else {
					AIState = 1;
				}
				
				
				if (rootNode == null) {
					// MCTSの下準備を行う
					if (AIState == 0) {
						normalMctsPrepare();
					} else if (AIState == 1) {
						//harmlessMctsPrepare();
					} else {
						
					}
				}

				
				if (AIState == 0) {
					bestAction = normalMcts.runMcts();					
				} else if (AIState == 1) {
					commandCenter.commandCall("DASH");
				} else {
					
				}

				
				
				if (ableAction(bestAction)) {
					commandCenter.commandCall(bestAction.name()); // MCTSで選択された行動を実行する
//					logger.updateLog(rootNode.games);
					if (FixParameter.DEBUG_MODE) {
						if (leftPunchBF < rightPunchBF) {
							// TODO: Change the expression above
							highlightMcts.printNode(rootNode);
						} else {
							normalMcts.printNode(rootNode);
						}
						
					}
					rootNode = null;
				}
			}
		} else {
			canFC = true;
			isFcFirst = true;
		}
}

	public boolean ableAction(Action action) {
		if (action == null)
			return false;
		if (myCharacter.isControl()) {
			return true;
		} else {
			return myCharacter.isHitConfirm() && checkFrame() && checkAction(action);
		}
	}

	public boolean checkFrame() {
		return (myMotion.get(myCharacter.getAction().ordinal()).getCancelAbleFrame() <= myMotion
				.get(myCharacter.getAction().ordinal()).getFrameNumber() - myCharacter.getRemainingFrame());
	}

	public boolean checkAction(Action act) {
		return (myMotion.get(myCharacter.getAction().ordinal()).getCancelAbleMotionLevel() >= myMotion
				.get(act.ordinal()).getMotionLevel());
	}

	@Override
	public void roundEnd(int x, int y, int frame) {
		gameState = 4;
		setTTSParameters();
		tts.speak("What a Play!");
	}

	/**
	 * MCTSの下準備
	 * 遅れフレーム分進ませたFrameDataの取得などを行う
	 */
	public void normalMctsPrepare() {
		setNormalMyAction();
		setNormalOppAction();

		rootNode = new Node(null);
		FrameData simFrameData = new FrameData(simulatorAheadFrameData);
		normalMcts = new MCTS(rootNode, simFrameData, simulator, myCharacter.getHp(), oppCharacter.getHp(),
				myNormalActions, oppNormalActions, playerNumber);

		normalMcts.createNode(rootNode);

	}
	
	public void highlightMctsPrepare() {
		setHighlightMyAction();
		setHighlightOppAction();

		rootNode = new Node(null);
		FrameData simFrameData = new FrameData(simulatorAheadFrameData);
		highlightMcts = new HighlightMCTS(rootNode, simFrameData, simulator, myCharacter.getHp(), oppCharacter.getHp(),
				myHighlightActions, oppHighlightActions, playerNumber, myMotion);

		highlightMcts.createNode(rootNode);

	}
	
	public void harmlessMctsPrepare() {
		setHarmlessMyAction();
		setNormalOppAction();

		rootNode = new Node(null);
		FrameData simFrameData = new FrameData(simulatorAheadFrameData);
		harmlessMcts = new MCTS(rootNode, simFrameData, simulator, myCharacter.getHp(), oppCharacter.getHp(),
				myHarmlessActions, oppNormalActions, playerNumber);

		harmlessMcts.createNode(rootNode);

	}
	
	/** 自身の可能な行動をセットする */
	public void setNormalMyAction() {
		myNormalActions.clear();

//		int energy = myCharacter.getEnergy();

		for (int i = 0; i < normalActionGround.length; i++) {
			if (Math.abs(myMotion.get(Action.valueOf(normalActionGround[i].name()).ordinal())
					.getAttackStartAddEnergy()) <= 0) {
				myNormalActions.add(normalActionGround[i]);
			}
		}
		
//		if (myCharacter.getState() == State.AIR) {
//			for (int i = 0; i < normalActionAir.length; i++) {
//				if (Math.abs(myMotion.get(Action.valueOf(normalActionAir[i].name()).ordinal())
//						.getAttackStartAddEnergy()) <= energy) {
//					myNormalActions.add(normalActionAir[i]);
//				}
//			}
//		} else {
//			for (int i = 0; i < normalActionGround.length; i++) {
//				if (Math.abs(myMotion.get(Action.valueOf(normalActionGround[i].name()).ordinal())
//						.getAttackStartAddEnergy()) <= 0) {
//					myNormalActions.add(normalActionGround[i]);
//				}
//			}
//		}

	}
	
	public void setHighlightMyAction() {
		myHighlightActions.clear();

		int energy = myCharacter.getEnergy();

		if (myCharacter.getState() == State.AIR) {
			for (int i = 0; i < highlightActionAir.length; i++) {
				if (Math.abs(myMotion.get(Action.valueOf(highlightActionAir[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					myHighlightActions.add(highlightActionAir[i]);
				}
			}
		} else {
			if (Math.abs(
				myMotion.get(Action.valueOf(highlightSpSkill.name()).ordinal()).getAttackStartAddEnergy()) <= energy) {
				myHighlightActions.add(highlightSpSkill);
			}

			for (int i = 0; i < highlightActionGround.length; i++) {
				if (Math.abs(myMotion.get(Action.valueOf(highlightActionGround[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					myHighlightActions.add(highlightActionGround[i]);
				}
			}
		}

	
	}

	public void setHarmlessMyAction() {
		myHarmlessActions.clear();

//		int energy = myCharacter.getEnergy();

		for (int i = 0; i < harmlessActionGround.length; i++) {
			if (Math.abs(myMotion.get(Action.valueOf(harmlessActionGround[i].name()).ordinal())
					.getAttackStartAddEnergy()) <= 0) {
				myHarmlessActions.add(normalActionGround[i]);
			}
		}
		
//		if (myCharacter.getState() == State.AIR) {
//			for (int i = 0; i < normalActionAir.length; i++) {
//				if (Math.abs(myMotion.get(Action.valueOf(normalActionAir[i].name()).ordinal())
//						.getAttackStartAddEnergy()) <= energy) {
//					myNormalActions.add(normalActionAir[i]);
//				}
//			}
//		} else {
//			for (int i = 0; i < normalActionGround.length; i++) {
//				if (Math.abs(myMotion.get(Action.valueOf(normalActionGround[i].name()).ordinal())
//						.getAttackStartAddEnergy()) <= 0) {
//					myNormalActions.add(normalActionGround[i]);
//				}
//			}
//		}

	}
	
	/** 相手の可能な行動をセットする */
	public void setNormalOppAction() {
		oppNormalActions.clear();

		int energy = oppCharacter.getEnergy();

		if (oppCharacter.getState() == State.AIR) {
			for (int i = 0; i < normalActionAir.length; i++) {
				if (Math.abs(oppMotion.get(Action.valueOf(normalActionAir[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					oppNormalActions.add(normalActionAir[i]);
				}
			}
		} else {
			for (int i = 0; i < normalActionGround.length; i++) {
				if (Math.abs(oppMotion.get(Action.valueOf(normalActionGround[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= 0) {
					oppNormalActions.add(normalActionGround[i]);
				}
			}
		}
	}
	
	public void setHighlightOppAction() {
		oppHighlightActions.clear();

		int energy = oppCharacter.getEnergy();

		if (oppCharacter.getState() == State.AIR) {
			for (int i = 0; i < highlightActionAir.length; i++) {
				if (Math.abs(oppMotion.get(Action.valueOf(highlightActionAir[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= energy) {
					oppHighlightActions.add(highlightActionAir[i]);
				}
			}
		} else {
			if (Math.abs(oppMotion.get(Action.valueOf(highlightSpSkill.name()).ordinal()).getAttackStartAddEnergy()) <= energy) {
				oppHighlightActions.add(highlightSpSkill);
			}

			for (int i = 0; i < highlightActionGround.length; i++) {
				if (Math.abs(oppMotion.get(Action.valueOf(highlightActionGround[i].name()).ordinal())
						.getAttackStartAddEnergy()) <= 0) {
					oppHighlightActions.add(highlightActionGround[i]);
				}
			}
		}
	}
	
	public void updateAvatar(String action) {

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\Xu_FDG\\UKI-R2_FDG\\UKI-R2_FDG\\P-Tracker2\\P-Tracker2\\PDA\\avatar\\ai_cmd.txt"));
			bw.write(action);
			bw.write(System.getProperty("line.separator"));
			bw.write(action);
			bw.close();
		} catch (IOException e) {
		}
	}
	
	private void initUkiSkillMap() {
//		ukiSkillMap.put("STAND_D_DB_BA", "Flying crop");
//		ukiSkillMap.put("BACK_STEP", "Back step");
//		ukiSkillMap.put("FORWARD_WALK", "Step forward");
//		ukiSkillMap.put("DASH", "Lean forward");
//		ukiSkillMap.put("STAND_GUARD", "Guard");
//		ukiSkillMap.put("CROUCH_GUARD", "Guard");
		ukiSkillMap.put("Throw", "Two Hand Punch");//THROW_A
		ukiSkillMap.put("Step Down + Two Hand Punch", "Great Throw");//THROW_B
		ukiSkillMap.put("Left Punch", "Punch");//STAND_A
		ukiSkillMap.put("Left Knee Strike", "Kick");//STAND_B
		ukiSkillMap.put("Crouch + Left Punch", "Low Punch");//CROUCH_A
		ukiSkillMap.put("Left kick", "Low Kick");//CROUCH_B
		ukiSkillMap.put("Right Punch", "Heavy Punch");//STAND_FA
		ukiSkillMap.put("Right Knee Strike", "Heavy Kick");//STAND_FB
		ukiSkillMap.put("Crouch + Right Punch", "Low Heavy Punch");//CROUCH_FA
		ukiSkillMap.put("Right Kick", "Low Heavy Kick");//CROUCH_FB
		ukiSkillMap.put("STAND_D_DF_FA", "Hadouken");//STAND_D_DF_FA
//		ukiSkillMap.put("STAND_D_DF_FB", "Super Hadouken");	
		ukiSkillMap.put("Left uppercut", "Uppercut");//STAND_F_D_DFA
		ukiSkillMap.put("Knifehand Strike", "Super Uppercut");//STAND_F_D_DFB
		ukiSkillMap.put("Right swing", "Slide Kick");//STAND_D_DB_BB
//		ukiSkillMap.put("Hadouken", "Ultimate Hadouken");//STAND_D_DF_FC
	}
	
	private void initRealUkiSkillMap() {
		realToUkiMap.put("STAND_A", 7);
		realToUkiMap.put("STAND_FA", 8);
		realToUkiMap.put("STAND_B",9);
		realToUkiMap.put("STAND_FB",10);
		realToUkiMap.put("CROUCH",11);
		realToUkiMap.put("CROUCH_A", 13);
		realToUkiMap.put("CROUCH_FA", 14);
		realToUkiMap.put("CROUCH_B",15);
		realToUkiMap.put("CROUCH_FB",16);
		realToUkiMap.put("THROW_A",17);
		realToUkiMap.put("THROW_B",18);
		realToUkiMap.put("STAND_D_DB_BB",19);
		realToUkiMap.put("STAND_F_D_DFA",20);
		realToUkiMap.put("STAND_F_D_DFB", 21);
		realToUkiMap.put("STAND_D_DF_FA",22);
		
	}
	
	
	
	public String ukiNameToReal(String skillCode) {
		return ukiSkillMap.getOrDefault(skillCode, "Default");
	}
	
	public float getBalancednessFitness(String currentSkill) {
		System.out.println(currentSkill);
		int skillUkiName = realToUkiMap.getOrDefault(currentSkill, 0);
		System.out.println(skillUkiName);
		if (skillUkiName == 0) {
			return -1.0f;
		} else {
			return balFitnessDataLoader.getBalFitnessById(skillUkiName);			
		}

	}
	
//	public String getMaxVarAction() {
//		float maxVarValue = -100.0f;
//		String maxVarActionName = "Default";
//		for (int count = 0; count < 26; count++) {
//			if (balFitnessDataLoader.getvarDataById(count) > maxVarValue) {
//				maxVarValue = balFitnessDataLoader.getvarDataById(count);
//				maxVarActionName = balFitnessDataLoader.getActionNameById(count);
//			}
//		}		
//		return maxVarActionName;
//	}

	/** 遅れフレーム分進める */
	private void aheadFrame() {
		simulatorAheadFrameData = simulator.simulate(this.frameData, playerNumber, null, null, 1);
		myCharacter = simulatorAheadFrameData.getCharacter(playerNumber);
		oppCharacter = simulatorAheadFrameData.getCharacter(!playerNumber);
	}

	/** アクションの配列の初期化 */
	private void setPerformAction() {
		highlightActionAir = new Action[] { Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DA, Action.AIR_DB,
				Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA, Action.AIR_D_DF_FB,
				Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA, Action.AIR_D_DB_BB };
		
		highlightActionGround = new Action[] { Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH,
				Action.JUMP, Action.STAND_GUARD, Action.CROUCH_GUARD, Action.THROW_A,
				Action.THROW_B, Action.STAND_A, Action.STAND_B, Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA,
				Action.STAND_FB, Action.CROUCH_FA, Action.CROUCH_FB, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB,
				Action.STAND_F_D_DFA, Action.STAND_F_D_DFB, Action.STAND_D_DB_BB };
		
		highlightSpSkill = Action.STAND_D_DF_FC;
		
		normalActionAir = new Action[] { Action.AIR_GUARD, Action.AIR_A, Action.AIR_B, Action.AIR_DA, Action.AIR_DB,
				Action.AIR_FA, Action.AIR_FB, Action.AIR_UA, Action.AIR_UB, Action.AIR_D_DF_FA, Action.AIR_D_DF_FB,
				Action.AIR_F_D_DFA, Action.AIR_F_D_DFB, Action.AIR_D_DB_BA, Action.AIR_D_DB_BB };
		
		normalActionGround = new Action[] { Action.STAND_D_DB_BA, Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH,
				Action.JUMP, Action.STAND_GUARD, Action.CROUCH_GUARD, Action.THROW_A,
				Action.THROW_B, Action.STAND_A, Action.STAND_B, Action.CROUCH_A, Action.CROUCH_B, Action.STAND_FA,
				Action.STAND_FB, Action.CROUCH_FA, Action.CROUCH_FB, Action.STAND_D_DF_FA, Action.STAND_D_DF_FB,
				Action.STAND_F_D_DFA, Action.STAND_F_D_DFB, Action.STAND_D_DB_BB };

//		normalActionAir = new Action[] {};
//		normalActionGround = new Action[] {  Action.BACK_STEP, Action.FORWARD_WALK, Action.DASH,
//				Action.JUMP,Action.STAND_GUARD,
//				 Action.STAND_A, Action.STAND_B, Action.CROUCH_B, Action.STAND_FA,
//				Action.STAND_FB, Action.CROUCH_FA, Action.CROUCH_FB, Action.STAND_D_DF_FA
//				};
		
		harmlessActionAir = new Action[] {};
		
		harmlessActionGround = new Action[] {Action.FORWARD_WALK, Action.DASH
				};
	}

	/**
	 * AIが行動できるかどうかを判別する
	 *
	 * @return AIが行動できるかどうか
	 */
	public boolean canProcessing() {
		return !frameData.getEmptyFlag() && frameData.getRemainingTimeMilliseconds() > 0;
	}

	@Override
	public Key input() {
		// TODO 自動生成されたメソッド・スタブ

		return key;
	}
}



