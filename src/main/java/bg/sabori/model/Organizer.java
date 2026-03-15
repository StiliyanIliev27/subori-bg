package bg.sabori.model;

public class Organizer {
    private int    id;
    private String name;
    private String type;           // "Община" | "Читалище" | "Частен"
    private String contact;
    private int    settlementId;
    private String settlementName; // joined field — not stored in DB

    public Organizer() {}

    public Organizer(int id, String name, String type, String contact,
                     int settlementId, String settlementName) {
        this.id             = id;
        this.name           = name;
        this.type           = type;
        this.contact        = contact;
        this.settlementId   = settlementId;
        this.settlementName = settlementName;
    }

    public int    getId()             { return id; }
    public String getName()           { return name; }
    public String getType()           { return type; }
    public String getContact()        { return contact; }
    public int    getSettlementId()   { return settlementId; }
    public String getSettlementName() { return settlementName; }

    public void setId(int id)                     { this.id             = id; }
    public void setName(String name)              { this.name           = name; }
    public void setType(String type)              { this.type           = type; }
    public void setContact(String contact)        { this.contact        = contact; }
    public void setSettlementId(int settlementId) { this.settlementId   = settlementId; }
    public void setSettlementName(String s)       { this.settlementName = s; }

    @Override
    public String toString() { return name; }
}
