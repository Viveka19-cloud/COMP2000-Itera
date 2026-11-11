
package itera.model.zombie.boss;

import itera.model.human.Human;
import itera.model.zombie.MutantBoss;
import java.util.ArrayList;

public interface BossState {

    void update(
        MutantBoss boss,
        ArrayList<Human> humans,
        long currentTime
    );
}
