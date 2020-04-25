package it.polito.tdp.meteo.model;

import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;

	private List<Rilevamento> bestSoluzione;
	private int bestPunteggio;

	private MeteoDAO DAO;
	int counter = 0;

	public Model() {
		DAO = new MeteoDAO();
		bestSoluzione = null;
		bestPunteggio = 2000;
	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		String result = "";
		for (String localita : DAO.getAllLocalita()) {
			double media = calcolaMedia(DAO.getAllRilevamentiLocalitaMese(mese, localita));
			result += String.format("%-10s %5.2f%%\n", localita, media);
		}
		return result;
	}

	private double calcolaMedia(List<Rilevamento> allRilevamentiLocalitaMese) {
		double somma = 0.0;
		for (Rilevamento r : allRilevamentiLocalitaMese) {
			somma += r.getUmidita();
		}
		return somma / allRilevamentiLocalitaMese.size();
	}

	// of course you can change the String output with what you think works best
	public String trovaSequenza(int mese) {

		// Per ogni città ho i suoi 15 rilevamenti
		List<Citta> listaCitta = new LinkedList<Citta>();
		for (String localita : DAO.getAllLocalita()) {
			listaCitta.add(
					new Citta(localita, DAO.getQuindiciRilevamentiLocalitaMese(mese, localita, NUMERO_GIORNI_TOTALI)));
		}

		List<Rilevamento> parziale = new LinkedList<Rilevamento>();
		int livello = 0;

		ricorsiva(livello, parziale, listaCitta);

		String result = "";
		System.out.println(bestPunteggio);
		for (Rilevamento r : bestSoluzione) {
			result += r.getLocalita() + r.getUmidita() + "\n";
		}

		return result;
	}

	private void ricorsiva(int livello, List<Rilevamento> parziale, List<Citta> listaCitta) {

		// casi terminali
		for (Citta c : listaCitta) {
			if (c.getCounter() > NUMERO_GIORNI_CITTA_MAX)
				return;
		}

		// condizione terminale
		if (livello >= NUMERO_GIORNI_TOTALI) {

			if (AlmenoGiorniMin(parziale))
			// migliore della bestSoluzione?
			{
//				System.out.println(counter++);
//				System.out.println(parziale);
				int costo = calcolaCosto(parziale);
				if (costo < bestPunteggio) {
					bestSoluzione = new LinkedList<Rilevamento>(parziale);
					bestPunteggio = costo;
				}
			}
			return;
		}

		// generiamo i sotto-problemi
		for (Citta c : listaCitta) {
			parziale.add(c.getRilevamenti().get(livello));
			c.increaseCounter();
			ricorsiva(livello + 1, parziale, listaCitta);
			parziale.remove(parziale.size() - 1);
			c.setCounter(c.getCounter() - 1);
		}

	}

	private boolean AlmenoGiorniMin(List<Rilevamento> parziale) {
		// Se ne trova 1 o 2 da soli rimanda false

		for (int i = 0; i < NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN; i++) {
			if (parziale.get(0).getLocalita().compareTo(parziale.get(i).getLocalita()) != 0
					|| !parziale.get(NUMERO_GIORNI_TOTALI - 1).equals(parziale.get(NUMERO_GIORNI_TOTALI - 1 - i)))
				return false;
		}

		for (int i = 2; i < NUMERO_GIORNI_TOTALI - 1; i++) {
			if (!parziale.get(i + 1).equals(parziale.get(i))) {
				if (!parziale.get(i).equals(parziale.get(i - 1)) || !parziale.get(i).equals(parziale.get(i - 2)))
					return false;
			}
		}

		return true;
	}

	private int calcolaCosto(List<Rilevamento> parziale) {
		int costo = 0;
		// citta lastCity = citta[0]
//		for (Rilevamento r : parziale) {
//			costo += r.getUmidita();
//			// if citta[i] != lastCity {costo+=COST} else {lastCity = citta[i]};
//			if (parziale.indexOf(r) > 0 && !r.equals(parziale.get(parziale.indexOf(r)-1))) {
//				costo += COST;
//			}
//		}
		for (int i = 0; i < NUMERO_GIORNI_TOTALI; i++) {
			costo += parziale.get(i).getUmidita();
			if (i != 0 && !parziale.get(i).equals(parziale.get(i - 1)))
				costo += COST;
		}
		return costo;
	}

}