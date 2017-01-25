/*
 * Copyright (c) 2009-2010, Sergey Karakovskiy and Julian Togelius
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Mario AI nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package will.game.mario.agent;

import ch.idsia.agents.AgentOptions;
import ch.idsia.agents.controllers.MarioAgentBase;
import ch.idsia.benchmark.mario.engine.input.MarioInput;
import ch.idsia.benchmark.mario.environments.IEnvironment;
import ch.idsia.tools.EvaluationInfo;

/**
 * Abstract class that serves as a basis for implementing new Mario-AI agents.
 *
 * Based on MarioAIBase class
 *
 * @author Will Hardwick-Smith
 */
public abstract class MarioAIBase2 extends MarioAgentBase {

	protected IEnvironment environment;
	protected MarioInput lastInput = new MarioInput();
	protected int highestFitness;

	// fields to help determine if mario has moved much
	private int lastPos = -1;
	private int framesInSamePos = 0;
	private int STAYS_STILL_THRESHOLD = 48; // 2 seconds

	public MarioAIBase2() {
		super("MarioAIBase");
		name = getClass().getSimpleName();
	}

	public MarioAIBase2(String agentName) {
		super(agentName);
	}	
	
	@Override
	public void reset(AgentOptions options) {
		super.reset(options);
		highestFitness = 0;
        lastPos = -1;
		framesInSamePos = 0;
		lastInput = new MarioInput();
	}

	@Override
	public void observe(IEnvironment environment) {
		this.environment = environment;

		int fitness = fitness(environment.getEvaluationInfo());

		if (fitness > highestFitness) {
			highestFitness = fitness;
		}

		// if this agent shows no promise, acknowledge that it sucks
		if (doesSuck()) {
			sucks = true;
		}
	}

	protected int fitness(EvaluationInfo info) {
/*        return info.distancePassedCells
				- (info.timeSpent / 10)
				+ (info.killsTotal * 5);*/
		return info.distancePassedCells;
	}

	protected boolean doesSuck() {
		// determine whether mario has moved significantly
		if (environment.getEvaluationInfo().distancePassedPhys == lastPos) {
			framesInSamePos++;
			if (framesInSamePos >= STAYS_STILL_THRESHOLD) {
				return true;
			}
		} else {
			framesInSamePos = 0;
		}

		lastPos = environment.getEvaluationInfo().distancePassedPhys;

		return false;
	}

	public float getFitness() {
		return highestFitness;
	}

}
