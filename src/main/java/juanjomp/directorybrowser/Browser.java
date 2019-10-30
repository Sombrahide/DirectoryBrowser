/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package juanjomp.directorybrowser;

/**
 *
 * @author Juanjo
 */
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

/**
 *
 * @author Juanjo
 */
public class Browser {

    public void initBrowser() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        Scanner sc = new Scanner(System.in);
        WriteLog log = new WriteLog("log.txt", true);
        String [] entries;
        String entryText = "";
        final String dir = System.getProperty("user.dir");
        File previousDirectory = new File("");
        File currentDirectory = new File(dir);

        TreatEntry.EntryType lastEntry = null;

        File auxDirectory;
        TreatEntry TreatEntryText;
        try {
            log.writeToLog(dtf.format(LocalDateTime.now()) + "\n===================");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        while (lastEntry != TreatEntry.EntryType.EXIT) {
            System.out.print(currentDirectory.getAbsolutePath() + ">");
            entryText = sc.nextLine();
            try {
                log.writeToLog(" " + entryText);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            TreatEntryText = new TreatEntry(entryText);
            switch (TreatEntryText.obtainEntryType()) {
                case GOTO:
                    if (currentDirectory.getPath() == "") {
                        auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                    } else {
                        auxDirectory = new File(currentDirectory.getPath() + currentDirectory.separator + TreatEntryText.obtainParameters()[0]);
                    }
                    if (auxDirectory.exists() && auxDirectory.isDirectory()) {
                        previousDirectory = currentDirectory;
                        currentDirectory = auxDirectory;
                    } else {
                        System.out.println("Directorio no encontrado\n");
                    }
                    lastEntry = TreatEntry.EntryType.GOTO;
                    break;
                case GOLAST:
                    auxDirectory = currentDirectory;
                    currentDirectory = previousDirectory;
                    previousDirectory = auxDirectory;
                    lastEntry = TreatEntry.EntryType.GOLAST;
                    break;
                case LIST:
                    String[] elementsList = currentDirectory.list();
                    if (elementsList != null && elementsList.length > 0) {
                        for (String item : elementsList) {
                            System.out.println(">> " + item);
                        }
                    } else {
                        System.out.println("Directorio sin elementos\n");
                    }
                    lastEntry = TreatEntry.EntryType.LIST;
                    break;
                case UP:
                    if (currentDirectory.getAbsoluteFile().getParentFile() != null) {
                        currentDirectory = currentDirectory.getAbsoluteFile().getParentFile();
                    } else {
                        System.out.println("No se puede ir mas atras\n");
                    }
                    lastEntry = TreatEntry.EntryType.UP;
                    break;
                case INFOFILE:
                    auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                    if (auxDirectory.exists() && auxDirectory.isFile()) {
                        System.out.println("    >> Name: " + auxDirectory.getName());
                        System.out.println("    >> Last Modified: " + sdf.format(new Date(auxDirectory.lastModified())));
                    } else {
                        System.out.println("El nombre dado no existe o no es un archivo\n");
                    }
                    lastEntry = TreatEntry.EntryType.INFOFILE;
                    break;
                case INFODIR:
                    auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                    if (auxDirectory.exists() && auxDirectory.isDirectory()) {
                        System.out.println("    >> Name: " + auxDirectory.getName());
                        System.out.println("    >> Last Modified: " + sdf.format(new Date(auxDirectory.lastModified())));
                    } else {
                        System.out.println("El nombre dado no existe o no es un directorio\n");
                    }
                    lastEntry = TreatEntry.EntryType.INFODIR;
                    break;
                case HELP:
                    if (TreatEntryText.obtainParameters()[0] == null) {
                        System.out.println("Lista de comandos: \n>>goto\n>>golast\n>>list\n>>up\n>>infofile\n>>infodir\n>>help"
                                + "\n>>createdir\n>>createfile\n>>sortby\n>>deletedir\n>>deletefile\n>>exit\n");
                    } else {
                        switch (TreatEntryText.obtainParameters()[0]) {
                            case "goto":
                                System.out.println("GoTo:\n*Ir al directorio indicado.\n");
                                break;
                            case "golast":
                                System.out.println("GoLast:\n*Ir al ultimo directorio que se visito.\n");
                                break;
                            case "list":
                                System.out.println("List:\n*Listar una lista de elementos del directorio actual.\n");
                                break;
                            case "up":
                                System.out.println("Up:\n*Ir al directorio padre.\n");
                                break;
                            case "infofile":
                                System.out.println("InfoFile:\n*Informacion del fichero indicado.\n");
                                break;
                            case "infodir":
                                System.out.println("InfoDir:\n*Informacion del directorio indicado.\n");
                                break;
                            case "help":
                                System.out.println("Help:\n*Mostrar todos los comandos, en caso de indicar un parametro dar mas informacion sobre este.\n");
                                break;
                            case "createdir":
                                System.out.println("CreateDir:\n*Crear un directorio.\n");
                                break;
                            case "createfile":
                                System.out.println("CreateFile:\n*Crear un fichero.\n");
                                break;
                            case "sortby":
                                System.out.println("SortBy:\n*Mostrar el contenido del directorio indicado de forma ordenada segun el usuario.\n"
                                        + " NAME: Ordenas de forma alfabetica\n"
                                        + " DATE: Ordenas segun la ultima fecha de modificacion\n");
                                break;
                            case "deletedir":
                                System.out.println("DeleteDir:\n*Borrar el/los directorios indicados.\n");
                                break;
                            case "deletefile":
                                System.out.println("DeleteFile:\n*Borrar el/los ficheros indicados.\n");
                                break;
                            case "exit":
                                System.out.println("Exit:\n*Salir del programa.\n");
                                break;
                            default:
                                System.out.println("El comando indicado no existe\n");
                                break;
                        }
                    }
                    lastEntry = TreatEntry.EntryType.HELP;
                    break;
                case CREATEDIR:
                    for (String parameter : TreatEntryText.obtainParameters()) {
                        auxDirectory = new File(parameter);
                        if (!auxDirectory.exists()) {
                            auxDirectory.mkdir();
                        } else {
                            System.out.println("El directorio ya existe\n");
                        }
                    }
                    lastEntry = TreatEntry.EntryType.CREATEDIR;
                    break;

                case CREATEFILE:
                    for (String parameter : TreatEntryText.obtainParameters()) {
                        auxDirectory = new File(parameter);
                        if (!auxDirectory.exists()) {
                            try {
                                auxDirectory.createNewFile();
                            } catch (IOException e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            System.out.println("El archivo ya no existe\n");
                        }
                    }
                    lastEntry = TreatEntry.EntryType.CREATEFILE;
                    break;
                case SORTBY:
                    TreatEntry.SortType sortType = TreatEntryText.obtainSortType();
                    entries = currentDirectory.list();
                    String auxString;
                    switch (sortType) {
                        case NAME:
                            for (int i = 0; i < entries.length; i++){
                                for (int y = i+1; y < entries.length; y++){
                                    if (entries[i].compareTo(entries[y])>0){
                                        auxString = entries[i];
                                        entries[i] = entries[y];
                                        entries[y] = auxString;
                                    }
                                }
                            }
                            break;
                        case DATE:
                            File [] entriesFiles = new File[entries.length];
                            for (int i = 0; i < entries.length; i++){
                                entriesFiles[i] = new File(entries[i]);
                            }
                            for (int i = 0; i < entries.length; i++){
                                for (int y = i+1; y < entries.length; y++){
                                    if (entriesFiles[i].lastModified() < entriesFiles[y].lastModified()){
                                        auxString = entries[i];
                                        entries[i] = entries[y];
                                        entries[y] = auxString;
                                        
                                        auxDirectory = entriesFiles[i];
                                        entriesFiles[i] = entriesFiles[y];
                                        entriesFiles[y] = auxDirectory;
                                    }
                                }
                            }
                            break;
                    }
                    if (entries != null && entries.length > 0) {
                        for (String item : entries) {
                            System.out.println(">> " + item);
                        }
                    } else {
                        System.out.println("Directorio sin elementos\n");
                    }
                    lastEntry = TreatEntry.EntryType.SORTBY;
                    break;
                case DELETEDIR:
                    for (String parameter : TreatEntryText.obtainParameters()) {
                        auxDirectory = new File(parameter);
                        if (auxDirectory.exists() && auxDirectory.isDirectory()) {
                            entries = auxDirectory.list();
                            for (String s : entries) {
                                File currentFile = new File(auxDirectory.getPath(), s);
                                currentFile.delete();
                            }
                            auxDirectory.delete();
                        }
                    }
                    lastEntry = TreatEntry.EntryType.DELETEDIR;
                    break;
                case DELETEFILE:
                    for (String parameter : TreatEntryText.obtainParameters()) {
                        auxDirectory = new File(parameter);
                        if (auxDirectory.exists() && auxDirectory.isDirectory()) {
                            auxDirectory.delete();
                        }
                    }
                    lastEntry = TreatEntry.EntryType.DELETEFILE;
                    break;
                case EXIT:
                    System.out.println("Saliendo de el navegador\n");
                    lastEntry = TreatEntry.EntryType.EXIT;
                    break;
                case ERROR:
                    System.out.println("ERROR: " + TreatEntryText.obtainError() + "\n");
                    lastEntry = TreatEntry.EntryType.ERROR;
                    break;
            }
        }
        try {
            log.writeToLog("\n===================\n\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
