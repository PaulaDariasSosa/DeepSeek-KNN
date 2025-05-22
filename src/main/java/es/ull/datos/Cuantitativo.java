package datos;

import vectores.Vector;

public class Cuantitativo extends Atributo{
	private Vector valores;
	
	public Cuantitativo() {
		this.nombre = "";
		this.valores = new Vector();
	}
	
	public Cuantitativo(String name) {
		this();
		this.nombre = name;
	}
	
	public Cuantitativo(String name, Double valor) {
		this();
		this.nombre = name;
		valores.add(valor);
	}
	
	public Cuantitativo(String name, Vector valor) {
		this();
		this.nombre = name;
		this.valores = valor;
	}
	
	
	public Vector getValores() {
		return this.valores;
	}
	
	public void setValores(Vector nuevos) {
		this.valores = nuevos;
	}

	public double minimo() {
		return valores.getMin(); // Usamos el método existente de Vector
	}

	public double maximo() {
		return valores.getMax(); // Usamos el método existente de Vector
	}

	public double media() {
		return valores.avg(); // Usamos el método existente de Vector
	}

	public double desviacion() {
		if (valores.size() <= 1) return 0.0;

		double media = this.media();
		double sumaCuadrados = 0.0;

		for (int i = 0; i < valores.size(); i++) {
			sumaCuadrados += Math.pow(valores.get(i) - media, 2);
		}

		return Math.sqrt(sumaCuadrados / (valores.size() - 1)); // Desviación muestral
	}

	public void estandarizacion() {
		if (valores.size() <= 1) return;

		double media = this.media();
		double desviacion = this.desviacion();

		for (int i = 0; i < valores.size(); i++) {
			double valorEstandarizado = (valores.get(i) - media) / desviacion;
			valores.set(i, valorEstandarizado);
		}
	}

	@Override
	public void add(Object valor) {
		if (valor instanceof Number) {
			valores.add(((Number)valor).doubleValue());
		} else {
			throw new IllegalArgumentException("El valor debe ser numérico");
		}
	}

	public int size() {
		return this.valores.size();
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
	public Cuantitativo copiar() {
		return new Cuantitativo(this.nombre, this.valores.copiar());
	}
}
