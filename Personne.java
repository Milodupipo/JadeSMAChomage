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


public class Personne extends Agent {

    private String inscription = "";
    private int expAncienTravail = 0;
    private int idAncienTravail = -1;
    private String nomEntreprise = "";

    @Override
    protected void setup() {
        System.out.println("Demarrage : " + this.getAID().getName());

        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));

        Object[] args = getArguments();

        Vector< Integer> competence = new Vector< Integer>();

        if (args != null) {
            try {
                inscription = (String) args[0];
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
        message.setContent(inscription);
        send(message);

        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    try {
                        String decodage = msg.getContent();
                        if (msg.getSender().getName().split("@")[0].split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)")[0].compareTo("entreprise") == 0) {
                            //System.out.println("decodage Personne:" + decodage);
                            JSONParser parser = new JSONParser();
                            JSONObject ligne = (JSONObject) parser.parse(decodage);
                            int expTravail = (int) (long) ligne.get("experience");
                            //Si le nouveau travail proposé répond mieux aux compétences de l'agent alors il l'accepte
                            if (Math.abs(expTravail - competence.get(1)) < Math.abs(expAncienTravail - competence.get(1))) {
                                ACLMessage message = new ACLMessage(ACLMessage.INFORM);
                                //Si on avait deja un travail avant alors on le quitte avant de prendre le nouveau
                                if (idAncienTravail != -1) {
                                    message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
                                    message.setContent("{\"accepter\": false," + "\"demissionner\": true," + " \"entreprise\":\"" + nomEntreprise + "\", \"idPoste\":" + idAncienTravail + "}");
                                    send(message);
                                }
                                message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
                                message.setContent("{\"accepter\": true," + " \"entreprise\":\"" + msg.getSender().getName().split("@")[0] + "\"," + "\"idPoste\":" + ligne.get("idPoste") + "}");
                                send(message);
                                //Si le poste ne correspond pas exactement aux compétences du chomeur alors on se réinscrie
                                if (expTravail != competence.get(1)) {
                                    message.setContent(inscription);
                                    send(message);
                                }
                                expAncienTravail = expTravail;
                                idAncienTravail = (int) (long) ligne.get("idPoste");
                                nomEntreprise = msg.getSender().getName().split("@")[0];
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
