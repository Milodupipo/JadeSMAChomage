/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smajadetest;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.DataStore;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Mickael
 */
public class Agence extends Agent {

    Map<String, Vector< Integer>> tabPersonnes = new HashMap<String, Vector< Integer>>();
    Map<String, Vector< Vector< Integer>>> tabEntreprises = new HashMap<String, Vector< Vector< Integer>>>();

    @Override
    protected void setup() {
        System.out.println("Demarrage : " + this.getAID().getName());

        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                for (Entry<String, Vector< Vector< Integer>>> entreprise : tabEntreprises.entrySet()) {
                    for (Vector< Integer> emploi : entreprise.getValue()) {
                        for (Entry<String, Vector< Integer>> chomeur : tabPersonnes.entrySet()) {
                            //si domaine == domaine et experience requise < experience chomeur
                            if (emploi.get(1) == chomeur.getValue().get(0)) {
                                message.addReceiver(new AID(entreprise.getKey(), AID.ISLOCALNAME));
                                message.setContent("{\"idPoste\": " + emploi.get(0) + ",\"chomeur\":\"" + chomeur.getKey() + "\"}");
                                System.out.println("L'agence à trouvé un job pour quelqu'un !");
                                 System.out.println(message);
                                send(message);
                               break ;
                            }
                        }
                    }
                }
            }
        });

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        //Ajoute une entreprise
                        String decodage = msg.getContent();
                        System.out.println(msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
                        if (msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].compareTo("chomeur") != 0)
                        {
                            System.out.println("decodage:" + decodage);
                            JSONParser parser = new JSONParser();
                            //nom de l'entreprise à parser
                            JSONArray tableau = (JSONArray) parser.parse(decodage);
                            //chaque ligne de la map correspond à une entreprise différente
                            tabEntreprises.put(msg.getSender().getName().split("@")[0], new Vector< Vector< Integer>>());
                            for (int i = 0; i < tableau.size(); i++) {
                                //chaque ligne ajoutée au vector correspond à un travail 
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).add(new Vector< Integer>());
                                JSONObject ligne = (JSONObject) tableau.get(i);
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).get(i).add((int) (long) ligne.get("idPoste"));
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).get(i).add((int) (long) ligne.get("domaine"));
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).get(i).add((int) (long) ligne.get("exp"));
                                System.out.println("L'entreprise propose le poste numéro " + (long) ligne.get("idPoste") + " avec " + (long) ligne.get("exp") + " ans d'expérience dans le domaine " + (long) ligne.get("domaine"));
                            }
                        } 
                        else 
                        {
                            JSONParser parser = new JSONParser();
                            JSONObject ligne = (JSONObject) parser.parse(decodage);
                            boolean jobAccepter = (boolean) ligne.get("accepter");
                            //Si un job est accepté on supprime le chomeur de la liste
                            //Sinon le chomeur veut s'inscrire
                            if (jobAccepter) {
                                tabPersonnes.remove(msg.getSender().getName().split("@")[0]);
                                tabEntreprises.get((String) ligne.get("entreprise")).remove(((int) (long) ligne.get("idPoste")));
                                System.out.println("Le chomeur a accepté son travail, il est désinscrie");
                            } else {
                                //Ajoute une personne à la liste avec son domaine d'expertise et son expérience
                                //parser le nom de l'expéditeur
                                tabPersonnes.put(msg.getSender().getName().split("@")[0], new Vector<Integer>());
                                tabPersonnes.get(msg.getSender().getName().split("@")[0]).add((int) (long) ligne.get("domaine"));
                                tabPersonnes.get(msg.getSender().getName().split("@")[0]).add((int) (long) ligne.get("exp"));
                                System.out.println("Le chomeur a " + (long) ligne.get("exp") + " ans d'expérience dans le domaine " + (long) ligne.get("domaine"));
                            }
                        }

                    } catch (ParseException ex) {
                        Logger.getLogger(Agence.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                block();

            }
        });
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        //Ajoute une entreprise
                        String decodage = msg.getContent();
                        System.out.println(msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0]);
                        if (msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].compareTo("chomeur") != 0)
                        {
                            System.out.println("decodage:" + decodage);
                            JSONParser parser = new JSONParser();
                            //nom de l'entreprise à parser
                            JSONArray tableau = (JSONArray) parser.parse(decodage);
                            //chaque ligne de la map correspond à une entreprise différente
                            tabEntreprises.put(msg.getSender().getName().split("@")[0], new Vector< Vector< Integer>>());
                            for (int i = 0; i < tableau.size(); i++) {
                                //chaque ligne ajoutée au vector correspond à un travail 
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).add(new Vector< Integer>());
                                JSONObject ligne = (JSONObject) tableau.get(i);
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).get(i).add((int) (long) ligne.get("idPoste"));
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).get(i).add((int) (long) ligne.get("domaine"));
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).get(i).add((int) (long) ligne.get("exp"));
                                System.out.println("L'entreprise propose le poste numéro " + (long) ligne.get("idPoste") + " avec " + (long) ligne.get("exp") + " ans d'expérience dans le domaine " + (long) ligne.get("domaine"));
                            }
                        } 
                        else 
                        {
                            JSONParser parser = new JSONParser();
                            JSONObject ligne = (JSONObject) parser.parse(decodage);
                            boolean jobAccepter = (boolean) ligne.get("accepter");
                            //Si un job est accepté on supprime le chomeur de la liste
                            //Sinon le chomeur veut s'inscrire
                            if (jobAccepter) {
                                tabPersonnes.remove(msg.getSender().getName().split("@")[0]);
                                tabEntreprises.get((String) ligne.get("entreprise")).remove(((int) (long) ligne.get("idPoste")));
                                System.out.println("Le chomeur a accepté son travail, il est désinscrie");
                            } else {
                                //Ajoute une personne à la liste avec son domaine d'expertise et son expérience
                                //parser le nom de l'expéditeur
                                tabPersonnes.put(msg.getSender().getName().split("@")[0], new Vector<Integer>());
                                tabPersonnes.get(msg.getSender().getName().split("@")[0]).add((int) (long) ligne.get("domaine"));
                                tabPersonnes.get(msg.getSender().getName().split("@")[0]).add((int) (long) ligne.get("exp"));
                                System.out.println("Le chomeur a " + (long) ligne.get("exp") + " ans d'expérience dans le domaine " + (long) ligne.get("domaine"));
                            }
                        }

                    } catch (ParseException ex) {
                        Logger.getLogger(Agence.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                block();

            }
        });
    }

    @Override

    protected void takeDown() {
        System.out.println("Destruction de PoleEmploi");
    }
}
