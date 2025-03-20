package procesamiento;

import java.util.ArrayList;

import datos.*;

public class DatosCrudos implements Preprocesado{

	public ArrayList<Atributo> Procesar(Dataset datos) {
		return datos.getAtributos();
	}
}
