package progettoIngegneriaBianchi;

import net.sourceforge.jwebunit.junit.WebTester;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ControllerAcceptanceTest {

    private WebTester tester;


    @Before
    public void prepare(){
        tester = new WebTester();
        tester.setBaseUrl("http://127.0.0.1:8080/");

        tester.beginAt("/input");

        tester.setTextField("firstName","Michele");
        tester.setTextField("lastName","Bianchi");
        tester.setTextField("admissionDate","1993-06-11");
        tester.submit();
    }

    @After
    public void clean(){
        int size = tester.getElementsByXPath("//table[@id='present']/tbody/tr").size();
        for(int i=0; i<size;i++){
            tester.clickLinkWithText("Cancella");
        }
    }

    @Test
    public void testInitialView(){
        tester.beginAt("/");
        tester.clickLinkWithText("Registra un paziente");
        tester.assertTextPresent("Torna alla lista");
    }

    @Test
    public void addNewPatientTest(){
        tester.beginAt("/list");

        int listPatientSize = tester.getElementByXPath("//table").getElements("//tr").size();
        tester.clickLinkWithText("Registra un paziente");

        tester.assertTextPresent("Inserisci un nuovo paziente");
        tester.setTextField("firstName","Andrea");
        tester.setTextField("lastName","Rossi");
        tester.setTextField("admissionDate","1993-06-11");
        tester.submit();

        tester.assertTextPresent("Pazienti Ricoverati");
        int listPatientSizeAfter = tester.getElementByXPath("//table").getElements("//tr").size();

        tester.assertTextInTable("", new String[]{"Andrea","Rossi"});

        assertEquals(listPatientSize+1, listPatientSizeAfter);
    }

    @Test
    public void deletePatientTest(){
        tester.beginAt("/");

        int listPatientSize = tester.getElementsByXPath("//tr").size();

        tester.clickLinkWithText("Cancella");

        tester.assertTextPresent("Pazienti Ricoverati");
        int listPatientSizeAfter = tester.getElementsByXPath("//tr").size();
        assertEquals(listPatientSize-1, listPatientSizeAfter);
    }

    @Test
    public void testOperation(){
        tester.beginAt("/");
        tester.assertTextPresent("Cartella");
        tester.assertTextPresent("Modifica");
        tester.assertTextPresent("Dimetti");
        tester.assertTextPresent("Cancella");
        tester.assertTextPresent("Stampa scheda");


        tester.clickLinkWithText("Cartella");
        tester.assertTextPresent("Cartella Clinica Paziente:");
        tester.clickLinkWithText("Torna alla lista");

        tester.clickLinkWithText("Modifica");
        tester.assertTextPresent("Modifica Cartella Clinica");
        tester.clickLinkWithText("Torna alla lista");

        tester.clickLinkWithText("Dimetti");
        tester.assertTextPresent("Pazienti Ricoverati");

        tester.clickLinkWithText("Stampa scheda");
        tester.assertTextPresent("Paziente:");
        tester.assertTextPresent("Data ricovero:");
        tester.assertTextPresent("Data dimissione:");
        tester.assertTextPresent("FARMACI:");
        tester.assertTextPresent("PRESCRIZIONE:");
        tester.clickLinkWithText("Torna alla lista");

        tester.clickLinkWithText("Cancella");
        tester.assertTextPresent("Pazienti Ricoverati");
    }

    @Test
    public void testDischarge(){
        tester.beginAt("/list");
        int numPresent = tester.getElementsByXPath("//table[@id='present']/tbody/tr").size();
        int numDischarged = tester.getElementsByXPath("//table[@id='discharged']/tbody/tr").size();

        tester.clickLinkWithText("Dimetti");
        int numPresentAfter = tester.getElementsByXPath("//table[@id='present']/tbody/tr").size();
        int numDischargedAfter = tester.getElementsByXPath("//table[@id='discharged']/tbody/tr").size();

        assertEquals(numPresent-1, numPresentAfter);
        assertEquals(numDischarged+1, numDischargedAfter);
    }

    @Test
    public void testUpdateRecord(){
        tester.beginAt("/list");
        tester.clickLinkWithText("Modifica");

        tester.assertTextPresent("Modifica Cartella Clinica");
        tester.setTextField("drugs","Finasteride");
        tester.setTextField("prescription","2 volte al giorno");
        tester.submit();

        tester.clickLinkWithText("Cartella");
        tester.assertTextPresent("Finasteride");
        tester.assertTextPresent("2 volte al giorno");
        tester.clickLinkWithText("Torna alla lista");
    }

    @Test
    public void testPrintRecord(){
        tester.beginAt("/list");
        tester.clickLinkWithText("Stampa scheda");
        tester.assertTextPresent("Paziente:");
        tester.assertTextPresent("Data ricovero:");
        tester.assertTextPresent("Data dimissione:");
        tester.assertTextPresent("FARMACI:");
        tester.assertTextPresent("PRESCRIZIONE:");
    }

    @Test
    public void testSearchPatient(){

        tester.beginAt("/list");

        tester.clickLinkWithText("Registra un paziente");
        tester.setTextField("firstName","Michele");
        tester.setTextField("lastName","Bianchi");
        tester.setTextField("admissionDate","1993-06-11");
        tester.submit();

        tester.clickLinkWithText("Registra un paziente");
        tester.setTextField("firstName","Andrea");
        tester.setTextField("lastName","Rossi");
        tester.setTextField("admissionDate","1993-06-11");
        tester.submit();

        tester.setTextField("lastName","Bianchi");
        tester.submit();

        int size = tester.getElementsByXPath("//table[@id='present']/tbody/tr").size();
        int sizeDischarged = tester.getElementsByXPath("//table[@id='discharged']/tbody/tr").size();
        String cognome;

        for(int i=0; i<size;i++){
            cognome  = tester.getElementsByXPath("//table[@id='present']/tbody/tr/td[@id='lastName']").get(i).getTextContent();
            assertEquals("La search deve mostrare solo pazienti con il cognome indicato", "Bianchi", cognome);
        }

        for(int i=0; i<sizeDischarged;i++){
            cognome =  tester.getElementsByXPath("//table[@id='discharged']/tbody/tr/td[@id='lastNameDischarged']").get(i).getTextContent();
            assertEquals("La search deve mostrare solo pazienti con il cognome indicato", "Bianchi", cognome);
        }

    }

    @Test
    public void testNonEditableAndDeletableDischargedPatient(){
        tester.clickLinkWithText("Dimetti");
        int sizeBefore = tester.getElementsByXPath("//table[@id='discharged']/tbody/tr").size();
        tester.clickLinkWithText("Cancella");
        int sizeAfter = tester.getElementsByXPath("//table[@id='discharged']/tbody/tr").size();
        assertEquals("La cancellazione per un cliente dismesso non deve avere alcun effetto",sizeAfter,sizeBefore);

        tester.clickLinkWithText("Modifica");
        tester.assertTextNotPresent("Modifica Cartella Clinica");
        tester.assertTextPresent("Pazienti Ricoverati");

        tester.clickLinkWithText("Cartella");
        tester.assertTextPresent("Cartella Clinica Paziente:");

        tester.assertTextNotPresent("Modifica");
        tester.assertTextNotPresent("Cancella");
    }
}

