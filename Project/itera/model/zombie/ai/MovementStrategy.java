
package itera.model.zombie.ai;

import itera.model.human.Human;
import itera.model.zombie.Zombie;

public interface MovementStrategy {

    void move(Zombie zombie, Human target);

}
