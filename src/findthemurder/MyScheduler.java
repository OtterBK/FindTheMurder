package findthemurder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

public class MyScheduler {
	
	public static List<MyScheduler> debugList = new ArrayList<MyScheduler>();
	
	public int schId = -1;
	public int schTime;
	public int schTime2;
	public Minigame minigame;
	
	public MyScheduler(Minigame game) {
		debugList.add(this);
		minigame = game;
		if(minigame != null) minigame.schList.add(this);
	}
	
	public void addSchList(Minigame game) {
		minigame = game;
		if(minigame != null) minigame.schList.add(this);
	}
	
	public boolean cancelTask(boolean removeList) {
		if(schId == -1) return false;
		Bukkit.getScheduler().cancelTask(schId);
		if(removeList && minigame != null)minigame.schList.remove(this);
		schId = -1;
		return true;
	}
}
