package ice_tts;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SkillMap {
	
	private Map<String, String> skillMap;
	
	public SkillMap() {
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
	
	
	/**
	 * natural language processing using real action name, just prototype for it
	 * TODO
	 * @return complete sentence
	 */
	public String generateSentence(String actionRealName) {
		// 20 template for each element
		if (actionRealName == "Default") {
			return "Skip.";// this should be normal chat, add later
		} else {
		Random random = new Random();
			String forwardSentence[] = {"She", "I have never seen that before", "So threatening she", "Garnet", "For an opportunity she", "she continues to", "Oh My God and just like that showing you exactly what can happen with", "Nice thing for", "That's common that", "That's very crucial", "What a fast", "She knew is that to", "She could turn on his head if that", "She just know the", "It's always not enough to", "She used", "You would think even given", "She pressing it out pump", "She's so powerful releasing", "Hit him by"};
			String backwardSentence[] = {", that exactly a good move", ", really wants to get in there", "for real baby", "actually gave him always fuckin here", "be a trigger right now already for nice hit", ", what a fast under funky invasiveness he read the board on the box", ", wow that happen real quickly", "punish that point", "really a good one", ", that's really important to go", "that Zen should be very careful", "all of us should applause for him", ", answers back immediately reflect around", ", she gotta be punished", "always not going to be a son", "it could go to different", "Oh that'll be a great deal", ", some damage here", "for a bit of comeback", "that to tell him should quit this game"};		
			return forwardSentence[random.nextInt(20)] + " " + actionRealName + " " + backwardSentence[random.nextInt(20)] + ".";			
		}
	}
	
	public String generateSentenceByCode(String skillCode) {
		return generateSentence(getActionRealName(skillCode));
	}
}