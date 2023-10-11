package ru.alexnv.apps.wallet.service;

import java.util.Date;

import ru.alexnv.apps.wallet.domain.model.Player;

public class Action {
	
	private Player player;
	private String description;
	private final Date date = new Date();
	
	public Action(Player player, String description) {
		super();
		this.player = player;
		this.description = description;
	}

}
