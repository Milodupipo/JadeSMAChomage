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

        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
        String c = "[{\"idPoste\": 0, \"domaine\": 3, \"exp\": 2}, {\"idPoste\": 1, \"domaine\": 0, \"exp\": 4}]";
        message.setContent(c);
        send(message);

        /*Object[] args = getArguments();
         if (args.length == 2) {
         secteur = (String) args[0];
         nbEmplois = (Integer) args[1];
         }
        addBehaviour(new TickerBehaviour(this, 2000) {
            @Override
            protected void onTick() {
                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
                message.setContent(String.valueOf(nbEmplois));
                send(message);
            }
        });*/

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        //Ajoute une entreprise
                       String decodage = msg.getContent();
                        System.out.println(decodage);
                        // Si Pole emploi envoie message qu'il a trouvé quelq'un pour un poste 
                            // Envoyer message au chomeur envoyé par Pole emploi pour informer l'entreprise a accepté son profil et attendre sa réponde
                                JSONParser parser = new JSONParser();
                                JSONObject ligne = (JSONObject) parser.parse(decodage);
                            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                            message.addReceiver(new AID("chomeur1", AID.ISLOCALNAME));    
                            message.setContent("{\"accepter\":true,"+"\"idPoste\":" + ligne.get("idPoste") + "," + "\"chomeur\":\"" +  ligne.get("chomeur") + "\"}");
                            System.out.println(message);
                            System.out.println("L'entreprise a accepté le profile du chomeur. Il l'envoie message^^");
                            send(message);
              
                    }catch (ParseException ex) {
                        Logger.getLogger(Agence.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                else block();

            }
        });
    }

    @Override
    protected void takeDown() {
        System.out.println("Destruction de Entreprise");
    }
}
