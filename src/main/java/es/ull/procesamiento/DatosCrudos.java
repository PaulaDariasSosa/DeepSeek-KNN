package procesamiento;

import java.util.List;

import datos.*;

public class DatosCrudos implements Preprocesado{

	public List<Atributo> Procesar(Dataset datos) {
		return datos.getAtributos();
	}
}
