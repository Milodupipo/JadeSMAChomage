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
public class Personne extends Agent {

    private int expAncienTravail = 0;

    @Override
    protected void setup() {
        System.out.println("Demarrage : " + this.getAID().getName());
        String c = "";
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
        //  String c = "{\"accepter\": false, \"domaine\": 0, \"exp\": 7}";

        Object[] args = getArguments();

        Vector< Integer> competence = new Vector< Integer>();

        if (args != null) {
            try {
                c = (String) args[0];
                JSONParser parser = new JSONParser();
                JSONObject ligne = (JSONObject) parser.parse((String) args[0]);
                for (int j = 0; j < ligne.size(); j++) {
                    competence.add((int) (long) ligne.get("domaine"));
                    competence.add((int) (long) ligne.get("exp"));
                }
            } catch (ParseException ex) {
                Logger.getLogger(Entreprise.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Envoie la candidature à PoleEmploi
        message.setContent(c);
        send(message);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        String decodage = msg.getContent();
                        if (msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].compareTo("entreprise") == 0) {
                            System.out.println("decodage Personne:" + decodage);
                            JSONParser parser = new JSONParser();
                            JSONObject ligne = (JSONObject) parser.parse(decodage);
                            int expTravail = (int) (long) ligne.get("experience");
                            //Si le nouveau travail proposé répond mieux aux compétences de l'agent alors il l'accepte
                            if ( Math.abs(expTravail - competence.get(1)) < Math.abs(expAncienTravail - competence.get(1))) {
                                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                                message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
                                message.setContent("{\"accepter\": true," + " \"entreprise\":\"" + msg.getSender().getName().split("@")[0] + "\"," + "\"idPoste\":" + ligne.get("idPoste") + "}");
                                send(message);
                                expAncienTravail = expTravail;
                            }

                        }

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
        System.out.println("Destruction de Personne");
    }

}
