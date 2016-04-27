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
public class Entreprise extends Agent {

    private String secteur;
    private int nbEmplois;

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
         });
         addBehaviour(new CyclicBehaviour(this) {
         @Override
         public void action() {
         block();
         ACLMessage msg = receive();
         if (msg != null) {
         if (Integer.parseInt(msg.getContent()) == 1) {
         System.out.println("Un employé a quitté son poste");
         } else {
         System.out.println("Un employé a rejoint l'entreprise");
         nbEmplois--;
         }
         }
         }
         });*/
    }

    @Override
    protected void takeDown() {
        System.out.println("Destruction de Entreprise");
    }
}
