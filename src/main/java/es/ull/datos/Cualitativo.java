package datos;

import java.util.ArrayList;

//import vectores.Vector;

public class Cualitativo extends Atributo{
	private ArrayList<String> valores;
	
	public Cualitativo() {
		this.nombre = "";
		this.valores = new ArrayList<String>();
	}
	
	public Cualitativo(String name) {
		this();
		this.nombre = name;
	}
	
	public Cualitativo(String name, String valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}
	
	public Cualitativo(String name, ArrayList<String> valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}
	
	public ArrayList<String> getValores() {
		return this.valores;
	}
	
	public void setValores(ArrayList<String> nuevos) {
		this.valores = nuevos;
	}
	
	public ArrayList<String> clases() {
		ArrayList<String> clases = new ArrayList<>();
		for(int i = 0; i < this.valores.size(); ++i) {
			if(!clases.contains(this.valores.get(i))) clases.add(this.valores.get(i));
		}
		return clases;
	}
	
	public int nClases() {
		return this.clases().size();
	}
	
	public ArrayList<Double> frecuencia() {
		ArrayList<String> clases = this.clases();
		ArrayList<Double> frecuencias = new ArrayList<>();
		for (int j = 0; j < this.nClases(); ++j) {
			double auxiliar = 0;
			for(int i = 0; i < this.valores.size(); ++i) {
				if(clases.get(j).equals(this.valores.get(i))) auxiliar++;
			}
			frecuencias.add(auxiliar/this.valores.size());
		}
		return frecuencias;
	}
	
	public int size() {
		return this.valores.size();
	}
	
	@Override
	public void add(Object valor) {
		valores.add((String) valor);
	}
	
	@Override
	public Object getValor(int i) {
		return valores.get(i);
		
	}
	
	@Override
	public void delete(int index) {
		valores.remove(index);
	}
	
	@Override
	public String toString() {
		return valores.toString();
		
	}
	
	@Override
	public void clear() {
		valores.clear();
	}
	
	@Override
	public Cualitativo clone() {
		return new Cualitativo(new String (this.nombre), new ArrayList<String>(this.valores));
	}
}
