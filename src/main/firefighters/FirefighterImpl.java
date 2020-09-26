package main.firefighters;

import main.api.CityNode;
import main.api.Firefighter;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class FirefighterImpl implements Firefighter {

  /**
   * Records the distance travelled by {@link Firefighter}
   */
  private int distanceTravelled;

  /**
   * Records the location of {@link Firefighter}
   */
  private CityNode location;

  public FirefighterImpl(int distanceTravelled, CityNode location) {
    this.distanceTravelled = distanceTravelled;
    this.location = location;
  }

  @Override
  public CityNode getLocation() {
    return location;
  }

  @Override
  public int distanceTraveled() {
    return distanceTravelled;
  }
}
