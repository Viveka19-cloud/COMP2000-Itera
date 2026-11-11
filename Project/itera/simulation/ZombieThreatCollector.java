
package itera.simulation;

import itera.model.zombie.Zombie;
import itera.model.zombie.Zombie.AIBehaviour;

import java.util.Set;
import java.util.EnumSet;
import java.util.stream.Collector;

public class ZombieThreatCollector {

    // Stores statistics while processing zombies.
    private static class Accumulator {
        long total;
        long chasing;
        long flanking;
        long regrouping;
        long injured;
        int threatScore;

        void add(Zombie zombie) {
            if (!zombie.isAlive()) {
                return;
            }

            total++;

            AIBehaviour behaviour = zombie.getCurrentBehaviour();

            switch (behaviour) {
                case CHASE -> {
                    chasing++;
                    threatScore += 5;
                }
                case FLANK -> {
                    flanking++;
                    threatScore += 8;
                }
                case REGROUP -> {
                    regrouping++;
                    threatScore += 2;
                }
            }

            if (zombie.getHealth() < zombie.getMaxHealth() / 2.0) {
                injured++;
            }
        }

        Accumulator combine(Accumulator other) {
            total += other.total;
            chasing += other.chasing;
            flanking += other.flanking;
            regrouping += other.regrouping;
            injured += other.injured;
            threatScore += other.threatScore;

            return this;
        }

        ZombieThreatReport finish() {
            return new ZombieThreatReport(
                total,
                chasing,
                flanking,
                regrouping,
                injured,
                threatScore
            );
        }
    }

    public static Collector<Zombie, ?, ZombieThreatReport> toThreatReport() {
        return Collector.of(
            Accumulator::new,
            Accumulator::add,
            Accumulator::combine,
            Accumulator::finish
        );
    }
}
