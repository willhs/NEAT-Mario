package ch.idsia.agents.controllers;

import java.awt.Graphics;

import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.environments.IEnvironment;
import ch.idsia.benchmark.mario.engine.VisualizationComponent;

public interface IMarioDebugDraw {

	public void debugDraw(VisualizationComponent vis, LevelScene level, IEnvironment env, Graphics g);
	
}
