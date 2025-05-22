package procesamiento;

import java.util.List;

import datos.Atributo;
import datos.Cuantitativo;
import datos.Dataset;

public class Estandarizacion implements Preprocesado{

	public List<Atributo> procesar(Dataset datos) {
		List<Atributo> nuevos = datos.getAtributos();
		for (int i = 0; i < nuevos.size(); i++) {
			if (nuevos.get(i) instanceof Cuantitativo) {
				Cuantitativo atributo = (Cuantitativo) nuevos.get(i);
				// No estandarizar si solo hay un valor o todos iguales
				if (atributo.size() > 1 && atributo.desviacion() > 0) {
					atributo.estandarizacion();
					nuevos.set(i, atributo);
				}
			}
		}
		return nuevos;
	}
	
}
