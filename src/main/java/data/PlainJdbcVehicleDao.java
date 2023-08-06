package src.main.java.data;

import src.main.java.classes.Vehicle;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PlainJdbcVehicleDao implements VehicleDao {

    private static final String INSERT_SQL = "INSERT INTO VEHICLE (VEHICLE_NO, COLOR, WHEEL, SEAT) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL = "UPDATE VEHICLE SET COLOR = ?, WHEEL = ?, SEAT = ? WHERE VEHICLE_NO = ?";

    private static final String DELETE_SQL = "DELETE FROM VEHICLE WHERE VEHICLE_NO = ?";

    private static final String SELECT_ALL_SQL = "SELECT * FROM VEHICLE";

    private static final String SELECT_SQL = "SELECT * FROM VEHICLE WHERE VEHICLE_NO = ?";

    private final DataSource dataSource;

    public PlainJdbcVehicleDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    //The vehicle insert operation : Each time this method is called,
   //a new database connection is made, the query is executed and the connection is closed automatically because of the tr-with-resources
    // If the try-with resources is not used - remember to close the connection to prevent connection leaks.
    @Override
    public void insert(Vehicle vehicle) {
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(INSERT_SQL)) {
            prepareStatement(ps, vehicle);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vehicle findByVehicleNo(String vehicleNo) {
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(SELECT_SQL)) {
            ps.setString(1, vehicleNo);

            Vehicle vehicle = null;
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    vehicle = toVehicle(rs);
                }
            }
            return vehicle;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Vehicle> findAll() {
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(SELECT_ALL_SQL);
             var rs = ps.executeQuery()) {

            var vehicles = new ArrayList<Vehicle>();
            while (rs.next()) {
                vehicles.add(toVehicle(rs));
            }
            return vehicles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void update(Vehicle vehicle) {
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(UPDATE_SQL)) {
            prepareStatement(ps, vehicle);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Vehicle vehicle) {
        try (var conn = dataSource.getConnection();
             var ps = conn.prepareStatement(DELETE_SQL)) {
            ps.setString(1, vehicle.getVehicleNo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Helper method to set the parameters for the insert and update operations
    private Vehicle toVehicle(ResultSet rs) throws SQLException {
        return new Vehicle(rs.getString("VEHICLE_NO"), rs.getString("COLOR"), rs.getInt("WHEEL"), rs.getInt("SEAT"));

    }

    //Helper method to reuses the mapping logic
    private void prepareStatement(PreparedStatement ps, Vehicle vehicle) throws SQLException {
        ps.setString(1, vehicle.getVehicleNo());
        ps.setString(2, vehicle.getColor());
        ps.setInt(3, vehicle.getWheel());
        ps.setInt(4, vehicle.getSeat());
    }

}
