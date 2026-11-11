
package itera.model.zombie;

import itera.model.*;
import itera.model.Character;
import itera.model.human.*;
import itera.model.zombie.targeting.*;
import itera.model.zombie.ai.*;

import java.awt.*;
import java.util.*;

public class Zombie extends Character {

    public enum AIBehaviour {
        CHASE,
        FLANK,
        REGROUP
    }
   
    private AIBehaviour currentBehaviour = AIBehaviour.CHASE;

    // Movement Strategy Pattern
    private final MovementStrategy chaseStrategy =
            new ChaseStrategy();

    private final MovementStrategy flankStrategy =
            new FlankStrategy();

    private final MovementStrategy regroupStrategy =
            new RegroupStrategy();

    private MovementStrategy movementStrategy = chaseStrategy;

    private java.util.List<Building> buildings =
            java.util.List.of();

    protected double detectionRange = 500;

    // Existing Targeting Strategy Pattern
    protected TargetingStrategy targetingStrategy =
            new NearestTarget();

    protected static final int DAMAGE = 25;
    protected static final double ATTACK_DISTANCE = 20;

    private int blockedTicks = 0;
    private int behaviourTicks = 0;
    private int flankDirection = 1;

    public Zombie(int x, int y) {
        super(100, 1.875, x, y, 18);
    }

    public void receiveGunshot() {
        if (isAlive()) {
            takeDamage(50);
        }
    }

    public void setBuildings(
            java.util.List<Building> buildings) {

        this.buildings = buildings == null
                ? java.util.List.of()
                : buildings;
    }

    public TargetingStrategy getTargetingStrategy() {
        return targetingStrategy;
    }

    public void setTargetingStrategy(
            TargetingStrategy strategy) {

        this.targetingStrategy = Objects.requireNonNull(
                strategy,
                "Targeting strategy is required"
        );
    }

    public AIBehaviour getCurrentBehaviour() {
        return currentBehaviour;
    }

    public int getFlankDirection() {
        return flankDirection;
    }

    public void moveInDirection(double dx, double dy) {
        moveDirection(dx, dy);
    }

    // Select the appropriate movement algorithm.
    private void setBehaviour(AIBehaviour behaviour) {

        currentBehaviour = behaviour;

        switch (behaviour) {

            case CHASE:
                movementStrategy = chaseStrategy;
                break;

            case FLANK:
                movementStrategy = flankStrategy;
                System.out.println("FLANK strategy activated!");
                break;

            case REGROUP:
                movementStrategy = regroupStrategy;
                System.out.println("REGROUP strategy activated!");
                break;
        }
    }

    public Human update(
            int worldWidth,
            int worldHeight,
            ArrayList<Human> humans,
            SafePoint safePoint) {

        if (!isAlive()) {
            return null;
        }

        Human target = findTarget(humans);

        if (target == null || target.isSheltered()) {
            setBehaviour(AIBehaviour.CHASE);
            return null;
        }

        double distance =
                position.distanceTo(target.getPosition());

        if (distance > detectionRange) {
            return null;
        }

        if (distance <= ATTACK_DISTANCE) {

            boolean attacked = performAttack(target);

            if (attacked && !target.isAlive()) {
                return target;
            }

            return null;
        }

        updateBehaviour(target, humans);

        double previousX = position.getX();
        double previousY = position.getY();

        // Strategy Pattern:
        // Delegate movement to the selected algorithm.
        movementStrategy.move(this, target);

        double nextX = position.getX();
        double nextY = position.getY();

        // Preserve existing obstacle avoidance.
        if (isBlocked(nextX, nextY, safePoint)) {

            position.setX(nextX);
            position.setY(previousY);

            if (isBlocked(
                    position.getX(),
                    position.getY(),
                    safePoint)) {

                position.setX(previousX);
                position.setY(nextY);

                if (isBlocked(
                        position.getX(),
                        position.getY(),
                        safePoint)) {

                    position.setX(previousX);
                    position.setY(previousY);
                    blockedTicks++;
                }
            }

        } else {
            blockedTicks = Math.max(0, blockedTicks - 1);
        }

        keepInsideWorld(worldWidth, worldHeight);

        return null;
    }

