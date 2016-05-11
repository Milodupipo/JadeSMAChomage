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
                    new Object[]{"[{\"idPoste\": 0, \"domaine\": 0, \"exp\": 2}, "
                        + "{\"idPoste\": 1, \"domaine\": 0, \"exp\": 5}, " 
                        + "{\"idPoste\": 2, \"domaine\": 0, \"exp\": 7}, "
                        + "{\"idPoste\": 3, \"domaine\": 1, \"exp\": 3},"
                        + "{\"idPoste\": 4, \"domaine\": 1, \"exp\": 7},"
                        + "{\"idPoste\": 5, \"domaine\": 1, \"exp\": 2},"
                        + "{\"idPoste\": 6, \"domaine\": 2, \"exp\": 2},"
                        + "{\"idPoste\": 7, \"domaine\": 2, \"exp\": 4},"
                        + "{\"idPoste\": 8, \"domaine\": 2, \"exp\": 6},"
                        + "{\"idPoste\": 9, \"domaine\": 2, \"exp\": 8}]"});

            AgentController agence = ac.createNewAgent("PoleEmploi", "smajadetest.Agence", new Object[]{});

            AgentController tabAgent[] = new AgentController[15];
            for (int i = 0; i < tabAgent.length; i++) {
                int domaine = ThreadLocalRandom.current().nextInt(0, 2 + 1);
                int exp = ThreadLocalRandom.current().nextInt(2, 8 + 1);
                tabAgent[i] = ac.createNewAgent("chomeur" + i, "smajadetest.Personne", new Object[]{"{\"accepter\": false, \"demissionner\": false, \"domaine\": " + domaine + ", \"exp\": " + exp + "}"});
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
