package procesamiento;

import java.util.ArrayList;

import datos.*;
import vectores.Vector;

public class Normalizacion implements Preprocesado{
	
	public ArrayList<Atributo> Procesar(Dataset datos) {
		ArrayList<Atributo> nuevos = new ArrayList<Atributo>(datos.getAtributos());
		Cuantitativo ejemplo = new Cuantitativo();
		for (int i = 0; i < nuevos.size(); i++) {
			if (nuevos.get(i).getClass() == ejemplo.getClass()) {
				ejemplo = (Cuantitativo) nuevos.get(i);
				Vector valores = ejemplo.getValores();
				valores.normalize();
				ejemplo.setValores(valores);
				nuevos.set(i,ejemplo);
			}
		}
		return nuevos;
	}	
	
}
