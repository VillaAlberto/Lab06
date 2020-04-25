package it.polito.tdp.meteo.model;

import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.meteo.DAO.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO DAO = new MeteoDAO();
	private List<Rilevamento> bestSoluzione;
	private int bestPunteggio;
	int counter = 0;

	public Model() {
		bestSoluzione = null;
		bestPunteggio = 2000;

	}

	// of course you can change the String output with what you think works best
	public String getUmiditaMedia(int mese) {
		String result = "";

		for (String localita : DAO.getAllLocalita()) {
			double media = calcolaMedia(DAO.getAllRilevamentiLocalitaMese(mese, localita));
			result += String.format("%s %30.2f%%\n", localita, media);
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

		List<Citta> listaCitta = new LinkedList<Citta>();
		List<Rilevamento> parziale = new LinkedList<Rilevamento>();
		int livello = 0;
		// Ottengo una lista con tutte le città e per ognuna NUMERO_GIORNI_TOTALI di
		// rilevazioni
		for (String localita : DAO.getAllLocalita()) {
			listaCitta.add(
					new Citta(localita, DAO.getQuindiciRilevamentiLocalitaMese(mese, localita, NUMERO_GIORNI_TOTALI)));
		}

		ricorsiva(livello, parziale, listaCitta);
		String result = "";
		for (Rilevamento r : bestSoluzione) {
			result += r.getLocalita() + "\n";
		}

		return result;
	}

	private void ricorsiva(int livello, List<Rilevamento> parziale, List<Citta> listaCitta) {

		for (Citta c : listaCitta) {
			if (c.getCounter() > NUMERO_GIORNI_CITTA_MAX) {
				return;
			}
		}

		int contatore = 0;
		for (Rilevamento r : parziale) {
			if (parziale.indexOf(r)>1&&r.getLocalita().compareTo(parziale.get(parziale.indexOf(r)-1).getLocalita()) != 0) {
				contatore = 0;
			} else {
				contatore++;
			}
			if (contatore == NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN) {
				return;
			}
		}

		if (livello == NUMERO_GIORNI_TOTALI) {
			System.out.println(counter++);
			int costo = calcolaCosto(parziale);
			if (costo < bestPunteggio) {
				bestSoluzione = new LinkedList<Rilevamento>(parziale);
				bestPunteggio = costo;
			}
			return;
		}

		for (Citta c : listaCitta) {
			parziale.add(c.getRilevamenti().get(livello));
			c.increaseCounter();
			ricorsiva(livello + 1, parziale, listaCitta);
			// backtracking

			parziale.remove(parziale.size() - 1);
			c.setCounter(c.getCounter() - 1);
		}
	}

	private int calcolaCosto(List<Rilevamento> parziale) {
		int costo = 0;
		for (Rilevamento r : parziale) {
			costo += r.getUmidita();
			if (parziale.indexOf(r)>1&&r.getLocalita().compareTo(parziale.get(parziale.indexOf(r)-1).getLocalita()) != 0) {
				costo += COST;
			}
		}

		return costo;
	}

}
