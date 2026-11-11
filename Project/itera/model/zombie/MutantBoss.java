
package itera.model.zombie;

import itera.model.*;
import itera.model.human.*;
import itera.model.zombie.boss.*;

import java.awt.*;
import java.util.ArrayList;

public class MutantBoss extends Zombie {

    private static final int BOSS_HEALTH = 270;
    private static final int BOSS_DAMAGE = 50;
    private static final double BOSS_SPEED = 2.75;
    private static final int BOSS_SIZE = 40;
    private static final double BOSS_DETECTION_RANGE = 700;

    // Ground Smash settings
    private static final int SMASH_DAMAGE = 20;
    private static final double SMASH_RADIUS = 100;
    private static final long SMASH_COOLDOWN = 8000;
    private static final long SMASH_WARNING = 2000;

    // State Design Pattern
    private BossState currentState = new ChaseState();

    // Summoning runs independently of Ground Smash.
    private final BossState summonState = new SummonState();

    private long lastSmashTime;
    private boolean smashClockStarted = false;
    private boolean summonRequested = false;

    public MutantBoss(int x, int y) {
        super(x, y);

        this.health = BOSS_HEALTH;
        this.speed = BOSS_SPEED;
        this.size = BOSS_SIZE;
        this.detectionRange = BOSS_DETECTION_RANGE;
    }

    @Override
    protected boolean performAttack(Human target) {
        return target.receiveZombieHit(BOSS_DAMAGE);
    }

    /**
     * Called once per simulation tick.
     * Delegates special abilities to state objects.
     */
    public void updateSpecialAbilities(
            ArrayList<Human> humans,
            long currentTime) {

        if (!isAlive()) {
            return;
        }

        // Check summoning independently.
        summonState.update(this, humans, currentTime);

        // Begin cooldown when boss first appears.
        if (!smashClockStarted) {
            lastSmashTime = currentTime;
            smashClockStarted = true;
        }

        // Delegate behaviour to the active state.
        currentState.update(this, humans, currentTime);
        System.out.println(
    "BOSS DEBUG | time=" + currentTime
    + " | state=" + getBossStateName()
    + " | elapsed=" + (currentTime - lastSmashTime)
);
    }

    /**
     * Transition to another boss state.
     */
    public void changeState(BossState nextState) {

        if (nextState == null) {
            throw new IllegalArgumentException(
                "Boss state required"
            );
        }

        currentState = nextState;
    }

    /**
     * Update the active state after a transition.
     */
    public void updateCurrentState(
            ArrayList<Human> humans,
            long currentTime) {

        currentState.update(this, humans, currentTime);
        System.out.println("Boss State: " + getBossStateName());
    }

    public long getLastSmashTime() {
        return lastSmashTime;
    }

    public void setLastSmashTime(long time) {
        lastSmashTime = time;
    }

    public long getSmashCooldown() {
        return SMASH_COOLDOWN;
    }

    public long getSmashWarningDuration() {
        return SMASH_WARNING;
    }

    /**
     * Used by SummonState to request reinforcements.
     */
    public void requestSummon() {
        summonRequested = true;
    }

    /**
     * Whether the boss is currently warning players.
     */
    public boolean isSmashWarningActive() {
        return currentState instanceof SmashWarningState;
    }

    /**
     * Useful for debugging and testing transitions.
     */
    public String getBossStateName() {
        return currentState.getClass().getSimpleName();
    }

    /**
     * Damage every nearby living human.
     */
    public void performGroundSmash(ArrayList<Human> humans) {

        double bossCenterX = getX() + size / 2.0;
        double bossCenterY = getY() + size / 2.0;

        for (Human human : humans) {

            if (!human.isAlive()) {
                continue;
            }

            double humanCenterX =
                human.getX() + human.getSize() / 2.0;

            double humanCenterY =
                human.getY() + human.getSize() / 2.0;

            double distance = Math.hypot(
                bossCenterX - humanCenterX,
                bossCenterY - humanCenterY
            );

            if (distance <= SMASH_RADIUS) {
                human.receiveZombieHit(SMASH_DAMAGE);
            }
        }
    }

    /**
     * World consumes the summoning request.
     * Returns true once per request.
     */
    public boolean consumeSummonRequest() {

        if (!summonRequested) {
            return false;
        }

        summonRequested = false;
        return true;
    }

    @Override
    public void draw(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        int centerX = getX() + size / 2;
        int centerY = getY() + size / 2;

        // Draw warning circle during SmashWarningState.
        if (isSmashWarningActive()) {

            g2.setColor(new Color(255, 100, 0, 65));

            g2.fillOval(
                (int) (centerX - SMASH_RADIUS),
                (int) (centerY - SMASH_RADIUS),
                (int) (SMASH_RADIUS * 2),
                (int) (SMASH_RADIUS * 2)
            );

            g2.setColor(Color.ORANGE);
            g2.setStroke(new BasicStroke(3));

            g2.drawOval(
                (int) (centerX - SMASH_RADIUS),
                (int) (centerY - SMASH_RADIUS),
                (int) (SMASH_RADIUS * 2),
                (int) (SMASH_RADIUS * 2)
            );
        }

        // Boss remains purple.
        g2.setColor(new Color(74, 20, 140));

        g2.fillOval(
            getX(),
            getY(),
            size,
            size
        );

        drawHealthBar(g2);

        g2.setColor(Color.BLACK);
        g2.setFont(
            new Font("Arial", Font.BOLD, 12)
        );

        g2.drawString(
            isSmashWarningActive()
                ? "BOSS - SMASH!"
                : "BOSS",
            getX(),
            getY() + size + 15
        );

        g2.dispose();
    }

    public int getBossHealth() {
        return health;
    }

    @Override
    public int getMaxHealth() {
        return BOSS_HEALTH;
    }

    public int getMaxBossHealth() {
        return BOSS_HEALTH;
    }
}
