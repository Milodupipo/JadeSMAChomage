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
            
            AgentController personneA = ac.createNewAgent("chomeur1", "smajadetest.Personne", new Object[]{});

            AgentController entrepriseA = ac.createNewAgent("entreprise1", "smajadetest.Entreprise", new Object[]{});

            AgentController agence = ac.createNewAgent("PoleEmploi", "smajadetest.Agence", new Object[]{});

            agence.start();
            entrepriseA.start();
            personneA.start();

        } catch (ControllerException ex) {
            Logger.getLogger(SmaJadeTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
