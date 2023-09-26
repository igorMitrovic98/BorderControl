package com.example.pj23_1188_17.controller;
import com.example.pj23_1188_17.Run;
import com.example.pj23_1188_17.model.BorderSimulation;
import com.example.pj23_1188_17.model.terminals.Terminal;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.logging.Level;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatcherController{

        public void check() {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                //path to directory , not a specific file!!!!!!!!
                Path filePath = Paths.get(System.getProperty("user.dir") + File.separator + "generatedFiles");
                filePath.register(watchService, ENTRY_MODIFY);
                    while (BorderSimulation.moreVehiclesforCustomsTerminal()) {
                        WatchKey key;
                        try {

                        key = watchService.take();

                } catch (InterruptedException exception) {return;}//if not return , then key is not initialized!! nullpointerexception
                        for(WatchEvent<?> event: key.pollEvents()){
                            WatchEvent<Path> occurred = (WatchEvent<Path>) event;
                            Path occurredName = occurred.context();
                            System.out.println(occurredName + "  "+event.kind().toString()); //this works
                            if("terminalState.txt".equals(occurredName.toString()) && event.kind().equals(ENTRY_MODIFY)) {
                                //System.out.println("DOES IT GOES PAST THIS POINT"); works
                                List<String> fileContent = Files.readAllLines(Paths.get(System.getProperty("user.dir")+File.separator+"generatedFiles"+File.separator+"terminalState.txt"));
                                int terminalID;
                                char tmp;
                                for(String string : fileContent){
                                    System.out.println(string);
                                    if(string.contains(":")) {
                                        int index = string.indexOf(':');
                                        if (index != -1 && index + 1 < string.length()) {
                                            terminalID = string.charAt(index + 1) - '0'; // need to subtract zero so I can get integer value
                                            System.out.println("THIS IS THE FILE TERMINAL ID: " + terminalID);
                                            tmp = string.charAt(string.length() - 1);
                                            System.out.println("AND THIS IS ITS STATE: " + tmp);
                                            if (tmp == '1') {
                                                terminalRunning(terminalID);
                                            } else if (tmp == '0') {
                                                terminalStandby(terminalID);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        //need some kinda interupt here , cant start the simulation otherwise.... FIXED
                        boolean tmp = key.reset();
                        if (!tmp) break;

            }
            }catch (IOException exception){
                Run.logger.log(Level.SEVERE,exception.fillInStackTrace().toString());


            }
        }

    void terminalRunning(int terminalID){ //pauseInserted resource made just for this purpose
            System.out.println("FILE WATCHER DOING CONTINUE");
            for (Terminal terminal:BorderSimulation.terminals){
                if(terminal.getTerminalID() == terminalID){
                    if(terminal.isPause()){
                        terminal.setPause(false);
                        synchronized (terminal.pauseInserted){
                            terminal.pauseInserted.notify();
                        }
                    }
                }
            }
    }
    void terminalStandby(int terminalID){
        System.out.println("FILE WATCHER DOING PAUSE");
        for(Terminal terminal:BorderSimulation.terminals){
            if(terminal.getTerminalID() == terminalID){
                if(!terminal.isPause()){
                    terminal.setPause(true);
                }
            }
        }
    }


}



