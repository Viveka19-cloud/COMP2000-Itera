
package itera.model.zombie.boss;

import itera.model.human.Human;
import itera.model.zombie.MutantBoss;
import java.util.ArrayList;

public final class SmashWarningState implements BossState {

    private long startedAt = -1;

    @Override
    public void update(MutantBoss boss,
                       ArrayList<Human> humans,
                       long currentTime) {

        if (startedAt == -1) {
            startedAt = currentTime;
        }

        if (currentTime - startedAt >= 2000) {
            boss.changeState(new SmashAttackState());
            boss.updateCurrentState(humans, currentTime);
        }
    }
}
