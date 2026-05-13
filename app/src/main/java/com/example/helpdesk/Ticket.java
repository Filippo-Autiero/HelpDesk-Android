package com.example.helpdesk;

public class Ticket {
    public int ID;
    public String Titolo;
    public String Descrizione;
    public String Dove;
    public String Dataapertura;
    public String Datachiusura;
    public Integer Oreuomopreviste;
    public int Prioritaautore;
    public String Marcaprodotto;
    public String Modelloprodotto;
    public String Serialeprodotto;
    public String Inventarioprodotto;
    public String Annotazione;
    public int Notificheallautore;
    public int IDtiposegnalazione;
    public int IDutentecreatore;
    public int IDutenteultimoeditor;
    public String Imgpath;
    public String Filepath;

    // Costruttore vuoto necessario per Gson
    public Ticket() {}
}