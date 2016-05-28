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
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.ThreadLocalRandom;

public class SmaJadeTest {

    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();
             Properties p = new ExtendedProperties();
             p.setProperty("gui", "false");
             ProfileImpl pc = new ProfileImpl(p);
             
           // Runtime rt = Runtime.instance();
           // ProfileImpl pc = new ProfileImpl(false);
            pc.setParameter(ProfileImpl.MAIN_HOST, "localhost");
            AgentContainer ac = rt.createAgentContainer(pc);

            AgentController entrepriseA = ac.createNewAgent("entreprise1", "smajadetest.Entreprise",
                    new Object[]{"[{\"idPoste\": 0, \"domaine\": 0, \"exp\": 3}, "
                        + "{\"idPoste\": 1, \"domaine\": 0, \"exp\": 5}, "
                        + "{\"idPoste\": 2, \"domaine\": 0, \"exp\": 7}, "
                        + "{\"idPoste\": 3, \"domaine\": 0, \"exp\": 9},"
                        + "{\"idPoste\": 4, \"domaine\": 0, \"exp\": 11},"
                        + "{\"idPoste\": 5, \"domaine\": 1, \"exp\": 3},"
                        + "{\"idPoste\": 6, \"domaine\": 1, \"exp\": 5},"
                        + "{\"idPoste\": 7, \"domaine\": 1, \"exp\": 7},"
                        + "{\"idPoste\": 8, \"domaine\": 1, \"exp\": 9},"
                        + "{\"idPoste\": 9, \"domaine\": 1, \"exp\": 11}]"});

            AgentController agence = ac.createNewAgent("PoleEmploi", "smajadetest.Agence", new Object[]{});

            AgentController tabAgent[] = new AgentController[12];
            for (int i = 0; i < tabAgent.length; i += 2) {
                tabAgent[i] = ac.createNewAgent("chomeur" + i, "smajadetest.Personne", new Object[]{"{\"accepter\": false, \"demissionner\": false, \"domaine\": " + 0 + ", \"exp\": " + (i+1) + "}"});
                tabAgent[i+1] = ac.createNewAgent("chomeur" + (i+1), "smajadetest.Personne", new Object[]{"{\"accepter\": false, \"demissionner\": false, \"domaine\": " + 1 + ", \"exp\": " + (i+1) + "}"});
            }

            agence.start();
            entrepriseA.start();

            for (int i = 0; i < tabAgent.length; i++) {
                tabAgent[i].start();
            }

        } catch (ControllerException ex) {
            Logger.getLogger(SmaJadeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
