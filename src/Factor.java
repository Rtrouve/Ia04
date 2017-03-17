import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Factor extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println(getLocalName()+ "--> Installed Factorielle Agent");
		addBehaviour(new ReceiveBehaviour(this));
		
	}
	
	class ReceiveBehaviour extends Behaviour{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		private HashMap<Integer, ArrayList<Operation>> todo;
		private HashMap<Integer, Integer> nbOperationsAttente;
		private Integer idCount = 0;
		private ObjectMapper map;
		
		public ReceiveBehaviour(Agent a){
			super(a);
			todo = new HashMap<>();
			nbOperationsAttente = new HashMap<>();
			map = new ObjectMapper();
		}

		@Override
		public void action() {
			MessageTemplate mtReq = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage request = receive(mtReq);
			
			if(request != null){
				Float n = Float.parseFloat(request.getContent());
				idCount++;
				todo.put(idCount, new ArrayList<>());
				nbOperationsAttente.put(idCount, 0);
				while(n > 1){
					if(n-1 > 1)
						todo.get(idCount).add(new Operation(n, n-1));
					else 
						todo.get(idCount).add(new Operation(n, null));
					n= n-2;
				}
			}
			
			MessageTemplate mtInf = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			ACLMessage response = receive(mtInf);
			//Receive result
			if(response != null){
				Integer id = Integer.parseInt(response.getConversationId());
				Resultat res;
				try {
					res = map.readValue(response.getContent(), Resultat.class);
					nbOperationsAttente.replace(id, nbOperationsAttente.get(id) - 1);
					
					if(todo.get(id).isEmpty() && nbOperationsAttente.get(id) == 0){
						System.out.println("Resultat : "+res.getResult());
						todo.remove(id);
						nbOperationsAttente.remove(id);
					}else{
						boolean needNewOp = true;
						for (Operation operation : todo.get(id)) {
							if(operation.hasMissingOperand()){
								operation.setMissingOperand(res.getResult());
								needNewOp = false;
							}
						}
						if(needNewOp){
							todo.get(id).add(new Operation(res.getResult(), null));
						}	
					}					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			// Vide les listes d'operations et envoi des operations a l'agent mult
			for(int i = 1; i <= idCount; i++){
				ArrayList<Operation>  temp = new ArrayList<Operation>();
				
				
				for (Operation operation : todo.get(i)) {
					if(!operation.hasMissingOperand()) {
						ACLMessage requestOperation = new ACLMessage(ACLMessage.REQUEST);
						try {
							requestOperation.setContent(map.writeValueAsString(operation));
						} catch (JsonProcessingException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						requestOperation.addReceiver(new AID("MULT", AID.ISLOCALNAME));
						requestOperation.setConversationId(""+i);
						send(requestOperation);


						temp.add(operation);
						nbOperationsAttente.replace(i, nbOperationsAttente.get(i) + 1);
					}
				}
				
				todo.get(i).removeAll(temp);
				
			}

		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		
	}
}
