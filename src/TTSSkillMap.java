
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
		"It just starting up so we see Garnet versus Zen!",
		"The two already up the game with a great rushing up each other!",
	};
	
	String endCommentary[] = {
		"What a triumph!",
		"What a game!"
	};
	
	String chat[] = {
		"She going to be dancing a lot",
		"They are try to predict each other",
		"So attention these two players",
		"They try to play it slow in this match",
		"These two are too patient on the ground",
		"Haven't really seen that much comes"
	};
	
	String forwardActionInstruction[] = {
		"You could",
		"It's possible to",
		"It's interesting"
	};
	
	String actionInstruction[] = {
			"step back your right foot for guarding",
			"step forward your right foot for walking forward",
			"lean backward to move backward",
			"lean forward for dashing",
			"jump to make the character jump",
			"left punch to execute light punch",
			"right punch to execute heavy punch",
			"raise up the left knee to execute light kick",
			"raise up the right knee to execute heavy kick",
			"left Kick for executing light kick while crouching",
			"right kick to execute heavy kick while crouching. This skill can take the opponent down"
			
			
			
			
//			23	Right Step Back for guarding	Step back your right foot for guarding
//			22	Right Step Forward for walking forward	Step forward your right foot for walking forward
//			21	Lean Backward Left punch to execute weak punchfor moving backward	Lean backward to move backward
//			20	Lean Forward for dashing	Lean forward for dashing
//			19	Jump	Jump to make the character jump
//			18	Right Step Back and Jump for jumping backward	Step back the right foot and jump to jump backward
//			17	Right Step Front and Jump for jumping forward	Step front the right foot and jump to jump forward
//			16	Left Punch for weak punching	
//			15	Right Punch for heavy punching	Right punch to execute heavy punch
//			14	Left Knee Strike for weak kicking	Raise up the left knee to execute weak kick
//			13	Right Knee Strike for heavy kicking	Raise up the right knee to execute heavy kick
//			12	Crouch	Crouch to make the character crouch
//			11	Right Step Back and Crouch for guarding while crouching	Step back the right foot and crouch to guard while crouching
//			10	Crouch and Left Punch for punching while crouching	Crouch and left punch to execute weak punch while crouching
//			9	Crouch and Right Punch for throwing uppercut while crouching	Crouch and right punch to execute weak uppercut while crouching
//			8	Left Kick for executing weak kick while crouching	Left kick to execute weak kick while crouching
//			7	Right Kick for executing heavy kick while crouching	Right kick to execute heavy kick while crouching. This skill can take the opponent down.
//			6	Two-handed Punch for throwing the opponent.	Two-handed punch while the opponent is close to throw the opponent.
//			5	Right Step Back and Two-handed Punch for throwing the opponent heavily.	Step back the right foot and two-handed punch while the opponent is close to heavily throw the opponent
//			4	Right Swing for executing sliding attack.	Right swing from back to front to execute sliding attack. This skill can take the opponent down.
//			3	Left Uppercut for throwing uppercut	Left uppercut to execute heavy uppercut. This skill can take the opponent down.
//			2	Right Knifehand Strike for executing forward flying attack.	Right-handed knifehand strike (karate chop) to execute forward flying attack.
//			1	Hadouken for executing projectile attack.	Do Hadouken on your right side to execute projectile attack.
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
			"Hit him by",
			"Again that "
			};
	
	String actionForwardNegativeCommentary[] = {
	};
				
	String actionBackwardPositiveCommentary[] = {
			"excellent for that",
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
			"that to tell him should quit this game",
			"always has to respect it",
			"good flow",
			"counted"
			};
	
	String actionBackwardNegativeCommentary[] = {
	};
	
	// health PDA 
	
	String healthForwardQuestion[] = {
		"There's a button there,",
		"It's good to trying to employ",
		"It seems a possible to"
		
	};
	
	String healthBackwardQuestion[] = {
		", that you can rely on being pretty safe to hit",
		"to get him punished",
		"to go for knock down"
	};
	
	String healthPositiveCommentary[] = {
		"Yeah, She successfully punish him!",
		"Oh I can't believe it, such a pretty move!",
		"Wow, Optimal!",
		"Hoo, being a real the timing!",
		"She put into the hands with a fantastic played up!",
		"Oh, That works out!"
		
	};
	
	String healthNegativeCommentary[] = {
		"She's kind of infiltrating that dangerous living space",
		"She's like in that range where she sucks",	
		"Hoo, being a real the bad timing",
		"She took converse and there where possible with these to knock down",
		"The moves didn't come"
	};
	
	public TTSSkillMap() {
		skillMap = new HashMap<String, String>();
		
		skillMap.put("STAND_D_DB_BA", "Flying crop");
//		skillMap.put("BACK_STEP", "Back step");
//		skillMap.put("FORWARD_WALK", "Step forward");
//		skillMap.put("DASH", "Lean forward");
		skillMap.put("STAND_GUARD", "Guard");
		skillMap.put("CROUCH_GUARD", "Guard");
		skillMap.put("THROW_A", "Throw");
		skillMap.put("THROW_B", "Great Throw");
		skillMap.put("STAND_A", "Punch");
		skillMap.put("STAND_B", "Kick");
		skillMap.put("CROUCH_A", "Low Punch");
		skillMap.put("CROUCH_B", "Low Kick");
		skillMap.put("STAND_FA", "Heavy Punch");
		skillMap.put("STAND_FB", "Heavy Kick");
		skillMap.put("CROUCH_FA", "Low Heavy Punch");
		skillMap.put("CROUCH_FB", "Low Heavy Kick");
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
	 * @return complete Commentary
	 */
	
	public String generateNormalCommentary(String actionRealName) {
		if (actionRealName == "Default") {
			if (getRandomNumber(100) < 30) {
				return chat[getRandomNumber(chat.length)];				
			} else {
				return forwardActionInstruction[getRandomNumber(forwardActionInstruction.length)] + " " + actionInstruction[getRandomNumber(actionInstruction.length)];
			}

		} else {
			return actionForwardPositiveCommentary[getRandomNumber(actionForwardPositiveCommentary.length)] + " " + actionRealName + " " + actionBackwardPositiveCommentary[getRandomNumber(actionBackwardPositiveCommentary.length)] + ".";			
		}

	}
	
	public String generateHealthQuestion(String recommendedActionRealName) {
		return healthForwardQuestion[getRandomNumber(healthForwardQuestion.length)] + " " + recommendedActionRealName + " " + healthBackwardQuestion[getRandomNumber(healthBackwardQuestion.length)];
	}
	//TODO
	public String generateActionInstruction(String recommendedActionRealName) {
		return actionInstruction[getRandomNumber(actionInstruction.length)];
	}
	
	public String generateHealthCommentary(String recommendedActionRealName, boolean judgement) {
		if (judgement) {	
			return healthPositiveCommentary[getRandomNumber(healthPositiveCommentary.length)];			
		} else {
			return healthNegativeCommentary[getRandomNumber(healthNegativeCommentary.length)];				
		}
	}
	
	public String generateBeginCommentary(){
		return beginCommentary[getRandomNumber(beginCommentary.length)];
	}
	
	public String generateEndCommentary(){
		return endCommentary[getRandomNumber(endCommentary.length)];
	}
	
	
	/**
	 * random number generator
	 * 
	 * @param range
	 * @return random number within range
	 */
	public int getRandomNumber(int range) {
		Random random = new Random();
		return random.nextInt(range);
	}
}