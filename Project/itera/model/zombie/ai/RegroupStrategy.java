
package itera.model.zombie.ai;

import itera.model.human.Human;
import itera.model.zombie.Zombie;

public class RegroupStrategy implements MovementStrategy {

    @Override
    public void move(Zombie zombie, Human target) {

        double dx = zombie.getX() - target.getX();
        double dy = zombie.getY() - target.getY();

        double distance = Math.hypot(dx, dy);

        if (distance == 0) {
            return;
        }

        zombie.moveInDirection(
                dx / distance,
                dy / distance
        );
    }
}
