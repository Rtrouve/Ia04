import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class MULT extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println(getLocalName()+ "--> Installed Mult Agent");
		addBehaviour(new MultBehaviour(this));
		
	}

	
	class MultBehaviour extends Behaviour {
		private ObjectMapper map;
		
		public MultBehaviour(Agent a){
			super(a);
			map = new ObjectMapper();
		}
		
		public void action() {
			ACLMessage message = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
			if (message != null) {
				answer(message);
			}
			else
				block();
		}
		
		private void answer(ACLMessage message) {
			Operation par;
			
			ACLMessage reply = message.createReply();
			
			try {
				par = map.readValue(message.getContent(), Operation.class);
				

				Float n = par.getOperande1() * par.getOperande2();
				reply.setPerformative(ACLMessage.INFORM);
				try {
					reply.setContent(map.writeValueAsString(new Resultat(n)));
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			send(reply);
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}
}
