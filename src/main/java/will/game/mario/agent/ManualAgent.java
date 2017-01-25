package will.game.mario.agent;

import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.IAgent;
import ch.idsia.agents.controllers.MarioHijackAIBase;
import ch.idsia.benchmark.mario.MarioSimulator;
import ch.idsia.benchmark.mario.engine.generalization.Enemy;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.options.FastOpts;
import ch.idsia.benchmark.mario.options.MarioOptions;
import ch.idsia.tools.EvaluationInfo;

import static will.game.mario.fitness.AbstractMarioFitnessFunction.*;


/**
 * Agent that sprints forward, jumps and shoots.
 * 
 * @author Jakub 'Jimmy' Gemrot, gemrot@gamedev.cuni.cz
 */
public class ManualAgent extends MarioHijackAIBase implements IAgent {

	private boolean shooting = false;
	
	@Override
	public void reset(AgentOptions options) {
		super.reset(options);
	}

	public MarioInput actionSelectionAI() {

//		int fitness = info.distancePassedCells;
//				- (info.timeSpent/10)
//				+ info.killsTotal * 5;

		int fitness = info.levelLength - info.distancePassedCells;

		System.out.println(fitness);

		return action;
	}

	public static void main(String[] args) {
		// IMPLEMENTS END-LESS RUNS
		while (true) {
			String options = DEFAULT_SIM_OPTIONS
					.replace(FastOpts.VIS_OFF, FastOpts.VIS_ON_2X)
					.replace(LEVEL, FastOpts.LEVEL_06_GOOMBA)
                    .replace(LEVEL_LENGTH, FastOpts.L_LENGTH_512)
                    .replace(DIFFICULTY, FastOpts.L_DIFFICULTY(2))
					.replace(FastOpts.L_ENEMY(Enemy.GOOMBA), "")
					+ FastOpts.L_COINS_ON
//					+ FastOpts.VIS_FIELD(SimulatorOptions.ReceptiveFieldMode.GRID_ENTITIES)
//					+ FastOpts.S_MARIO_LARGE
                    + FastOpts.S_MARIO_INVULNERABLE
					+ FastOpts.L_RANDOM_SEED(0)
//                    + " " + MarioOptions.IntOption.LEVEL_MARIO_INITIAL_POSITION_X.getParam() + " " + (512*16-20)
					;

			MarioSimulator simulator = new MarioSimulator(options);
			
			IAgent agent = new ManualAgent();
			
			EvaluationInfo info = simulator.run(agent);

			switch (info.marioStatus) {
			case Mario.STATUS_RUNNING:
				if (info.timeLeft <= 0) {
					System.out.println("LEVEL TIMED OUT!");
				} else {
					throw new RuntimeException("Invalid state...");
				}
				break;
			case Mario.STATUS_WIN:
				System.out.println("VICTORY");
				break;
			case Mario.STATUS_DEAD:
				System.out.println("MARIO KILLED");
				break;
			}
		}
		
		//System.exit(0);
	}
}