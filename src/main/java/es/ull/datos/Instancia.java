package datos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import vectores.Vector;

public class Instancia {
	private List<Object> valores;
	
	public Instancia(){
		this.valores = new ArrayList<Object>();
	}

	public Instancia(List<Object> nuevos){
		this.valores = new ArrayList<>(nuevos); // Crear nueva lista mutable
	}
	
	public Instancia(String nuevos){
		String[] subcadenas = nuevos.split(",");
		ArrayList<Object> arrayList = new ArrayList<>(Arrays.asList(subcadenas));
		this.valores = arrayList;
	}
	
	public List<Object> getValores() {
		return this.valores;
	}
	
	public String toString() {
		return valores.toString();
	}

	public Vector getVector() {
		Vector aux = new Vector();
		for (int i = 0; i < valores.size()-1; ++i) {
			Object valor = valores.get(i);
			if (valor instanceof Number) {
				aux.add(((Number)valor).doubleValue());
			}
		}
		return aux;
	}
	
	public String getClase() {
		return (String) this.valores.get(valores.size()-1);
	}

	public void normalizar() {
		Vector aux = this.getVector();
		aux.normalize();
		ArrayList<Object> arrayListObject = new ArrayList<>();
		for (Double d : aux.getValores()) {
			arrayListObject.add(d);
		}
		// Preservar la clase
		if (!valores.isEmpty()) {
			arrayListObject.add(valores.get(valores.size()-1));
		}
		this.valores = arrayListObject;
	}

	public void estandarizar() {
		Vector aux = this.getVector();
		double media = aux.avg(); // Usar método existente de Vector
		double desviacion = 0.0;

		for (int i = 0; i < aux.size(); ++i) {
			desviacion += Math.pow(aux.get(i) - media, 2);
		}
		desviacion = Math.sqrt(desviacion/aux.size());

		for (int i = 0; i < aux.size(); ++i) {
			aux.set(i, (aux.get(i)-media)/desviacion);
		}

		ArrayList<Object> arrayListObject = new ArrayList<>();
		for (Double d : aux.getValores()) {
			arrayListObject.add(d);
		}
		// Preservar la clase
		if (!valores.isEmpty()) {
			arrayListObject.add(valores.get(valores.size()-1));
		}
		this.valores = arrayListObject;
	}
	
	public void deleteClase() {
		valores.remove(valores.size() - 1);
	}

}
