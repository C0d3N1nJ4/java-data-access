package src.main.java.data;

import src.main.java.classes.Vehicle;

import java.util.Collection;
import java.util.List;

public interface VehicleDao {

    void insert(Vehicle vehicle);

    void update(Vehicle vehicle);

    void delete(Vehicle vehicle);

    Vehicle findByVehicleNo(String vehicleNo);

    List<Vehicle> findAll();

    //inserts a collection of vehicles
    default void insert(Collection<Vehicle> vehicles) {
        vehicles.forEach(this::insert);
    }

}
