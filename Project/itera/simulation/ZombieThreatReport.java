package itera.simulation;


public record ZombieThreatReport(
        long totalZombies,
        long chasing,
        long flanking,
        long regrouping,
        long injured,
        int threatScore
) {
    public String getThreatLevel() {
        if (threatScore >= 100) {
            return "HIGH";
        } else if (threatScore >= 40) {
            return "MEDIUM";
        }
        return "LOW";
    }
}
