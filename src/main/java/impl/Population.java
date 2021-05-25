package main.java.impl;

import engine.Engine;
import engine.exceptions.VisualizationNotFoundException;

import main.Exceptions.EdgesNotFoundException;
import main.Exceptions.IncompleteNodesException;
import main.Exceptions.XYNotFoundException;

import java.util.*;

public class Population {
    private ArrayList<DNA_Node> populationNodeList;
    private ArrayList<DNA_Edge> populationEdgeList;
    private int elitismoSize;
    private boolean acabouNodeXY;
    private boolean acabouNodeType;
    private boolean acabouEdgeNodes;
    private boolean acabouEdgeWeights;
    private double nodeMutationFreq;
    private int populationSize;
    private DNA_Node bestXY;
    private DNA_Node completeNodeDNA;
    private DNA_Edge bestNodeEdges;
    private DNA_Edge completeEdges;
    private Engine eng;
    private double repFitness;
    private int repCounter;
    private int numMaxRep;

    public Population(Engine eng, int populationSize, int elitismoSize, double nodeMutationFreq, int numMaxRep) {
        this.populationNodeList = new ArrayList<>();
        this.populationEdgeList = new ArrayList<>();
        this.eng = eng;
        this.elitismoSize = elitismoSize;
        this.populationSize = populationSize;
        this.acabouNodeType = false;
        this.acabouNodeXY = false;
        this.acabouEdgeNodes = false;
        this.bestXY = null;
        this.completeNodeDNA = null;
        this.repFitness = 0;
        this.repCounter = 0;
        this.nodeMutationFreq = nodeMutationFreq;
        this.numMaxRep = numMaxRep;
    }

    public void setAcabouNodeXY(boolean acabouNode) {
        this.acabouNodeXY = acabouNode;
    }

    public boolean isAcabouNodeXY() {
        return acabouNodeXY;
    }

    public boolean isAcabouNodeType() {
        return acabouNodeType;
    }

    public void setAcabouNodeType(boolean acabouNodeType) {
        this.acabouNodeType = acabouNodeType;
    }

    public DNA_Node getBestXY() {
        return bestXY;
    }

    public void setBestXY(DNA_Node bestDNA) {
        this.bestXY = bestDNA;
    }

    public DNA_Node getcompleteNodeDNA() {
        return completeNodeDNA;
    }

    public void setcompleteNodeDNA(DNA_Node completeNodeDNA) {
        this.completeNodeDNA = completeNodeDNA;
    }

