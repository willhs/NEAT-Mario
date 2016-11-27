package will.pso.io;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import will.pso.Feature;
import will.pso.WillParticle;
import will.pso.WillSwarm;
import will.pso.encog.EncogHyperMarioProblem;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static will.pso.PSORunner.*;
import static will.pso.io.util.XMLUtil.asList;

/**
 * Created by Will on 4/06/2016.
 */
public class PSOIO {

    public static void writePSOIterationToFile(String filename, WillSwarm swarm, int iteration, double gBestFitness) {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = null;

        // build xml
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            doc = builder.newDocument();

            Element swarmElem = doc.createElement("swarm");
            doc.appendChild(swarmElem);

            swarmElem.setAttribute("time", Instant.now().toString());
            swarmElem.setAttribute("iteration", iteration + "");
            swarmElem.setAttribute("c1", c1 + "");
            swarmElem.setAttribute("c2", c2 + "");
            swarmElem.setAttribute("inertia", inertia + "");
            swarmElem.setAttribute("gBestFitness", gBestFitness + "");

            Element particleElem = doc.createElement("particles");

            for (WillParticle particle : swarm.getParticles()) {
                particleElem.appendChild(generateParticleXML(particle, doc));
            }

            swarmElem.appendChild(particleElem);

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        // write xml to file
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
//            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            // send DOM to file
            tr.transform(new DOMSource(doc),
                    new StreamResult(new FileOutputStream(filename)));

        } catch (TransformerException te) {
            System.out.println(te.getMessage());
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    private static Node generateParticleXML(WillParticle particle, Document dom) {
        Element particleElem = dom.createElement("particle");

        particleElem.setAttribute("c1", particle.getC1() + "");
        particleElem.setAttribute("c2", particle.getC2() + "");
        particleElem.setAttribute("inertia", particle.getInertia() + "");
        particleElem.setAttribute("fitness", particle.getFitness() + "");
        particleElem.setAttribute("pBestFitness", particle.getPBestFitness() + "");
        particleElem.setAttribute("nBestFitness", particle.getNBestFitness() + "");

        Element featuresElem = dom.createElement("features");
        particleElem.appendChild(featuresElem);
        for (Feature feature : particle.getFeatures()) {
            featuresElem.appendChild(generateFeatureXML(feature, dom));
        }

        Element pBestFeaturesElem = dom.createElement("pBestFeatures");
        particleElem.appendChild(pBestFeaturesElem);
        for (double pBestFeature : particle.getPBestFeatures()) {
            Element pBestFeatureElem = dom.createElement("pBestFeature");
            pBestFeatureElem.appendChild(dom.createTextNode(pBestFeature + ""));
            pBestFeaturesElem.appendChild(pBestFeatureElem);
        }

        Element nBestFeaturesElem = dom.createElement("nBestFeatures");
        particleElem.appendChild(nBestFeaturesElem);
        for (double nBestFeature : particle.getNBestFeatures()) {
            Element nBestFeatureElem = dom.createElement("nBestFeature");
            nBestFeatureElem.appendChild(dom.createTextNode(nBestFeature + ""));
            nBestFeaturesElem.appendChild(nBestFeatureElem);
        }

        return particleElem;
    }

    private static Node generateFeatureXML(Feature feature, Document dom) {
        Element featElem = dom.createElement("feature");

        featElem.setAttribute("name", feature.getName());
        featElem.setAttribute("value", feature.getVal() + "");
        featElem.setAttribute("velocity", feature.getVel() + "");
        featElem.setAttribute("min", feature.getMin() + "");
        featElem.setAttribute("max", feature.getMax() + "");
        featElem.setAttribute("initialVal", feature.getInitialVal() + "");

        return featElem;
    }

    public static WillSwarm parseSwarm(EncogHyperMarioProblem problem, String filename) {
        double c1 = -1;
        double c2 = -1;
        double inertia = -1;
        List<WillParticle> particles = new ArrayList<>();

        // parse xml
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(filename));
            doc.getDocumentElement().normalize();
            Element swarm = (Element) doc.getElementsByTagName("swarm").item(0);

            c1 = getDoubleAttr(swarm, "c1");
            c2 = getDoubleAttr(swarm, "c2");
            inertia = getDoubleAttr(swarm, "inertia");

            Element particlesElem = (Element) swarm.getElementsByTagName("particles").item(0);

            for (Node particleElem : asList(particlesElem.getElementsByTagName("particle"))) {
                particles.add(parseParticle((Element)particleElem));
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new WillSwarm(problem, particles, c1, c2, inertia);
    }

    private static WillParticle parseParticle(Element particleElem) {
        double c1 = getDoubleAttr(particleElem, "c1");
        double c2 = getDoubleAttr(particleElem, "c2");
        double inertia = getDoubleAttr(particleElem, "inertia");
        double fitness = getDoubleAttr(particleElem, "fitness");
        double pBestFitness = getDoubleAttr(particleElem, "pBestFitness");
        double nBestFitness = getDoubleAttr(particleElem, "nBestFitness");

        List<Feature> neurophFeatures = new ArrayList<>();
        Element featuresNode = (Element) particleElem.getElementsByTagName("neurophFeatures").item(0);

        for (Node featureNode : asList(featuresNode.getElementsByTagName("feature"))) {
            String name = getStringVal(featureNode, "name");
            double val = getDoubleAttr(featureNode, "value");
            double vel = getDoubleAttr(featureNode, "velocity");
            double min = getDoubleAttr(featureNode, "min");
            double max = getDoubleAttr(featureNode, "max");
            double initialVal = getDoubleAttr(featureNode, "initialVal");

            neurophFeatures.add(new Feature(name, val, vel, min, max, initialVal));
        }

        Element pBestFeaturesElem = (Element) particleElem.getElementsByTagName("pBestFeatures").item(0);
        List<Double> pBestFeatures = new ArrayList<>();
        for (Node pBestNode : asList(pBestFeaturesElem.getElementsByTagName("pBestFeature"))) {
            pBestFeatures.add(Double.parseDouble(pBestNode.getTextContent()));
        }

        Element nBestFeaturesElem = (Element) particleElem.getElementsByTagName("nBestFeatures").item(0);
        List<Double> nBestFeatures = new ArrayList<>();
        for (Node nBestNode : asList(nBestFeaturesElem.getElementsByTagName("nBestFeature"))) {
            nBestFeatures.add(Double.parseDouble(nBestNode.getTextContent()));
        }

        return new WillParticle(
                neurophFeatures, pBestFeatures, nBestFeatures, fitness, pBestFitness, nBestFitness, c1, c2, inertia
        );
    }

    private static String getStringVal(Node node, String attrName) {
        return node.getAttributes().getNamedItem(attrName).getNodeValue();
    }

    private static double getDoubleAttr(Node node, String attrName) {
        return Double.parseDouble(node.getAttributes().getNamedItem(attrName).getNodeValue());
    }

    public static double parseBestFitness(String filename) {
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = builder.parse(new File(filename));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        Element swarm = (Element) doc.getElementsByTagName("swarm").item(0);

        return Double.parseDouble(swarm.getAttribute("bestFitness"));
    }

    public static int parseStartingIter(String filename) {
        DocumentBuilder builder = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = builder.parse(new File(filename));
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        doc.getDocumentElement().normalize();
        Element swarm = (Element) doc.getElementsByTagName("swarm").item(0);

        return Integer.parseInt(swarm.getAttribute("iteration"));
    }
}
