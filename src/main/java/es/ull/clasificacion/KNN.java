package clasificacion;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.util.stream.IntStream;

import datos.*;
import vectores.Vector;

public class KNN {
  private int vecinos;
  
  public KNN (int k) {
	  this.vecinos = k;
  }

	public Vector getDistancias(Dataset datos, Instancia nueva) {
		// Convertir pesos a array primitivo para mejor performance
		double[] pesosArray = datos.getAtributos().stream()
				.mapToDouble(Atributo::getPeso)
				.toArray();

		// Paralelización del cálculo de distancias
		return IntStream.range(0, datos.numeroCasos())
				.parallel()
				.mapToObj(i -> {
					Instancia instancia = datos.getInstance(i);
					return getDistanciaEuclidea(
							instancia.getVector(),
							nueva.getVector(),
							pesosArray
					);
				})
				.collect(Vector::new, Vector::add, Vector::concat);
	}
  
  public String getClase (List<Instancia> candidatos) {
	  List<String> nombresClases = new ArrayList<>();
	  for (int i = 0; i < candidatos.size(); i++) {
		  if (!nombresClases.contains(candidatos.get(i).getClase())) nombresClases.add(candidatos.get(i).getClase());
	  }
	  List<Integer> numeroClases = new ArrayList<>();
	  for (int i = 0; i < nombresClases.size(); i++) {
		  int aux = 0;
		  for (int j = 0; j < candidatos.size();++j) {
			  if (candidatos.get(j).getClase().equals(nombresClases.get(i))) aux += 1;
		  }
		  numeroClases.add(aux);
	  }
	  return nombresClases.get(numeroClases.indexOf(Collections.max(numeroClases)));
			  
  }
  
  public double getDistanciaEuclidea(Vector vieja, Vector nueva) {
	  if (vieja == null || nueva == null) {
		  throw new IllegalArgumentException("Los vectores no pueden ser nulos");
	  }
	  if (vieja.size() != nueva.size()) {
		  throw new IllegalArgumentException(
				  String.format("Tamaños de vectores no coinciden (%d != %d)",
						  vieja.size(), nueva.size())
		  );
	  }
	double dist = 0.0;
	for(int i = 0; i < nueva.size(); i++) {
		dist += Math.pow((vieja.get(i) - nueva.get(i)), 2);
	}
	return Math.sqrt(dist);
  }

	public double getDistanciaEuclidea(Vector vieja, Vector nueva, double[] pesos) {
		if (pesos == null) {
			throw new IllegalArgumentException("La lista de pesos no puede ser nula");
		}
		if (vieja.size()-1 != nueva.size()) {
			throw new IllegalArgumentException(
					String.format("Tamaños no coinciden (vieja: %d, nueva: %d, pesos: %d)",
							vieja.size(), nueva.size(), pesos.length)
			);
		}

		double dist = 0.0;
		for (int i = 0; i < nueva.size(); i++) {
			double diff = (vieja.get(i) - nueva.get(i)) * pesos[i];
			dist += diff * diff;
		}
		return Math.sqrt(dist);
	}
  
  public String getVecino(List<Instancia> candidatos, Vector distancias){
	  Vector aux = new Vector();
	  List<Integer> indices = new ArrayList<>();
	  for (int i = 0; i < vecinos; i++) {
		  aux.add(distancias.get(i));
		  indices.add(i);
	  }
	  // metemos los k primeros elementos en un vector
	  for (int i = 0+vecinos-1; i < candidatos.size(); ++i) {
		// si el elemento mayor del vector tiene 
		if (aux.getMax() > distancias.get(i)) {
			// sacar el mayor y meter el nuevo
			aux.set(aux.getMaxInt(), distancias.get(i));
			indices.set(aux.getMaxInt(), i);
		}
	  }
	  List<Instancia> elegidos = new ArrayList<>();
	  for (int i = 0; i < indices.size(); i++) elegidos.add(candidatos.get(indices.get(i)));
	  return this.getClase(elegidos);
  }
  
  public String clasificar(Dataset datos, Instancia nueva) {
	  Vector aux = this.getDistancias(datos, nueva);
	  List<Instancia> elegidos = new ArrayList<>();
	  for (int i = 0; i < datos.numeroCasos(); ++i) {
	    elegidos.add(datos.getInstance(i));
	  }
	  return this.getVecino(elegidos, aux);
  }
}
