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

public class Agence extends Agent {

    Map<String, Vector< Integer>> tabPersonnes = new HashMap<String, Vector< Integer>>();
    Map<String, Vector< Vector< Integer>>> tabEntreprises = new HashMap<String, Vector< Vector< Integer>>>();

    @Override
    protected void setup() {
        System.out.println("Demarrage : " + this.getAID().getName());

        addBehaviour(new TickerBehaviour(this, 1500) {
            @Override
            protected void onTick() {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                for (Entry<String, Vector< Vector< Integer>>> entreprise : tabEntreprises.entrySet()) {
                    for (Vector< Integer> emploi : entreprise.getValue()) {
                        if (emploi.get(3) != 1) {
                            for (Entry<String, Vector< Integer>> chomeur : tabPersonnes.entrySet()) {
                                //si domaine == domaine et experience requise < experience chomeur
                                if (chomeur.getValue().get(3) == 1 && emploi.get(1) == chomeur.getValue().get(0) && chomeur.getValue().get(1) >= emploi.get(2)) {
                                    message.addReceiver(new AID(entreprise.getKey(), AID.ISLOCALNAME));
                                    message.setContent("{\"idPoste\": " + emploi.get(0) + ",\"chomeur\":\"" + chomeur.getKey() + "\"}");
                                    send(message);
                                    break;
                                }
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
                        if (msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].compareTo("chomeur") != 0) {
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
                                //indique que le poste est dispo, passe à 1 lorsque le poste est pris par quelqu'un
                                tabEntreprises.get(msg.getSender().getName().split("@")[0]).get(i).add(0);
                                //System.out.println("L'entreprise propose le poste numéro " + (long) ligne.get("idPoste") + " avec " + (long) ligne.get("exp") + " ans d'expérience dans le domaine " + (long) ligne.get("domaine"));
                            }
                        } else {
                            JSONParser parser = new JSONParser();
                            //System.out.println(decodage);

                            JSONObject ligne = (JSONObject) parser.parse(decodage);
                            boolean jobAccepter = (boolean) ligne.get("accepter");
                            //Si un job est accepté on supprime le chomeur de la liste
                            //Sinon le chomeur veut s'inscrire
                            if (jobAccepter) {
                                //System.out.println("Le chomeur " + msg.getSender().getName().split("@")[0] + " a accepté son travail idPoste" + ligne.get("idPoste") + ", il est désinscri");
                                //System.out.println("****** TABLE PERSONNES :" + tabPersonnes);
                                //System.out.println("****** LIGNE :" + ligne);
                                //System.out.println("****** personne remove  :" + msg.getSender().getName().split("@")[0]);
                                //On passe le chomeur en occupé et ne recherchant plus d'emploi
                                tabPersonnes.get(msg.getSender().getName().split("@")[0]).set(2, 1);
                                tabPersonnes.get(msg.getSender().getName().split("@")[0]).set(3, 0);
                                tabEntreprises.get((String) ligne.get("entreprise")).get((int) (long) ligne.get("idPoste")).set(3, 1);

                            } else {
                                boolean demissioner = (boolean) ligne.get("demissionner");
                                //Soit la personne demissionne soit elle s'inscrit
                                if (demissioner) {
                                    //System.out.println("Le chomeur " + msg.getSender().getName().split("@")[0] + " a quitté son travail idPoste" + ligne.get("idPoste"));
                                    Vector< Vector< Integer>> postes = tabEntreprises.get((String) ligne.get("entreprise"));
                                    for (int i = 0; i < postes.size(); i++) {
                                        if (postes.get(i).get(0) == (int) (long) ligne.get("idPoste")) {
                                            tabEntreprises.get((String) ligne.get("entreprise")).get(i).set(3, 0);
                                            break;
                                        }
                                    }
                                } else {
                                    //Ajoute une personne à la liste avec son domaine d'expertise et son expérience
                                    //Soit la personne est deja connue de pole emploi et on l'ajoute soit on la connait deja et on modifie son statut en recherche d'emploi
                                    if (!tabPersonnes.containsKey(msg.getSender().getName().split("@")[0])) {
                                        tabPersonnes.put(msg.getSender().getName().split("@")[0], new Vector<Integer>());
                                        tabPersonnes.get(msg.getSender().getName().split("@")[0]).add((int) (long) ligne.get("domaine"));
                                        tabPersonnes.get(msg.getSender().getName().split("@")[0]).add((int) (long) ligne.get("exp"));
                                        //Indique si une personne est occupée ou non, 0 chomage , 1 travaille
                                        tabPersonnes.get(msg.getSender().getName().split("@")[0]).add(0);
                                        //Indique si la personne recherche un emploi ou non (1 ou 0)
                                        tabPersonnes.get(msg.getSender().getName().split("@")[0]).add(1);
                                    } else {
                                        tabPersonnes.get(msg.getSender().getName().split("@")[0]).set(3, 1);
                                    }
                                    //System.out.println("Le " + msg.getSender().getName().split("@")[0] + " a " + (long) ligne.get("exp") + " ans d'expérience dans le domaine " + (long) ligne.get("domaine"));
                                }
                            }
                        }
                    } catch (ParseException ex) {
                        Logger.getLogger(Agence.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    block();
                }

            }
        }
        );

        //Génère des petites statistiques sur le nombre d'emplois et la situation des personnes
        addBehaviour(new TickerBehaviour(this, 100) {
            @Override
            protected void onTick() {
                int nbPersonnes = 0, nbActif = 0, nbRecherche = 0;
                int nbEmploi = 0, nbOccupes = 0;
                for (Entry<String, Vector< Integer>> chomeur : tabPersonnes.entrySet()) {
                    nbPersonnes++;
                    if (chomeur.getValue().get(2) == 1) {
                        nbActif++;
                    }
                    if (chomeur.getValue().get(3) == 1) {
                        nbRecherche++;
                    }
                }
                for (Entry<String, Vector< Vector< Integer>>> entreprise : tabEntreprises.entrySet()) {
                    for (Vector< Integer> emploi : entreprise.getValue()) {
                        nbEmploi++;
                        if (emploi.get(3) == 1) {
                            nbOccupes++;
                        }
                    }
                }

                for (int i = 0;
                        i < 25; i++) {
                    System.out.println("");
                }

                System.out.println(nbOccupes + " emplois sont occupés sur " + nbEmploi);
                System.out.println(nbActif + " personnes travaillent sur " + nbPersonnes);
                System.out.println(nbRecherche + " personnes recherchent un travail sur " + nbPersonnes);
            }
        }
        );
    }

    @Override

    protected void takeDown() {
        System.out.println("Destruction de PoleEmploi");
    }
}
