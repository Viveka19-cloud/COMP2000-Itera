
package itera.model.zombie.boss;

import itera.model.human.Human;
import itera.model.zombie.MutantBoss;
import java.util.ArrayList;

public final class SmashAttackState implements BossState {

    private boolean attackExecuted = false;

    @Override
    public void update(
            MutantBoss boss,
            ArrayList<Human> humans,
            long currentTime) {

        if (attackExecuted) {
            return;
        }

        attackExecuted = true;

        // Perform Ground Smash on nearby humans.
        boss.performGroundSmash(humans);

        System.out.println("BOSS: Ground Smash executed!");

        // Restart the 8-second cooldown.
        boss.setLastSmashTime(currentTime);

        // Return to chasing.
        boss.changeState(new ChaseState());
    }
}
