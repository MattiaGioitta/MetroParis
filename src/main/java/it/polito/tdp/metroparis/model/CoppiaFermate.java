package it.polito.tdp.metroparis.model;

public class CoppiaFermate {
	
	private Fermata fa;
	private Fermata fp;
	/**
	 * @param fa
	 * @param fp
	 */
	public CoppiaFermate(Fermata fa, Fermata fp) {
		super();
		this.fa = fa;
		this.fp = fp;
	}
	/**
	 * @return the fa
	 */
	public Fermata getFa() {
		return fa;
	}
	/**
	 * @param fa the fa to set
	 */
	public void setFa(Fermata fa) {
		this.fa = fa;
	}
	/**
	 * @return the fp
	 */
	public Fermata getFp() {
		return fp;
	}
	/**
	 * @param fp the fp to set
	 */
	public void setFp(Fermata fp) {
		this.fp = fp;
	}
	

}
