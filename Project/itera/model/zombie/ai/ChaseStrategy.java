
package itera.model.zombie.ai;

import itera.model.human.Human;
import itera.model.zombie.Zombie;

public class ChaseStrategy implements MovementStrategy {

    @Override
    public void move(Zombie zombie, Human target) {
        zombie.chase(target);
    }
}
