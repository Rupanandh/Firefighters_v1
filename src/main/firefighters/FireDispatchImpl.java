package main.firefighters;

import main.api.*;
import main.api.exceptions.InvalidDimensionException;
import main.api.exceptions.NoFireFoundException;
import main.api.exceptions.OutOfCityBoundsException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FireDispatchImpl implements FireDispatch {

    private final City city;
    private final int[][] cityMatrix;
    private List<Firefighter> firefighters;

    public FireDispatchImpl(City city) {
        this.city = city;
        this.cityMatrix = new int[city.getXDimension()][city.getYDimension()];
    }

    @Override
    public List<Firefighter> getFirefighters() {
        return firefighters;
    }

    @Override
    public void setFirefighters(int numFirefighters) {
        firefighters = firefighters != null ? firefighters : new ArrayList<>();
        Building fireStation = city.getFireStation();
        for (int i = 0; i < numFirefighters; i++) {
            firefighters.add(new FirefighterImpl(0, fireStation.getLocation()));
        }
    }

    @Override
    public void dispatchFirefighters(CityNode... burningBuildings) {
        // Iterate through the burning building coordinates and dispatch the firefighters
        for (CityNode fireNode : burningBuildings) {
            // on each dispatch grab the building on fire and the fighter next in queue
            // on success add the fighter back to the queue
            dispatch(fireNode, firefighters.remove(0)).ifPresent(firefighter -> firefighters.add(firefighter));
        }
    }


    private Optional<Firefighter> dispatch(CityNode fireNode, Firefighter firefighter) {
        try {
            return Optional.of(stopTheFire(fireNode, firefighter));
        } catch (NoFireFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Firefighter stopTheFire(CityNode fireNode, Firefighter firefighter) throws NoFireFoundException {
      // Grab the coordinates and the buliding on fire
      int destX = fireNode.getX();
        int destY = fireNode.getY();
        Building burningBuilding = city.getBuilding(fireNode);

        // Grab the fighter location and the distance travelled
        int sourceX = firefighter.getLocation().getX();
        int sourceY = firefighter.getLocation().getY();
        int distance = firefighter.distanceTraveled();

        // initialize the fighter travel paths
        boolean travelLeft = false;
        boolean travelRight = false;
        boolean travelUp = false;
        boolean travelDown = false;

        // Ensure coordinates falls in the city limits
        if(destX >= cityMatrix.length || destY >= cityMatrix[0].length || destX < 0 || destY < 0) {
          throw new OutOfCityBoundsException();
        }

        // Identify the travel direction for first leg
        if (sourceX > destX) {
            travelUp = true;
        } else {
            travelDown = true;
        }

      // Identify the travel direction for second leg
        if (sourceY > destY) {
            travelLeft = true;
        } else {
            travelRight = true;
        }


        if (travelUp) {
            distance = travelUp(distance, sourceX, sourceY, destX, destY, travelLeft, travelRight, burningBuilding);
        } else if (travelDown) {
            distance = travelDown(distance, sourceX, sourceY, destX, destY, travelLeft, travelRight, burningBuilding);
        }

        return new FirefighterImpl(distance, fireNode);
    }

    private int travelLeft(int distance, int sourceY, int destY, Building burnBuilding) throws NoFireFoundException {
      // iterate through until the destination is found
      for (int j = sourceY; j >= 0; j--) {
            if (j == destY) {
              // put the fire off and return to base
                burnBuilding.extinguishFire();
                return distance;
            }
            distance++;

        }
        return -1;
    }

    private int travelRight(int distance, int sourceY, int destY, Building burnBuilding) throws NoFireFoundException {
      // iterate through until the destination y coordinate is found
      for (int j = sourceY; j < cityMatrix[0].length; j++) {
            if (j == destY) {
              // put the fire off and return to base
                burnBuilding.extinguishFire();
                return distance;
            }
            distance++;
        }
        return -1;
    }

    private int travelUp(int distance, int sourceX, int sourceY, int destX,
                         int destY, boolean travelLeft, boolean travelRight,
                         Building burnBuilding) throws NoFireFoundException {
      // Iterate through along the matching destination x coordinate is found
        for (int i = sourceX; i >= 0; i--) {
          // As soon the x coordinate is found, identify the turn either left or right
            if (i == destX && travelLeft) {
                return travelLeft(distance, sourceY, destY, burnBuilding);
            } else if (i == destX && travelRight) {
                return travelRight(distance, sourceY, destY, burnBuilding);
            }
            distance++;
        }
        return -1;
    }

    private int travelDown(int distance, int sourceX, int sourceY, int destX,
                           int destY, boolean travelLeft, boolean travelRight,
                           Building burnBuilding) throws NoFireFoundException {
      // Iterate through along the matching destination x coordinate is found
        for (int i = sourceX; i < cityMatrix.length; i++) {
          // As soon the destination coordinate is found, identify the turn either left or right
            if (i == destX && travelLeft) {
                return travelLeft(distance, sourceY, destY, burnBuilding);
            } else if (i == destX && travelRight) {
                return travelRight(distance, sourceY, destY, burnBuilding);
            }
            distance++;
        }
        return -1;
    }
}
