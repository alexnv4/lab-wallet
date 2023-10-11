package ru.alexnv.apps.wallet.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Аудит всех действий игрока
 */
public class Auditor {
	
	private List<Action> actions;
	
	public Auditor() {
		actions = new ArrayList<>();
	}
	
	public void addAction(Action action) {
		actions.add(action);
	}
	
	public List<Action> getActions() {
		return actions;
	}

}
