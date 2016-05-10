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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Mickael
 */
public class Personne extends Agent {

    private boolean occupe = false;
    private String specialite = null;
    private String c;

    @Override
    protected void setup() {
        System.out.println("Demarrage : " + this.getAID().getName());

        Object[] args = getArguments();
        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
        //  String c = "{\"accepter\": false, \"domaine\": 0, \"exp\": 7}";
        if (args != null) {
            c = (String) args[0];
        }
        message.setContent(c);
        send(message);


        /* message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
         String d = "{\"accepter\": true, \"entreprise\": \"entreprise1\", \"idPoste\": 1}";
         message.setContent(d);
         send(message);
         Object[] args = getArguments();
         if (args.length == 1) {
         specialite = (String) args[0];
         }
         addBehaviour(new TickerBehaviour(this, 2000) {
         @Override
         protected void onTick() {
         String oc;
         if (occupe) {
         oc = "0";
         } else {
         oc = "1";
         }
         ACLMessage message = new ACLMessage(ACLMessage.INFORM);
         message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
         message.setContent(oc);
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
                        if (msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].compareTo("entreprise") == 0) {
                            System.out.println("decodage Personne:" + decodage);
                            JSONParser parser = new JSONParser();
                            JSONObject ligne = (JSONObject) parser.parse(decodage);
                            ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                            message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
                            message.setContent("{\"accepter\": true," + " \"entreprise\":\"" + msg.getSender().getName().split("@")[0] + "\"," + "\"idPoste\":" + ligne.get("idPoste") + "}");
                            System.out.println("Le chomeur a accepté le poste^^");
                            send(message);

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
