
package itera.model.zombie.boss;

import itera.model.human.Human;
import itera.model.zombie.MutantBoss;
import java.util.ArrayList;

public final class ChaseState implements BossState {

    @Override
    public void update(
            MutantBoss boss,
            ArrayList<Human> humans,
            long currentTime) {

        long elapsed = currentTime - boss.getLastSmashTime();

        if (elapsed >= boss.getSmashCooldown()) {
            boss.changeState(new SmashWarningState());
        }
    }
}

