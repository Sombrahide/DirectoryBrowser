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
import java.time.format.DateTimeFormatter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import org.w3c.dom.*;
import org.xml.sax.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.*;

/**
 *
 * @author Juanjo
 */
public class Browser {

    String _idiom;
    Document _xmlDocument;
    XPath _xPath;
    
    private void executeXpath(File file) throws ParserConfigurationException, IOException, XPathExpressionException, SAXException{
        //Parse file XML to Document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        _xmlDocument = builder.parse(file);

        //Create XPATH object to apply filter
        _xPath = XPathFactory.newInstance().newXPath();
    }
    
    private String exactExpression(String expression)throws ParserConfigurationException, IOException, XPathExpressionException, SAXException{
        Node node = (Node) _xPath.compile(expression).evaluate(_xmlDocument, XPathConstants.NODE);
        String result = (String) node.getTextContent();
        return result;
    }

    public void initBrowser() {
        try {
            _idiom = "eng";
            File inputIdiom = new File("strings.xml");
            
            Scanner scriptScanner;
            
            executeXpath(inputIdiom);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
            Scanner sc = new Scanner(System.in);
            WriteLog log = new WriteLog("log.txt", true);
            String[] entries;
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
                            System.out.println(
                                    exactExpression("/commands/command[@id='directory']/"+_idiom)+ " " + 
                                    exactExpression("/commands/command[@id='notFound']/"+_idiom)+"\n");
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
                            System.out.println(
                                    exactExpression("/commands/command[@id='directory']/"+_idiom)+ " " +
                                    exactExpression("/commands/command[@id='withNoElements']/"+_idiom)+"\n");
                        }
                        lastEntry = TreatEntry.EntryType.LIST;
                        break;
                    case UP:
                        if (currentDirectory.getAbsoluteFile().getParentFile() != null) {
                            currentDirectory = currentDirectory.getAbsoluteFile().getParentFile();
                        } else {
                            System.out.println(
                                    exactExpression("/commands/command[@id='cantUpMore']/"+_idiom)+"\n");
                        }
                        lastEntry = TreatEntry.EntryType.UP;
                        break;
                    case INFOFILE:
                        auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                        if (auxDirectory.exists() && auxDirectory.isFile()) {
                            System.out.println("    >> "+
                                    exactExpression("/commands/command[@id='info']/field[@id='name']/"+_idiom)
                                    +": " + auxDirectory.getName());
                            System.out.println("    >> "+
                                    exactExpression("/commands/command[@id='info']/field[@id='lastModified']/"+_idiom)+": "
                                    + sdf.format(new Date(auxDirectory.lastModified())));
                        } else {
                            System.out.println(
                                    exactExpression("/commands/command[@id='noExist']/"+_idiom)+
                                    exactExpression("/commands/command[@id='file']/"+_idiom)+"\n");
                        }
                        lastEntry = TreatEntry.EntryType.INFOFILE;
                        break;
                    case INFODIR:
                        auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                        if (auxDirectory.exists() && auxDirectory.isDirectory()) {
                            System.out.println("    >> "+
                                    exactExpression("/commands/command[@id='info']/field[@id='name']/"+_idiom)
                                    +": " + auxDirectory.getName());
                            System.out.println("    >> "+
                                    exactExpression("/commands/command[@id='info']/field[@id='lastModified']/"+_idiom)+": "
                                    + sdf.format(new Date(auxDirectory.lastModified())));
                        } else {
                            System.out.println(
                                    exactExpression("/commands/command[@id='noExist']/"+_idiom)+
                                    exactExpression("/commands/command[@id='directory']/"+_idiom)+"\n");
                        }
                        lastEntry = TreatEntry.EntryType.INFODIR;
                        break;
                    case HELP:
                        if (TreatEntryText.obtainParameters()[0] == null) {
                            System.out.println(exactExpression("/commands/command[@id='commandList']/"+_idiom)+": \n>>goto\n>>golast\n>>list\n>>up\n>>infofile\n>>infodir\n>>help"
                                    + "\n>>createdir\n>>createfile\n>>sortby\n>>deletedir\n>>deletefile\n>>changeidiom\n>>exit\n");
                        } else {
                            switch (TreatEntryText.obtainParameters()[0]) {
                                case "goto":
                                    System.out.println("GoTo:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='goto']/"+_idiom)+".\n");
                                    break;
                                case "golast":
                                    System.out.println("GoLast:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='golast']/"+_idiom)+".\n");
                                    break;
                                case "list":
                                    System.out.println("List:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='list']/"+_idiom)+".\n");
                                    break;
                                case "up":
                                    System.out.println("Up:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='up']/"+_idiom)+".\n");
                                    break;
                                case "infofile":
                                    System.out.println("InfoFile:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='infofile']/"+_idiom)+".\n");
                                    break;
                                case "infodir":
                                    System.out.println("InfoDir:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='infodir']/"+_idiom)+".\n");
                                    break;
                                case "help":
                                    System.out.println("Help:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='help']/"+_idiom)+".\n");
                                    break;
                                case "createdir":
                                    System.out.println("CreateDir:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='createdir']/"+_idiom)+".\n");
                                    break;
                                case "createfile":
                                    System.out.println("CreateFile:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='createfile']/"+_idiom)+".\n");
                                    break;
                                case "sortby":
                                    System.out.println("SortBy:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='sortby']/"+_idiom)+".\n"
                                            + " NAME: "+exactExpression("/commands/command[@id='commandList']/field[@id='sortby']/subfield[@id='name']/"+_idiom)+"\n"
                                            + " DATE: "+exactExpression("/commands/command[@id='commandList']/field[@id='sortby']/subfield[@id='date']/"+_idiom)+"\n");
                                    break;
                                case "deletedir":
                                    System.out.println("DeleteDir:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='deletedir']/"+_idiom)+".\n");
                                    break;
                                case "deletefile":
                                    System.out.println("DeleteFile:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='deletefile']/"+_idiom)+".\n");
                                    break;
                                case "changeidiom":
                                    System.out.println("ChangeIdiom:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='changeidiom']/"+_idiom)+".\n"
                                            +" ESP: "+exactExpression("/commands/command[@id='commandList']/field[@id='changeidiom']/subfield[@id='esp']/"+_idiom)+"\n"
                                            +" ENG: "+exactExpression("/commands/command[@id='commandList']/field[@id='changeidiom']/subfield[@id='eng']/"+_idiom)+"\n");
                                    break;
                                case "script":
                                    System.out.println("Script:\n*"+exactExpression("/commands/command[@id='script']/field[@id='script']/"+_idiom)+".\n");
                                    break;
                                case "exit":
                                    System.out.println("Exit:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='exit']/"+_idiom)+".\n");
                                    break;
                                default:
                                    System.out.println(""+exactExpression("/commands/command[@id='commandList']/field[@id='error']/"+_idiom)+"\n");
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
                                System.out.println(
                                        exactExpression("/commands/command[@id='directory']/"+_idiom)+
                                        exactExpression("/commands/command[@id='alreadyExist']/"+_idiom)+
                                        "\n");
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
                                System.out.println(
                                        exactExpression("/commands/command[@id='file']/"+_idiom)+
                                        exactExpression("/commands/command[@id='alreadyExist']/"+_idiom)+
                                        "\n");
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
                                for (int i = 0; i < entries.length; i++) {
                                    for (int y = i + 1; y < entries.length; y++) {
                                        if (entries[i].compareTo(entries[y]) > 0) {
                                            auxString = entries[i];
                                            entries[i] = entries[y];
                                            entries[y] = auxString;
                                        }
                                    }
                                }
                                break;
                            case DATE:
                                File[] entriesFiles = new File[entries.length];
                                for (int i = 0; i < entries.length; i++) {
                                    entriesFiles[i] = new File(entries[i]);
                                }
                                for (int i = 0; i < entries.length; i++) {
                                    for (int y = i + 1; y < entries.length; y++) {
                                        if (entriesFiles[i].lastModified() < entriesFiles[y].lastModified()) {
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
                            System.out.println(
                                    exactExpression("/commands/command[@id='directory']/"+_idiom)+ " " +
                                    exactExpression("/commands/command[@id='withNoElements']/"+_idiom)+"\n");
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
                    case CHANGEIDIOM:
                        _idiom = TreatEntryText.obtainParameters()[0];
                        System.out.println(exactExpression("/commands/command[@id='changeidiom']/"+_idiom)+"\n");
                        lastEntry = TreatEntry.EntryType.CHANGEIDIOM;
                        break;
                    case SCRIPT:
                        auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                        scriptScanner = new Scanner(auxDirectory);
                        while (scriptScanner.hasNextLine()){
                            initScript(scriptScanner.nextLine());
                        }
                        
                        
                        
                        
                        System.out.println("/commands/command[@id='script']/"+_idiom+"\n");
                        lastEntry = TreatEntry.EntryType.SCRIPT;
                        break;
                    case EXIT:
                        System.out.println(
                                    exactExpression("/commands/command[@id='exit']/"+_idiom)+"\n");
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void initScript(String commandText) {
        try {
            File inputIdiom = new File("strings.xml");
            executeXpath(inputIdiom);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            WriteLog log = new WriteLog("log.txt", true);
            String[] entries;
            String entryText = "";
            final String dir = System.getProperty("user.dir");
            File previousDirectory = new File("");
            File currentDirectory = new File(dir);

            TreatEntry.EntryType lastEntry = null;

            File auxDirectory;
            TreatEntry TreatEntryText;
            try {
                log.writeToLog(" Script");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        System.out.print(currentDirectory.getAbsolutePath() + ">");
            entryText = commandText;
            try {
                log.writeToLog("   " + entryText);
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
                        System.out.println(
                                exactExpression("   "+"/commands/command[@id='directory']/"+_idiom)+ " " + 
                                exactExpression("   "+"/commands/command[@id='notFound']/"+_idiom)+"\n");
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
                        System.out.println(
                                exactExpression("   "+"/commands/command[@id='directory']/"+_idiom)+ " " +
                                exactExpression("   "+"/commands/command[@id='withNoElements']/"+_idiom)+"\n");
                    }
                    lastEntry = TreatEntry.EntryType.LIST;
                    break;
                case UP:
                    if (currentDirectory.getAbsoluteFile().getParentFile() != null) {
                        currentDirectory = currentDirectory.getAbsoluteFile().getParentFile();
                    } else {
                        System.out.println(
                                exactExpression("   "+"/commands/command[@id='cantUpMore']/"+_idiom)+"\n");
                    }
                    lastEntry = TreatEntry.EntryType.UP;
                    break;
                case INFOFILE:
                    auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                    if (auxDirectory.exists() && auxDirectory.isFile()) {
                        System.out.println("   "+"    >> "+
                                exactExpression("/commands/command[@id='info']/field[@id='name']/"+_idiom)
                                +": " + auxDirectory.getName());
                        System.out.println("   "+"    >> "+
                                exactExpression("/commands/command[@id='info']/field[@id='lastModified']/"+_idiom)+": "
                                + sdf.format(new Date(auxDirectory.lastModified())));
                    } else {
                        System.out.println(
                                exactExpression("   "+"/commands/command[@id='noExist']/"+_idiom)+
                                exactExpression("   "+"/commands/command[@id='file']/"+_idiom)+"\n");
                    }
                    lastEntry = TreatEntry.EntryType.INFOFILE;
                    break;
                case INFODIR:
                    auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                    if (auxDirectory.exists() && auxDirectory.isDirectory()) {
                        System.out.println("   "+"    >> "+
                                exactExpression("/commands/command[@id='info']/field[@id='name']/"+_idiom)
                                +": " + auxDirectory.getName());
                        System.out.println("   "+"    >> "+
                                exactExpression("/commands/command[@id='info']/field[@id='lastModified']/"+_idiom)+": "
                                + sdf.format(new Date(auxDirectory.lastModified())));
                    } else {
                        System.out.println(
                                exactExpression("   "+"/commands/command[@id='noExist']/"+_idiom)+
                                exactExpression("   "+"/commands/command[@id='directory']/"+_idiom)+"\n");
                    }
                    lastEntry = TreatEntry.EntryType.INFODIR;
                    break;
                case HELP:
                    if (TreatEntryText.obtainParameters()[0] == null) {
                        System.out.println(exactExpression("   "+"/commands/command[@id='commandList']/"+_idiom)+": \n>>goto\n>>golast\n>>list\n>>up\n>>infofile\n>>infodir\n>>help"
                                + "\n>>createdir\n>>createfile\n>>sortby\n>>deletedir\n>>deletefile\n>>changeidiom\n>>script\n>>exit\n");
                    } else {
                        switch (TreatEntryText.obtainParameters()[0]) {
                            case "goto":
                                System.out.println("   "+"GoTo:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='goto']/"+_idiom)+".\n");
                                break;
                            case "golast":
                                System.out.println("   "+"GoLast:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='golast']/"+_idiom)+".\n");
                                break;
                            case "list":
                                System.out.println("   "+"List:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='list']/"+_idiom)+".\n");
                                break;
                            case "up":
                                System.out.println("   "+"Up:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='up']/"+_idiom)+".\n");
                                break;
                            case "infofile":
                                System.out.println("   "+"InfoFile:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='infofile']/"+_idiom)+".\n");
                                break;
                            case "infodir":
                                System.out.println("   "+"InfoDir:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='infodir']/"+_idiom)+".\n");
                                break;
                            case "help":
                                System.out.println("   "+"Help:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='help']/"+_idiom)+".\n");
                                break;
                            case "createdir":
                                System.out.println("   "+"CreateDir:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='createdir']/"+_idiom)+".\n");
                                break;
                            case "createfile":
                                System.out.println("   "+"CreateFile:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='createfile']/"+_idiom)+".\n");
                                break;
                            case "sortby":
                                System.out.println("   "+"SortBy:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='sortby']/"+_idiom)+".\n"
                                        + "   "+" NAME: "+exactExpression("/commands/command[@id='commandList']/field[@id='sortby']/subfield[@id='name']/"+_idiom)+"\n"
                                        + "   "+" DATE: "+exactExpression("/commands/command[@id='commandList']/field[@id='sortby']/subfield[@id='date']/"+_idiom)+"\n");
                                break;
                            case "deletedir":
                                System.out.println("   "+"DeleteDir:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='deletedir']/"+_idiom)+".\n");
                                break;
                            case "deletefile":
                                System.out.println("   "+"DeleteFile:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='deletefile']/"+_idiom)+".\n");
                                break;
                            case "changeidiom":
                                System.out.println("   "+"ChangeIdiom:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='changeidiom']/"+_idiom)+".\n"
                                        +"   "+" ESP: "+exactExpression("/commands/command[@id='commandList']/field[@id='changeidiom']/subfield[@id='esp']/"+_idiom)+"\n"
                                        +"   "+" ENG: "+exactExpression("/commands/command[@id='commandList']/field[@id='changeidiom']/subfield[@id='eng']/"+_idiom)+"\n");
                                break;
                            case "script":
                                System.out.println("   "+"Script:\n*"+exactExpression("/commands/command[@id='script']/field[@id='script']/"+_idiom)+".\n");
                                break;
                            case "exit":
                                System.out.println("   "+"Exit:\n*"+exactExpression("/commands/command[@id='commandList']/field[@id='exit']/"+_idiom)+".\n");
                                break;
                            default:
                                System.out.println("   "+exactExpression("/commands/command[@id='commandList']/field[@id='error']/"+_idiom)+"\n");
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
                            System.out.println(
                                    exactExpression("   "+"/commands/command[@id='directory']/"+_idiom)+
                                    exactExpression("   "+"/commands/command[@id='alreadyExist']/"+_idiom)+
                                    "\n");
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
                            System.out.println(
                                    exactExpression("   "+"/commands/command[@id='file']/"+_idiom)+
                                    exactExpression("   "+"/commands/command[@id='alreadyExist']/"+_idiom)+
                                    "\n");
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
                            for (int i = 0; i < entries.length; i++) {
                                for (int y = i + 1; y < entries.length; y++) {
                                    if (entries[i].compareTo(entries[y]) > 0) {
                                        auxString = entries[i];
                                        entries[i] = entries[y];
                                        entries[y] = auxString;
                                    }
                                }
                            }
                            break;
                        case DATE:
                            File[] entriesFiles = new File[entries.length];
                            for (int i = 0; i < entries.length; i++) {
                                entriesFiles[i] = new File(entries[i]);
                            }
                            for (int i = 0; i < entries.length; i++) {
                                for (int y = i + 1; y < entries.length; y++) {
                                    if (entriesFiles[i].lastModified() < entriesFiles[y].lastModified()) {
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
                        System.out.println(
                                exactExpression("   "+"/commands/command[@id='directory']/"+_idiom)+ " " +
                                exactExpression("   "+"/commands/command[@id='withNoElements']/"+_idiom)+"\n");
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
                case CHANGEIDIOM:
                    _idiom = TreatEntryText.obtainParameters()[0];
                    System.out.println(exactExpression("   "+"/commands/command[@id='changeidiom']/"+_idiom)+"\n");
                    lastEntry = TreatEntry.EntryType.CHANGEIDIOM;
                    break;
                case SCRIPT:
                    auxDirectory = new File(TreatEntryText.obtainParameters()[0]);
                    System.out.println("   "+"/commands/command[@id='script']/"+_idiom+"\n");
                    lastEntry = TreatEntry.EntryType.SCRIPT;
                    break;
                case EXIT:
                    System.out.println(
                                exactExpression("   "+"/commands/command[@id='exit']/"+_idiom)+"\n");
                    lastEntry = TreatEntry.EntryType.EXIT;
                    break;
                case ERROR:
                    System.out.println("   "+"ERROR: " + TreatEntryText.obtainError() + "\n");
                    lastEntry = TreatEntry.EntryType.ERROR;
                    break;
            }
            try {
                log.writeToLog(" EndScript\n");
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
