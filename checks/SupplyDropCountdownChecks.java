
package itera.checks;

import itera.simulation.EmergencySupplyDrop;

public final class SupplyDropCountdownChecks {

    private static int passed = 0;

    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        passed++;
    }

    public static void main(String[] args) {

        EmergencySupplyDrop drop =
            new EmergencySupplyDrop(100, 100, 1000, 25000);

        check(drop.getRemainingSeconds(999) == 0,
            "Countdown before spawning");

        check(drop.getRemainingSeconds(1000) == 25,
            "Countdown starts at 25 seconds");

        check(drop.getRemainingSeconds(1001) == 25,
            "Countdown rounds up partial seconds");

        check(drop.getRemainingSeconds(2000) == 24,
            "Countdown decreases after one second");

        check(drop.getRemainingSeconds(13500) == 13,
            "Countdown rounds up remaining time");

        check(drop.getRemainingSeconds(25000) == 1,
            "Countdown shows final second");

        check(drop.getRemainingSeconds(25999) == 1,
            "Countdown remains positive before expiry");

        check(drop.getRemainingSeconds(26000) == 0,
            "Countdown reaches zero at expiry");

        check(drop.getRemainingSeconds(36000) == 0,
            "Expired drop stays at zero");

        EmergencySupplyDrop collected =
            new EmergencySupplyDrop(10, 10, 0, 25000);

        collected.getMedicine().collect();

        check(collected.getRemainingSeconds(1000) == 0,
            "Collected drop has no remaining countdown");

        System.out.println(
            "Passed " + passed + " supply countdown checks."
        );
    }
}
