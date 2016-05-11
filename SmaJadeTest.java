/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package smajadetest;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.util.leap.Properties;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mickael
 */
public class SmaJadeTest {

    public static void main(String[] args) {
        try {
            /*Runtime rt = Runtime.instance();
             Properties p = new ExtendedProperties();
             p.setProperty("gui", "true");
             ProfileImpl pc = new ProfileImpl(p);
             AgentContainer c = rt.createAgentContainer(pc);
             c.start();*/
            Runtime rt = Runtime.instance();
            ProfileImpl pc = new ProfileImpl(false);
            pc.setParameter(ProfileImpl.MAIN_HOST, "localhost");
            AgentContainer ac = rt.createAgentContainer(pc);
            
            AgentController entrepriseA = ac.createNewAgent("entreprise1", "smajadetest.Entreprise", 
                    new Object[]{"[{\"idPoste\": 0, \"domaine\": 3, \"exp\": 2}, {\"idPoste\": 1, \"domaine\": 0, \"exp\": 4}, {\"idPoste\": 2, \"domaine\": 0, \"exp\": 7}]"});

            AgentController personneA = ac.createNewAgent("chomeur1", "smajadetest.Personne", new Object[]{"{\"accepter\": false, \"domaine\": 0, \"exp\": 7}"});
            AgentController personneB = ac.createNewAgent("chomeur2", "smajadetest.Personne", new Object[]{"{\"accepter\": false, \"domaine\": 3, \"exp\": 7}"});
             AgentController personneC = ac.createNewAgent("chomeur3", "smajadetest.Personne", new Object[]{"{\"accepter\": false, \"domaine\": 0, \"exp\": 6}"});
            AgentController personneD = ac.createNewAgent("chomeur4", "smajadetest.Personne", new Object[]{"{\"accepter\": false, \"domaine\": 1, \"exp\": 5}"});
            
            AgentController agence = ac.createNewAgent("PoleEmploi", "smajadetest.Agence", new Object[]{});

            agence.start();
            entrepriseA.start();
            personneA.start();
            personneB.start();
            personneC.start();
            personneD.start();

        } catch (ControllerException ex) {
            Logger.getLogger(SmaJadeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
