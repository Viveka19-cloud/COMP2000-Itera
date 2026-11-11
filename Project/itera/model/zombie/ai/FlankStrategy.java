
package itera.model.zombie.ai;

import itera.model.human.Human;
import itera.model.zombie.Zombie;

public class FlankStrategy implements MovementStrategy {

    @Override
    public void move(Zombie zombie, Human target) {

        double dx = target.getX() - zombie.getX();
        double dy = target.getY() - zombie.getY();

        double distance = Math.hypot(dx, dy);

        if (distance == 0) {
            return;
        }

        double forwardX = dx / distance;
        double forwardY = dy / distance;

        double sideX =
                -forwardY * zombie.getFlankDirection();

        double sideY =
                forwardX * zombie.getFlankDirection();

        double strength = distance > 65 ? 0.7 : 0.2;

        zombie.moveInDirection(
                forwardX + sideX * strength,
                forwardY + sideY * strength
        );
    }
}
