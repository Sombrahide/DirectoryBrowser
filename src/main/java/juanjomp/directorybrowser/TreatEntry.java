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
public class TreatEntry {

    public enum EntryType {
        GOTO,
        GOLAST,
        LIST,
        UP,
        INFOFILE,
        INFODIR,
        HELP,
        CREATEDIR,
        CREATEFILE,
        SORTBY,
        DELETEDIR,
        DELETEFILE,
        CHANGEIDIOM,
        SCRIPT,
        ERROR,
        EXIT;
    }

    public enum SortType {
        NAME,
        DATE;
    }

    private String _entry;
    private String _error;
    private EntryType _entryType;
    private SortType _sortType;
    private String[] _parameters;

    private void loadObject() {
        String auxText = this._entry;

        if (auxText != null && !auxText.isEmpty()) {
            String[] elements = auxText.split(" ");
            elements[0] = elements[0].toLowerCase();

            switch (elements[0]) {
                case "goto":
                    if (elements.length == 2) {
                        this._parameters = new String[1];
                        this._parameters[0] = elements[1];
                        this._entryType = EntryType.GOTO;
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "golast":
                    if (elements.length == 1) {
                        this._entryType = EntryType.GOLAST;
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "list":
                    if (elements.length == 1) {
                        this._entryType = EntryType.LIST;
                        this._parameters = new String[1];
                        this._parameters[0] = null;
                    } else if (elements.length == 2) {
                        this._entryType = EntryType.LIST;
                        this._parameters = new String[1];
                        this._parameters[0] = elements[1];
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "up":
                    if (elements.length == 1) {
                        this._entryType = EntryType.UP;
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                break;
                case "infofile":
                    if (elements.length > 1) {
                        this._entryType = EntryType.INFOFILE;
                        this._parameters = new String[elements.length - 1];
                        for (int i = 0; i < _parameters.length; i++) {
                            this._parameters[i] = elements[i + 1];
                        }
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "infodir":
                    if (elements.length > 1) {
                        this._entryType = EntryType.INFODIR;
                        this._parameters = new String[elements.length - 1];
                        for (int i = 0; i < _parameters.length; i++) {
                            this._parameters[i] = elements[i + 1];
                        }
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "help":
                    if (elements.length == 1) {
                        this._entryType = EntryType.HELP;
                        this._parameters = new String[1];
                        this._parameters[0] = null;
                    } else if (elements.length == 2) {
                        this._entryType = EntryType.HELP;
                        this._parameters = new String[1];
                        this._parameters[0] = elements[1].toLowerCase();
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "createdir":
                    if (elements.length > 1) {
                        this._entryType = EntryType.CREATEDIR;
                        this._parameters = new String[elements.length - 1];
                        for (int i = 0; i < _parameters.length; i++) {
                            this._parameters[i] = elements[i + 1];
                        }
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "createfile":
                    if (elements.length > 1) {
                        this._entryType = EntryType.CREATEFILE;
                        this._parameters = new String[elements.length - 1];
                        for (int i = 0; i < _parameters.length; i++) {
                            this._parameters[i] = elements[i + 1];
                        }
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "sortby":
                    if (elements.length == 2) {
                        this._entryType = EntryType.SORTBY;
                        elements[1] = elements[1].toLowerCase();
                        switch (elements[1]) {
                            case ("name"):
                                _sortType = SortType.NAME;
                                break;
                            case ("date"):
                                _sortType = SortType.DATE;
                                break;
                            default:
                                System.out.println("Se ha usado un metodo de ordenacion no valido, intente usar uno de los siguiente:\n     NAME\n      DATE");
                                break;
                        }
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "deletedir":
                    if (elements.length > 1) {
                        this._entryType = EntryType.DELETEDIR;
                        this._parameters = new String[elements.length - 1];
                        for (int i = 0; i < _parameters.length; i++) {
                            this._parameters[i] = elements[i + 1];
                        }
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "deletefile":
                    if (elements.length > 1) {
                        this._entryType = EntryType.DELETEFILE;
                        this._parameters = new String[elements.length - 1];
                        for (int i = 0; i < _parameters.length; i++) {
                            this._parameters[i] = elements[i + 1];
                        }
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "changeidiom":
                    if (elements.length == 1){
                        this._entryType = EntryType.CHANGEIDIOM;
                        this._parameters = new String[elements.length - 1];
                        this._parameters[0] = elements[1];}
                    else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "script":
                    if (elements.length == 1){
                        this._entryType = EntryType.SCRIPT;
                        this._parameters = new String[elements.length - 1];
                        this._parameters[0] = elements[1];}
                    else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                case "exit":
                    if (elements.length == 1) {
                        this._entryType = EntryType.EXIT;
                    } else {
                        this._entryType = EntryType.ERROR;
                        this._error = "Incorrect number of parameters";
                    }
                    break;
                default:
                    this._entryType = EntryType.ERROR;
                    break;
            }
        } else {
            this._entryType = EntryType.ERROR;
            this._error = "Sin texto";
        }
    }

    public EntryType obtainEntryType() {
        return this._entryType;
    }

    public String[] obtainParameters() {
        return this._parameters;
    }

    public SortType obtainSortType() {
        return this._sortType;
    }

    public String obtainError() {
        return this._error;
    }

    public TreatEntry(String entry) {
        this._entry = entry;
        loadObject();
    }
}
