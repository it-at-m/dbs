export default class UtilFunctions {
    static getErrorMessage(httpStatus: number): string {
        switch (httpStatus) {
            case 401:
            case 403:
                return "Sie sind nicht berechtigt diese Aktion durchzuführen.";
            case 404:
                return "Die angeforderten Daten konnten nicht gefunden werden.";
            case 500:
                return "Ein unbekannter Fehler ist aufgetreten. Bitte versuchen Sie es zu einem späteren Zeitpunkt erneut";
            default:
                return "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.";
        }
    }
}
