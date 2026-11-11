
package itera.model.zombie.boss;

import itera.model.human.Human;
import itera.model.zombie.MutantBoss;
import java.util.ArrayList;

public final class SummonState implements BossState {

    private boolean summonTriggered = false;

    @Override
    public void update(
            MutantBoss boss,
            ArrayList<Human> humans,
            long currentTime) {

        // Summon reinforcements once at 50% health.
        if (!summonTriggered
                && boss.getBossHealth() <= boss.getMaxBossHealth() / 2) {

            summonTriggered = true;

            // World processes the request.
            boss.requestSummon();

            System.out.println("BOSS: Reinforcements requested!");
        }
    }
}
