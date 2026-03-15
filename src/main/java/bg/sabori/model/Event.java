package bg.sabori.model;

import java.time.LocalDate;

public class Event {
    private int       id;
    private String    name;
    private LocalDate eventDate;
    private boolean   isRecurring;
    private String    description;
    private int       settlementId;
    private int       categoryId;
    private int       organizerId;

    // joined fields — not stored in DB
    private String settlementName;
    private String categoryName;
    private String organizerName;

    public Event() {}

    public Event(int id, String name, LocalDate eventDate, boolean isRecurring,
                 String description, int settlementId, int categoryId, int organizerId,
                 String settlementName, String categoryName, String organizerName) {
        this.id             = id;
        this.name           = name;
        this.eventDate      = eventDate;
        this.isRecurring    = isRecurring;
        this.description    = description;
        this.settlementId   = settlementId;
        this.categoryId     = categoryId;
        this.organizerId    = organizerId;
        this.settlementName = settlementName;
        this.categoryName   = categoryName;
        this.organizerName  = organizerName;
    }

    public int       getId()             { return id; }
    public String    getName()           { return name; }
    public LocalDate getEventDate()      { return eventDate; }
    public boolean   isRecurring()       { return isRecurring; }
    public String    getDescription()    { return description; }
    public int       getSettlementId()   { return settlementId; }
    public int       getCategoryId()     { return categoryId; }
    public int       getOrganizerId()    { return organizerId; }
    public String    getSettlementName() { return settlementName; }
    public String    getCategoryName()   { return categoryName; }
    public String    getOrganizerName()  { return organizerName; }

    public void setId(int id)                       { this.id             = id; }
    public void setName(String name)                { this.name           = name; }
    public void setEventDate(LocalDate eventDate)   { this.eventDate      = eventDate; }
    public void setRecurring(boolean recurring)     { this.isRecurring    = recurring; }
    public void setDescription(String description)  { this.description    = description; }
    public void setSettlementId(int settlementId)   { this.settlementId   = settlementId; }
    public void setCategoryId(int categoryId)       { this.categoryId     = categoryId; }
    public void setOrganizerId(int organizerId)     { this.organizerId    = organizerId; }
    public void setSettlementName(String s)         { this.settlementName = s; }
    public void setCategoryName(String c)           { this.categoryName   = c; }
    public void setOrganizerName(String o)          { this.organizerName  = o; }

    @Override
    public String toString() { return name; }
}
