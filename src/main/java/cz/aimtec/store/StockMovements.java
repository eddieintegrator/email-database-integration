package cz.aimtec.store;

public class StockMovements {

    private String stored;
    private String space_code;
    private String mat_id;
    private int units;

    public StockMovements() {
    }

    public String getStored() {
        return stored;
    }

    public void setStored(String stored) {
        this.stored = stored;
    }

    public String getSpace_code() {
        return space_code;
    }

    public void setSpace_code(String space_code) {
        this.space_code = space_code;
    }

    public String getMat_id() {
        return mat_id;
    }

    public void setMat_id(String mat_id) {
        this.mat_id = mat_id;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

}
