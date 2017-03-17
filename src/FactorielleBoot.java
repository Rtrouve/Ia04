import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
		

public class FactorielleBoot {

	public static void main(String[] args) {
		final String MAIN_PROPERTIES_FILE = "PrimaryProperties";
		Runtime rt = Runtime.instance();
		Profile p = null;

		try{
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			AgentContainer mc = rt.createMainContainer(p);
			
			AgentController ac = mc.createNewAgent("FactTest","Factor", null);
			ac.start();		
			AgentController ac2 = mc.createNewAgent("MULT","MULT", null);
			ac2.start();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
