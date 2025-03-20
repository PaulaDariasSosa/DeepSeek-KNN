package procesamiento;
import java.util.ArrayList;

import datos.Atributo;
import datos.Dataset;

public interface Preprocesado {
	
	public ArrayList<Atributo> Procesar(Dataset datos);
}
