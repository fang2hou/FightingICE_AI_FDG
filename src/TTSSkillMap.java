
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TTSSkillMap {
	
	private Map<String, String> skillMap;
	
	//  extracted Commentary
	
	String beginCommentary[] = {
		"Alright, hey everybody welcome to match!",
		"Alright, hey welcome everybody!",
		"Welcome! Let's see the match between the two!",
		"It jsut starting up so we see Garnet versus Zen!",
		"The two already up the game with a great rushing up each other!"
	};
	
	String endCommentary[] = {
		"What a triumph!",
		"What a game!"
	};
	
	String positiveChat[] = {
		
	};
	
	String negativeChat[] = {
		
	};

// only describe player 1 now, Garnet vs Zen
	String actionForwardPositiveCommentary[] = {
			"She",
			"I have never seen that before",
			"So threatening she",
			"Garnet",
			"For an opportunity she",
			"she continues to",
			"Oh My God and just like that showing you exactly what can happen with",
			"Nice thing for",
			"That's common that",
			"That's very crucial",
			"What a fast",
			"She knew is that to",
			"She could turn on his head if that",
			"She just know the",
			"It's always not enough to",
			"She used",
			"You would think even given",
			"She pressing it out pump",
			"She's so powerful releasing",
			"Hit him by"
			};
	
	String actionForwardNegativeCommentary[] = {
	};
				
	String actionBackwardPositiveCommentary[] = {
			", that exactly a good move",
			", really wants to get in there",
			"for real baby",
			"actually gave him always fuckin here",
			"be a trigger right now already for nice hit",
			", what a fast under funky invasiveness he read the board on the box",
			", wow that happen real quickly",
			"punish that point",
			"really a good one",
			", that's really important to go",
			"that Zen should be very careful",
			"all of us should applause for him",
			", answers back immediately reflect around",
			", she gotta be punished",
			"always not going to be a son",
			"it could go to different",
			"Oh that'll be a great deal",
			", some damage here",
			"for a bit of comeback",
			"that to tell him should quit this game"
			};
	
	String actionBackwardNegativeCommentary[] = {
	};
	

	
	public TTSSkillMap() {
		skillMap = new HashMap<String, String>();
		
		skillMap.put("STAND_D_DB_BA", "Flying crop");
//		skillMap.put("BACK_STEP", "Back step");
//		skillMap.put("FORWARD_WALK", "Step forward");
		skillMap.put("DASH", "Lean forward");
//		skillMap.put("STAND_GUARD", "Guard");
//		skillMap.put("CROUCH_GUARD", "Guard");
		skillMap.put("THROW_A", "Throw");
		skillMap.put("THROW_B", "Great Throw");
		skillMap.put("STAND_A", "Punch");
		skillMap.put("STAND_B", "Kick");
		skillMap.put("CROUCH_A", "Low Punch");
		skillMap.put("CROUCH_B", "Low Kick");
		skillMap.put("STAND_FA", "Heavy Punch");
		skillMap.put("STAND_FB", "Heavy Kick");
		skillMap.put("CROUCH_A", "Low Heavy Punch");
		skillMap.put("CROUCH_B", "Low Heavy Kick");
		skillMap.put("STAND_D_DF_FA", "Hadouken");
		skillMap.put("STAND_D_DF_FB", "Super Hadouken");	
		skillMap.put("STAND_F_D_DFA", "Uppercut");
		skillMap.put("STAND_F_D_DFB", "Super Uppercut");
		skillMap.put("STAND_D_DB_BB", "Slide Kick");
		skillMap.put("STAND_D_DF_FC", "Ultimate Hadouken");	
	}
	
	/**
	 * transfer action code into real action name in natural language
	 */
	public String getActionRealName(String skillCode) {
		return skillMap.getOrDefault(skillCode, "Default");
	}
	
	public String generateCommentaryByCode(String skillCode) {
		return generateCommentary(getActionRealName(skillCode));
	}	
	/**
	 * natural language processing using real action name, just prototype for it
	 * TODO
	 * @return complete Commentary
	 */
	
	public String generateCommentary(String actionRealName) {
		Random random = new Random();
		if (actionRealName == "Default") {
			return "Skip.";// this should be normal chat, add later
		} else {
			return actionForwardPositiveCommentary[random.nextInt(actionForwardPositiveCommentary.length)] + " " + actionRealName + " " + actionBackwardPositiveCommentary[random.nextInt(actionBackwardPositiveCommentary.length)] + ".";			
		}

	}
	
	public String generateBeginCommentary(){
		Random random = new Random();
		return beginCommentary[random.nextInt(beginCommentary.length)];
	}
	
	public String generateEndCommentary(){
		Random random = new Random();
		return endCommentary[random.nextInt(endCommentary.length)];
	}
	
	
}