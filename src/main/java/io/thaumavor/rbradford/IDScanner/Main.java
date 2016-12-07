package io.thaumavor.rbradford.IDScanner;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

import javax.swing.*;

@SuppressWarnings("InfiniteLoopStatement")
public class Main {

	// Files = IDS/Names (new line = new ID)
	// Files = CSV Days

	ArrayList<String> ids;
	ArrayList<String> coordinatingNames;
	private Scanner peopleScanner;
	private PrintWriter peoplePrinter;

	public static void main(String[] args) {
		initUpdateChecker("1.05");
		Main main = new Main();
		main.start();
	}

	private static void initUpdateChecker(String ver) {
		try {
			new Update("http://rbradford.thaumavor.io/jars/IDScanner/", "IDScanner", "index.txt", ver);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void doSystemExit(int status){
		System.out.println("Robots don't quit... \nBut this isn't a Robot!");
		System.exit(status);
	}

	private String promptUserID(){
		String id = JOptionPane.showInputDialog("Enter an ID");
		if (id == null){
			doSystemExit(0); // User clicked 'Cancel'
		}
		if (id.equals("")){
			promptUserID(); // We don't accept Empty Strings, try again
		}
		return id;
	}

	private void start(){
		try {
			ids = new ArrayList<>();
			coordinatingNames = new ArrayList<>();
			String currentDate = "";
			File todaysFile = null;
			File peopleData = new File(System.getProperty("user.dir") + "/people.txt");
			if (!peopleData.exists()) {
				peopleData.createNewFile();
			}
			peopleScanner = new Scanner(peopleData);
			while(peopleScanner.hasNext()) {
				String nextLine = peopleScanner.nextLine();
				ids.add(nextLine.split(",")[0]);
				coordinatingNames.add(nextLine.split(",")[1]);
			}
			while (true) {
				FileWriter peopleWriter = new FileWriter(peopleData, true);
				peoplePrinter = new PrintWriter(peopleWriter);

				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				String today = dateFormat.format(new Date());
				if(!currentDate.equals(today)) {
					todaysFile = new File(System.getProperty("user.dir") + "/" + today + ".txt");
				}

				if (!todaysFile.exists()) {
					todaysFile.createNewFile();
				}
				FileWriter writeToday = new FileWriter(todaysFile, true);
				PrintWriter attendance = new PrintWriter(writeToday);
				String UID = promptUserID(); // Person's (usually numerical) ID
				if (!ids.contains(UID)) {
					ids.add(UID);
					coordinatingNames.add(JOptionPane.showInputDialog("What is your name?")); // Ask for Name
					peoplePrinter.println(UID + "," + coordinatingNames.get(coordinatingNames.size() - 1));
				}
				int index = 0;
				for(int i = 0; i < ids.size(); i++) {
					if(ids.get(i).equals(UID)) {
						index = i;
					}
				}
				attendance.println(coordinatingNames.get(index));

				attendance.flush();
                attendance.close();
                peopleWriter.flush();
                peopleWriter.close();

				JOptionPane.showMessageDialog(null, "Thanks for coming, " + coordinatingNames.get(index) + "!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		doSystemExit(0);
	}
}