    public int getPopulationSize() {
        return populationSize;
    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public boolean isAcabouEdgeNodes() {
        return acabouEdgeNodes;
    }

    public void setAcabouEdgeNodes(boolean acabouEdgeNodes) {
        this.acabouEdgeNodes = acabouEdgeNodes;
    }

    public DNA_Edge getBestNodeEdges() {
        return bestNodeEdges;
    }

    public void setBestNodeEdges(DNA_Edge bestNodeEdges) {
        this.bestNodeEdges = bestNodeEdges;
    }

    public DNA_Edge getCompleteEdges() {
        return completeEdges;
    }

    public void setCompleteEdges(DNA_Edge completeEdges) {
        this.completeEdges = completeEdges;
    }

    public boolean isAcabouEdgeWeights() {
        return acabouEdgeWeights;
    }

    public void setAcabouEdgeWeights(boolean acabouEdgeWeights) {
        this.acabouEdgeWeights = acabouEdgeWeights;
    }

    public double getRepFitness() {
        return repFitness;
    }

    public void setRepFitness(double repFitness) {
        this.repFitness = repFitness;
    }

    public int getRepCounter() {
        return repCounter;
    }

    public void setRepCounter(int repCounter) {
        this.repCounter = repCounter;
    }

    public int getNumMaxRep() {
        return numMaxRep;
    }

    public void calcNodeFitness() {
        Iterator iterator = populationNodeList.iterator();
        while (iterator.hasNext()) {
            DNA_Node dna = (DNA_Node) iterator.next();
            dna.calcNodeXYFitness();
            //verificar se chegou ao fim
            if (!isAcabouNodeXY()) {
                dna.calcNodeXYFitness();
                if(getRepCounter() == getNumMaxRep()){
                //if (dna.getFitness() == 1 && getBestXY() == null) {
                    endNodeSearch(dna);
                }
            } //calcular fitness do tipo
        }
    }

    public void calcTypeFitness() throws XYNotFoundException {
        if(!isAcabouNodeXY())
            throw new XYNotFoundException("O X e Y dos nodes têm que ser encontrados antes do tipo");

        Collections.sort(populationNodeList);
        Iterator iterator = populationNodeList.iterator();
        while (iterator.hasNext()) {
            DNA_Node dna = (DNA_Node) iterator.next();
            dna.calcNodeTypeFitness();
            if(getRepCounter() == getNumMaxRep()){
            //if(dna.getFitness() == 1 && getcompleteNodeDNA() == null) {
                setAcabouNodeType(true);
                setcompleteNodeDNA(populationNodeList.get(0));
            }
        }

    }

    private void endNodeSearch(DNA_Node dna){
        setAcabouNodeXY(true);
        dna.removeUselessXYNodes();
        setBestXY(new DNA_Node(eng,dna.cloneList()));
    }

    public void prepararListaPTipo()throws XYNotFoundException {
        if(!isAcabouNodeXY())
            throw new XYNotFoundException("O X e Y dos nodes têm que ser encontrados antes do tipo");

        for (int i = 0; i < this.populationNodeList.size(); i++) {
            DNA_Node novoDNA = new DNA_Node(eng, getBestXY().cloneList());
            populationNodeList.set(i, novoDNA);
        }
    }

    private ArrayList<DNA_Node> elitismoNodeList() {
        ArrayList<DNA_Node> best = new ArrayList<>();
        Collections.sort(populationNodeList);
        for (int i = 0; i < elitismoSize; i++) {
            DNA_Node novoDna = new DNA_Node(eng, populationNodeList.get(i).cloneList());
            best.add(novoDna);
        }
        fitnessRepCounter(populationNodeList.get(0).getFitness());
        return best;
    }

    private ArrayList<DNA_Edge> elitismoEdgeList() {
        ArrayList<DNA_Edge> best = new ArrayList<>();
        Collections.sort(populationEdgeList);
        for (int i = 0; i < elitismoSize; i++) {
            DNA_Edge novoDna = new DNA_Edge(getcompleteNodeDNA().cloneList(),populationEdgeList.get(i).cloneList(),eng);
            best.add(novoDna);
        }
        fitnessRepCounter(populationEdgeList.get(0).getFitness());
        return best;
    }

    private int randInt(int upperbound) {
        Random rand = new Random(); //instance of random class
        int int_random = rand.nextInt(upperbound);
        return int_random;
    }

    public void genNodeXYPop() {
        if(this.populationNodeList.size() == 0) {
            for (int i = 0; i < getPopulationSize(); i++) {
                populationNodeList.add(new DNA_Node(eng));
            }
        }else {
            Collections.sort(populationNodeList);
            ArrayList<DNA_Node> elitismo = elitismoNodeList();
            int upperbound = elitismo.size();
            for (int i = 0; i < this.populationNodeList.size(); i++) {
                if (i < elitismoSize) {
                    populationNodeList.set(i, elitismo.get(i));
                } else {
                    int randnum = randInt(upperbound);
                    DNA_Node novoDNA = new DNA_Node(eng, elitismo.get(randnum).cloneList());
                    populationNodeList.set(i, novoDNA);
                }
            }
        }
    }


    public void genNodeTypePop() throws XYNotFoundException {
        if(!isAcabouNodeXY())
            throw new XYNotFoundException("O X e Y dos nodes têm que ser encontrados antes do tipo");

            ArrayList<DNA_Node> elitismo = elitismoNodeList();
            int upperbound = elitismo.size();

            for (int i = 0; i < this.populationNodeList.size(); i++) {
                if (i < elitismoSize) {
                    populationNodeList.set(i, elitismo.get(i));
                } else {
                    int randnum = randInt(upperbound);
                    DNA_Node novoDNA = new DNA_Node(eng, elitismo.get(randnum).cloneList());
                    populationNodeList.set(i, novoDNA);
                }
            }
    }

    public void mutatePopulationXY() {
        for (int i = elitismoSize; i < this.populationNodeList.size(); i++) {
            populationNodeList.get(i).mutateXY(this.nodeMutationFreq);
        }
    }

    public void mutatePopulationType() throws XYNotFoundException {
        if(!isAcabouNodeXY())
            throw new XYNotFoundException("O X e Y dos nodes têm que ser encontrados antes do tipo");
        for (int i = elitismoSize; i < this.populationNodeList.size(); i++) {
            populationNodeList.get(i).mutateType(this.nodeMutationFreq);
        }
    }

    public void visualizationNodePop() throws VisualizationNotFoundException {
        Collections.sort(populationNodeList);
        populationNodeList.get(0).updateVisualization();
    }

    public void genEdgeNodePop(double percentagem) throws IncompleteNodesException {
        if(!isAcabouNodeXY() && !isAcabouNodeType())
            throw new IncompleteNodesException("Os Nodes não foram totalmente encontrados !");

        if(this.populationEdgeList.size() == 0) {
            setRepCounter(0);
            for (int i = 0; i < getPopulationSize(); i++) {
                populationEdgeList.add(new DNA_Edge(percentagem, getcompleteNodeDNA().cloneList(), eng));
            }
        }else {
            ArrayList<DNA_Edge> elitismo = elitismoEdgeList();
            int upperbound = elitismo.size();
            for (int i = 0; i < this.populationEdgeList.size(); i++) {
                if (i < elitismoSize) {
                    populationEdgeList.set(i, elitismo.get(i));
                } else {
                    int randnum = randInt(upperbound);
                    DNA_Edge novoDNA = this.populationEdgeList.get(i).crossOver(elitismo.get(randnum));
                    populationEdgeList.set(i, novoDNA);
                }
            }
        }
    }

    public void calcEdgeNodeFitness() throws IncompleteNodesException {
        if(!isAcabouNodeXY() && !isAcabouNodeType())
            throw new IncompleteNodesException("Os Nodes não foram totalmente encontrados !");

        Collections.sort(populationEdgeList);
        Iterator iterator = populationEdgeList.iterator();
        while (iterator.hasNext()) {
            DNA_Edge dna = (DNA_Edge) iterator.next();
            dna.calcEdgeNodeFitness();
            //verificar se chegou ao fim
            if(getRepCounter() == getNumMaxRep()){
            //if (dna.getFitness() == 1 && getBestNodeEdges() == null) {
                    endNodeEdgeSearch(populationEdgeList.get(0));
            }
        }

    }

    public void mutatePopulationEdgeNode() throws IncompleteNodesException {
        if(!isAcabouNodeXY() && !isAcabouNodeType())
            throw new IncompleteNodesException("Os Nodes não foram totalmente encontrados !");

        for (int i = elitismoSize; i < this.populationEdgeList.size(); i++) {
            populationEdgeList.get(i).mutateEdgeNodes(this.nodeMutationFreq);
        }
    }

    private void endNodeEdgeSearch(DNA_Edge dna){
        setAcabouEdgeNodes(true);
        dna.removeUselessEdgeNodes();
        setBestNodeEdges(new DNA_Edge(dna.getNodelist(),dna.cloneList(),eng));
    }

    public void prepararListaPPeso()throws EdgesNotFoundException {
        if(!isAcabouEdgeNodes())
            throw new EdgesNotFoundException("As Edges não foram totalmente encontradas !");

        setRepCounter(0);
        for (int i = 0; i < this.populationEdgeList.size(); i++) {
            DNA_Edge novoDNA = new DNA_Edge(getBestNodeEdges().getNodelist(),getBestNodeEdges().cloneList(),eng);
            populationEdgeList.set(i, novoDNA);
        }
    }

    public void visualizationEdgePop() throws VisualizationNotFoundException {
        Collections.sort(populationEdgeList);
        populationEdgeList.get(0).updateVisualization();

    }

    public void genWeightPop() throws EdgesNotFoundException {
        if(!isAcabouEdgeNodes())
            throw new EdgesNotFoundException("As Edges não foram totalmente encontradas !");
        ArrayList<DNA_Edge> elitismo = elitismoEdgeList();
        int upperbound = elitismo.size();

        for (int i = 0; i < this.populationNodeList.size(); i++) {
            if (i < elitismoSize) {
                populationEdgeList.set(i, elitismo.get(i));
            } else {
                int randnum = randInt(upperbound);
                DNA_Edge novoDNA = new DNA_Edge(getcompleteNodeDNA().getNodeList(), elitismo.get(randnum).cloneList(), eng);
                populationEdgeList.set(i, novoDNA);
            }
        }
    }


    public void calcWeightFitness() throws EdgesNotFoundException {
        if(!isAcabouEdgeNodes())
            throw new EdgesNotFoundException("As Edges não foram totalmente encontradas !");

        Collections.sort(populationEdgeList);
        Iterator iterator = populationEdgeList.iterator();
        while (iterator.hasNext()) {
            DNA_Edge edge = (DNA_Edge) iterator.next();
            edge.calcWeightFitness();
            if(getRepCounter() == getNumMaxRep()){
            //if(edge.getFitness() == 1 && getCompleteEdges() == null) {
                setAcabouEdgeWeights(true);
                setCompleteEdges(populationEdgeList.get(0));
            }
        }
    }

    public void mutatePopulationWeight() throws EdgesNotFoundException {
        if(!isAcabouEdgeNodes())
            throw new EdgesNotFoundException("As Edges não foram totalmente encontradas !");
        for (int i = elitismoSize; i < this.populationEdgeList.size(); i++) {
            populationEdgeList.get(i).mutateWeight(this.nodeMutationFreq);
        }
    }

    private void fitnessRepCounter(double fitness){
        if(fitness != getRepFitness()) {
            setRepFitness(fitness);
            setRepCounter(0);
        }
        else
            setRepCounter(getRepCounter() +1);
    }


    public String toStringNodeList() {
        return "Population{" +
                "populationNodeList=" + populationNodeList +
                '}';
    }

    public String toStringEdgeList() {
        return "Population{" +
                "populationNodeList=" + populationEdgeList +
                '}';
    }
}
