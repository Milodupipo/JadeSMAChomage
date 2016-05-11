/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smajadetest;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
public class Entreprise extends Agent {

    private String secteur;
    private int nbEmplois;
    Map<String, Vector< Integer>> tabPersonnes = new HashMap<String, Vector< Integer>>();

    @Override
    protected void setup() {
        System.out.println("Demarrage : " + this.getAID().getName());
        Object[] args = getArguments();

        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
        Vector< Vector<Integer>> postes = new Vector< Vector<Integer>>();
        
        if (args != null) {
            try {
                JSONParser parser = new JSONParser();
                JSONArray tableau = (JSONArray) parser.parse((String) args[0]);
                for (int i = 0; i < tableau.size(); i++) {
                    JSONObject ligne = (JSONObject) tableau.get(i);
                    for (int j = 0; j < ligne.size(); j++) {
                        postes.add(new Vector<Integer>());
                        postes.get(j).add((int)(long) ligne.get("domaine"));
                        postes.get(j).add((int)(long) ligne.get("exp"));
                    }
                }
            } catch (ParseException ex) {
                Logger.getLogger(Entreprise.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String c = (String)args[0];
        message.setContent(c);
        send(message);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        //Ajoute une entreprise
                        String contenuMessage = msg.getContent();
                        // Si Pole emploi envoie message qu'il a trouvé quelq'un pour un poste 
                        // Envoyer message au chomeur envoyé par Pole emploi pour informer l'entreprise a accepté son profil et attendre sa réponde
                        JSONParser parser = new JSONParser();
                        JSONObject ligne = (JSONObject) parser.parse(contenuMessage);
                        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                        message.addReceiver(new AID((String)ligne.get("chomeur"), AID.ISLOCALNAME));
                        //recupere l'experience d'un poste
                        int experience = postes.get((int)(long)ligne.get("idPoste")).get(1);
                        message.setContent("{\"accepter\":true," + "\"idPoste\":" + ligne.get("idPoste") + "," + "\"chomeur\":\"" + ligne.get("chomeur") + "\", \"experience\":" + experience + "}");
                        send(message);

                    } catch (ParseException ex) {
                        Logger.getLogger(Agence.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    block();
                }

            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Destruction de Entreprise");
    }
}
