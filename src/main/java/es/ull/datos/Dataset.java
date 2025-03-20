package datos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Dataset {
	private ArrayList<Atributo> atributos;
	int preprocesado;
	
	public Dataset() {
		this.atributos = new ArrayList<Atributo>();
	}
	
	public Dataset(ArrayList<Atributo> nuevos) {
		this();
		this.atributos = nuevos;
	}
	
	public Dataset(String filename) throws IOException {
		this();
		this.read(filename);
	}
	
	public Dataset(Dataset datos) {
		this();
		this.atributos = new ArrayList<>(datos.atributos);
	}
	
	// Cambiar Peso para todos
	public void CambiarPeso(ArrayList<String> arrayList) {
		if ( arrayList.size() != atributos.size()) throw new IllegalArgumentException("El número de pesos para asignar debe ser igual al número de atributos");
        for (int i = 0; i <  arrayList.size(); i++) {
        	Atributo aux = atributos.get(i);
        	aux.setPeso(Double.parseDouble(arrayList.get(i)));
        	this.atributos.set(i, aux);
        }
	}
	
	// Cambiar peso para uno
	public void CambiarPeso(int index, double peso) {
		Atributo aux = this.atributos.get(index);
		aux.setPeso(peso);
		this.atributos.set(index, aux);
	}
	
	// Cambiar mismo Peso para todos
	public void CambiarPeso(double peso) {
	       for (int i = 0; i <  atributos.size(); i++) {
	        Atributo aux = atributos.get(i);
	        aux.setPeso(peso);
	        this.atributos.set(i, aux);
	       }
	}
	
	// Print
	public void print() {
		System.out.println(this.toString());
	}
	
	// toString
	public String toString() {
		String data = "";
		ArrayList<String> valores = this.NombreAtributos();
		valores.addAll(this.getValores());
		int contador = 1;
		for (int i = 0; i < valores.size(); ++i) { 
			data += valores.get(i);
			if (contador == this.NumeroAtributos()) {
				data += "\n";
				contador = 1;
			} else {
				data += ",";
				++contador;
			}
		}
	    return data;
	}
	
	// Modify (mezcla de add y delete)
	// Add instancia 
	public void add(Instancia nueva) {
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux =  atributos.get(i);
			aux.add(nueva.getValores().get(i));
			atributos.set(i, aux);
		}	
	}
	
	public void add(ArrayList<String> nueva) {
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux =  atributos.get(i);
			try {
				aux.add(Double.parseDouble(nueva.get(i)));
    		} catch (NumberFormatException e) {
    			aux.add(nueva.get(i));
    		}
			atributos.set(i, aux);
		}	
	}
	// Delete
	public void delete(int nueva) {
		//atributos.forEach(atributo -> atributo.delete(nueva));
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo aux = atributos.get(i);
			aux.delete(nueva);
			atributos.set(i, aux);
		}
	}
	
	// Método para escribir el dataset en un archivo CSV
    public void write(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write(this.toString());
        }
    }
	
	public void read(String filename) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            // Leer la primera línea para obtener los nombres de los atributos
			// llamar al constructor vacio
            String[] attributeNamesArray = reader.readLine().split(",");
            String line;
            if ((line = reader.readLine()) != null) {
            	String[] values = line.split(",");
            	for (int i = 0; i < attributeNamesArray.length ; ++i) {
            		try {
            			this.atributos.add(new Cuantitativo(attributeNamesArray[i], Double.parseDouble(values[i]))); // sino poner encima Double.parseDouble(values[i])
            		} catch (NumberFormatException e) {
            			this.atributos.add(new Cualitativo(attributeNamesArray[i], values[i]));
            		}
            	}
            }
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                for (int i = 0; i < attributeNamesArray.length ; ++i) {
                	Atributo nuevo = this.atributos.get(i);
            		try {
            			nuevo.add(Double.parseDouble(values[i]));
            		} catch (NumberFormatException e) {
            			nuevo.add(values[i]);
            		}
            		this.atributos.set(i, nuevo);
            	}
            }
        }
	}
	
	// numero atributos
	public int NumeroAtributos() {
		return atributos.size();
	}
	
	// nombre atributos
	public ArrayList<String> NombreAtributos(){
		ArrayList<String> nombres = new ArrayList<>();
		for(int i = 0; i < atributos.size(); ++i) nombres.add(atributos.get(i).getNombre());
		return nombres;
	}
	
	public ArrayList<Atributo> getAtributos(){
		return atributos;
	}
	
	public ArrayList<Atributo> getAtributosEmpty() {
		ArrayList<Atributo> aux = new ArrayList<Atributo> (atributos.size());
		for (int i = 0; i < atributos.size(); ++i) {
			try {
				Cualitativo auxiliar = (Cualitativo) atributos.get(i);
				aux.add(new Cualitativo(auxiliar.getNombre()));
			} catch (ClassCastException e) {
				Cuantitativo auxiliar = (Cuantitativo) atributos.get(i);
				aux.add(new Cuantitativo(auxiliar.getNombre()));
			}
		}
		for (int i = 0; i < atributos.size(); ++i) {
			Atributo prov = aux.get(i);
			prov.setPeso(atributos.get(i).getPeso());
			aux.set(i, prov);
		}
		return aux;
	}
	
	// numero casos
	public int NumeroCasos() {
		return atributos.get(0).size();
	}

	public ArrayList<String> getValores(){
		ArrayList<String> valores = new ArrayList<String>();
		 for (int i = 0; i < atributos.get(0).size(); ++i) {
	        	for (int j = 0; j < atributos.size(); ++j) valores.add(String.valueOf(atributos.get(j).getValor(i)));
		}
		return valores;
	}
	
	public Atributo get(int index) {
		return atributos.get(index);
	}
	
	public Instancia getInstance(int index){
	 	ArrayList<Object> auxiliar = new ArrayList<>();
		for (int i = 0; i < atributos.size(); ++i) auxiliar.add(atributos.get(i).getValor(index));
		Instancia nuevo = new Instancia (auxiliar);
		return nuevo;
	}
	
	public ArrayList<String> getPesos() {
		ArrayList<String> valores = new ArrayList<String>();
		for (Atributo valor : this.atributos) valores.add(valor.get());
		return valores;
	}
	
	public ArrayList<String> getClases() {
		return ((Cualitativo) this.atributos.get(atributos.size()-1)).clases();
	}
	
	public int getPreprocesado() {
		return preprocesado;
	}
	
	public void setPreprocesado(int opcion) {
		this.preprocesado = opcion;
	}
	
	public void setAtributos(ArrayList<Atributo> nuevos) {
		this.atributos = nuevos;
	}
	
	public Dataset clone() {
		Dataset copia = new Dataset();
	    // Realizar una copia profunda de los elementos de la lista
		ArrayList<Atributo> copiaAtributos = new ArrayList<>();
	    for (Atributo atributo : this.atributos) {
	        copiaAtributos.add(atributo.clone());
	    }
	    copia.setAtributos(copiaAtributos);
	    return copia;
	}
}