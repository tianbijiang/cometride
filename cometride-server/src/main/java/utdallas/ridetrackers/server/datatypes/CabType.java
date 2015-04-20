package utdallas.ridetrackers.server.datatypes;

/**
 * Created with IntelliJ IDEA.
 * User: mlautz
 */
public class CabType {

    private String typeId;
    private String typeName;
    private int maximumCapacity;

    public CabType() {}

    public CabType(String typeId, String typeName, int maximumCapacity) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.maximumCapacity = maximumCapacity;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getMaximumCapacity() {
        return maximumCapacity;
    }

    public void setMaximumCapacity(int maximumCapacity) {
        this.maximumCapacity = maximumCapacity;
    }
}
