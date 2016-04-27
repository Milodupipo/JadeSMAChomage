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

/**
 *
 * @author Mickael
 */
public class Personne extends Agent {

    private boolean occupe = false;
    private String specialite = null;

    @Override
    protected void setup() {
        System.out.println("Demarrage : " + this.getAID().getName());

        ACLMessage message = new ACLMessage(ACLMessage.INFORM);
        message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
        String c = "{\"accepter\": false, \"domaine\": 0, \"exp\": 3}";
        message.setContent(c);
        send(message);


        message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
        c = "{\"accepter\": true, \"entreprise\": \"entreprise1\", \"idPoste\": 1}";
        message.setContent(c);
        send(message);
        /*Object[] args = getArguments();
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
        /* addBehaviour(new CyclicBehaviour(this) {
         @Override
         public void action() {
         block();
         ACLMessage msg = receive();
         if (msg != null) {
         if (msg.getContent().compareTo("Job") == 0) {
         System.out.println(this.getAgent().getName().split("@")[0] + " : J'ai un travail !");
         occupe = true;
         } else if (msg.getContent().compareTo("CV") == 0) {
         ACLMessage message = new ACLMessage(ACLMessage.INFORM);
         message.addReceiver(new AID("PoleEmploi", AID.ISLOCALNAME));
         message.setContent(specialite);
         send(message);
         }
         }
         }
         });*/
    }

    @Override
    protected void takeDown() {
        System.out.println("Destruction de Personne");
    }

}