    private void updateBehaviour(
            Human target,
            ArrayList<Human> humans) {

        behaviourTicks++;

        // Avoid switching behaviour every frame.
        if (behaviourTicks < 25) {
            return;
        }

        behaviourTicks = 0;

        int nearbySoldiers = 0;

        for (Human human : humans) {

            if (!human.isAlive() || human.isSheltered()) {
                continue;
            }

            if (human instanceof Soldier
                    && position.distanceTo(
                            human.getPosition()) < 130) {

                nearbySoldiers++;
            }
        }

        // Change direction if repeatedly blocked.
        if (blockedTicks >= 3) {

            setBehaviour(AIBehaviour.FLANK);

            flankDirection *= -1;
            blockedTicks = 0;
            return;
        }

        // Flank when multiple soldiers are nearby.
        if (nearbySoldiers >= 2) {
            setBehaviour(AIBehaviour.FLANK);
            return;
        }

        // Retreat when injured near a soldier.
        if (nearbySoldiers == 1
                && getHealthRatio() < 0.5) {

            setBehaviour(AIBehaviour.REGROUP);
            return;
        }

        // Default movement.
        setBehaviour(AIBehaviour.CHASE);
    }

    private double getHealthRatio() {

        return Math.max(
                0.0,
                Math.min(
                        1.0,
                        health / (double) getMaxHealth()
                )
        );
    }

    private void moveDirection(double dx, double dy) {

        double length = Math.sqrt(dx * dx + dy * dy);

        if (length <= 0) {
            return;
        }

        position.add(
                dx / length * speed,
                dy / length * speed
        );
    }

    protected Human findTarget(ArrayList<Human> humans) {

        return targetingStrategy
                .chooseTarget(this, humans)
                .orElse(null);
    }

    protected boolean performAttack(Human target) {
        return target.receiveZombieHit(DAMAGE);
    }

    public void attack(Character target) {
        target.takeDamage(DAMAGE);
    }

    // Retained for existing callers and ChaseStrategy.
    public void chase(Character target) {

        double directionX =
                target.getX() - position.getX();

        double directionY =
                target.getY() - position.getY();

        double distance = Math.sqrt(
                directionX * directionX
                        + directionY * directionY
        );

        if (distance > 0) {
            position.add(
                    directionX / distance * speed,
                    directionY / distance * speed
            );
        }
    }

    private boolean isBlocked(
            double x,
            double y,
            SafePoint safePoint) {

        if (safePoint != null
                && safePoint.wouldZombieEnter(x, y, size)) {
            return true;
        }

        for (Building building : buildings) {

            if (building.overlaps(x, y, size)) {
                return true;
            }
        }

        return false;
    }

    protected void keepInsideWorld(
            int width,
            int height) {

        if (position.getX() < 0) {
            position.setX(0);
        }

        if (position.getX() > width - size) {
            position.setX(width - size);
        }

        if (position.getY() < 0) {
            position.setY(0);
        }

        if (position.getY() > height - size) {
            position.setY(height - size);
        }
    }

    @Override
    public void draw(Graphics g) {

        g.setColor(Color.RED);
        g.fillOval(getX(), getY(), size, size);

        drawTypeLabel(g, "Z");
    }

    public int getMaxHealth() {
        return 100;
    }

    protected void drawHealthBar(Graphics g) {

        int width = Math.max(24, size);
        int barX = getX() + (size - width) / 2;
        int barY = getY() - 9;

        int remaining = (int) Math.round(
                width * Math.max(
                        0,
                        Math.min(
                                1.0,
                                health / (double) getMaxHealth()
                        )
                )
        );

        g.setColor(Color.DARK_GRAY);
        g.fillRect(barX, barY, width, 4);

        g.setColor(Color.RED);
        g.fillRect(barX, barY, remaining, 4);
    }

    protected void drawTypeLabel(
            Graphics g,
            String label) {

        drawHealthBar(g);

        g.setColor(Color.BLACK);
        g.setFont(new Font(
                "Arial",
                Font.BOLD,
                10
        ));

        g.drawString(
                label,
                getX() - 5,
                getY() + size + 12
        );
    }
}
