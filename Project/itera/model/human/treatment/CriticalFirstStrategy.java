
package itera.model.human.treatment;

import itera.model.human.Human;
import itera.model.human.Medic;
import java.util.Comparator;

/**
 * Prioritises critically injured patients (35 HP or below).
 * Within each priority group, the nearest patient is treated first.
 */
public final class CriticalFirstStrategy implements TreatmentStrategy {

    private static final int CRITICAL_HEALTH = 35;

    @Override
    public Comparator<Human> priorityFor(Medic medic) {
        return Comparator
            .comparingInt((Human human) ->
                human.getHealth() <= CRITICAL_HEALTH ? 0 : 1)
            .thenComparingDouble(human ->
                medic.getPosition().distanceTo(human.getPosition()))
            .thenComparingInt(Human::getHealth);
    }
}
