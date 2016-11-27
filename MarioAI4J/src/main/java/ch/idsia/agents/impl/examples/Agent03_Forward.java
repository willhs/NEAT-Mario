package ch.idsia.agents.impl.examples;

import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.controllers.MarioHijackAIBase;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.benchmark.mario.engine.input.MarioKey;
import ch.idsia.benchmark.mario.options.FastOpts;
import ch.idsia.agents.IAgent;
import ch.idsia.benchmark.mario.engine.input.MarioInput;

/**
 * An agent that sprints forward and jumps if it detects an obstacle ahead.
 * 
 * @author Jakub 'Jimmy' Gemrot, gemrot@gamedev.cuni.cz
 */
public class Agent03_Forward extends MarioHijackAIBase implements IAgent {

	@Override
	public void reset(AgentOptions options) {
		super.reset(options);		
	}

	private boolean enemyAhead() {
		return
				   entities.danger(1, 0) || entities.danger(1, -1)
				|| entities.danger(2, 0) || entities.danger(2, -1)
				|| entities.danger(3, 0) || entities.danger(2, -1);
	}
	
	private boolean brickAhead() {
		return
				   tiles.brick(1, 0) || tiles.brick(1, -1)
				|| tiles.brick(2, 0) || tiles.brick(2, -1)
				|| tiles.brick(3, 0) || tiles.brick(3, -1);
	}

	public MarioInput actionSelectionAI() {
		// ALWAYS RUN RIGHT
		action.press(MarioKey.RIGHT);
		
		// ALWAYS SPEED RUN
		action.press(MarioKey.SPEED);
		
		// ENEMY || BRICK AHEAD => JUMP
		// WARNING: do not press JUMP if UNABLE TO JUMP!
		action.set(MarioKey.JUMP, (enemyAhead() || brickAhead()) && mario.mayJump);
		
		// If in the air => keep JUMPing
		if (!mario.onGround) {
			action.press(MarioKey.JUMP);
		}

		//System.out.println();

		return action;
	}
	
	public static void main(String[] args) {
		String options = FastOpts.VIS_ON_2X + FastOpts.LEVEL_02_JUMPING /* + FastOpts.L_ENEMY(Enemy.GOOMBA) */;
		
		MarioSimulator simulator = new MarioSimulator(options);
		
		IAgent agent = new Agent03_Forward();

		simulator.run(agent);

		System.exit(0);
	}
}