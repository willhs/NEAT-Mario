# NEAT-Mario: for training NEAT agents on Mario Brothers problems.

This repo contains a Mario Brothers game benchmark and code for game-playing agents which utilise the NEAT algorithm (implemented in a dependent repository linked below). This repo contains the source code used for the AI'17 paper on phased searching for transfer learning authored by Will Hardwick-Smith, Gang Chen, Yiming Peng and Yi Mei. If the paper is published I will link it here.

NEAT is a genetic algorithm which evolves a population of artificial neural networks toward a goal; in this case the goal is to play Mario Brothers well. The *fitness function* that determines an individual's fitness is defined in the Fitness

The Mario Brothers benchmark included in this repo is called Infinite Mario Brothers and was originally used for the Mario AI Championship which ran from 2009-2012 ([original site](http://julian.togelius.com/mariocompetition2009/)). In this repo I used [kefik's fork](https://github.com/kefik/MarioAI) 

## Requirements
1. [Will's fork of the encog-java-core library](https://github.com/willhs/encog-java-core), on the phased-search branch. 
2. [MM-NEAT repo](https://github.com/schrum2/MM-NEATv2). I may remove this dependency soon though because it is only required by code that is no longer important. 

## How to use:
There are several entry points in the repo to begin training with a NEAT agent. The entry point used for the our AI'17 paper is will.mario.experiment.TransferExperiments.java. This class also contains each of the experiments that were performed for the paper.
