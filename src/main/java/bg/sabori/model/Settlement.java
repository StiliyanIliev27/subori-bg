package bg.sabori.model;

public class Settlement {
    private int    id;
    private String name;
    private String type;       // "село" | "град"
    private int    regionId;
    private String regionName; // joined field — not stored in DB

    public Settlement() {}

    public Settlement(int id, String name, String type, int regionId, String regionName) {
        this.id         = id;
        this.name       = name;
        this.type       = type;
        this.regionId   = regionId;
        this.regionName = regionName;
    }

    public int    getId()         { return id; }
    public String getName()       { return name; }
    public String getType()       { return type; }
    public int    getRegionId()   { return regionId; }
    public String getRegionName() { return regionName; }

    public void setId(int id)               { this.id         = id; }
    public void setName(String name)        { this.name       = name; }
    public void setType(String type)        { this.type       = type; }
    public void setRegionId(int regionId)   { this.regionId   = regionId; }
    public void setRegionName(String r)     { this.regionName = r; }

    @Override
    public String toString() { return type + " " + name; }
}
