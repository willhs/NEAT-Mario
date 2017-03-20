# NEAT-Mario: for training NEAT agents on Mario Brothers problems.

This repo contains a Mario Brothers game benchmark and code for game-playing agents which utilise the NEAT algorithm (implemented in a dependent repository linked below). This repo contains the source code used for the AI'17 paper on phased searching for transfer learning authored by Will Hardwick-Smith, Gang Chen, Yiming Peng and Yi Mei. If the paper is published I will link it here.

NEAT is a genetic algorithm which evolves a population of artificial neural networks toward a goal; in this case the goal is to play Mario Brothers well. The *fitness function* that determines an individual's fitness is defined in the Fitness

The Mario Brothers benchmark included in this repo is called Infinite Mario Brothers and was originally used for the Mario AI Championship which ran from 2009-2012 ([original site](http://julian.togelius.com/mariocompetition2009/)). In this repo I used [kefik's fork](https://github.com/kefik/MarioAI) 

## Dependencies
1. [Will's fork of the encog-java-core library](https://github.com/willhs/encog-java-core), *on the _phased-search_ branch*. 

## How to use:
There are several entry points in the repo to begin training with a NEAT agent. The entry point used for the our AI'17 paper is will.mario.experiment.TransferExperiments.java. This class also contains each of the experiments that were performed for the paper.
The arguments for this entry point are as follows: `[level [, outputPath [, taskAGens [, taskBGens [, phaseLength [, baseEvolver [, transferEvolver [, complexificationFirst [, runType [, seed]]]]]]]]]]` where:
1. `level`: the preset experiment to run. Can be one of: `kills`, `speed-enemies`, `fly`, `dist-kills`, `speed-coins`.
2. `outputPath`: the directory to output results to.
3. `taskAGens`: # of generations to run on task A (the source task).
4. `taskBGens`: # of generations to run on task B (the target task).
5. `phaseLength`: the phase length if static phased search is used as the search strategy.
6. `baseEvolver`: which evolution strategy to use to train on the source task (as an integer) where:
    1. `0`: regular NEAT.
    2. `1`: blended search NEAT (both additive and subtractive mutations).
    3. `2`: static phased search NEAT (a simple phased search strategy).
    4. `3`: Green's phased search NEAT (Green's adaptive phased search strategy).
    5. `4`: static phased search NEAT with power law mutations (presented in our paper).
7. `transferEvolver`: which evolution strategy to use to train on the target task (uses same integer values as above)>
8. `complexificationFirst`: boolean value for whether phased search strategies should start in the complexification phase rather than the simplification phase (`true` recommended) (only applies when phased search is used).
9. `runType`: which tasks should be trained. Can either be `BOTH` to run the source task then the target task (normal transfer learning experiment), `A` for only the source task, or `B` for only the target task.
10. `seed`: integer used to determine the sequence of random numbers used in the experiment.

After the experiment finishes running, the results from each generation will be written to `outputPath` ("saved/results.csv" by default). These results will also be written to the console as the experiment runs.
