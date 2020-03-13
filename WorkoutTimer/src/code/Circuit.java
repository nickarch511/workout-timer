package code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Circuit {

	private List<Exercise> circuit;
	
	public Circuit() {
		circuit = new ArrayList<>();
	}
	
	public void addExercise(Exercise e) {
		circuit.add(e);
	}
	
	public void removeExercise(Exercise e) {
		if (!circuit.contains(e)) {
			return;
		}
		circuit.remove(e);
	}
	
	public void removeLastExercise() {
		circuit.remove(this.getCircuit().size()-1);
	}
	
	public ArrayList<Exercise> getCircuit() {
		return (ArrayList<Exercise>) circuit;
	}
	
	public void sortList() {
		Collections.sort(circuit);
	}
	
	@Override
	public String toString() {
		String retString = "";
		int counter = 1;
		for (Exercise e : circuit) {
			retString += e.toString();
			retString += "   ";
			if (counter >= 3) {
				counter = 0; 
				retString += "\n";
			}
			counter++;
		}
		return retString;
	}
	
	public int getTotalTime() {
		int total = 0;
		for (Exercise e : circuit) {
			total+= e.getLength();
		}
		return total;
	}

	
	
	
	
	
}
