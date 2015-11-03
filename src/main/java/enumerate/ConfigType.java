package enumerate;

/**
 * Created by jakub on 29/10/15.
 */
public enum ConfigType {

    ITERATIONS("Iterations", "Maximum number of iterations"),
    DIMENSION("Dimension", "Dimension of the problem"),
    POP_SIZE("Population size", "Number of individuals in population"),

    C1("C1", "Congnitive parameter"),
    C2("C2", "Social parameter"),
    MAX_VELOCITY("Maximum velocity", "Maximum velocity in range [0,1] where 0 -> No movement, 1 -> Move across entire search space"),


    DEGREE_LIMIT("Maximum node degree", "Maximum node degree value before repulsion starts"),
    REP_ROUNDS_LIMIT("Repulsion rounds limit", "Length of repulsion phase"),
    REP_END_ITERATION("Repulsion end iteration", "Number of last iteration in which repulsion can start");

    private final String name;
    private final String description;

    ConfigType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name;
    }
}
