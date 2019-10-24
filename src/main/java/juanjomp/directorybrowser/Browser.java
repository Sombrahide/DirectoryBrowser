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

/**
 *
 * @author Juanjo
 */
public class Browser {

    public void initBrowser(String args[]) {
        Scanner sc = new Scanner(System.in);
        String entryText = "";
        File previousDirectory = new File("");
        File currentDirectory = new File("");

        TreatEntry.EntryType lastEntry = null;

        File auxDirectory;
        TreatEntry TreatEntryText;

        while (lastEntry != TreatEntry.EntryType.EXIT) {
            System.out.print(currentDirectory.getAbsolutePath() + ">");
            entryText = sc.nextLine();
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
                    lastEntry = TreatEntry.EntryType.INFOFILE;
                    break;
                case INFODIR:
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
                                System.out.println("SortBy:\n*Mostrar el contenido del directorio indicado de forma ordenada segun el usuario.\n");
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
                    lastEntry = TreatEntry.EntryType.CREATEDIR;
                    break;
                case CREATEFILE:
                    lastEntry = TreatEntry.EntryType.CREATEFILE;
                    break;
                case SORTBY:
                    lastEntry = TreatEntry.EntryType.SORTBY;
                    break;
                case DELETEDIR:
                    lastEntry = TreatEntry.EntryType.DELETEDIR;
                    break;
                case DELETEFILE:
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

    }
}
