
public class Operation {

	private Float operande1;
	
	private Float operande2;

	public Operation(){
		operande1 = null;
		operande2 = null;
	}
	
	public Operation(Float op1, Float op2){
		operande1 = op1;
		operande2 = op2;
	}
	
	public Float getOperande1() {
		return operande1;
	}

	public void setOperande1(Float operande1) {
		this.operande1 = operande1;
	}

	public Float getOperande2() {
		return operande2;
	}

	public void setOperande2(Float operande2) {
		this.operande2 = operande2;
	}

	public boolean hasMissingOperand(){
		return (getOperande1()==null || getOperande2()==null);
	}
	
	public void setMissingOperand(Float a){
		if(hasMissingOperand()){
			if(getOperande1()==null)
				setOperande1(a);
			else 
				setOperande2(a);			
		}
	}
	
}
