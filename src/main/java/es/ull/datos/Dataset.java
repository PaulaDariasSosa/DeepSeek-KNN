package datos;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Dataset {
	private List<Atributo> atributos;
	int preprocesado;
	
	public Dataset() {
		this.atributos = new ArrayList<Atributo>();
	}
	
	public Dataset(List<Atributo> nuevos) {
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

	/**
	 * Cambia el peso de todos los atributos en el dataset.
	 *
	 * @param pesos Una lista de pesos (en formato String) que se asignarán a los atributos.
	 * @throws IllegalArgumentException Si la lista de pesos no tiene el mismo tamaño que la lista de atributos,
	 *                                  o si algún peso no es un número válido o está fuera del rango permitido.
	 */
	public void cambiarPeso(List<String> pesos) {
		if (pesos.size() != atributos.size()) {
			throw new IllegalArgumentException("El número de pesos para asignar debe ser igual al número de atributos");
		}
		for (int i = 0; i < pesos.size(); i++) {
			Atributo aux = atributos.get(i);
			aux.setPeso(Double.parseDouble(pesos.get(i)));
			this.atributos.set(i, aux);
		}
	}


	// Cambiar peso para uno
	public void CambiarPeso(int index, double peso) {
		Atributo aux = this.atributos.get(index);
		aux.setPeso(peso);
		this.atributos.set(index, aux);
	}

	/**
	 * Cambia el peso de todos los atributos en el dataset.
	 *
	 * @param peso El nuevo peso que se asignará a todos los atributos.
	 * @throws IllegalArgumentException Si el peso no es válido (por ejemplo, fuera de un rango específico).
	 */
	public void CambiarPeso(double peso) {
		// Validar que el peso esté en un rango válido (opcional)
		if (peso < 0 || peso > 1) {
			throw new IllegalArgumentException("El peso debe estar entre 0 y 1.");
		}

		// Cambiar el peso de todos los atributos
		for (Atributo atributo : atributos) {
			atributo.setPeso(peso);
		}
	}
	
	// Print
	public void print() {
		Logger logger = Logger.getLogger(Dataset.class.getName());
		logger.info(this.toString());
	}
	
	// toString
	public String toString() {
		StringBuilder data = new StringBuilder();
		List<String> valores = this.nombreAtributos();
		valores.addAll(this.getValores());
		int contador = 1;
		for (int i = 0; i < valores.size(); ++i) {
			data.append(valores.get(i));
			if (contador == this.numeroAtributos()) {
				data.append("\n");
				contador = 1;
			} else {
				data.append(",");
				++contador;
			}
		}
		return data.toString();
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
	
	public void add(List<String> nueva) {
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
	public int numeroAtributos() {
		return atributos.size();
	}
	
	// nombre atributos
	public List<String> nombreAtributos(){
		ArrayList<String> nombres = new ArrayList<>();
		for(int i = 0; i < atributos.size(); ++i) nombres.add(atributos.get(i).getNombre());
		return nombres;
	}
	
	public List<Atributo> getAtributos(){
		return atributos;
	}
	
	public List<Atributo> getAtributosEmpty() {
		List<Atributo> aux = new ArrayList<Atributo> (atributos.size());
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
	public int numeroCasos() {
		return atributos.get(0).size();
	}

	public List<String> getValores(){
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
		return new Instancia (auxiliar);
	}
	
	public List<String> getPesos() {
		ArrayList<String> valores = new ArrayList<String>();
		for (Atributo valor : this.atributos) valores.add(valor.get());
		return valores;
	}
	
	public List<String> getClases() {
		return ((Cualitativo) this.atributos.get(atributos.size()-1)).clases();
	}
	
	public int getPreprocesado() {
		return preprocesado;
	}
	
	public void setPreprocesado(int opcion) {
		this.preprocesado = opcion;
	}
	
	public void setAtributos(List<Atributo> nuevos) {
		this.atributos = nuevos;
	}
	
	public Dataset copiar() {
		Dataset copia = new Dataset();
	    // Realizar una copia profunda de los elementos de la lista
		ArrayList<Atributo> copiaAtributos = new ArrayList<>();
	    for (Atributo atributo : this.atributos) {
	        copiaAtributos.add(atributo.copiar());
	    }
	    copia.setAtributos(copiaAtributos);
	    return copia;
	}
}